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
 * Implements the Interface(s): Deterministic, Observability
 * 
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
		initialState = states.getState(special.get(0).get(0));	//Special ArrayList 0-entry is InitialState
		states.getState(initialState).setStateInitial(true);
		for(int i = 0; i < special.get(1).size(); i++)			//Special ArrayList 1-entry is MarkedState
			states.getState(special.get(1).get(i)).setStateMarked(true);
		for(int i = 0; i < special.get(2).size(); i++)			//Special ArrayList 2-entry is ObservableEvent
			events.getEvent(special.get(2).get(i)).setEventObservability(false);
	}
	
	/**
	 * Constructor for a DetObsFSM that takes any FSM as a parameter and creates a new
	 * DetObsFSM using that as the basis. Any information which is not permissible in a
	 * DetObsFSM is thrown away, because it does not have any means to handle it.
	 * 
	 * @param other - FSM<<r>S, T, E> object that is provided to be copied into the DetObsFSM object being constructed.
	 * @param inId - String object representing the id for the new FSM object to carry.
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
	 * Constructor for a DetObsFSM object that initializes its instance variables, leaving
	 * them empty for later usage, and assigns a provided String as this object's id.
	 * 
	 * @param inId - String object representing the id associated to this DetObsFSM object.
	 */
	
	public DetObsFSM(String inId) {
		id = inId;
		states = new StateMap<State>(State.class);
		events = new EventMap<ObservableEvent>(ObservableEvent.class);
		transitions = new TransitionFunction<State, DetTransition<State, ObservableEvent>, ObservableEvent>(new DetTransition<State, ObservableEvent>());
		initialState = null;
	}
	
	/**
	 * Constructor for a DetObsFSM object that contains no transitions or states, allowing the
	 * user to add those elements their-self. It has no id, either, initializing the
	 * instance variables and assigning the id the empty String.
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
		// TODO Finish the special aspects
		return newFSM;
	}

	@Override
	public DetObsFSM product(FSM<State, DetTransition<State, ObservableEvent>, ObservableEvent> other) {
		DetObsFSM newFSM = new DetObsFSM();
		productHelper(other, newFSM);
		// TODO Finish the special aspects
		return newFSM;
	}
	
	@Override
	public DetObsFSM parallelComposition(FSM<State, DetTransition<State, ObservableEvent>, ObservableEvent> other) {
		DetObsFSM newFSM = new DetObsFSM();
		parallelCompositionHelper(other, newFSM);
		// TODO Finish the special aspects
		return newFSM;
	}

	@Override
	public void toTextFile(String filePath, String name) {
		if(name == null)		//If no provided name, use FSM object's id
			name = id;
		String truePath = "";		//Need to develop the actual filePath w/ respect to file name
		truePath = filePath + (filePath.charAt(filePath.length()-1) == '/' ? "" : "/") + name;	//Handle awkward '/' cases
		String special = "3\n1\n" + this.getInitialState().getStateName() + "\n";		//3 Special types at head of File, first is Initial
		ArrayList<String> mark = new ArrayList<String>();		//Need to process States and Events to find how many for notation and scripture
		ArrayList<String> unob = new ArrayList<String>();
		for(State s : this.getStates())					//If a State is marked, add to list
			if(s.getStateMarked())
				mark.add(s.getStateName());
		for(ObservableEvent e : this.getEvents())		//If an Event is found Unobservable, add to list
			if(!e.getEventObservability())
				unob.add(e.getEventName());
		special += mark.size() + "\n";					//Lead section with number of entries to follow, then fill in entries
		for(String s : mark)
			special += s + "\n";
		special += unob.size() + "\n";					//Follow with number of Unobservable Events, fill in entries
		for(String s : unob)
			special += s + "\n";
		ReadWrite<State, DetTransition<State, ObservableEvent>, ObservableEvent> rdWrt = new ReadWrite<State, DetTransition<State, ObservableEvent>, ObservableEvent>();
		rdWrt.writeToFile(truePath,  special, this.getTransitions());		//Let ReadWrite handle Transition scripture, supply filePath and precomputed
	}

//---  Getter Methods   -----------------------------------------------------------------------

	@Override
	public Boolean getEventObservability(String eventName) {
		if(events.getEvent(eventName) == null)		//If the Event is not found, null
			return null;
		return events.getEvent(eventName).getEventObservability();		//Otherwise, result of the query
	}

	@Override
	public ArrayList<State> getInitialStates() {
		ArrayList<State> initial = new ArrayList<State>();
		if(initialState != null)				//Single entry ArrayList if initialState exists, otherwise don't add null; compatibility
		  initial.add(initialState);
		return initial;
	}

	@Override
	public State getInitialState() {
		return initialState;
	}
	
//---  Setter Methods   -----------------------------------------------------------------------

	@Override
	public boolean setEventObservability(String eventName, boolean status) {
		if(events.getEvent(eventName) != null) {		//Ensure it exists
			events.getEvent(eventName).setEventObservability(status);		//Apply the change
			return true;			//Success
		}
		return false;		//Fail, no such Event exists
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

	@Override
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
