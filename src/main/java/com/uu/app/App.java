package com.uu.app;

import com.bpodgursky.jbool_expressions.And;
import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.Not;
import com.bpodgursky.jbool_expressions.Variable;
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

		testConstraints();

		ToolAnnotationHandler testHandler = new ToolAnnotationHandler();

		testHandler.GetToolAnnotationConstraints().forEach(System.out::println);

		ApeTester tester = ApeTester.basicSimpleDemoTester();

		System.out.println(tester.test());
	}

	public static void testConstraints() {
		printSLTL(ApeSltlFactory.DoNotUseModule("test"));        // correct
		printSLTL(ApeSltlFactory.IfThenNot("test1", "test2"));            // correct
		printSLTL(ApeSltlFactory.IfThenUse("test1", "test2"));            // correct
		printSLTL(ApeSltlFactory.UseBAfterA("test1", "test2"));            // correct
		printSLTL(ApeSltlFactory.UseModule("test"));            // correct
		printSLTL(ApeSltlFactory.UseModuleLast("test"));        // correct

		StateData testtest1 = new StateData();
		testtest1.stateId = 1;
		testtest1.name = "test";

		StateData testtest2 = new StateData();
		testtest2.stateId = 2;
		testtest2.name = "test";

		StateData testtest3 = new StateData();
		testtest3.stateId = 3;
		testtest3.name = "test";

		Expression<StateData> finalTest = And.of(
			Not.of(Variable.of(testtest1)),
			Not.of(Variable.of(testtest2)),
			Variable.of(testtest3)
		);

		Expression<StateData> finalTest2 = RuleSet.simplify(ApeSltlFactory.UseModuleLast("test").StateFold(3));

		System.out.println(finalTest2.equals(finalTest));
	}

	public static void printSLTL(SLTL sltl) {
		System.out.println("-------------------------");
		System.out.println(sltl);
		System.out.println(sltl.StateFold(3));
		System.out.println("-------------------------");
		System.out.println(RuleSet.simplify(sltl.StateFold(3)));
	}


	public static void testApe(String[] args) {
		ToolAnnotationHandler testHandler = new ToolAnnotationHandler();

		testHandler.GetToolAnnotationConstraints().forEach(System.out::println);

		String apeConfigPath = "./SimpleDemo/ape.configuration";
		String apeExtensionConfigPath = "./SimpleDemo/apeExt.configuration";

		APEHandler ape = null;
		try {

			ape = new APEHandler(apeConfigPath);
		} catch (Exception e) {
			System.err.println(e);
			return;
		}

		SLTL test = new SLTLBuilder().addNext("test")
			.addUnary(UnarySLTLOp.Next)
			.getResult();

		Expression<StateData> test2 = test.StateFold(3);

		System.out.println(test2);
		System.out.println(RuleSet.toCNF(test2));
/*
		SLTLBuilder sltlBuilder = new SLTLBuilder("true")
			.addNext("add_cpt")
			.addUnary(UnarySLTLOp.Future);

		SLTLConstraintBuilder constraint = new SLTLConstraintBuilder(sltlBuilder.getResult());

		System.out.println(constraint.GetCnfString(5));
*/

		ape.AddConstraint(ApeSltlFactory.IfThenNot("Points_and_lines", "Basemaps"));
		ape.AddConstraint(ApeSltlFactory.UseBAfterA("initGMT", "Plot_creation"));
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
			.addBinaryRight(test, BinarySLTLOp.Until).getResult();


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
