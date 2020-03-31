package com.uu.app.APE;

import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.rules.RuleSet;
import com.uu.app.SLTL.SLTL;
import com.uu.app.SLTL.StateFold.StateData;
import com.uu.app.SLTL.StateFold.StateType;
import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.State;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.core.extrernal.ExternalConstraintBuilder;
import nl.uu.cs.ape.sat.models.AbstractModule;
import nl.uu.cs.ape.sat.models.AllModules;
import nl.uu.cs.ape.sat.models.AtomMappings;
import nl.uu.cs.ape.sat.models.Module;
import nl.uu.cs.ape.sat.models.enums.WorkflowElement;
import nl.uu.cs.ape.sat.utils.APEDomainSetup;

import java.security.PublicKey;
import java.util.*;
import java.util.stream.Stream;

public class SLTLConstraintBuilder implements ExternalConstraintBuilder {
	Expression<StateData> booleanCnfConstraint;

	// Null until called in APE
	AllModules allModules;
	ModuleAutomaton moduleAutomaton;
	TypeAutomaton typeAutomaton;
	AtomMappings atomMappings;

	public SLTLConstraintBuilder(SLTL temporalConstraint, int maxBound) {
		booleanCnfConstraint = temporalConstraint.StateFold(maxBound);

		booleanCnfConstraint = RuleSet.toCNF(booleanCnfConstraint);
	}

	@Override
	public StringBuilder Build(APEDomainSetup apeDomainSetup, ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, AtomMappings atomMappings) {
		this.allModules = apeDomainSetup.getAllModules();
		this.moduleAutomaton = moduleAutomaton;
		this.typeAutomaton = typeAutomaton;
		this.atomMappings = atomMappings;


		return WalkCnf();
	}

	StringBuilder WalkCnf() {
		List<Expression<StateData>> conjunctionExpressions = this.booleanCnfConstraint.getChildren();

		if (this.booleanCnfConstraint.getExprType().equals("or"))
			conjunctionExpressions = Collections.singletonList(this.booleanCnfConstraint);


		StringBuilder conjunctionString = conjunctionExpressions.stream()
			.map(disjunctionExpr -> {
				StringBuilder disjunctionString = new StringBuilder();

				if (isSingleVariable(disjunctionExpr)) {
					disjunctionString.append(TranslateSingleVariable(disjunctionExpr));
					disjunctionString.append("0\n ");

					return disjunctionString;
				}

				List<Expression<StateData>> test = disjunctionExpr
					.getChildren();

				StringBuilder result = test.stream()
					.map(this::TranslateSingleVariable)
					.reduce(disjunctionString, StringBuilder::append)
					.append("0\n ");


				//disjunctionExpr.getExprType();

				return disjunctionString;
			})
			.reduce(new StringBuilder(), StringBuilder::append);

		return conjunctionString;
	}

	boolean isSingleVariable(Expression<StateData> value) {
		return value.getExprType().equals("variable") || value.getExprType().equals("not");
	}

	StringBuilder TranslateSingleVariable(Expression<StateData> value) {
		StringBuilder variableString = new StringBuilder();
		StateData variable = (StateData) value.getAllK().toArray()[0];

		if (value.getExprType().equals("not"))
			variableString.append("-");

		int literal = LookupModuleLiteral(variable);

		return variableString.append(literal).append(' ');
	}

	// maxbound and k should be the same
	Integer LookupModuleLiteral(StateData stateData) {

		if (stateData.type == StateType.Type)
			return null;

		int stateIndex = stateData.stateId - 1;
		State state = moduleAutomaton.get(stateIndex);

		AbstractModule module = allModules.get(stateData.name);
		WorkflowElement elementType = WorkflowElement.MODULE;

		// If module didn't within APE it is an external module
		if (module == null) {
			module = CreateNewModule(stateData.name);
			elementType = WorkflowElement.EXTERNAL;
		}

		return atomMappings.add(module, state, elementType);
	}

	Module CreateNewModule(String name) {
		return new Module(name, name, "ToolsTaxonomy", null);
	}

	public String GetCnfString() {
		return booleanCnfConstraint.toString();
	}

}
