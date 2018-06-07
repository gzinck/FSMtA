package support;

import java.util.*;

/**
 * Wrapper for a HashMap allowing the user to search for an state object using the corresponding name.
 * It also holds nice functions for working with states.
 * 
 * This class is a part of the support package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 *
 * @param <S> Class that extends State.
 */

public class StateMap<S extends State> {

//--- Instance Variables   --------------------------------------------------------------------
	
	/** HashMap mapping String names of states to their State objects. */
	private HashMap<String, S> states;
	
//---  Constructors   -------------------------------------------------------------------------
	
	/**
	 * Creates a new HashMap for storing States.
	 */
	
	public StateMap() {
		states = new HashMap<String, S>();
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	/**
	 * Renames a state from its former String oldName to some String
	 * newName.
	 * 
	 * @param oldName String representing the state's former name.
	 * @param newName String representing the state's new name.
	 * @return True if the state was successfully renamed; false otherwise.
	 */
	
	public boolean renameState(String oldName, String newName) {
		if(oldName == null || newName == null) return false;
		S state = states.get(oldName);
		if(state == null) return false; 
		state.setStateName(newName);
		states.remove(oldName);
		states.put(newName, state);
		return true;
	}
	
	/**
	 * Renames all the states in the set of states in the FSM so that
	 * states are named sequentially ("0", "1", "2"...).
	 */
	
	public void renameStates() {
		int index = 0;
		for(S state : states.values())
			renameState(state.getStateName(), index + "");
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	/**
	 * Gets a State from the map using its String stateName.
	 * 
	 * @param stateName Name corresponding to a State.
	 * @return The corresponding State object.
	 */
	
	public S getState(String stateName) {
		return states.get(stateName);
	}

	/**
	 * Gets a State in the current FSM using a State from another
	 * FSM's String stateName.
	 * 
	 * @param state State object, not necessarily from the same FSM.
	 * @return State object from the current FSM.
	 */
	
	public S getState(S state) {
		String stateName = state.getStateName();
		return states.get(stateName);
	}
	
	/**
	 * Checks if a State exists using its corresponding stateName.
	 * 
	 * @param stateName String representing the state's name.
	 * @return True if the state exists, false otherwise.
	 */
	
	public boolean stateExists(String stateName) {
		return states.containsKey(stateName);
	}
	
	/**
	 * Gets a Collection with all the States currently stored.
	 * 
	 * @return Collection of States that are currently stored.
	 */
	
	public Collection<S> getStates() {
		return states.values();
	}

//---  Setter Methods   -----------------------------------------------------------------------
	
	/**
	 * Adds a State to the mapping.
	 * 
	 * @param state State object to add.
	 */
	
	public void addState(S state) {
		states.put(state.getStateName(), state);
	}
	
//---  Manipulations   ------------------------------------------------------------------------
	
	/**
	 * Removes a State from the HashMap.
	 * 
	 * @param state State to remove.
	 */
	
	public void removeState(S state) {
		states.remove(state.getStateName());
	}
	
	/**
	 * Removes a State from the HashMap using its
	 * corresponding String name.
	 * 
	 * @param stateName Name of the state to remove.
	 */
	
	public void removeState(String stateName) {
		states.remove(stateName);
	}

}
