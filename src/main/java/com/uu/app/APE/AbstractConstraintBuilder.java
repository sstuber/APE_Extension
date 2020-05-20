package com.uu.app.APE;

import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.Literal;
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

import java.util.Collections;
import java.util.List;

public abstract class AbstractConstraintBuilder implements ExternalConstraintBuilder {

	// Null until called in APE
	AllModules allModules;
	ModuleAutomaton moduleAutomaton;
	TypeAutomaton typeAutomaton;
	AtomMappings atomMappings;

	StringBuilder WalkCnf(Expression<StateData> booleanCnfConstraint) {
		List<Expression<StateData>> conjunctionExpressions = booleanCnfConstraint.getChildren();

		if (booleanCnfConstraint.getExprType().equals("or"))
			conjunctionExpressions = Collections.singletonList(booleanCnfConstraint);

		if (booleanCnfConstraint.equals(Literal.of(false)))
			return new StringBuilder().append("0\n");

		StringBuilder conjunctionString = conjunctionExpressions.stream()
			.map(disjunctionExpr -> {
				StringBuilder disjunctionString = new StringBuilder();

				if (isSingleVariable(disjunctionExpr)) {
					disjunctionString.append(TranslateSingleVariable(disjunctionExpr));
					disjunctionString.append("0\n");

					return disjunctionString;
				}

				List<Expression<StateData>> test = disjunctionExpr
					.getChildren();

				StringBuilder result = test.stream()
					.map(this::TranslateSingleVariable)
					.reduce(disjunctionString, StringBuilder::append)
					.append("0\n");


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

}
