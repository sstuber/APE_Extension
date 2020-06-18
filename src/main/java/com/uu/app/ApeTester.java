package com.uu.app;

import com.uu.app.APE.APEHandler;
import com.uu.app.APE.ApeSltlFactory;
import nl.uu.cs.ape.sat.core.implSAT.SATsolutionsList;
import nl.uu.cs.ape.sat.core.solutionStructure.SolutionWorkflow;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ApeTester {

	APEHandler apeExternalConstraints;
	APEHandler apeInternalConstraints;

	public ApeTester() {
	}

	public ApeTester(APEHandler external, APEHandler internal) {
		this.apeExternalConstraints = external;
		this.apeInternalConstraints = internal;
	}

	public static ApeTester GeoExample1() {
		APEHandler internal = null;
		APEHandler external = null;
		String apeConfigPath = "./GeoGMT/E0/ape.configuration";
		String apeExtensionConfigPath = "./GeoGMT/E0/apeExt.configuration";

		try {
			internal = new APEHandler(apeConfigPath);
			external = new APEHandler(apeExtensionConfigPath);
		} catch (Exception e) {
			System.err.println(e);
			return null;
		}

		external.AddConstraint(ApeSltlFactory.IfThenUse("Modules_with_xyz_file_output", "Modules_with_xyz_file_input"));
		external.AddConstraint(ApeSltlFactory.IfThenUse("Modules_with_grid_file_output", "Modules_with_grid_file_input"));
		external.AddConstraint(ApeSltlFactory.IfThenUse("Modules_with_color_palette_output", "Modules_with_color_palette_input"));
		external.AddConstraint(ApeSltlFactory.IfThenNot("Data_generation", "Adding_data"));
		external.AddConstraint(ApeSltlFactory.IfThenNot("Data_processing", "Adding_data"));
		external.AddConstraint(ApeSltlFactory.IfThenNot("Data_processing", "Data_generation"));
		external.AddConstraint(ApeSltlFactory.IfThenNot("Plot_creation", "Adding_data"));
		external.AddConstraint(ApeSltlFactory.IfThenNot("Plot_creation", "Data_generation"));
		external.AddConstraint(ApeSltlFactory.IfThenNot("Plot_creation", "Data_processing"));
		external.AddConstraint(ApeSltlFactory.IfThenNot("Data_presentation", "Adding_data"));
		external.AddConstraint(ApeSltlFactory.IfThenNot("Data_presentation", "Data_generation"));
		external.AddConstraint(ApeSltlFactory.IfThenNot("Data_presentation", "Data_processing"));
		external.AddConstraint(ApeSltlFactory.IfThenNot("Data_presentation", "Plot_creation"));
		external.AddConstraint(ApeSltlFactory.IfThenNot("Layer2", "Layer1"));
		external.AddConstraint(ApeSltlFactory.IfThenNot("Layer3", "Layer1"));
		external.AddConstraint(ApeSltlFactory.IfThenNot("Layer3", "Layer2"));
		external.AddConstraint(ApeSltlFactory.IfThenNot("gs", "gs"));
		external.AddConstraint(ApeSltlFactory.UseBAfterA("initGMT", "Plot_creation"));
		external.AddConstraint(ApeSltlFactory.DoNotUseModule("3D_surfaces"));
		external.AddConstraint(ApeSltlFactory.UseModule("Draw_water"));
		external.AddConstraint(ApeSltlFactory.UseModule("Draw_land"));
		external.AddConstraint(ApeSltlFactory.UseModule("Draw_political_borders"));
		external.AddConstraint(ApeSltlFactory.UseModule("Display_PostScript_files"));
		external.AddConstraint(ApeSltlFactory.UseModule("Draw_lines"));
		external.AddConstraint(ApeSltlFactory.UseModule("Draw_points"));
		external.AddConstraint(ApeSltlFactory.UseModule("Draw_boundary_frame"));
		external.AddConstraint(ApeSltlFactory.UseModule("Write_title"));
		external.AddConstraint(ApeSltlFactory.UseModule("Draw_time_stamp_logo"));

		return new ApeTester(external, internal);
	}

	public static ApeTester basicSimpleDemoTester() {
		APEHandler internal = null;
		APEHandler external = null;
		String apeConfigPath = "./SimpleDemo/ape.configuration";
		String apeExtensionConfigPath = "./SimpleDemo/apeExt.configuration";

		try {
			internal = new APEHandler(apeConfigPath);
			external = new APEHandler(apeExtensionConfigPath);
		} catch (Exception e) {
			System.err.println(e);
			return null;
		}

		external.AddConstraint(ApeSltlFactory.IfThenNot("Points_and_lines", "Basemaps"));
		external.AddConstraint(ApeSltlFactory.UseBAfterA("initGMT", "Plot_creation"));
		external.AddConstraint(ApeSltlFactory.UseModule("Draw_water"));
		external.AddConstraint(ApeSltlFactory.UseModule("Draw_land"));
		external.AddConstraint(ApeSltlFactory.UseModule("Draw_political_borders"));
		external.AddConstraint(ApeSltlFactory.UseModule("Points_and_lines"));


		return new ApeTester(external, internal);
	}

	public boolean test() {
		SATsolutionsList internal = this.apeInternalConstraints.RunSynthesisWithResults();
		SATsolutionsList external = this.apeExternalConstraints.RunSynthesisWithResults();

		HashMap<Integer, Integer> internalCounts = countSollutions(internal);
		HashMap<Integer, Integer> externalCounts = countSollutions(external);

		return internalCounts.equals(externalCounts);
	}

	public HashMap<Integer, Integer> countSollutions(SATsolutionsList solutions) {
		HashMap<Integer, Integer> result = new HashMap<>();

		solutions.getStream()
			.map(SolutionWorkflow::getSolutionlength)
			.forEach(length -> {
				Integer lengthCount = result.getOrDefault(length, 0);
				result.put(length, lengthCount + 1);
			});

		return result;
	}

	public StringBuilder metricEvaluationAPE(String basepath, boolean isGata) {
		StringBuilder result = new StringBuilder(basepath);

		APEHandler handler = null;
		try {
			if (isGata)
				handler = APEHandler.GataApeHandler(basepath);
			else
				handler = new APEHandler(basepath + "apeExt.configuration");
		} catch (Exception e) {
			System.err.println("error in reading " + basepath);
		}

		long time1 = System.currentTimeMillis();
		SATsolutionsList solutionsList = handler.RunSynthesisWithResults();
		float synthesisRunTime = (System.currentTimeMillis() - time1) / 1000f;
		result.append(",").append(synthesisRunTime);
		System.out.printf("total run time was: %f", synthesisRunTime);

		result.append(",").append(solutionsList.size());
		int totalSolutionsBeforeFilter = solutionsList.size();


		float time2 = 0;
		if (isGata) {
			long tmp = System.currentTimeMillis();
			solutionsList = handler.FilterSolutions();
			time2 = (System.currentTimeMillis() - tmp) / 1000f;
		}
		result.append(",").append(time2);

		result.append(printSolutionsCount(solutionsList, 9));

		return result.append("\n");
	}

	public StringBuilder printSolutionsCount(SATsolutionsList list, int maxLength) {
		Map<Integer, Integer> counts = countSollutions(list);
		StringBuilder result = new StringBuilder();

		for (int i = 1; i <= maxLength; i++)
			result.append(",").append(counts.getOrDefault(i, 0));

		return result;
	}

	public void RunAllTests() {
		String path = "./APE_Examples/PeoplePerRegionUtrecht/";


		StringBuilder csvFile = new StringBuilder("name,runtime,TotalSolutions,filtertime");

		for (int i = 1; i <= 9; i++)
			csvFile.append(",amount").append(i);

		csvFile.append("\n");

		csvFile.append(metricEvaluationAPE(path, false));

		csvFile.append("testComplete");

		try {


			String csvPath = ".\\test_results\\results.csv";
			File file = new File(csvPath);

			if (!file.exists())
				file.createNewFile();

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

			bw.write(csvFile.toString());
			bw.close();

		} catch (Exception e) {
			System.out.println(e);
		}
	}


}
