package fsm;

import java.util.*;
import support.*;
import support.transition.Transition;
import support.event.Event;

/**
 * This class models a Finite State Machine with some of the essential elements.
 * It must be extended to be used (eg. by NonDeterministic or Deterministic to
 * determine how transitions and initial states are handled).
 * 
 * It is part of the fsm package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public abstract class FSM<T extends Transition, E extends Event> {
	
//---  Constant Values   ----------------------------------------------------------------------
	
	/** String constant designating this object as a specific type of FSM for clarification purposes*/
	public static final String FSM_TYPE = "FSM";
	/** String constant designating the file extension to append to the file name when writing to the system*/
	public static final String FSM_EXTENSION = ".fsm";
	
//---  Instance Variables   -------------------------------------------------------------------
	
	/** HashMap<String, State> mapping state names to state objects, which all contain attributes of the given state. */
	protected StateMap<State> states;
	/** HashMap<String, E> mapping event names to event objects, which all contain attributes of the given event. */
	protected EventMap<E> events;
	/** TransitionFunction mapping states to sets of transitions (which contain the state names). */
	protected TransitionFunction<T> transitions;
	/** String object possessing the identification for this FSM object. */
	protected String id;
	
//---  Single-FSM Operations   ----------------------------------------------------------------

	/**
	 * Renames all the states in the set of states in the FSM so that
	 * states are named sequentially ("0", "1", "2"...).
	 */
	
	public void renameStates() {
		states.renameStates();
	} // renameStates()
	
	/**
	 * Makes a String object which has the dot representation of the FSM, which
	 * can be pulled into GraphViz.
	 * 
	 * @return String containing the dot representation of the FSM.
	 */
	
	public String makeDotString() {
		String statesInDot = states.makeDotString();
		String transitionsInDot = transitions.makeDotString();
		return statesInDot + transitionsInDot;
	}
	
	/**
	 * This method performs a trim operation on the calling FSM (performing the
	 * makeAccessible() and makeCoAccessible() methods) to make sure only states
	 * that are reachable from initial states and can reach marked states are
	 * included.
	 * 
	 * @return - An FSM representing the trimmed version of the calling FSM.
	 */
	
	public FSM<T, E> trim() {
		FSM<T, E> newFSM = this.makeAccessible();
		return newFSM.makeCoAccessible();
	}
	
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
	
	public abstract FSM<T, E> makeAccessible();
	
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
	
	public abstract FSM<T, E> makeCoAccessible();

	/**
	 * Formerly createFileFormat(), toTextFile(String, String) converts an
	 * FSM into a text file which can be read back in and used to recreate
	 * an FSM later.
	 */
	
	public abstract void toTextFile(String filePath, String name);

//---  Multi-FSM Operations   -----------------------------------------------------------------
	
	/**
	 * Performs a union operation on two FSMs and returns the result.
	 * 
	 * @param other - The FSM to add to the current FSM in order to create a unioned
	 * FSM.
	 * @return - The result of the union.
	 */
	
	public abstract FSM<T, E> union(FSM<T, E> other);
	
	/**
	 * Performs a product or intersection operation on two FSMs and returns the result.
	 * @param other - The FSM to perform the product operation on with the current FSM.
	 * @return - The resulting FSM from the product operation.
	 */
	
	public abstract FSM<T, E> product(FSM<T, E> other);
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	/**
	 * Gets the specified state object from the FSM with the same
	 * state name as the parameter (but it might not be the same
	 * object, if the input state was associated with a different
	 * fsm).
	 * 
	 * @param state - State object from another FSM to find the
	 * corresponding one in the current FSM.
	 * @return - The corresponding State in the current FSM.
	 */
	
	public State getState(State state) {
		return states.getState(state);
	}
	
	/**
	 * Gets the State object with the specified name.
	 * 
	 * @param stateName - String name of the state to get.
	 * @return - The corresponding State in the current FSM.
	 */
	
	public State getState(String stateName) {
		return states.getState(stateName);
	}
	
	/**
	 * Returns if a state exists in the FSM.
	 * 
	 * @param state - String representing the state to check for existence.
	 * @return - True if the state exists in the FSM, false otherwise.
	 */
	
	public boolean stateExists(String state) {
		return states.stateExists(state);
	}
	
	/**
	 * Getter method that returns the TransitionFunction<T> object containing all
	 * the Transitions associated to this FSM object.
	 * 
	 * @return - Returns a TransitionFunction<T> object containing all the Transitions associated to this FSM object.
	 */
	
	public TransitionFunction<T> getTransitions() {
		return transitions;
	}
	
//---  Manipulations - Adding   ---------------------------------------------------------------
	
	/**
	 * This method adds a new State to the StateMap<State> object, returning true if it didn't exist
	 * and was added successfully and false if it was already present.
	 * 
	 * @param stateName - String object representing the name of a State to add to the StateMap
	 * @return - Returns a boolean value describing the results of the defined operation
	 */
	
	public boolean addState(String stateName) {
		if(states.stateExists(stateName))
			return false;
		states.addState(new State(stateName));
		return true;
	}
	
	/**
	 * Adds a new state to the FSM using a State object. It creates a
	 * copy of the input state object.
	 * 
	 * @param oldState - State object to add, which will be copied.
	 * @return - True if the state was successfully added, false
	 * if the state already existed.
	 */
	
	public boolean addState(State oldState) {
		if(states.stateExists(oldState.getStateName())) return false;
		State newState = new State(oldState);
		states.addState(newState);
		return true;
	}
	
	/**
	 * Adds transitions leaving a given state to the FSM.
	 * 
	 * @param state - The State object to start from.
	 * @param newTransitions - ArrayList of Transition objects leading to all the
	 * places state is connected to.
	 */
	
	public void addStateTransitions(State state, ArrayList<T> newTransitions) {
		transitions.putTransitions(state, newTransitions);
	}

	/**
	 * Adds the parameter state as an initial state of the FSM.
	 * Behavior depends on if the FSM is deterministic or non-deterministic.
	 * 
	 * @param newInitial - String for the state name to be added as an initial state.
	 * @return - True if the initial state was added successfully, otherwise false
	 * (fails if the initial state did not already exist).
	 */
	
	public abstract boolean addInitialState(String newInitial);

	/**
	 * Adds an event from one state to another state.
	 * 
	 * @param state1 - The String corresponding to the origin state for the transition.
	 * @param eventName - The String corresponding to the event to create.
	 * @param state2 - The String corresponding to the destination state for the transition.
	 * @return - True if the event was added successfully, false otherwise (i.e., if either
	 * state1 or state2 did not exist in the FSM).
	 */
	
	public abstract boolean addEvent(String state1, String eventName, String state2);

//---  Manipulations - Removing   -------------------------------------------------------------
	
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
	 * Removes the parameter state from the FSM's set of initial states.
	 * 
	 * @param state - String for the state name to be removed as an initial state.
	 * @return - True if the input state was successfully removed from the set of initial
	 * states, false otherwise.
	 */
	
	public abstract boolean removeInitialState(String state);

//---  Manipulations - Other   ----------------------------------------------------------------

	/**
	 * Toggles a state's marked property.
	 * 
	 * @param stateName - String representing the name of the state.
	 * @return - True if the state is now marked, false if the state is
	 * now unmarked (or if the state does not exist).
	 */
	
	public boolean toggleMarkedState(String stateName) {
		boolean isMarked = states.getState(stateName).getStateMarked();
		states.getState(stateName).setStateMarked(!isMarked);
		return !isMarked;
	}

} // class FSM
