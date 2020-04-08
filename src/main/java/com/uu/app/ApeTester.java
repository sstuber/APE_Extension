package com.uu.app;

import com.uu.app.APE.APEHandler;
import com.uu.app.APE.ApeSltlFactory;
import nl.uu.cs.ape.sat.core.implSAT.SATsolutionsList;
import nl.uu.cs.ape.sat.core.solutionStructure.SolutionWorkflow;

import java.util.HashMap;
import java.util.Map;

public class ApeTester {

	APEHandler apeExternalConstraints;
	APEHandler apeInternalConstraints;

	public ApeTester(APEHandler external, APEHandler internal) {
		this.apeExternalConstraints = external;
		this.apeInternalConstraints = internal;
	}

	public static ApeTester basicSimpleDemoTester() {
		APEHandler internal = null;
		APEHandler external = null;
		String apeConfigPath = "./SimpleDemo/ape.configuration";
		String apeExtensionConfigPath = "./SimpleDemo/apeExt.configuration";

		try {
			internal = new APEHandler(apeConfigPath);
			external = new APEHandler(apeExtensionConfigPath);
		} catch (Exception e) {
			System.err.println(e);
			return null;
		}

		external.AddConstraint(ApeSltlFactory.IfThenNot("Points_and_lines", "Basemaps"));
		external.AddConstraint(ApeSltlFactory.UseBAfterA("initGMT", "Plot_creation"));
		external.AddConstraint(ApeSltlFactory.UseModule("Draw_water"));
		external.AddConstraint(ApeSltlFactory.UseModule("Draw_land"));
		external.AddConstraint(ApeSltlFactory.UseModule("Draw_political_borders"));
		external.AddConstraint(ApeSltlFactory.UseModule("Points_and_lines"));


		return new ApeTester(external, internal);
	}

	public boolean test() {
		SATsolutionsList internal = this.apeInternalConstraints.RunSynthesisWithResults();
		SATsolutionsList external = this.apeExternalConstraints.RunSynthesisWithResults();

		HashMap<Integer, Integer> internalCounts = countSollutions(internal);
		HashMap<Integer, Integer> externalCounts = countSollutions(external);

		return internalCounts.equals(externalCounts);
	}

	public HashMap<Integer, Integer> countSollutions(SATsolutionsList solutions) {
		HashMap<Integer, Integer> result = new HashMap<>();

		solutions.getStream()
			.map(SolutionWorkflow::getSolutionlength)
			.forEach(length -> {
				Integer lengthCount = result.getOrDefault(length, 0);
				result.put(length, lengthCount + 1);
			});

		return result;
	}


}
