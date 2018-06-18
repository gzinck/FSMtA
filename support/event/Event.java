package support.event;

/**
 * This class models an Event in an FSM, storing information about the Event's name.
 * 
 * This class is a part of the support.event package
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public class Event {
	
//---  Instance Variables   -------------------------------------------------------------------
	
	/** String instance variable representing the name of the Event*/
	private String id;
	
//---  Constructors   -------------------------------------------------------------------------
	
	/**
	 * Constructor for Event objects that does not assign an ID. This should not be used unless it is
	 * promptly renamed after called, it is simply used so that instantiation is possible in generic
	 * EventMap methods. 
	 */
	
	public Event() {
		id = "";
	}
	
	/**
	 * Constructor for Event objects that assigns the provided String object as the Name of this Event object
	 * 
	 * @param in - String object provided as the value for this Event object's name
	 */
	
	public Event(String in) {
		id = in;
	}
	
	/**
	 * Constructor for Event objects that uses the parameter Event object's information
	 * to contruct a new event object.
	 * 
	 * @param oldEvent - Event object to be copied.
	 */
	
	public Event(Event oldEvent) {
		id = oldEvent.id;
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
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	/**
	 * Getter method that requests the current name of the Event object
	 * 
	 * @return - Returns a String object representing the name of this Event object
	 */
	
	public String getEventName() {
		return id;
	}
	
	/**
	 * Gets a copy of the event.
	 * 
	 * @return Copied Event object.
	 */
	
	public <E extends Event> E copy() {
		// For use in other areas, and when Event is extended, this is
		// necessary.
		return (E)(new Event(this));
	}

	/**
	 * This method returns an integer value describing the type of Event during the
	 * creation of a visualization of an FSM.
	 * 
	 * @return - Returns an integer value representing the type of Event
	 */
	
	public int getEventType() {
		return 0;
	}
	
}
