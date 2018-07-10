package support;

import java.util.*;

/**
 * This class is a wrapper for a HashMap, allowing the user to search for a State object using its corresponding name.
 * It also permits convenient interfacing with State objects.
 * 
 * This class is a part of the support package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public class StateMap<S extends State> {

//--- Instance Variables   --------------------------------------------------------------------
	
	/** HashMap<<r>String, <<r>S extends State>> object that maps the String object names of States to their State objects.*/
	private HashMap<String, S> states;
	/** HashMap<<r>S, ArrayList<<r>S>> object that maps a State extending object to a list of State extending objects which compose it.*/
	private HashMap<S, ArrayList<S>> composition;
	/** Holds the precise class of the generic State class describing the generic variable S. */
	private Class<S> stateClass;
	
//---  Constructors   -------------------------------------------------------------------------
	
	/**
	 * Constructor for a StateMap object that initializes the state HashMap<<r>String, <<r>S extends State>> object.
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
	 * @param state - State object that needs to be renamed.
	 * @param newName - String object representing the State's new name.
	 * @return - Returns a boolean value; true if the State was successfully renamed, false otherwise.
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
	 * 
	 * TODO: Could there be incidental overwrite?
	 */
	
	public void renameStates() {
		State[] stateArr = new State[states.size()];
		states.values().toArray(stateArr);
		for(int i = 0; i < states.size(); i++)
			renameState((S)stateArr[i], i + "");
		composition = null;
	}
	
	/**
	 * Makes a String object which has the dot representation of the states, which can be used for sending an FSM to GraphViz.
	 * 
	 * @return - Returns a String object containing the dot representation of the State Map's States.
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
	 * Getter method that returns a list of States which compose the provided State. (Through operations
	 * such as Determinization or generating the Observer View.) In the event that the provided State is
	 * not found, but there is a mapping of some States to a list of States, then return a single-entry
	 * list containing the querying State. If there is no mapping, return null. 
	 * 
	 * @param provided - State extending object provided to request a specific State's set of composing States.
	 * @return - Returns an ArrayList<<r>S> representing all the States that are designated as composing the provided State.
	 */
	
	public ArrayList<S> getStateComposition(S provided){
		if(composition == null || composition.get(provided) == null) {
			ArrayList<S> out = new ArrayList<S>();
			out.add(provided);
			return out;
		}
		else
			return composition.get(provided);
	}
	
	/**
	 * Getter method that returns a HashMap<<r>S, ArrayList<<r>S>> representing the full set of States
	 * and the list of States which compose each one after operations that aggregate States together.
	 * 
	 * @return - Returns a HashMap<<r>S, ArrayList<<r>S>> object holding paired States and lists of composing States.
	 */
	
	public HashMap<S, ArrayList<S>> getComposedStates(){
		return composition;
	}
	
	/**
	 * Getter method that requests a State from the map using the provided String.
	 * 
	 * @param stateName - String object representing a name corresponding to a State.
	 * @return - Returns the State object corresponding to the provided String object.
	 */
	
	public S getState(String stateName) {
		return states.get(stateName);
	}

	/**
	 * Gets a State in the current FSM using a State from another FSM's String stateName.
	 * 
	 * @param state - State object that's String-form name is used for identification in this StateMap object.
	 * @return - Returns a State object from this StateMap object, representing what its HashMap<<r>String, <<r>S extends State>> had stored at that position.
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
	 * Getter method that requests the Collection<<r>S> of State objects that the HashMap<<r>String, <<r>S extends State>> object is storing.
	 * 
	 * @return - Returns the Collection<<r>S> of States that are stored within the HashMap<<r>String, <<r>S extends State>> object.
	 */
	
	public Collection<S> getStates() {
		return states.values();
	}
	
	/**
	 * Gets the class type of the States stored in the StateMap
	 * 
	 * @return - Returns the Class<<r>S> type of the States.
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
	
	/**
	 * Setter method that assigns a new set of State extending objects to individual lists of State extending objects
	 * which represent the States that have been aggregated to compose the key State in <<r>State, ArrayList<<r>S>> pairs.
	 * 
	 * @param newComposed - HashMap<S, ArrayList<S>> object representing the new set of States and their composing States.
	 */
	
	public void setCompositionStates(HashMap<S, ArrayList<S>> newComposed) {
		composition = newComposed;
	}
	
	/**
	 * Setter method that assigns a list of State extending objects to be designated as the States which have
	 * composed the provided State in some operation that aggregated the values in that list to produce the
	 * singular State. 
	 * 
	 * @param keyState - State extending object whose entry in the set of States and their composing States will be adjusted.
	 * @param composition - ArrayList<<r>S> object containing the State extending objects which compose the designated State extending object.
	 */
	
	public void setStateComposition(S keyState, ArrayList<S> composedStates) {
		if(composition == null)
			composition = new HashMap<S, ArrayList<S>>();
		HashSet<S> hash = new HashSet<S>(composedStates);
		composedStates.clear();
		composedStates.addAll(hash);
		Collections.sort(composedStates);
		composition.put(keyState, composedStates);
	}
	
//---  Manipulations - Adding   ---------------------------------------------------------------
	
	/**
	 * This method adds a copy of the parameter state to the HashMap<<r>String, <<r>S extends State>> mapping.
	 * If a state with the same id already exists, nothing is changed and the corresponding pre-existing State
	 * object is returned.
	 * 
	 * @param state - State object to add to the State Map's HashMap<<r>String, <<r>S extends State>>.
	 * @return - Returns a State-extending object representing the object added to the mapping (or the one that
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
	 * This method adds a copy of the parameter state to the HashMap<<r>String, <<r>S extends State>>
	 * mapping, but with an id with a prefix attached.
	 * If a state with the same id (with the prefix) already exists, nothing is changed and the
	 * corresponding pre-existing State object is returned.
	 * 
	 * @param state - State object to add to the State Map's HashMap<<r>String, <<r>S extends State>>.
	 * @return - Returns a State object representing the object added to the mapping (or the one that already existed in the mapping).
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
	 * Adds a state to the mapping that is a hybrid of the two input states, combining their names. 
	 * 
	 * @param state1 - The first State object from which to adopt attributes and make a new state.
	 * @param state2 - The second State object from which to adopt attributes and make a new state.
	 * @return - Returns the State object that has been added to the State Map as the concatenation of the two provided States.
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
	 * @param stateName - String object representing the State's name.
	 * @return - Returns a State object representing the object added to the mapping (or the one that already existed in the mapping).
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
	
	/**
	 * This method permits the addition of a new State to the State Map's HashMap<<r>String, State>> as a product
	 * of numerous provided States, using the corresponding State constructor that compares the attributes of
	 * each provided State to decide on the aggregate State's attributes.
	 * 
	 * @param states - State ... object (varargs) provided as a series of States used to create a new State object.
	 * @return - Returns a State object representing the newly generated State object.
	 */
	
	public State addState(State ... states) {
		State st = new State(states);
		return st;
	}
	
	/**
	 * This method removes a State from the HashMap<<r>String, State> mapping.
	 * 
	 * @param state - State object to remove from the HashMap<<r>String, State> mapping.
	 */
	
//---  Manipulations - Removing   -------------------------------------------------------------
	
	public void removeState(State state) {
		states.remove(state.getStateName());
	}
	
	/**
	 * This method removes a State from the HashMap<<r>String, <<r>S extends State>> using its corresponding String name.
	 * 
	 * @param stateName - String object representing the name of the state to remove.
	 */
	
	public void removeState(String stateName) {
		states.remove(stateName);
	}
	
	/**
	 * Removes all States in the parameter ArrayList<<r>S> from the StateMap.
	 * 
	 * @param inStates - ArrayList<<r>S> object of State objects to remove from the StateMap.
	 */
	
	public void removeStates(ArrayList<S> inStates) {
		for(State s : inStates) {
			states.remove(s.getStateName());
		}
	}
}
