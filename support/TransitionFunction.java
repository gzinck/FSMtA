package support;

import java.util.*;
import support.transition.Transition;

public class TransitionFunction<T extends Transition> {
	/** HashMap<String, ArrayList<Transition>> containing all the transitions from a given state with
	 * various events that are possible. */
	protected HashMap<State, ArrayList<T>> transitions;
	
	public TransitionFunction() {
		transitions = new HashMap<State, ArrayList<T>>();
	}
	
	public ArrayList<T> getTransitions(State state) {
		return transitions.get(state);
	}
	
	public void putTransitions(State state, ArrayList<T> inTransitions) {
		transitions.put(state, inTransitions);
	}
	
	public void addTransition(State state, T transition) {
		ArrayList<T> currT = transitions.get(state);
		currT.add(transition);
	}
	
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
