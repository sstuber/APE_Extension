package com.uu.app;

import com.uu.app.SLTL.SLTL;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConstraintVisitor extends GatlBaseVisitor<String> {

	ArrayList<SLTL> constraints = new ArrayList<>();

	@Override
	public String visitData(GatlParser.DataContext ctx) {


		return null;
	}

	@Override
	public String visitFunction(GatlParser.FunctionContext ctx) {

		String name = ctx.Ident().getText();

		ctx.gatl().stream()
			.map(this::visit)
			.filter(Objects::nonNull) // get idents of lower functions
			.forEach(childName -> {
				// everything that is non null is the name of the function below
				// add a constraint linking this node and the child node
			});


		return name;
	}
}
