package fsm.attribute;

import support.transition.DetTransition;
import support.State;

/**
 * The Deterministic interface defines specialized methods which apply to all FSMs
 * which are Deterministic, such as setInitialState, among others.
 * 
 * This interface is a part of the fsm.attribute package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public interface Deterministic<T extends DetTransition> {
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	/**
	 * Gets the initial state for the deterministic FSM.
	 * 
	 * @return State object which is currently the initial state. 
	 */
	
	public abstract State getInitialState();
}
