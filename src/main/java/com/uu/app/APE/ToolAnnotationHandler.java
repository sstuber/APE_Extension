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
import java.util.stream.Stream;

public class ToolAnnotationHandler {

	String path = "./gata_annotation.json";
	String mainKey = "annotations";
	String toolKey = "tool";
	String gataAnnotation = "functions";

	public ToolAnnotationHandler() {
	}

	public ToolAnnotationHandler(String toolAnnotationPath) {
		this.path = toolAnnotationPath;
	}

	public Stream<SLTL> GetToolAnnotationConstraints() {
		return getAnnotationStreamFromJson()
			.map(this::transformAnnotation);
	}

	public Stream<ToolAnnotation> getAnnotationStreamFromJson() {
		try {
			String content = FileUtils.readFileToString(new File(path), "utf-8");
			JSONObject jsonObject = new JSONObject(content);

			JSONArray annotations = jsonObject.getJSONArray(mainKey);

			Stream<ToolAnnotation> annotationList = annotations.toList().stream()
				.map(this::collectAnnotation);

			return annotationList;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	// G ( tool -> fn1 and fn2)
	// G ( ! tool or (fn1 and fn2))
	private SLTL transformAnnotation(ToolAnnotation annotation) {
		SLTLBuilder toolside = new SLTLBuilder()
			.addNext(annotation.name)
			.addUnary(UnarySLTLOp.Neg);

		SLTLBuilder functionSide = GataParserHandler.ParseGataToolAnnotation(annotation.gataAnnotation);

		return toolside
			.AddBinaryRight(functionSide, BinarySLTLOp.Or)
			.getResult();
	}

	private ToolAnnotation collectAnnotation(Object test) {
		ToolAnnotation result = new ToolAnnotation();

		result.name = ((HashMap<String, String>) test).get(toolKey);
		result.gataAnnotation = ((HashMap<String, String>) test).get(gataAnnotation);
		return result;
	}
}
