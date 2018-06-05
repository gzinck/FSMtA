package support;

/**
 * This class models a State object in an FSM.
 * 
 * This class is a part of the support package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public class State {
	
//--- Instance Variables   --------------------------------------------------------------------

	/** String instance variable representing the name of this State object*/
	private String id;
	/** String instance variable representing the name of the FSM that this State object is a part of*/
	private String fsm;
	/** boolean instance variable representing the status of this State's being initial*/
	private boolean initial;
	/** boolean instance variable representing the status of this State's being marked*/
	private boolean marked;

//--- Constructors   --------------------------------------------------------------------------
	
	/**
	 * Constructor for a State object that assigns values to the object's id, parent-FSM, initial status,
	 * and marked status as defined by the user input.
	 * 
	 * @param name - String object representing the name of this State object
	 * @param parent - String object representing the name of this State object's FSM-parent
	 * @param init - boolean value representing the status of this State object's being an initial State
	 * @param mark - boolean value representing the status of this State object's being a marked State
	 */
	
	public State(String name, String parent, boolean init, boolean mark) {
		id = name;
		fsm = parent;
		initial = init;
		marked = mark;
	}
	
	/**
	 * Constructor for a State object that assigns a name and parent-FSM to the object, and
	 * assigns, according to the user-defined code value, values to the initial and marked
	 * boolean values.
	 * 
	 * Code: 0  - Initial: false, Marked: false
	 *       1  - Initial: false, Marked: true
	 *       2  - Initial: true, Marked: false
	 *       3  - Initial: true, Marked: true
	 * 
	 * @param name - String object representing the name of this State object
	 * @param parent - String object representing the name of this State object's FSM-parent
	 * @param code - int value describing how to configure the initial and marked boolean values
	 */
	
	public State(String name, String parent, int code) {
		id = name;
		fsm = parent;
		switch(code) {
		  case 0: initial = false;
		  		  marked = false;
		  		  break;
		  case 1: marked = true;
		  		  initial = false;
		  		  break;
		  case 2: initial = true;
		  		  marked = false;
		  		  break;  
		  case 3: initial = true;
		  		  marked = true;
		  		  break;
		  default:
				  break;
		}
	}
	
	/**
	 * Constructor for a State object that assigns a name and parent-FSM to the object, and
	 * defaults the initial and marked boolean values to false.
	 * 
	 * @param name - String object representing the State object's name
	 * @param parent - String object representing the State object's parent's name
	 */
	
	public State(String name, String parent) {
		id = name;
		fsm = parent;
		initial = false;
		marked = false;
	}
	
	/**
	 * Constructor for a State object that assigns a name to this object as defined by
	 * the input, defaults the parent-FSM String, and defaults the initial and marked
	 * values to false.
	 * 
	 * @param name - String object representing the State object's name
	 */
	
	public State(String name) {
		id = name;
		fsm = "";
		initial = false;
		marked = false;
	}
	
	/**
	 * Constructor for a State object that copies the values stored in an existing State
	 * object into this State object.
	 * 
	 * @param replace - State object that's contents are copied into the object being constructed
	 */
	
	public State(State replace) {
		id = replace.getStateName();
		fsm = replace.getStateFSM();
		initial = replace.getStateInitial();
		marked = replace.getStateMarked();
	}
	
//--- Getter Methods   ------------------------------------------------------------------------
	
	/**
	 * Getter method to access the State object's name
	 * 
	 * @return - Returns a String object representing the State object's name
	 */
	
	public String getStateName() {
		return id;
	}
	
	/**
	 * Getter method to access the State object's parent's name
	 * 
	 * @return - Returns a String object representing the State object's parent's name
	 */
	
	public String getStateFSM() {
		return fsm;
	}
	
	/**
	 * Getter method to request the status of this State being initial
	 * 
	 * @return - Returns a boolean value representing the status of this State being initial
	 */
	
	public boolean getStateInitial() {
		return initial;
	}
	
	/**
	 * Getter method to request the status of this State being marked
	 * 
	 * @return - Returns a boolean value representing the status of this State being marked
	 */
	
	public boolean getStateMarked() {
		return marked;
	}
	
//--- Setter Methods   ------------------------------------------------------------------------
	
	/**
	 * Setter method to replace the currently stored object representing the State object's name with the provided String
	 * 
	 * @param in - String object representing the new name to assign to this State object
	 */
	
	public void setStateName(String in) {
		id = in;
	}
	
	/**
	 * Setter method to replace the currently stored object representing the State object's parent's name with the provided String
	 * 
	 * @param in - String object representing the new parent's name to assign this State object 
	 */
	
	public void setStateFSM(String in) {
		fsm = in;
	}
	
	/**
	 * Setter method to assign a new boolean value to this State object's initial boolean instance variable
	 * 
	 * @param init - boolean value representing the new status of this State object's being Initial
	 */
	
	public void setStateInitial(boolean init) {
		
	}
	
	/**
	 * Setter method to assign a new boolean value to this State object's marked boolean instance variable
	 * 
	 * @param init - boolean value representing the new status of this State object's being Marked
	 */
	
	public void setStateMarked(boolean init) {
		
	}
	
}
