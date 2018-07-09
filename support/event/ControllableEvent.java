package support.event;

import support.event.Event;
import support.attribute.EventControllability;
import support.attribute.EventObservability;

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
	
	/** boolean instance variable representing the status of this ControllableEvent object's being Controllable*/
	private boolean controllability;
	
//---  Constructors   -------------------------------------------------------------------------
	
	/**
	 * Constructor for a Controllable Event object that does not assign an ID. It defaults to setting
	 * the event as controllable. This should not be used unless it is promptly renamed after called,
	 * it is simply used so that instantiation is possible in generic EventMap methods.
	 */
	
	public ControllableEvent() {
		super();
		controllability = true;
	}
	
	/**
	 * Constructor for a Controllable Event object that assigns the defined String object
	 * to be the Controllable Event object's name, and defaults the status of Controllability
	 * as true. (Is Controllable)
	 * 
	 * @param eventName - String object provided as the name of the Event associated to this Controllable Event object
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
	
	/**
	 * Constructor for a ControllableEvent object that uses the provided Event object's information
	 * to construct a new Event object.
	 * 
	 * @param oldEvent - Event object provided to have its attributes copied into the new ControllableEvent object.
	 */
	
	public ControllableEvent(ControllableEvent oldEvent) {
		super(oldEvent);
		controllability = oldEvent.controllability;
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	@Override
	public void setEventControllability(boolean newControl) {
		controllability = newControl;
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	@Override
	public void copyDataFrom(Event other) {
		id = other.id;
		controllability = ((other instanceof EventControllability) ? ((EventControllability)other).getEventControllability() : true);
	}
	
	@Override
	public void copyDataFrom(Event other1, Event other2) {
		id = other1.id;
		boolean firstIsControllable = ((other1 instanceof EventControllability) ? ((EventControllability)other1).getEventControllability() : true);
		boolean secondIsControllable = ((other2 instanceof EventControllability) ? ((EventControllability)other2).getEventControllability() : true);
		controllability = (firstIsControllable && secondIsControllable);
	}
	
	@Override
	public <E extends Event> E makeEventWith(E other) {
		ControllableEvent newEvent = new ControllableEvent(this);
		if(other instanceof EventControllability) {
			EventControllability obsOther = (EventControllability)other;
			newEvent.controllability = (this.controllability && obsOther.getEventControllability());
		}
		return (E)newEvent;
	}
	
	@Override
	public boolean getEventControllability() {
		return controllability;
	}
	
	@Override
	public int getEventType() {
		return controllability ? 2 : 0;
	}
}
