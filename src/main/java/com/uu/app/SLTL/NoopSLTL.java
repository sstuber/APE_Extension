package com.uu.app.SLTL;

public class NoopSLTL extends SLTL {

	public String typeName;

	public NoopSLTL(String typeName) {
		this.typeName = typeName;
	}

	@Override
	public <T> T Fold(SLTLFoldData<T> data) {
		return data.noopFold.Fold(this, data);
	}

	@Override
	public String toString() {
		return this.typeName;
	}
}
