package support;

import java.util.*;

/**
 * Wrapper for a HashMap allowing the user to search for an state object using the corresponding name.
 * It also holds nice functions for working with states.
 * 
 * This class is a part of the support package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 * @param <S> Class that extends State.
 */

public class StateMap<S extends State> {

//--- Instance Variables   --------------------------------------------------------------------
	
	/** HashMap<String, <S extends State>> object that maps the String object names of States to their State objects. */
	private HashMap<String, S> states;
	
//---  Constructors   -------------------------------------------------------------------------
	
	/**
	 * Constructor for a StateMap object that initializes the state HashMap<String, <S extends State>> object.
	 */
	
	public StateMap() {
		states = new HashMap<String, S>();
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	/**
	 * This method renames a state from its former String oldName to a provided String newName.
	 * 
	 * @param oldName - String object representing the State's former name.
	 * @param newName - String object representing the State's new name.
	 * @return - Returns true if the state was successfully renamed; false otherwise.
	 */
	
	public boolean renameState(String oldName, String newName) {
		if(oldName == null || newName == null)
			return false;
		S state = states.get(oldName);
		if(state == null) 
			return false; 
		state.setStateName(newName);
		states.remove(oldName);
		states.put(newName, state);
		return true;
	}
	
	/**
	 * This method renames all the states in the set of states in the FSM so that states are named sequentially ("0", "1", "2"...).
	 */
	
	public void renameStates() {
		int index = 0;
		for(S state : states.values())
			renameState(state.getStateName(), index + "");
	}
	

	/**
	 * Makes a String object which has the dot representation of the states, which can be used for sending an FSM to GraphViz.
	 * 
	 * @return - Returns a String object containing the dot representation of the states.
	 */
	
	public String makeDotString() {
		StringBuilder sb = new StringBuilder();
		for(S state : states.values()) {
			sb.append(state.makeDotString());
		}
		return sb.toString();
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	/**
	 * Getter method that requests a State from the map using a provided String.
	 * 
	 * @param stateName - Name corresponding to a State.
	 * @return - Returns the State object corresponding to the provided String object.
	 */
	
	public S getState(String stateName) {
		return states.get(stateName);
	}

	/**
	 * Gets a State in the current FSM using a State from another FSM's String stateName.
	 * 
	 * @param state - State object that's String-form name is used for identification in this StateMap object.
	 * @return - Returns a State object from this StateMap object, representing what its HashMap<String, S> had stored at that position.
	 */
	
	public S getState(S state) {
		String stateName = state.getStateName();
		return states.get(stateName);
	}
	
	/**
	 * Getter method that requests the status of whether or not an entry for a given State's name exists.
	 * 
	 * @param stateName - String object representing the State's name.
	 * @return - Returns a boolean value; true if the String object has an entry, false otherwise.
	 */
	
	public boolean stateExists(String stateName) {
		return states.get(stateName) != null;
	}
	
	/**
	 * Getter method that requests the Collection of State objects that the HashMap<String, <S extends State>> object is storing.
	 * 
	 * @return - Returns the Collection of States that are stored within the HashMap<String, <S extends State>> object.
	 */
	
	public Collection<S> getStates() {
		return states.values();
	}

//---  Setter Methods   -----------------------------------------------------------------------

	
//---  Manipulations   ------------------------------------------------------------------------
	
	/**
	 * This method adds a State to the HashMap<String, <S extends State>> mapping.
	 * 
	 * @param state - State object to add to the HashMap<String, <S extends State>>.
	 */
	
	public void addState(S state) {
		states.put(state.getStateName(), state);
	}
	
	/**
	 * This method removes a State from the HashMap<String, <S extends State>> mapping.
	 * 
	 * @param state - State object to remove from the HashMap<String, <S extends State>> mapping.
	 */
	
	public void removeState(S state) {
		states.remove(state.getStateName());
	}
	
	/**
	 * This method removes a State from the HashMap<String, <S extends State>> using its corresponding String name.
	 * 
	 * @param stateName - String object representing the name of the state to remove.
	 */
	
	public void removeState(String stateName) {
		states.remove(stateName);
	}
	
}
