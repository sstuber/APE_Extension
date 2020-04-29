package com.uu.app.APE;

import com.bpodgursky.jbool_expressions.And;
import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.Not;
import com.bpodgursky.jbool_expressions.Variable;
import com.bpodgursky.jbool_expressions.rules.RuleSet;
import com.uu.app.SLTL.StateFold.StateData;
import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.models.AtomMappings;
import nl.uu.cs.ape.sat.utils.APEDomainSetup;
import org.apache.commons.math3.util.CombinatoricsUtils;

import javax.swing.plaf.nimbus.State;
import java.util.*;
import java.util.stream.Collectors;

public class AtMostNConstraintBuilder extends AbstractConstraintBuilder {

	Map<String, Integer> countMap;

	public AtMostNConstraintBuilder(Map<String, Integer> countMap) {
		this.countMap = countMap;

	}

	@Override
	public StringBuilder Build(APEDomainSetup apeDomainSetup, ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, AtomMappings atomMappings, int maxBound) {
		this.allModules = apeDomainSetup.getAllModules();
		this.moduleAutomaton = moduleAutomaton;
		this.typeAutomaton = typeAutomaton;
		this.atomMappings = atomMappings;

		StringBuilder test = generateConstraints(maxBound);

		return test;
	}

	StringBuilder generateConstraints(int maxBound) {
		List<Expression<StateData>> test = countMap.entrySet().stream()
			.filter(entry -> entry.getValue() < maxBound)
			.map(entry -> transformEntry(entry, maxBound))
			.map(Collection::stream)
			.flatMap(x -> x)
			.collect(Collectors.toCollection(ArrayList::new));

		return test.stream()
			.map(RuleSet::toCNF)
			.map(this::WalkCnf)
			.reduce(new StringBuilder(), StringBuilder::append);
	}

	List<Expression<StateData>> transformEntry(Map.Entry<String, Integer> entry, int maxBound) {
		List<Expression<StateData>> constraintList = new ArrayList<>();
		Iterator<int[]> iterator = generate(maxBound, entry.getValue() + 1);

		while (iterator.hasNext()) {
			List<Expression<StateData>> expressionList = new ArrayList<>();

			for (int i : iterator.next())
				expressionList.add(Variable.of(new StateData() {{
					name = entry.getKey();
					stateId = i + 1;
				}}));

			constraintList.add(Not.of(And.of(expressionList)));
		}

		return constraintList;
	}

	public Iterator<int[]> generate(int n, int r) {
		if (r > n)
			r = n;

		return CombinatoricsUtils.combinationsIterator(n, r);
	}

}
