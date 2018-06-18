package support.event;

import support.event.Event;
import support.attribute.EventControllability;

/**
 * This class models a Controllable Event in an FSM, building on the base Event class
 * to also store information about the Event's being Controllable. 
 * 
 * This class is a part of the support.event package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public class ControllableEvent extends Event implements EventControllability{

//---  Instance Variables   -------------------------------------------------------------------
	
	/** boolean instance variable representing the status of this Event's being Controllable*/
	private boolean controllability;
	
//---  Constructors   -------------------------------------------------------------------------
	
	/**
	 * Constructor for a Controllable Event object that assigns the defined String object
	 * to be the Controllable Event object's name, and defaults the status of Controllability
	 * as false. (Is Controllable)
	 * 
	 * @param eventName - String object provided as the name of this Controllable Event object
	 */
	
	public ControllableEvent(String eventName) {
		super(eventName);
		controllability = true;
	}
	
	/**
	 * Constructor for a Controllable Event object that assigns the defined String object and boolean value
	 * to be the Controllable Event object's name and status of the object's Controllability.
	 * 
	 * @param eventName - String object provided as the name of this Controllable Event object
	 * @param control - boolean value provided as the status of the Controllable Event object's being Controllable
	 */
	
	public ControllableEvent(String eventName, boolean control) {
		super(eventName);
		controllability = control;
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	@Override
	public void setEventControllability(boolean newControl) {
		controllability = newControl;
	}
	
//---  Getter Methods   -----------------------------------------------------------------------

	@Override
	public boolean getEventControllability() {
		return controllability;
	}
	
	public int getEventType() {
		return controllability ? 2 : 0;
	}
}
