package com.uu.app.SLTL.StateFold;

import com.bpodgursky.jbool_expressions.And;
import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.Or;
import com.uu.app.SLTL.BinarySLTL;
import com.uu.app.SLTL.BinarySLTLFold;
import com.uu.app.SLTL.SLTLFoldData;
import com.uu.app.SLTL.UnarySLTL;
import com.uu.app.StateData;

public class BinarySLTLStateFold implements BinarySLTLFold<Expression<StateData>> {
	@Override
	public Expression<StateData> Fold(
		Expression<StateData> arg1,
		Expression<StateData> arg2,
		BinarySLTL obj,
		SLTLFoldData<Expression<StateData>> data
	) {
		if (data.currentState > data.maxBound) {
			return null;
		}

		Expression<StateData> result = null;

		switch (obj.op) {
			case Or: {
				result = Or.of(arg1, arg2);
				break;
			}
			case And: {
				result = And.of(arg1, arg2);
				break;
			}
			case Until: {
				Expression<StateData> untilExpr = IncreaseStateFold(obj, data);
				result = untilExpr == null ? arg1 : And.of(arg1, untilExpr);

				result = Or.of(arg2, result);
				break;
			}
		}

		return result;
	}


	private Expression<StateData> IncreaseStateFold(BinarySLTL obj, SLTLFoldData<Expression<StateData>> data) {
		SLTLFoldData<Expression<StateData>> data2 = data.Copy();
		data2.currentState += 1;

		if (data2.currentState == data2.maxBound)
			return null;

		return obj.Fold(data2);
	}
}
