package support.transition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import support.State;
import support.event.Event;

/**
 * This class models a path that connects a State to another State in an FSM, storing an Event
 * and the Single State that it leads to. This is the base variation of a Transition, only
 * storing the one State, and thus modeling in a Deterministic manner.
 * 
 * This class is a part of the support.transition package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public class DetTransition implements Transition {

	/** Event extending instance variable representing the Event associated to this object*/
	public Event event;
	/** State extending instance variable representing the target State associated to this object*/
	protected State state;
	
//---  Constructors   -------------------------------------------------------------------------
	
	/**
	 * Constructor for a Transition object, assigning the provided Event and State to their
	 * corresponding instance variables.
	 * 
	 * @param inEvent - Event object representing the Event associated to this Transition
	 * @param inState - State object representing the State being led to by the Event of this Transition
	 */
	
	public DetTransition(Event inEvent, State inState) {
		event = inEvent;
		state = inState;
	}
	
	/**
	 * Constructor for a Transition object, assigning the event and state null values
	 * until added later on. This is essential for instantiation in generic types.
	 */
	
	public DetTransition() {
		event = null;
		state = null;
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
		return "\"" + firstState.getStateName() + "\"->{\"" + state.getStateName() + "\"} [label = \"" + event.getEventName() + "\" " + eventDeal + " ];";
	}
	
	/**
	 * Generates a String object which has the dot representation of this Transition, but
	 * with a dotted line to indicate that it is a may transition, which
	 * can be used for converting an Modal Specification to a .jpg image via GraphViz.
	 * 
	 * @param firstState - State object associated to this Transition. This is used to
	 * determine the exact text for the dot representation.
	 * @return - Returns a String object containing the dot representation of this Transition.
	 */
	public String makeDotStringMayTransition(State firstState) {
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
		eventDeal += "; style=dotted";
		return "\"" + firstState.getStateName() + "\"->{\"" + state.getStateName() + "\"} [label = \"" + event.getEventName() + "\" " + eventDeal + " ];";
	}
	
//---  Getter Methods   -----------------------------------------------------------------------

	/**
	 * Getter method to access the State associated to the Event associated to this Transition object
	 * 
	 * @return - Returns a State object representing the State associated to the Event associated to this Transition object
	 */
	
	public State getTransitionState() {
		return state;
	}
	
	@Override
	public Event getTransitionEvent() {
		return event;
	}
	
	@Override
	public ArrayList<State> getTransitionStates() {
		ArrayList<State> list = new ArrayList<State>();
		if(state != null)
			list.add(state);
		return list;
	}
	
	@Override
	public boolean stateExists(String stateName) {
		return state.equals(new State(stateName));
	}
	
	@Override
	public boolean stateExists(State inState) {
			return state.equals(inState);
		}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	@Override
	public void setTransitionEvent(Event in) {
		event = in;
	}
	
	@Override 
	public void setTransitionState(State in) {
		state = in;
	}
	
//---  Manipulations   -----------------------------------------------------------------------

	public boolean addTransitionState(State inState) {
		if(state != null)
			return false;
		state = inState;
		return true;
	}
	
	@Override
	public boolean removeTransitionState(String stateName) {
		if(state.getStateName().equals(stateName)) {
			state = null;
			return true;
		}
		return false;
	}
	
	@Override
	public boolean removeTransitionState(State inState) {
		if(state.equals(inState))
			state = null;
		return (state == null);
	}
	
	@Override
	public boolean removeTransitionStates(Collection<State> inStates) {
		if(inStates.contains(state))
			state = null;
		return (state == null);
	}
	
	@Override
	public DetTransition generateTransition(){
		DetTransition outbound = new DetTransition();
		return outbound;
	}
}
