package com.uu.app.APE;

import com.uu.app.GATA.GataParserHandler;
import com.uu.app.SLTL.BinarySLTLOp;
import com.uu.app.SLTL.SLTL;
import com.uu.app.SLTL.SLTLBuilder;
import com.uu.app.SLTL.UnarySLTLOp;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.Optional;

public class ToolAnnotationHandler {

	String path = "./gata_annotation.json";
	String mainKey = "annotations";
	String toolKey = "tool";
	String gataAnnotation = "functions";

	HashMap<String, ArrayList<String>> functionToolListMap;

	public ToolAnnotationHandler() {
		functionToolListMap = new HashMap<>();
	}

	public ToolAnnotationHandler(String toolAnnotationPath) {
		functionToolListMap = new HashMap<>();
		this.path = toolAnnotationPath;
	}

	public Stream<SLTL> GetToolAnnotationConstraints() {
		return getAnnotationStreamFromJson()
			.map(this::transformAnnotation);
	}

	public Stream<ToolAnnotationStruct> getAnnotationStreamFromJson() {
		try {
			String content = FileUtils.readFileToString(new File(path), "utf-8");
			JSONObject jsonObject = new JSONObject(content);

			JSONArray annotations = jsonObject.getJSONArray(mainKey);

			Stream<ToolAnnotationStruct> annotationList = annotations.toList().stream()
				.map(obj -> (HashMap<String, String>) obj)
				.map(this::collectAnnotation);

			return annotationList;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	// G ( tool -> fn1 and fn2)
	// G ( ! tool or (fn1 and fn2))
	private SLTL transformAnnotation(ToolAnnotationStruct annotation) {
		ArrayList<String> functionsInAnnotation = GataParserHandler.ParseGataToolAnnotation(annotation.gataAnnotation);

		for (String functionName : functionsInAnnotation) {
			ArrayList<String> test = functionToolListMap.getOrDefault(functionName, new ArrayList<>());

			test.add(annotation.name);
			functionToolListMap.put(functionName, test);
		}

		return buildToolUsedSltl(annotation.name, functionsInAnnotation);
	}

	private SLTL buildToolUsedSltl(String toolName, ArrayList<String> functionNames) {
		SLTLBuilder toolside = new SLTLBuilder()
			.addNext(toolName)
			.addUnary(UnarySLTLOp.Neg);

		SLTLBuilder functionSide = functionNames.stream()
			.map(name -> new SLTLBuilder().addNext(name))
			.reduce((acc, test) -> acc.addBinaryRight(test, BinarySLTLOp.And))
			.orElse(null);

		if (functionSide == null) {
			System.err.println(String.format("Error in annotation at %s", toolName));
			return null;
		}

		return toolside
			.addBinaryRight(functionSide, BinarySLTLOp.Or)
			.getResult();
	}

	private ToolAnnotationStruct collectAnnotation(HashMap<String, String> jsonMap) {
		ToolAnnotationStruct result = new ToolAnnotationStruct();

		result.name = jsonMap.get(toolKey);
		result.gataAnnotation = jsonMap.get(gataAnnotation);
		return result;
	}
}
