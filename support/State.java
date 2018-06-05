package support;

import java.util.*;

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
	/** ArrayList<Boolean> instance variable holding the values that correspond to different features of the State*/
	private ArrayList<Boolean> attributes;

//--- Constructors   --------------------------------------------------------------------------
	
	/**
	 * Constructor for a State object that assigns a name and parent-FSM to the object.
	 * 
	 * @param name - String object representing the State object's name
	 * @param parent - String object representing the State object's parent's name
	 */
	
	public State(String name, String parent) {
		id = name;
		fsm = parent;
		attributes = new ArrayList<Boolean>();
	}
	
	public State(String name) {
		id = name;
		fsm = "";
		attributes = new ArrayList<Boolean>();
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
	 * Getter method to access the State object's attributes at a specified index, upgrading
	 * the ArrayList<Boolean> to be large enough for this action if necessary.
	 * 
	 * @param index - int value representing the position to access at in the Attributes ArrayList<Boolean>
	 * @return - Returns a boolean value representing the value stored at the specified index in the Attributes ArrayList<Boolean>
	 */
	
	public boolean getStateAttribute(int index) {
		while(attributes.size() <= index) {
			attributes.add(false);
		}
		return attributes.get(index);
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
	
	public void setStateParent(String in) {
		fsm = in;
	}
	
	/**
	 * Setter method to replace the currently stored object at the specified index in the State object's Attribute ArrayList<Boolean>,
	 * upgrading the ArrayList<Boolean> to be large enough for this transaction if necessary
	 * 
	 * @param index - int value specifying the location in the Attributes ArrayList<Boolean> to replace the value of
	 * @param posit - boolean value specifying the new value to place at the specified index in the Attributes ArrayList<Boolean>
	 */
	
	public void setStateAttribute(int index, boolean posit) {
		while(attributes.size() <= index) {
			attributes.add(false);
		}
		attributes.set(index, posit);
	}
	
}
