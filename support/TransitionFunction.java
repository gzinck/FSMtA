package support;

import java.util.*;
import support.transition.Transition;
import support.event.Event;

/**
 * This class models all Transitions in an FSM, storing States and an ArrayList<<r>T> of Transitions as <<r>Key, Value> pairs.
 * 
 * This class is a part of the support package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 * @param <T> - T being a class in the Transitions hierarchy from support.transition package
 */

public class TransitionFunction<S extends State, T extends Transition<S, E>, E extends Event> {
	
//---  Instance Variables   -------------------------------------------------------------------
	
	/** HashMap<<r>String, ArrayList<Transition>> object containing all the transitions from a given state with various events that are possible. */
	protected HashMap<S, ArrayList<T>> transitions;
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
		transitions = new HashMap<S, ArrayList<T>>();
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
		for(Map.Entry<S, ArrayList<T>> entry : transitions.entrySet()) {
			State firstState = entry.getKey();
			ArrayList<T> thisTransitions = entry.getValue();
			for(T aTransition : thisTransitions) {
				sb.append(aTransition.makeDotString(firstState));
			} // for aTransition
		} // for entry
		return sb.toString();
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	/**
	 * Getter method to acquire an ArrayList<<r>T> of Transition objects associated to the provided State object 
	 * 
	 * @param state - State object in an FSM associated to the returned ArrayList<<r>T> of Transition objects
	 * @return - Returns an ArrayList<<r>T> of Transition objects that are associated to a defined State in an FSM
	 */
	
	public ArrayList<T> getTransitions(S state) {
		return transitions.get(state) != null ? transitions.get(state) : new ArrayList<T>();
	}
	
	/**
	 * Getter method to acquire a set of all states and its corresponding transition objects.
	 * 
	 * @return - Returns a Set of map entries with State objects and an ArrayList of the Transitions. (Set<<r>Map, Entry<<r>S, ArrayList<<r>T>>>)
	 */
	 
	public Set<Map.Entry<S, ArrayList<T>>> getAllTransitions() {
		return transitions.entrySet();
	}
	
	/**
	 * Getter method to generate a new object extending Transition<<s>S, E> through a method present in
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
		if(thisTransitions != null)
			for(T t : thisTransitions)
				if(t.getTransitionEvent().equals(event))
					return true;
		return false;
	}
	
	/**
	 * Getter method that retrieves the Transition States at a designated State that correspond to the provided Event.
	 * 
	 * @param state - State object whose Transitions are searched through.
	 * @param event - Event object provided to denote which State Transitions to return corresponding to the provided State object.
	 * @return - Returns an ArrayList<<r>S> of Transition States that the provided State leads to, or null if there are none.
	 */
	
	public ArrayList<S> getTransitionStates(State state, Event event) {
		ArrayList<T> thisTransitions = transitions.get(state);
		if(thisTransitions != null)
			for(T t : thisTransitions)
				if(t.getTransitionEvent().equals(event))
					return t.getTransitionStates();
		return null;
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	/**
	 * Setter method that assigns a new paired <<r>State, ArrayList<<r>T>> data set to the Transitions data structure,
	 * overwriting any previous entry for that State.
	 * 
	 * @param state - State object representing the Key in the stored <<r>Key, Value> data structure, <<r>State, ArrayList<<r>T>>.
	 * @param inTransitions - ArrayList<<r>T> of Transition objects to become the new Value stored in a <<r>Key, Value> data structure.
	 */
	
	public void putTransitions(S state, ArrayList<T> inTransitions) {
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
	
	public void addTransition(S state, T transition) {
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
	 * This method removes entries in the <<r>S, ArrayList<<r>T>> Map that correspond to the provided State.
	 * 
	 * @param state - State object representing the Key-set to remove from the <<r>S, ArrayList<<r>T>> data set.
	 */
	
	public void removeState(State state) {
		transitions.remove(state);
		for(Map.Entry<S, ArrayList<T>> entry : transitions.entrySet()) {
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
	
	public void removeStates(Collection<S> badStates) {
		// Remove the transitions from the bad states
		Iterator<S> itr = badStates.iterator();
		while(itr.hasNext()) transitions.remove(itr.next());
		
		// Remove the transitions that go to the bad states
		for(Map.Entry<S, ArrayList<T>> entry : transitions.entrySet()) {
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
