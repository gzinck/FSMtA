package support;

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
	
	/** String instance variable representing the name of the Event associated to this object*/
	private String event;
	/** String instance variable representing the name of the target State associated to this object*/
	private String state;
	
//---  Constructors   -------------------------------------------------------------------------
	
	/**
	 * Constructor for a Transition object, assigning the provided Event and State names to their
	 * corresponding instance variables.
	 * 
	 * @param inEvent - String object representing the name of the Event associated to this Transition
	 * @param inState - String object representing the name of the State being led to by the Event of this Transition
	 */
	
	public Transition(String inEvent, String inState) {
		event = inEvent;
		state = inState;
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	/**
	 * Getter method to access the name of the Event associated to this Transition object
	 * 
	 * @return - Returns a String object representing the name of the Event associated to this Transition object
	 */
	
	public String getTransitionEvent() {
		return event;
	}
	
	/**
	 * Getter method to access the name of the State associated to the Event associated to this Transition object
	 * 
	 * @return - Returns a String object representing the name of the State associated to the Event associated to this Transition object
	 */
	
	public String getTransitionState() {
		return state;
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	/**
	 * Setter method to assign a new String as the Event name associated to this Transition object
	 * 
	 * @param in - String object representing the new name of this Transition object's Event
	 */
	
	public void setTransitionEvent(String in) {
		event = in;
	}
	
	/**
	 * Setter method to assign a new String as the State name associated to the Event associated to this Transition object
	 * 
	 * @param in - String object representing the new name of this Transition object's Event's target State
	 */
	
	public void setTransitionState(String in) {
		state = in;
	}
	
}
