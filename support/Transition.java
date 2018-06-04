package support;

import java.util.*;

/**
 * This class models a path that connects a State to another State in an FSM, storing an
 * Event and a list of States it may lead to. It is the NonDeterministicFSM variant of
 * the base Transition class.
 * 
 * This class is a part of the support package
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public class NonDetTransition extends Transition{

//--- Instance Variables   --------------------------------------------------------------------
	
	/** ArrayList<String> object holding all States associated to the Event associated to this NonDetTransition object*/
	private ArrayList<String> state;
	
//--- Constructors   --------------------------------------------------------------------------
	
	/**
	 * Constructor for a NonDetTransition object, assigning a single String value as the Event name associated
	 * to this object and a list of Strings as the State names led to by that Event.
	 * 
	 * @param eventNom - String object representing the name of the Event associated to this NonDetTransition object
	 * @param states - List of String objects representing the States led to by the Event associated to this NonDetTransition object
	 */
	
	public NonDetTransition(String eventNom, String ... states) {
		super(eventNom, "");
		state = new ArrayList<String>();
		for(int i = 0; i < states.length; i++)
		  state.add(states[i]);
	}
	
//--- Setter Methods   ------------------------------------------------------------------------
	
	/**
	 * Setter method to replace the current ArrayList<String> of State names with
	 * the provided one.
	 * 
	 * @param in - ArrayList<String> object representing the list of States led to by the Event associated to this NonDetTransition object
	 */
	
	public void setTransitionState(ArrayList<String> in) {
		state = in;
	}
	
//--- Getter Methods   ------------------------------------------------------------------------
	
	/**
	 * Getter method to access the ArrayList<String> of State names led to by the Event
	 * associated to this NonDetTransition object
	 * 
	 * @return - Returns an ArrayList<String> containing the State names led to by the Event associated to this NonDetTransition object
	 */
	
	public ArrayList<String> getTransitionStates(){
		return state;
	}
	
	
	/**
	 * Getter method to query whether or not a State exists in the ArrayList<String> containing the
	 * State names led to by the Event associated to this NonDetTransition object
	 * 
	 * @param stateName - String object representing the name of the State to search for in ArrayList<String> held by this object
	 * @return - Returns a boolean value describing the result of the query; if true, the State is present, false otherwise.
	 */
	
	public boolean stateExists(String stateName) {
		return state.contains(stateName);
	}
	
//--- Manipulations   -------------------------------------------------------------------------
	
	/**
	 * This method appends the provided String, representing the name of a State, to the
	 * ArrayList<String> holding all State names led to by the Event associated to this
	 * NonDetTransition object
	 * 
	 * @param stateName - String object representing the State to append to the end of the list of States associated to this object
	 */
	
	public void addTransitionState(String stateName) {
		state.add(stateName);
	}
	
	/**
	 * This method removes a State from the ArrayList<String> holding all States as
	 * defined by the provided object stateName
	 * 
	 * @param stateName - String object representing a State to remove from the ArrayList<String> holding all States associated to this object
	 */
	
	public void removeTransitionState(String stateName) {
		state.remove(stateName);
	}

	
}
