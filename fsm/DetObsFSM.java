package fsm;

import fsm.attribute.Observability;
import support.EventMap;
import support.ReadWrite;
import support.State;
import support.StateMap;
import support.TransitionFunction;
import support.event.Event;
import support.event.ObservableEvent;
import support.transition.DetTransition;
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

public class DetObsFSM extends FSM<State, DetTransition<State, ObservableEvent>, ObservableEvent> implements Observability<State, DetTransition<State, ObservableEvent>, ObservableEvent>{
	
//---  Constant Values   ----------------------------------------------------------------------

	/** String constant designating this object as a specific type of FSM for clarification purposes*/
	public static final String FSM_TYPE = "DetObs FSM";

//---  Instance Variables   -------------------------------------------------------------------
		
	/** State object that stores the initial state for this Deterministic FSM object. */
	protected State initialState;
		
//---  Constructors  --------------------------------------------------------------------------
	
	/**
	 * Constructor for a DetObsFSM object that reads in a supplied File to assign values to its
	 * instance variables, seeking out information about Transitions, Initial States, Marked States,
	 * and Unobservable Events. Also accepts a String to name this object.
	 * 
	 * DetObsFSM File Order for Special: Initial, Marked, Unobservable Events.
	 * 
	 * @param in - File object that contains information pertaining to the configuration of this DetObsFSM object
	 * @param id - String object that represents the name of this DetObsFSM object
	 */
	
	public DetObsFSM(File in, String inId) {
		id = inId;
		
		states = new StateMap<State>(State.class);
		events = new EventMap<ObservableEvent>(ObservableEvent.class);
		transitions = new TransitionFunction<State, DetTransition<State, ObservableEvent>, ObservableEvent>(new DetTransition<State, ObservableEvent>());
		
		ReadWrite<State, DetTransition<State, ObservableEvent>, ObservableEvent> redWrt = new ReadWrite<State, DetTransition<State, ObservableEvent>, ObservableEvent>();
		ArrayList<ArrayList<String>> special = redWrt.readFromFile(states, events, transitions, in);
		initialState = states.getState(special.get(0).get(0));
		states.getState(initialState).setStateInitial(true);
		for(int i = 0; i < special.get(1).size(); i++)
			states.getState(special.get(1).get(i)).setStateMarked(true);
		for(int i = 0; i < special.get(2).size(); i++)
			events.getEvent(special.get(2).get(i)).setEventObservability(false);
	}
	
	/**
	 * Constructor for an FSM object that contains no transitions or states, allowing the
	 * user to add those elements him/herself. It has no id, either.
	 */
	
	public DetObsFSM() {
		id = "";
		states = new StateMap<State>(State.class);
		events = new EventMap<ObservableEvent>(ObservableEvent.class);
		transitions = new TransitionFunction<State, DetTransition<State, ObservableEvent>, ObservableEvent>(new DetTransition<State, ObservableEvent>());
		initialState = null;
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	@Override
	public FSM<State, DetTransition<State, ObservableEvent>, ObservableEvent> createObserverView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FSM<State, DetTransition<State, ObservableEvent>, ObservableEvent> makeCoAccessible() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FSM union(FSM<State, DetTransition<State, ObservableEvent>, ObservableEvent> other) {
		// T	ODO Auto-generated method stub
		return null;
	}

	@Override
	public DetObsFSM product(FSM<State, DetTransition<State, ObservableEvent>, ObservableEvent> other) {
		DetObsFSM newFSM = new DetObsFSM();
		productHelper(other, newFSM);
		return newFSM;
	}
	
	@Override
	public DetObsFSM parallelComposition(FSM<State, DetTransition<State, ObservableEvent>, ObservableEvent> other) {
		DetObsFSM newFSM = new DetObsFSM();
		parallelCompositionHelper(other, newFSM);
		return newFSM;
	}

	@Override
	public void toTextFile(String filePath, String name) {
		// TODO Auto-generated method stub
	}

//---  Getter Methods   -----------------------------------------------------------------------

	@Override
	public Boolean getEventObservability(ObservableEvent event) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<State> getInitialStates() {
		// TODO Auto-generated method stub
		return null;
	}

//---  Setter Methods   -----------------------------------------------------------------------

	@Override
	public void setEventObservability(ObservableEvent event, boolean status) {
		// TODO Auto-generated method stub	
	}

//---  Manipulations   ------------------------------------------------------------------------
	
	@Override
	public void addInitialState(String newInitial) {
		// TODO Auto-generated method stub
	}

	public void addInitialState(State newState) {
		
	}
	
	@Override
	public boolean removeInitialState(String stateName) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
