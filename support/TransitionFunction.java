package support;

import java.util.*;
import support.transition.Transition;

/**
 * 
 * 
 * @author Mac Clevinger and Graeme Zinck
 * @param <T>
 */

public class TransitionFunction<T extends Transition> {
	
//---  Instance Variables   -------------------------------------------------------------------
	
	/** HashMap<String, ArrayList<Transition>> containing all the transitions from a given state with various events that are possible. */
	protected HashMap<State, ArrayList<T>> transitions;
	
//---  Constructors   -------------------------------------------------------------------------
	
	/**
	 * Constructor for TransitionFunction objects
	 */
	
	public TransitionFunction() {
		transitions = new HashMap<State, ArrayList<T>>();
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	/**
	 * 
	 * @param state
	 * @return
	 */
	
	public ArrayList<T> getTransitions(State state) {
		return transitions.get(state);
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	/**
	 * 
	 * @param state
	 * @param inTransitions
	 */
	
	public void putTransitions(State state, ArrayList<T> inTransitions) {
		transitions.put(state, inTransitions);
	}
	
//---  Manipulations   ------------------------------------------------------------------------

	/**
	 * 
	 * @param state
	 * @param transition
	 */
	
	public void addTransition(State state, T transition) {
		ArrayList<T> currT = transitions.get(state);
		currT.add(transition);
	}
	
	/**
	 * 
	 * @param state
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
	
	/**
	 * Makes a String object which has the dot representation of the transitions, which
	 * can be used for sending an FSM to GraphViz.
	 * 
	 * @return String containing the dot representation of the transitions.
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
}
