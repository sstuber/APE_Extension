package com.uu.app.APE;

import guru.nidi.graphviz.attribute.RankDir;
import nl.uu.cs.ape.sat.APE;
import nl.uu.cs.ape.sat.core.extrernal.ExternalConstraintBuilder;
import nl.uu.cs.ape.sat.core.implSAT.SATsolutionsList;

import java.io.IOException;

public class APEHandler {

	APE apeFramework;
	String configPath;

	public APEHandler(String configPath) throws IOException {
		this.configPath = configPath;
		this.apeFramework = new APE(configPath);

	}

	public void AddConstraint(ExternalConstraintBuilder constraint) {
		this.apeFramework.AddExternalConstraint(constraint);
	}

	public void RunSynthesis() {
		SATsolutionsList solutions = null;
		try {
			solutions = apeFramework.runSynthesis(this.configPath);
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
