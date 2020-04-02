package com.uu.app.APE;

import com.uu.app.SLTL.BinarySLTLOp;
import com.uu.app.SLTL.SLTL;
import com.uu.app.SLTL.SLTLBuilder;
import com.uu.app.SLTL.UnarySLTLOp;

/*  HelperClass
	Makes creating common SLTL formula's easier
 */

public class ApeSltlFactory {

	// ¬ <Tool 1>true
	static SLTLBuilder DoNotUseModule(String param1) {
		return new SLTLBuilder("true")
			.addNext(param1)
			.addUnary(UnarySLTLOp.Neg);
	}

	// G(¬ <Tool 1>true) |
	//	 X G ¬ <Tool 2>true)
	public static SLTLConstraintBuilder IfThenNot(String param1, String param2) {

		SLTLBuilder leftSide = DoNotUseModule(param1);

		SLTLBuilder rightSide = DoNotUseModule(param2)
			.addUnary(UnarySLTLOp.Global)
			.addUnary(UnarySLTLOp.Next);

		SLTLBuilder finalFormula = leftSide
			.AddBinaryRight(rightSide, BinarySLTLOp.Or)
			.addUnary(UnarySLTLOp.Global);

		return new SLTLConstraintBuilder(finalFormula.getResult());
	}

	// G(¬ <Tool 1>true |
	//	 X <Tool 2>true)
	public static SLTLConstraintBuilder UseBAfterA(String param1, String param2) {
		SLTLBuilder leftSide = DoNotUseModule(param1);

		SLTLBuilder rightSide = new SLTLBuilder().addNext(param2);

		SLTLBuilder finalFormula = leftSide
			.AddBinaryRight(rightSide, BinarySLTLOp.Or)
			.addUnary(UnarySLTLOp.Global);

		return new SLTLConstraintBuilder(finalFormula.getResult());
	}

	// F <Tool 1>true
	public static SLTLConstraintBuilder UseModule(String param1) {
		return new SLTLConstraintBuilder(
			new SLTLBuilder()
				.addNext(param1)
				.addUnary(UnarySLTLOp.Future)
				.getResult()
		);
	}


}
