package support.event;

public class Event {
	
	private String id;
	
	public Event(String in) {
		id = in;
	}
	
	public void setEventName(String in) {
		id = in;
	}
	
	public String getEventName() {
		return id;
	}
	
}
