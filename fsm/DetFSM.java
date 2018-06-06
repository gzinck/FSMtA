package fsm;

import java.io.File;
import java.util.*;

import support.*;
import java.util.ArrayList;

import support.State;
import support.transition.Transition;

public class DetFSM extends FSM{
	
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
	public FSM makeAccessible() {
		DetFSM newFSM = new DetFSM();
		
		// Add the initial state
		State newInitial = new State(initialState, id);
		newFSM.addState(newInitial);
		newFSM.initialState = newInitial;
		
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
			ArrayList<Transition> currTransitions = transitions.getTransitions(curr);
			ArrayList<Transition> newTransitions = new ArrayList<Transition>();
			
			// Go through the transitions and add the states to the queue
			for(Transition transition : currTransitions) {
				// Get the current state in the old FSM
				State state = transition.getTransitionState();
				State newState;
				
				if(!newFSM.stateExists(state.getStateName())) {
					// Since the state does not yet exist, add it to the new fsm.
					newState = new State(state, id);
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
	public FSM makeCoAccessible() {
		DetFSM newFSM = new DetFSM();
		// When a state is processed, add it to the map and state if it reached a marked state.
		HashMap<String, Boolean> processedStates = new HashMap<String, Boolean>();
		
		// First, we'll just add the states
		// Start by adding all the states to a queue
		LinkedList<State> queue = new LinkedList<State>(states.getStates());
		while(!queue.isEmpty()) {
			State curr = queue.poll();
			// If the state is not yet processed...
			if(!processedStates.containsKey(curr.getStateName())) {
				recurseCoAc(processedStates, curr);
			} // if
		} // while
		return null;
	} // makeCoAccessible()
	
	private void recurseCoAc(HashMap<String, Boolean> processedStates, State curr) {
		
	} // recurseCoAc()
	
	@Override
	public FSM trim() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void toTextFile(String filePath, String name) {
		// TODO Auto-generated method stub
		
	}
	
//--- Multi-FSM Operations  ------------------------------------------------------------------------------

	@Override
	public FSM union(FSM other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FSM product(FSM other) {
		// TODO Auto-generated method stub
		return null;
	}

//--- Getter/Setter Methods  --------------------------------------------------------------------------

	@Override
	public boolean addState(String newState) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean addState(State newState) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean stateExists(String state) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean removeState(String state) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean toggleMarkedState(String state) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addInitialState(String newState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean removeInitialState(String state) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addStateTransitions(State state, ArrayList<Transition> transitions) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addEvent(String state1, String eventName, String state2) {
		// TODO Auto-generated method stub
		
	}
	
//--- Helper methods --------------------------------------------------------------------------
	
}
