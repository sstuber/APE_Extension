package com.uu.app.GATA;

import java.util.*;
import java.util.stream.Collectors;

public class GataOrderVisitor extends GatlBaseVisitor<ArrayList<ArrayList<String>>> {

	@Override
	public ArrayList<ArrayList<String>> visitData(GatlParser.DataContext ctx) {
		return null;
	}


	@Override
	public ArrayList<ArrayList<String>> visitFunction(GatlParser.FunctionContext ctx) {
		String name = ctx.Ident().getText();

		ArrayList<ArrayList<String>> result;
		result = ctx.gatl().stream()
			.map(this::visit)
			.filter(Objects::nonNull)
			.flatMap(Collection::stream)
			.peek(list -> list.add(name))
			.collect(Collectors.toCollection(ArrayList::new));

		if (result.isEmpty())
			result = new ArrayList<ArrayList<String>>() {{
				add(new ArrayList<String>() {{
					add(name);
				}});
			}};

		return result;
	}
}
