package com.uu.app.GATA;

import java.util.ArrayList;
import java.util.HashMap;

public class GataCountFunctionsVisitor extends GatlBaseVisitor<String>  {

	HashMap<String,Integer> countMap = new HashMap<>();

	@Override
	public String visitData(GatlParser.DataContext ctx) {
		return null;
	}

	@Override
	public String visitFunction(GatlParser.FunctionContext ctx) {
		String name = ctx.Ident().getText();

		countMap.merge(name,1, Integer::sum);

		ctx.gatl()
			.forEach(this::visit);
		return null;
	}

}
