package fsm.attribute;

import support.transition.Transition;
import support.event.Event;
import support.State;

/**
 * This interface defines the methods that any FSM with the characteristic of Controllable
 * (or, possessing Events that are Controllable and Uncontrollable) should implement.
 * 
 * This interface is a part of the fsm.attribute package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public interface Controllability<S extends State, T extends Transition<S, E>, E extends Event>{

//---  Getter Methods   -----------------------------------------------------------------------
	
	/**
	 * This abstract method must be implemented by any class implementing the Controllability interface.
	 * 
	 * Getter method that requests the status of the defined Event's being Controllable, returning
	 * true if it is Controllable, false if it is not, and null if the Event did not exist.
	 * 
	 * @param event - <E extends Event> object representing the Event whose status of Controllability is being checked.
	 * @return - Returns a Boolean object representing the result of this method's query.
	 */
	
	public abstract Boolean getEventControllability(E event);
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	/**
	 * This abstract method must be implemented by any class implementing the Controllability interface.
	 * 
	 * Setter method that assigns a new value to the defined Event object's status of being Controllable.
	 * 
	 * @param event - <E extends Event> object representing the Event whose status of Controllability is being edited.
	 * @param value - boolean value representing the new value to assign to the defined Event object.
	 */
	
	public abstract void setEventControllability(E event, boolean value);
	
}
