package com.uu.app.GATA;

public class GataFinalFunctionVisitor extends GatlBaseVisitor<String> {

	@Override
	public String visitData(GatlParser.DataContext ctx) {
		return null;
	}

	@Override
	public String visitFunction(GatlParser.FunctionContext ctx) {
		return ctx.Ident().getText();
	}
}
