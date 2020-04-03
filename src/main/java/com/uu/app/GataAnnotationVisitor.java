package com.uu.app;

import java.util.ArrayList;

public class GataAnnotationVisitor extends GatlBaseVisitor<String> {

	ArrayList<String> usedFunctions = new ArrayList<>();

	@Override
	public String visitData(GatlParser.DataContext ctx) {
		return null;
	}

	@Override
	public String visitFunction(GatlParser.FunctionContext ctx) {
		String name = ctx.Ident().getText();
		usedFunctions.add(name);

		ctx.gatl()
			.forEach(this::visit);

		return name;
	}
}
