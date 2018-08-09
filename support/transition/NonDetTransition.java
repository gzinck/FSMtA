package support.transition;

import java.util.*;
import support.State;
import support.Event;

/**
 * This class models a path that connects a State to another State in an FSM, storing an
 * Event and a list of States it may lead to. It is the NonDeterministicFSM variant of
 * the base Transition class.
 * 
 * This class is a part of the support.transition package
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public class NonDetTransition implements Transition {

//--- Instance Variables   --------------------------------------------------------------------
	
	/** Event instance variable representing the Event associated to this object*/
	public Event event;
	/** ArrayList<<r>State> object holding all State objects associated to the Event associated to this NonDetTransition object*/
	private ArrayList<State> states;
	
//--- Constructors   --------------------------------------------------------------------------
	
	/**
	 * Constructor for a NonDetTransition object, assigning a single Event object and a list of States which the event can lead to.
	 * 
	 * @param inEvent - Event object representing the event that leads to the associated transition states.
	 * @param inStates - List of State objects representing the States led to by the Event associated with this NonDetTransition object.
	 */
	
	public NonDetTransition(Event inEvent, State ... inStates) {
		event = inEvent;
		states = new ArrayList<State>();
		for(int i = 0; i < inStates.length; i++)
			states.add(inStates[i]);
	}
	
	/**
	 * Constructor for a NonDetTransition object, assigning a single Event object and a list of States which the event can lead to.
	 * 
	 * @param inEvent - Event object representing the event that leads to the associated transition states.
	 * @param inStates - Collection of State objects representing the States led to by the Event associated with this NonDetTransition object.
	 */
	
	public NonDetTransition(Event inEvent, Collection<State> inStates) {
		event = inEvent;
		states = new ArrayList<State>(inStates);
	}
	
	/**
	 * Constructor for a NonDetTransition object, assigning the event and states empty values until added later on.
	 */
	
	public NonDetTransition() {
		event = null;
		states = new ArrayList<State>();
	}
	
//---  Operations   ---------------------------------------------------------------------------

	@Override
	public String makeDotString(State firstState) {
		boolean obs = event.getEventObservability();
		boolean atk = event.getEventAttackerObservability();
		boolean con = event.getEventControllability();
		
		String eventDeal = "color = ";
		
		if(event.getEventObservability()) {		//Red means System can't see
			eventDeal += "\"black\"";
		}
		else {
			eventDeal += "\"red\"";
		}
		
		eventDeal += " arrowhead = \"normal";
		
		if(!event.getEventAttackerObservability()) {		//Dot means Attacker can't see
			eventDeal += "odot";
		}
		
		if(!event.getEventControllability()) {		//Diamond means System can't control
			eventDeal += "diamond";
		}
		
		eventDeal += "\"";
		StringBuilder sb = new StringBuilder();
		sb.append("\"" + firstState.getStateName() + "\"->{\"");
		Iterator<State> itr = states.iterator();
		while(itr.hasNext()) {
			State s = itr.next();
			sb.append(s.getStateName());
			if(itr.hasNext())
				sb.append("\",\"");
		} // while there are more states
			
		sb.append("\"} [label = \"" + event.getEventName() + "\" " + eventDeal + " ]; \n");
		return sb.toString();
	}
	
	@Override
	public NonDetTransition generateTransition(){
		NonDetTransition outbound = new NonDetTransition();
		return outbound;
	}

//--- Setter Methods   ------------------------------------------------------------------------
	
	/**
	 * Setter method to replace the current ArrayList<<r>State> of State names with the provided one
	 * 
	 * @param in - ArrayList<<r>State> object representing the list of States led to by the Event associated to this NonDetTransition object
	 */
	
	public void setTransitionState(ArrayList<State> in) {
		states = in;
	}
	
	@Override
	public void setTransitionState(State in) {
		if(!states.contains(in))
			states.add(in);
	}
	
	@Override
	public void setTransitionEvent(Event in) {
		event = in;
	}
	
//--- Getter Methods   ------------------------------------------------------------------------
	
	@Override
	public Event getTransitionEvent() {
		return event;
	}
	
	@Override
	public ArrayList<State> getTransitionStates() {
		return states;
	}
	
	@Override
	public boolean stateExists(String stateName) {
		return states.contains(new State(stateName));
	}
	
	@Override
	public boolean stateExists(State inState) {
		return states.contains(inState);
	}
	
//--- Manipulations   -------------------------------------------------------------------------
	
	@Override
	public boolean addTransitionState(State stateNew) {
		if(states.indexOf(stateNew) == -1) {
			states.add(stateNew);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean removeTransitionState(String stateName) {
		states.remove(new State(stateName));
		return (states.size() == 0);
	}
	
	@Override
	public boolean removeTransitionState(State inState) {
		states.remove(inState);
		return (states.size() == 0);
	}
	
	@Override
	public boolean removeTransitionStates(Collection<State> inStates) {
		states.removeAll(inStates);
		return (states.size() == 0);
	}

	@Override
	public int compareTo(Transition o) {
		// Simply compares the names of the two tranisitons' events
		return this.event.getEventName().compareTo(o.getTransitionEvent().getEventName());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(event.getEventName() + " goes to the states: ");
		Iterator<State> itr = states.iterator();
		while(itr.hasNext()) {
			sb.append(itr.next().getStateName());
			if(itr.hasNext()) sb.append(", ");
		}
		return sb.toString();
	}
}
