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

public abstract class FSM<S extends State, T extends Transition<S, E>, E extends Event> {
	
//---  Constant Values   ----------------------------------------------------------------------
	
	/** String constant designating this object as a specific type of FSM for clarification purposes*/
	public static final String FSM_TYPE = "FSM";
	/** String constant designating the file extension to append to the file name when writing to the system*/
	public static final String FSM_EXTENSION = ".fsm";
	/** String value describing the prefix assigned to all States in an FSM to differentiate it from another FSM*/
	public final static String STATE_PREFIX_1 = "a";
	/** String value describing the prefix assigned to all States in an FSM to differentiate it from another FSM*/
	public final static String STATE_PREFIX_2 = "b";
	
//---  Instance Variables   -------------------------------------------------------------------
	
	/** HashMap<String, <S extends State>> mapping state names to state objects, which all contain attributes of the given state. */
	protected StateMap<S> states;
	/** HashMap<String, <E extends Event>> mapping event names to event objects, which all contain attributes of the given event. */
	protected EventMap<E> events;
	/** TransitionFunction mapping states to sets of transitions (which contain the state names). */
	protected TransitionFunction<S, T, E> transitions;
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
	
	public <fsm extends FSM<S, T, E>> fsm trim() {
		fsm newFSM = this.makeAccessible();
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
	
	public <fsm extends FSM<S, T, E>> fsm makeAccessible() {
		// Make a queue to keep track of states that are accessible and their neighbours.
		LinkedList<String> queue = new LinkedList<String>();
		
		// Initialize a new FSM with initial states.
		try {
			FSM<S, T, E> newFSM = this.getClass().newInstance();
			for(S initial : getInitialStates()) {
				newFSM.addInitialState(initial.getStateName());
				queue.add(initial.getStateName());
			} // for initial state
			
			while(!queue.isEmpty()) {
				String stateName = queue.poll();
				// Go through the transitions
				ArrayList<T> currTransitions = this.transitions.getTransitions(getState(stateName));
				if(currTransitions != null) {
					for(T t : currTransitions) {
						// Add the states it goes to to the queue if not already present
						for(S s : t.getTransitionStates())
							if(!newFSM.stateExists(s.getStateName()))
								queue.add(s.getStateName());
						// Add the transition by copying the old one.
						newFSM.addTransition(newFSM.getState(stateName), t);
					} // for
				} // if not null
			} // while
			
			return (fsm)newFSM;
		} catch(IllegalAccessException e) {
			e.printStackTrace();
			return null;
		} catch(InstantiationException e) {
			e.printStackTrace();
			return null;
		}	
	} // makeAccessible()
	
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
	
	public abstract <fsm extends FSM<S, T, E>> fsm makeCoAccessible();

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
	
	public abstract FSM union(FSM<S, T, E> other);
	
	/**
	 * Performs a product or intersection operation on two FSMs and returns the result.
	 * @param other - The FSM to perform the product operation on with the current FSM.
	 * @return - The resulting FSM from the product operation.
	 */
	
	public abstract FSM product(FSM<S, T, E> other);
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	/**
	 * Setter method that assigns a new StateMap<<s>S> to replace the previously assigned set of States.
	 * 
	 * @param inState - StateMap<<s>S> object that assigns a new set of Events to this FSM object
	 */
	
	public void setFSMStateMap(StateMap<S> inState) {
		states = inState;
	}
	
	/**
	 * Setter method that assigns a new EventMap<<s>E> object to replace the previously assigned set of Events.
	 * 
	 * @param inEvent - EventMap<<s>E> object that assigns a new set of Events to this FSM object
	 */
	
	public void setFSMEventMap(EventMap<E> inEvent) {
		events = inEvent;
	}
	
	/**
	 * Setter method that assigns a new TransitionFunction<<s>S, T, E> object to replace the previously assigned set of Transitions.
	 * 
	 * @param inTrans - TransitionFunction<<s>S, T, E> object that assigns a new set of Transitions to this FSM object
	 */
	
	public void setFSMTransitionFunction(TransitionFunction<S, T, E> inTrans) {
		transitions = inTrans;
	}
	
	/**
	 * Setter method that aggregates the other setter methods to assign new values to the instance variables containing
	 * information about the State, Transitions, and Events.
	 * 
	 * @param inStates - StateMap<<s>S> object that stores a new set of States to assign to this FSM object
	 * @param inEvents - TransitionFunction<<s>S, T, E> object that stores a new set of Transitions to assign to this FSM object
	 * @param inTrans - EventMap<<e>E> object that stores a new set of Events to assign to this FSM object
	 */
	
	public void constructFSM(StateMap<S> inStates, TransitionFunction<S, T, E> inTrans, EventMap<E> inEvents) {
		setFSMStateMap(inStates);
		setFSMEventMap(inEvents);
		setFSMTransitionFunction(inTrans);
	}
	
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
	
	public S getState(S state) {
		return states.getState(state);
	}
	
	/**
	 * Gets the State object with the specified name.
	 * 
	 * @param stateName - String name of the state to get.
	 * @return - The corresponding State in the current FSM.
	 */
	
	public S getState(String stateName) {
		return states.getState(stateName);
	}
	
	/**
	 * Gets all initial states in the object as an ArrayList.
	 * 
	 * @return An ArrayList of States which are all initial states.
	 */
	
	public abstract ArrayList<S> getInitialStates();
	
	/**
	 * Returns if a state exists in the FSM.
	 * 
	 * @param stateName - String representing the state to check for existence.
	 * @return - True if the state exists in the FSM, false otherwise.
	 */
	
	public boolean stateExists(String stateName) {
		return states.stateExists(stateName);
	}
	
	/**
	 * Getter method that returns the TransitionFunction<T> object containing all
	 * the Transitions associated to this FSM object.
	 * 
	 * @return - Returns a TransitionFunction<T> object containing all the Transitions associated to this FSM object.
	 */
	
	public TransitionFunction<S, T, E> getTransitions() {
		return transitions;
	}
	
	/**
	 * This method checks gets a HashMap mapping all the states in the FSM to a boolean
	 * representing if the given state is coaccessible or not.
	 * 
	 * @return HashMap mapping String state names to true if the state is coaccessible, 
	 * and false if it is not.
	 */
	
	protected HashMap<String, Boolean> getCoAccessibleMap() {
		// When a state is processed, add it to the map and state if it reached a marked state.
		HashMap<String, Boolean> results = new HashMap<String, Boolean>();
		
		for(S curr : this.states.getStates()) {
			// Recursively check for a marked state, and keep track of a HashSet of states
			// which have already been visited to avoid loops.
			boolean isCoaccessible = recursivelyFindMarked(curr, results, new HashSet<String>());
			if(!isCoaccessible) results.put(curr.getStateName(), false);
		}
		return results;
	} // isCoAccessible(State, HashMap<String, Boolean>)

	/**
	 * This method helps the isCoAccessible method by recursively checking
	 * states, but avoiding loops (using the HashSet of visited states).
	 * This will only mark results when a given state is proven coaccessible,
	 * but will never mark results for a state which is not coaccessible.
	 * Thus, the method must be called for every state that needs to be
	 * evaluated.
	 * 
	 * @param curr The current state to evaluate if it is coaccessible.
	 * @param results HashMap mapping the state names to true if the state
	 * is proven coaccessible, and false if it was proven otherwise.
	 * @param visited HashSet of states which have already been visited when
	 * evaluating the coaccessibility of curr.
	 * @return True if curr is coaccessible, false otherwise.
	 */
	private boolean recursivelyFindMarked(S curr, HashMap<String, Boolean> results, HashSet<String> visited) {
		visited.add(curr.getStateName());
		
		// If the state is marked, return true
		if(curr.getStateMarked()) {
			results.put(curr.getStateName(), true);
			return true;
		}
		
		// Base cases when already checked if the state was coaccessible
		Boolean check = results.get(curr.getStateName());
		if(check != null && check == true) 			return true;
		else if (check != null && check == false)		return false;
		
		// Go through each unvisited state and recurse until find a marked state
		ArrayList<T> thisTransitions = transitions.getTransitions(curr);
		if(thisTransitions == null) return false;
		for(T t : thisTransitions) {
			for(S next : (ArrayList<S>)t.getTransitionStates()) {
				if(!visited.contains(next.getStateName())) { // If not already visited
					// If next is coaccessible, so is curr.
					if(recursivelyFindMarked(next, results, visited)) {
						results.put(curr.getStateName(), true);
						return true;
					} // if coaccessible
				} // if not already visited
			} // for each transition state
		} // for each transition object
		return false;
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
		states.addState(stateName);
		return true;
	}
	
	/**
	 * Adds transitions leaving a given state to the FSM.
	 * 
	 * @param state - The State object to start from.
	 * @param newTransitions - ArrayList of Transition objects leading to all the
	 * places state is connected to.
	 */
	
	public void addStateTransitions(S state, ArrayList<T> newTransitions) {
		transitions.putTransitions(state, newTransitions);
	}
	
	/**
	 * Adds the parameter state as an initial state of the FSM.
	 * Behavior depends on if the FSM is deterministic or non-deterministic.
	 * 
	 * @param newInitial - String for the state name to be added as an initial state.
	 */
	
	public abstract void addInitialState(String newInitial);

	/**
	 * Adds a transition from one state to another state.
	 * 
	 * @param state1 - The String corresponding to the origin state for the transition.
	 * @param eventName - The String corresponding to the event to create.
	 * @param state2 - The String corresponding to the destination state for the transition.
	 */
	
	public void addTransition(String state1, String eventName, String state2) {
		// If they do not exist yet, add the states.
		S s1 = states.addState(state1);
		S s2 = states.addState(state2);
		
		// Get the event or make it
		E e = events.addEvent(eventName);
		try {
			// TODO Make sure that we check if the transition's event already exists
			// for the from state in the transition, because if it exists, then we
			// have to add the state to that object instead...
			// TODO Also make sure you CANNOT add a new state to the transition object
			// elsewhere...
			T outbound = transitions.getEmptyTransition();
			outbound.setTransitionEvent(e);
			outbound.setTransitionState(s2);
			transitions.addTransition(s1, outbound);
		}
		catch(Exception e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Adds a transition from one state to another state by copying the parameter
	 * state and transition objects.
	 * 
	 * @param state
	 * @param transition
	 */
	
	public void addTransition(S state, T transition) {
		S fromState = states.addState(state); // Get the state or make it
		E e = events.addEvent(transition.getTransitionEvent()); // Get the event or make it
		try {
			T outbound = transitions.getEmptyTransition(); // New transition object
			outbound.setTransitionEvent(e);
			for(S s : transition.getTransitionStates()) { // Add all the transition states (make them if necessary)
				S toState = states.addState(s);
				outbound.setTransitionState(toState);
			} // for transition state
			transitions.addTransition(fromState, outbound);
		} catch(Exception e1) {
			e1.printStackTrace();
		}
	}
	
	
//---  Manipulations - Removing   -------------------------------------------------------------
	
	/**
	 * Removes a state from the FSM. If the State was an initial state, then the
	 * State is no longer an initial state after removing it.
	 * 
	 * @param stateName - String value representing the State to remove from the FSM.
	 * @return - Returns a boolean value representing the outcome of the operation:
	 * true if the state was removed, false if the state did not exist.
	 */

	public boolean removeState(String stateName) {
		// If the state exists...
		if(states.stateExists(stateName)) {
			// If it is the initial state, it shouldn't be anymore
			removeInitialState(stateName);
			states.removeState(stateName);
			// Then, we need to remove the state from every reference to it in the transitions.
			transitions.removeState(states.getState(stateName));
			return true;
		}
		return false;
	}
	
	/**
	 * Removes the parameter state from the FSM's set of initial states.
	 * 
	 * @param stateName - String for the state name to be removed as an initial state.
	 * @return - True if the input state was successfully removed from the set of initial
	 * states, false otherwise.
	 */
	
	public abstract boolean removeInitialState(String stateName);
	
	/**
	 * Removes a transition from one state to another state.
	 * 
	 * @param state1 - The String corresponding to the origin state for the transition.
	 * @param eventName - The String corresponding to the event to create.
	 * @param state2 - The String corresponding to the destination state for the transition.
	 * @return True if the event was removed, false if it did not exist.
	 */
	
	public boolean removeTransition(String state1, String eventName, String state2) {
		S s1 = getState(state1);
		S s2 = getState(state2);
		E e = events.getEvent(eventName);
		if(s1 == null || s2 == null || e == null) return false;
		if(transitions.removeTransition(s1, e, s2)) return true;
		return false;
	}

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
