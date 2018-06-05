package support.transition;

import support.State;
import support.event.Event;

/**
 * This class models a path that connects a State to another State in an FSM, storing an Event
 * and the Single State that it leads to. This is the base variation of a Transition, only
 * storing the one State, and thus modeling in a Deterministic manner.
 * 
 * This class is a part of the support package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public class Transition {

//---  Instance Variables   -------------------------------------------------------------------
	
	/** Event instance variable representing the Event associated to this object*/
	private Event event;
	/** State instance variable representing the target State associated to this object*/
	private State state;
	
//---  Constructors   -------------------------------------------------------------------------
	
	/**
	 * Constructor for a Transition object, assigning the provided Event and State to their
	 * corresponding instance variables.
	 * 
	 * @param inEvent - Event object representing the Event associated to this Transition
	 * @param inState - State object representing the State being led to by the Event of this Transition
	 */
	
	public Transition(Event inEvent, State inState) {
		event = inEvent;
		state = inState;
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	/**
	 * Getter method to access the Event associated to this Transition object
	 * 
	 * @return - Returns a Event object representing the Event associated to this Transition object
	 */
	
	public Event getTransitionEvent() {
		return event;
	}
	
	/**
	 * Getter method to access the name of the State associated to the Event associated to this Transition object
	 * 
	 * @return - Returns a State object representing the State associated to the Event associated to this Transition object
	 */
	
	public State getTransitionState() {
		return state;
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	/**
	 * Setter method to assign a new Event object as the Event associated to this Transition object
	 * 
	 * @param in - Event object provided to replace the value stored previously in this Transition object
	 */
	
	public void setTransitionEvent(Event in) {
		event = in;
	}
	
	/**
	 * Setter method to assign a new String as the State name associated to the Event associated to this Transition object
	 * 
	 * @param in - State object representing the Transition object's Event's new target State
	 */
	
	public void setTransitionState(State in) {
		state = in;
	}
	
}
