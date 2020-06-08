package com.uu.app.APE;

import com.uu.app.GataConstraintHandler;
import com.uu.app.GataGraphFilterHandler;
import com.uu.app.SLTL.SLTL;
import guru.nidi.graphviz.attribute.RankDir;
import nl.uu.cs.ape.sat.APE;
import nl.uu.cs.ape.sat.core.extrernal.ExternalConstraintBuilder;
import nl.uu.cs.ape.sat.core.implSAT.SATsolutionsList;

import java.io.IOException;

public class APEHandler {

	APE apeFramework;
	String configPath;

	SATsolutionsList solutionsList;
	GataGraphFilterHandler filterHandler;

	public APEHandler(String configPath) throws IOException {
		this.configPath = configPath;
		this.apeFramework = new APE(configPath);

	}

	public static APEHandler GataApeHandler(String basepath) throws IOException {
		APEHandler handler = new APEHandler(basepath + "apeExt.configuration");

		GataConstraintHandler constraintHandler = new GataConstraintHandler(basepath);

		constraintHandler.AddGataConstraintsToApe(handler);

		// Initialize GraphFilterHandler
		handler.filterHandler = new GataGraphFilterHandler(
			constraintHandler.GetInputString(),
			constraintHandler.GetToolAnnotationList()
		);
		return handler;
	}


	public void AddConstraint(SLTL constraint) {
		this.apeFramework.AddExternalConstraint(new SltlToSatConstraintBuilder(constraint));
	}

	public void AddConstraint(ExternalConstraintBuilder constraint) {
		this.apeFramework.AddExternalConstraint(constraint);
	}

	public SATsolutionsList RunSynthesisWithResults() {
		SATsolutionsList solutions = null;
		try {
			solutions = apeFramework.runSynthesis(this.configPath, apeFramework.getDomainSetup());
		} catch (IOException e) {
			System.err.println("Error in synthesis execution. Writing to the file system failed.");

		}

		solutionsList = solutions;

		return solutions;
	}

	public void GenerateGraphs() {
		if (solutionsList == null)
			return;

		try {
			apeFramework.writeSolutionToFile(solutionsList);
			apeFramework.writeControlFlowGraphs(solutionsList, RankDir.LEFT_TO_RIGHT);
		} catch (IOException e) {
			System.err.println("Error in writing the solutions. to the file system.");
			e.printStackTrace();
		}

	}


	public void RunSynthesis() {
		SATsolutionsList solutions = null;
		try {
			solutions = apeFramework.runSynthesis(this.configPath, apeFramework.getDomainSetup());
		} catch (IOException e) {
			System.err.println("Error in synthesis execution. Writing to the file system failed.");
			return;
		}

		if (solutions.isEmpty()) {
			System.out.println("UNSAT");
		} else {
			try {
				apeFramework.writeSolutionToFile(solutions);
				apeFramework.writeControlFlowGraphs(solutions, RankDir.LEFT_TO_RIGHT);
			} catch (IOException e) {
				System.err.println("Error in writing the solutions. to the file system.");
				e.printStackTrace();
			}

		}
	}

}
