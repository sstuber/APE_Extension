package com.uu.app.SLTL;

public abstract class SLTL {
	abstract <T> T Fold(
		BinarySLTLFold<T> binarySLTLFold,
		UnarySLTLFold<T> unarySLTLFold,
		NoopSLTLFold<T> noopSLTLFold
	);
}
