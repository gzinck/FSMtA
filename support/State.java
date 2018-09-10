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
	/** boolean instance variable representing that status of this State's being bad*/
	private boolean badState;
	/** boolean instance variable representing the status of this State's being private*/
	private boolean privacy;

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
		privacy = false;
		badState = false;
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
		badState = false;
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
	 * Constructor for a State object that takes in a State variable argument and processes it
	 * to create an aggregate State composed of all the provided States in that parameter.
	 * 
	 * @param states - State ... object whose variable contents are used to generate a new State object.
	 */
	
	public State(State ... states) {
		initial = false;
		marked = true;
		badState = false;
		privacy = true;
		String name = "";
		//Arrays.sort(states);		THIS WAS BAD
		for(State s : states) {
			//initial = s.getStateInitial() ? true : initial;
			marked = s.getStateMarked() ? marked : false;
			badState = s.getStateBad() ? true : badState;
			privacy = s.getStatePrivate() ? privacy : false;
			name += s.getStateName() + ",";
		}
		id = "{"+name.substring(0, name.length()-1)+"}";
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
		privacy = replace.getStatePrivate();
		badState = false;
	}
	
	/**
	 * Constructor for a State object which creates a new id by combining the two parameter
	 * States. It has the initial/marked properties only if both state1 and state2 have the
	 * property. This is useful when creating states during product operations, etc.
	 * 
	 * @param state1 - The first State object to inherit from.
	 * @param state2 - The second State object to inherit from.
	 */

	public State(State state1, State state2) {
		id = "(" + state1.getStateName() + "," + state2.getStateName() + ")";
		initial = (state1.initial && state2.initial);
		marked = (state1.marked && state2.marked);
		privacy = (state1.privacy && state2.privacy);
		badState = false;
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
		badState = false;
		privacy = false;
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
		badState = false;
		privacy = false;
	}

//---  Operations   ---------------------------------------------------------------------------
	
	/**
	 * Getter method to acquire a copy of the state via a Constructor that takes in a State and
	 * copies its contents.
	 * 
	 * @return - Returns an object extending State that represents a disjoint object identical
	 * to the original State extending object.
	 */
	
	public State copy() {
		// For use in other areas, and when State is extended, this is
		// necessary.
		return new State(this);
	}
	
	/**
	 * Copies the data from another state into this state. Useful when creating a copy of only
	 * the information visible to a given (perhaps less sophisticated) State.
	 * 
	 * @param other - State object that's attributes are copied into the calling State object.
	 */
	
	public void copyDataFrom(State other) {
		id = other.id;
		initial = other.initial;
		marked = other.marked;
		privacy = other.privacy;
		badState = other.badState;
	}
	
	/**
	 * Makes a new state that performs the AND operation on all the properties
	 * and adopts the name of the calling state combined with the other state;
	 * i.e, (1, 2) if the the two States were 1 and 2.
	 * 
	 * @param other - The State object provided to combine with the calling State object.
	 * @return - Returns a State object representing the merge of the calling state and the parameter
	 * state, where the AND logical operator is performed on all its properties.
	 */
	
	public State makeStateWith(State other) {
		return new State(this, other);
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
			sb.append("\"" + id + "\"[shape = doublecircle");
		// Else, just make the normal state.
		else
			sb.append("\"" + id + "\"[shape = circle");
		
		// If bad, make it a different colour.
		if(badState && privacy)
			sb.append(" color = \"red\"];");
		else if(badState)
			sb.append(" color = \"purple\"];");
		else if(privacy)
			sb.append(" color = \"orange\"];");
		else
			sb.append(" color = \"black\"];");

		sb.append("\n");
		// If initial, make the state have a line going into it.
		if(initial) {
			sb.append("\"" + DUMMY_STATE_NAME + id + "\"[fontSize = 1 shape = point]; \n");
			sb.append("{\"" + DUMMY_STATE_NAME + id + "\"}->{\"" + id + "\"}; \n");
		}
		return sb.toString();
	}
	
//--- Getter Methods   ------------------------------------------------------------------------
	
	/**
	 * Getter method to access the State object's name.
	 * 
	 * @return - Returns a String object representing the State object's name.
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
	
	/**
	 * Getter method to request the status of this State being private
	 * 
	 * @return - Returns a boolean value representing the status of this State being private
	 */
	
	public boolean getStatePrivate() {
		return privacy;
	}
	
	/**
	 * Getter method to request the status of this State being bad
	 * 
	 * @return - Returns a boolean value representing the status of this State being bad
	 */
	
	public boolean getStateBad() {
		return badState;
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
	
	/**
	 * Setter method to assign a new boolean value to this State object's private boolean instance variable
	 * 
	 * @param init - boolean value representing the new status of this State object's being Private
	 */
	
	public void setStatePrivate(boolean init) {
		privacy = init;
	}
	
	/**
	 * Setter method to assign a new boolean value to this State object's bad boolean instance variable
	 * 
	 * @param init - boolean value representing the new status of this State object's being Bad
	 */
	
	public void setStateBad(boolean init) {
		badState = init;
	}

//---  Miscellaneous   ------------------------------------------------------------------------
	
	@Override
	public int compare(State st1, State st2) {
		return st1.getStateName().compareTo(st2.getStateName());
	}

	@Override
	public int compareTo(State o) {
		return this.getStateName().compareTo(o.getStateName());
	}
	
	@Override
	public String toString() {
		return this.getStateName();
	}
}
