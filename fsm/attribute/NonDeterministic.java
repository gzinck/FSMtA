package fsm.attribute;

import fsm.*;
import support.State;
import support.event.Event;
import support.transition.NonDetTransition;

/**
 * The NonDeterministic interface defines specialized methods which apply to all FSMs
 * which are non-deterministic, such as setInitialState, among others.
 * 
 * This interface is a part of the fsm.attribute package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public interface NonDeterministic<S extends State, T extends NonDetTransition<S, E>, E extends Event> {
	
//---  Operations   ---------------------------------------------------------------------------
	
	/**
	 * Determinizes a NonDeterministic FSM and returns the corresponding
	 * Deterministic FSM. It condenses the FSM into sets of states where
	 * only one transition of a given event name will leave any given state.
	 * 
	 * @return Deterministic FSM corresponding to the deterministic version
	 * of the FSM.
	 */
	
	public abstract <fsm extends FSM> fsm determinize();
}
