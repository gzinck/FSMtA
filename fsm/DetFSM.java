package fsm;

import java.io.File;
import java.util.*;

import support.*;
import support.transition.Transition;

public class DetFSM extends FSM{
	
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
		transitions = new HashMap<State, ArrayList<Transition>>();
		
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
		transitions = new HashMap<State, ArrayList<Transition>>();
		initialState = null;
	} // DetFSM()
	
	/**
	 * Constructor for an FSM object that contains no transitions or states, allowing the
	 * user to add those elements him/herself. It has no id, either.
	 */
	public DetFSM() {
		id = "";
		states = new StateMap<State>();
		transitions = new HashMap<State, ArrayList<Transition>>();
		initialState = null;
	} // DetFSM()

//--- Single-FSM Operations  ------------------------------------------------------------------------------
	
	@Override
	public FSM makeAccessible() {
		DetFSM newFSM = new DetFSM();
		
		// Add the initial state
		State newInitial = new State(initialState, this);
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
			ArrayList<Transition> currTransitions = this.transitions.get(curr);
			ArrayList<Transition> newTransitions = new ArrayList<Transition>();
			// Go through the transitions and add the states to the queue
			for(Transition transition : currTransitions) {
				// Get the current state in the old FSM
				State state = transition.getTransitionState();
				State newState;
				if(!newFSM.stateExists(state.getStateName())) {
					// Since the state does not yet exist, add it to the new fsm.
					newState = new State(state, this);
					newFSM.addState(newState);
					oldQueue.add(state);
					newQueue.add(newState);
				} else {
					newState = newFSM.states.getState(state.getStateName());
				}
				// Add a new transition to the new FSM set of transitions
				newTransitions.add(new Transition(transition.getTransitionEvent(), newState));
			} // for
			newFSM.transitions.put(newCurr, newTransitions);
		} // while
		
		return newFSM;
	}
	
	@Override
	public FSM makeCoAccessible() {
		// TODO Auto-generated method stub
		return null;
	}
	
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
	public boolean removeState(String state) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean stateExists(String state) {
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
