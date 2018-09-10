package fsm;

import support.map.TransitionFunction;
import support.transition.Transition;
import fsm.attribute.Deterministic;
import java.util.LinkedList;
import java.util.Collection;
import support.map.EventMap;
import support.map.StateMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import support.State;
import support.Event;
import java.util.*;

/**
 * This abstract class provides the framework for Finite State Machine objects
 * and their like, handling the presence of States, Events, and Transitions of
 * generic types to permit different variations using the same design.
 * 
 * This abstract class is a part of the fsm package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 *
 * @param <<r>T> - Generic object with the stipulation that it extend the Transition class.
 */

public abstract class TransitionSystem<T extends Transition> {
	
//---  Instance Variables   -------------------------------------------------------------------

	/** StateMap object possessing all the States associated to this TransitionSystem object */
	protected StateMap states;
	/** EventMap object possessing all the Events associated to this TransitionSystem object */
	protected EventMap events;
	/** TransitionFunction<<r>T> object mapping states to sets of transitions (which contain the state names). */
	protected TransitionFunction<T> transitions;
	/** String object possessing the identification for this TransitionSystem object. */
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
	 * @return - Returns a String object containing the dot-form representation of this FSM object.
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
	 * @return - Returns a TransitionSystem<<r>T> object representing the trimmed
	 * version of the calling TransitionSystem object.
	 */
	
	public <TS extends TransitionSystem<T>> TS trim() {
		TS newFSM = this.makeAccessible();
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
	 * @return - Returns a TransitionSystem object representing the accessible
	 * version of the calling TransitionSystem object. 
	 */
	
	public <TS extends TransitionSystem<T>> TS makeAccessible() {
		// Make a queue to keep track of states that are accessible and their neighbours.
		LinkedList<State> queue = new LinkedList<State>();
		
		// Initialize a new FSM with initial states.
		try {
			TransitionSystem<T> newFSM = this.getClass().newInstance();
			for(State initial : getInitialStates()) {
				newFSM.addInitialState(initial);
				queue.add(initial);
			} // for initial state
			
			while(!queue.isEmpty()) {
				State curr = queue.poll();
				newFSM.addState(curr);
				// Go through the transitions
				ArrayList<T> currTransitions = this.transitions.getTransitions(getState(curr));
				if(currTransitions != null) {
					for(T t : currTransitions) {
						// Add the states; it goes to to the queue if not already present in the newFSM
						for(State s : t.getTransitionStates())
							if(!newFSM.stateExists(s.getStateName()))
								queue.add(s);
						// Add the transition by copying the old one.
						newFSM.addTransition(newFSM.getState(curr.getStateName()), t);
					} // for
				} // if not null
			} // while
			
			return (TS)newFSM;
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
	 * @return - Returns an TransitionSystem object representing the CoAccessible version of the original
	 * TransitionSystem.
	 */
	
	public <TS extends TransitionSystem<T>> TS makeCoAccessible() {
		try {
			TransitionSystem<T> newTS = this.getClass().newInstance();
			// First, find what states we need to add.
			HashMap<String, Boolean> processedStates = getCoAccessibleMap();	//Use helper method to generate list of legal/illegal States

			// Secondly, create the states and add the transitions
			for(Map.Entry<String, Boolean> entry : processedStates.entrySet()) {
				// If the state is coaccessible, add it!
				if(entry.getValue()) {
					State oldState = getState(entry.getKey());
					newTS.addState(oldState);
					if(transitions.getTransitions(oldState) != null) { // Only continue if there are transitions from the state
						for(T t : transitions.getTransitions(oldState)) {
							T trans = t.generateTransition();
							trans.setTransitionEvent(t.getTransitionEvent());
							for(State state : t.getTransitionStates()) {
								if(processedStates.get(state.getStateName()))
									trans.setTransitionState(state);
							}
							if(trans.getTransitionStates().size() != 0)
								newTS.addTransition(oldState, trans);
						}
					} // if not null
				} // if coaccessible
			} // for processed state
		
			// Finally, add the initial state
			for(State state : this.getInitialStates()) {
				if(processedStates.get(state.getStateName()))
					newTS.addInitialState(state.getStateName());
			}
			return (TS)newTS;
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
	 * @return - Returns a HashMap<<r>String, Boolean> object mapping String state names to true if the state is coaccessible, and false if it is not.
	 */
	
	protected HashMap<String, Boolean> getCoAccessibleMap() {
		// When a state is processed, add it to the map and state if it reached a marked state.
		HashMap<String, Boolean> results = new HashMap<String, Boolean>();
		
		for(State curr : this.states.getStates()) {
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
	 * @param curr - State object that represents the current 'State' to process recursively.
	 * @param results - HashMap<<r>String, Boolean> object that records the status of each State as CoAccessible or not.
	 * @param visited - HashSet<<r>String> object that keeps track of which States have been already visited.
	 * @return - Returns a boolean value: true if the State object curr is coaccessible, false otherwise.
	 */
	
	private boolean recursivelyFindMarked(State curr, HashMap<String, Boolean> results, HashSet<String> visited) {
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
			for(State next : (ArrayList<State>)t.getTransitionStates()) {
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
	} // recursivelyFindMarked(State, HashMap<String, Boolean>, HashSet<String>)
	
	/**
	 * Gets if the FSM is blocking—that is, if there are possible words which are not
	 * part of the prefix closure of the marked language of the FSM. In other words, if
	 * the FSM is NOT coaccessible, then the FSM is blocking.
	 * It marks bad states along the way.
	 * 
	 * @return - Returns a boolean value; true if the FSM is found to be blocking, false otherwise.
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
	
	/**
	 * This method converts an FSM object into a text file which can be read back in and used to recreate
	 * an FSM later, or used for analytical purposes. A helper class, ReadWrite, manages the brunt
	 * of this process, but for the various special features of FSM objects, each has to handle
	 * itself separately.
	 * 
	 * @param filePath - String object representing the path to the folder to place the text file.
	 * @param name - String object representing the name of the text file to create.
	 */
	
	public abstract void toTextFile(String filePath, String name);
	
//---  Copy Methods that steal from other FSMs   -----------------------------------------------------------------------

	/**
	 * This method copies the states of another FSM object into the current FSM object with a prepended
	 * String prefix (which can be an empty String).
	 * 
	 * @param other - FSM object whose states are copied for renaming.
	 * @param prefix - String object representing the prefix for the new state names (can be the empty string).
	 */
	
	public <T1 extends Transition> void copyStates(TransitionSystem<T1> other, String prefix) {
		for(State s : other.getStates())
			states.addState(s, prefix);
		for(State s : other.getInitialStates())
			addInitialState(prefix + s.getStateName());
	} // copyStates (FSM, String)
	
	/**
	 * This method copies the states of a provided FSM object into the current FSM object.
	 * 
	 * @param other - FSM object whose states are copied.
	 */
	
	public <T1 extends Transition> void copyStates(TransitionSystem<T1> other) {
		copyStates(other, "");
	} // copyStates(FSM)
	
	/**
	 * This method copies the states of a provided FSM object into the current FSM object,
	 * excluding the states in badStates.
	 * 
	 * @param other - FSM object whose states are copied.
	 * @param badStates - HashSet of State String names which are excluded from copying.
	 */
	
	public <T1 extends Transition> void copyStates(TransitionSystem<T1> other, HashSet<String> badStates) {
		for(State s : other.getStates()) if(!badStates.contains(s.getStateName()))
			states.addState(s).setStateInitial(false);
		for(State s : other.getInitialStates()) if(!badStates.contains(s.getStateName()))
			addInitialState(s.getStateName());
	}
	
	/**
	 * This method copies the events of a provided FSM object into the current FSM object.
	 * 
	 * @param other - An FSM object whose events are copied.
	 */
	
	public <T1 extends Transition> void copyEvents(TransitionSystem<T1> other) {
		for(Event e : other.events.getEvents())
			events.addEvent(e);
	} // copyEvents(FSM)
	
	/**
	 * This method copies the transitions of another FSM into the current FSM.
	 * 
	 * @param other - An FSM object whose transitions are copied.
	 */
	
	public <T1 extends Transition> void copyTransitions(TransitionSystem<T1> other) {
		for(State s : other.states.getStates()) {
			ArrayList<T1> thisTransitions = other.transitions.getTransitions(s);
			if(thisTransitions != null)
				for(T1 t : thisTransitions) {
					// Add every state the transition leads to
					for(State toState : t.getTransitionStates())
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
	 * Setter method that assigns a set of States as the States that compose the single defined State; that is,
	 * the parameter State aggregate is defined to have been made up of the parameter State ... varargs pieces.
	 * 
	 * @param aggregate - State object that represents a State which has been previously generated as the composition of a set of States.
	 * @param pieces - State ... varargs object that represents the set of States which compose the State aggregate.
	 */
	
	public void setStateComposition(State aggregate, State ... pieces) {
		ArrayList<State> composed = new ArrayList<State>();
		for(State in : pieces)
			if(composed.indexOf(in) == -1)
				composed.add(in); 
		states.setStateComposition(aggregate, composed);
	}
	
	public void setStateCompositionDuplicate(State aggregate, State ... pieces) {
		ArrayList<State> composed = new ArrayList<State>();
		for(State in : pieces)
			composed.add(in); 
		states.setStateComposition(aggregate, composed);
	}

	/**
	 * Setter method that allows for the assignation of a new HashMap<<r>State, ArrayList<<r>State>> object
	 * as the set of States and their corresponding set of States which compose them in this TransitionSystem
	 * object's StateMap.
	 * 
	 * @param composed - A HashMap<<r>State, ArrayList<<r>State>> object to replace the StateMap's composition instance variable.  
	 */
	
	public void setCompositionStates(HashMap<State, ArrayList<State>> composed) {
		states.setCompositionStates(composed);
	}
	
	/**
	 * Setter method that assigns a new StateMap<<r>State> to replace the previously assigned set of States.
	 * 
	 * @param inState - StateMap<<r>State> object that assigns a new set of Events to this FSM object
	 */
	
	public void setFSMStateMap(StateMap inState) {
		states = inState;
	}
	
	/**
	 * Setter method that assigns a new EventMap<<r>Event> object to replace the previously assigned set of Events.
	 * 
	 * @param inEvent - EventMap<<r>Event> object that assigns a new set of Events to this FSM object
	 */
	
	public void setFSMEventMap(EventMap inEvent) {
		events = inEvent;
	}
	
	/**
	 * Setter method that assigns a new TransitionFunction<<r>State, T, Event> object to replace the previously assigned set of Transitions.
	 * 
	 * @param inTrans - TransitionFunction<<r>State, T, Event> object that assigns a new set of Transitions to this FSM object
	 */
	
	public void setFSMTransitionFunction(TransitionFunction<T> inTrans) {
		transitions = inTrans;
	}
	
	/**
	 * Setter method that aggregates the other setter methods to assign new values to the instance variables containing
	 * information about the State, Transitions, and Events.
	 * 
	 * @param inStates - StateMap<<r>State> object that stores a new set of States to assign to this FSM object
	 * @param inEvents - TransitionFunction<<r>State, T, Event> object that stores a new set of Transitions to assign to this FSM object
	 * @param inTrans - EventMap<<r>Event> object that stores a new set of Events to assign to this FSM object
	 */
	
	public void constructFSM(StateMap inStates, TransitionFunction<T> inTrans, EventMap inEvents) {
		setFSMStateMap(inStates);
		setFSMEventMap(inEvents);
		setFSMTransitionFunction(inTrans);
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	/**
	 * Getter method that returns the StateMap object associated to the TransitionSystem object.
	 * 
	 * @return - Returns a StateMap object containing the Event objects associated to this TransitionSystem object.
	 */
	
	public StateMap getStateMap() {
		return states;
	}
	
	/**
	 * Getter method that returns the EventMap object associated to the TransitionSystem object.
	 * 
	 * @return - Returns an EventMap object containing the Event objects associated to this TransitionSystem object. 
	 */
	
	public EventMap getEventMap() {
		return events;
	}
	
	/**
	 * Getter method that is used to acquire the ID (represented as a String object) associated to this FSM object.
	 * 
	 * @return - Returns a String object representing the ID associated to this FSM object.
	 */
	
	public String getId() {
		return id;
	}
	
	/**
	 * Getter method that requests a HashMap<<r>State, ArrayList<<r>State>> object holding pairs made up of a 
	 * State and the Composite States that it was built fromt.
	 * 
	 * @return - Returns a HashMap<<r>State, ArrayList<<r>State>> object containing pairs of States and their Composite State.
	 */
	
	public HashMap<State, ArrayList<State>> getComposedStates(){
		return states.getComposedStates();
	}
	
	/**
	 * Getter method that requests a particular ArrayList<<r>State> object corresponding to a particular
	 * State object that, if found within the HashMap<<r>State, ArrayList<<r>State>>, is composed of
	 * a set of States store within the ArrayList<<r>State> object.
	 * 
	 * @param state - State object representing the aggregate-State composed of the returned set of States.
	 * @return - Returns an ArrayList<<r>State> object representing the States that compose the provided State object.
	 */
	
	public ArrayList<State> getStateComposition(State state){
		return states.getStateComposition(state);
	}
	
	/**
	 * Getter method that requests a particular ArrayList<<r>T> object corresponding to a provided State object.
	 * 
	 * @param state - State object provided as a key to search for its associated Transition objects. 
	 * @return - Returns an ArrayList<<r>T> object containing all the Transitions associated to the provided State object.
	 */
	
	public ArrayList<T> getStateTransitions(State state){
		return transitions.getTransitions(state);
	}
	
	/**
	 * Getter method that searches within the FSM object's StateMap for a State object which corresponds
	 * to the provided State object; it searches by the name associated to the State object, which
	 * may find a match while being distinctly separate objects.
	 * 
	 * @param state - State object provided as a search reference for a matching State in the FSM object's StateMap.
	 * @return - Returns a State object that corresponds to the provided State object's name in the current FSM.
	 */
	
	public State getState(State state) {
		return states.getState(state);
	}
	
	/**
	 * Getter method that obtains the Collection of State objects stored within this FSM object's StateMap.
	 * 
	 * @return - Returns a Collection<<r>State> object representing the State objects associated to this FSM object.
	 */
	
	public Collection<State> getStates() {
		return states.getStates();
	}
	
	/**
	 * Getter method that obtains the Collection of Event objects stored within this FSM object's EventMap.
	 * 
	 * @return - Returns a Collection<<r>Event> object representing the Event objects associated to this FSM object.
	 */
	
	public Collection<Event> getEvents(){
		return events.getEvents();
	}
	
	/**
	 * Getter method that obtains the State object associated to the provided String representing a State's name.
	 * 
	 * @param stateName - String object representing the name of the State object to search for.
	 * @return - Returns the State object in this FSM object's StateMap corresponding to the provided String name.
	 */
	
	public State getState(String stateName) {
		return states.getState(stateName);
	}
	
	/**
	 * Getter method that obtains the Event object associated to the provided String object representing an Event's name.
	 * 
	 * @param eventName - String object representing the name of the Event object being searched for in this TransitionSystem object.
	 * @return - Returns an Event object stored by this TransitionSystem object as defined by the provided String object.
	 */
	
	public Event getEvent(String eventName) {
		return events.getEvent(eventName);
	}
	
	/**
	 * Getter method that requests whether or not a State object exists in the calling FSM object's
	 * StateMap, the supplied String object representing the State object.
	 * 
	 * @param stateName - String object representing the State object to query the FSM object's StateMap for; represents its name.
	 * @return - Returns a boolean value; true if the State object exists in the FSM, false otherwise.
	 */
	
	public boolean stateExists(String stateName) {
		return states.stateExists(stateName);
	}
	
	/**
	 * Getter method that returns the TransitionFunction<<r>T> object containing all
	 * the Transitions associated to this FSM object.
	 * 
	 * @return - Returns a TransitionFunction<<r>T> object containing all the Transitions associated to this FSM object.
	 */
	
	public TransitionFunction<T> getTransitions() {
		return transitions;
	}
	
	/**
	 * Getter method that requests whether a specified State object is a Marked State.
	 * 
	 * @param stateName - String object representing the name of the State object being queried for its status as Marked.
	 * @return - Returns a boolean value; true if the state is Marked, false otherwise.
	 */
	
	public boolean isMarked(String stateName) {
		return states.getState(stateName).getStateMarked();
	}

	/**
	 * Getter method that returns all of the Initial States in the FSM object as an ArrayList of State objects.
	 * 
	 * @return - Returns an ArrayList<<r>State> of State objects which are the Initial States associated to this FSM object.
	 */
	
	public abstract ArrayList<State> getInitialStates();
	
	/**
	 * Getter method that requests whether the calling FSM object possesses an Initial State with
	 * the provided String name or not.
	 * 
	 * @param stateName - String object representing the name of the State object.
	 * @return - Returns a boolean value, true if the State object is an Initial State, false otherwise.
	 */
	
	public abstract boolean hasInitialState(String stateName);

//---  Manipulations - Adding   ---------------------------------------------------------------
	
	/**
	 * This method adds a new State to the StateMap<<r>State> object, returning the State object in the FSM
	 * that was either newly added or the already existing State corresponding to the provided State name.
	 * 
	 * @param stateName - String object representing the name of a State to add to the StateMap
	 * @return - Returns a State object representing the corresponding State in the FSM that was just added.
	 */
	
	public State addState(String stateName) {
		return states.addState(stateName);
	}
	
	/**
	 * This method adds a new State object to the calling FSM object's StateMap, using the provided
	 * State object to perform this task. It creates a weak copy, only respecting the State extending
	 * object's name and nothing else.
	 * 
	 * @param state - State object provided as the schematic of what to add to the calling FSM object's StateMap.
	 * @return - Returns a State object representing the corresponding State in the FSM that was just added.
	 */
	
	public State addState(State state) {
		return states.addState(state);
	}
	
	/**
	 * This method adds a State to the StateMap as a newly defined State produced by the composition
	 * of numerous States merged together, as provided as an argument.
	 * 
	 * @param state - State ... varargs object representing a group of State objects provided as ingredients of a new State.
	 * @return - Returns a State object built out of the provided States, its attributes the merging of those States.
	 */
	
	public State addState(State ... state) {
		Arrays.sort(state);
		return states.addState(state);
	}
	
	/**
	 * This method adds a new series of Transition objects to a given State object, accessing
	 * the calling FSM object's TransitionFunction object to either create a new entry for the State or
	 * append the new Transitions to its pre-existing entry.
	 * 
	 * @param state - State object representing the State to which the Transitions belong.
	 * @param newTransitions - ArrayList of Transition objects describing what Transitions belong to the provided State object.
	 */
	
	public void addStateTransitions(State state, ArrayList<T> newTransitions) {
		transitions.putTransitions(state, newTransitions);
	}
	
	/**
	 * Adds a map of states to the composition of an FSM.
	 * @param composed HashMap mapping States to an ArrayList of States, which is
	 * which is to be added to the state composition of the transition system.
	 */

	public void addStateComposition(HashMap<State, ArrayList<State>> composed) {
		HashMap<State, ArrayList<State>> map = getComposedStates();
		map.putAll(composed);
		setCompositionStates(map);
	}

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
		State s1 = states.addState(state1);
		State s2 = states.addState(state2);
		
		// Get the event or make it
		Event e = events.addEvent(eventName);
		
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
	 * @param state - State object representing the State acquiring a new Transition.
	 * @param transition - Transition object representing the Transition being added to the provided State object.
	 */
	
	public void addTransition(State state, T transition) {
		State fromState = states.addState(state); // Get the state or make it
		Event e = events.addEvent(transition.getTransitionEvent()); // Get the event or make it
		try {
			T outbound = transitions.getEmptyTransition(); // New transition object
			outbound.setTransitionEvent(e);
			for(State s : transition.getTransitionStates()) { // Add all the transition states (make them if necessary)
				State toState = states.addState(s);
				outbound.setTransitionState(toState);
			} // for transition state
			transitions.addTransition(fromState, outbound);
		} catch(Exception e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * This method permits the adding of a new Transition to the Transition System in the format
	 * of two States and an Event, representing a State leading to another State by the defined
	 * Event; a corresponding method exists within the TransitionFunction to take these arguments.
	 * 
	 * @param state - State object representing the State that can lead to the other State by the defined Event.
	 * @param event - Event object representing the Event by which the State -> State2 Transition occurs.
	 * @param state2 - State object representing the target State that is led to by the defined Event from the defined State.
	 */
	
	public void addTransition(State state, Event event, State state2) {
		transitions.addTransitionState(states.addState(state),  events.addEvent(event), states.addState(state2));
	}
	
	/**
	 * This method handles the introduction of a new State object as an Initial State
	 * via a String object representing its name, behaving differently between Deterministic and 
	 * NonDeterministic FSM objects as their definitions specify and require.
	 * 
	 * @param newInitial - String object representing the name of the State object being introduced as an Initial State.
	 */
	
	public abstract void addInitialState(String newInitial);

	/**
	 * This method handles the introduction of a new State object as an Initial State,
	 * behaving differently between Deterministic and NonDeterministic FSM objects as their
	 * definitions specify and require.
	 * 
	 * @param newState - State object representing the State being introduced as an Initial State.
	 */
	
	public abstract void addInitialState(State newState);
	
//---  Manipulations - Removing   -------------------------------------------------------------
	
	/**
	 * This method removes a State object from the calling FSM object as described by the
	 * provided String object, further handling the cases of the State being Initial or appearing
	 * in the TransitionFunction associated to this FSM object.
	 * 
	 * @param stateName - String object representing the name of the State object to remove from the calling FSM object.
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
	 * This method removes a State object from the calling FSM object's method of storing
	 * Initial States, the State being described by a provided String representation of its name.
	 * 
	 * The exact details are handled by the FSM class implementing this, as Deterministic and NonDeterministic
	 * FSMs handle Initial States differently.
	 * 
	 * @param stateName - String object representing the State object's name, denoting which State to remove from storage of Initial States.
	 * @return - Returns a boolean value; true if the denoted State was successfully removed from the set of Initial States, false otherwise.
	 */
	
	public abstract boolean removeInitialState(String stateName);
	
	/**
	 * This method handles the removing of a Transition object from the calling FSM object's
	 * TransitionFunction, as described by the provided format of Transition information: 3 String objects
	 * representing the State leading, by a defined Event, to another State.
	 * 
	 * @param state1 - String object corresponding to the origin State object for the Transition object.
	 * @param eventName - String object corresponding to the Event for the Transition object.
	 * @param state2 - String object corresponding to the destination State object for the Transition object.
	 * @return - Returns a boolean value; true if the Transition was removed, false if it did not exist.
	 */
		
	public boolean removeTransition(String state1, String eventName, String state2) {
		State s1 = getState(state1);
		State s2 = getState(state2);
		Event e = events.getEvent(eventName);
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
	
//---  Manipulations - Changing Many States at the Same Time   ---------------------------------------------------------------
	
	/**
	 * This method marks all states in the transition system.
	 */
	
	public void markAllStates() {
		for(State s : states.getStates())
			s.setStateMarked(true);
	}
	
	/**
	 * This method unmarks all states in the transition system.
	 */
	
	public void unmarkAllStates() {
		for(State s : states.getStates())
			s.setStateMarked(false);
	}
	
	/**
	 * This method removes any bad state markings in the transition system.
	 */
	
	public void makeAllStatesGood() {
		for(State s : states.getStates())
			s.setStateBad(false);
	}
	
	/**
	 * This method removes all bad states from the transition system.
	 */
	
	public void removeBadStates() {
		// Collect all the bad states
		ArrayList<State> badStates = new ArrayList<State>();
		for(State s : states.getStates())
			if(s.getStateBad()) badStates.add(s);
		// Remove the states from the state map
		for(State s : badStates) {
			states.removeState(s);
			removeInitialState(s.getStateName());
		}
		// Remove any state in the bad map from transitions
		transitions.removeStates(badStates);
	}

//---  Manipulations - Other   ----------------------------------------------------------------

	/**
	 * This method handles the toggling of a State object's status as Marked, reversing
	 * its current status to its opposite. (true -> false, false -> true). The State object
	 * is so defined by a provided String object representing its name.
	 * 
	 * @param stateName - String object representing the name of the State object to have its status as Marked be toggled.
	 * @return - Returns a Boolean object; true if the state is now marked, false if the state is now unmarked, or null if it did not exist.
	 */
	
	public Boolean toggleMarkedState(String stateName) {
		State curr = states.getState(stateName);
		if(curr == null)	
			return null;
		boolean isMarked = curr.getStateMarked();
		curr.setStateMarked(!isMarked);
		return !isMarked;
	}
	
	/**
	 * This method handles the toggling of a State object's status as bad, reversing
	 * its current status to its opposite. (true -> false, false -> true). The State is identified
	 * by passing in its String name.
	 * 
	 * @param stateName - String object representing the name of the State object to have its status as Marked be toggled.
	 * @return - Returns a Boolean object; true if the state is now bad, false if the state is now not bad, or null if it did not exist.
	 */
	
	public Boolean toggleBadState(String stateName) {
		State curr = states.getState(stateName);
		if(curr == null) return null;
		boolean isBad = curr.getStateBad();
		curr.setStateBad(!isBad);
		return !isBad;
	}
	
	/**
	 * This method handles the toggling of a State object's status as secret, reversing
	 * its current status to its opposite. (true -> false, false -> true). The State is identified
	 * by passing in its String name.
	 * 
	 * @param stateName - String object representing the name of the State object to have its status as secret be toggled.
	 * @return - Returns a Boolean object; true if the state is now secret, false if the state is now not secret, or null if it did not exist.
	 */
	
	public Boolean toggleSecretState(String stateName) {
		State curr = states.getState(stateName);
		if(curr == null) return null;
		boolean isSecret = curr.getStatePrivate();
		curr.setStatePrivate(!isSecret);
		return !isSecret;
	}
	
}
