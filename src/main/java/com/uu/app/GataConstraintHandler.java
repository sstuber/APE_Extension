package com.uu.app;

import com.uu.app.APE.APEHandler;
import com.uu.app.APE.AtMostNConstraintBuilder;
import com.uu.app.APE.ToolAnnotationHandler;
import com.uu.app.GATA.*;
import com.uu.app.SLTL.BinarySLTLOp;
import com.uu.app.SLTL.SLTL;
import com.uu.app.SLTL.SLTLBuilder;
import com.uu.app.SLTL.UnarySLTLOp;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public class GataConstraintHandler {
	String basePath;
	String annotationFileName = "gata_annotation.json";
	String gataFileName = "gata_input.gata";

	ToolAnnotationHandler toolAnnotationHandler;

	public GataConstraintHandler(String basePath) {
		this.basePath = basePath;

		String annotationPath = basePath + annotationFileName;
		toolAnnotationHandler = new ToolAnnotationHandler(annotationPath);

	}

	public Stream<SLTL> getToolAnnotationConstraints() {

		return toolAnnotationHandler.GetToolAnnotationConstraints();
	}

	public APEHandler AddGataConstraintsToApe(APEHandler handler) {
		Stream.concat(
			getToolAnnotationConstraints(),
			getInputConstraints()
		).forEach(constraint -> {
			System.out.println(constraint);
			handler.AddConstraint(constraint);
		});

		handler.AddConstraint(getAtMostNConstraintBuilder());

		return handler;
	}

	/**
	 * Reads the GATA input and generates the constraints related to the input
	 *
	 * @return Returns a Stream with the SLTL constraints
	 */

	public Stream<SLTL> getInputConstraints() {
		ParseTree gataTree = getParseTree();

		Stream<SLTL> orderConstraints = getOrderFunctionConstraints(gataTree);
		Stream<SLTL> presentFunctions = getPresentFunctionsConstraints(gataTree);
		Stream<SLTL> functionTallyConstraints = getFunctionCountConstraints(gataTree);

		return Stream.of(orderConstraints, presentFunctions, functionTallyConstraints).flatMap(x -> x);
	}

	// should get the formula's for the order
	public Stream<SLTL> getDirectInputConstraints(ParseTree tree) {
		GataInputVisitor visitor = new GataInputVisitor();
		visitor.visit(tree);

		return visitor.constraints.build();
	}

	// should prevent function that are not in the input to be present

	/**
	 * Reads the GATA input and creates constraints for all the functions not
	 * present in the input in the form of G( Not (<name> true))
	 *
	 * @param tree The ParseTree of the GATA input
	 * @return A Stream that contains the resulting constraints
	 */
	public Stream<SLTL> getPresentFunctionsConstraints(ParseTree tree) {

		GataFunctionVisitor visitor = new GataFunctionVisitor();
		visitor.visit(tree);

		HashSet<String> allNames = AllFunctionNames();
		HashSet<String> presentNames = new HashSet<>(visitor.functionNames);

		return allNames.stream()
			.filter(name -> !presentNames.contains(name))
			.map(functionName -> new SLTLBuilder()
				.addNext(functionName)
				.addUnary(UnarySLTLOp.Neg)
				.addUnary(UnarySLTLOp.Global)
				.getResult()
			);
	}

	public Stream<SLTL> getOrderFunctionConstraints(ParseTree tree) {
		GataOrderVisitor visitor = new GataOrderVisitor();
		ArrayList<ArrayList<String>> list = visitor.visit(tree);

		return list.stream()
			.flatMap(orderList -> transformSimpleOrder(orderList, visitor.multipleParamFunctionSet));
	}

	Stream<SLTL> transformSimpleOrder(ArrayList<String> functionOrder, Set<String> multipleParamFunctionSet) {
		Stream.Builder<SLTL> resultList = Stream.builder();

		for (int i = 0; i < functionOrder.size() - 1; i++) {
			String firstName = functionOrder.get(i);
			String secondName = functionOrder.get(i + 1);

			SLTL result = multipleParamFunctionSet.contains(secondName)
				? transformNaryFunction(firstName, secondName)
				: transformUnaryFunction(firstName, secondName);
			resultList.add(result);
		}

		return resultList.build();
	}

	SLTL transformNaryFunction(String firstName, String secondName) {
		SLTLBuilder firstPart = new SLTLBuilder()
			.addNext(firstName);

		SLTLBuilder secondPart = new SLTLBuilder()
			.addNext(secondName)
			.addUnary(UnarySLTLOp.Finally);

		return firstPart.addBinaryRight(secondPart, BinarySLTLOp.And)
			.addUnary(UnarySLTLOp.Finally)
			.getResult();
	}

	SLTL transformUnaryFunction(String firstName, String secondName) {
		SLTLBuilder firstPart = new SLTLBuilder()
			.addNext(firstName);

		SLTLBuilder secondPart = new SLTLBuilder()
			.addNext(secondName);

		SLTLBuilder intermediate = secondPart
			.addBinaryRight(
				new SLTLBuilder().addNext(secondName).addUnary(UnarySLTLOp.Next),
				BinarySLTLOp.Or
			);

		return firstPart.addBinaryRight(intermediate, BinarySLTLOp.And)
			.addUnary(UnarySLTLOp.Finally)
			.getResult();
	}

	SLTL transformFunctionOrder(ArrayList<String> functionOrder, Set<String> multipleParamFunctionSet) {
		SLTLBuilder result = new SLTLBuilder();

		for (int i = functionOrder.size() - 1; i >= 0; i--) {
			String name = functionOrder.get(i);

			// <name> true
			SLTLBuilder intermediateResult = new SLTLBuilder().addNext(name);

			// <name> true and result
			intermediateResult = intermediateResult.addBinaryRight(result, BinarySLTLOp.And);

			// F (<name> true and result)
			intermediateResult = intermediateResult.addUnary(UnarySLTLOp.Finally);

			result = intermediateResult;
		}

		return result.getResult();
	}

	SLTL transformFunctionOrder2(ArrayList<String> functionOrder, Set<String> multipleParamFunctionSet) {
		List<SLTLBuilder> intermediateList = new ArrayList<>();
		intermediateList.add(new SLTLBuilder());

		for (int i = functionOrder.size() - 1; i >= 1; i--) {
			String name = functionOrder.get(i);

			if (multipleParamFunctionSet.contains(name)) {
				SLTLBuilder combinedList = SLTLBuilder.combineList(intermediateList, BinarySLTLOp.Or);

				// <name> true
				SLTLBuilder intermediateResult = new SLTLBuilder().addNext(name);
				// <name> true and result
				intermediateResult = intermediateResult.addBinaryRight(combinedList, BinarySLTLOp.And);
				// F (<name> true and result)
				intermediateResult = intermediateResult.addUnary(UnarySLTLOp.Finally);

				SLTLBuilder finalIntermediateResult = intermediateResult;
				intermediateList = new ArrayList<SLTLBuilder>() {{
					add(finalIntermediateResult);
				}};
				continue;
			}

			List<SLTLBuilder> test = new ArrayList<>();
			for (SLTLBuilder sltlBuilder : intermediateList) {
				SLTLBuilder sameState = new SLTLBuilder().addNext(name);
				SLTLBuilder nextState = new SLTLBuilder().addNext(name).addUnary(UnarySLTLOp.Next);

				test.add(sameState.addBinaryRight(sltlBuilder, BinarySLTLOp.And));
				test.add(nextState.addBinaryRight(sltlBuilder.addUnary(UnarySLTLOp.Next), BinarySLTLOp.And));
			}

			intermediateList = test;

		}

		SLTLBuilder combinedList = SLTLBuilder.combineList(intermediateList, BinarySLTLOp.Or);
		String name = functionOrder.get(0);
		// <name> true
		SLTLBuilder intermediateResult = new SLTLBuilder().addNext(name);
		// <name> true and result
		intermediateResult = intermediateResult.addBinaryRight(combinedList, BinarySLTLOp.And);
		// F (<name> true and result)
		intermediateResult = intermediateResult.addUnary(UnarySLTLOp.Finally);

		SLTLBuilder result = intermediateResult;

		return result.getResult();
	}

	public Stream<SLTL> getFunctionCountConstraints(ParseTree tree) {
		GataCountFunctionsVisitor visitor = new GataCountFunctionsVisitor();
		visitor.visit(tree);

		return visitor.countMap.entrySet().stream()
			.map(this::transformFunctionCount);
	}

	SLTL transformFunctionCount(Map.Entry<String, Integer> entry) {
		int depth = entry.getValue();
		String name = entry.getKey();
		SLTLBuilder result = new SLTLBuilder();

		while (depth > 0) {
			// <name> true
			SLTLBuilder intermediateResult = new SLTLBuilder().addNext(name);

			// <name> true and result
			intermediateResult = intermediateResult.addBinaryRight(result.addUnary(UnarySLTLOp.Next), BinarySLTLOp.And);

			// F (<name> true and result)
			intermediateResult = intermediateResult.addUnary(UnarySLTLOp.Finally);

			result = intermediateResult;

			depth--;
		}

		return result.getResult();
	}

	/**
	 * Generates a constraint builder that for each function, if the function appears N times
	 * prevents the function from appearing N+1 times
	 *
	 * @return
	 */
	public AtMostNConstraintBuilder getAtMostNConstraintBuilder() {
		ParseTree tree = getParseTree();
		GataCountFunctionsVisitor visitor = new GataCountFunctionsVisitor();
		visitor.visit(tree);

		return new AtMostNConstraintBuilder(visitor.countMap);
	}

	public HashSet<String> AllFunctionNames() {
		HashSet<String> allNames = new HashSet<>();

		toolAnnotationHandler.functionToolListMap.forEach((key, value) -> allNames.add(key));

		return allNames;
	}


	private ParseTree getParseTree() {
		File gataInputFile = new File(basePath + gataFileName);

		String inputString = "";
		try {
			inputString = FileUtils.readFileToString(gataInputFile, "UTF-8");
		} catch (Error | IOException e) {
			System.err.println("gata file was wrong");
		}

		return GataParserHandler.parse(inputString);
	}

}
