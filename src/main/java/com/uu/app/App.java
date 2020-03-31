package com.uu.app;

import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.rules.RuleSet;
import com.uu.app.SLTL.*;
import com.uu.app.SLTL.StateFold.StateData;

/**
 * Hello world!
 */
public class App {
	public static void main(String[] args) {

		GatlParserHandler handler = new GatlParserHandler();

		handler.ParseAndPrint("reify(pi(sigma( interpol(noise,locations),euros)) )");

		SLTLBuilder test = new SLTLBuilder("true")
			.addNext("interpol")
			.addNext("sigma")
			.addUnary(UnarySLTLOp.Future);

		SLTL finalSltl = new SLTLBuilder("true")
			.AddBinaryRight(test, BinarySLTLOp.Until).getResult();

		//SLTLFoldData<Expression<StateData>> test2 = SLTLFoldData.CreateStateFold(2);

		Expression<StateData> res = finalSltl.StateFold(2);
		// f <sigma> <interpol> true yields ((sigma(s1) & interpol(s2) & true(s2)) | sigma(s2)

		System.out.println("formula");
		System.out.println(finalSltl);
		System.out.println("Propositional");
		System.out.println(res);

		res = RuleSet.toCNF(res);
		System.out.println("Cnf");
		System.out.println(res);

		System.out.println("Hello World!");
	}
}
