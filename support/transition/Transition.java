package support.transition;

import java.util.Collection;
import java.util.ArrayList;
import support.State;
import support.Event;

/**
 * This interface provides the framework for the structure of Transition objects, leaving
 * the implementation to the classes that use this interface. 
 * 
 * This interface is part of the support.transition package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public interface Transition extends Comparable<Transition> {
	
//---  Operations   ---------------------------------------------------------------------------
	
	/**
	 * Generates a String object which has the dot representation of this Transition, which
	 * can be used for converting an FSM to a .jpg image via GraphViz.
	 * 
	 * @param firstState - State object associated to this Transition.
	 * @return - Returns a String object containing the dot representation of this Transition.
	 */
	
	public String makeDotString(State firstState);

//--- Setter Methods   ------------------------------------------------------------------------
	
	/**
	 * Setter method to add a new State to the Transition. If the transition is non-deterministic,
	 * then setting the state will not overwrite any other states (it will just add). If it is
	 * deterministic, the state will overwrite the previous state.
	 * If the State was already a transition state, it is not duplicated.
	 * 
	 * @param in - State object representing the Transition object's Event's new target State
	 */
	
	public void setTransitionState(State in);
	
	/**
	 * Setter method to assign an Event object as the Event associated to this Transition object
	 * 
	 * @param in - Event object provided to replace the value stored previously in this Transition object
	 */
	
	public void setTransitionEvent(Event in);

//--- Getter Methods   ------------------------------------------------------------------------
	
	/**
	 * Getter method to query whether or not a State exists in the Transition object by a provided String object.
	 * 
	 * @param stateName - String object representing the name of the State to search for.
	 * @return - Returns a boolean value describing the result of the query; if true, the State is present, false otherwise.
	 */
	
	public boolean stateExists(String stateName);
	
	/**
	 * Getter method to query whether or not a State exists in the Transition object by a provided State object.
	 * 
	 * @param inState - State object to search for in this Transition object's stored State(s).
	 * @return - Returns a boolean value describing the result of the query; if true, the State is present, false otherwise.
	 */
	
	public boolean stateExists(State inState);
	
	/**
	 * Getter method to access the Event associated to this Transition object
	 * 
	 * @return - Returns an Event representing the Event associated to this Transition object
	 */
	
	public Event getTransitionEvent();
	
	/**
	 * Getter method to access the ArrayList of State names led to by the Event associated to this Transition object
	 * 
	 * @return - Returns an ArrayList object containing the States led to by the Event associated to this Transition object
	 */
	
	public ArrayList<State> getTransitionStates();
	
	/**
	 * Getter method that requests a blank-slate Transition object of a type that matches that of the object
	 * to whom this method belongs with no correspondence to the object calling this method.
	 * 
	 * @return - Returns a Transition object, or a descendant of the class, that is empty.
	 */
	
	public <T extends Transition> T generateTransition();
	
//---  Manipulations   ------------------------------------------------------------------------	

	/**
	 * This method permits the inclusion of a new State into the Transition object's storage
	 * of destination State objects which are led to by an assigned Event, varying in implementation
	 * depending on the permissiveness of having multiple target States with the same Event.
	 * 
	 * @param state - State object representing the new State to be led to by the Transition object's Event.
	 * @return - Returns a boolean value; true if successful inclusion, false if the State were already present.
	 */
	
	public boolean addTransitionState(State state);
	
	/**
	 * This method removes a State from the Transition object as described by a provided String; or, if the
	 * State is the only item in the Transition object (as it is for the base Transition object), it returns
	 * true to indicate that the Transition object should be deleted entirely.
	 * 
	 * @param state - String object representing the State to delete from the transition object.
	 * @return - Returns a boolean value; true if the Transition object is empty after this operation, otherwise false.
	 */
	
	public boolean removeTransitionState(String stateName);
	
	/**
	 * This method removes the provided State from the Transition object, and, if the State is the only item
	 * in the Transition object(as it is for the base Transition object), it returns true to indicate that the
	 * Transition object should be deleted entirely.
	 * 
	 * @param inState - State object to delete from the Transition object.
	 * @return - Returns a boolean value; true if the Transition object is empty after this operation, otherwise false.
	 */
	
	public boolean removeTransitionState(State inState);
	
	/**
	 * This method removes the provided set of States from the transition object, and if those States were
	 * the only items in the Transition object, it returns true to indicate that the Transition object should
	 * be deleted entirely.
	 * 
	 * @param inState - Collection of State objects to delete from the transition object.
	 * @return - Returns a boolean value; true if the Transition object has no States to which it points, otherwise false.
	 */
	
	public boolean removeTransitionStates(Collection<State> inStates);
}
