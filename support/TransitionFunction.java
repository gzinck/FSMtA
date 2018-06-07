package support;

import java.util.*;
import support.transition.Transition;

/**
 * This class models all Transitions in an FSM, storing States and an ArrayList<T> of Transitions as <Key, Value> pairs.
 * 
 * This class is a part of the support package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 * @param <T> - T being a class in the Transitions hierarchy from support.transition package
 */

public class TransitionFunction<T extends Transition> {
	
//---  Instance Variables   -------------------------------------------------------------------
	
	/** HashMap<String, ArrayList<Transition>> containing all the transitions from a given state with various events that are possible. */
	protected HashMap<State, ArrayList<T>> transitions;
	
//---  Constructors   -------------------------------------------------------------------------
	
	/**
	 * Constructor for TransitionFunction objects that initializes the HashMap<State, ArrayList<T>> for this object.
	 */
	
	public TransitionFunction() {
		transitions = new HashMap<State, ArrayList<T>>();
	}
	
//---  Operations   ---------------------------------------------------------------------------
		
	/**
	 * INCOMPLETE - NEED TO FINISH -
	 * This method converts the information stored in this TransitionFunction object into the dot-form
	 * representation for use with GraphViz. 
	 * 
	 * @return - Returns a String representing the dot-form version of the information stored by this TransitionFunction object.
	 */
	
	public String makeDotString() {
		StringBuilder sb = new StringBuilder();
		for(Map.Entry<State, ArrayList<T>> entry : transitions.entrySet()) {
			State firstState = entry.getKey();
			ArrayList<T> thisTransitions = entry.getValue();
			for(Transition aTransition : thisTransitions) {
				sb.append(aTransition.makeDotString(firstState));
			} // for aTransition
		} // for entry
		return sb.toString();
	}

//---  Getter Methods   -----------------------------------------------------------------------
	
	/**
	 * Getter method to acquire an ArrayList<T> of Transition objects associated to the provided State object 
	 * 
	 * @param state - State object in an FSM associated to the returned ArrayList<T> of Transition objects
	 * @return - Returns an ArrayList<T> of Transition objects that are associated to a defined State in an FSM
	 */
	
	public ArrayList<T> getTransitions(State state) {
		return transitions.get(state);
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	/**
	 * Setter method that assigns a new paired <State, ArrayList<T>> data set to the Transitions data structure,
	 * overwriting any previous entry for that State.
	 * 
	 * @param state - State object representing the Key in the stored <Key, Value> data structure, <State, ArrayList<T>>.
	 * @param inTransitions - ArrayList<T> of Transition objects to become the new Value stored in a <Key, Value> data structure.
	 */
	
	public void putTransitions(State state, ArrayList<T> inTransitions) {
		transitions.put(state, inTransitions);
	}
	
//---  Manipulations   ------------------------------------------------------------------------

	/**
	 * This method appends a new Transition object to the ArrayList<T> at the specified State Key in <Key, Value>
	 * data sets (<State, ArrayList<T>>), creating the entry if it doesn't yet exist.
	 * 
	 * @param state - State object representing the Key in the stored <Key, Value> data structure, <State, ArrayList<T>>.
	 * @param transition - <T extends Transition> object representing the new Transition to append to the existing ArrayList<T> at Key State in <Key, Value>. 
	 */
	
	public void addTransition(State state, T transition) {
		ArrayList<T> currT = transitions.get(state);
		if(currT == null) {
			transitions.put(state, new ArrayList<T>());
			currT = transitions.get(state);
		}
		currT.add(transition);
	}
	
	/**
	 * This method removes entries in the <State, ArrayList<T>> according to the provided State.
	 * 
	 * @param state - State object representing the Key-set to remove from the <State, ArrayList<T>> data set.
	 */
	
	public void removeState(State state) {
		transitions.remove(state);
		for(Map.Entry<State, ArrayList<T>> entry : transitions.entrySet()) {
			for(T transition : entry.getValue())
				if(transition.removeTransitionState(state))
					// Then remove the transition from existence
					entry.getValue().remove(transition);
		} // for every entry
	}

}
