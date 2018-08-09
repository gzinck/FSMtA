package fsm;

import fsm.attribute.Controllability;
import support.transition.Transition;
import fsm.attribute.Deterministic;
import fsm.attribute.Observability;
import fsm.attribute.OpacityTest;
import support.Event;
import support.State;
import java.util.*;

/**
 * This abstract class models a Finite State Machine with some of the essential elements.
 * It must be extended to be used (eg. by NonDeterministic or Deterministic to
 * determine how transitions and initial states are handled).
 * 
 * It is part of the fsm package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public abstract class FSM<T extends Transition> extends TransitionSystem<T> implements Observability<T>,	
																					 Controllability<T>,
																					 OpacityTest {
	
//---  Constant Values   ----------------------------------------------------------------------
	
	/** String constant designating this object as a specific type of FSM for clarification purposes*/
	public static final String FSM_TYPE = "FSM";
	/** String constant designating the file extension to append to the file name when writing to the system*/
	public static final String FSM_EXTENSION = ".fsm";
	/** String value describing the prefix assigned to all States in an FSM to differentiate it from another FSM*/
	public static final String STATE_PREFIX_1 = "a";
	/** String value describing the prefix assigned to all States in an FSM to differentiate it from another FSM*/
	public static final String STATE_PREFIX_2 = "b";
	
//---  Single-FSM Operations   ----------------------------------------------------------------

	/**
	 * This method creates a modified FSM or Modal Specification derived from the calling object by removing Observable Events
	 * and enforcing a Determinized status.
	 * 
	 * Collapse Unobservable
	 *  - Map singular States to their Collectives
	 * Calculate Transitions from Collective Groups
	 * - Will produce new States, need to then process these as well	
	 * 
	 * @return - Returns a TransitionSystem object representing the Determinized Observer-View derivation of the calling FSM or Modal Specification object.
	 */
	
	public abstract FSM buildObserver();
	
//---  Multi-FSM Operations   -----------------------------------------------------------------

	/**
	 * Helper method that performs the brunt of the operations involved with a single Product operation
	 * between two FSM objects, leaving the specialized features in more advanced FSM types to their
	 * own interpretations after this function has occurred.
	 * 
	 * Performs a product operation on the calling FSM with the first parameter FSM, and builds the
	 * resulting FSM in the second FSM. Has no return, does its action by side-effect.
	 * 
	 * @param other - FSM object representing the FSM object performing the Product operation with the calling FSM object.
	 * @param newFSM - FSM object representing the FSM holding the contents of the product of the Product operation.
	 */
	
	protected <T1 extends Transition> void productHelper(FSM<T1> other, FSM<T> newFSM) {
		// Get all the events the two have in common
		for(Event thisEvent : this.events.getEvents()) {
			for(Event otherEvent : other.events.getEvents()) {
				// All common events are added
				if(thisEvent.getEventName().equals(otherEvent.getEventName())) {
					newFSM.events.addEvent(thisEvent, otherEvent);
				} // if the event is identical
			} // for otherEvent
		} // for thisEvent
		
		// Go through all the initial states and add everything they connect to with shared events.
		for(State thisInitial : this.getInitialStates()) {
			for(State otherInitial : other.getInitialStates()) {
				// Now, start going through the paths leading out from this new initial state.
				LinkedList<State> thisNextState = new LinkedList<State>();
				thisNextState.add(thisInitial);
				LinkedList<State> otherNextState = new LinkedList<State>();
				otherNextState.add(otherInitial);
				
				while(!thisNextState.isEmpty() && !otherNextState.isEmpty()) { // Go through all the states connected
					State thisState = thisNextState.poll();
					State otherState = otherNextState.poll();
					State newState = newFSM.states.addState(thisState, otherState); // Add the new state
					if(thisState.getStateInitial() && otherState.getStateInitial())
						newFSM.addInitialState(newState);
					
					newFSM.setStateComposition(newState, thisState, (State)otherState);
					
					// Go through all the transitions in each, see what they have in common
					ArrayList<T> thisTransitions = this.transitions.getTransitions(thisState);
					ArrayList<T1> otherTransitions = other.transitions.getTransitions(otherState);
					if(thisTransitions != null && otherTransitions != null) {
						for(T thisTrans : thisTransitions) {
							for(T1 otherTrans : otherTransitions) {
								
								// If they share the same event
								Event thisEvent = thisTrans.getTransitionEvent();
								if(thisEvent.getEventName().equals(otherTrans.getTransitionEvent().getEventName())) {
									
									// Then create transitions to all the combined neighbours
									for(State thisToState : thisTrans.getTransitionStates()) {
										for(State otherToState : otherTrans.getTransitionStates()) {
											
											// If the state doesn't exist, add to queue
											if(!newFSM.stateExists("(" + thisToState.getStateName() + "," + otherToState.getStateName() + ")")) {
												thisNextState.add(thisToState);
												otherNextState.add(otherToState);
											} // if state doesn't exist
											
											// Add the state, then add the transition
											State newToState = newFSM.states.addState(thisToState, otherToState);
											newFSM.addTransition(newState.getStateName(), thisEvent.getEventName(), newToState.getStateName());
										} // for every state in other transition
									} // for every state in this transition
								} // if they share the event
							} // for other transitions
						} // for this transitions
					} // if transitions not null
				} // while there are more states connected to the 2-tuple of initial states
			} // for otherInitial
		} // for thisInitial
		newFSM.addStateComposition(this.getComposedStates());
		newFSM.addStateComposition(other.getComposedStates());
	} // productHelper(FSM)

	/**
	 * Helper method that performs the brunt of the operations involved with a single Parallel Composition
	 * operation between two FSM objects, leaving the specialized features in more advanced FSM types to
	 * their own interpretations after this function has occurred.
	 * 
	 * Performs a Parallel Composition operation on the FSM object calling this method with the FSM object provided as
	 * an argument (other), and places the results of this operation into the provided FSM object (newFSM).
	 * 
	 * @param other - FSM extending object that performs the Parallel Composition operation with FSM object calling this method.
	 * @param newFSM - FSM extending object that is provided to contain the results of this Parallel Composition operation.
	 */
	
	protected <T1 extends Transition> void parallelCompositionHelper(FSM<T1> other, FSM<T> newFSM) {
		// Get all the events the two have in common
		HashSet<String> commonEvents = new HashSet<String>();
		for(Event thisEvent : this.events.getEvents()) {
			for(Event otherEvent : other.events.getEvents()) {
				// If it is a common event
				if(thisEvent.getEventName().equals(otherEvent.getEventName())) {
					Event newEvent = newFSM.events.addEvent(thisEvent, otherEvent);
					commonEvents.add(newEvent.getEventName());
				} // if the event is identical
			} // for otherEvent
		} // for thisEvent
		
		// Add all the events unique to each FSM
		for(Event thisEvent : this.events.getEvents())
			if(!commonEvents.contains(thisEvent.getEventName()))
				newFSM.events.addEvent(thisEvent);
		for(Event otherEvent : other.events.getEvents())
			if(!commonEvents.contains(otherEvent.getEventName()))
				newFSM.events.addEvent(otherEvent);
		
		// Go through all the initial states and add everything they connect to.
		for(State thisInitial : this.getInitialStates()) {
			for(State otherInitial : other.getInitialStates()) {
				
				// Now, start going through the paths leading out from this new initial state.
				LinkedList<State> thisNextState = new LinkedList<State>();
				thisNextState.add(thisInitial);
				LinkedList<State> otherNextState = new LinkedList<State>();
				otherNextState.add(otherInitial);
				
				while(!thisNextState.isEmpty() && !otherNextState.isEmpty()) { // Go through all the states connected
					State thisState = thisNextState.poll();
					State otherState = otherNextState.poll();
					State newState = newFSM.states.addState(thisState, otherState); // Add the new state
					if(newState.getStateInitial())
						newFSM.addInitialState(newState);
					
					newFSM.setStateComposition(newState, thisState, (State)otherState);
					
					// Go through all the transitions in each, see what they have in common
					ArrayList<T> thisTransitions = this.transitions.getTransitions(thisState);
					ArrayList<T1> otherTransitions = other.transitions.getTransitions(otherState);
					if(thisTransitions != null && otherTransitions != null) {
						for(T thisTrans : thisTransitions) {
							for(T1 otherTrans : otherTransitions) {
								
								// If they share the same event
								Event thisEvent = thisTrans.getTransitionEvent();
								if(thisEvent.getEventName().equals(otherTrans.getTransitionEvent().getEventName())) {
									
									// Then create transitions to all the combined neighbours
									for(State thisToState : thisTrans.getTransitionStates()) {
										for(State otherToState : otherTrans.getTransitionStates()) {
											
											// If the state doesn't exist, add to queue
											if(!newFSM.stateExists("(" + thisToState.getStateName() + "," + otherToState.getStateName() + ")")) {
												thisNextState.add(thisToState);
												otherNextState.add(otherToState);
											} // if state doesn't exist
											
											// Add the state, then add the transition
											State newToState = newFSM.states.addState(thisToState, otherToState);
											newFSM.addTransition(newState.getStateName(), thisEvent.getEventName(), newToState.getStateName());
										} // for every state in other transition
									} // for every state in this transition
								} // if they share the event
							} // for other transitions
						} // for this transitions
					} // if transitions are not null
					// Go through all the transitions and see what is unique
					if(thisTransitions != null) {
						for(T thisTrans : thisTransitions) {
							// If it's NOT a common event
							Event thisEvent = thisTrans.getTransitionEvent();
							if(!commonEvents.contains(thisTrans.getTransitionEvent().getEventName())) {
								// Then, add all the transitions
								for(State thisToState : thisTrans.getTransitionStates()) {
									
									// If it doesn't exist, add it to the queue
									if(!newFSM.stateExists("(" + thisToState.getStateName() + "," + otherState.getStateName() + ")")) {
										thisNextState.add(thisToState);
										otherNextState.add(otherState);
									} // if state doesn't exist
									
									// Add the state, then add the transition
									State newToState = newFSM.states.addState(thisToState, otherState);
									newFSM.addTransition(newState.getStateName(), thisEvent.getEventName(), newToState.getStateName());
								} // for the toStates
							} // if not a common event
						} // for this transitions
					} // if transitions are not null
					if(otherTransitions != null) {
						for(T1 otherTrans : other.transitions.getTransitions(otherState)) {
							// If it's NOT a common event
							Event thisEvent = otherTrans.getTransitionEvent();
							if(!commonEvents.contains(otherTrans.getTransitionEvent().getEventName())) {
								// Then, add all the transitions
								for(State otherToState : otherTrans.getTransitionStates()) {
									
									// If it doesn't exist, add it to the queue
									if(!newFSM.stateExists("(" + thisState.getStateName() + "," + otherToState.getStateName() + ")")) {
										thisNextState.add(thisState);
										otherNextState.add(otherToState);
									} // if state doesn't exist
									
									// Add the state, then add the transition
									State newToState = newFSM.states.addState(thisState, otherToState);
									newFSM.addTransition(newState.getStateName(), thisEvent.getEventName(), newToState.getStateName());
								} // for the toStates
							} // if not a common event
						} // for other transitions
					} // if transitions are not null
				} // while there are more states connected to the 2-tuple of initial states
			} // for otherInitial
		} // for thisInitial
	} // parallelCompositionHelper(FSM)

	/**
	 * This method performs a Product(or Intersection) operation between multiple FSM objects, one provided as an
	 * argument and the other being the FSM object calling this method, and returns the resulting FSM object.
	 * 
	 * @param other - Array of FSM extending objects that performs the product operation on with the current FSM.
	 * @return - Returns a FSM extending object representing the FSM object resulting from all Product operations.
	 */
	
	public abstract FSM<? extends Transition> product(FSM<?> ... other);
	
	/**
	 * This method performs the Parallel Composition of multiple FSMs: the FSM calling this method and the FSMs
	 * provided as arguments. The resulting, returned, FSM will be the same type as the calling FSM.
	 * 
	 * @param other - Array of FSM extending objects provided to perform Parallel Composition with the calling FSM object.
	 * @return - Returns a FSM extending object representing the result of all Parallel Composition operations.
	 */
	
	public abstract FSM<? extends Transition> parallelComposition(FSM<?> ... other);
	
} // class FSM
