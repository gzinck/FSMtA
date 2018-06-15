package fsm;

import fsm.attribute.Observability;
import support.EventMap;
import support.ReadWrite;
import support.State;
import support.StateMap;
import support.TransitionFunction;
import support.event.ObservableEvent;
import support.transition.Transition;
import java.io.*;
import java.util.*;

/**
 * This class models a Deterministic Observable FSM that expands upon the Deterministic FSM class to
 * implement the Observable characteristics of an FSM - Events being capable of being UnObservable.
 * 
 * This class is a part of the fsm package
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public class DetObsFSM extends DetFSM implements Observability<State, Transition, ObservableEvent>{
	
//---  Constant Values   ----------------------------------------------------------------------

	/** String constant designating this object as a specific type of FSM for clarification purposes*/
	public static final String FSM_TYPE = "DetObs FSM";
	
//---  Constructors  --------------------------------------------------------------------------
	
	/**
	 * Constructor for
	 * 
	 * DetObsFSM File Order for Special: Initial, Marked, Unobservable Events.
	 * 
	 * @param in
	 * @param id
	 */
	
	public DetObsFSM(File in, String inId) {
		id = inId;
		
		StateMap<State> states = new StateMap<State>(State.class);
		EventMap<ObservableEvent> events = new EventMap<ObservableEvent>(ObservableEvent.class);
		TransitionFunction<State, Transition> transitions = new TransitionFunction<State, Transition>(Transition.class);
		
		ReadWrite<State, ObservableEvent, Transition> redWrt = new ReadWrite<State, ObservableEvent, Transition>();
		ArrayList<ArrayList<String>> special = redWrt.readFromFile(states, events, transitions, in);
		initialState = states.getState(special.get(0).get(0));
		states.getState(initialState).setStateInitial(true);
		for(int i = 0; i < special.get(1).size(); i++)
			states.getState(special.get(1).get(i)).setStateMarked(true);
		for(int i = 0; i < special.get(2).size(); i++)
			events.getEvent(special.get(2).get(i)).setEventObservability(false);
		
		
		//constructFSM(states, events, transitions);
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	@Override
	public FSM<State, Transition, ObservableEvent> createObserverView() {
		// TODO Auto-generated method stub
		return null;
	}

//---  Getter Methods   -----------------------------------------------------------------------

	@Override
	public Boolean getEventObservability(ObservableEvent event) {
		// TODO Auto-generated method stub
		return null;
	}

//---  Setter Methods   -----------------------------------------------------------------------

	@Override
	public void setEventObservability(ObservableEvent event, boolean status) {
		// TODO Auto-generated method stub
		
	}
	
}
