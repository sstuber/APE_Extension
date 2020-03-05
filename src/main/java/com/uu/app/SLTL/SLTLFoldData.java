package com.uu.app.SLTL;

import com.bpodgursky.jbool_expressions.Expression;
import com.uu.app.SLTL.StateFold.BinarySLTLStateFold;
import com.uu.app.SLTL.StateFold.NoopSLTLStateFold;
import com.uu.app.SLTL.StateFold.UnarySLTLStateFold;
import com.uu.app.StateData;

public class SLTLFoldData<T> {

	public SLTLFoldData(int maxBound) {
		this.maxBound = maxBound;
	}

	public NoopSLTLFold<T> noopFold;
	public UnarySLTLFold<T> unaryFold;
	public BinarySLTLFold<T> binaryFold;
	public int currentState = 0;
	public int maxBound;

	public SLTLFoldData<T> Copy() {
		SLTLFoldData<T> data = new SLTLFoldData<>(maxBound);
		data.noopFold = noopFold;
		data.unaryFold = unaryFold;
		data.binaryFold = binaryFold;
		data.currentState = currentState;
		return data;
	}

	public static SLTLFoldData<Expression<StateData>> CreateStateFold(int maxBound) {

		SLTLFoldData<Expression<StateData>> data = new SLTLFoldData<>(maxBound);
		data.binaryFold = new BinarySLTLStateFold();
		data.unaryFold = new UnarySLTLStateFold();
		data.noopFold = new NoopSLTLStateFold();
		return data;
	}
}
