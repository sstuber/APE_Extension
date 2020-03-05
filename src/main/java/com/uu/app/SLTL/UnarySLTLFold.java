package com.uu.app.SLTL;

public interface UnarySLTLFold<T> {
	T Fold(UnarySLTL obj, SLTLFoldData<T> data);
}
