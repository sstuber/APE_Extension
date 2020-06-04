package com.uu.app.GATA.graph;

import java.util.ArrayList;
import java.util.List;

public class GataNode {

	String functionName;

	GataNode parentNode;
	List<GataNode> children;

	boolean isRoot = false;
	boolean isLeaf = false;

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
}
