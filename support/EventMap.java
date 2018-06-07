package support;

import java.util.*;
import support.event.Event;

/**
 * Wrapper for a HashMap allowing the user to search for an
 * event object using the corresponding name. It also holds
 * nice functions for working with events.
 * 
 * This class is a part of the support package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 *
 * @param <E> Class that extends Event.
 */
public class EventMap<E extends Event> {
	
	/** HashMap mapping String names of events to their corresponding
	 * Event objects. */
	private HashMap<String, E> events;
	
	/**
	 * Creates a new HashMap for searching for events.
	 */
	public EventMap() {
		events = new HashMap<String, E>();
	}
	
	/**
	 * Adds an event to the map which is mapped to the name
	 * stored within the Event object already.
	 * 
	 * @param newEvent Event object (this object will NOT be
	 * copied).
	 */
	public void addEvent(E newEvent) {
		events.put(newEvent.getEventName(), newEvent);
	}
	
	/**
	 * Gets an event using the event's String name to identify it.
	 * 
	 * @param eventName String representing the event.
	 * @return The corresponding event.
	 */
	public E getEvent(String eventName) {
		return events.get(eventName);
	}
	
	/**
	 * Gets an event using an event object from another FSM.
	 * 
	 * @param event Event object from another FSM (but not necessarily
	 * the current FSM).
	 * @return The corresponding Event object from the current FSM, which
	 * has the same event name String as the input event.
	 */
	public E getEvent(E event) {
		String name = event.getEventName();
		return events.get(name);
	}
	
	/**
	 * Removes the Event from the mapping.
	 * 
	 * @param event Event object to remove.
	 */
	public void removeEvent(E event) {
		events.remove(event.getEventName());
	}
	
	/**
	 * Removes the Event corresponding to the String eventName
	 * from the mapping.
	 * 
	 * @param eventName String name of the event to remove.
	 */
	public void removeEvent(String eventName) {
		events.remove(eventName);
	}
	
	/**
	 * Renames the Event corresponding to the oldName String with the
	 * newName String.
	 * 
	 * @param oldName String representing the name of the Event.
	 * @param newName String representing the desired new name of the Event.
	 */
	public void renameEvent(String oldName, String newName) {
		E event = events.get(oldName);
		event.setEventName(newName);
		events.remove(oldName);
		events.put(newName, event);
	}
	
	/**
	 * Checks if an event exists in the map.
	 * 
	 * @param eventName String representing the Event to
	 * look for.
	 * @return True iff the Event exists in the map.
	 */
	public boolean eventExists(String eventName) {
		return events.containsKey(eventName);
	}
	
	/**
	 * Gets all the events and returns them as a Collection.
	 * 
	 * @return Collection of Event objects.
	 */
	public Collection<E> getEvents() {
		return events.values();
	}
	
}
