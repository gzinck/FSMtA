package fsm;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import support.*;

public class DetFSM extends FSM{
	
//--- Constructors  ----------------------------------------------------------------------------

	/**
	 * Constructor for an DetFSM object that takes in a file encoding the contents of the FSM.
	 * 
	 * @param in - File read in order to create the FSM.
	 * @param id - The id for the FSM (can be any String).
	 */
	public DetFSM(File in, String inId) {
		id = inId;
		states = new HashMap<String, State>();
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
		states = new HashMap<String, State>();
		transitions = new HashMap<State, ArrayList<Transition>>();
	} // DetFSM()

//--- Single-FSM Operations  ------------------------------------------------------------------------------
	
	@Override
	public FSM makeAccessible() {
		// TODO Auto-generated method stub
		return null;
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
	
	
}
