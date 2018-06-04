package fsm;

import java.io.File;
import java.util.*;

public abstract class FSM {
	//--- Constant Values  -------------------------------------------------------------------------
	
	/** String constant designating this object as a specific type of FSM for clarification purposes*/
	public static final String FSM_TYPE = "FSM";
	/** String constant designating the file extension to append to the file name when writing to the system*/
	public static final String FSM_EXTENSION = ".fsm";
	
	//--- Instance Variables  ----------------------------------------------------------------------
	
	protected ArrayList<State> states;
	/** HashMap<String, ArrayList<Transition>> containing all the transitions from a given state with
	 * various events that are possible*/
	protected HashMap<State, ArrayList<Transition>> transitions;
	/** String object possessing the identification for this FSM object*/
	protected String id;
	
	//--- Constructors  ----------------------------------------------------------------------------
	
	/**
	 * Constructor for an FSM object that takes in a file encoding the contents of the FSM.
	 * 
	 * @param in - File read in order to create the FSM.
	 */
	public FSM(File in) {
		id = "";
	} // FSM(File)
	
	/**
	 * Constructor for an FSM object that contains no transitions or states, allowing the
	 * user to add those elements him/herself.
	 */
	public FSM() {
		id = "";
	}
	
	//--- Single-FSM Operations  ------------------------------------------------------------------------------
	/**
	 * Renames all the states in the set of states in the FSM so that
	 * states are named sequentially ("0", "1", "2"...).
	 */
	public void renameStates() {
		int index = 0;
		for(State state : states)
			state.setStateName(index + "");
	} // renameStates()
	
	/**
	 * Searches through the graph represented by the transitions hashmap, and removes
	 * disjoint elements.
	 * 
	 * Algorithm starts from all initial States, and adds them to a queue. They are
	 * then placed into the new transitions HashMap, and all States reachable by these
	 * initial states are placed in a queue for processing. Once dealt with, a State is
	 * added to a HashSet to keep track of what has already been seen.
	 * 
	 * @return - Returns an FSM object representing the accessible version of the current
	 * FSM. 
	 */
	public abstract FSM makeAccessible();
	
	//--- Multi-FSM Operations  ------------------------------------------------------------------------------
	
	/**
	 * Performs a union operation on two FSMs and returns the result.
	 * 
	 * @param other - The FSM to add to the current FSM in order to create a unioned
	 * FSM.
	 * @return - The result of the union.
	 */
	public abstract FSM union(FSM other);
	
	/**
	 * Performs a product or intersection operation on two FSMs and returns the result.
	 * @param other - The FSM to perform the product operation on with the current FSM.
	 * @return - The resulting FSM from the product operation.
	 */
	public abstract FSM product(FSM other);
	
	
	
	//--- Constructors  ----------------------------------------------------------------------------
} // class FSM
