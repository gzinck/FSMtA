package support;

import java.util.*;

/**
 * This class models a State object in an FSM, storing information about its Name, the FSM it is a part of, and whether or
 * not it is Initial or Marked.
 * 
 * This class is a part of the support package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public class State implements Comparator<State>, Comparable<State>{
	
//--- Constants   --------------------------------------------------------------------
	
	/** String representing the prefix for a dummy state used for creating a line to an initial state when performing makeDotString(). */
	protected static final String DUMMY_STATE_NAME = "___NEVER_USE_THIS_NAME___";
	
//--- Instance Variables   --------------------------------------------------------------------

	/** String instance variable representing the name of this State object*/
	private String id;
	/** boolean instance variable representing the status of this State's being initial*/
	private boolean initial;
	/** boolean instance variable representing the status of this State's being marked*/
	private boolean marked;

//--- Constructors   --------------------------------------------------------------------------
	
	/**
	 * Constructor for a State object that assigns values to the object's id, initial status,
	 * and marked status as defined by the user input.
	 * 
	 * @param name - String object representing the name of this State object
	 * @param init - boolean value representing the status of this State object's being an initial State
	 * @param mark - boolean value representing the status of this State object's being a marked State
	 */
	
	public State(String name, boolean init, boolean mark) {
		id = name;
		initial = init;
		marked = mark;
	}
	
	/**
	 * Constructor for a State object that assigns a name to the object, and
	 * assigns, according to the user-defined code value, values to the initial and marked
	 * boolean values.
	 * 
	 * Code: 0  - Initial: false, Marked: false
	 *       1  - Initial: false, Marked: true
	 *       2  - Initial: true, Marked: false
	 *       3  - Initial: true, Marked: true
	 * 
	 * @param name - String object representing the name of this State object
	 * @param code - int value describing how to configure the initial and marked boolean values
	 */
	
	public State(String name, int code) {
		id = name;
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
	 * Constructor for a State object that copies the values stored in an existing State
	 * object into this State object.
	 * 
	 * @param replace - State object that's contents are copied into the object being constructed
	 */
	
	public State(State replace) {
		id = replace.getStateName();
		initial = replace.getStateInitial();
		marked = replace.getStateMarked();
	}
	
	/**
	 * Constructor for a State object which creates a new id by combining the two parameter
	 * States. It has the initial/marked properties only if both state1 and state2 have the
	 * property. This is useful when creating states during product operations, etc.
	 * 
	 * @param state1 The first state to inherit from.
	 * @param state2 The second state to inherit from.
	 */
	public State(State state1, State state2) {
		id = "(" + state1.getStateName() + ", " + state2.getStateName() + ")";
		initial = (state1.initial && state2.initial);
		marked = (state1.marked && state2.marked);
	}
	
	/**
	 * Constructor for a State object that assigns a name to this object as defined by
	 * the input and defaults the initial and marked values to false.
	 * 
	 * @param name - String object representing the State object's name
	 */
	
	public State(String name) {
		id = name;
		initial = false;
		marked = false;
	}
	
	/**
	 * Constructor for a State object that contains nothing, not even an id. This is NOT
	 * for general use, and it should only be used when immediately followed by setting
	 * the State's id (see StateMap's addState(String) method to understand why this is
	 * necessary with the generic type instantiation).
	 */
	
	public State() {
		id = "";
		initial = false;
		marked = false;
	}

//---  Operations   ---------------------------------------------------------------------------
	
	/**
	 * Getter method to acquire a copy of the state via a Constructor that takes in a State and
	 * copies its contents.
	 * 
	 * @return - Returns an object extending State that represents a disjoint object identical
	 * to the original State extending object.
	 */
	
	public <S extends State> S copy() {
		// For use in other areas, and when State is extended, this is
		// necessary.
		return (S)(new State(this));
	}
	
	/**
	 * Makes a new state that performs the AND operation on all the properties
	 * and adopts the name of the calling state combined with the other state,
	 * like (1, 2) if the the two states are 1 and 2.
	 * 
	 * @param other The State to combine with.
	 * @return State representing the merge of the calling state and the parameter
	 * state, where the AND logical operator is performed on all the properties.
	 */
	public <S extends State> S makeStateWith(State other) {
		return (S)new State(this, other);
	}
	
	/**
	 * Makes a String object which has the dot representation of the state, which
	 * can be used for sending an FSM to GraphViz.
	 * 
	 * @return - Returns a String object containing the dot representation of the State.
	 */
	
	public String makeDotString() {
		StringBuilder sb = new StringBuilder();
		// If marked, make the state have a double circle.
		if(marked)
			sb.append("\"" + id + "\"[shape = doublecircle];");
		// Else, just make the normal state.
		else
			sb.append("\"" + id + "\"[shape = circle];");
		// If initial, make the state have a line going into it.
		if(initial) {
			sb.append("\"" + DUMMY_STATE_NAME + id + "\"[fontSize = 1 shape = point];");
			sb.append("{\"" + DUMMY_STATE_NAME + id + "\"}->{\"" + id + "\"};");
		}
		return sb.toString();
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
	 * Setter method to assign a new boolean value to this State object's initial boolean instance variable
	 * 
	 * @param init - boolean value representing the new status of this State object's being Initial
	 */
	
	public void setStateInitial(boolean init) {
		initial = init;
	}
	
	/**
	 * Setter method to assign a new boolean value to this State object's marked boolean instance variable
	 * 
	 * @param init - boolean value representing the new status of this State object's being Marked
	 */
	
	public void setStateMarked(boolean init) {
		marked = init;
	}

//---  Miscellaneous   ------------------------------------------------------------------------
	
	/**
	 * This method is implemented as a part of the Comparable interface, defining how State objects should be compared.
	 * 
	 * @param st1 - State object provided as the first of two objects to compare to one another for sorting purposes. 
	 * @param st2 - State objects provided as the second of two objects to compare to one another for sorting purposes. 
	 */
	
	@Override
	public int compare(State st1, State st2) {
		return st1.getStateName().compareTo(st2.getStateName());
	}

	@Override
	public int compareTo(State o) {
		return this.getStateName().compareTo(o.getStateName());
}

}
