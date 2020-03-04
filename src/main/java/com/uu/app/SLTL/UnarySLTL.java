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
	<T> T Fold(SLTLFoldData<T> data) {

		T rightFold = right.Fold(data);

		return data.unaryFold.Fold(rightFold, this, data);
	}

	@Override
	public String toString() {
		if (op == UnarySLTLOp.Next)
			return "<" + this.moduleName + "> " + right.toString();

		return op.toString() + " " + right.toString();
	}
}
