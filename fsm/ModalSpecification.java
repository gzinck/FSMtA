package fsm;

import support.*;
import support.event.Event;
import support.transition.DetTransition;
import support.transition.NonDetTransition;
import support.transition.Transition;

import java.util.ArrayList;
import java.util.HashSet;

import fsm.attribute.*;

public class ModalSpecification
		extends TransitionSystem<State, NonDetTransition<State, Event>, Event> {
	
//--- Instance Variables  ----------------------------------------------------------------------

	/** ArrayList<<j>State> object that holds a list of Initial States for this Modal Specification object. */
	protected ArrayList<State> initialStates;
	
	/** TransitionFunction object mapping states to sets of "must" transitions for the Modal Specification.
	 * These are transitions which must be present in the controlled FSM in order to satisfy the spec. */
	protected TransitionFunction<State, NonDetTransition<State, Event>, Event> mustTransitions;
	
//---  Constructors  --------------------------------------------------------------------------
	
	/**
	 * Constructor for a ModalSpecification that takes any TransitionSystem as a parameter and
	 * creates a new ModalSpecification using that as the basis. Any information which is not
	 * permissible in a ModalSpecification is thrown away, because it does not have any means to handle it.
	 * 
	 * @param other TransitionSystem to copy as a ModalSpecification.
	 * @param inId Id for the new ModalSpecification to carry.
	 */
	
	public <S1 extends State, E1 extends Event, T1 extends Transition<S1, E1>> ModalSpecification(TransitionSystem<S1, T1, E1> other, String inId) {
		id = inId;
		states = new StateMap<State>(State.class);
		events = new EventMap<Event>(Event.class);
		transitions = new TransitionFunction<State, NonDetTransition<State, Event>, Event>(new NonDetTransition<State, Event>());
		
		copyStates(other); // Add in all the states
		copyEvents(other); // Add in all the events
		copyTransitions(other); // Add in all the transitions
		
		if(other instanceof ModalSpecification) {
			
		} // if it's a ModalSpecification
	} // ModalSpecification(TransitionSystem, String)
	
	/**
	 * Constructor for an ModalSpecification object that contains no transitions or states, allowing the
	 * user to add those elements him/herself.
	 */
	
	public ModalSpecification(String inId) {
		id = inId;
		events = new EventMap<Event>(Event.class);
		states = new StateMap<State>(State.class);
		transitions = new TransitionFunction<State, NonDetTransition<State, Event>, Event>(new NonDetTransition<State, Event>());
		mustTransitions = new TransitionFunction<State, NonDetTransition<State, Event>, Event>(new NonDetTransition<State, Event>());
		initialStates = new ArrayList<State>();
	} // ModalSpecification()
	
	/**
	 * Constructor for an ModalSpecification object that contains no transitions or states, allowing the
	 * user to add those elements him/herself. It has no id, either.
	 */
	
	public ModalSpecification() {
		id = "";
		events = new EventMap<Event>(Event.class);
		states = new StateMap<State>(State.class);
		transitions = new TransitionFunction<State, NonDetTransition<State, Event>, Event>(new NonDetTransition<State, Event>());
		mustTransitions = new TransitionFunction<State, NonDetTransition<State, Event>, Event>(new NonDetTransition<State, Event>());
		initialStates = new ArrayList<State>();
	} // ModalSpecification()
	
//---  Operations   -----------------------------------------------------------------------
	
	public <S extends State, T extends Transition<S, E>, E extends Event> FSM<S, T, E> makeOptimalSupervisor(FSM<S, T, E> fsm) {
		//--------------------------------------------
		// Step 1: Create the reachable part of the combo
		FSM<S, DetTransition<S, E>, E> product = getDeterminizedProductWithFSM(fsm);
		// Now mark the bad states
		boolean keepGoing = true;
		while(keepGoing) {
			keepGoing = markBadStates(fsm, product, new HashSet<String>());
			// TODO: remove these bad states
			markDeadEnds(fsm, product, new HashSet<String>());
		} // while
		return null;
	}
	
	private <S extends State, T extends Transition<S, E>, E extends Event> FSM<S, DetTransition<S, E>, E> getDeterminizedProductWithFSM(FSM<S, T, E> fsm) {
		FSM<S, DetTransition<S, E>, E> newFSM = ((Observability)fsm).createObserverView().determinize();
		NonDetObsContFSM tempFSM = new NonDetObsContFSM();
		// We need to take all the events from the parameter fsm and copy them over to the tempFSM,
		// but copy the states and transitions direct from the current FSM.
		tempFSM.copyEvents(fsm);
		tempFSM.copyStates(this);
		tempFSM.copyTransitions(this);
		// Determinize the underlying transition system of this FSM.
		FSM tempFSM2 = tempFSM.createObserverView().determinize(); // TODO: fix the types here...
		return newFSM.product(tempFSM2);
	}
	
	/**
	 * This marks states that are bad states in a supervisor for an FSM that is trying to satisfy a
	 * modal specification. There are two types of bad states:
	 * 1) When there is some uncontrollable and observable event where there is a possible state in
	 * the determinized collection of states where the event is possible, but there is no such possible
	 * transition in the specification; and
	 * 2) When there is some observable event where the event must be possible according to the specification,
	 * but there is no such transition defined for the determinized collection of states.
	 * 
	 * @param fsm Original FSM which needs to be controlled.
	 * @param product FSM representing the product of the determinized first FSM with the specification.
	 * @param badStates HashSet of all the names of States which are bad.
	 * @return True if there was at least one state which was marked as bad.
	 */
	private <S extends State, T extends Transition<S, E>, E extends Event>
			boolean markBadStates(FSM<S, T, E> fsm, FSM<S, DetTransition<S, E>, E> product, HashSet<String> badStates) {
		// We need to parse every state in the product, check every component state if there is some uncontrollable observable
		// event that isn't defined in the product.
		// Also look if must transitions exist...
		// TODO: Make it so that a determinize function returns a set of states which can easily be cycled through...
		return false;
	}
	
	private <S extends State, T extends Transition<S, E>, E extends Event>
			boolean markDeadEnds(FSM<S, T, E> fsm, FSM<S, DetTransition<S, E>, E> product, HashSet<String> badStates) {
		// TODO: essentially, perform the coaccessible operation every time, but must get to an end
		// state in both FSMs (maybe in different ways).
		return false;
	}
	
//---  Copy methods that steal from other systems   -----------------------------------------------------------------------
	
	/**
	 * Copies the must transitions of another ModalSpecification into the current ModalSpecification.
	 * @param other ModalSpecification whose transitions are to be copied.
	 */
	
	public void copyMustTransitions(ModalSpecification other) {
		for(State s : other.states.getStates()) {
			ArrayList<NonDetTransition<State, Event>> thisTransitions = other.mustTransitions.getTransitions(s);
			if(thisTransitions != null)
				for(NonDetTransition<State, Event> t : thisTransitions) {
					// Add every state the transition leads to
					for(State toState : t.getTransitionStates())
						this.addMustTransition(s.getStateName(), t.getTransitionEvent().getEventName(), toState.getStateName());
				} // for every transition
		} // for every state
	} // copyMustTransitions(ModalSpecification)
//---  Getter Methods   -----------------------------------------------------------------------
	
	@Override
	public ArrayList<State> getInitialStates() {
		return initialStates;
	}

	@Override
	public boolean hasInitialState(String stateName) {
		State s = getState(stateName);
		return initialStates.contains(s);
	}

//---  Manipulations   -----------------------------------------------------------------------
	
	/**
	 * This method handles the adding of a new Must Transition to the calling FSM object via a format
	 * of 3 String objects representing a State, via an Event, leading to another State, creating
	 * the objects in the calling FSM object's State and EventMaps where necessary.
	 * 
	 * @param state1 - String object corresponding to the origin State for this Must Transition.
	 * @param eventName - String object corresponding to the Event of this Must Transition.
	 * @param state2 - String object corresponding to the destination State for the Must Transition.
	 */
	
	public void addMustTransition(String state1, String eventName, String state2) {
		// First, add the "may" transition
		addTransition(state1, eventName, state2);
		
		// If they do not exist yet, add the states.
		State s1 = states.addState(state1);
		State s2 = states.addState(state2);
		
		// Get the event or make it
		Event e = events.addEvent(eventName);
		
		// See if there is already a transition with the event...
		ArrayList<NonDetTransition<State, Event>> thisTransitions = mustTransitions.getTransitions(s1);
		if(thisTransitions != null) {
			for(NonDetTransition<State, Event> t : thisTransitions) {
				if(t.getTransitionEvent().equals(e)) {
					t.setTransitionState(s2);
					return;
				} // if equal
			} // for every transition
		} // if not null
		
		// Otherwise, make a new Transition object
		NonDetTransition<State, Event> outbound = transitions.getEmptyTransition();
		outbound.setTransitionEvent(e);
		outbound.setTransitionState(s2);
		mustTransitions.addTransition(s1, outbound);
	}
	
	@Override
	public void addInitialState(String newInitial) {
		State theState = states.addState(newInitial);
		theState.setStateInitial(true);
		initialStates.add(theState);
	}

	@Override
	public void addInitialState(State newState) {
		State theState = states.addState(newState);
		theState.setStateInitial(true);
		initialStates.add(theState);
	}

	@Override
	public boolean removeInitialState(String stateName) {
		State theState = states.getState(stateName);
		if(theState != null) {
			theState.setStateInitial(false);
			if(initialStates.remove(theState)) return true;
		}
		return false;
	}
}
