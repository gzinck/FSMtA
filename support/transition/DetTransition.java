package support.transition;

import java.util.ArrayList;

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

	/** Event instance variable representing the Event associated to this object*/
	public Event event;
	/** State instance variable representing the target State associated to this object*/
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
	
	/**
	 * Makes a String object which has the dot representation of the transitions, which
	 * can be used for sending an FSM to GraphViz.
	 * 
	 * @param firstState The State which leads to the transition. This is used to
	 * determine the exact text for the dot representation.
	 * @return String containing the dot representation of the transitions.
	 */
	
	public String makeDotString(State firstState) {
		return "\"" + firstState.getStateName() + "\"->{\"" + state.getStateName() + "\"} [label = \"" + event.getEventName() + "\"];";
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	// Implementation as per the Transition interface
	@Override
	public Event getTransitionEvent() {
		return event;
	}
	
	/**
	 * Getter method to access the State associated to the Event associated to this Transition object
	 * 
	 * @return - Returns a State object representing the State associated to the Event associated to this Transition object
	 */
	
	public State getTransitionState() {
		return state;
	}
	
	// Implementation as per the Transition interface
	@Override
	public ArrayList<State> getTransitionStates() {
		ArrayList<State> list = new ArrayList<State>();
		list.add(state);
		return list;
	}
	
	// Implementation as per the Transition interface
	@Override
	public boolean stateExists(String stateName) {
		return state.equals(new State(stateName));
	}
	
	// Implementation as per the Transition interface
		@Override
		public boolean stateExists(State inState) {
			return state.equals(inState);
		}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	// Implementation as per the Transition interface
	@Override
	public void setTransitionEvent(Event in) {
		event = in;
	}
	
	/**
	 * Setter method to assign a new String as the State name associated to the Event associated to this Transition object
	 * 
	 * @param in - State object representing the Transition object's Event's new target State
	 */
	
	public void setTransitionState(State in) {
		state = in;
	}
	
//---  Manipulations   -----------------------------------------------------------------------

	// Implementation as per the Transition interface
	@Override
	public boolean removeTransitionState(String stateName) {
		if(state.getStateName().equals(stateName)) {
			state = null;
			return true;
		}
		return false;
	}
	
	// Implementation as per the Transition interface
	@Override
	public boolean removeTransitionState(State inState) {
		if(state.equals(inState)) {
			state = null;
		}
		return (state == null);
	}
}
