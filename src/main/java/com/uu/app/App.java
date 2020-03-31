package com.uu.app;

import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.rules.RuleSet;
import com.uu.app.APE.APEHandler;
import com.uu.app.APE.SLTLConstraintBuilder;
import com.uu.app.SLTL.*;
import com.uu.app.SLTL.StateFold.StateData;
import nl.uu.cs.ape.sat.core.implSAT.SATsolutionsList;

import java.io.IOException;
import java.rmi.server.ExportException;

/**
 * Hello world!
 */
public class App {
	public static void main(String[] args) {
		String apeConfigPath = "C:\\University\\scriptie\\APE_fork\\ape.configuration";

		APEHandler ape = null;
		try {

			ape = new APEHandler(apeConfigPath);
		} catch (Exception e) {
			System.err.println(e);
			return;
		}

		SLTLBuilder sltlBuilder = new SLTLBuilder("true")
			.addNext("add_cpt")
			.addUnary(UnarySLTLOp.Future);

		SLTLConstraintBuilder constraint = new SLTLConstraintBuilder(sltlBuilder.getResult(), 7);

		System.out.println(constraint.GetCnfString());

		ape.AddConstraint(constraint);
		ape.RunSynthesis();
	}

	public static void TestStateFold() {
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
