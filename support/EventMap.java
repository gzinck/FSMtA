package support;

import java.util.*;
import support.event.Event;

/**
 * This class is a wrapper for a HashMap allowing the user to search for an event object using the corresponding name.
 * It also holds nice functions for working with events.
 * 
 * This class is a part of the support package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 * @param <E> Classes that extends Event.
 */

public class EventMap<E extends Event> {
	
//---  Instance Variables   -------------------------------------------------------------------
	
	/** HashMap mapping String names of events to their corresponding Event objects. */
	private HashMap<String, E> events;
	
//---  Constructors   -------------------------------------------------------------------------
	
	/**
	 * Constructor for an EventMap that initializes the events HashMap<String, E>.
	 */
	
	public EventMap() {
		events = new HashMap<String, E>();
	}

//---  Operations   ---------------------------------------------------------------------------
	
	/**
	 * Renames the Event corresponding to the oldName String with the newName String.
	 * 
	 * @param oldName - String representing the name of the Event.
	 * @param newName - String representing the desired new name of the Event.
	 */
	
	public void renameEvent(String oldName, String newName) {
		E event = events.get(oldName);
		event.setEventName(newName);
		events.remove(oldName);
		events.put(newName, event);
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	/**
	 * Gets an event using the event's String name to identify it.
	 * 
	 * @param eventName - String object representing an Event by its id.
	 * @return - Returns the event corresponding to the provided String object.
	 */
	
	public E getEvent(String eventName) {
		return events.get(eventName);
	}

	/**
	 * Gets an event using an event object from another FSM.
	 * 
	 * @param event - Event object from another FSM (but not necessarily the current FSM).
	 * @return - Returns the corresponding Event object from the current FSM, which has the same event name String as the input event.
	 */
	
	public E getEvent(E event) {
		String name = event.getEventName();
		return events.get(name);
	}
	
	/**
	 * Gets all the events and returns them as a Collection.
	 * 
	 * @return - Returns a Collection of Event objects.
	 */
	
	public Collection<E> getEvents() {
		return events.values();
	}
	
	/**
	 * Checks if an event exists in the map.
	 * 
	 * @param eventName - String representing the Event to look for.
	 * @return - Returns a boolean value; true if the Event exists in the map, false otherwise.
	 */
	
	public boolean eventExists(String eventName) {
		return events.containsKey(eventName);
	}

//---  Manipulations   ------------------------------------------------------------------------
	
	/**
	 * Adds an event to the map which is mapped to the name stored within the Event object already.
	 * It creates a copy to put into the map, unless the event already exists in the map (in
	 * which case, nothing happens). The new event is returned, or the event that was already
	 * in the map is.
	 * 
	 * @param oldEvent - Event object provided as a new entry in the HashMap<String, E>; the Event object is not copied.
	 * @return Event object which corresponds to the oldEvent's id, and which is currently
	 * stored in the EventMap.
	 */
	
	public E addEvent(E oldEvent) {
		String eventName = oldEvent.getEventName();
		if(events.containsKey(eventName))
			return events.get(eventName);
		E newEvent = oldEvent.copy();
		events.put(eventName, newEvent);
		return newEvent;
	}

	/**
	 * Removes the Event from the mapping.
	 * 
	 * @param event - Event object to remove from the HashMap<String, E> events object.
	 */
	
	public void removeEvent(E event) {
		events.remove(event.getEventName());
	}
	
	/**
	 * Removes the Event corresponding to the provided String from the mapping if present.
	 * 
	 * @param eventName - String object representing the name of the event to remove.
	 */
	
	public void removeEvent(String eventName) {
		events.remove(eventName);
	}

}
