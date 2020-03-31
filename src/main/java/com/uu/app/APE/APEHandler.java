package com.uu.app.APE;

import nl.uu.cs.ape.sat.APE;
import nl.uu.cs.ape.sat.core.extrernal.ExternalConstraintBuilder;

import java.io.IOException;

public class APEHandler {

	APE apeFramework;

	public APEHandler (String configPath) throws IOException {
		this.apeFramework = new APE(configPath);

	}

	public void AddConstraint(ExternalConstraintBuilder constraint){
		this.apeFramework.AddExternalConstraint(constraint);
	}

}
