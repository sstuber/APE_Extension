package com.uu.app;

import com.uu.app.APE.APEHandler;
import com.uu.app.APE.ApeSltlFactory;
import nl.uu.cs.ape.sat.core.implSAT.SATsolutionsList;
import nl.uu.cs.ape.sat.core.solutionStructure.SolutionWorkflow;

import java.util.HashMap;
import java.util.Map;

public class ApeTester {

	APEHandler apeExternalConstraints;
	APEHandler apeInternalConstraints;

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


}
