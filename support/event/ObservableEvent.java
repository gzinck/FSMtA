package support.event;

import support.event.Event;
import support.attribute.EventObservability;

public class ObservableEvent extends Event implements EventObservability{

	private boolean observability;
	
	public ObservableEvent(String eventName) {
		super(eventName);
		observability = true;
	}
	
	public ObservableEvent(String eventName, boolean obs) {
		super(eventName);
		observability = obs;
	}
	
	public void setEventObservability(boolean replace) {
		observability = replace;
	}
	
	public boolean getEventObservability() {
		return observability;
	}
	
}
