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

import java.util.ArrayList;
import java.util.Optional;

public class SLTLConstraintBuilder implements ExternalConstraintBuilder {

	Expression<StateData> booleanCnfConstraint;
	ArrayList<String> toolNames;

	// Null until called in APE
	AllModules allModules;
	ModuleAutomaton moduleAutomaton;
	TypeAutomaton typeAutomaton;
	AtomMappings atomMappings;

	public SLTLConstraintBuilder(SLTL temporalConstraint, ArrayList<String> toolNames, int maxBound) {
		this.toolNames = toolNames;
		booleanCnfConstraint = temporalConstraint.StateFold(maxBound);

		booleanCnfConstraint = RuleSet.toCNF(booleanCnfConstraint);
	}

	@Override
	public StringBuilder Build(APEDomainSetup apeDomainSetup, ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, AtomMappings atomMappings) {
		this.allModules = apeDomainSetup.getAllModules();
		this.moduleAutomaton = moduleAutomaton;
		this.typeAutomaton = typeAutomaton;
		this.atomMappings = atomMappings;


		return null;
	}

	// maxbound and k should be the same
	Integer LookupModuleLiteral(StateData stateData) {

		if (stateData.type == StateType.Type)
			return null;

		int stateIndex = stateData.stateId -1;
		State state = moduleAutomaton.get(stateIndex);

		AbstractModule module = allModules.get(stateData.name);
		WorkflowElement elementType = WorkflowElement.MODULE;

		// If module didn't within APE it is an external module
		if (module == null){
			module = CreateNewModule(stateData.name);
			elementType = WorkflowElement.EXTERNAL;
		}

		return atomMappings.add(module,state,elementType);
	}

	Module CreateNewModule(String name){
		return new Module(name, name, "ToolsTaxonomy", null);
	}

}
