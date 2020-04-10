package com.uu.app.GATA;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class GataFunctionVisitor extends GatlBaseVisitor<String> {

	public ArrayList<String> functionNames = new ArrayList<>();

	@Override
	public String visitData(GatlParser.DataContext ctx) {
		return null;
	}

	@Override
	public String visitFunction(GatlParser.FunctionContext ctx) {
		functionNames.add(ctx.Ident().getText());

		ctx.gatl().forEach(this::visit);

		return null;
	}
}
