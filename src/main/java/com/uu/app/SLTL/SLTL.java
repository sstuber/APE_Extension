package com.uu.app.SLTL;

import com.bpodgursky.jbool_expressions.Expression;
import com.uu.app.SLTL.StateFold.StateData;

public abstract class SLTL {
	public abstract <T> T Fold(SLTLFoldData<T> foldData);

	public Expression<StateData> StateFold(int maxBound) {
		SLTLFoldData<Expression<StateData>> test = SLTLFoldData.CreateStateFold(maxBound);
		return this.Fold(test);
	}

}
