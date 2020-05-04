package com.uu.app.GATA;

import com.uu.app.SLTL.*;

import java.util.Objects;
import java.util.stream.Stream;

public class GataInputVisitor extends GatlBaseVisitor<String> {

	public Stream.Builder<SLTL> constraints = Stream.<SLTL>builder();

	@Override
	public String visitData(GatlParser.DataContext ctx) {
		return null;
	}

	@Override
	public String visitFunction(GatlParser.FunctionContext ctx) {
		String name = ctx.Ident().getText();

		SLTL useFunctionConstraint = new SLTLBuilder()
			.addNext(name)
			.addUnary(UnarySLTLOp.Finally)
			.getResult();
		constraints.add(useFunctionConstraint);

		ctx.gatl().stream()
			.map(this::visit)
			.filter(Objects::nonNull) // get idents of lower functions
			.forEach(childName -> {

				SLTLBuilder child = new SLTLBuilder("true")
					.addNext(childName);

				SLTLBuilder current = new SLTLBuilder("true")
					.addNext(name)
					.addUnary(UnarySLTLOp.Neg)
					.addBinaryRight(child, BinarySLTLOp.Until);

				SLTL finalSLTL = current.getResult();

				constraints.add(finalSLTL);

				// everything that is non null is the name of the function below
				// add a constraint linking this node and the child node
			});


		return name;
	}
}
