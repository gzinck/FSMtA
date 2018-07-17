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

//---  Operations   ---------------------------------------------------------------------------	
	
	/**
	 * Copies the data from another Event into this Event. Useful when creating a copy of only
	 * the information visible to a given (perhaps less sophisticated) Event.
	 * 
	 * @param other - Event object provided to copy data from into the calling Event object.
	 */
	
	public void copyDataFrom(Event other) {
		id = other.id;
		observability = ((other instanceof EventObservability) ? ((EventObservability)other).getEventObservability() : true);
		controllability = ((other instanceof EventControllability) ? ((EventControllability)other).getEventControllability() : true);
	}
	
	/**
	 * Copies the data from another Event into this Event. Useful when creating a copy of only
	 * the information visible to a given (perhaps less sophisticated) Event.
	 * Performs the AND operation on all the properties for the two events put in as parameters.
	 * 
	 * @param other1 - First Event object provided to copy data from into the calling Event object.
	 * @param other2 - Second Event object provided to copy data from into the calling Event object.
	 */

	public void copyDataFrom(Event other1, Event other2) {
		id = other1.id;
		boolean firstIsControllable = ((other1 instanceof EventControllability) ? ((EventControllability)other1).getEventControllability() : true);
		boolean secondIsControllable = ((other2 instanceof EventControllability) ? ((EventControllability)other2).getEventControllability() : true);
		controllability = (firstIsControllable && secondIsControllable);
		boolean firstIsObservable = ((other1 instanceof EventObservability) ? ((EventObservability)other1).getEventObservability() : true);
		boolean secondIsObservable = ((other2 instanceof EventObservability) ? ((EventObservability)other2).getEventObservability() : true);
		observability = (firstIsObservable && secondIsObservable);
	}
	
	/**
	 * Makes a new event that performs the AND operation on all the properties.
	 * If the current class is just Event, then there are no properties to copy anyways.
	 * 
	 * @param other - The Event object provided to combine its properties with the calling Event.
	 * @return - Returns a new Event extending object which has the AND of the properties in the provided and calling Event objects.
	 */

	public Event makeEventWith(Event other) {
		Event newEvent = new Event(this);
		newEvent.setEventControllability(this.controllability && other.getEventControllability());
		EventObservability obsOther = (EventObservability)other;
		newEvent.setEventObservability(this.observability && other.getEventObservability());
		return newEvent;
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
