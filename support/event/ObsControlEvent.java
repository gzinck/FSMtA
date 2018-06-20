package support.event;

import support.event.Event;
import support.attribute.EventControllability;
import support.attribute.EventObservability;

/**
 * This class models an Observable and Controllable event in an FSM, building on
 * the base Event class via the EventControllability and EventObservability interfaces
 * to store information about, in addition to the Event's name, its status as Observable
 * and Controllable.
 * 
 * This class is a part of the support.event package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public class ObsControlEvent extends Event implements EventObservability, EventControllability{

//---  Instance Variables   -------------------------------------------------------------------
	
	/** boolean instance variable representing the status of this Event's being Controllable*/
	private boolean controllability;
	/** boolean instance variable representing the status of this Observable Event obejct's being Observable*/
	private boolean observability;
	
//---  Constructors   -------------------------------------------------------------------------
	
	/**
	 * Constructor for an ObsControlEvent object that assigns a defined String object to be its name,
	 * defaulting its status as Controllability and Observability to be true.
	 * 
	 * @param eventName - String object that represents the name of the ObsControlEvent object.
	 */
	
	public ObsControlEvent(String eventName) {
		super(eventName);
		controllability = true;
		observability = true;
	}
	
	/**
	 * Constructor for an ObsControlEvent object that assigns a defined String object and two boolean values to
	 * be its name and the statuses of the object's being Observable and Controllable.
	 * 
	 * @param eventName - String object that represents the name of the ObsControlEvent object.
	 * @param cont - boolean value that represents the status of the ObsControlEvent's being Controllable.
	 * @param obs - boolean value that represents the status of the ObsControlEvent's being Observable.
	 */
	
	public ObsControlEvent(String eventName, boolean cont, boolean obs) {
		super(eventName);
		controllability = cont;
		observability = obs;
	}
	
	/**
	 * Constructor for an ObservableEvent object that uses the parameter Event object's information
	 * to construct a new event object.
	 * 
	 * @param oldEvent - Event object to be copied.
	 */
	public ObsControlEvent(ObsControlEvent oldEvent) {
		super(oldEvent);
		controllability = oldEvent.controllability;
		observability = oldEvent.observability;
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	@Override
	public void setEventObservability(boolean obs) {
		observability = obs;
	}
	
	@Override
	public void setEventControllability(boolean con) {
		controllability = con;
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	@Override
	public <E extends Event> E copy() {
		// For use in other areas, and when Event is extended, this is
		// necessary.
		return (E)(new ObsControlEvent(this));
	}
	
	@Override
	public <E extends Event> E makeEventWith(E other) {
		ObsControlEvent newEvent = new ObsControlEvent(this);
		if(other instanceof EventControllability) {
			EventControllability obsOther = (EventControllability)other;
			newEvent.controllability = (this.controllability && obsOther.getEventControllability());
		}
		if(other instanceof EventObservability) {
			EventObservability obsOther = (EventObservability)other;
			newEvent.observability = (this.observability && obsOther.getEventObservability());
		}
		return (E)newEvent;
	}
	
	@Override
	public boolean getEventObservability() {
		return observability;
	}
	
	@Override
	public boolean getEventControllability() {
		return controllability;
	}	

	@Override
	public int getEventType() {
		return controllability && observability ? 3 : controllability ? 2 : observability ? 1 : 0;
	}
	
}
