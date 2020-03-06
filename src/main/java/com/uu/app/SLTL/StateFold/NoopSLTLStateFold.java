package com.uu.app.SLTL.StateFold;

import com.bpodgursky.jbool_expressions.*;
import com.bpodgursky.jbool_expressions.rules.RuleSet;

import com.uu.app.SLTL.NoopSLTL;
import com.uu.app.SLTL.NoopSLTLFold;
import com.uu.app.SLTL.SLTLFoldData;
import com.uu.app.StateData;
import com.uu.app.StateType;

public class NoopSLTLStateFold implements NoopSLTLFold<Expression<StateData>> {

	@Override
	public Expression<StateData> Fold(NoopSLTL objSLTL, SLTLFoldData<Expression<StateData>> data) {
		// TODO fix something for the true variant
		// TODO distinguish between type and module states

		// TODO this is not correct. a type state expression needs to be made for each type input
		// p(t 1 i) or p(t 2 i) or p(t k i) with k amount of input type states
		// <module> type means the module in s1 and the type in s1. the type is the output of the module
		// as it's the input of the next module

		if (objSLTL.typeName.equals("true"))
			return null;

		StateData state = new StateData();
		state.name = objSLTL.typeName;
		state.stateId = data.currentState;
		state.type = StateType.Type;

		//Expression<StateData> test = Variable.of();

		return Variable.of(state);
	}
}
