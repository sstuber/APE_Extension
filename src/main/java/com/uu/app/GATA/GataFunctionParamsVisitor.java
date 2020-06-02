package com.uu.app.GATA;

import java.util.*;
import java.util.stream.Collectors;

public class GataFunctionParamsVisitor extends GatlBaseVisitor<String> {

	public Map<String, List<List<String>>> paramMap = new HashMap<>();

	@Override
	public String visitData(GatlParser.DataContext ctx) {
		return null;
	}

	@Override
	public String visitFunction(GatlParser.FunctionContext ctx) {
		String functionName = ctx.Ident().getText();


		List<String> params = ctx.gatl().stream()
			.map(this::visit)
			.filter(Objects::nonNull)
			.collect(Collectors.toList());

		if (params.size() < 2)
			return functionName;

		List<List<String>> mapResult;
		mapResult = paramMap.getOrDefault(functionName, new ArrayList<>());
		mapResult.add(params);
		paramMap.put(functionName, mapResult);

		return functionName;
	}
}
