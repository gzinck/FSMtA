package support.transition;

import java.util.*;
import support.State;
import support.event.Event;

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
	/** ArrayList<State> object holding all States associated to the Event associated to this NonDetTransition object*/
	private ArrayList<State> states;
	
//--- Constructors   --------------------------------------------------------------------------
	
	/**
	 * Constructor for a NonDetTransition object, assigning a single String value as the Event name associated
	 * to this object and a list of Strings as the State names led to by that Event
	 * 
	 * @param inEvent - String object representing the name of the Event associated to this NonDetTransition object
	 * @param inStates - List of State objects representing the States led to by the Event associated to this NonDetTransition object
	 */
	
	public NonDetTransition(Event inEvent, State ... inStates) {
		event = inEvent;
		states = new ArrayList<State>();
		for(int i = 0; i < inStates.length; i++)
			states.add(inStates[i]);
	}
	
	/**
	 * Constructor for a NonDetTransition object, assigning the event and states empty values
	 * until added later on. This is essential for instantiation in generic types.
	 */
	
	public NonDetTransition() {
		event = null;
		states = new ArrayList<State>();
	}
	
//--- Setter Methods   ------------------------------------------------------------------------
	
	/**
	 * Setter method to replace the current ArrayList<State> of State names with the provided one
	 * 
	 * @param in - ArrayList<State> object representing the list of States led to by the Event associated to this NonDetTransition object
	 */
	
	public void setTransitionState(ArrayList<State> in) {
		states = in;
	}
	
	// Implementation as per the Transition interface
	@Override
	public void setTransitionState(State in) {
		if(!states.contains(in))
			states.add(in);
	}
	
	// Implementation as per the Transition interface
	@Override
	public void setTransitionEvent(Event in) {
		event = in;
	}
	
//--- Getter Methods   ------------------------------------------------------------------------
	
	// Implementation as per the Transition interface
	@Override
	public Event getTransitionEvent() {
		return event;
	}
	
	// Implementation as per the Transition interface
	@Override
	public ArrayList<State> getTransitionStates() {
		return states;
	}
	
	// Implementation as per the Transition interface
	@Override
	public boolean stateExists(String stateName) {
		return states.contains(new State(stateName));
	}
	
	// Implementation as per the Transition interface
	@Override
	public boolean stateExists(State inState) {
		return states.contains(inState);
	}
	
//--- Manipulations   -------------------------------------------------------------------------
	
	/**
	 * This method appends the provided State to the ArrayList<State> holding all State names led to by
	 * the Event associated to this NonDetTransition object
	 * 
	 * @param stateName - State object representing the State to append to the end of the list of States associated to this object
	 */
	
	public void addTransitionState(State stateNew) {
		states.add(stateNew);
	}
	
	// Implementation as per the Transition interface
	@Override
	public boolean removeTransitionState(String stateName) {
		states.remove(new State(stateName));
		return (states.size() == 0);
	}
	
	// Implementation as per the Transition interface
	@Override
	public boolean removeTransitionState(State inState) {
		states.remove(inState);
		return (states.size() == 0);
	}
	
//---  Operations   ---------------------------------------------------------------------------

	// Implementation as per the Transition interface
	@Override
	public String makeDotString(State firstState) {
		StringBuilder sb = new StringBuilder();
		sb.append("\"" + firstState.getStateName() + "\"->{\"");
		for(State s : states)
			sb.append(s.getStateName());
		sb.append("\"} [label = \"" + event.getEventName() + "\"];");
		return sb.toString();
	}
}
