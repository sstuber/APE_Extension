package com.uu.app.SLTL;

public class SLTLBuilder {

	private SLTL result;

	public SLTLBuilder(String type) {
		this.result = new NoopSLTL(type);
	}

	public SLTLBuilder() {
		this.result = new NoopSLTL("true");
	}

	public SLTLBuilder addUnary(UnarySLTLOp op) {
		result = new UnarySLTL(result, op);
		return this;
	}

	public SLTLBuilder addNext(String module) {
		result = new UnarySLTL(result, UnarySLTLOp.Next, module);
		return this;
	}

	public SLTLBuilder addBinaryRight(SLTLBuilder builder, BinarySLTLOp op) {
		result = new BinarySLTL(op, result, builder.getResult());
		return this;
	}

	public SLTLBuilder AddBinaryLeft(SLTLBuilder builder, BinarySLTLOp op) {
		result = new BinarySLTL(op, builder.getResult(), result);
		return this;
	}

	public SLTL getResult() {
		return result;
	}

}
