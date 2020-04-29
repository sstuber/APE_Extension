package com.uu.app.APE;

import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.rules.RuleSet;
import com.uu.app.SLTL.SLTL;
import com.uu.app.SLTL.StateFold.StateData;
import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.models.AtomMappings;
import nl.uu.cs.ape.sat.utils.APEDomainSetup;


public class SltlToSatConstraintBuilder extends AbstractConstraintBuilder {
	Expression<StateData> booleanCnfConstraint;
	SLTL temporalConstraint;

	public SltlToSatConstraintBuilder(SLTL temporalConstraint) {
		this.temporalConstraint = temporalConstraint;
	}

	@Override
	public StringBuilder Build(APEDomainSetup apeDomainSetup, ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, AtomMappings atomMappings, int maxBound) {
		this.allModules = apeDomainSetup.getAllModules();
		this.moduleAutomaton = moduleAutomaton;
		this.typeAutomaton = typeAutomaton;
		this.atomMappings = atomMappings;

		return WalkCnf(getConstraint(maxBound));
	}

	Expression<StateData> getConstraint(int maxBound) {
		return RuleSet.toCNF(temporalConstraint.StateFold(maxBound));
	}


	public String GetCnfString(int bound) {
		return RuleSet.toCNF(temporalConstraint.StateFold(bound)).toString();
	}

}
