package fsm;

import fsm.attribute.Deterministic;
import fsm.attribute.Observability;
import support.EventMap;
import support.ReadWrite;
import support.State;
import support.StateMap;
import support.TransitionFunction;
import support.event.Event;
import support.event.ObservableEvent;
import support.transition.DetTransition;
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

public class DetObsFSM extends FSM<State, DetTransition<State, ObservableEvent>, ObservableEvent>
		implements	Observability<State, DetTransition<State, ObservableEvent>, ObservableEvent>,
					Deterministic<State, DetTransition<State, ObservableEvent>, ObservableEvent> {
	
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
	 * Constructor for a DetObsFSM that takes any FSM as a parameter and creates a new
	 * DetObsFSM using that as the basis. Any information which is not permissible in a
	 * DetObsFSM is thrown away, because it does not have any means to handle it.
	 * 
	 * @param other FSM to copy as a DetObsFSM (can be any kind of FSM).
	 * @param inId Id for the new FSM to carry.
	 */
	
	public DetObsFSM(FSM<State, Transition<State, Event>, Event> other, String inId) {
		id = inId;
		states = new StateMap<State>(State.class);
		events = new EventMap<ObservableEvent>(ObservableEvent.class);
		transitions = new TransitionFunction<State, DetTransition<State, ObservableEvent>, ObservableEvent>(new DetTransition<State, ObservableEvent>());
		
		// Add in all the states
		for(State s : other.states.getStates())
			this.states.addState(s).setStateInitial(false);
		// Add in all the events
		for(Event e : other.events.getEvents())
			this.events.addEvent(e);
		// Add in all the transitions (but only take the first state it transitions to)
		for(State s : other.states.getStates()) {
			for(Transition<State, Event> t : other.transitions.getTransitions(s)) {
				ArrayList<State> toStates = t.getTransitionStates();
				this.addTransition(s.getStateName(), t.getTransitionEvent().getEventName(), toStates.get(0).getStateName());
			} // for every transition
		} // for every state
		// Add in the initial state
		ArrayList<State> initial = other.getInitialStates();
		initialState = this.getState(initial.get(0));
		initialState.setStateInitial(true);
	} // DetObsFSM(FSM, String)
	
	/**
	 * Constructor for an FSM object that contains no transitions or states, allowing the
	 * user to add those elements their-self.
	 */
	
	public DetObsFSM(String inId) {
		id = inId;
		states = new StateMap<State>(State.class);
		events = new EventMap<ObservableEvent>(ObservableEvent.class);
		transitions = new TransitionFunction<State, DetTransition<State, ObservableEvent>, ObservableEvent>(new DetTransition<State, ObservableEvent>());
		initialState = null;
	} // DetObsFSM(String)
	
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
	public NonDetObsFSM union(FSM<State, DetTransition<State, ObservableEvent>, ObservableEvent> other) {
		NonDetObsFSM newFSM = new NonDetObsFSM();
		unionHelper(other, newFSM);
		return newFSM;
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
		if(name == null)
			name = id;
		String truePath = "";
		truePath = filePath + (filePath.charAt(filePath.length()-1) == '/' ? "" : "/") + name;
		String special = "3\n1\n" + this.getInitialState().getStateName() + "\n";
		ArrayList<String> mark = new ArrayList<String>();
		ArrayList<String> unob = new ArrayList<String>();
		for(State s : this.getStates())
			if(s.getStateMarked())
				mark.add(s.getStateName());
		for(ObservableEvent e : this.getEvents())
			if(!e.getEventObservability())
				unob.add(e.getEventName());
		special += mark.size() + "\n";
		for(String s : mark)
			special += s + "\n";
		special += unob.size() + "\n";
		for(String s : unob)
			special += s + "\n";
		ReadWrite<State, DetTransition<State, ObservableEvent>, ObservableEvent> rdWrt = new ReadWrite<State, DetTransition<State, ObservableEvent>, ObservableEvent>();
		rdWrt.writeToFile(truePath,  special, this.getTransitions());
	}

//---  Getter Methods   -----------------------------------------------------------------------

	@Override
	public Boolean getEventObservability(String eventName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<State> getInitialStates() {
		ArrayList<State> initial = new ArrayList<State>();
		if(initialState != null)
		  initial.add(initialState);
		return initial;
	}
	
	@Override
	public boolean hasInitialState(String stateName) {
		return initialState.getStateName().equals(stateName);
	}

	@Override
	public State getInitialState() {
		return initialState;
	}
	
//---  Setter Methods   -----------------------------------------------------------------------

	@Override
	public void setEventObservability(String eventName, boolean status) {
		// TODO Auto-generated method stub	
	}

//---  Manipulations   ------------------------------------------------------------------------
	
	@Override
	public void addInitialState(String newInitial) {
		// Get the state, or add it if not yet present
		State theState = states.addState(newInitial);
		theState.setStateInitial(true);
		if(initialState != null) 
			initialState.setStateInitial(false);
		initialState = theState;
	}

	public void addInitialState(State newState) {
		State obt = states.addState(newState);
		obt.setStateInitial(true);
		if(initialState != null) 
			initialState.setStateInitial(false);
		initialState = obt;
	}
	
	@Override
	public boolean removeInitialState(String stateName) {
		if(initialState != null && stateName.equals(initialState.getStateName())) {
			initialState.setStateInitial(false);
			initialState = null;
			return true;
		}
		return false;
	}
	
}
