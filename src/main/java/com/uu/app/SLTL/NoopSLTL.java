package com.uu.app.SLTL;

public class NoopSLTL extends SLTL {

	String typeName;

	public NoopSLTL(String typeName) {
		this.typeName = typeName;
	}

	@Override
	<T> T Fold(BinarySLTLFold<T> binarySLTLFold, UnarySLTLFold<T> unarySLTLFold, NoopSLTLFold<T> noopSLTLFold) {
		return noopSLTLFold.Fold(this);
	}
}
