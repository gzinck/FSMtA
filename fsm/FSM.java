package fsm;

import java.util.*;
import support.*;
import support.transition.Transition;
import support.event.Event;

/**
 * This abstract class models a Finite State Machine with some of the essential elements.
 * It must be extended to be used (eg. by NonDeterministic or Deterministic to
 * determine how transitions and initial states are handled).
 * 
 * It is part of the fsm package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public abstract class FSM<S extends State, T extends Transition<S, E>, E extends Event>
		extends TransitionSystem<S, T, E> {
	
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
	 * This method converts an FSM object into a text file which can be read back in and used to recreate
	 * an FSM later, or used for analytical purposes. A helper class, ReadWrite, manages the brunt
	 * of this process, but for the various special features of FSM objects, each has to handle
	 * itself separately.
	 * 
	 * @param filePath - String object representing the path to the folder to place the text file.
	 * @param name - String object representing the name of the text file to create.
	 */
	
	public abstract void toTextFile(String filePath, String name);

//---  Multi-FSM Operations   -----------------------------------------------------------------
	
	/**
	 * This method performs a union operation between multiple FSM objects and returns the result.
	 * 
	 * @param other - Array of FSM<<r>S, T, E> extending objects that is added to the calling FSM object in order to create a unioned FSM<<r>S, T, E> extending object.
	 * @return - Returns a FSM<<r>S, T, E> extending object representing the result of all union operation.
	 */
	
	public abstract <S1 extends State, T1 extends Transition<S1, E1>, E1 extends Event> FSM union(FSM ... other);
	
	/**
	 * Helper method that performs the brunt of the operations involved with a single Union operation
	 * between two FSM objects, leaving the specialized features in more advanced FSM types to their
	 * own interpretations after this function has occurred.
	 * 
	 * Performs a Union operation on the two provided FSM objects by dynamically adding
	 * to the provided generic FSM, newFSM.
	 * 
	 * @param other - FSM<<r>S, T, E> extending object that is provided as one of two FSM object's being adjoined via Union
	 * @param newFSM - FSM<<r>S, T, E> extending object that is provided as the holding place for the product of the two FSM object's being adjoined via Union
	 */
	
	protected <S1 extends State, T1 extends Transition<S1, E1>, E1 extends Event, NewT extends Transition<S, E>> void unionHelper(FSM<S1, T1, E1> other, FSM<S, NewT, E> newFSM) {
		// Add all states
		for(S s : this.states.getStates())
			newFSM.states.addState(s, STATE_PREFIX_1);
		for(S1 s : other.states.getStates())
			newFSM.states.addState(s, STATE_PREFIX_2);
		
		// Add initial states
		for(State s : getInitialStates())  // Add the states from the this FSM
			newFSM.addInitialState(STATE_PREFIX_1 + s.getStateName());
		for(State s : other.getInitialStates())  // Add the states from the other FSM
			newFSM.addInitialState(STATE_PREFIX_2 + s.getStateName());
		
		// Add events
		for(E e : this.events.getEvents())
			newFSM.events.addEvent(e);
		for(E1 e : other.events.getEvents())
			newFSM.events.addEvent(e);
		
		// Add transitions
		for(Map.Entry<S, ArrayList<T>> entry : this.transitions.getAllTransitions()) {
			S currState = newFSM.states.getState(STATE_PREFIX_1 + entry .getKey().getStateName());
			for(T t : entry.getValue()) {
				E newEvent = newFSM.events.getEvent(t.getTransitionEvent());
				for(S toState : t.getTransitionStates()) {
					S newState = newFSM.states.getState(STATE_PREFIX_1 + toState.getStateName());
					newFSM.addTransition(currState.getStateName(), newEvent.getEventName(), newState.getStateName());
				} // for every toState in the transition
			} // for transition
		} // for entry
		
		for(Map.Entry<S1, ArrayList<T1>> entry : other.transitions.getAllTransitions()) {
			S currState = newFSM.states.getState(STATE_PREFIX_2 + entry.getKey().getStateName());
			for(T1 t : entry.getValue()) {
				E newEvent = newFSM.events.getEvent(t.getTransitionEvent());
				for(S1 toState : t.getTransitionStates()) {
					S newState = newFSM.states.getState(STATE_PREFIX_2 + toState.getStateName());
					newFSM.addTransition(currState.getStateName(), newEvent.getEventName(), newState.getStateName());
				} // for every toState in the transition
			} // for transition
		} // for entry
	} // unionHelper(FSM, FSM)
	
	/**
	 * This method performs a Product(or Intersection) operation between multiple FSM objects, one provided as an
	 * argument and the other being the FSM object calling this method, and returns the resulting FSM object.
	 * 
	 * @param other - Array of FSM<<r>S, T, E> extending objects that performs the product operation on with the current FSM.
	 * @return - Returns a FSM<<r>S, T, E> extending object representing the FSM object resulting from all Product operations.
	 */
	
	public abstract <S1 extends State, T1 extends Transition<S1, E1>, E1 extends Event> FSM product(FSM ... other);
	
	/**
	 * Helper method that performs the brunt of the operations involved with a single Product operation
	 * between two FSM objects, leaving the specialized features in more advanced FSM types to their
	 * own interpretations after this function has occurred.
	 * 
	 * Performs a product operation on the calling FSM with the first parameter FSM, and builds the
	 * resulting FSM in the second FSM. Has no return, does its action by side-effect.
	 * 
	 * @param other - FSM<<r>S, T, E> object representing the FSM object performing the Product operation with the calling FSM object.
	 * @param newFSM - FSM<<r>S, T, E> object representing the FSM holding the contents of the product of the Product operation.
	 */
	
	protected <S1 extends State, T1 extends Transition<S1, E1>, E1 extends Event> void productHelper(FSM<S1, T1, E1> other, FSM<S, T, E> newFSM) {
		// Get all the events the two have in common
		for(E thisEvent : this.events.getEvents()) {
			for(E1 otherEvent : other.events.getEvents()) {
				// All common events are added
				if(thisEvent.getEventName().equals(otherEvent.getEventName())) {
					newFSM.events.addEvent(thisEvent, otherEvent);
				} // if the event is identical
			} // for otherEvent
		} // for thisEvent
		
		// Go through all the initial states and add everything they connect to with shared events.
		for(S thisInitial : this.getInitialStates()) {
			for(S1 otherInitial : other.getInitialStates()) {
				// Now, start going through the paths leading out from this new initial state.
				LinkedList<S> thisNextState = new LinkedList<S>();
				thisNextState.add(thisInitial);
				LinkedList<S1> otherNextState = new LinkedList<S1>();
				otherNextState.add(otherInitial);
				
				while(!thisNextState.isEmpty() && !otherNextState.isEmpty()) { // Go through all the states connected
					S thisState = thisNextState.poll();
					S1 otherState = otherNextState.poll();
					S newState = newFSM.states.addState(thisState, otherState); // Add the new state
					if(thisState.getStateInitial() && otherState.getStateInitial())
						newFSM.addInitialState(newState);
					
					// Go through all the transitions in each, see what they have in common
					ArrayList<T> thisTransitions = this.transitions.getTransitions(thisState);
					ArrayList<T1> otherTransitions = other.transitions.getTransitions(otherState);
					if(thisTransitions != null && otherTransitions != null) {
						for(T thisTrans : thisTransitions) {
							for(T1 otherTrans : otherTransitions) {
								
								// If they share the same event
								E thisEvent = thisTrans.getTransitionEvent();
								if(thisEvent.getEventName().equals(otherTrans.getTransitionEvent().getEventName())) {
									
									// Then create transitions to all the combined neighbours
									for(S thisToState : thisTrans.getTransitionStates()) {
										for(S1 otherToState : otherTrans.getTransitionStates()) {
											
											// If the state doesn't exist, add to queue
											if(!newFSM.stateExists("(" + thisToState.getStateName() + "," + otherToState.getStateName() + ")")) {
												thisNextState.add(thisToState);
												otherNextState.add(otherToState);
											} // if state doesn't exist
											
											// Add the state, then add the transition
											S newToState = newFSM.states.addState(thisToState, otherToState);
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
	} // productHelper(FSM)
	
	/**
	 * This method performs the Parallel Composition of multiple FSMs: the FSM calling this method and the FSMs
	 * provided as arguments. The resulting, returned, FSM will be the same type as the calling FSM.
	 * 
	 * @param other - Array of FSM<<r>S, T, E> extending objects provided to perform Parallel Composition with the calling FSM object.
	 * @return - Returns a FSM<<r>S, T, E> extending object representing the result of all Parallel Composition operations.
	 */
	
	public abstract <S1 extends State, T1 extends Transition<S1, E1>, E1 extends Event> FSM<S, T, E> parallelComposition(FSM ... other);
	
	/**
	 * Helper method that performs the brunt of the operations involved with a single Parallel Composition
	 * operation between two FSM objects, leaving the specialized features in more advanced FSM types to
	 * their own interpretations after this function has occurred.
	 * 
	 * Performs a Parallel Composition operation on the FSM object calling this method with the FSM object provided as
	 * an argument (other), and places the results of this operation into the provided FSM object (newFSM).
	 * 
	 * @param other - FSM<<r>S, T, E> extending object that performs the Parallel Composition operation with FSM object calling this method.
	 * @param newFSM - FSM<<r>S, T, E> extending object that is provided to contain the results of this Parallel Composition operation.
	 */
	
	protected <S1 extends State, T1 extends Transition<S1, E1>, E1 extends Event> void parallelCompositionHelper(FSM<S1, T1, E1> other, FSM<S, T, E> newFSM) {
		// Get all the events the two have in common
		HashSet<String> commonEvents = new HashSet<String>();
		for(E thisEvent : this.events.getEvents()) {
			for(E1 otherEvent : other.events.getEvents()) {
				// If it is a common event
				if(thisEvent.getEventName().equals(otherEvent.getEventName())) {
					E newEvent = newFSM.events.addEvent(thisEvent, otherEvent);
					commonEvents.add(newEvent.getEventName());
				} // if the event is identical
			} // for otherEvent
		} // for thisEvent
		
		// Add all the events unique to each FSM
		for(E thisEvent : this.events.getEvents())
			if(!commonEvents.contains(thisEvent.getEventName()))
				newFSM.events.addEvent(thisEvent);
		for(E1 otherEvent : other.events.getEvents())
			if(!commonEvents.contains(otherEvent.getEventName()))
				newFSM.events.addEvent(otherEvent);
		
		// Go through all the initial states and add everything they connect to.
		for(S thisInitial : this.getInitialStates()) {
			for(S1 otherInitial : other.getInitialStates()) {
				
				// Now, start going through the paths leading out from this new initial state.
				LinkedList<S> thisNextState = new LinkedList<S>();
				thisNextState.add(thisInitial);
				LinkedList<S1> otherNextState = new LinkedList<S1>();
				otherNextState.add(otherInitial);
				
				while(!thisNextState.isEmpty() && !otherNextState.isEmpty()) { // Go through all the states connected
					S thisState = thisNextState.poll();
					S1 otherState = otherNextState.poll();
					S newState = newFSM.states.addState(thisState, otherState); // Add the new state
					if(newState.getStateInitial())
						newFSM.addInitialState(newState);
					
					// Go through all the transitions in each, see what they have in common
					ArrayList<T> thisTransitions = this.transitions.getTransitions(thisState);
					ArrayList<T1> otherTransitions = other.transitions.getTransitions(otherState);
					if(thisTransitions != null && otherTransitions != null) {
						for(T thisTrans : thisTransitions) {
							for(T1 otherTrans : otherTransitions) {
								
								// If they share the same event
								E thisEvent = thisTrans.getTransitionEvent();
								if(thisEvent.getEventName().equals(otherTrans.getTransitionEvent().getEventName())) {
									
									// Then create transitions to all the combined neighbours
									for(S thisToState : thisTrans.getTransitionStates()) {
										for(S1 otherToState : otherTrans.getTransitionStates()) {
											
											// If the state doesn't exist, add to queue
											if(!newFSM.stateExists("(" + thisToState.getStateName() + "," + otherToState.getStateName() + ")")) {
												thisNextState.add(thisToState);
												otherNextState.add(otherToState);
											} // if state doesn't exist
											
											// Add the state, then add the transition
											S newToState = newFSM.states.addState(thisToState, otherToState);
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
							E thisEvent = thisTrans.getTransitionEvent();
							if(!commonEvents.contains(thisTrans.getTransitionEvent().getEventName())) {
								// Then, add all the transitions
								for(S thisToState : thisTrans.getTransitionStates()) {
									
									// If it doesn't exist, add it to the queue
									if(!newFSM.stateExists("(" + thisToState.getStateName() + "," + otherState.getStateName() + ")")) {
										thisNextState.add(thisToState);
										otherNextState.add(otherState);
									} // if state doesn't exist
									
									// Add the state, then add the transition
									S newToState = newFSM.states.addState(thisToState, otherState);
									newFSM.addTransition(newState.getStateName(), thisEvent.getEventName(), newToState.getStateName());
								} // for the toStates
							} // if not a common event
						} // for this transitions
					} // if transitions are not null
					if(otherTransitions != null) {
						for(T1 otherTrans : other.transitions.getTransitions(otherState)) {
							// If it's NOT a common event
							E1 thisEvent = otherTrans.getTransitionEvent();
							if(!commonEvents.contains(otherTrans.getTransitionEvent().getEventName())) {
								// Then, add all the transitions
								for(S1 otherToState : otherTrans.getTransitionStates()) {
									
									// If it doesn't exist, add it to the queue
									if(!newFSM.stateExists("(" + thisState.getStateName() + "," + otherToState.getStateName() + ")")) {
										thisNextState.add(thisState);
										otherNextState.add(otherToState);
									} // if state doesn't exist
									
									// Add the state, then add the transition
									S newToState = newFSM.states.addState(thisState, otherToState);
									newFSM.addTransition(newState.getStateName(), thisEvent.getEventName(), newToState.getStateName());
								} // for the toStates
							} // if not a common event
						} // for other transitions
					} // if transitions are not null
				} // while there are more states connected to the 2-tuple of initial states
			} // for otherInitial
		} // for thisInitial
	} // parallelCompositionHelper(FSM)
} // class FSM
