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
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	public void setEventObservability(boolean replace) {
		observability = replace;
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public boolean getEventObservability() {
		return observability;
	}
	
}
