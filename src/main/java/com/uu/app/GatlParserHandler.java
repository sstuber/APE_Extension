package com.uu.app;

import com.uu.app.SLTL.BinarySLTLOp;
import com.uu.app.SLTL.SLTL;
import com.uu.app.SLTL.SLTLBuilder;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;

public class GatlParserHandler {

	private static ParseTree parse(String input){
		ANTLRInputStream inputStream = new ANTLRInputStream(input);

		// create a lexer that feeds off of inputStream CharStream
		GatlLexer lexer = new GatlLexer(inputStream);

		// create a buffer of tokens pulled from the lexer
		CommonTokenStream tokens = new CommonTokenStream(lexer);

		// create a parser that feeds off the tokens buffer
		GatlParser parser = new GatlParser(tokens);

		return parser.gatl();
	}

	public static SLTLBuilder ParseGataToolAnnotation(String input)  {
		ParseTree tree = parse(input);
		GataAnnotationVisitor visitor = new GataAnnotationVisitor();

		visitor.visit(tree);
		ArrayList<String> usedFunctions = visitor.usedFunctions;

		Optional<SLTLBuilder> conjunctionOfNames = usedFunctions.stream()
			.map(name -> new SLTLBuilder().addNext(name))
			.reduce((acc, test) -> acc.AddBinaryRight(test, BinarySLTLOp.And));

		return conjunctionOfNames
			.orElse(null);
	}

	public ArrayList<SLTL> ParseGataInput(String input){
		ConstraintVisitor visitor = new ConstraintVisitor();
		ParseTree tree =  parse(input);

		visitor.visit(tree);

		return visitor.constraints;
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

		ConstraintVisitor visitor = new ConstraintVisitor();

		String test =  visitor.visit(tree);


		System.out.println(tree.toStringTree(parser)); // print LISP-style tree
	}

}
