package support.event;

import support.event.Event;
import support.attribute.EventObservability;

/**
 * This class models an Observable Event in an FSM, building on the base Event class
 * to store, in addition to the Event's name, the status of the object's being Observable.
 * 
 * This class is a part of the support.event package
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public class ObservableEvent extends Event implements EventObservability{

//---  Instance Variables   -------------------------------------------------------------------
	
	/** boolean instance variable representing the status of this Observable Event obejct's being Observable*/
	private boolean observability;
	
//---  Constructors   -------------------------------------------------------------------------
	
	/**
	 * Constructor 
	 * 
	 */
	
	public ObservableEvent() {
		super();
		observability = true;
	}
	
	/**
	 * Constructor for an ObservableEvent object that assigns the provided String object as its
	 * name and defaults the status of its being Observable to true. (Can be seen.)
	 * 
	 * @param eventName - String object representing the name of the ObservableEvent object
	 */
	
	public ObservableEvent(String eventName) {
		super(eventName);
		observability = true;
	}
	
	/**
	 * Constructor for an ObservableEvent object that assigns the provided String object and boolean
	 * value as its name and the status of its being Observable.
	 * 
	 * @param eventName - String object representing the name of the ObservableEvent object
	 * @param obs - boolean value representing the status of its being Observable
	 */
	
	public ObservableEvent(String eventName, boolean obs) {
		super(eventName);
		observability = obs;
	}
	
	/**
	 * Constructor for an ObservableEvent object that uses the parameter Event object's information
	 * to construct a new event object.
	 * 
	 * @param oldEvent - Event object to be copied.
	 */
	public ObservableEvent(ObservableEvent oldEvent) {
		super(oldEvent);
		observability = oldEvent.observability;
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	@Override
	public void setEventObservability(boolean replace) {
		observability = replace;
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	@Override
	public <E extends Event> E copy() {
		// For use in other areas, and when Event is extended, this is
		// necessary.
		return (E)(new ObservableEvent(this));
	}
	
	@Override
	public <E extends Event> E makeEventWith(E other) {
		ObservableEvent newEvent = new ObservableEvent(this);
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
	public int getEventType() {
		return observability ? 0 : 1;
	}
}
