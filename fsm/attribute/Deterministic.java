package fsm.attribute;

import support.State;
import support.event.Event;
import support.transition.DetTransition;

/**
 * The Deterministic interface defines specialized methods which apply to all FSMs
 * which are deterministic, such as setInitialState, among others.
 * 
 * This interface is a part of the fsm.attribute package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public interface Deterministic<S extends State, T extends DetTransition<S, E>, E extends Event> {
	
	/**
	 * Gets the initial state for the deterministic FSM.
	 * 
	 * @return State object which is currently the initial state. 
	 */
	public abstract S getInitialState();
}
