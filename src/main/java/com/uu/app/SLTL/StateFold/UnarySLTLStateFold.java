package com.uu.app.SLTL.StateFold;

import com.bpodgursky.jbool_expressions.*;
import com.uu.app.SLTL.SLTLFoldData;
import com.uu.app.SLTL.UnarySLTL;
import com.uu.app.SLTL.UnarySLTLFold;
import com.uu.app.SLTL.UnarySLTLOp;

public class UnarySLTLStateFold implements UnarySLTLFold<Expression<StateData>> {
	@Override
	public Expression<StateData> Fold(UnarySLTL obj, SLTLFoldData<Expression<StateData>> data) {

		// all states after a next should be increased
		if (obj.op == UnarySLTLOp.Next) {
			// we need a copy so 2 nexts after each other don't share the same object;
			data = data.Copy();
			data.currentState += 1;
			if (data.currentState > data.maxBound) {
				return Literal.of(false);
			}
		}

		Expression<StateData> arg1 = obj.right.Fold(data);

		// can't allow a state higher than max bound to exist
		if (data.currentState > data.maxBound) {
			return null;
		}

		if (arg1 == null && obj.op != UnarySLTLOp.Next)
			return null;

		Expression<StateData> result = null;

		switch (obj.op) {
			case Neg: {
				result = Not.of(arg1);
				break;
			}
			case Future: {
				// Create continuation of Future
				Expression<StateData> rightExpr = IncreaseStateFold(obj, data);
				result = arg1;
				result = rightExpr == null ? result : Or.of(arg1, rightExpr);
				break;
			}
			case Global: {
				// Create continuation of Future
				Expression<StateData> rightExpr = IncreaseStateFold(obj, data);
				result = arg1;
				result = rightExpr == null ? result : And.of(arg1, rightExpr);
				break;
			}
			case Next: {
				// When a next is used without modulename we just want the next states with the increased values;
				if (obj.moduleName == null) {
					result = arg1;
					break;
				}

				StateData nextState = new StateData();
				nextState.stateId = data.currentState; // State is increase at the start of this fn
				nextState.name = obj.moduleName;
				nextState.type = StateType.Module;

				result = Variable.of(nextState);
				result = arg1 == null ? result : And.of(result, arg1);
				break;
			}
			case Bracket: {
				result = arg1;
				break;
			}
			default:
				throw new Error("Operator not implemented");
		}
		return result;
	}

	private Expression<StateData> IncreaseStateFold(UnarySLTL obj, SLTLFoldData<Expression<StateData>> data) {
		SLTLFoldData<Expression<StateData>> data2 = data.Copy();
		data2.currentState += 1;

		if (data2.currentState >= data2.maxBound)
			return null;

		return obj.Fold(data2);
	}
}
