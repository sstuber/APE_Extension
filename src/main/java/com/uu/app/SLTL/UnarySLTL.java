package com.uu.app.SLTL;

public class UnarySLTL extends SLTL {
	public UnarySLTL(SLTL right, UnarySLTLOp op, String moduleName) {
		this.right = right;
		this.op = op;
		this.moduleName = moduleName;
	}

	public UnarySLTL(SLTL right, UnarySLTLOp op) {
		this.right = right;
		this.op = op;
		this.moduleName = null;
	}

	SLTL right;
	UnarySLTLOp op;
	String moduleName;

	@Override
	<T> T Fold(BinarySLTLFold<T> binarySLTLFold, UnarySLTLFold<T> unarySLTLFold, NoopSLTLFold<T> noopSLTLFold) {

		T rightFold = right.Fold(binarySLTLFold, unarySLTLFold, noopSLTLFold);

		return unarySLTLFold.Fold(rightFold, this);
	}
}
