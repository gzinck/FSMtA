package support.event;

import support.event.Event;
import support.attribute.EventControllability;
import support.attribute.EventObservability;

public class ObsControlEvent extends Event implements EventObservability, EventControllability{

	private boolean control;
	private boolean observe;
	
	public ObsControlEvent(String eventName) {
		super(eventName);
		control = false;
		observe = true;
	}
	
	public void setEventObservability(boolean obs) {
		observe = obs;
	}
	
	public void setEventControllability(boolean con) {
		control = con;
	}
	
	public boolean getEventObservability() {
		return observe;
	}
	
	public boolean getEventControllability() {
		return control;
	}
	
}
