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

public class NonDetTransition<S extends State, E extends Event> implements Transition<S, E> {

//--- Instance Variables   --------------------------------------------------------------------
	
	/** Event instance variable representing the Event associated to this object*/
	public E event;
	/** ArrayList<<s>State> object holding all States associated to the Event associated to this NonDetTransition object*/
	private ArrayList<S> states;
	
//--- Constructors   --------------------------------------------------------------------------
	
	/**
	 * Constructor for a NonDetTransition object, assigning a single String value as the Event name associated
	 * to this object and a list of Strings as the State names led to by that Event
	 * 
	 * @param inEvent - String object representing the name of the Event associated to this NonDetTransition object
	 * @param inStates - List of State objects representing the States led to by the Event associated to this NonDetTransition object
	 */
	
	public NonDetTransition(E inEvent, S ... inStates) {
		event = inEvent;
		states = new ArrayList<S>();
		for(int i = 0; i < inStates.length; i++)
			states.add(inStates[i]);
	}
	
	/**
	 * Constructor for a NonDetTransition object, assigning the event and states empty values
	 * until added later on. This is essential for instantiation in generic types.
	 */
	
	public NonDetTransition() {
		event = null;
		states = new ArrayList<S>();
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
			eventDeal = "color = \"red\"";
			break;
		case 2:
			// Observable and uncontrollable
			eventDeal = "color = \"yellow\"";
			break;
		case 3:
			// Unobservable and uncontrollable
			eventDeal = "color = \"orange\"";
			break;
		default: break;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("\"" + firstState.getStateName() + "\"->{\"");
		Iterator<S> itr = states.iterator();
		while(itr.hasNext()) {
			S s = itr.next();
			sb.append(s.getStateName());
			if(itr.hasNext())
				sb.append("\",\"");
		} // while there are more states
			
		sb.append("\"} [label = \"" + event.getEventName() + "\" " + eventDeal + " ];");
		return sb.toString();
	}
		
	@Override
	public NonDetTransition<S, E> generateTransition(){
		NonDetTransition<S, E> outbound = new NonDetTransition<S, E>();
		return outbound;
	}

//--- Setter Methods   ------------------------------------------------------------------------
	
	/**
	 * Setter method to replace the current ArrayList<State> of State names with the provided one
	 * 
	 * @param in - ArrayList<State> object representing the list of States led to by the Event associated to this NonDetTransition object
	 */
	
	public void setTransitionState(ArrayList<S> in) {
		states = in;
	}
	
	@Override
	public void setTransitionState(S in) {
		if(!states.contains(in))
			states.add(in);
	}
	
	@Override
	public void setTransitionEvent(E in) {
		event = in;
	}
	
//--- Getter Methods   ------------------------------------------------------------------------
	
	@Override
	public E getTransitionEvent() {
		return event;
	}
	
	@Override
	public ArrayList<S> getTransitionStates() {
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
	
	/**
	 * This method appends the provided State to the ArrayList<State> holding all State names led to by
	 * the Event associated to this NonDetTransition object
	 * 
	 * @param stateName - State object representing the State to append to the end of the list of States associated to this object
	 */
	
	public void addTransitionState(S stateNew) {
		if(states.indexOf(stateNew) == -1)
			states.add(stateNew);
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

}
