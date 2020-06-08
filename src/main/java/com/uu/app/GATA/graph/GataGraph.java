package com.uu.app.GATA.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GataGraph {

	GataNode root;
	List<GataNode> nodeList;
	List<GataNode> leaves;

	public GataGraph() {
		nodeList = new ArrayList<>();
		leaves = new ArrayList<>();
	}

	public void AddRoot(GataNode node) {
		node.isRoot = true;

		if (root != null)
			root.isRoot = false;

		root = node;
	}

	public GataGraph Copy() {
		GataGraph newGraph = new GataGraph();
		GataNode newRoot = root.Copy();

		newGraph.leaves = newRoot
			.GetLeaves()
			.collect(Collectors.toList());

		newGraph.nodeList = newRoot
			.GetAllNodes()
			.collect(Collectors.toList());

		newGraph.AddRoot(newRoot);
		return newGraph;
	}
}
