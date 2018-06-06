package support.transition;

import support.State;
import support.event.Event;

/**
 * This class models a path that connects a State to another State in an FSM, storing an Event
 * and the Single State that it leads to. This is the base variation of a Transition, only
 * storing the one State, and thus modeling in a Deterministic manner.
 * 
 * This class is a part of the support.transition package.
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
	 * Getter method to access the State associated to the Event associated to this Transition object
	 * 
	 * @return - Returns a State object representing the State associated to the Event associated to this Transition object
	 */
	
	public State getTransitionState() {
		return state;
	}
	
	/**
	 * Getter method to access the String name of the State associated to the Event associated to this Transition object
	 * 
	 * @return - Returns a String representing the State's name.
	 */
	
	public String getTransitionStateName() {
		return state.getStateName();
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
	
	/**
	 * Removes the state from the transition object; or, if the state
	 * is the only item in the transition object (as it is for the base
	 * Transition object), it returns true to indicate that the transition
	 * object should be deleted entirely.
	 * 
	 * @param state State to delete from the transition object.
	 * @return True if the transition object has no states to which it points,
	 * else false.
	 */
	
	public boolean removeTransitionState(State inState) {
		if(inState.equals(state)) {
			state = null;
			return true;
		}
		return false;
	}
	
}
