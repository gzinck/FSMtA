package fsm.attribute;

import support.transition.Transition;
import support.event.Event;

import java.util.HashMap;
import java.util.HashSet;

import fsm.DetObsContFSM;
import fsm.FSM;
import support.DisabledEvents;
import support.State;

/**
 * This interface defines the methods that any FSM with the characteristic of Controllable
 * (or, possessing Events that are Controllable or Uncontrollable) should implement.
 * 
 * This interface is a part of the fsm.attribute package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public interface Controllability<S extends State, T extends Transition<S, E>, E extends Event>{

//---  Operations   -----------------------------------------------------------------------
	/**
	 * This creates a new FSM which represents the supremal controllable sublanguage
	 * of the calling FSM with respect to the language of the parameter FSM, other.
	 * This factors in both the observability and controllability of different events in the
	 * FSMs.
	 * 
	 * @param other The FSM representing the language to which the the calling FSM must be
	 * controllable. 
	 * @return The FSM representing the supremal controllable sublanguage of the calling FSM
	 * with respect to the parameter FSM.
	 */
	public abstract FSM<S, T, E> getSupremalControllableSublanguage(FSM other);
	
	/**
	 * Recursively goes through states and examines what should be disabled. The results of what states
	 * to disable and events to disable (at enabled states) are stored in the disabledMap HashMap using
	 * DisabledEvents objects.
	 * 
	 * @param curr State in the current FSM that is being evaluated for disabled events.
	 * @param otherFSM FSM representing the desired maximum specification for the final FSM product.
	 * @param visitedStates HashSet of state names (Strings) which indicate which states have already
	 * been recursed through (thereby preventing loops). Because of the fact that loops are not allowed
	 * (which in turn allows this to not enter an infinite loop), this whole process must be repeated for
	 * every single state in the FSM (aside from ones we already have a guarantee are disabled).
	 * @param disabledMap Results of what to disable at each state.
	 * @return A DisabledEvents object with what needs to be disabled at any given state.
	 */
	public abstract DisabledEvents getDisabledEvents(State curr, FSM otherFSM, HashSet<String> visitedStates, HashMap<String, DisabledEvents> disabledMap);
	
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
	 * @return - Returns a boolean value representing the result of this process; false if the event was not found, true otherwise.
	 */
	
	public abstract void setEventControllability(String eventName, boolean value);
	
}
