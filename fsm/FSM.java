package fsm;

import java.util.*;
import support.*;
import support.transition.Transition;
import support.event.Event;
import java.io.*;

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
				newFSM.addInitialState(initial);
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
	
	public <fsm extends FSM<S, T, E>> fsm makeCoAccessible() {
		try {
			FSM<S, T, E> newFSM = this.getClass().newInstance();
			// First, find what states we need to add.
			HashMap<String, Boolean> processedStates = getCoAccessibleMap();

			// Secondly, create the states and add the transitions
			for(Map.Entry<String, Boolean> entry : processedStates.entrySet()) {
				// If the state is coaccessible, add it!
				if(entry.getValue()) {
					S oldState = getState(entry.getKey());
					newFSM.addState(oldState);
					if(transitions.getTransitions(oldState) != null) { // Only continue if there are transitions from the state
						for(T t : transitions.getTransitions(oldState)) {
							T trans = t.generateTransition();
							trans.setTransitionEvent(t.getTransitionEvent());
							for(S state : t.getTransitionStates()) {
								if(processedStates.get(state.getStateName()))
									trans.setTransitionState(state);
							}
							if(trans.getTransitionStates().size() != 0)
								newFSM.addTransition(oldState, trans);
						}
					} // if not null
				} // if coaccessible
			} // for processed state
		
			// Finally, add the initial state
			for(S state : this.getInitialStates()) {
				if(processedStates.get(state.getStateName()))
					newFSM.addInitialState(state.getStateName());
			}
			return (fsm)newFSM;
		}
		catch(IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
		catch(InstantiationException e) {
			e.printStackTrace();
			return null;
		}	
	}

	/**
	 * Formerly createFileFormat(), toTextFile(String, String) converts an
	 * FSM into a text file which can be read back in and used to recreate
	 * an FSM later.
	 * 
	 * @param filePath The path of the folder to place the text file.
	 * @param name The name of the text file to create.
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
	 * Performs a union operation on the two FSMs by dynamically adding
	 * to an FSM passed as a parameter: newFSM.
	 * 
	 * @param other The FSM to add to the current FSM to build a union FSM.
	 * @param newFSM The empty FSM to fill with all the states and transitions
	 * of the calling FSM and the other FSM.
	 */
	
	protected <NewT extends Transition<S, E>> void unionHelper(FSM<S, T, E> other, FSM<S, NewT, E> newFSM) {
		// Add initial states
		for(State s : getInitialStates())  // Add the states from the this FSM
			newFSM.addInitialState(STATE_PREFIX_1 + s.getStateName());
		for(State s : other.getInitialStates())  // Add the states from the other FSM
			newFSM.addInitialState(STATE_PREFIX_2 + s.getStateName());
		
		// Add other states as well
		for(S s : this.states.getStates())
			newFSM.states.addState(s, STATE_PREFIX_1);
		for(S s : other.states.getStates())
			newFSM.states.addState(s, STATE_PREFIX_2);
		
		// Add events
		for(E e : this.events.getEvents())
			newFSM.events.addEvent(e);
		for(E e : other.events.getEvents())
			newFSM.events.addEvent(e);
		
		// Add transitions
		for(Map.Entry<S, ArrayList<T>> entry : this.transitions.getAllTransitions()) {
			S currState = newFSM.states.getState(STATE_PREFIX_1 + entry .getKey().getStateName());
			for(T t : entry.getValue()) {
				E newEvent = newFSM.events.getEvent(t.getTransitionEvent());
				for(S toState : t.getTransitionStates()) {
					S newState = newFSM.states.getState(STATE_PREFIX_1 + toState.getStateName());
					newFSM.addTransition(currState.getStateName(), newEvent.getEventName(), newState.getStateName());
				} // for every toState in the transition
			} // for transition
		} // for entry
		
		for(Map.Entry<S, ArrayList<T>> entry : other.transitions.getAllTransitions()) {
			S currState = newFSM.states.getState(STATE_PREFIX_2 + entry.getKey().getStateName());
			for(T t : entry.getValue()) {
				E newEvent = newFSM.events.getEvent(t.getTransitionEvent());
				for(S toState : t.getTransitionStates()) {
					S newState = newFSM.states.getState(STATE_PREFIX_2 + toState.getStateName());
					newFSM.addTransition(currState.getStateName(), newEvent.getEventName(), newState.getStateName());
				} // for every toState in the transition
			} // for transition
		} // for entry
	} // unionHelper(FSM, FSM)
	
	/**
	 * Performs a product or intersection operation on two FSMs and returns the result.
	 * 
	 * @param other - The FSM to perform the product operation on with the current FSM.
	 * @return - The resulting FSM from the product operation.
	 */
	
	public abstract FSM product(FSM<S, T, E> other);
	
	/**
	 * Performs a product operation on the calling FSM with the first parameter
	 * FSM, and builds the resulting FSM in the second FSM. Has no return, does
	 * its action by side-effect.
	 * 
	 * @param other FSM to perform the product operation on with the calling FSM.
	 * @param newFSM The FSM to use for building the product.
	 */
	
	protected void productHelper(FSM<S, T, E> other, FSM<S, T, E> newFSM) {
		// Get all the events the two have in common
		for(E thisEvent : this.events.getEvents()) {
			for(E otherEvent : other.events.getEvents()) {
				// All common events are added
				if(thisEvent.getEventName().equals(otherEvent.getEventName())) {
					newFSM.events.addEvent(thisEvent, otherEvent);
				} // if the event is identical
			} // for otherEvent
		} // for thisEvent
		
		// Go through all the initial states and add everything they connect to with shared events.
		for(S thisInitial : this.getInitialStates()) {
			for(S otherInitial : other.getInitialStates()) {
				// Now, start going through the paths leading out from this new initial state.
				LinkedList<S> thisNextState = new LinkedList<S>();
				thisNextState.add(thisInitial);
				LinkedList<S> otherNextState = new LinkedList<S>();
				otherNextState.add(otherInitial);
				
				while(!thisNextState.isEmpty() && !otherNextState.isEmpty()) { // Go through all the states connected
					S thisState = thisNextState.poll();
					S otherState = otherNextState.poll();
					S newState = newFSM.states.addState(thisState, otherState); // Add the new state
					
					// Go through all the transitions in each, see what they have in common
					ArrayList<T> thisTransitions = this.transitions.getTransitions(thisState);
					ArrayList<T> otherTransitions = other.transitions.getTransitions(otherState);
					if(thisTransitions != null && otherTransitions != null) {
						for(T thisTrans : thisTransitions) {
							for(T otherTrans : otherTransitions) {
								
								// If they share the same event
								E thisEvent = thisTrans.getTransitionEvent();
								if(thisEvent.equals(otherTrans.getTransitionEvent())) {
									
									// Then create transitions to all the combined neighbours
									for(S thisToState : thisTrans.getTransitionStates()) {
										for(S otherToState : otherTrans.getTransitionStates()) {
											
											// If the state doesn't exist, add to queue
											if(!newFSM.stateExists("(" + thisToState.getStateName() + ", " + otherToState.getStateName() + ")")) {
												thisNextState.add(thisToState);
												otherNextState.add(otherToState);
											} // if state doesn't exist
											
											// Add the state, then add the transition
											S newToState = newFSM.states.addState(thisToState, otherToState);
											newFSM.addTransition(newState.getStateName(), thisEvent.getEventName(), newToState.getStateName());
										} // for every state in other transition
									} // for every state in this transition
								} // if they share the event
							} // for other transitions
						} // for this transitions
					} // if transitions not null
				} // while there are more states connected to the 2-tuple of initial states
			} // for otherInitial
		} // for thisInitial
	} // productHelper(FSM)
	
	/**
	 * Performs the parallel composition of the calling FSM with the parameter
	 * FSM. The resulting FSM will be the same type as the calling FSM.
	 * 
	 * @param other FSM with which to perform the parallel composition.
	 * @return The result of the FSM operation.
	 */
	
	public abstract FSM<S, T, E> parallelComposition(FSM<S, T, E> other);
	
	/**
	 * Performs a parallel composition operation on the calling FSM with the first parameter
	 * FSM, and builds the resulting FSM in the second FSM. Has no return, does
	 * its action by side-effect.
	 * 
	 * @param other FSM to perform the parallel composition operation on with the calling FSM.
	 * @param newFSM The FSM to use for building the parallel composition.
	 */
	
	protected void parallelCompositionHelper(FSM<S, T, E> other, FSM<S, T, E> newFSM) {
		// Get all the events the two have in common
		HashSet<String> commonEvents = new HashSet<String>();
		for(E thisEvent : this.events.getEvents()) {
			for(E otherEvent : other.events.getEvents()) {
				// If it is a common event
				if(thisEvent.getEventName().equals(otherEvent.getEventName())) {
					E newEvent = newFSM.events.addEvent(thisEvent, otherEvent);
					commonEvents.add(newEvent.getEventName());
				} // if the event is identical
			} // for otherEvent
		} // for thisEvent
		
		// Add all the events unique to each FSM
		for(E thisEvent : this.events.getEvents())
			if(!commonEvents.contains(thisEvent.getEventName()))
				newFSM.events.addEvent(thisEvent);
		for(E otherEvent : other.events.getEvents())
			if(!commonEvents.contains(otherEvent.getEventName()))
				newFSM.events.addEvent(otherEvent);
		
		// Go through all the initial states and add everything they connect to.
		for(S thisInitial : this.getInitialStates()) {
			for(S otherInitial : other.getInitialStates()) {
				// Now, start going through the paths leading out from this new initial state.
				LinkedList<S> thisNextState = new LinkedList<S>();
				thisNextState.add(thisInitial);
				LinkedList<S> otherNextState = new LinkedList<S>();
				otherNextState.add(otherInitial);
				
				while(!thisNextState.isEmpty() && !otherNextState.isEmpty()) { // Go through all the states connected
					S thisState = thisNextState.poll();
					S otherState = otherNextState.poll();
					S newState = newFSM.states.addState(thisState, otherState); // Add the new state
					
					// Go through all the transitions in each, see what they have in common
					ArrayList<T> thisTransitions = this.transitions.getTransitions(thisState);
					ArrayList<T> otherTransitions = other.transitions.getTransitions(otherState);
					if(thisTransitions != null && otherTransitions != null) {
						for(T thisTrans : thisTransitions) {
							for(T otherTrans : otherTransitions) {
								
								// If they share the same event
								E thisEvent = thisTrans.getTransitionEvent();
								if(thisEvent.equals(otherTrans.getTransitionEvent())) {
									
									// Then create transitions to all the combined neighbours
									for(S thisToState : thisTrans.getTransitionStates()) {
										for(S otherToState : otherTrans.getTransitionStates()) {
											
											// If the state doesn't exist, add to queue
											if(!newFSM.stateExists("(" + thisToState.getStateName() + ", " + otherToState.getStateName() + ")")) {
												thisNextState.add(thisToState);
												otherNextState.add(otherToState);
											} // if state doesn't exist
											
											// Add the state, then add the transition
											S newToState = newFSM.states.addState(thisToState, otherToState);
											newFSM.addTransition(newState.getStateName(), thisEvent.getEventName(), newToState.getStateName());
										} // for every state in other transition
									} // for every state in this transition
								} // if they share the event
							} // for other transitions
						} // for this transitions
					} // if transitions are not null
					
					// Go through all the transitions and see what is unique
					if(thisTransitions != null) {
						for(T thisTrans : thisTransitions) {
							// If it's NOT a common event
							E thisEvent = thisTrans.getTransitionEvent();
							if(!commonEvents.contains(thisTrans.getTransitionEvent().getEventName())) {
								// Then, add all the transitions
								for(S thisToState : thisTrans.getTransitionStates()) {
									
									// If it doesn't exist, add it to the queue
									if(!newFSM.stateExists("(" + thisToState.getStateName() + ", " + otherState.getStateName() + ")")) {
										thisNextState.add(thisToState);
										otherNextState.add(otherState);
									} // if state doesn't exist
									
									// Add the state, then add the transition
									S newToState = newFSM.states.addState(thisToState, otherState);
									newFSM.addTransition(newState.getStateName(), thisEvent.getEventName(), newToState.getStateName());
								} // for the toStates
							} // if not a common event
						} // for this transitions
					} // if transitions are not null
					if(otherTransitions != null) {
						for(T otherTrans : other.transitions.getTransitions(otherState)) {
							// If it's NOT a common event
							E thisEvent = otherTrans.getTransitionEvent();
							if(!commonEvents.contains(otherTrans.getTransitionEvent().getEventName())) {
								// Then, add all the transitions
								for(S otherToState : otherTrans.getTransitionStates()) {
									
									// If it doesn't exist, add it to the queue
									if(!newFSM.stateExists("(" + thisState.getStateName() + ", " + otherToState.getStateName() + ")")) {
										thisNextState.add(thisState);
										otherNextState.add(otherToState);
									} // if state doesn't exist
									
									// Add the state, then add the transition
									S newToState = newFSM.states.addState(thisState, otherToState);
									newFSM.addTransition(newState.getStateName(), thisEvent.getEventName(), newToState.getStateName());
								} // for the toStates
							} // if not a common event
						} // for other transitions
					} // if transitions are not null
				} // while there are more states connected to the 2-tuple of initial states
			} // for otherInitial
		} // for thisInitial
	} // parallelCompositionHelper(FSM)
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	/**
	 * Setter method that assigns the parameter inId as the id for the FSM, which is used by
	 * the UI to identify the FSM.
	 * 
	 * @param inId String representing the desired name for the FSM.
	 */
	public void setId(String inId) {
		id = inId;
	}
	
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
	 * Gets the ID for the FSM which is used to identify it in the UI.
	 * 
	 * @return String representing the ID for the FSM.
	 */
	
	public String getId() {
		return id;
	}
	
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
	 * 
	 * @return
	 */
	
	public Collection<S> getStates() {
		return states.getStates();
	}
	
	/**
	 * 
	 * @return
	 */
	
	public Collection<E> getEvents(){
		return events.getEvents();
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
	 * Gets if a State with a given stateName String is a marked state.
	 * 
	 * @param stateName String representing the name of the state.
	 * @return True if the state is marked, false otherwise.
	 */
	
	public boolean isMarked(String stateName) {
		return states.getState(stateName).getStateMarked();
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
	 * @return - Returns a boolean value: true if the State extending object curr is coaccessible, false otherwise.
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
	} // recursivelyFindMarked(S, HashMap<String, Boolean>, HashSet<String>)
	
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
	 * Adds a new state using the state object passed in, which may be from another FSM.
	 * It copies over the same state name.
	 * 
	 * @param state State object to be used as a template for the new one to add to the
	 * FSM.
	 * @return True if the state was added successfully, false otherwise.
	 */
	
	public boolean addState(S state) {
		return addState(state.getStateName());
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
	 * 
	 * @param newState
	 */
	
	public abstract void addInitialState(State newState);
	
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
		S curr = states.getState(stateName);
		if(curr == null)	return false;
		boolean isMarked = curr.getStateMarked();
		states.getState(stateName).setStateMarked(!isMarked);
		return !isMarked;
	}

} // class FSM
