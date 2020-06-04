package com.uu.app;

import com.uu.app.APE.ToolAnnotationStruct;
import com.uu.app.GATA.GataParserHandler;
import com.uu.app.GATA.graph.GataGraph;
import com.uu.app.GATA.graph.GataGraphVisitor;
import nl.uu.cs.ape.sat.core.implSAT.SATsolutionsList;
import nl.uu.cs.ape.sat.core.solutionStructure.SolutionWorkflow;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GataGraphFilterHandler {
	GataGraph inputGraph;
	Map<String, String> iriToAnnotationMap;

	public GataGraphFilterHandler(String gataInput, ArrayList<ToolAnnotationStruct> annotationList) {
		inputGraph = getGraphFromGataString(gataInput);
		iriToAnnotationMap = getIRIToAnnotationMap(annotationList);

	}

	/**
	 * Filter out all solutions that do not fit the input graph
	 *
	 * @param satSolutionsList
	 * @return
	 */
	public List<SolutionWorkflow> FilterSolutionList(SATsolutionsList satSolutionsList) {

		return satSolutionsList.getParallelStream()
			.filter(this::compareSolutionWorkflow)
			.collect(Collectors.toCollection(ArrayList::new));
	}

	// can we build a graph that represents the input graph with this workflow?
	private boolean compareSolutionWorkflow(SolutionWorkflow workflow) {
		return true;
	}

	private Map<String, String> getIRIToAnnotationMap(List<ToolAnnotationStruct> annotationList) {
		Map<String, String> result = new HashMap<>();

		annotationList.forEach(struct ->
			result.put(struct.getName(), struct.gataAnnotation)
		);

		return result;
	}

	private GataGraph getGraphFromGataString(String input) {
		ParseTree tree = GataParserHandler.parse(input);
		GataGraphVisitor visitor = new GataGraphVisitor();

		visitor.visit(tree);

		return visitor.graph;
	}
}
