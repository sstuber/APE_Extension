package com.uu.app.GATA.graph;

import java.util.ArrayList;
import java.util.List;

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

		if (nodeList.contains(node))
			return;

		nodeList.add(node);
	}

}
