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
	
	/** HashMap<<r>String, E extends Event> mapping String names of events to their corresponding Event objects. */
	private HashMap<String, E> events;
	/** Class<<r>E extends Event> object that holds the precise class of the generic Event class. */
	private Class<E> eventClass;
	
//---  Constructors   -------------------------------------------------------------------------
	
	/**
	 * Constructor for an EventMap that initializes the events HashMap<<r>String, E>.
	 * 
	 * @param inClass - Class<<r>E extends Event> object representing the class of Event the map will hold, used for instantiation.
	 */
	
	public EventMap(Class<E> inEventClass) {
		eventClass = inEventClass;
		events = new HashMap<String, E>();
		eventClass = inEventClass;
	}

//---  Operations   ---------------------------------------------------------------------------
	
	/**
	 * Renames the Event corresponding to the oldName String with the newName String.
	 * 
	 * @param oldName - String object representing the name of the Event.
	 * @param newName - String object representing the desired new name of the Event.
	 */
	
	public void renameEvent(String oldName, String newName) {
		E event = events.get(oldName);
		event.setEventName(newName);
		events.remove(oldName);
		events.put(newName, event);
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	/**
	 * Getter method that returns an Event extending object using a provided String representing the
	 * Event's name from the information stored by this Event Map.
	 * 
	 * @param eventName - String object representing an Event object by its id.
	 * @return - Returns the Event extending object corresponding to the provided String object.
	 */
	
	public E getEvent(String eventName) {
		return events.get(eventName);
	}

	/**
	 * Getter method that returns an Event extending object using an Event object from another FSM.
	 * 
	 * @param event - Event object from another FSM (but not necessarily the current FSM).
	 * @return - Returns the corresponding Event object from the current FSM, which has the same event name String as the input event.
	 */
	
	public E getEvent(Event event) {
		String name = event.getEventName();
		return events.get(name);
	}
	
	/**
	 * Getter method that returns all the Event extending objects in this Event Map as a Collection<<r>E>.
	 * 
	 * @return - Returns a Collection<<r>E> of Event extending objects.
	 */
	
	public Collection<E> getEvents() {
		return events.values();
	}
	
	/**
	 * Getter method that returns a boolean value describing whether or not a given Event extending
	 * object exists within this EventMap as denoted by a provided String object.
	 * 
	 * @param eventName - String object representing the Event extending object to look for.
	 * @return - Returns a boolean value; true if the Event extending object exists in the map, false otherwise.
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
	 * @return - Returns an Event extending object which corresponds to the oldEvent's id (be it new or otherwise).
	 */
	
	public E addEvent(Event oldEvent) {
		E newEvent = null;
		try {
			String eventName = oldEvent.getEventName();
			if(events.containsKey(eventName))
				return events.get(eventName);
			newEvent = eventClass.newInstance();
			newEvent.copyDataFrom(oldEvent);
			events.put(eventName, newEvent);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return newEvent;
	}
	
	/**
	 * Adds an event to the map which takes its information from two other events, deciding on
	 * the property values by taking the AND operation of the two events. This is done whenever
	 * two FSMs are combined in some operation and their set of events is amalgamated.
	 * 
	 * @param event1 - Event object from an FSM to use for creating the new Event.
	 * @param event2 - Event object from a second FSM to use for creating the new Event.
	 * @return - Returns an Event extending object derived from the two provided Events.
	 */
	
	public E addEvent(Event event1, Event event2) {
		E newEvent = null;
		try {
			String eventName = event1.getEventName();
			if(events.containsKey(eventName))
				return events.get(eventName);
			newEvent = eventClass.newInstance();
			newEvent.copyDataFrom(event1, event2);
			events.put(eventName, newEvent);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return newEvent;
	}
	
	/**
	 * Adds an event to the map which is mapped to the name indicated. The new event initializes with
	 * the default settings for the given Event class. If the event already existed, no new object is
	 * created.
	 * 
	 * @param eventName - String object representing the name of the Event extending object to add to this EventMap object.
	 * @return - Returns an Event extending object representing the Event in three cases: the Event already existed,
	 * the Event was newly made, or null if there was an error instantiating the event.
	 */
	
	public E addEvent(String eventName) {
		if(events.containsKey(eventName))
			return events.get(eventName);
		try {
			E newEvent = eventClass.newInstance();
			newEvent.setEventName(eventName);
			events.put(eventName, newEvent);
			return newEvent;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Removes the Event from the mapping.
	 * 
	 * @param event - Event extending object to remove from the HashMap<<r>String, E> events object.
	 */
	
	public void removeEvent(E event) {
		events.remove(event.getEventName());
	}
	
	/**
	 * Removes the Event corresponding to the provided String from the mapping if present.
	 * 
	 * @param eventName - String object representing the name of the Event to remove from this Event Map.
	 */
	
	public void removeEvent(String eventName) {
		events.remove(eventName);
	}

}
