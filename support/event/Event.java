package support.event;

import support.attribute.EventControllability;
import support.attribute.EventObservability;

/**
 * This class models an Event in an FSM, storing information about the Event's name and
 * its status as Observable, Controllable, and whatever other features may be implemented
 * in the future.
 * 
 * This class is a part of the support.event package
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public class Event implements EventControllability, EventObservability{
	
//---  Instance Variables   -------------------------------------------------------------------
	
	/** String instance variable representing the name of the Event*/
	protected String id;
	/** boolean instance variable representing the status of this Event's being Controllable*/
	private boolean controllability;
	/** boolean instance variable representing the status of this Observable Event obejct's being Observable*/
	private boolean observability;
	
//---  Constructors   -------------------------------------------------------------------------
	
	/**
	 * Constructor for an Event object that does not assign an ID. It defaults to setting
	 * the event as observable & controllable. This should not be used unless it is promptly renamed after called,
	 * it is simply used so that instantiation is possible in generic EventMap methods.
	 */
	
	public Event() {
		id = "";
		controllability = true;
		observability = true;
	}
	
	/**
	 * Constructor for an Event object that assigns a defined String object to be its name,
	 * defaulting its status as Controllability and Observability to be true.
	 * 
	 * @param eventName - String object that represents the name of the Event object.
	 */
	
	public Event(String eventName) {
		id = eventName;
		controllability = true;
		observability = true;
	}
	
	/**
	 * Constructor for an Event object that assigns a defined String object and two boolean values to
	 * be its name and the statuses of the object's being Observable and Controllable.
	 * 
	 * @param eventName - String object that represents the name of the Event object.
	 * @param cont - boolean value that represents the status of the Event's being Controllable.
	 * @param obs - boolean value that represents the status of the Event's being Observable.
	 */
	
	public Event(String eventName, boolean cont, boolean obs) {
		id = eventName;
		controllability = cont;
		observability = obs;
	}
	
	/**
	 * Constructor for an Event object that uses the parameter Event object's information to construct a new event object.
	 * 
	 * @param oldEvent - Event object provided to have its attributes copied into the new Event object..
	 */
	
	public Event(Event oldEvent) {
		id = oldEvent.getEventName();
		controllability = oldEvent.getEventControllability();
		observability = oldEvent.getEventObservability();
	}
	
	/**
	 * Constructor for an Event that copies the data from two other events.
	 * Performs the AND operation on all the properties for the two events put in as parameters.
	 * 
	 * @param other1 - First Event object provided to copy data from into the calling Event object.
	 * @param other2 - Second Event object provided to copy data from into the calling Event object.
	 */

	public Event(Event other1, Event other2) {
		id = other1.id;
		controllability = (other1.getEventControllability() && other2.getEventControllability());
		observability = (other1.getEventObservability() && other2.getEventObservability());
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	/**
	 * Setter method that assigns the provided String value to be the new name of this Event object
	 * 
	 * @param in - String object provided as the new value for this Event object's name
	 */
	
	public void setEventName(String in) {
		id = in;
	}
	
	@Override
	public void setEventObservability(boolean obs) {
		observability = obs;
	}
	
	@Override
	public void setEventControllability(boolean con) {
		controllability = con;
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	/**
	 * Getter method that requests the current name of the Event object
	 * 
	 * @return - Returns a String object representing the name of this Event object
	 */
	
	public String getEventName() {
		return id;
	}

	public int getEventType() {
		return !controllability && !observability ? 3 : !controllability ? 2 : !observability ? 1 : 0;
	}
		
	@Override
	public boolean getEventObservability() {
		return observability;
	}
	
	@Override
	public boolean getEventControllability() {
		return controllability;
	}	

//---  Miscellaneous   ------------------------------------------------------------------------
	
	@Override
	public boolean equals(Object other) {
		if(other == null) return false;
		if(other instanceof Event)
		    if (this.id.equals(((Event)other).id)) return true;
		return false;
	}

}
