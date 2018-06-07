package fsm;

import java.io.File;
import java.util.*;
import support.*;
import java.util.ArrayList;
import support.StateMap;
import support.State;
import support.transition.Transition;
import support.event.Event;
import support.TransitionFunction;

public class DetFSM extends FSM<Transition, Event> {
	
//--- Constant Values  -------------------------------------------------------------------------

	/** String constant designating this object as a specific type of FSM for clarification purposes*/
	public static final String FSM_TYPE = "Deterministic FSM";
	
//--- Instance Variables  ----------------------------------------------------------------------
	
	/** State object with the initial state for the deterministic FSM. */
	protected State initialState;
	
//--- Constructors  ----------------------------------------------------------------------------

	/**
	 * Constructor for an DetFSM object that takes in a file encoding the contents of the FSM.
	 * 
	 * @param in - File read in order to create the FSM.
	 * @param id - The id for the FSM (can be any String).
	 */
	public DetFSM(File in, String inId) {
		id = inId;
		states = new StateMap<State>();
		transitions = new TransitionFunction<Transition>();
		
		// Deal with the actual input here
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
	 * user to add those elements him/herself.
	 */
	public DetFSM(String inId) {
		id = inId;
		states = new StateMap<State>();
		transitions = new TransitionFunction<Transition>();
		initialState = null;
	} // DetFSM()
	
	/**
	 * Constructor for an FSM object that contains no transitions or states, allowing the
	 * user to add those elements him/herself. It has no id, either.
	 */
	public DetFSM() {
		id = "";
		states = new StateMap<State>();
		transitions = new TransitionFunction<Transition>();
		initialState = null;
	} // DetFSM()

//--- Single-FSM Operations  ------------------------------------------------------------------------------
	
	@Override
	public FSM<Transition, Event> makeAccessible() {
		DetFSM newFSM = new DetFSM();
		
		// Add the initial state
		newFSM.addState(this.initialState);
		newFSM.initialState = newFSM.states.getState(this.initialState);
		
		// Make a queue to keep track of states that are accessible and their
		// neighbours.
		LinkedList<State> oldQueue = new LinkedList<State>();
		oldQueue.add(this.initialState);
		LinkedList<State> newQueue = new LinkedList<State>();
		oldQueue.add(newFSM.initialState);
		
		while(!oldQueue.isEmpty()) {
			State curr = oldQueue.poll();
			State newCurr = newQueue.poll();
				
			// Add the transitions from it
			ArrayList<Transition> currTransitions = this.transitions.getTransitions(curr);
			ArrayList<Transition> newTransitions = new ArrayList<Transition>();
			
			// Go through the transitions and add the states to the queue
			for(Transition transition : currTransitions) {
				// Get the current state in the old FSM
				State state = transition.getTransitionState();
				State newState;
				
				if(!newFSM.stateExists(state.getStateName())) {
					// Since the state does not yet exist, add it to the new fsm.
					newState = new State(state);
					newFSM.addState(newState);
					oldQueue.add(state);
					newQueue.add(newState);
				} else {
					newState = newFSM.states.getState(state.getStateName());
				}
				
				// Add a new transition to the new FSM set of transitions
				newTransitions.add(new Transition(transition.getTransitionEvent(), newState));
			} // for
			newFSM.transitions.putTransitions(newCurr, newTransitions);
		} // while
		
		return newFSM;
	} // makeAccessible()
	
	@Override
	public FSM<Transition, Event> makeCoAccessible() {
		DetFSM newFSM = new DetFSM();
		// When a state is processed, add it to the map and state if it reached a marked state.
		HashMap<String, Boolean> processedStates = new HashMap<String, Boolean>();
		
		// First, just find what states we need to add.
		for(State curr : this.states.getStates()) {
			if(!processedStates.containsKey(curr.getStateName()))
				isCoAccessible(curr, processedStates);
		} // for
		
		// Second, create the states to add the transitions
		for(Map.Entry<String, Boolean> entry : processedStates.entrySet()) {
			// If the state is coaccessible, add it!
			if(entry.getValue()) {
				State oldState = getState(entry.getKey());
				// Add a new state which is a copy of the state in the original FSM 
				newFSM.addState(oldState);
				
				// Add all the transitions that go to states that are coaccessible
				ArrayList<Transition> newTransitions = new ArrayList<Transition>();
				for(Transition trans : transitions.getTransitions(oldState)) {
					String toState = trans.getTransitionStateName();
					// If it is coaccessible...
					if(processedStates.get(toState))
						newTransitions.add(new Transition(trans.getTransitionEvent(), newFSM.states.getState(toState)));
				} // for transition
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
	
//--- Multi-FSM Operations  ------------------------------------------------------------------------------

	@Override
	public FSM<Transition, Event> union(FSM<Transition, Event> other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FSM<Transition, Event> product(FSM<Transition, Event> other) {
		// TODO Auto-generated method stub
		return null;
	}

//--- Getter/Setter Methods  --------------------------------------------------------------------------
	
	@Override
	public boolean removeState(String state) {
		// If the state exists and is not the initial state...
		if(states.stateExists(state) && !initialState.getStateName().equals(state)) {
			states.removeState(state);
			// Then, we need to remove the state from every reference to it in the transitions.
			return true;
		}
		return false;
	}

	@Override
	public boolean addInitialState(String newInitial) {
		if(states.stateExists(newInitial)) {
			initialState = states.getState(newInitial);
			return true;
		}
		return false;
	}

	@Override
	public boolean removeInitialState(String state) {
		if(state.equals(initialState.getStateName())) {
			initialState = null;
			return true;
		}
		return false;
	}

	@Override
	public boolean addEvent(String state1, String eventName, String state2) {
		if(stateExists(state1) && stateExists(state2)) {
			Event e = events.getEvent(eventName);
			transitions.addTransition(getState(state1), new Transition(e, getState(state2)));
			return true;
		}
		return false;
	}
	
//--- Helper methods --------------------------------------------------------------------------

	/**
	 * isCoAccessible checks if a State leads to a marked state. In the process, the
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
		Iterator<Transition> itr = transitions.getTransitions(curr).iterator();
		while(itr.hasNext()) {
			State next = itr.next().getTransitionState();
			// If the next is coaccessible, curr is too.
			if(isCoAccessible(next, processedStates)) {
				processedStates.put(curr.getStateName(), true);
				return true;
			} // if
		} // while
		// If none are marked
		return false;
	} // isCoAccessible(State, HashMap<String, Boolean>)
} // class DetFSM
