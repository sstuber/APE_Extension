package com.uu.app;

import com.uu.app.APE.APEHandler;
import com.uu.app.APE.ToolAnnotationHandler;
import com.uu.app.GATA.GataFunctionVisitor;
import com.uu.app.GATA.GataInputVisitor;
import com.uu.app.GATA.GataParserHandler;
import com.uu.app.SLTL.SLTL;
import com.uu.app.SLTL.SLTLBuilder;
import com.uu.app.SLTL.UnarySLTLOp;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.stream.Stream;

public class GataConstraintHandler {
	String basePath;
	String annotationFileName = "gata_annotation.json";
	String gataFileName = "gata_input.gata";
	
	public GataConstraintHandler(String basePath) {
		this.basePath = basePath;
	}

	public Stream<SLTL> getToolAnnotationConstraints() {
		String annotationPath = basePath + annotationFileName;
		ToolAnnotationHandler handler = new ToolAnnotationHandler(annotationPath);

		return handler.GetToolAnnotationConstraints();
	}

	public APEHandler AddGataConstraintsToApe(APEHandler handler) {
		Stream.concat(
			getToolAnnotationConstraints(),
			getInputConstraints()
		)
			.forEach(handler::AddConstraint);

		return handler;
	}


	/**
	 * Reads the GATA input and generates the constraints related to the input
	 *
	 * @return Returns a Stream with the SLTL constraints
	 */
	public Stream<SLTL> getInputConstraints() {
		File gataInputFile = new File(basePath + gataFileName);

		String inputString = "";
		try {
			inputString = FileUtils.readFileToString(gataInputFile, "UTF-8");
		} catch (Error | IOException e) {
			System.err.println("gata file was wrong");
		}

		ParseTree gataTree = GataParserHandler.parse(inputString);

		Stream<SLTL> inputConstraints = getDirectInputConstraints(gataTree);
		Stream<SLTL> presentFunctions = getPresentFunctionsConstraints(gataTree);

		return Stream.concat(inputConstraints, presentFunctions);
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

	public HashSet<String> AllFunctionNames() {
		HashSet<String> allNames = new HashSet<>();
		allNames.add("name1");
		allNames.add("name2");
		allNames.add("reify");
		allNames.add("pi");
		allNames.add("sigma");
		allNames.add("interpol");
		return allNames;

	}


}
