package com.uu.app;

import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.rules.RuleSet;
import com.uu.app.APE.APEHandler;
import com.uu.app.APE.ApeSltlFactory;
import com.uu.app.APE.ToolAnnotationHandler;
import com.uu.app.GATA.GataParserHandler;
import com.uu.app.SLTL.*;
import com.uu.app.SLTL.StateFold.StateData;

/**
 * Hello world!
 */
public class App {
	public static void main(String[] args) {
		ToolAnnotationHandler testHandler = new ToolAnnotationHandler();

		testHandler.GetToolAnnotationConstraints().forEach(System.out::println);

		String apeConfigPath = "./ape.configuration";
		String apeExtensionConfigPath = "./apeExt.configuration";

		APEHandler ape = null;
		try {

			ape = new APEHandler(apeExtensionConfigPath);
		} catch (Exception e) {
			System.err.println(e);
			return;
		}
/*
		SLTLBuilder sltlBuilder = new SLTLBuilder("true")
			.addNext("add_cpt")
			.addUnary(UnarySLTLOp.Future);

		SLTLConstraintBuilder constraint = new SLTLConstraintBuilder(sltlBuilder.getResult());

		System.out.println(constraint.GetCnfString(5));
*/

		ape.AddConstraint(ApeSltlFactory.IfThenNot("Points_and_lines", "Basemaps"));
		//ape.AddConstraint(ApeSltlFactory.UseBAfterA("initGMT", "Plot_creation"));
		ape.AddConstraint(ApeSltlFactory.UseModule("Draw_water"));
		ape.AddConstraint(ApeSltlFactory.UseModule("Draw_land"));
		ape.AddConstraint(ApeSltlFactory.UseModule("Draw_political_borders"));
		ape.AddConstraint(ApeSltlFactory.UseModule("Points_and_lines"));


		//ape.AddConstraint(constraint);
		ape.RunSynthesis();
	}

	public static void TestStateFold() {
		GataParserHandler handler = new GataParserHandler();

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
