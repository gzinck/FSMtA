package support;

import java.util.*;

public class StateMap<S extends State> {
	
	private HashMap<String, S> states;
	
	public StateMap() {
		states = new HashMap<String, S>();
	}
	
	public void addState(S state) {
		states.put(state.getStateName(), state);
	}
	
	public S getState(String state) {
		return states.get(state);
	}
	
	public S getState(State state) {
		String name = state.getStateName();
		return states.get(name);
	}
	
	public void removeState(S state) {
		states.remove(state.getStateName());
	}
	
	public void removeState(String stateName) {
		states.remove(stateName);
	}
	
	public void renameState(String oldName, String newName) {
		S state = states.get(oldName);
		state.setStateName(newName);
		states.remove(oldName);
		states.put(newName, state);
	}
	
	/**
	 * Renames all the states in the set of states in the FSM so that
	 * states are named sequentially ("0", "1", "2"...).
	 */
	public void renameStates() {
		int index = 0;
		for(S state : states.values())
			renameState(state.getStateName(), index + "");
	} // renameStates()
	
	public boolean stateExists(String state) {
		return states.containsKey(state);
	}
	
	public Collection<S> getStates() {
		return states.values();
	}
}
