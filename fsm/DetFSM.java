package fsm;

import java.io.File;
import java.util.*;
import java.util.ArrayList;

import support.*;
import support.transition.*;
import support.event.Event;
import support.ReadWrite;

/**
 * This class models a Deterministic FSM that expands upon the abstract FSM class to
 * implement the Deterministic characteristics of an FSM - A Single Initial State, and
 * only one State being led to by each Event at a given State.
 * 
 * This class is a part of the fsm package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public class DetFSM extends FSM<State, DetTransition<State, Event>, Event> {
	
//---  Constant Values   ----------------------------------------------------------------------

	/** String constant designating this object as a specific type of FSM for clarification purposes*/
	public static final String FSM_TYPE = "Deterministic FSM";
	
//---  Instance Variables   -------------------------------------------------------------------
	
	/** State object with the initial state for the deterministic FSM. */
	protected State initialState;
	
//---  Constructors   -------------------------------------------------------------------------

	/**
	 * Constructor for an DetFSM object that takes in a file encoding the contents of the FSM.
	 * 
	 * DetFSM File Order for Special: Initial, Marked.
	 * 
	 * @param in - File read in order to create the FSM.
	 * @param id - The id for the FSM (can be any String).
	 */
	
	public DetFSM(File in, String inId) {
		id = inId;
		
		states = new StateMap<State>(State.class);
		events = new EventMap<Event>(Event.class);
		transitions = new TransitionFunction<State, DetTransition<State, Event>, Event>(new DetTransition<State, Event>());
		ReadWrite<State, DetTransition<State, Event>, Event> redWrt = new ReadWrite<State, DetTransition<State, Event>, Event>();
		
		ArrayList<ArrayList<String>> special = redWrt.readFromFile(states, events, transitions, in);
		initialState = states.getState(special.get(0).get(0));
		states.getState(initialState).setStateInitial(true);
		for(int i = 0; i < special.get(1).size(); i++) {
			states.getState(special.get(1).get(i)).setStateMarked(true);
		}
	} // DetFSM(File)
	
	/**
	 * Constructor for a DetFSM that takes any FSM as a parameter and creates a new
	 * DetFSM using that as the basis. Any information which is not permissible in a
	 * DetFSM is thrown away, because it does not have any means to handle it.
	 * 
	 * @param other FSM to copy as a DetFSM (can be any kind of FSM).
	 * @param inId Id for the new FSM to carry.
	 */
	
	public DetFSM(FSM<State, Transition<State, Event>, Event> other, String inId) {
		id = inId;
		states = new StateMap<State>(State.class);
		events = new EventMap<Event>(Event.class);
		transitions = new TransitionFunction<State, DetTransition<State, Event>, Event>(new DetTransition<State, Event>());
		
		// Add in all the states
		for(State s : other.states.getStates())
			this.states.addState(s);
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
		initialState = initial.get(0);
	} // DetFSM()
	
	/**
	 * Constructor for an FSM object that contains no transitions or states, allowing the
	 * user to add those elements their-self.
	 */
	
	public DetFSM(String inId) {
		id = inId;
		states = new StateMap<State>(State.class);
		events = new EventMap<Event>(Event.class);
		transitions = new TransitionFunction<State, DetTransition<State, Event>, Event>(new DetTransition<State, Event>());
		initialState = null;
	} // DetFSM()
	
	/**
	 * Constructor for an FSM object that contains no transitions or states, allowing the
	 * user to add those elements their-self. It has no id, either.
	 */
	
	public DetFSM() {
		id = "";
		states = new StateMap<State>(State.class);
		events = new EventMap<Event>(Event.class);
		transitions = new TransitionFunction<State, DetTransition<State, Event>, Event>(new DetTransition<State, Event>());
		initialState = null;
	} // DetFSM()

//---  Single-FSM Operations   ----------------------------------------------------------------
	
	@Override
	public void toTextFile(String filePath, String name) {
		if(name == null)
			name = id;
		String truePath = "";
		truePath = filePath + (filePath.charAt(filePath.length()-1) == '/' ? "" : "/") + name;
		String special = "2\n1\n" + this.getInitialState().getStateName() + "\n?\n";
		int counter = 0;
		for(State s : this.getStates()) {
			if(s.getStateMarked()) {
				counter++;
				special += s.getStateName() + "\n";
			}
		}
		special = special.replace("?", counter+"");
		ReadWrite<State, DetTransition<State, Event>, Event> rdWrt = new ReadWrite<State, DetTransition<State, Event>, Event>();
		rdWrt.writeToFile(truePath,  special, this.getTransitions());
	}
	
//---  Multi-FSM Operations   -----------------------------------------------------------------

	@Override
	public NonDetFSM union(FSM<State, DetTransition<State, Event>, Event> other) {
		NonDetFSM newFSM = new NonDetFSM();
		// Add initial states
		newFSM.addInitialState(STATE_PREFIX_1 + initialState.getStateName());
		for(State s : other.getInitialStates())  // Add the states from the other FSM
			newFSM.addInitialState(STATE_PREFIX_2 + s.getStateName());
		// Add other states as well
		for(State s : this.states.getStates())
			newFSM.states.addState(s, STATE_PREFIX_1);
		for(State s : other.states.getStates())
			newFSM.states.addState(s, STATE_PREFIX_2);
		// Add events
		for(Event e : this.events.getEvents())
			newFSM.events.addEvent(e);
		for(Event e : other.events.getEvents())
			newFSM.events.addEvent(e);
		// Add transitions
		for(Map.Entry<State, ArrayList<DetTransition<State, Event>>> entry : this.transitions.getAllTransitions()) {
			State currState = newFSM.states.getState(STATE_PREFIX_1 + entry .getKey().getStateName());
			for(DetTransition<State, Event> t : entry.getValue()) {
				Event newEvent = newFSM.events.getEvent(t.getTransitionEvent());
				State newState = newFSM.states.getState(STATE_PREFIX_1 + t.getTransitionState().getStateName());
				newFSM.transitions.addTransition(currState, new NonDetTransition<State, Event>(newEvent, newState));
			} // for transition
		} // for entry
		for(Map.Entry<State, ArrayList<DetTransition<State, Event>>> entry : other.transitions.getAllTransitions()) {
			State currState = newFSM.states.getState(STATE_PREFIX_2 + entry.getKey().getStateName());
			for(DetTransition<State, Event> t : entry.getValue()) {
				Event newEvent = newFSM.events.getEvent(t.getTransitionEvent());
				State newState = newFSM.states.getState(STATE_PREFIX_2 + t.getTransitionState().getStateName());
				newFSM.transitions.addTransition(currState, new NonDetTransition<State, Event>(newEvent, newState));
			} // for transition
		} // for entry
		unionHelper(other, newFSM);
		return newFSM;
	}

	@Override
	public DetFSM product(FSM<State, DetTransition<State, Event>, Event> other) {
		DetFSM newFSM = new DetFSM();
		productHelper(other, newFSM);
		return newFSM;
	}
	
	@Override
	public DetFSM parallelComposition(FSM<State, DetTransition<State, Event>, Event> other) {
		DetFSM newFSM = new DetFSM();
		parallelCompositionHelper(other, newFSM);
		return newFSM;
	}

//---  Getter Methods   -----------------------------------------------------------------------
	
	@Override
	public ArrayList<State> getInitialStates() {
		ArrayList<State> initial = new ArrayList<State>();
		if(initialState != null)
		  initial.add(initialState);
		return initial;
	} // getInitialStates()
	
	public State getInitialState() {
		return initialState;
	}

//---  Add Setter Methods   -----------------------------------------------------------------------
	
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
	
//---  Remove Setter Methods   ------------------------------------------------------------------------

	@Override
	public boolean removeInitialState(String stateName) {
		if(initialState != null && stateName.equals(initialState.getStateName())) {
			initialState.setStateInitial(false);
			initialState = null;
			return true;
		}
		return false;
	}
	
} // class DetFSM
