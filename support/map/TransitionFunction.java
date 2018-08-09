package support.map;

import support.transition.DetTransition;
import support.transition.Transition;
import support.attribute.*;
import support.Event;
import support.State;
import java.util.*;

/**
 * This class models all Transitions in an FSM, storing States and an ArrayList<<r>T> of Transitions as <<r>Key, Value> pairs.
 * 
 * This class is a part of the support package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 * @param <T> - T being a class in the Transitions hierarchy from support.transition package
 */

public class TransitionFunction <T extends Transition>{
	
//---  Instance Variables   -------------------------------------------------------------------
	
	/** HashMap<<r>String, ArrayList<Transition>> object containing all the transitions from a given state with various events that are possible. */
	protected HashMap<State, ArrayList<T>> transitions;
	/** T object extending Transition<<r>S, E> used for reference to the object's methods in a non-static way.*/
	private T dummyTransition;
	
//---  Constructors   -------------------------------------------------------------------------
	
	/**
	 * Constructor for TransitionFunction objects that initializes the HashMap<State, ArrayList<T>> for this object, and
	 * accepts an object extending Transition<<r>S, E> for use as an instance variable.
	 * 
	 * @param obj - Object of the type T that extends Transition<<r>S, E> to provide to the TransitionFunction object.
	 */
	
	public TransitionFunction(T obj) {
		transitions = new HashMap<State, ArrayList<T>>();
		dummyTransition = obj;
	}
	
//---  Operations   ---------------------------------------------------------------------------
		
	/**
	 * This method converts the information stored in this TransitionFunction object into the dot-form
	 * representation for use with GraphViz. 
	 * 
	 * @return - Returns a String object representing the dot-form version of the information stored by this TransitionFunction object.
	 */
	
	public String makeDotString() {
		StringBuilder sb = new StringBuilder();
		for(Map.Entry<State, ArrayList<T>> entry : transitions.entrySet()) {
			State firstState = entry.getKey();
			ArrayList<T> thisTransitions = entry.getValue();
			for(T aTransition : thisTransitions) {
				sb.append(aTransition.makeDotString(firstState));
			} // for aTransition
		} // for entry
		return sb.toString();
	}
	
	/**
	 * This method converts the information stored in this TransitionFunction object into the dot-form
	 * representation for use with GraphViz. It excludes the transitions which are present in the other
	 * transition function passed as a parameter.
	 * 
	 * @param other - TransitionFunction which uses the same FSM's states mapping to different things.
	 * @return - Returns a String object representing the dot-form version of the information stored by
	 * this TransitionFunction object, excluding any transitions in the other transition function.
	 */
	
	public String makeDotStringExcluding(TransitionFunction<T> other) {
		StringBuilder sb = new StringBuilder();
		for(Map.Entry<State, ArrayList<T>> entry : transitions.entrySet()) {
			State firstState = entry.getKey();
			ArrayList<T> thisTransitions = entry.getValue();
			for(T aTransition : thisTransitions) {
				ArrayList<State> otherTransitionStates = other.getTransitionStates(firstState, aTransition.getTransitionEvent());
				if(otherTransitionStates == null || otherTransitionStates.size() == 0) {
					if(aTransition instanceof DetTransition)
						sb.append(((DetTransition)aTransition).makeDotStringMayTransition(firstState)); // only append if the transition does not exist in other.
					else
						sb.append(aTransition.makeDotString(firstState));
				} // if
			} // for aTransition
		} // for entry
		return sb.toString();
	}
	
	/**
	 * This gets the epsilon reaches of all each state, mapping the state to a set
	 * of states which are reachable with unobservable events.
	 * 
	 * @param fsmStates Collection of states which exists in the FSM.
	 * @return Hashmap mapping each state to a hashset of states (which are all reachable
	 */

	public HashMap<State, HashSet<State>> getEpsilonReaches(Collection<State> fsmStates) {
		HashMap<State, HashSet<State>> epsilonReach = new HashMap<State, HashSet<State>>();	//Maps a State to all States it is attached to
		for(State s : fsmStates) {						//For all States in the FSM:
			HashSet<State> thisSet = new HashSet<State>();			//Keeps track of all States attached to this State
			thisSet.add(s);										//Add the original State as one in the group
			LinkedList<State> queue = new LinkedList<State>();		//Queue to process all States connected via Unobservable Events
			queue.add(s);											//First Queue entry is the original State
			HashSet<State> visited = new HashSet<State>();			//Keeps track of revisited States
			// Go through all the states connected by unobservable events
			while(!queue.isEmpty()) {
				State top = queue.poll();					//Get the next State
				if(visited.contains(top))					//If already processed, don't re-process the State
					continue;
				visited.add(top);							//Mark it as visited
				for(T t : this.getTransitions(top)) {	//Process all the State's Transitions
					// If it's an unobservable event, go through all transition states
					if(!((EventObservability)t.getTransitionEvent()).getEventObservability()) 
						for(State sr : t.getTransitionStates()) {
							if(!thisSet.contains(sr)) {
								thisSet.add(sr); //If the Event is unobservable and has not yet been seen, add the State
								queue.add(sr); //As the State is a part of the new aggregated State, check its transitions too
						} // if it doesn't contain it
					} // if we have an unobservable event, go through all the transition states
				} // for each transition
			} // while queue not empty
			epsilonReach.put(s, thisSet);
		} // for each state
		return epsilonReach;
	} // getEpsilonReaches(Collection<S>)
	
	/**
	 * Method to be called on the may transition function with a must transition function passed
	 * in (for all the transitions which must occur). It returns all the states that have transitions
	 * that exist in the mustTransitionFunction but not in the calling function, making the state
	 * inconsistent with the definition of a ModalSpecification.
	 * 
	 * @param mustTransitionFunction - TransitionFunction with the transitions that must occur for
	 * the TransitionSystem.
	 * @return - HashSet of all the States which were found to be inconsistent.
	 */

	public HashSet<String> getInconsistentStates(TransitionFunction<T> mustTransitionFunction) {
		// This is the must transitions; we want all the states where there is no transition in
		// other that corresponds to the one in this.
		HashSet<String> badStates = new HashSet<String>();
		
		for(State fromState : mustTransitionFunction.transitions.keySet()) {
			// Get the corresponding sorted transitions from each
			ArrayList<T> mustTransitions = mustTransitionFunction.getSortedTransitions(fromState);
			ArrayList<T> mayTransitions = this.getSortedTransitions(fromState);
			int mustIndex = 0, mayIndex = 0;
			while(mustIndex < mustTransitions.size() && mayIndex < mayTransitions.size()) {
				// Find the matching may transition
				while(mayIndex < mayTransitions.size() && !mustTransitions.get(mustIndex).equals(mayTransitions.get(mayIndex))) mayIndex++;
				// If there is no corresponding may transition, we have a bad state.
				if(mayIndex >= mayTransitions.size()) badStates.add(fromState.getStateName());
				mustIndex++;
			}
			if(mustIndex < mustTransitions.size()) badStates.add(fromState.getStateName());
		}
		return badStates;
	} // getInconsistentStates(TransitionFunction<T>)
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	/**
	 * Getter method to acquire an ArrayList<<r>T> of Transition objects associated to the provided State object 
	 * 
	 * @param state - State object in an FSM associated to the returned ArrayList<<r>T> of Transition objects
	 * @return - Returns an ArrayList<<r>T> of Transition objects that are associated to a defined State in an FSM
	 */
	
	public ArrayList<T> getTransitions(State state) {
		return transitions.get(state) != null ? transitions.get(state) : new ArrayList<T>();
	}
	
	/**
	 * Getter method for all the transitions from a given State object, sorted in order of the event names associated
	 * 
	 * @param state - State object in an FSM associated to the returned ArrayList<<r>T> of Transition objects
	 * @return - Returns an ArrayList<<r>T> of sorted Transition objects that are associated to a defined State in an FSM
	 */

	public ArrayList<T> getSortedTransitions(State state) {
		ArrayList<T> t = transitions.get(state);
		if(t != null) {
			Collections.sort(t);
			return t;
		} else return new ArrayList<T>();
	}
	
	/**
	 * Getter method to acquire a set of all states and its corresponding transition objects.
	 * 
	 * @return - Returns a Set of map entries with State objects and an ArrayList of the Transitions. (Set<<r>Map, Entry<<r>S, ArrayList<<r>T>>>)
	 */
	 
	public Set<Map.Entry<State, ArrayList<T>>> getAllTransitions() {
		return transitions.entrySet();
	}
	
	/**
	 * Getter method that returns a Collection of State objects, those being each of the stored
	 * States leading to an ArrayList of Transitions associated to that leading State.
	 * 
	 * @return - Returns a Collection<<r>State> object containing all the States in this TransitionFunction which have Transitions.
	 */
	
	public Collection<State> getStates(){
		return transitions.keySet();
	}
	
	/**
	 * Getter method to generate a new object extending Transition<<r>S, E> through a method present in
	 * the Transition interface that creates a new object of that object 'T' for creating Transition
	 * objects corresponding to the generic classes used to produce this TransitionFunction object.
	 * 
	 * @return - Returns an object extending Transition<<s>S, E> that can be assigned new values.
	 */
	
	public T getEmptyTransition() {
		return dummyTransition.generateTransition();
	}
	
	/**
	 * Getter method that retrieves if a certain event exists at a certain state.
	 * 
	 * @param state - State object in whose Transitions to search for an Event in.
	 * @param event - Event object to search for in the Transitions of the provided State object.
	 * @return - Returns a boolean value; true if the State has the provided Event in one of its Transitions, false otherwise.
	 */
	
	public boolean eventExists(State state, Event event) {
		ArrayList<T> thisTransitions = transitions.get(state);
		if(thisTransitions != null) {
//			System.out.println("In state " + state.getStateName() + ", we're looking for " + event.getEventName() + " in " + thisTransitions.toString());
			for(T t : thisTransitions)
				if(t.getTransitionEvent().equals(event))
					return true;
		}
		return false;
	}
	
	/**
	 * Getter method that retrieves the Transition States at a designated State that correspond to the provided Event.
	 * 
	 * @param state - State object whose Transitions are searched through.
	 * @param event - Event object provided to denote which State Transitions to return corresponding to the provided State object.
	 * @return - Returns an ArrayList<<r>S> of Transition States that the provided State leads to, or null if there are none.
	 */
	
	public ArrayList<State> getTransitionStates(State state, Event event) {
		ArrayList<T> thisTransitions = transitions.get(state);
		if(thisTransitions != null)
			for(T t : thisTransitions)
				if(t.getTransitionEvent().equals(event))
					return t.getTransitionStates();
		return null;
	}
	
	/**
	 * This method searches among the Transitions stored by this TransitionFunction object
	 * for the presence of a Transition provided as an argument for reference. (Does this
	 * TransitionFunction contain the defined Transition?)
	 * 
	 * @param reference - State object whose associated Transitions in this TransitionFunction object are searched through.
	 * @param transition - Transition extending object that is to be searched for in the Transitions stored by this TransitionFunction object.
	 * @return - Returns a boolean value representing the result of this search; true if the Transition exists, false otherwise.
	 */
	
	public boolean contains(State reference, T transition) {
		for(T t : getTransitions(reference)) {
			if(t.equals(transition))
				return true;
		}
		return false;
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	/**
	 * Setter method that assigns a new paired <<r>State, ArrayList<<r>T>> data set to the Transitions data structure,
	 * overwriting any previous entry for that State.
	 * 
	 * @param state - State object representing the Key in the stored <<r>Key, Value> data structure, <<r>State, ArrayList<<r>T>>.
	 * @param inTransitions - ArrayList<<r>T> of Transition objects to become the new Value stored in a <<r>Key, Value> data structure.
	 */
	
	public void putTransitions(State state, ArrayList<T> inTransitions) {
		transitions.put(state, inTransitions);
	}
	
//---  Manipulations   ------------------------------------------------------------------------
	
	/**
	 * This method appends a new Transition object to the ArrayList<<r>T> at the specified State key,
	 * creating the entry if it does not yet exist.
	 * 
	 * @param state - State object representing the Key in the stored <<r>Key, Value> data structure, <<r>State, ArrayList<<r>T>>.
	 * @param transition - <<r>T extends Transition> object representing the new Transition to append to the existing ArrayList<<r>T> at Key State in <<r>Key, Value>. 
	 */
	
	public void addTransition(State state, T transition) {
		ArrayList<T> currT = transitions.get(state);
		if(currT == null) {
			transitions.put(state, new ArrayList<T>());
			currT = transitions.get(state);
		}
		boolean on = true;
		for(T t : currT) {
			if(t.getTransitionEvent().equals(transition.getTransitionEvent()) && t.getTransitionStates().equals(transition.getTransitionStates())) {
				on = false;
				break;
			}
		}
		if(on)
			currT.add(transition);
	}
	
	/**
	 * This method adds a new Transition to the TransitionFunction in a format defined as State1, Event, State2;
	 * State1 leading to State2 via the Event. It creates a new Transition object and appends it to the
	 * ArrayList associated to the leading State in the Transition.
	 * 
	 * @param inState - State object representing the leading State1 in the format State1 -> State2 via Event.
	 * @param event - Event object representing the Event in the format State1 -> State2 via Event.
	 * @param outState - State object representing the target State2 in the format State1 -> State2 via Event.
	 */
	
	public void addTransitionState(State inState, Event event, State outState) {
		ArrayList<T> currT = transitions.get(inState);
		if(currT == null) {
			transitions.put(inState, new ArrayList<T>());
			currT = transitions.get(inState);
		}
		boolean did = false;
		for(T t : currT) {
			if(t.getTransitionEvent().equals(event)) {
				t.addTransitionState(outState);
				did = true;
				break;
			}
		}
		if(!did) {
			T trans = getEmptyTransition();
			trans.setTransitionEvent(event);
			trans.addTransitionState(outState);
			currT.add(trans);
		}
	}
	
	/**
	 * This method removes entries in the <<r>S, ArrayList<<r>T>> Map that correspond to the provided State.
	 * 
	 * @param state - State object representing the Key-set to remove from the <<r>S, ArrayList<<r>T>> data set.
	 */
	
	public void removeState(State state) {
		transitions.remove(state);
		for(Map.Entry<State, ArrayList<T>> entry : transitions.entrySet()) {
			ArrayList<T> tToRemove = new ArrayList<T>();
			for(T transition : entry.getValue())
				if(transition.removeTransitionState(state))
					tToRemove.add(transition);
			entry.getValue().removeAll(tToRemove);
		} // for every entry
	}
	
	/**
	 * This method removes entries in the transition function according to the provided Collection of states to remove.
	 * 
	 * @param badStates - Collections<<r>S> of States which are bad and must be removed from the TransitionFunction.
	 */
	
	public void removeStates(Collection<State> badStates) {
		// Remove the transitions from the bad states
		Iterator<State> itr = badStates.iterator();
		while(itr.hasNext()) transitions.remove(itr.next());
		
		// Remove the transitions that go to the bad states
		for(Map.Entry<State, ArrayList<T>> entry : transitions.entrySet()) {
			ArrayList<T> tToRemove = new ArrayList<T>();
			for(T transition : entry.getValue())
				if(transition.removeTransitionStates(badStates))
					tToRemove.add(transition);
			entry.getValue().removeAll(tToRemove);
		}
	}
	
	/**
	 * Removes the Transition in this TransitionFunction that corresponds to the provided values in 
	 * the form: State1 leading to State2 via an Event.
	 * 
	 * @param stateFrom - State object that the transition starts from.
	 * @param event - Event object associated with the transition.
	 * @param stateTo - State object that the transition ends at.
	 * @return - Returns a boolean value; true if the transition existed and was removed; false otherwise.
	 */
	
	public boolean removeTransition(State stateFrom, Event event, State stateTo) {
		ArrayList<T> thisTransitions = transitions.get(stateFrom);
		for(T transition : thisTransitions) {
			if(transition.getTransitionEvent().equals(event)) {
				if(transition.stateExists(stateTo)) {
					boolean shouldDeleteTransition = transition.removeTransitionState(stateTo);
					if(shouldDeleteTransition) thisTransitions.remove(transition);
					return true;
				}
			}
		}
		return false;
	}

}
