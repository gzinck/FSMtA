package support;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * DisabledEvents is an object designed for storing in a map between a state
 * name and one of these objects. It says what events are disabled for a given
 * state in order to make a supremal controllable sublanguage.
 * 
 * This class is a part of the support package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public class DisabledEvents {
	
//---  Instance Variables   -------------------------------------------------------------------
	
	/** boolean instance variable describing the status of this object as disabled or not*/
	private boolean stateIsDisabled;
	/** ArrayList<<r>String> object that stores all the events which are disabled for the state, or null if none are disabled. */
	private ArrayList<String> disabledEvents;
	
//---  Constructors   -------------------------------------------------------------------------
	
	/**
	 * Creates a new DisabledEvents object for storing what events are disabled at a given state,
	 * and if the state itself is disabled.
	 * 
	 * @param stateDisabled - boolean value describing whether or not the associated State is disabled.
	 */
	
	public DisabledEvents(boolean stateDisabled) {
		disabledEvents = null;
		stateIsDisabled = stateDisabled;
	}
	
//---  Getter Methods   -----------------------------------------------------------------------

	/**
	 * Getter method that returns the status of this DisabledEvents object as being disabled.
	 * 
	 * @return - Returns a boolean value; true if the State object has been disabled, false otherwise.
	 */
	
	public boolean stateIsDisabled() {
		return stateIsDisabled;
	}
	
	/**
	 * Getter method that returns the status of this DisabledEvents object's list of disabled Events being empty or not.
	 * 
	 * @return - Returns a boolean value; true if there are no disabled events, false otherwise.
	 */
	
	public boolean allEventsEnabled() {
		return (disabledEvents == null);
	}
	
	/**
	 * Getter method that returns the status of an Event (as represented by the provided String object)'s being
	 * disabled in this DisabledEvents object.
	 * 
	 * @param eventName - String object representing the name of the Event object which may or may not be enabled.
	 * @return - Returns a boolean value; true if the Event is enabled, false otherwise.
	 */
	
	public boolean eventIsEnabled(String eventName) {
		if(disabledEvents == null)
			return true;
		return !disabledEvents.contains(eventName);
	}
	
	/**
	 * Getter method that returns the ArrayList<<r>String> of disabled Events associated to this DisabledEvents object.
	 * 
	 * @return - Returns an ArrayList<<r>String> object representing the Events that are disabled, or null if there are none.
	 */
	
	public ArrayList<String> getDisabledEvents() {
		return disabledEvents;
	}
	
//---  Manipulations   ------------------------------------------------------------------------
	
	/**
	 * This method assigns the calling object and its corresponding State object as disabled,
	 * designating it as unusable in its FSM.
	 */
	
	public void disableState() {
		stateIsDisabled = true;
	}
	
	/**
	 * This method assigns a new Event as disabled for the corresponding State object, restricting
	 * its behavior in its FSM.
	 * 
	 * @param eventName - String object representing the Event object to mark as disabled.
	 */
	
	public void disableEvent(String eventName) {
		if(disabledEvents == null) 
			disabledEvents = new ArrayList<String>();
		disabledEvents.add(eventName);
	}
	
	/**
	 * This method uses another DisabledEvents object to assign Events as disabled for this
	 * DisabledEvents object and its corresponding State object.
	 * 
	 * @param other - DisabledEvents object whose assigned disabled Events are shared to the calling DisabledEvents object. 
	 */
	
	public void disableEvents(DisabledEvents other) {
		if(other != null && other.disabledEvents != null) {
			if(disabledEvents == null)
				disabledEvents = other.disabledEvents;
			else
				disabledEvents.addAll(other.disabledEvents);
		} // if other not null
	}
	
//---  Miscellaneous   ------------------------------------------------------------------------
	
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
