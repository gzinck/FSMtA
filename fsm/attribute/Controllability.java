package fsm.attribute;

import support.transition.Transition;
import support.DisabledEvents;
import java.util.HashMap;
import java.util.HashSet;
import support.State;
import fsm.FSM;

/**
 * This interface defines the methods that any FSM with the characteristic of Controllable
 * (or, possessing Events that are Controllable or Uncontrollable) should implement.
 * 
 * It is used for ensuring the implementation of certain features in other classes.
 * 
 * This interface is a part of the fsm.attribute package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public interface Controllability<T extends Transition> {

//---  Operations   -----------------------------------------------------------------------
	
	/**
	 * This abstract method must be implemented by any class implementing the Controllability interface.
	 * 
	 * This creates a new FSM which represents the supremal controllable sublanguage of the calling
	 * FSM with respect to the language of the parameter FSM, 'other'.
	 * 
	 * This factors in both the observability and controllability of different events in the FSMs.
	 * 
	 * @param other - The FSM object representing the language to which the the calling FSM must be controllable. 
	 * @return - Returns the FSM object representing the supremal controllable sublanguage of the calling FSM with
	 * respect to the parameter FSM.
	 */
	
	public abstract <T1 extends Transition> FSM<T> getSupremalControllableSublanguage(FSM<T1> other);
	
	/**
	 * This abstract method must be implemented by any class implementing the Controllability interface.
	 * 
	 * Recursively goes through States and examines what should be disabled. The results of what states
	 * to disable and events to disable (at enabled states) are stored in the disabledMap HashMap using
	 * DisabledEvents objects.
	 * 
	 * @param curr - State object in the current FSM that is being evaluated for disabled events.
	 * @param otherFSM - FSM object representing the desired maximum specification for the final FSM product.
	 * @param visitedStates - HashSet<<r>String> object containing State names (Strings) which indicate which states have already
	 * been recursed through (thereby preventing loops). Because of the fact that loops are not allowed
	 * (which in turn allows this to not enter an infinite loop), this whole process must be repeated for
	 * every single state in the FSM (aside from ones we already have a guarantee are disabled).
	 * @param disabledMap - HashMap<<r>String, DisabledEvents> object representing the results of what to disable at each state.
	 * @return - Returns a DisabledEvents object describing what needs to be disabled at any given state.
	 */
	
	public abstract <T1 extends Transition> DisabledEvents getDisabledEvents(State curr, FSM<T1> otherFSM, HashSet<String> visitedStates, HashMap<String, DisabledEvents> disabledMap);
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	/**
	 * This abstract method must be implemented by any class implementing the Controllability interface.
	 * 
	 * Getter method that requests the status of the defined Event's being Controllable, returning
	 * true if it is Controllable, false if it is not, and null if the Event did not exist.
	 * 
	 * @param event - String object representing the Event whose status of Controllability is being checked.
	 * @return - Returns a Boolean object representing the result of this method's query; null if the object did not exist, true/false representing the result of the query
	 */
	
	public abstract Boolean getEventControllability(String eventName);
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	/**
	 * This abstract method must be implemented by any class implementing the Controllability interface.
	 * 
	 * Setter method that assigns a new value to the defined Event object's status of being Controllable.
	 * 
	 * @param event - String object representing the Event whose status of Controllability is being edited.
	 * @param value - boolean value representing the new value to assign to the defined Event object.
	 */
	
	public abstract void setEventControllability(String eventName, boolean value);
	
}
