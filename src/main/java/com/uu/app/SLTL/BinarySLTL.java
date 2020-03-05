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
	public <T> T Fold(SLTLFoldData<T> data) {

		T leftFold = left.Fold(data);
		T rightFold = right.Fold(data);

		return data.binaryFold.Fold(leftFold, rightFold, this, data);
	}

	@Override
	public String toString() {
		return left.toString() + " " + op.toString() + " " + right.toString();
	}
}
