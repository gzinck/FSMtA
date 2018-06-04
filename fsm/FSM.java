package fsm;

import java.io.File;
import java.util.HashMap;

public abstract class FSM {
	//--- Constant Values  -------------------------------------------------------------------------
	
	/** String constant designating this object as a specific type of FSM for clarification purposes*/
	public static final String FSM_TYPE = "FSM";
	/** String constant designating the file extension to append to the file name when writing to the system*/
	public static final String FSM_EXTENSION = ".fsm";
	
	//--- Instance Variables  ----------------------------------------------------------------------
	
	/** HashMap<String, ArrayList<Transition>> containing all the transitions from a given state with
	 * various events that are possible*/
	protected HashMap<String, ArrayList<Transition>> transitions;
	/** String object possessing the identification for this FSM object*/
	protected String id;
	
	//--- Constructors  ----------------------------------------------------------------------------
	
	/**
	 * Constructor for an FSM object that takes in a file encoding the contents of the FSM.
	 * 
	 * @param in - File readin order to create the FSM.
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
	
	//--- Operations  ------------------------------------------------------------------------------
	
	/**
	 * Performs a union operation on two FSMs and returns the result.
	 * 
	 * @param other - The FSM to add to the current FSM in order to create a unioned
	 * FSM.
	 * @return - The result of the union.
	 */
	public FSM union(FSM other) {
		
	} // union(FSM)
	
	/**
	 * Performs a product or intersection operation on two FSMs and returns the result.
	 * @param other - The FSM to perform the product operation on with the current FSM.
	 * @return - The resulting FSM from the product operation.
	 */
	public abstract FSM product(FSM other);
	
	
	
	//--- Constructors  ----------------------------------------------------------------------------
} // class FSM
