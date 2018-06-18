package fsm;

import java.io.File;
import java.util.*;
import java.util.ArrayList;
import java.util.Map.Entry;

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
		transitions = new TransitionFunction<State, DetTransition<State, Event>, Event>(null);
		ReadWrite<State, DetTransition<State, Event>, Event> redWrt = new ReadWrite<State, DetTransition<State, Event>, Event>();
		
		ArrayList<ArrayList<String>> special = redWrt.readFromFile(states, events, transitions, in);
		initialState = states.getState(special.get(0).get(0));
		states.getState(initialState).setStateInitial(true);
		for(int i = 0; i < special.get(1).size(); i++) {
			states.getState(special.get(1).get(i)).setStateMarked(true);
		}
	} // DetFSM(File)
	
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
	public DetFSM makeCoAccessible() {
		DetFSM newFSM = new DetFSM();
		// First, just find what states we need to add.
		HashMap<String, Boolean> processedStates = new HashMap<String, Boolean>(); // When a state is processed, add it to the map and state if it reached a marked state.
		for(State curr : this.states.getStates()) {
			if(!processedStates.containsKey(curr.getStateName()))
				isCoAccessible(curr, processedStates);
		} // for states

		// Secondly, create the states and add the transitions
		for(Map.Entry<String, Boolean> entry : processedStates.entrySet()) {
			// If the state is coaccessible, add it!
			if(entry.getValue()) {
				State oldState = getState(entry.getKey());
				if(transitions.getTransitions(oldState) != null) { // Only continue if there are transitions from the state
					for(DetTransition<State, Event> t : transitions.getTransitions(oldState))
						if(processedStates.get(t.getTransitionState().getStateName())) // If it is coaccessible...
							newFSM.addTransition(oldState, t); // Add the transition (using copies in the newFSM)
				} // if not null
			} // if coaccessible
		} // for processed state
		
		// Finally, add the initial state
		if(processedStates.get(initialState.getStateName()))
			newFSM.addInitialState(initialState.getStateName());
		return newFSM;
	} // makeCoAccessible()
	
	@Override
	public void toTextFile(String filePath, String name) {
		if(name == null)
			name = id;
		// TODO Actually deal with this.
	}
	
//---  Multi-FSM Operations   -----------------------------------------------------------------

	@Override
	public NonDetFSM union(FSM<State, DetTransition<State, Event>, Event> other) {
		NonDetFSM newFSM = new NonDetFSM();
		
		// Add initial states
		newFSM.addInitialState(STATE_PREFIX_1 + initialState.getStateName());
		for(State s : other.getInitialStates()) // Add the states from the other FSM
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
		
		return newFSM;
	}

	@Override
	public DetFSM product(FSM<State, DetTransition<State, Event>, Event> other) {
		// TODO Auto-generated method stub
		return null;
	}

//---  Getter Methods   -----------------------------------------------------------------------
	
	@Override
	public ArrayList<State> getInitialStates() {
		ArrayList<State> initial = new ArrayList<State>();
		initial.add(initialState);
		return initial;
	} // getInitialStates()

//---  Add Setter Methods   -----------------------------------------------------------------------
	
	@Override
	public void addInitialState(String newInitial) {
		// Get the state, or add it if not yet present
		State theState = states.addState(newInitial);
		theState.setStateInitial(true);
		if(initialState != null) initialState.setStateInitial(false);
		initialState = theState;
	}

//---  Remove Setter Methods   ------------------------------------------------------------------------

	@Override
	public boolean removeInitialState(String stateName) {
		if(stateName.equals(initialState.getStateName())) {
			initialState.setStateInitial(false);
			initialState = null;
			return true;
		}
		return false;
	}
	
} // class DetFSM
