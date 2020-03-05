package com.uu.app;

import com.bpodgursky.jbool_expressions.Expression;
import com.uu.app.SLTL.SLTL;
import com.uu.app.SLTL.SLTLBuilder;
import com.uu.app.SLTL.SLTLFoldData;
import com.uu.app.SLTL.UnarySLTLOp;

/**
 * Hello world!
 */
public class App {
	public static void main(String[] args) {

		GatlParserHandler handler = new GatlParserHandler();

		handler.ParseAndPrint("reify(pi(sigma( interpol(noise,locations)  , euros)) )");

		SLTL test = new SLTLBuilder("true")
			.addNext("interpol")
			.addNext("sigma")
			.addUnary(UnarySLTLOp.Future).getResult();

		SLTLFoldData<Expression<StateData>> test2 = SLTLFoldData.CreateStateFold(2);

		Expression<StateData> res = test.Fold(test2);

		// f <sigma> <interpol> true yields ((sigma(s1) & interpol(s2) & true(s2)) | sigma(s2)

		System.out.println("Hello World!");
	}
}
