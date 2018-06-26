package support;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * DisabledEvents is an object designed for storing in a map between a state
 * name and one of these objects. It says what events are disabled for a given
 * state in order to make a supremal controllable sublanguage.
 * 
 * @author Mac Clevinger and Graeme Zinck
 *
 */

public class DisabledEvents {
	/** Stores whether the state is disabled. */
	private boolean stateIsDisabled;
	/** Stores all the events which are disabled for the state, or null if none are disabled. */
	private ArrayList<String> disabledEvents;
	
	/**
	 * Creates a new DisabledEvents object for storing what events are disabled
	 * at a given state, and if the state itself is disabled.
	 * 
	 * @param stateDisabled Says if the state itself is disabled.
	 */
	public DisabledEvents(boolean stateDisabled) {
		disabledEvents = null;
		stateIsDisabled = stateDisabled;
	}
	
	/**
	 * Disables the state.
	 */
	public void disableState() {
		stateIsDisabled = true;
	}
	
	/**
	 * Disables an event.
	 * @param eventName String representing the event to disable.
	 */
	public void disableEvent(String eventName) {
		if(disabledEvents == null) disabledEvents = new ArrayList<String>();
		disabledEvents.add(eventName);
	}
	
	/**
	 * Disables all events disabled in another DisabledEvents object.
	 * @param other DisabledEvents object with events to disable in the calling
	 * object.
	 */
	public void disableEvents(DisabledEvents other) {
		if(other != null && other.disabledEvents != null) {
			if(disabledEvents == null)
				disabledEvents = other.disabledEvents;
			else
				disabledEvents.addAll(other.disabledEvents);
		} // if other not null
	}
	
	/**
	 * Gets if the state is disabled.
	 * @return True if the state has been disabled, false otherwise.
	 */
	public boolean stateIsDisabled() {
		return stateIsDisabled;
	}
	
	/**
	 * Gets if all the events are enabled at the state.
	 * @return True if there are no disabled events.
	 */
	public boolean allEventsEnabled() {
		return (disabledEvents == null);
	}
	
	/**
	 * Gets if the given event is disabled or not.
	 * @param eventName Name of the event which may or may not be enabled.
	 * @return True if the event is enabled, false otherwise.
	 */
	public boolean eventIsEnabled(String eventName) {
		if(disabledEvents == null) return true;
		return !disabledEvents.contains(eventName);
	}
	
	/**
	 * Gets an ArrayList of Strings, which represent the events which must be disabled
	 * at the given state.
	 * @return ArrayList of Strings representing the events to remove.
	 */
	public ArrayList<String> getDisabledEvents() {
		return disabledEvents;
	}
	
	@Override
	public String toString() {
		if(stateIsDisabled)
			return "Disabled";
		if(disabledEvents == null)
			return "All enabled";
		StringBuilder sb = new StringBuilder("Enabled except ");
		Iterator<String> itr = disabledEvents.iterator();
		while(itr.hasNext()) {
			sb.append(itr.next());
			if(itr.hasNext())
				sb.append(", ");
		}
		return sb.toString();
	}
}
