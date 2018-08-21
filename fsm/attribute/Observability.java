package fsm.attribute;

import support.transition.Transition;
import fsm.DetObsContFSM;

/**
 * This interface defines the methods that any FSM object with the characteristic of Observable
 * (or, possessing of Events that are Observable and Unobservable) should implement.
 * 
 * It is used for ensuring the implementation of certain features in other classes.
 * 
 * This interface is a part of the fsm.attributes package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public interface Observability<T extends Transition> {

//---  Operations   ---------------------------------------------------------------------------

	
//---  Getter Methods   -----------------------------------------------------------------------
	
	/**
	 * This abstract method must be implemented by any class implementing the Observability interface.
	 * 
	 * Getter method that requests the state of a given Event's being Observable or not.
	 * 
	 * @param eventName - String object representing the name of the Event to request the status of.
	 * @return - Returns a Boolean object; True if the event is Observable, False if it is not, null if it does not exist.
	 */
	
	public abstract Boolean getEventObservability(String eventName);
	
	/**
	 * Getter method that requests the state of a given Event's being Observable by an Attacker or not.
	 * 
	 * @param eventName - String object representing the name of the Event to request the status of.
	 * @return - Returns a Boolean object; True if the event is observable by an attacker, False if not, null if there is no event.
	 */
	
	public abstract Boolean getEventAttackerObservability(String eventName);
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	/**
	 * This abstract method must be implemented by any class implementing the Observability interface.
	 * 
	 * Setter method that assigns the status of an Event's being Observable according to the provided boolean value and
	 * Event object.
	 * 
	 * @param eventName - String object representing the name of the Event of which to change its status as Observability.
	 * @param status - boolean value describing the new status of the Event to be adjusted in regards to Observability.
	 * @return - Returns a boolean value describing the result of the operation; false if the given event was not found, true if success.
	 */
	
	public abstract boolean setEventObservability(String eventName, boolean status);

	/**
	 * Setter method that assigns the status of an Event's being Observable by an Attacker according to the provided
	 * boolean value and Event object.
	 * 
	 * @param eventName - String object representing the name of the Event of which to change its status as Observable to the Attacker.
	 * @param status - boolean value describing the new status of the Event to be adjusted in regards to being Observable to the Attacker.
	 * @return - Returns a boolean value describing the result of the operation; false if the given Event was not found, true if successful.
	 */
	
	public abstract boolean setEventAttackerObservability(String eventName, boolean status);
	
}
