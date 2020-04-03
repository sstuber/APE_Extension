package com.uu.app.APE;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class ToolAnnotationHandler {

	String path = "./gata_annotation.json";
	String mainKey = "annotations";
	String toolKey = "tool";
	String gataAnnotation = "functions";

	public ArrayList<ToolAnnotation> ReadAnnotationJson() {
		try {
			String content = FileUtils.readFileToString(new File(path), "utf-8");
			JSONObject jsonObject = new JSONObject(content);

			JSONArray annotations = jsonObject.getJSONArray(mainKey);

			ArrayList<ToolAnnotation> annotationList = annotations.toList().stream()
				.map(this::collectAnnotation)
				.collect(Collectors
					.toCollection(ArrayList::new));

			return annotationList;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}


	private ToolAnnotation collectAnnotation(Object test) {
		ToolAnnotation result = new ToolAnnotation();

		result.name = ((HashMap<String, String>) test).get(toolKey);
		result.gataAnnotation = ((HashMap<String, String>) test).get(gataAnnotation);
		return result;
	}
}
