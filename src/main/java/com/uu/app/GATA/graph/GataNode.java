package com.uu.app.GATA.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GataNode {

	public String functionName;

	public GataNode parentNode;
	public List<GataNode> children;

	public boolean isRoot = false;
	public boolean isLeaf = false;

	GataNode(String functionName) {
		this.functionName = functionName;
		this.children = new ArrayList<>();
	}

	public static GataNode CreateRootNode(String name) {
		return new GataNode(name) {{
			isRoot = true;
		}};
	}

	public static GataNode CreateNode(String name) {
		return new GataNode(name);
	}

	public static GataNode CreateLeafNode(String name) {
		return new GataNode(name) {{
			isLeaf = true;
		}};
	}

	public void AddChild(GataNode node) {
		node.parentNode = this;
		children.add(node);
	}

	public GataNode Copy() {
		GataNode result;

		if (isLeaf) {
			result = GataNode.CreateLeafNode(functionName);
			return result;
		}

		result = GataNode.CreateNode(functionName);

		children.stream()
			.map(GataNode::Copy)
			.forEach(result::AddChild);

		return result;
	}

	public Stream<GataNode> GetLeaves() {
		if (isLeaf)
			return Stream.of(this);

		return children
			.stream()
			.flatMap(GataNode::GetLeaves);
	}

	public Stream<GataNode> GetAllNodes() {
		return Stream.concat(
			Stream.of(this),
			children.stream().flatMap(GataNode::GetAllNodes)
		);
	}

	public String toStringTree(int depth) {
		String childString = "";
		String indentation = String.join("", Collections.nCopies(depth, "| "));

		if (!isLeaf)
			childString = children.stream()
				.map(child -> child.toStringTree(depth + 1))
				.collect(Collectors.joining());

		return indentation + functionName + "\n" + childString;
	}
}
