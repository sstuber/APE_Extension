package com.uu.app.SLTL;

public class BinarySLTL extends SLTL {
	BinarySLTLOp op;
	SLTL left;
	SLTL right;

	public BinarySLTL(BinarySLTLOp op, SLTL left, SLTL right) {
		this.op = op;
		this.left = left;
		this.right = right;
	}

	@Override
	<T> T Fold(BinarySLTLFold<T> binarySLTLFold, UnarySLTLFold<T> unarySLTLFold, NoopSLTLFold<T> noopSLTLFold) {

		T leftFold = left.Fold(binarySLTLFold, unarySLTLFold, noopSLTLFold);
		T rightFold = right.Fold(binarySLTLFold, unarySLTLFold, noopSLTLFold);

		return binarySLTLFold.Fold(leftFold, rightFold, this);
	}
}
