package com.uu.app.GATA.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GataGraph {

	public GataNode root;
	public List<GataNode> nodeList;
	public List<GataNode> leaves;

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

	@Override
	public String toString() {
		return "GataGraph\n" +
			"  root=" + root.functionName + "\n" +
			"  leaves=" + leaves.stream().map(x -> x.functionName).collect(Collectors.toList()) +
			"\n  tree =\n" + root.toStringTree(0);
	}

	public GataGraph AddSubGraphToLeaf(GataNode leaf, GataGraph subGraph) {
		if (!leaves.contains(leaf)) {
			System.err.println("given leaf node not in leaves");
		}

		GataNode parent = leaf.parentNode;
		GataNode newChild = subGraph.root;
		newChild.isRoot = false;
		// remove leaf from current tree
		parent.children.remove(leaf);
		leaves.remove(leaf);

		// set references between the new link correct
		parent.children.add(newChild);
		newChild.parentNode = parent;

		// add all the nodes from the sub graph to the graph
		nodeList.addAll(subGraph.nodeList);
		leaves.addAll(subGraph.leaves);
		return this;
	}
}
