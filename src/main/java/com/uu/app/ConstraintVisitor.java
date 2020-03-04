package com.uu.app;

import com.uu.app.SLTL.*;

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

				SLTLBuilder child = new SLTLBuilder("true")
					.addNext(childName);

				SLTLBuilder current = new SLTLBuilder("true")
					.addNext(name)
					.addUnary(UnarySLTLOp.Neg)
					.AddBinaryRight(child, BinarySLTLOp.Until);

				constraints.add(current.getResult());


				// everything that is non null is the name of the function below
				// add a constraint linking this node and the child node
			});


		return name;
	}
}
