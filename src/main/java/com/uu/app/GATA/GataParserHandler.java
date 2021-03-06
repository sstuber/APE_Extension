package com.uu.app.GATA;

import com.uu.app.SLTL.BinarySLTLOp;
import com.uu.app.SLTL.SLTL;
import com.uu.app.SLTL.SLTLBuilder;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;

public class GataParserHandler {

	public static ArrayList<String> ParseGataToolAnnotation(String input) {
		ParseTree tree = parse(input);
		GataAnnotationVisitor visitor = new GataAnnotationVisitor();

		visitor.visit(tree);
		ArrayList<String> usedFunctions = visitor.usedFunctions;

		return usedFunctions;
	}

	public Stream<SLTL> ParseGataInput(String input) {
		GataInputVisitor visitor = new GataInputVisitor();
		ParseTree tree = parse(input);

		visitor.visit(tree);

		return visitor.constraints.build();
	}

	public static ParseTree parse(String input) {
		ANTLRInputStream inputStream = new ANTLRInputStream(input);

		// create a lexer that feeds off of inputStream CharStream
		GatlLexer lexer = new GatlLexer(inputStream);

		// create a buffer of tokens pulled from the lexer
		CommonTokenStream tokens = new CommonTokenStream(lexer);

		// create a parser that feeds off the tokens buffer
		GatlParser parser = new GatlParser(tokens);

		return parser.gatl();
	}

	public void ParseAndPrint(String input) {
		ANTLRInputStream inputStream = new ANTLRInputStream(input);

		// create a lexer that feeds off of inputStream CharStream
		GatlLexer lexer = new GatlLexer(inputStream);

		// create a buffer of tokens pulled from the lexer
		CommonTokenStream tokens = new CommonTokenStream(lexer);

		// create a parser that feeds off the tokens buffer
		GatlParser parser = new GatlParser(tokens);

		ParseTree tree = parser.gatl(); // begin parsing at init rule

		GataInputVisitor visitor = new GataInputVisitor();

		String test = visitor.visit(tree);


		System.out.println(tree.toStringTree(parser)); // print LISP-style tree
	}

}
