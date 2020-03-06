package com.uu.app;

public class StateData {
	public String name;
	public int stateId;
	public StateType type;

	@Override
	public String toString() {

		String typeString = type == StateType.Module ? "m" : "t";

		return name + "(" + typeString + stateId + ") ";
	}
}
