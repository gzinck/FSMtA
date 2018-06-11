package fsm;

import java.io.File;
import java.util.*;
import java.util.ArrayList;
import support.*;
import support.transition.*;
import support.event.Event;

/**
 * This class models a Deterministic FSM that expands upon the abstract FSM class to
 * implement the Deterministic characteristics of an FSM - A Single Initial State, and
 * only one State being led to by each Event at a given State.
 * 
 * This class is a part of the fsm package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public class DetFSM extends FSM<State, Transition, Event> {
	
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
	 * @param in - File read in order to create the FSM.
	 * @param id - The id for the FSM (can be any String).
	 */
	
	public DetFSM(File in, String inId) {
		id = inId;
		states = new StateMap<State>(State.class);
		events = new EventMap<Event>(Event.class);
		transitions = new TransitionFunction<Transition>();
		
		// TODO Deal with the actual input here
		// Gibberish goes here
		// Gibberish goes here
		// Gibberish goes here
		// Gibberish goes here
		// Gibberish goes here
		// Gibberish goes here
		System.err.println("Looks like Graeme didn't implement this.");
	} // DetFSM(File)
	
	/**
	 * Constructor for an FSM object that contains no transitions or states, allowing the
	 * user to add those elements their-self.
	 */
	
	public DetFSM(String inId) {
		id = inId;
		states = new StateMap<State>(State.class);
		events = new EventMap<Event>(Event.class);
		transitions = new TransitionFunction<Transition>();
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
		transitions = new TransitionFunction<Transition>();
		initialState = null;
	} // DetFSM()

//---  Single-FSM Operations   ----------------------------------------------------------------
	
	@Override
	public FSM<State, Transition, Event> makeAccessible() {
		DetFSM newFSM = new DetFSM();
		newFSM.addInitialState(this.initialState.getStateName());
		
		// Make a queue to keep track of states that are accessible and their neighbours.
		LinkedList<String> queue = new LinkedList<String>();
		queue.add(this.initialState.getStateName());
		
		while(!queue.isEmpty()) {
			String name = queue.poll();
				
			// Add the transitions from it
			ArrayList<Transition> currTransitions = this.transitions.getTransitions(getState(name));
			ArrayList<Transition> newTransitions = new ArrayList<Transition>();
			
			// Go through the transitions and add the states to the queue
			if(currTransitions != null) {
				for(Transition transition : currTransitions) {
					String stateName = transition.getTransitionStateName();
					if(!newFSM.stateExists(stateName))
						queue.add(stateName);
					State newState = newFSM.states.addState(getState(stateName)); // Get the new state for the transition
					Event newEvent = newFSM.events.addEvent(transition.getTransitionEvent()); // Get the new event
					newTransitions.add(new Transition(newEvent, newState)); // Add a new transition to the new FSM set of transitions
				} // for
				newFSM.transitions.putTransitions(newFSM.getState(name), newTransitions);
			} // if not null
		} // while
		
		return newFSM;
	} // makeAccessible()
	
	@Override
	public FSM<State, Transition, Event> makeCoAccessible() {
		DetFSM newFSM = new DetFSM();
		// When a state is processed, add it to the map and state if it reached a marked state.
		HashMap<String, Boolean> processedStates = new HashMap<String, Boolean>();
		
		// First, just find what states we need to add.
		for(State curr : this.states.getStates()) {
			if(!processedStates.containsKey(curr.getStateName()))
				isCoAccessible(curr, processedStates);
		} // for states
		
		// Second, create the states and add the transitions
		for(Map.Entry<String, Boolean> entry : processedStates.entrySet()) {
			// If the state is coaccessible, add it!
			if(entry.getValue()) {
				State oldState = getState(entry.getKey());
				// Add a new state which is a copy of the state in the original FSM 
				State newState = newFSM.states.addState(oldState);
				
				if(transitions.getTransitions(oldState) != null) { // Only continue if there are transitions from the state
					ArrayList<Transition> newTransitions = new ArrayList<Transition>(); // Add all the transitions that go to states that are coaccessible
					for(Transition t : transitions.getTransitions(oldState)) {
						String toState = t.getTransitionStateName();
						// If it is coaccessible...
						if(processedStates.get(toState)) {
							State newToState = newFSM.states.addState(getState(toState)); // Add a duplicate of the current FSM's state
							Event newEvent = newFSM.events.addEvent(t.getTransitionEvent());
							newTransitions.add(new Transition(newEvent, newToState));
						} // if
					} // for transition
					newFSM.addStateTransitions(newState, newTransitions);
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
	public NonDetFSM union(FSM<State, Transition, Event> other) {
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
		for(Map.Entry<State, ArrayList<Transition>> entry : this.transitions.getAllTransitions()) {
			State currState = newFSM.states.getState(STATE_PREFIX_1 + entry.getKey().getStateName());
			for(Transition t : entry.getValue()) {
				Event newEvent = newFSM.events.getEvent(t.getTransitionEvent());
				State newState = newFSM.states.getState(STATE_PREFIX_1 + t.getTransitionStateName());
				newFSM.transitions.addTransition(currState, new NonDetTransition(newEvent, newState));
			} // for transition
		} // for entry
		for(Map.Entry<State, ArrayList<Transition>> entry : other.transitions.getAllTransitions()) {
			State currState = newFSM.states.getState(STATE_PREFIX_2 + entry.getKey().getStateName());
			for(Transition t : entry.getValue()) {
				Event newEvent = newFSM.events.getEvent(t.getTransitionEvent());
				State newState = newFSM.states.getState(STATE_PREFIX_2 + t.getTransitionStateName());
				newFSM.transitions.addTransition(currState, new NonDetTransition(newEvent, newState));
			} // for transition
		} // for entry
		
		return newFSM;
	}

	@Override
	public DetFSM product(FSM<State, Transition, Event> other) {
		// TODO Auto-generated method stub
		return null;
	}

//---  Getter Methods   -----------------------------------------------------------------------

	/**
	 * This method checks if a State leads to a marked state. In the process, the
	 * method modifies a hashmap of processed states that says 1) if a state has been
	 * evaluated yet, and 2) if so, whether a given state is accessible.  
	 * 
	 * @param curr The state to check for coaccessibility.
	 * @param processedStates HashMap<String, Boolean> mapping string names of states
	 * to true if the state is coaccessible, and false if the state is not. If a state
	 * has not been processed, then the state will not exist in the HashMap.
	 * @return True if the state is coaccessible, false otherwise.
	 */
	
	protected boolean isCoAccessible(State curr, HashMap<String, Boolean> processedStates) {
		// If curr is marked, it is coaccessible so it's OK.
		if(curr.getStateMarked()) {
			processedStates.put(curr.getStateName(), true);
			return true;
		} // if
		// Before recursing, say that this state is processed.
		processedStates.put(curr.getStateName(), false);
		
		// Recurse until find a marked state
		ArrayList<Transition> thisTransitions = transitions.getTransitions(curr);
		if(thisTransitions != null) {
			for(Transition t : thisTransitions) {
				State next = t.getTransitionState();
				// If the next is coaccessible, curr is too.
				if(isCoAccessible(next, processedStates)) {
					processedStates.put(curr.getStateName(), true);
					return true;
				} // if
			} // while
		} // if not null
		// If none are marked
		return false;
	} // isCoAccessible(State, HashMap<String, Boolean>)
	
	@Override
	public ArrayList<State> getInitialStates() {
		ArrayList<State> initial = new ArrayList<State>();
		initial.add(initialState);
		return initial;
	} // getInitialStates()

//---  Add Setter Methods   -----------------------------------------------------------------------
	
	@Override
	public boolean addInitialState(String newInitial) {
		if(states.stateExists(newInitial)) {
			State theState = states.getState(newInitial);
			theState.setStateInitial(true);
			if(initialState != null) initialState.setStateInitial(false);
			initialState = theState;
			return true;
		} else {
			State theState = states.addState(newInitial);
			theState.setStateInitial(true);
			if(initialState != null) initialState.setStateInitial(false);
			initialState = theState;
			return false;
		}
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
