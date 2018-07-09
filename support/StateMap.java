package support;

import java.util.*;

/**
 * Wrapper for a HashMap allowing the user to search for an state object using the corresponding name.
 * It also holds nice functions for working with states.
 * 
 * This class is a part of the support package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public class StateMap<S extends State> {

//--- Instance Variables   --------------------------------------------------------------------
	
	/** HashMap<String, <S extends State>> object that maps the String object names of States to their State objects. */
	private HashMap<String, S> states;
	/** Holds the precise class of the generic State class. */
	private Class<S> stateClass;
	
//---  Constructors   -------------------------------------------------------------------------
	
	/**
	 * Constructor for a StateMap object that initializes the state HashMap<String, <S extends State>> object.
	 * 
	 * @param inClass - The class of State the map will hold, used for instantiation.
	 */
	
	public StateMap(Class<S> inClass) {
		states = new HashMap<String, S>();
		stateClass = inClass;
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	/**
	 * This method renames a state from its former String oldName to a provided String newName.
	 * 
	 * @param state State which needs to be renamed.
	 * @param newName String object representing the State's new name.
	 * @return Returns true if the state was successfully renamed; false otherwise.
	 */
	
	public boolean renameState(S state, String newName) {
		String oldName = state.getStateName();
		if(state == null || newName == null)
			return false;
		state.setStateName(newName);
		if(states.get(oldName) == state) states.remove(oldName); // only remove the mapping if it references the right object
		states.put(newName, state);
		return true;
	}
	
	/**
	 * This method renames all the states in the set of states in the FSM so that states are named sequentially ("0", "1", "2"...).
	 */
	
	public void renameStates() {
		State[] stateArr = new State[states.size()];
		states.values().toArray(stateArr);
		for(int i = 0; i < states.size(); i++)
			renameState((S)stateArr[i], i + "");
	}
	
	/**
	 * Makes a String object which has the dot representation of the states, which can be used for sending an FSM to GraphViz.
	 * 
	 * @return - Returns a String object containing the dot representation of the states.
	 */
	
	public String makeDotString() {
		StringBuilder sb = new StringBuilder();
		for(State state : states.values()) {
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
	 * @return - Returns a State object from this StateMap object, representing what its HashMap<String, <S extends State>> had stored at that position.
	 */
	
	public S getState(State state) {
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
	
	/**
	 * Gets the class type of the States stored in the StateMap
	 * 
	 * @return The class type of the States.
	 */
	
	public Class<S> getStateClassType(){
		return stateClass;
	}

//---  Setter Methods   -----------------------------------------------------------------------

	/**
	 * Setter method that assigns the provided HashMap<<r>String, S> object to this object's corresponding instance variable.
	 * 
	 * @param inHash - HashMap<<r>String, S> object that represents a matched set of Strings leading to State objects.
	 */
	
	public void setStateMapStates(HashMap<String, S> inHash) {
		states = inHash;
	}
	
	/**
	 * Setter method that assigns a new Class<<r>S> object to this object's corresponding instance variable.
	 * 
	 * @param inClass - Class<<r>S> object that represents a Class type corresponding to the type of State stored by this StateMap.
	 */
	
	public void setStateMapClass(Class<S> inClass) {
		stateClass = inClass;
	}
	
//---  Manipulations   ------------------------------------------------------------------------
	
	/**
	 * This method adds a copy of the parameter state to the HashMap<String, <S extends State>> mapping.
	 * If a state with the same id already exists, nothing is changed and the corresponding
	 * pre-existing State object is returned.
	 * 
	 * @param state - State object to add to the HashMap<String, <S extends State>>.
	 * @return State object representing the object added to the mapping (or the one that
	 * already existed in the mapping).
	 */
	
	public S addState(State state) {
		try {
			String stateName = state.getStateName();
			if(states.containsKey(stateName))
				return states.get(stateName);
			S newState = stateClass.newInstance();
			newState.copyDataFrom(state);
			states.put(stateName, newState);
			return newState;
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * This method adds a copy of the parameter state to the HashMap<String, <S extends State>>
	 * mapping, but with an id with a prefix attached.
	 * If a state with the same id (with the prefix) already exists, nothing is changed and the
	 * corresponding pre-existing State object is returned.
	 * 
	 * @param state - State object to add to the HashMap<String, <S extends State>>.
	 * @return State object representing the object added to the mapping (or the one that
	 * already existed in the mapping).
	 */
	
	public S addState(State state, String prefix) {
		String stateName = prefix + state.getStateName();
		if(states.containsKey(stateName))
			return states.get(stateName);
		S newState = state.copy();
		newState.setStateName(stateName);
		states.put(stateName, newState);
		return newState;
	}
	
	/**
	 * Adds a state to the mapping that is a hybrid of the two input states, combining
	 * their names 
	 * @param state1 The first State from which to adopt attributes and make a new state.
	 * @param state2 The second State from which to adopt attributes and make a new state.
	 * @return The State object which is presently mapped.
	 */
	
	public S addState(S state1, State state2) {
		String stateName = "(" + state1.getStateName() + "," + state2.getStateName() + ")";
		if(states.containsKey(stateName))
			return states.get(stateName);
		S newState = state1.makeStateWith(state2); // TODO: fix the generic types here
		states.put(newState.getStateName(), newState);
		if(state1.getStateInitial() && state2.getStateInitial())
			newState.setStateInitial(true);
		return newState;
	}
	
	/**
	 * This method adds a new State with the given name to the HashMap<String, State> mapping.
	 * If a state with the same id already exists, nothing is changed and the corresponding
	 * pre-existing State object is returned.
	 * 
	 * @param stateName String representing the State's name.
	 * @return State object representing the object added to the mapping (or the one that
	 * already existed in the mapping).
	 */
	
	public S addState(String stateName) {
		if(states.containsKey(stateName))
			return states.get(stateName);
		try {
			S newState = stateClass.newInstance();
			newState.setStateName(stateName);
			states.put(stateName, newState);
			return newState;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public State addState(State ... states) {
		State st = new State(states);
		return st;
	}
	
	/**
	 * This method removes a State from the HashMap<String, State> mapping.
	 * 
	 * @param state - State object to remove from the HashMap<String, State> mapping.
	 */
	
	public void removeState(State state) {
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
	
	/**
	 * Removes all States in the parameter ArrayList from the StateMap.
	 * 
	 * @param inStates ArrayList of State objects to remove from the StateMap.
	 */
	public void removeStates(ArrayList<S> inStates) {
		for(State s : inStates) {
			states.remove(s.getStateName());
		}
	}
}
