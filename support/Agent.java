package support;

import java.util.Collection;
import support.map.EventMap;

public class Agent {

	EventMap events;
	
	public Agent(Event ... eventsProvided) {
		events = new EventMap();
		for(Event e : eventsProvided)
			events.addEvent(e);
	}
	
	public boolean getObservable(String eventName) {
		return events.getEvent(eventName).getEventObservability();
	}
	
	public boolean getControllable(String eventName) {
		return events.getEvent(eventName).getEventControllability();
	}
	
	public Collection<Event> getAgentEvents(){
		return events.getEvents();
	}
	
	public void addNonPresentEvent(String eventName) {
		Event e = events.addEvent(eventName);
		e.setEventControllability(false);
		e.setEventObservability(false);
	}
	
	public boolean contains(String eventName) {
		return events.contains(eventName);
	}
	
}
