package com.uu.app;

import com.uu.app.APE.ToolAnnotationStruct;
import com.uu.app.GATA.GataParserHandler;
import com.uu.app.GATA.graph.GataGraph;
import com.uu.app.GATA.graph.GataGraphVisitor;
import com.uu.app.GATA.graph.GataNode;
import nl.uu.cs.ape.sat.core.implSAT.SATsolutionsList;
import nl.uu.cs.ape.sat.core.solutionStructure.ModuleNode;
import nl.uu.cs.ape.sat.core.solutionStructure.SolutionWorkflow;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
	public SATsolutionsList FilterSolutionList(SATsolutionsList satSolutionsList) {

		List<SolutionWorkflow> filteredList = satSolutionsList.getParallelStream()
			.filter(this::compareSolutionWorkflow)
			.collect(Collectors.toCollection(ArrayList::new));

		for (int i = 0; i < filteredList.size(); i++)
			filteredList.get(i).setIndex(i);

		satSolutionsList.SetSolutionList(filteredList);
		return satSolutionsList;
	}

	// can we build a graph that represents the input graph with this workflow?
	private boolean compareSolutionWorkflow(SolutionWorkflow workflow) {
		List<ModuleNode> reverseNodeList = reverseList(workflow.getModuleNodes());

		List<GataGraph> usedToolGraphs = reverseNodeList.stream()
			.map(x -> x.getUsedModule().getPredicateID())
			.map(x -> iriToAnnotationMap.get(x))
			.map(this::getGraphFromGataString)
			.collect(Collectors.toList());

		List<GataGraph> allPossibleGraphs = CreateAllPossibleGraphs(usedToolGraphs);

		for (GataGraph graph : allPossibleGraphs) {
			// should be refactored to equals function on graph
			boolean isEqualResult = compareGataGraphs(inputGraph, graph);
			if (isEqualResult) {
				System.out.println("found graph");
				return true;
			}
		}

		return false;
	}

	private boolean compareGataGraphs(GataGraph graph1, GataGraph graph2) {
		if (graph1.root.isLeaf && graph2.root.isLeaf)
			return true;

		Stack<GataNode> graph1NodeStack = new Stack<>();
		Stack<GataNode> graph2NodeSTack = new Stack<>();

		graph1NodeStack.add(graph1.root);
		graph2NodeSTack.add(graph2.root);

		while (graph1NodeStack.size() > 0 && graph2NodeSTack.size() > 0) {
			GataNode currentNode1 = graph1NodeStack.pop();
			GataNode currentNode2 = graph2NodeSTack.pop();

			if (currentNode1.isLeaf && currentNode2.isLeaf)
				continue;

			if (!currentNode1.functionName.equals(currentNode2.functionName))
				return false;

			graph1NodeStack.addAll(currentNode1.children);
			graph2NodeSTack.addAll(currentNode2.children);
		}

		System.out.println(graph1);
		System.out.println(graph2);
		return true;
	}

	private <T> List<T> reverseList(List<T> list) {
		final int last = list.size() - 1;
		return IntStream.rangeClosed(0, last) // a stream of all valid indexes into the list
			.map(i -> (last - i))             // reverse order
			.mapToObj(list::get)              // map each index to a list element
			.collect(Collectors.toList());    // wrap them up in a list
	}

	public List<GataGraph> CreateAllPossibleGraphs(List<GataGraph> tools) {
		List<GataGraph> resultList = new ArrayList<>();

		for (int i = 0; i < tools.size(); i++) {
			GataGraph newTool = tools.get(i);

			if (resultList.size() == 0) {
				resultList.add(newTool);
				continue;
			}

			List<GataGraph> newList = new ArrayList<>();

			for (GataGraph graph : resultList)
				for (int j = 0; j < graph.leaves.size(); j++) {
					GataGraph copiedGraph = graph.Copy();
					GataNode leaf = copiedGraph.leaves.get(j);
					newList.add(copiedGraph.AddSubGraphToLeaf(leaf, newTool.Copy()));
				}

			resultList = newList;
		}

		return resultList;
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
