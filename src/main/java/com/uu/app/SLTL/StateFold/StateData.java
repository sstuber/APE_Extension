package com.uu.app.SLTL.StateFold;

public class StateData {
	public String name;
	public int stateId;
	public StateType type = StateType.Module;

	@Override
	public String toString() {

		String typeString = type == StateType.Module ? "m" : "t";

		return name + "(" + typeString + stateId + ") ";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		StateData stateData = (StateData) o;

		if (stateId != stateData.stateId) return false;
		if (name != null ? !name.equals(stateData.name) : stateData.name != null) return false;
		return type == stateData.type;
	}

	@Override
	public int hashCode() {
		int result = name != null ? name.hashCode() : 0;
		result = 31 * result + stateId;
		result = 31 * result + (type != null ? type.hashCode() : 0);
		return result;
	}
}
