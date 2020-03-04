package com.uu.app.SLTL;

public interface UnarySLTLFold<T> {
	T Fold(T arg1, UnarySLTL obj, SLTLFoldData<T> data);
}
