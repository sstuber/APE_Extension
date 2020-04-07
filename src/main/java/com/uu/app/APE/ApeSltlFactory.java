package com.uu.app.APE;

import com.bpodgursky.jbool_expressions.rules.RuleSet;
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

	//G(¬ <Tool 1>true | X F <Tool 2>true)
	public static SLTL IfThenUse(String param1, String param2) {
		SLTLBuilder leftside = DoNotUseModule(param1);

		SLTLBuilder rightside = new SLTLBuilder()
			.addNext(param2)
			.addUnary(UnarySLTLOp.Future)
			.addUnary(UnarySLTLOp.Next);

		SLTLBuilder finalFormula = leftside
			.addBinaryRight(rightside, BinarySLTLOp.Or)
			.addUnary(UnarySLTLOp.Global);

		return finalFormula.getResult();
	}

	// G(¬ <Tool 1>true) | X G ¬ <Tool 2>true)
	public static SLTL IfThenNot(String param1, String param2) {

		SLTLBuilder leftSide = DoNotUseModule(param1);

		SLTLBuilder rightSide = DoNotUseModule(param2)
			.addUnary(UnarySLTLOp.Global)
			.addUnary(UnarySLTLOp.Next);

		SLTLBuilder finalFormula = leftSide
			.addBinaryRight(rightSide, BinarySLTLOp.Or)
			.addUnary(UnarySLTLOp.Global);

		System.out.println(finalFormula.getResult().StateFold(3));

		System.out.println(RuleSet.simplify(finalFormula.getResult().StateFold(3)));
		System.out.println(RuleSet.toCNF(finalFormula.getResult().StateFold(3)));

		return finalFormula.getResult();
	}

	// G(¬ <Tool 1>true | X <Tool 2>true)
	public static SLTL UseBAfterA(String param1, String param2) {
		SLTLBuilder leftSide = DoNotUseModule(param1);

		SLTLBuilder rightSide = new SLTLBuilder().addNext(param2).addUnary(UnarySLTLOp.Next);

		SLTLBuilder finalFormula = leftSide
			.addBinaryRight(rightSide, BinarySLTLOp.Or)
			.addUnary(UnarySLTLOp.Global);

		return finalFormula.getResult();
	}

	// F <Tool 1> true
	public static SLTL UseModule(String param1) {
		return new SLTLBuilder()
			.addNext(param1)
			.addUnary(UnarySLTLOp.Future)
			.getResult();
	}


}
