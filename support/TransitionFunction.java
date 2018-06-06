package support;

import java.util.*;
import support.transition.Transition;
import support.*;

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
}
