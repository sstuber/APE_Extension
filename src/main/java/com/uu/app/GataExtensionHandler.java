package com.uu.app;

import com.uu.app.APE.APEHandler;
import com.uu.app.APE.ToolAnnotationHandler;
import com.uu.app.GATA.GataParserHandler;
import com.uu.app.SLTL.SLTL;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

public class GataExtensionHandler {

	APEHandler apeHandler = null;

	String basePath;
	String annotationFileName = "gata_annotation.json";
	String gataFileName = "gata_input.gata";


	public GataExtensionHandler(String configPath, String basePath) {
		this.basePath = basePath;

		try {
			apeHandler = new APEHandler(configPath);
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	public Stream<SLTL> getToolAnnotationConstraints() {
		String annotationPath = basePath + annotationFileName;
		ToolAnnotationHandler handler = new ToolAnnotationHandler(annotationPath);

		return handler.GetToolAnnotationConstraints();
	}

	public Stream<SLTL> getInputConstraints() throws IOException {
		File gataInputFile = new File(basePath + gataFileName);

		String inputString = FileUtils.readFileToString(gataInputFile, "UTF-8");


		ParseTree gataTree = GataParserHandler.parse(inputString);


		return null;
	}

	// should get the formula's for the order
	public Stream<SLTL> getDirectInputConstraints() {


		return null;
	}

	// should prevent function that are not in the input to be present
	public Stream<SLTL> getPresentFunctionsConstraints() {
		return null;
	}


}
