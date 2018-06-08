package fsm;

import java.io.File;
import java.util.ArrayList;

import support.State;
import support.StateMap;
import support.TransitionFunction;
import support.transition.*;
import support.event.Event;

/**
 * This class models a NonDeterministic FSM that expands upon the abstract FSM class to
 * implement the Non-Deterministic characteristics of an FSM - Multiple Initial States, and
 * multiple States being permitted to be led to by each Event at a given State.
 * 
 * This class is a part of the fsm package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public class NonDetFSM extends FSM<NonDetTransition, Event>{
	
//--- Constant Values  -------------------------------------------------------------------------

	/** String constant designating this object as a specific type of FSM for clarification purposes*/
	public static final String FSM_TYPE = "NonDeterministic FSM";
			
//--- Instance Variables  ----------------------------------------------------------------------
			
	/** State object with the initial state for the deterministic FSM. */
	protected ArrayList<State> initialStates;
		
//---  Constructors  --------------------------------------------------------------------------
		
	/**
	 * Constructor for an DetFSM object that takes in a file encoding the contents of the FSM.
	 * 
	 * @param in - File read in order to create the FSM.
	 * @param id - The id for the FSM (can be any String).
	 */
	
	public NonDetFSM(File in, String inId) {
		id = inId;
		states = new StateMap<State>();
		transitions = new TransitionFunction<NonDetTransition>();
		
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
	
	public NonDetFSM(String inId) {
		id = inId;
		states = new StateMap<State>();
		transitions = new TransitionFunction<NonDetTransition>();
		initialStates = null;
	} // DetFSM()
	
	/**
	 * Constructor for an FSM object that contains no transitions or states, allowing the
	 * user to add those elements him/herself. It has no id, either.
	 */
	
	public NonDetFSM() {
		id = "";
		states = new StateMap<State>();
		transitions = new TransitionFunction<NonDetTransition>();
		initialStates = null;
	} // DetFSM()

//---  Single-FSM Operations   ----------------------------------------------------------------
	
	@Override
	public FSM<NonDetTransition, Event> makeAccessible() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FSM<NonDetTransition, Event> makeCoAccessible() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void toTextFile(String filePath, String name) {
		// TODO Auto-generated method stub
		
	}

//---  Multi-FSM Operations   -----------------------------------------------------------------
	
	@Override
	public FSM<NonDetTransition, Event> union(FSM<NonDetTransition, Event> other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FSM<NonDetTransition, Event> product(FSM<NonDetTransition, Event> other) {
		// TODO Auto-generated method stub
		return null;
	}

//---  Manipulations   ------------------------------------------------------------------------
	
	@Override
	public boolean addInitialState(String newInitial) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addEvent(String state1, String eventName, String state2) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean removeState(String state) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeInitialState(String state) {
		// TODO Auto-generated method stub
		return false;
	}

}
