package support.transition;

import java.util.ArrayList;

import support.State;
import support.event.Event;

public interface Transition<S extends State, E extends Event> {
	/**
	 * Setter method to assign a new String as the State name associated to the Event associated to this Transition object.
	 * If the State was already a transition state, it is not duplicated.
	 * 
	 * @param in State object representing the Transition object's Event's new target State
	 */
	
	public void setTransitionState(S in);
	
	/**
	 * Setter method to assign an Event object as the Event associated to this Transition object
	 * 
	 * @param in Event object provided to replace the value stored previously in this Transition object
	 */
	public void setTransitionEvent(E in);
	
	/**
	 * Getter method to access the Event associated to this Transition object
	 * 
	 * @return Returns a Event object representing the Event associated to this Transition object
	 */
	public E getTransitionEvent();
	
	/**
	 * Getter method to access the ArrayList of State names led to by the Event associated to this NonDetTransition object
	 * 
	 * @return Returns an ArrayList containing the States led to by the Event associated to this NonDetTransition object
	 */
	public ArrayList<S> getTransitionStates();
	
	/**
	 * Getter method to query whether or not a State exists in the Transition object.
	 * 
	 * @param stateName String object representing the name of the State to search for.
	 * @return Returns a boolean value describing the result of the query; if true, the State is present, false otherwise.
	 */
	public boolean stateExists(String stateName);
	
	/**
	 * Getter method to query whether or not a State exists in the Transition object.
	 * 
	 * @param inState State object to search for.
	 * @return Returns a boolean value describing the result of the query; if true, the State is present, false otherwise.
	 */
	public boolean stateExists(State inState);
	
	/**
	 * Removes the state from the transition object; or, if the state
	 * is the only item in the transition object (as it is for the base
	 * Transition object), it returns true to indicate that the transition
	 * object should be deleted entirely.
	 * 
	 * @param state String representing the state to delete from the transition object.
	 * @return True if the transition object has no states to which it points,
	 * else false.
	 */
	public boolean removeTransitionState(String stateName);
	
	/**
	 * Removes the state from the transition object; or, if the state
	 * is the only item in the transition object (as it is for the base
	 * Transition object), it returns true to indicate that the transition
	 * object should be deleted entirely.
	 * 
	 * @param inState State object to delete from the transition object.
	 * @return True if the transition object has no states to which it points,
	 * else false.
	 */
	public boolean removeTransitionState(State inState);
	
	/**
	 * Makes a String object which has the dot representation of the Transition, which
	 * can be used for sending an FSM to GraphViz.
	 * 
	 * @param firstState The State which leads to the transition. This is used to
	 * determine the exact text for the dot representation.
	 * @return String containing the dot representation of the Transition.
	 */
	public String makeDotString(State firstState);
}
