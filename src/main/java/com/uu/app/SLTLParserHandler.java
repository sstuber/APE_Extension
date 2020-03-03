package com.uu.app;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class SLTLParserHandler {

	public SLTLParserHandler() {

	}

	public void ParseAndPrint(String input) {
		ANTLRInputStream inputStream = new ANTLRInputStream(input);

		// create a lexer that feeds off of inputStream CharStream
		SLTLLexer lexer = new SLTLLexer(inputStream);

		// create a buffer of tokens pulled from the lexer
		CommonTokenStream tokens = new CommonTokenStream(lexer);

		// create a parser that feeds off the tokens buffer
		SLTLParser parser = new SLTLParser(tokens);

		ParseTree tree = parser.sltl(); // begin parsing at init rule

		System.out.println(tree.toStringTree(parser)); // print LISP-style tree
	}

}
