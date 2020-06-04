package com.uu.app.GATA.graph;

import com.uu.app.GATA.GatlBaseVisitor;
import com.uu.app.GATA.GatlParser;

import java.util.stream.Stream;

public class GataGraphVisitor extends GatlBaseVisitor<GataNode> {

	public GataGraph graph = new GataGraph();

	@Override
	public GataNode visitData(GatlParser.DataContext ctx) {
		String name = ctx.Ident().getText();
		GataNode leaf = GataNode.CreateLeafNode(name);

		graph.leaves.add(leaf);
		graph.nodeList.add(leaf);

		graph.AddRoot(leaf);

		return leaf;
	}

	@Override
	public GataNode visitFunction(GatlParser.FunctionContext ctx) {
		String name = ctx.Ident().getText();
		GataNode currentNode = GataNode.CreateNode(name);

		Stream<GataNode> children = ctx.gatl()
			.stream()
			.map(this::visit);

		children.forEach(currentNode::AddChild);

		graph.AddRoot(currentNode);

		return currentNode;
	}
}
