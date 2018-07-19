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
	
	/** Event extending instance variable representing the Event associated to this object*/
	public Event event;
	/** ArrayList<<r>State> object holding all State extending objects associated to the Event associated to this NonDetTransition object*/
	private ArrayList<State> states;
	
//--- Constructors   --------------------------------------------------------------------------
	
	/**
	 * Constructor for a NonDetTransition object, assigning a single Event object
	 * and a list of States which the event can lead to.
	 * 
	 * @param inEvent - Event object representing the event that leads to the associated transition states.
	 * @param inStates - List of State objects representing the States led to by the Event associated with this NonDetTransition object.
	 */
	
	@SafeVarargs
	public NonDetTransition(Event inEvent, State ... inStates) {
		event = inEvent;
		states = new ArrayList<State>();
		for(int i = 0; i < inStates.length; i++)
			states.add(inStates[i]);
	}
	
	/**
	 * Constructor for a NonDetTransition object, assigning a single Event object
	 * and a list of States which the event can lead to.
	 * 
	 * @param inEvent - Event object representing the event that leads to the associated transition states.
	 * @param inStates - Collection of State objects representing the States led to by the Event associated with this NonDetTransition object.
	 */
	
	public NonDetTransition(Event inEvent, Collection<State> inStates) {
		event = inEvent;
		states = new ArrayList<State>(inStates);
	}
	
	/**
	 * Constructor for a NonDetTransition object, assigning the event and states empty values
	 * until added later on. This is essential for instantiation in generic types.
	 */
	
	public NonDetTransition() {
		event = null;
		states = new ArrayList<State>();
	}
	
//---  Operations   ---------------------------------------------------------------------------

	@Override
	public String makeDotString(State firstState) {
		String eventDeal = "";
		switch(event.getEventType()) {
		case 0:
			// Observable and controllable
			eventDeal = "color = \"black\"";
			break;
		case 1:
			// Unobservable but controllable
			eventDeal = "color = \"red\" arrowhead = \"normalicurve\"";
			break;
		case 2:
			// Observable and uncontrollable
			eventDeal = "color = \"blue\" arrowhead = \"normaldiamond\"";
			break;
		case 3:
			// Unobservable and uncontrollable
			eventDeal = "color = \"purple\" arrowhead = \"normalodot\"";
			break;
		default: break;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("\"" + firstState.getStateName() + "\"->{\"");
		Iterator<State> itr = states.iterator();
		while(itr.hasNext()) {
			State s = itr.next();
			sb.append(s.getStateName());
			if(itr.hasNext())
				sb.append("\",\"");
		} // while there are more states
			
		sb.append("\"} [label = \"" + event.getEventName() + "\" " + eventDeal + " ];");
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

}
