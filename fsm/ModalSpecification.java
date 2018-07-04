package fsm;

import support.*;
import support.attribute.EventControllability;
import support.attribute.EventObservability;
import support.event.Event;
import support.transition.DetTransition;
import support.transition.Transition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import fsm.attribute.*;

/**
 * This class models an FSM-adjacent construction called a ModalSpecification which
 * Graeme should write a brief description of for me. TODO:
 * 
 * This class is a part of the fsm package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public class ModalSpecification
		extends TransitionSystem<State, DetTransition<State, Event>, Event> {
	
//--- Instance Variables  ----------------------------------------------------------------------

	/** ArrayList<<j>State> object that holds a list of Initial States for this Modal Specification object. */
	protected ArrayList<State> initialStates;
	/** TransitionFunction object mapping states to sets of "must" transitions for the Modal Specification.
	 * These are transitions which must be present in the controlled FSM in order to satisfy the spec. */
	protected TransitionFunction<State, DetTransition<State, Event>, Event> mustTransitions;
	
//---  Constructors  --------------------------------------------------------------------------
	
	/**
	 * Constructor for a ModalSpecification that takes any TransitionSystem as a parameter and
	 * creates a new ModalSpecification using that as the basis. Any information which is not
	 * permissible in a ModalSpecification is thrown away, because it does not have any means to handle it.
	 * 
	 * @param other - TransitionSystem object to copy as a ModalSpecification.
	 * @param inId - String object representing Id for the new ModalSpecification to carry.
	 */
	
	public <S1 extends State, E1 extends Event, T1 extends Transition<S1, E1>> ModalSpecification(TransitionSystem<S1, T1, E1> other, String inId) {
		id = inId;
		states = new StateMap<State>(State.class);
		events = new EventMap<Event>(Event.class);
		transitions = new TransitionFunction<State, DetTransition<State, Event>, Event>(new DetTransition<State, Event>());
		
		copyStates(other); // Add in all the states
		copyEvents(other); // Add in all the events
		copyTransitions(other); // Add in all the transitions
		
		if(other instanceof ModalSpecification) {
			
		} // if it's a ModalSpecification
	} // ModalSpecification(TransitionSystem, String)
	
	/**
	 * Constructor for an ModalSpecification object that contains no transitions or states, allowing the
	 * user to add those elements themselves.
	 * 
	 * @param inId - String object representing the id representing this ModalSpecification object.
	 */
	
	public ModalSpecification(String inId) {
		id = inId;
		events = new EventMap<Event>(Event.class);
		states = new StateMap<State>(State.class);
		transitions = new TransitionFunction<State, DetTransition<State, Event>, Event>(new DetTransition<State, Event>());
		mustTransitions = new TransitionFunction<State, DetTransition<State, Event>, Event>(new DetTransition<State, Event>());
		initialStates = new ArrayList<State>();
	} // ModalSpecification()
	
	/**
	 * Constructor for an ModalSpecification object that contains no transitions or states, allowing the
	 * user to add those elements themselves. It has no id, either.
	 */
	
	public ModalSpecification() {
		id = "";
		events = new EventMap<Event>(Event.class);
		states = new StateMap<State>(State.class);
		transitions = new TransitionFunction<State, DetTransition<State, Event>, Event>(new DetTransition<State, Event>());
		mustTransitions = new TransitionFunction<State, DetTransition<State, Event>, Event>(new DetTransition<State, Event>());
		initialStates = new ArrayList<State>();
	} // ModalSpecification()
	
//---  Operations   -----------------------------------------------------------------------
	
	/**
	 * This method performs the operation to create the Optimal Supervisor of a supplied FSM.
	 * 
	 * TODO: Graeme, explain this.
	 * 
	 * @param fsm - FSM<<r>S, T, E> object 
	 * @return - Returns a 
	 */
	
	public <S extends State, T extends Transition<S, E>, E extends Event> FSM<S, T, E> makeOptimalSupervisor(FSM<S, T, E> fsm) {
		//--------------------------------------------
		// Step 1: Create the reachable part of the combo
		FSM<S, DetTransition<S, E>, E> newFSM;
		if(fsm instanceof Observability) {
			newFSM = ((Observability)fsm).createObserverView().determinize();
		} else if(fsm instanceof NonDeterministic) {
			newFSM = ((NonDeterministic)fsm).determinize();
		} else{
			newFSM = (FSM<S, DetTransition<S, E>, E>)fsm; // TODO: make sure this is right...
		} // if/else
		
		DetObsContFSM specFSM = new DetObsContFSM();
		// Make the underlying FSM of the specification
		// TODO: Throw an exception when the specification has some unobservable events
		specFSM.copyEvents(this);
		specFSM.copyStates(this);
		specFSM.copyTransitions(this);
		
		FSM<S, DetTransition<S, E>, E> product = newFSM.product(specFSM);
		
		
		// Now mark the bad states
		boolean keepGoing = true;
		while(keepGoing) {
			markBadStates(fsm, specFSM, product);
			markDeadEnds(specFSM, product);
			markDeadEnds(fsm, product);
		} // while
		return null;
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
	 * @param fsm - Original FSM which needs to be controlled.
	 * @param specFSM - FSM underlying the modal specification object.
	 * @param product - FSM representing the product of the determinized first FSM with the specification.
	 * @return - Returns a HashSet of all the names of States which are bad.
	 */
	
	private <S extends State, T extends Transition<S, E>, E extends Event>
			HashSet<String> markBadStates(FSM<S, T, E> fsm, DetObsContFSM specFSM, FSM<S, DetTransition<S, E>, E> product) {
		// We need to parse every state in the product, check every component state if there is some uncontrollable observable
		// event that isn't defined in the product.
		// Also look if must transitions exist...
		
		HashSet<String> badStates = new HashSet<String>();
		// Go through every state in the product
		for(S s : product.getStates()) {
			
			// If a must transition does not exist at the state, mark the state
			String specStateName = getSpecificationState(s.getStateName());
			ArrayList<DetTransition<State, Event>> specTransitions = this.mustTransitions.getTransitions(this.getState(specStateName));
			if(specTransitions != null) for(DetTransition<State, Event> t : specTransitions) {
				Event event = t.getTransitionEvent();
				ArrayList<S> toStates = product.transitions.getTransitionStates(s, product.events.getEvent(event));
				// Mark the state as bad if the must transition does not exist
				if(toStates == null) {
					badStates.add(s.getStateName());
				} else {
					// Mark the state as bad if all the states it leads to are bad
					boolean itsBad = true;
					for(S toState : toStates)
						if(!badStates.contains(toState.getStateName()))
							itsBad = false;
					if(itsBad) {
						badStates.add(s.getStateName());
					}
				}
			} // for all the state's transitions
			
			// If an uncontrollable observable event exists from any of the states in the original fsm, and
			// the event not allowed in the product, then UH-NO NOT HAP'NIN (mark the state
			String[] origStateNames = getOriginalComponentStates(s.getStateName());
			for(int i = 0; i < origStateNames.length; i++) {
				// Go through all the original transitions
				ArrayList<T> origTransitions = fsm.transitions.getTransitions(fsm.getState(origStateNames[i]));
				if(origTransitions != null) for(T t : origTransitions) {
					E event = t.getTransitionEvent();
					if(event instanceof EventObservability && ((EventObservability)event).getEventObservability() && event instanceof EventControllability && !((EventControllability)event).getEventControllability()) {
						// Then the event must be present in the product
						ArrayList<S> toStates = product.transitions.getTransitionStates(product.getState(specStateName), product.events.getEvent(event));
						// Mark the state as bad if the must transition does not exist
						if(toStates == null) {
							badStates.add(s.getStateName());
						} else {
							// Mark the state as bad if all the states it leads to are bad
							boolean itsBad = true;
							for(S toState : toStates)
								if(!badStates.contains(toState.getStateName()))
									itsBad = false;
							if(itsBad) {
								badStates.add(s.getStateName());
							}
						}
					} // if it's observable but NOT controllable
				} // for all the transitions
			} // for every component state in the original fsm
		} // for every state
		return badStates;
	} // markBadStates(FSM, FSM, HashSet) 
	
	/**
	 * Gets the original state names for the fsm using the product state names (which is in the form of
	 * ({state1,state2}, specState) and we want the array of states in the first set).
	 * 
	 * @param aggregateStateName - String object representing the state to break apart.
	 * @return - Returns an array of String objects with all the states from the original FSM represented in the input String.
	 */
	
	static private String[] getOriginalComponentStates(String aggregateStateName) {
		String[] productHalves = aggregateStateName.split(", ");
		aggregateStateName = productHalves[0].substring(2, productHalves[0].length() - 1); // Take out the braces
		return aggregateStateName.split(","); // Get the individual state names
	}
	
	/**
	 * Gets the original state name for the specification using the product state names (which is in the form of
	 * ({state1,state2}, specState) and we want the second entry in the product).
	 * 
	 * @param aggregateStateName - String representing the state to break apart.
	 * @return - Returns a String object with the state name from the specification.
	 */
	
	static private String getSpecificationState(String aggregateStateName) {
		String[] productHalves = aggregateStateName.split(", ");
		return productHalves[1].substring(1, productHalves[1].length() - 2); // Take out the braces
	}
	
	/**
	 * Looks into all the sets of states in the first entry in the product FSM and cycles through,
	 * making sure that it is possible to reach some final state from each of the states based on
	 * what events are allowed (which is defined by the transitions in the product).
	 * 
	 * @param fsm - FSM that has all the possible states and transitions.
	 * @param product - FSM that defines what events are allowed at a given state in fsm.
	 */
	
	static protected <S extends State, T extends Transition<S, E>, E extends Event, S1 extends State, E1 extends Event>
			void markDeadEnds(FSM<S, T, E> fsm, FSM<S1, DetTransition<S1, E1>, E1> product) {
		
		// When a state is processed, add it to the map and state if it reached a marked state.
		HashMap<String, Boolean> results = new HashMap<String, Boolean>();
		
		for(S1 curr : product.states.getStates()) {
			for(String subState : getOriginalComponentStates(curr.getStateName())) {
				// Check for a path to a marked state.
				boolean isCoaccessible = stateIsCoAccessible(fsm.getState(subState), curr, results, fsm, product);
				if(!isCoaccessible) {
					curr.setStateBad(true); // Mark it as a bad state if it's not coaccessible.
					break;
				} // if not coaccessible
			} // for every substate
		} // for every state
	} // markDeadEnds
	
	/**
	 * Evaluates if a given state in an FSM (associated with a given state in a product FSM) is connected to
	 * a marked state.
	 * To do so, it performs a breadth-first search looking for a marked state in the fsm using only transitions which are
	 * enabled by the product.
	 * 
	 * @param fsmState - State to evaluate for coaccessibility from the fsm.
	 * @param prodState - State to evaluate for coaccessibility from the product.
	 * @param results - HashMap mapping states to either true (if known to be coaccessible) or false (if known to be NOT coaccessible).
	 * @param fsm - FSM which is one of the FSMs in the product and has a similar alphabet.
	 * @param product - FSM which is composed of the FSM and another FSM.
	 * @return - Returns a boolean value; true if the fsm state leads to a marked state using the product's transitions; false otherwise.
	 */
	
	static private <S extends State, T extends Transition<S, E>, E extends Event, S1 extends State, E1 extends Event>
			boolean stateIsCoAccessible(S fsmState, S1 prodState, HashMap<String, Boolean> results, FSM<S, T, E> fsm, FSM<S1, DetTransition<S1, E1>, E1> product) {
		HashSet<String> visited = new HashSet<String>();
		visited.add(fsmState.getStateName() + prodState.getStateName()); // We visit the combo to catch loops (BUT THIS HAS A HIGH COMPLEXITY)
		
		// Base cases when already checked if the state was coaccessible
		Boolean check = results.get(fsmState.getStateName());
		if(check != null)
			return check;
		
		// If the state is marked, return true
		if(fsmState.getStateMarked()) {
			results.put(fsmState.getStateName(), true);
			return true;
		}
		
		// Go through all the accessible states and find something marked using bfs
		LinkedList<State> statesToProcess = new LinkedList<State>();
		statesToProcess.add(fsmState);
		statesToProcess.add(prodState);
		while(!statesToProcess.isEmpty()) {
			State fsmState1 = statesToProcess.poll();
			State prodState1 = statesToProcess.poll();
			
			// Go through the neighbours of fsmState
			ArrayList<T> thisTransitions = fsm.transitions.getTransitions((S)fsmState1);
			if(thisTransitions != null) for(T t : thisTransitions) {
				// Only proceed if the event is acceptable in the product as well
				ArrayList<S1> prodNextList = product.transitions.getTransitionStates(prodState1, product.events.getEvent(t.getTransitionEvent()));
				if(prodNextList != null) {
					
					// TODO: Make sure it NEVER returns a list of dead states.
					
					S1 prodNext = prodNextList.get(0);
					// Go through all the transition states
					for(S toState : t.getTransitionStates()) {
						// Return true if it's marked
						if(toState.getStateMarked()) {
							results.put(fsmState1.getStateName() + prodState1.getStateName(), false);
							return true;
						} // if it's marked
						// Return true if it's already known to be good
						if(results.get(toState.getStateName() + prodNext.getStateName()) == true)
							return true;
						if(!visited.contains(toState.getStateName() + prodNext.getStateName())) {
							statesToProcess.add(toState);
							statesToProcess.add(prodNext);
						} // if haven't visited the nodes
					} // for all the toStates
				} // if event is OK in product
			} // for all the transitions
		} // while queue is not empty
		
		// If made it here, it's not accessible.
		results.put(fsmState.getStateName() + prodState.getStateName(), false);
		return false;
	} // recursivelyFindMarked
	
//---  Copy methods that steal from other systems   -----------------------------------------------------------------------
	
	/**
	 * Copies the must transitions of another ModalSpecification into the current ModalSpecification.
	 * 
	 * @param other - ModalSpecification object whose transitions are to be copied.
	 */
	
	public void copyMustTransitions(ModalSpecification other) {
		for(State s : other.states.getStates()) {
			ArrayList<DetTransition<State, Event>> thisTransitions = other.mustTransitions.getTransitions(s);
			if(thisTransitions != null)
				for(DetTransition<State, Event> t : thisTransitions) {
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
		ArrayList<DetTransition<State, Event>> thisTransitions = mustTransitions.getTransitions(s1);
		if(thisTransitions != null) {
			for(DetTransition<State, Event> t : thisTransitions) {
				if(t.getTransitionEvent().equals(e)) {
					t.setTransitionState(s2);
					return;
				} // if equal
			} // for every transition
		} // if not null
		
		// Otherwise, make a new Transition object
		DetTransition<State, Event> outbound = transitions.getEmptyTransition();
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