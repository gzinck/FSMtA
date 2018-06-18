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
	private boolean control;
	/** boolean instance variable representing the status of this Observable Event obejct's being Observable*/
	private boolean observe;
	
//---  Constructors   -------------------------------------------------------------------------
	
	/**
	 * Constructor for an ObsControlEvent object that assigns a defined String object to be its name,
	 * defaulting its status as Controllability and Observability to be true.
	 * 
	 * @param eventName - String object that represents the name of the ObsControlEvent object.
	 */
	
	public ObsControlEvent(String eventName) {
		super(eventName);
		control = true;
		observe = true;
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
		control = cont;
		observe = obs;
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	@Override
	public void setEventObservability(boolean obs) {
		observe = obs;
	}
	
	@Override
	public void setEventControllability(boolean con) {
		control = con;
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	@Override
	public boolean getEventObservability() {
		return observe;
	}
	
	@Override
	public boolean getEventControllability() {
		return control;
	}	

	@Override
	public int getEventType() {
		return control && observe ? 3 : control ? 2 : observe ? 1 : 0;
	}
	
}
