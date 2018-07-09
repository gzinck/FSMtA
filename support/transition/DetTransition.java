package support.transition;

import java.util.ArrayList;
import java.util.Collection;

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

public class DetTransition<S extends State, E extends Event> implements Transition<S, E> {

	/** Event extending instance variable representing the Event associated to this object*/
	public E event;
	/** State extending instance variable representing the target State associated to this object*/
	protected S state;
	
//---  Constructors   -------------------------------------------------------------------------
	
	/**
	 * Constructor for a Transition object, assigning the provided Event and State to their
	 * corresponding instance variables.
	 * 
	 * @param inEvent - Event object representing the Event associated to this Transition
	 * @param inState - State object representing the State being led to by the Event of this Transition
	 */
	
	public DetTransition(E inEvent, S inState) {
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
	public E getTransitionEvent() {
		return event;
	}
	
	@Override
	public ArrayList<S> getTransitionStates() {
		ArrayList<S> list = new ArrayList<S>();
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
	public void setTransitionEvent(E in) {
		event = in;
	}
	
	@Override 
	public void setTransitionState(S in) {
		state = in;
	}
	
//---  Manipulations   -----------------------------------------------------------------------

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
	public boolean removeTransitionStates(Collection<S> inStates) {
		if(inStates.contains(state))
			state = null;
		return (state == null);
	}
	
	@Override
	public DetTransition<S, E> generateTransition(){
		DetTransition<S, E> outbound = new DetTransition<S, E>();
		return outbound;
	}
}
