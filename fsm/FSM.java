package fsm;

import java.util.*;
import support.*;
import support.transition.Transition;

/**
 * This class models a Finite State Machine with some of the essential elements.
 * It must be extended to be used (eg. by NonDeterministic or Deterministic to
 * determine how transitions and initial states are handled).
 * 
 * It is part of the fsm package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 *
 */
public abstract class FSM {
	
//--- Constant Values  -------------------------------------------------------------------------
	
	/** String constant designating this object as a specific type of FSM for clarification purposes*/
	public static final String FSM_TYPE = "FSM";
	/** String constant designating the file extension to append to the file name when writing to the system*/
	public static final String FSM_EXTENSION = ".fsm";
	
//--- Instance Variables  ----------------------------------------------------------------------
	
	/** HashMap<String, State> mapping state names to state objects, which all contain attributes
	 * of the given state. */
	protected StateMap<State> states;
	/** TransitionFunction mapping states to sets of transitions (which contain the state names). */
	protected TransitionFunction<Transition> transitions;
	/** String object possessing the identification for this FSM object. */
	protected String id;
	
//--- Single-FSM Operations  ------------------------------------------------------------------------------

	/**
	 * Renames all the states in the set of states in the FSM so that
	 * states are named sequentially ("0", "1", "2"...).
	 */
	public void renameStates() {
		states.renameStates();
	} // renameStates()
	
	/**
	 * Searches through the graph represented by the transitions hashmap, and removes
	 * disjoint elements.
	 * 
	 * Algorithm starts from all initial States, and adds them to a queue. They are
	 * then placed into the new transitions HashMap, and all States reachable by these
	 * initial states are placed in a queue for processing. Once dealt with, a State is
	 * added to a HashSet to keep track of what has already been seen.
	 * 
	 * @return - Returns an FSM object representing the accessible version of the current
	 * FSM. 
	 */
	public abstract FSM makeAccessible();
	
	/**
	 * Searches through the graph represented by the transitions hashmap, and removes any
	 * states that cannot reach a marked state.
	 * 
	 * Every state is added to a queue to search through its neighbors via a recursive
	 * depth-first search. In this search, once a Marked State is found, all States
	 * along that path are considered 'in' the new FSM which will be returned. Otherwise,
	 * those states are left out.
	 * 
	 * @return - An FSM representing the CoAccessible version of the original FSM.
	 */
	public abstract FSM makeCoAccessible();
	
	/**
	 * This method performs a trim operation on the calling FSM (performing the
	 * makeAccessible() and makeCoAccessible() methods) to make sure only states
	 * that are reachable from initial states and can reach marked states are
	 * included.
	 * 
	 * @return - An FSM representing the trimmed version of the calling FSM.
	 */
	public abstract FSM trim();
	
	/**
	 * Formerly createFileFormat(), toTextFile(String, String) converts an
	 * FSM into a text file which can be read back in and used to recreate
	 * an FSM later.
	 */
	public abstract void toTextFile(String filePath, String name);
	
//--- Multi-FSM Operations  ------------------------------------------------------------------------------
	
	/**
	 * Performs a union operation on two FSMs and returns the result.
	 * 
	 * @param other - The FSM to add to the current FSM in order to create a unioned
	 * FSM.
	 * @return - The result of the union.
	 */
	public abstract FSM union(FSM other);
	
	/**
	 * Performs a product or intersection operation on two FSMs and returns the result.
	 * @param other - The FSM to perform the product operation on with the current FSM.
	 * @return - The resulting FSM from the product operation.
	 */
	public abstract FSM product(FSM other);
	
//--- Getter/Setter Methods  --------------------------------------------------------------------------
	
	/**
	 * Adds a new state to the FSM using a String object.
	 * 
	 * @param state - String representing the state to add.
	 * @return - True if the state was successfully added, false
	 * if the state already existed.
	 */
	public abstract boolean addState(String newState);
	
	/**
	 * Adds a new state to the FSM using a State object.
	 * 
	 * @param state - State object to add.
	 * @return - True if the state was successfully added, false
	 * if the state already existed.
	 */
	public abstract boolean addState(State newState);
	
	/**
	 * Removes a state from the FSM, unless it is the initial state.
	 * 
	 * @param state - String value representing the State to remove from the FSM.
	 * @return - Returns a boolean value representing the outcome of the operation:
	 * true if the state was removed, false if the state was an initial state and
	 * could not be removed or if the state did not exist.
	 */
	public abstract boolean removeState(String state);
	
	/**
	 * Returns if a state exists in the FSM.
	 * 
	 * @param state - String representing the state to check for existence.
	 * @return - True if the state exists in the FSM, false otherwise.
	 */
	public abstract boolean stateExists(String state);
	
	/**
	 * Toggles a state's marked property.
	 * 
	 * @param state - String representing the name of the state.
	 * @return - True if the state is now marked, false if the state is
	 * now unmarked (or if the state does not exist).
	 */
	public abstract boolean toggleMarkedState(String state);
	
	/**
	 * Adds the parameter state as an initial state of the FSM.
	 * Behavior depends on if the FSM is deterministic or non-deterministic.
	 * 
	 * @param newState - String for the state name to be added as an initial state.
	 */
	public abstract void addInitialState(String newState);
	
	/**
	 * Removes the parameter state from the FSM's set of initial states.
	 * 
	 * @param state - String for the state name to be removed as an initial state.
	 * @return - True if the input state was successfully removed from the set of initial
	 * states, false otherwise.
	 */
	public abstract boolean removeInitialState(String state);
	
	/**
	 * Adds transitions leaving a given state to the FSM.
	 * 
	 * @param state - The State object to start from.
	 * @param transitions - ArrayList of Transition objects leading to all the
	 * places state is connected to.
	 */
	public abstract void addStateTransitions(State state, ArrayList<Transition> transitions);
	
	/**
	 * Adds an event from one state to another state.
	 */
	public abstract void addEvent(String state1, String eventName, String state2);
} // class FSM
