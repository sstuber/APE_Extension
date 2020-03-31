package com.uu.app.SLTL.StateFold;

import com.bpodgursky.jbool_expressions.And;
import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.Or;
import com.uu.app.SLTL.BinarySLTL;
import com.uu.app.SLTL.BinarySLTLFold;
import com.uu.app.SLTL.SLTLFoldData;

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
				if (arg1 != null && arg2 != null)
					result = Or.of(arg1, arg2);

				if (arg1 == null)
					result = arg2;

				if (arg2 == null)
					result = arg1;

				break;
			}
			case And: {
				if (arg1 != null && arg2 != null)
					result = And.of(arg1, arg2);

				if (arg1 == null)
					result = arg2;

				if (arg2 == null)
					result = arg1;
				break;
			}
			case Until: {
				Expression<StateData> untilExpr = IncreaseStateFold(obj, data);
				if (arg1 != null && untilExpr != null)
					result = And.of(arg1, untilExpr);

				if (untilExpr == null)
					result = arg1;

				if (arg1 == null)
					result = untilExpr;

				if (result != null && arg2 != null)
					result = Or.of(arg2, result);

				if (result == null)
					result = arg2;
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
