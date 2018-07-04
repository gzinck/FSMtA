package fsm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

import support.EventMap;
import support.State;
import support.StateMap;
import support.TransitionFunction;
import support.event.Event;
import support.transition.Transition;

/**
 * This class
 * 
 * This class is a part of the fsm package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 *
 * @param <<r>S> - 
 * @param <<r>T> - 
 * @param <<r>E> - 
 */

public abstract class TransitionSystem<S extends State, T extends Transition<S, E>, E extends Event> {
	
//---  Instance Variables   -------------------------------------------------------------------

	/** StateMap<<r>S> object possessing all the States associated to this FSM object */
	protected StateMap<S> states;
	/** EventMap<<r>E> object possessing all the Events associated to this FSM object */
	protected EventMap<E> events;
	/** TransitionFunction<<r>S, T, E> object mapping states to sets of transitions (which contain the state names). */
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
	 * This method processes the information stored by the FSM object to generate a
	 * String in the dot-form format for consumption by the GraphViz program to
	 * create a visual representation of the FSM.
	 * 
	 * @return - Returns a String containing the dot-form representation of this FSM object.
	 */
	
	public String makeDotString() {
		String statesInDot = states.makeDotString();	//Have the StateMap do its thing
		String transitionsInDot = transitions.makeDotString();	//Have the TransitionFunction do its thing
		return statesInDot + transitionsInDot;	//Return 'em all
	}
	
	/**
	 * This method performs a trim operation on the calling FSM (performing the
	 * makeAccessible() and makeCoAccessible() methods) to make sure only states
	 * that are reachable from Initial States and can reach Marked States are
	 * included.
	 * 
	 * @return - Returns an FSM<<r>S, T, E> extending object representing the trimmed version of the calling FSM object.
	 */
	
	public <fsm extends FSM<S, T, E>> fsm trim() {
		fsm newFSM = this.makeAccessible();
		return newFSM.makeCoAccessible();
	}
	
	/**
	 * Searches through the graph represented by the TransitionFunction object, and removes
	 * disjoint elements.
	 * 
	 * Algorithm starts from all initial States, and adds them to a queue. They are
	 * then placed into the new TransitionFunction object, and all States reachable by these
	 * initial states are placed in a queue for processing. States are checked against being
	 * already present in the newFSM object, not being added to the queue if already handled.
	 * 
	 * Some post-processing may be required by more advanced types of FSM.
	 * 
	 * @return - Returns an FSM<<r>S, T, E> extending object representing the accessible version of the calling FSM object. 
	 */
	
	public <fsm extends FSM<S, T, E>> fsm makeAccessible() {
		// Make a queue to keep track of states that are accessible and their neighbours.
		LinkedList<S> queue = new LinkedList<S>();
		
		// Initialize a new FSM with initial states.
		try {
			TransitionSystem<S, T, E> newFSM = this.getClass().newInstance();
			for(S initial : getInitialStates()) {
				newFSM.addInitialState(initial);
				queue.add(initial);
			} // for initial state
			
			while(!queue.isEmpty()) {
				S stateName = queue.poll();
				newFSM.addState(stateName);
				// Go through the transitions
				ArrayList<T> currTransitions = this.transitions.getTransitions(getState(stateName));
				if(currTransitions != null) {
					for(T t : currTransitions) {
						// Add the states; it goes to to the queue if not already present in the newFSM
						for(S s : t.getTransitionStates())
							if(!newFSM.stateExists(s.getStateName()))
								queue.add(s);
						// Add the transition by copying the old one.
						newFSM.addTransition(newFSM.getState(stateName.getStateName()), t);
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
	 * Searches through the graph represented by the TransitionFunction object, and removes any
	 * states that cannot reach a marked state.
	 * 
	 * A helper method is utilized to generate a list of States and their legality in the new FSM object,
	 * after which the contents of the old FSM object are processed with respect to this list as they construct
	 * the new FSM object.
	 * 
	 * @return - Returns an FSM<<r>S, T, E> extending object representing the CoAccessible version of the original FSM.
	 */
	
	public <fsm extends FSM<S, T, E>> fsm makeCoAccessible() {
		try {
			TransitionSystem<S, T, E> newFSM = this.getClass().newInstance();
			// First, find what states we need to add.
			HashMap<String, Boolean> processedStates = getCoAccessibleMap();	//Use helper method to generate list of legal/illegal States

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
	 * Helper method that processes the calling FSM object to generate a list of States for that
	 * object describing their status as CoAccessible, or able to reach a Marked State.
	 * 
	 * @return - Returns a HashMap mapping String state names to true if the state is coaccessible, and false if it is not.
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
	} // getCoAccessibleMap(State, HashMap<String, Boolean>)

	/**
	 * Helper method to the getCoAccessibleMap method that recursively checks States in the calling FSM object's StateMap,
	 * declaring them as either CoAccessible, not CoAccessible, or unvisited (true, false, null in the results HashMap)
	 * over the course of its processing, creating via side-effect the list of States' statuses once all have been visited.
	 * 
	 * Algorithm works by recursively exploring all the neighbors of each observed State until it either finds a Marked State,
	 * runs out of neighbors, or finds a State it has already seen. In the first case, all States along that path are set as 
	 * 'true', and if States remain, another is processed in this same way. In either other case, it returns false, and either
	 * another pathway is explored or that path of States is marked as not being CoAccessible ('false').
	 * 
	 * The method getCoAccessibleMap() uses this to generate the list of States and their statuses.
	 * 
	 * @param curr - State extending object that represents the current 'State' to process recursively.
	 * @param results - HashMap<<r>String, Boolean> object that records the status of each State as CoAccessible or not.
	 * @param visited - HashSet<<r>String> object that keeps track of which States have been already visited.
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
		if(check != null)
			return check;
		
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
	
	/**
	 * Gets if the FSM is blocking—that is, if there are possible words which are not
	 * part of the prefix closure of the marked language of the FSM. In other words, if
	 * the FSM is NOT coaccessible, then the FSM is blocking.
	 * It marks bad states along the way.
	 * 
	 * @return - Returns a boolean value; true if the FSM is blocking, false otherwise
	 */
	
	public boolean isBlocking() {
		// First, find what states we need to indicate
		HashMap<String, Boolean> processedStates = getCoAccessibleMap();	//Use helper method to generate list of legal/illegal States

		boolean isBlocking = false;
		
		// Secondly, indicate blocking states
		for(Map.Entry<String, Boolean> entry : processedStates.entrySet()) {
			if(!entry.getValue()) {
				isBlocking = true;
				this.getState(entry.getKey()).setStateBad(true);
			} // if
			else
				// Reset the badness to false of good states (accommodates multiple different
				// operations doing marking by overwriting).
				this.getState(entry.getKey()).setStateBad(false);
		} // for
		
		return isBlocking;
	} // isBlocking()
	
//---  Copy Methods that steal from other FSMs   -----------------------------------------------------------------------

	/**
	 * Copies the states of another FSM into the current FSM with an appended prefix (which can be an empty String).
	 * 
	 * @param other - FSM object whose states are copied.
	 * @param prefix - String object representing the  prefix for the new state names (can be the empty string).
	 */
	
	public <S1 extends State, E1 extends Event, T1 extends Transition<S1, E1>> void copyStates(TransitionSystem<S1, T1, E1> other, String prefix) {
		for(State s : other.getStates())
			states.addState(s, prefix);
		for(State s : other.getInitialStates())
			addInitialState(prefix + s.getStateName());
	} // copyStates (FSM, String)
	
	/**
	 * Copies the states of another FSM into the current FSM.
	 * 
	 * @param other - FSM object whose states are copied.
	 */
	
	public <S1 extends State, E1 extends Event, T1 extends Transition<S1, E1>> void copyStates(TransitionSystem<S1, T1, E1> other) {
		copyStates(other, "");
	} // copyStates(FSM)
	
	/**
	 * Copies the events of another FSM into the current FSM.
	 * 
	 * @param other - FSM object whose events are copied.
	 */
	
	public <S1 extends State, E1 extends Event, T1 extends Transition<S1, E1>> void copyEvents(TransitionSystem<S1, T1, E1> other) {
		for(E1 e : other.events.getEvents())
			events.addEvent(e);
	} // copyEvents(FSM)
	
	/**
	 * Copies the transitions of another FSM into the current FSM.
	 * 
	 * @param other - FSM object whose transitions are to be copied.
	 */
	
	public <S1 extends State, E1 extends Event, T1 extends Transition<S1, E1>> void copyTransitions(TransitionSystem<S1, T1, E1> other) {
		for(S1 s : other.states.getStates()) {
			ArrayList<T1> thisTransitions = other.transitions.getTransitions(s);
			if(thisTransitions != null)
				for(T1 t : thisTransitions) {
					// Add every state the transition leads to
					for(S1 toState : t.getTransitionStates())
						this.addTransition(s.getStateName(), t.getTransitionEvent().getEventName(), toState.getStateName());
				} // for every transition
		} // for every state
	} // copyTransitions(FSM)
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	/**
	 * Setter method that assigns the parameter inId as the id for the FSM, which is used to identify the FSM.
	 * 
	 * @param inId - String object representing the new name associated to this FSM object.
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
	 * Getter method that is used to acquire the ID (represented as a String object) associated to this FSM object.
	 * 
	 * @return - Returns a String object representing the ID associated to this FSM object.
	 */
	
	public String getId() {
		return id;
	}
	
	/**
	 * Getter method that searches within the FSM object's StateMap for a State extending object which corresponds
	 * to the provided State extending object; it searches by the name associated to the State extending object, which
	 * may find a match while being distinctly separate objects.
	 * 
	 * @param state - State extending object provided as a search reference for a matching State in the FSM object's StateMap.
	 * @return - Returns a State extending object that corresponds to the provided State extending object's name in the current FSM.
	 */
	
	public S getState(State state) {
		return states.getState(state);
	}
	
	/**
	 * Getter method that obtains the Collection of State extending objects stored within this FSM object's StateMap.
	 * 
	 * @return - Returns a Collection<<r>S> object representing the State extending objects associated to this FSM object.
	 */
	
	public Collection<S> getStates() {
		return states.getStates();
	}
	
	/**
	 * Getter method that obtains the Collection of Event extending objects stored within this FSM object's EventMap.
	 * 
	 * @return - Returns a Collection<<r>E> object representing the Event extending objects associated to this FSM object.
	 */
	
	public Collection<E> getEvents(){
		return events.getEvents();
	}
	
	/**
	 * Getter method that obtains the State extending object associated to the provided String representing a State's name.
	 * 
	 * @param stateName - String object representing the name of the State extending object to search for.
	 * @return - Returns the State extending object in this FSM object's StateMap corresponding to the provided String name.
	 */
	
	public S getState(String stateName) {
		return states.getState(stateName);
	}
	
	/**
	 * Getter method that returns all of the Initial States in the FSM object as an ArrayList of State extending objects.
	 * 
	 * @return - Returns an ArrayList<<r>S> of State extending objects which are the Initial States associated to this FSM object.
	 */
	
	public abstract ArrayList<S> getInitialStates();
	
	/**
	 * Getter method that requests whether the calling FSM object possesses an Initial State with
	 * the provided String name or not.
	 * 
	 * @param stateName - String object representing the name of the State extending object.
	 * @return - Returns a boolean value, true if the State extending object is an Initial State, false otherwise.
	 */
	
	public abstract boolean hasInitialState(String stateName);
	
	/**
	 * Getter method that requests whether or not a State extending object exists in the calling FSM object's
	 * StateMap, the supplied String object representing the State extending object.
	 * 
	 * @param stateName - String object representing the State extending object to query the FSM object's StateMap for; represents its name.
	 * @return - Returns a boolean value; true if the State extending object exists in the FSM, false otherwise.
	 */
	
	public boolean stateExists(String stateName) {
		return states.stateExists(stateName);
	}
	
	/**
	 * Getter method that returns the TransitionFunction<<r>T> object containing all
	 * the Transitions associated to this FSM object.
	 * 
	 * @return - Returns a TransitionFunction<T> object containing all the Transitions associated to this FSM object.
	 */
	
	public TransitionFunction<S, T, E> getTransitions() {
		return transitions;
	}
	
	/**
	 * Getter method that requests whether a specified State extending object is a Marked State.
	 * 
	 * @param stateName - String object representing the name of the State extending object being queried for its status as Marked.
	 * @return - Returns a boolean value; true if the state is Marked, false otherwise.
	 */
	
	public boolean isMarked(String stateName) {
		return states.getState(stateName).getStateMarked();
	}

//---  Manipulations - Adding   ---------------------------------------------------------------
	
	/**
	 * This method adds a new State to the StateMap<State> object, returning true if it didn't exist
	 * and was added successfully and false if it was already present.
	 * 
	 * @param stateName - String object representing the name of a State to add to the StateMap
	 * @return - Returns a boolean value describing the results of the defined operation
	 */
	
	public S addState(String stateName) {
		return states.addState(stateName);
	}
	
	/**
	 * This method adds a new State extending object to the calling FSM object's StateMap, using the provided
	 * State extending object to perform this task. It creates a weak copy, only respecting the State extending
	 * object's name and nothing else.
	 * 
	 * @param state - State extending object provided as the schematic of what to add to the calling FSM object's StateMap.
	 * @return - Returns a boolean value; true if the State extending object was added successfully, false otherwise.
	 */
	
	public S addState(State state) {
		return states.addState(state);
	}
	
	/**
	 * This method adds a new series of Transition extending objects to a given State extending object, accessing
	 * the calling FSM object's TransitionFunction object to either create a new entry for the State or
	 * append the new Transitions to its pre-existing entry.
	 * 
	 * @param state - State extending object representing the State to which the Transitions belong.
	 * @param newTransitions - ArrayList of Transition objects describing what Transitions belong to the provided State extending object.
	 */
	
	public void addStateTransitions(S state, ArrayList<T> newTransitions) {
		transitions.putTransitions(state, newTransitions);
	}
	
	/**
	 * This method handles the introduction of a new State extending object as an Initial State
	 * via a String object representing its name, behaving differently between Deterministic and 
	 * NonDeterministic FSM objects as their definitions specify and require.
	 * 
	 * @param newInitial - String object representing the name of the State extending object being introduced as an Initial State.
	 */
	
	public abstract void addInitialState(String newInitial);

	/**
	 * This method handles the introduction of a new State extending object as an Initial State,
	 * behaving differently between Deterministic and NonDeterministic FSM objects as their
	 * definitions specify and require.
	 * 
	 * @param newState - State extending object representing the State being introduced as an Initial State.
	 */
	
	public abstract void addInitialState(S newState);
	
	/**
	 * This method handles the adding of a new Transition to the calling FSM object via a format
	 * of 3 String objects representing a State, via an Event, leading to another State, creating
	 * the objects in the calling FSM object's State and EventMaps where necessary.
	 * 
	 * @param state1 - String object corresponding to the origin State for this Transition.
	 * @param eventName - String object corresponding to the Event of this Transition.
	 * @param state2 - String object corresponding to the destination State for the Transition.
	 */
	
	public void addTransition(String state1, String eventName, String state2) {
		// If they do not exist yet, add the states.
		S s1 = states.addState(state1);
		S s2 = states.addState(state2);
		
		// Get the event or make it
		E e = events.addEvent(eventName);
		
		// See if there is already a transition with the event...
		ArrayList<T> thisTransitions = transitions.getTransitions(s1);
		if(thisTransitions != null) {
			for(T t : thisTransitions) {
				if(t.getTransitionEvent().equals(e)) {
					t.setTransitionState(s2);
					return;
				} // if equal
			} // for every transition
		} // if not null
		
		// Otherwise, make a new Transition object
		T outbound = transitions.getEmptyTransition();
		outbound.setTransitionEvent(e);
		outbound.setTransitionState(s2);
		transitions.addTransition(s1, outbound);
	}
	
	/**
	 * This method handles the adding of a new Transition to the calling FSM object via a
	 * format of State and Transition objects, acquiring a reference to that State within
	 * the StateMap and appending them jointly to the TransitionFunction.
	 * 
	 * @param state - State extending object representing the State acquiring a new Transition.
	 * @param transition - Transition extending object representing the Transition being added to the provided State extending object.
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
	 * This method removes a State extending object from the calling FSM object as described by the
	 * provided String object, further handling the cases of the State being Initial or appearing
	 * in the TransitionFunction associated to this FSM object.
	 * 
	 * @param stateName - String object representing the name of the State extending object to remove from the calling FSM object.
	 * @return - Returns a boolean value representing the outcome of the operation: true if the state was removed, false if the state did not exist.
	 */
	
	public boolean removeState(String stateName) {
		// If the state exists...
		if(states.stateExists(stateName)) {
			// If it is the initial state, it shouldn't be anymore
			removeInitialState(stateName);
			// Then, we need to remove the state from every reference to it in the transitions.
			transitions.removeState(states.getState(stateName));
			states.removeState(stateName);
			return true;
		}
		return false;
	}
	
	/**
	 * This method removes a State extending object from the calling FSM object's method of storing
	 * Initial States, the State being described by a provided String representation of its name.
	 * 
	 * The exact details are handled by the FSM class implementing this, as Deterministic and NonDeterministic
	 * FSMs handle Initial States differently.
	 * 
	 * @param stateName - String object representing the State extending object's name, denoting which State to remove from storage of Initial States.
	 * @return - Returns a boolean value; true if the denoted State was successfully removed from the set of Initial States, false otherwise.
	 */
	
	public abstract boolean removeInitialState(String stateName);
	
	/**
	 * This method handles the removing of a Transition extending object from the calling FSM object's
	 * TransitionFunction, as described by the provided format of Transition information: 3 String objects
	 * representing the State leading, by a defined Event, to another State.
	 * 
	 * @param state1 - String object corresponding to the origin State extending object for the Transition object.
	 * @param eventName - String object corresponding to the Event for the Transition object.
	 * @param state2 - String object corresponding to the destination State extending object for the Transition object.
	 * @return - Returns a boolean value; true if the Transition was removed, false if it did not exist.
	 */
		
	public boolean removeTransition(String state1, String eventName, String state2) {
		S s1 = getState(state1);
		S s2 = getState(state2);
		E e = events.getEvent(eventName);
		if(s1 == null) {
			System.out.println("First state failed");
		}
		if(e == null) {
			System.out.println("Event failed");
		}
		if(s1 == null || s2 == null || e == null) {
			return false;
		}
		if(transitions.removeTransition(s1, e, s2)) return true;
		return false;
	}

//---  Manipulations - Other   ----------------------------------------------------------------

	/**
	 * This method handles the toggling of a State extending object's status as Marked, reversing
	 * its current status to its opposite. (true -> false, false -> true). The State extending object
	 * is so defined by a provided String object representing its name.
	 * 
	 * @param stateName - String object representing the name of the State extending object to have its status as Marked be toggled.
	 * @return - Returns a Boolean object; true if the state is now marked, false if the state is now unmarked, or null if it did not exist.
	 */
	
	public Boolean toggleMarkedState(String stateName) {
		S curr = states.getState(stateName);
		if(curr == null)	
			return null;
		boolean isMarked = curr.getStateMarked();
		states.getState(stateName).setStateMarked(!isMarked);
		return !isMarked;
	}
}