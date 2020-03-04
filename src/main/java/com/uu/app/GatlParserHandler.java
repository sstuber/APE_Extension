package com.uu.app;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class GatlParserHandler {


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
