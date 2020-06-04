package com.uu.app;

import com.uu.app.GATA.GataParserHandler;
import com.uu.app.GATA.graph.GataGraph;
import com.uu.app.GATA.graph.GataGraphVisitor;
import nl.uu.cs.ape.sat.core.implSAT.SATsolutionsList;
import org.antlr.v4.runtime.tree.ParseTree;

public class GataGraphFilterHandler {
	GataGraph inputGraph;
	SATsolutionsList solutionList;

	public GataGraphFilterHandler(String gataInput, SATsolutionsList solutionList) {
		inputGraph = getGraphFromGataString(gataInput);
		this.solutionList = solutionList;

	}

	private GataGraph getGraphFromGataString(String input) {
		ParseTree tree = GataParserHandler.parse(input);
		GataGraphVisitor visitor = new GataGraphVisitor();

		visitor.visit(tree);

		return visitor.graph;
	}


}
