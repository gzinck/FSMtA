package support.event;

import support.event.Event;
import support.attribute.EventControllability;

public class ControllableEvent extends Event implements EventControllability{

	private boolean controllability;
	
	public ControllableEvent(String eventName) {
		super(eventName);
		controllability = false;
	}
	
	public void setEventControllability(boolean newControl) {
		controllability = newControl;
	}
	
	public boolean getEventControllability() {
		return controllability;
	}
	
}
