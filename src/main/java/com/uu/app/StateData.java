package com.uu.app;

public class StateData {
	public String name;
	public int stateId;

	@Override
	public String toString() {
		return name + "(s" + stateId + ") ";
	}
}
