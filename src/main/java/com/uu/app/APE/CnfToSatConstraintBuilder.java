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

public class CnfToSatConstraintBuilder extends AbstractConstraintBuilder {
	Expression<StateData> booleanCnfConstraint;
	SLTL temporalConstraint;

	public CnfToSatConstraintBuilder(SLTL temporalConstraint) {
		this.temporalConstraint = temporalConstraint;
	}

	@Override
	public StringBuilder Build(APEDomainSetup apeDomainSetup, ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, AtomMappings atomMappings, int maxBound) {
		this.allModules = apeDomainSetup.getAllModules();
		this.moduleAutomaton = moduleAutomaton;
		this.typeAutomaton = typeAutomaton;
		this.atomMappings = atomMappings;

		booleanCnfConstraint = temporalConstraint.StateFold(maxBound);
		booleanCnfConstraint = RuleSet.toCNF(booleanCnfConstraint);

		return WalkCnf(booleanCnfConstraint);
	}


	public String GetCnfString(int bound) {
		return RuleSet.toCNF(temporalConstraint.StateFold(bound)).toString();
	}

}
