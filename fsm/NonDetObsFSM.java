package fsm;

import fsm.attribute.Observability;
import support.EventMap;
import support.ReadWrite;
import support.State;
import support.StateMap;
import support.TransitionFunction;
import support.transition.DetTransition;
import support.transition.NonDetTransition;
import support.event.Event;
import support.event.ObservableEvent;
import java.io.*;
import java.util.ArrayList;

/**
 * This class models an Observable NonDeterministic FSM that expands upon the NonDetFSM class to
 * implement the Observable characteristics of an FSM - Events storing information about their
 * Observability, and some operations being made available to interact with such information.
 * 
 * This class is a part of the fsm packge.
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public class NonDetObsFSM extends FSM<State, NonDetTransition<State, ObservableEvent>, ObservableEvent> implements Observability<State, NonDetTransition<State, ObservableEvent>, ObservableEvent>{
	
//--- Constant Values  -------------------------------------------------------------------------

	/** String constant designating this object as a specific type of FSM for clarification purposes*/
	public static final String FSM_TYPE = "NonDetObs FSM";
	
//--- Instance Variables  ----------------------------------------------------------------------
	
	/** ArrayList<<s>State> object that holds a list of Initial States for this Non Deterministic FSM object. */
	protected ArrayList<State> initialStates;
	
//---  Constructors  --------------------------------------------------------------------------

	/**
	 * Constructor for a NonDetObsFSM object that takes in a file and String id as input, processing
	 * the file to fill the object's contents and titling it as the defined String input.
	 * 
	 * NonDetObsFSM File Order for Special: Initial, Marked, Unobservable Events.
	 * 
	 * @param in - File object provided to be read to generate the NonDetObsFSM object.
	 * @param id - String object provided to be the name of the generated NonDetObsFSM object.
	 */
	
	public NonDetObsFSM(File in, String inId) {
		id = inId;
		
		states = new StateMap<State>(State.class);
		events = new EventMap<ObservableEvent>(ObservableEvent.class);
		transitions = new TransitionFunction<State, NonDetTransition<State, ObservableEvent>, ObservableEvent>(new NonDetTransition<State, ObservableEvent>());
		ReadWrite<State, NonDetTransition<State, ObservableEvent>, ObservableEvent> redWrt = new ReadWrite<State, NonDetTransition<State, ObservableEvent>, ObservableEvent>();
		
		ArrayList<ArrayList<String>> special = redWrt.readFromFile(states, events, transitions, in);
		initialStates = new ArrayList<State>();
		for(int i = 0; i < special.get(0).size(); i++) {
			states.getState(special.get(0).get(i)).setStateInitial(true);
			initialStates.add(states.getState(special.get(0).get(i)));
		}
		for(int i = 0; i < special.get(1).size(); i++) {
			states.getState(special.get(1).get(i)).setStateMarked(true);
		}
		for(int i = 0; i < special.get(2).size(); i++) {
			events.getEvent(special.get(2).get(i)).setEventObservability(false);
		}
	}
	
	/**
	 * Constructor for an FSM object that has an ID.
	 * It contains no transitions or states, allowing the user to add those elements him/herself.
	 * 
	 * @param inID String representing the id for the FSM.
	 */
	
	public NonDetObsFSM(String inID) {
		id = inID;
		states = new StateMap<State>(State.class);
		events = new EventMap<ObservableEvent>(ObservableEvent.class);
		transitions = new TransitionFunction<State, NonDetTransition<State, ObservableEvent>, ObservableEvent>(new NonDetTransition<State, ObservableEvent>());
		initialStates = new ArrayList<State>();
	}
	
	/**
	 * Constructor for an FSM object that contains no transitions or states, allowing the
	 * user to add those elements him/herself. It has no id, either.
	 */
	
	public NonDetObsFSM() {
		id = "";
		states = new StateMap<State>(State.class);
		events = new EventMap<ObservableEvent>(ObservableEvent.class);
		transitions = new TransitionFunction<State, NonDetTransition<State, ObservableEvent>, ObservableEvent>(new NonDetTransition<State, ObservableEvent>());
		initialStates = new ArrayList<State>();
	}
		
//---  Operations   ---------------------------------------------------------------------------
		
	@Override
	public FSM<State, NonDetTransition<State, ObservableEvent>, ObservableEvent> createObserverView() {
		// TODO Auto-generated method stub
		return null;
	}
	
//---  Getter Methods   -----------------------------------------------------------------------

	@Override
	public Boolean getEventObservability(String eventName) {
		// TODO Auto-generated method stub
		return null;
	}
	
//---  Setter Methods   -----------------------------------------------------------------------

	@Override
	public void setEventObservability(String eventName, boolean status) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public FSM<State, NonDetTransition<State, ObservableEvent>, ObservableEvent> makeCoAccessible() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void toTextFile(String filePath, String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public FSM union(FSM<State, NonDetTransition<State, ObservableEvent>, ObservableEvent> other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NonDetObsFSM product(FSM<State, NonDetTransition<State, ObservableEvent>, ObservableEvent> other) {
		NonDetObsFSM newFSM = new NonDetObsFSM();
		productHelper(other, newFSM);
		return newFSM;
	}
	
	@Override
	public NonDetObsFSM parallelComposition(FSM<State, NonDetTransition<State, ObservableEvent>, ObservableEvent> other) {
		NonDetObsFSM newFSM = new NonDetObsFSM();
		parallelCompositionHelper(other, newFSM);
		return newFSM;
	}

	@Override
	public ArrayList<State> getInitialStates() {
		// TODO Auto-generated method stub
		return null;
	}

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
