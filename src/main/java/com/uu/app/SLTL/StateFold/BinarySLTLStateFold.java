package com.uu.app.SLTL.StateFold;

import com.bpodgursky.jbool_expressions.Expression;
import com.uu.app.SLTL.BinarySLTL;
import com.uu.app.SLTL.BinarySLTLFold;
import com.uu.app.SLTL.SLTLFoldData;
import com.uu.app.StateData;

public class BinarySLTLStateFold implements BinarySLTLFold<Expression<StateData>> {
	@Override
	public Expression<StateData> Fold(
		Expression<StateData> arg1,
		Expression<StateData> arg2,
		BinarySLTL obj,
		SLTLFoldData<Expression<StateData>> data
	) {
		return null;
	}
}
