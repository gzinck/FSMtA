package fsm;

import support.*;
import support.attribute.EventControllability;
import support.attribute.EventObservability;
import support.event.Event;
import support.event.ObsControlEvent;
import support.transition.DetTransition;
import support.transition.Transition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import fsm.attribute.*;

/**
 * ModalSpecification is an enhanced version of a transition system, which defines both "may" and
 * "must" transitions (ones which are allowed and ones which are absolutely required to satisfy the
 * specification for the service).
 * One central operation for the modal specification is to be able to make the optimal supervisor,
 * which takes a modal specification and disables transitions which are blocking iteratively to
 * create the supervisor that steps in the least frequently (it is maximally permissive).
 * 
 * This class is a part of the fsm package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public class ModalSpecification
		extends TransitionSystem<State, DetTransition<State, Event>, Event> {
	
//--- Constants  ----------------------------------------------------------------------
	
	/** String used to separate the two states visited when checking coaccessibility and keeping
	 * track of which state combinations have been visited in a HashSet. This string should be different
	 * from anything the user might use as names for states (though even if the user uses it, it's unlikely
	 * to cause huge issues).
	 */
	private static final String VISITED_STATE_SEPARATOR = "*h$#ksfUJF8;8s2%";
	
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
		
		copyStates(other); // Add in all the states (also sets up the initial states)
		copyEvents(other); // Add in all the events
		copyTransitions(other); // Add in all the transitions
		
		if(other instanceof ModalSpecification) copyMustTransitions((ModalSpecification)other);
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
	
	// Overrides the method from TransitionSystem to include the must transitions.
	@Override
	public String makeDotString() {
		String statesInDot = states.makeDotString();	//Have the StateMap do its thing
		String transitionsInDot = transitions.makeDotStringExcluding(mustTransitions);	//Have the TransitionFunction do its thing
		String mustTransitionsInDot = mustTransitions.makeDotString();
		return statesInDot + transitionsInDot + mustTransitionsInDot;	//Return 'em all
	}
	
	/**
	 * Gets the underlying FSM representation of the Modal Specification by copying all the
	 * data to a new deterministic FSM. It loses information about the must transitions.
	 * 
	 * @return DetObsContFSM which uses the underlying transition system of the modal specification.
	 */
	public DetObsContFSM getUnderlyingFSM() {
		DetObsContFSM specFSM = new DetObsContFSM();
		// Make the underlying FSM of the specification
		specFSM.copyEvents(this);
		specFSM.copyStates(this);
		specFSM.copyTransitions(this);
		return specFSM;
	}
	
	/**
	 * This method tries to get the maximally permissive (optimal) supervisor for an fsm
	 * (which will have certain transitions which are controllable and uncontrollable, etc.)
	 * that satisfies the modal specification. That is, it may ONLY have "may" transitions,
	 * and it MUST contain any "must" transitions (unless such a state does not exist, of course).
	 * 
	 * Any transitions which are illegal are removed, but if those transitions are uncontrollable,
	 * then we have a problem and we have to remove the entire state. States which do not lead
	 * to any marked states must also be removed. This process is done iteratively until all
	 * no more bad states exist and we have the maximally permissive supervisor.
	 * 
	 * This follows the algorithm explained in Darondeau et al., 2010.
	 * 
	 * @param fsm - FSM<<r>S, T, E> object 
	 * @return - Returns a maximally permissive controller for the original FSM.
	 * @throws IllegalArgumentException - Throws an illegal argument exception if the
	 * FSM defines one of the events in the modal specification as an unobservable event, which
	 * is illegal in this implementation.
	 */
	
	public <S extends State, T extends Transition<S, E>, E extends Event>
			FSM<S, T, E> makeOptimalSupervisor(FSM<S, T, E> fsm) throws IllegalArgumentException {
		//--------------------------------------------
		// Step 1: Create the reachable part of the combo
		// TODO: How can we make this parameterized? It doesn't seem to like me...
		FSM newFSM = (FSM)((Observability)fsm).createObserverView();
		DetObsContFSM specFSM = getUnderlyingFSM();
		
		// If we have unobservable events in the specification, that's illegal
		for(ObsControlEvent e : specFSM.events.getEvents()) {
			// See if it's visible in the FSM
			Event otherE = fsm.events.getEvent(e);
			if(otherE instanceof EventObservability && !((EventObservability)otherE).getEventObservability())
				throw new IllegalArgumentException("The modal specification has the event \"" + e.getEventName() + "\", which is an unobservable event in the plant passed in for getting the supervisor. The specification should only have observable events.");
		}
		
		FSM product = newFSM.product(specFSM);
		
		//--------------------------------------------
		// Step 2: Mark the bad states
		HashSet<String> badStates = new HashSet<String>();
		boolean keepGoing = true;
		
		DetObsContFSM universalObserverView = new DetObsContFSM("UniObsView");
		HashMap<String, String>universalObserverViewMap = createUniversalObserverView(fsm, universalObserverView);
		
		while(keepGoing) {
			boolean keepGoing1 = markBadStates(fsm, specFSM, product, badStates);
			System.out.println(badStates.toString());
			boolean keepGoing2 = markDeadEnds(universalObserverView, universalObserverViewMap, product, badStates);
			System.out.println(badStates.toString());
			
			keepGoing = keepGoing1 || keepGoing2;
			
			// TODO: MAKE SURE THIS LOOP DOESN'T GO FOREVER. When all the bad states are marked with the hashset, then
			// the we should construct the supervisor by copying all the states NOT marked bad and all the transitions
			// to states that are NOT marked bad from the product.
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
	 * @param badStates - HashMap of all the states already marked as bad, which will be further updated as
	 * we go through this iteration.
	 * @return - Returns true if at least one state was marked as bad, false otherwise.
	 */
	
	private <S extends State, T extends Transition<S, E>, E extends Event>
			boolean markBadStates(FSM<S, T, E> fsm, DetObsContFSM specFSM, FSM<S, DetTransition<S, E>, E> product, HashSet<String> badStates) {
		// We need to parse every state in the product, check every component state if there is some uncontrollable observable
		// event that isn't defined in the product.
		// Also look if must transitions exist...
		boolean foundABadOne = false;
		
		System.out.println("Going through bad states");
	
		// Go through every state in the product
		for(S s : product.getStates()) {
			// Don't process the state if it's already bad
			if(badStates.contains(s.getStateName())) continue;
			
			// If a must transition does not exist at the state, mark the state
			String specStateName = product.getStateComposition(s).get(1).getStateName(); // Gets the specification
			ArrayList<DetTransition<State, Event>> specTransitions = this.mustTransitions.getTransitions(this.getState(specStateName));
			if(specTransitions != null) for(DetTransition<State, Event> t : specTransitions) {
				Event event = t.getTransitionEvent();
				ArrayList<S> toStates = product.transitions.getTransitionStates(s, product.events.getEvent(event));
				// Mark the state as bad if the must transition does not exist
				if(toStates == null) {
					badStates.add(s.getStateName());
					foundABadOne = true;
				} else {
					// Mark the state as bad if all the states it leads to are bad
					boolean itsBad = true;
					for(S toState : toStates)
						if(!badStates.contains(toState.getStateName())) itsBad = false;
					if(itsBad) {
						badStates.add(s.getStateName());
						foundABadOne = true;
					}
				}
			} // for all the state's transitions
			
			// If an uncontrollable observable event exists from any of the states in the original fsm, and
			// the event not allowed in the product, then UH-NO NOT HAP'NIN (mark the state)
			S observerState = product.getStateComposition(s).get(0);
//			ArrayList<S> origStates = fsm.states.getStateComposition(observerState);
			ArrayList<S> origStates = product.getStateComposition(observerState);
			System.out.println(product.getComposedStates());
			for(S fromState : origStates) {
//				System.out.println(fromState.getStateName());
				// Go through all the original transitions
				ArrayList<T> origTransitions = fsm.transitions.getTransitions(fromState);
//				System.out.println(fsm.states.toString());
				if(origTransitions != null) for(T t : origTransitions) {
					E event = t.getTransitionEvent();
					// If the event is observable but NOT controllable, we have a problem
					if(event instanceof EventObservability && ((EventObservability)event).getEventObservability() && event instanceof EventControllability && !((EventControllability)event).getEventControllability()) {
						// Then the event must be present in the product
						ArrayList<S> toStates = product.transitions.getTransitionStates(product.getState(s), product.events.getEvent(event));
						// Mark the state as bad if the event is not allowed in the product.
						if(toStates == null) {
							System.out.println("There was an uncontrollable, observable event that did not exist in the spec.");
							badStates.add(s.getStateName());
							foundABadOne = true;
						} else {
							// Mark the state as bad if all the states it leads to are bad
							boolean itsBad = true;
							for(S toState : toStates)
								if(!badStates.contains(toState.getStateName()))
									itsBad = false;
							if(itsBad) {
								badStates.add(s.getStateName());
								foundABadOne = true;
							}
						}
					} // if it's observable but NOT controllable
				} // for all the transitions
			} // for every component state in the original fsm
		} // for every state
		return foundABadOne;
	} // markBadStates(FSM, FSM, FSM, HashSet)
	
	/**
	 * Gets the observer's state name for the fsm using the product state names (which is in the form of
	 * ({state1,state2},specState) and we want the aggregate of states in the first set).
	 * 
	 * @param aggregateStateName - String object representing the state to break apart.
	 * @return - Returns a String object with the state name from the observer.
	 */
	
//	static private String getObserverState(String aggregateStateName) {
//		String name = aggregateStateName.substring(1, aggregateStateName.length() - 1); // remove the main brackets
//		int numBrackets = 0;
//		int i = 0;
//		while(i < name.length()) {
//			char character = name.charAt(i++);
//			if(character == '(' || character == '{')
//				numBrackets++;
//			else if(character == ')' || character == '}')
//				numBrackets--;
//			else if(numBrackets == 0 && character == ',')
//				return name.substring(0, i - 1);
//		}
//		return null;
//	}
	
	/**
	 * Gets the original states in the first FSM which compose the states in the observer view, which is
	 * the first entry in the product entries of the state name.
	 * 
	 * @param aggregateStateName String representing the name of the state, like ({4,5,{3,5}},8).
	 * @return ArrayList of Strings that represent the original states in the first FSM, so in the
	 * above example, they would be ["4", "5", "{3,5}"].
	 */
	
//	static private ArrayList<String> getOriginalStates(String aggregateStateName) {
//		String observer = getObserverState(aggregateStateName); // remove the main brackets
//		System.out.println(observer);
//		if(observer.length() < 3) return null; 
//		String name = observer.substring(1, observer.length() - 1);
//		ArrayList<String> states = new ArrayList<String>();
//		int numBrackets = 0;
//		int lastSplice = 0; // Keep track of where the last splice was done to create a state name.
//		int i = 0;
//		while(i < name.length()) {
//			char character = name.charAt(i++);
//			if(character == '(' || character == '{')
//				numBrackets++;
//			else if(character == ')' || character == '}')
//				numBrackets--;
//			// If we reached an end condition, make it into a String representing the state.
//			if(numBrackets == 0 && character == ',') {
//				states.add(name.substring(lastSplice, i - 1));
//				lastSplice = i;
//			} else if(numBrackets == 0 && i == name.length()) {
//				states.add(name.substring(lastSplice, i));
//				lastSplice = i;
//			}
//		}
//		return states;
//	}
	
	/**
	 * Gets the original state name for the specification using the product state names (which is in the form of
	 * ({state1,state2},specState) and we want the second entry in the product).
	 * 
	 * @param aggregateStateName - String representing the state to break apart.
	 * @return - Returns a String object with the state name from the specification.
	 */
	
//	static private String getSpecificationState(String aggregateStateName) {
//		String name = aggregateStateName.substring(1, aggregateStateName.length() - 1); // remove the main brackets
//		int numBrackets = 0;
//		int i = 0;
//		while(i < name.length()) {
//			char character = name.charAt(i++);
//			if(character == '(' || character == '{')
//				numBrackets++;
//			else if(character == ')' || character == '}')
//				numBrackets--;
//			else if(numBrackets == 0 && character == ',')
//				return name.substring(i, name.length());
//		}
//		return null;
//	}
	
	static public <S extends State, E extends Event, S1 extends State, E1 extends Event>
			boolean markDeadEnds(FSM<S, DetTransition<S, E>, E> universalObserverView, HashMap<String, String> universalObserverViewMap, FSM<S1, DetTransition<S1, E1>, E1> product, HashSet<String> badStates) {
		// For every combo of states (q,(P,s)) such that q is an element of P and (P,s) is the product, we want to
		// perform the product with initial states being the parameter product and every possible q. If it is
		// possible to reach a marked state in the product from this initial point, then it's ok! If there's one
		// q for the state which cannot reach a good state, then we have to add the state to the bad states.
		
		boolean removedAState = false;
		
		// To recover after operations:
		ArrayList<S1> productInitialStates = product.getInitialStates();
		
		// Go through all the good product states
		for(S1 productState : product.getStates()) if(!badStates.contains(productState.getStateName())) {
			// Get all the states in the original FSM
			S1 leftSideOfProduct = product.getStateComposition(productState).get(0);
			ArrayList<S1> originalStates = product.getStateComposition(leftSideOfProduct);
			for(S1 q : originalStates) {
				String universalInitial = universalObserverViewMap.get(q.getStateName());
				universalObserverView.addInitialState(universalInitial);
				product.addInitialState(productState);
				FSM<S, DetTransition<S, E>, E> massiveProduct = universalObserverView.product(product);
				// Now, we get to look through the massive product for a marked state
				boolean reachesMarked = canReachMarked(massiveProduct, massiveProduct.getInitialStates().get(0), badStates);
				if(!reachesMarked) {
					badStates.add(productState.getStateName());
					removedAState = true;
					break;
				} // if cannot reach a marked state
			} // for every state q in the product's initial states
		} // for every product state that isn't marked as bad
		
		// Recover original properties of FSMs:
		product.addInitialState(productInitialStates.get(0));
		
		return removedAState;
	} // markDeadEnds(FSM, HashMap, FSM, HashSet)
	
	static protected <S extends State, E extends Event>
			boolean canReachMarked(FSM<S, DetTransition<S, E>, E> fsm, S state, HashSet<String> badStates) {
		// Get the name of the state in the right side of the product in the fsm (second part of the state).
		String name = fsm.getStateComposition(state).get(1).getStateName();
		if(badStates.contains(name)) return false;
		
		HashSet<S> visited = new HashSet<S>();
		LinkedList<S> queue = new LinkedList<S>();
		visited.add(state);
		queue.add(state);
		
		while(!queue.isEmpty()) {
			// Go through all neighbours in BFS
			S curr = queue.poll();
			for(DetTransition<S, E> t : fsm.transitions.getTransitions(curr)) for(S toState : t.getTransitionStates()) {
				name = fsm.getStateComposition(toState).get(1).getStateName();
				if(!badStates.contains(name)) {
					if(toState.getStateMarked()) return true; // if marked, we're all good!
					if(!visited.contains(toState)) { // if it hasn't been visited yet, add to queue
						queue.add(toState);
						visited.add(toState);
					} // if not yet visited
				} // if it's not a bad state
			} // for every transition state
		} // while the queue still has stuff in it
		
		return false;
	} // canReachMarked(FSM, HashSet)
	
//	/**
//	 * Looks into all the sets of states in the first entry in the product FSM and cycles through,
//	 * making sure that it is possible to reach some final state from each of the states based on
//	 * what events are allowed (which is defined by the transitions in the product).
//	 * 
//	 * @param fsm - FSM that has all the possible states and transitions.
//	 * @param product - FSM that defines what events are allowed at a given state in fsm.
//	 * @param badStates - HashSet of Strings which holds the names of the product's states
//	 * which are considered to be bad states.
//	 */
//	
//	static protected <S extends State, T extends Transition<S, E>, E extends Event, S1 extends State, E1 extends Event>
//			boolean markDeadEnds(FSM<S, T, E> fsm, FSM<S1, DetTransition<S1, E1>, E1> product, HashSet<String> badStates) {
//		// When a state is processed, add it to the map and say if it reached a marked state. True means the
//		// state is OK.
//		HashMap<String, Boolean> results = new HashMap<String, Boolean>();
//		boolean foundABadOne = false;
//		
//		for(S1 curr : product.states.getStates()) {
//			String observerStateName = getObserverState(curr.getStateName());
//			for(S subState : fsm.states.getStateComposition(fsm.getState(observerStateName))) {
//				// Check for a path to a marked state.
//				System.out.println(subState.getStateName());
//				boolean isCoaccessible = stateIsCoAccessible(fsm.getState(subState), curr, results, badStates, fsm, product);
//				if(!isCoaccessible) foundABadOne = true;
//			} // for every substate
//		} // for every state
//		
//		return foundABadOne;
//	} // markDeadEnds
	
//	/**
//	 * Evaluates if a given state in an FSM (associated with a given state in a product FSM) is connected to
//	 * a marked state.
//	 * To do so, it performs a breadth-first search looking for a marked state in the fsm using only transitions which are
//	 * enabled by the product.
//	 * 
//	 * @param fsmState - State to evaluate for coaccessibility from the fsm.
//	 * @param prodState - State to evaluate for coaccessibility from the product.
//	 * @param results - HashMap mapping states to either true (if known to be coaccessible) or false (if known to be NOT coaccessible).
//	 * @param fsm - FSM which is one of the FSMs in the product and has a similar alphabet.
//	 * @param product - FSM which is composed of the FSM and another FSM.
//	 * @return - Returns a boolean value; true if the fsm state leads to a marked state using the product's transitions; false otherwise.
//	 */
//	
//	static private <S extends State, T extends Transition<S, E>, E extends Event, S1 extends State, E1 extends Event>
//			boolean stateIsCoAccessible(S fsmState, S1 prodState, HashMap<String, Boolean> results, HashSet<String> badStates, FSM<S, T, E> fsm, FSM<S1, DetTransition<S1, E1>, E1> product) {
//		HashSet<String> visited = new HashSet<String>();
//		visited.add(fsmState.getStateName() + VISITED_STATE_SEPARATOR + prodState.getStateName()); // We visit the combo to catch loops (BUT THIS HAS A HIGH COMPLEXITY)
//		
//		// Base case when already checked if the state was coaccessible
//		Boolean check = results.get(fsmState.getStateName());
//		if(check != null)
//			return check;
//		
//		// Base case when we actually have a bad state here...
//		if(badStates.contains(prodState.getStateName()))
//			return false;
//		
//		// If the state is marked, return true
//		if(fsmState.getStateMarked()) {
//			results.put(fsmState.getStateName() + VISITED_STATE_SEPARATOR + prodState.getStateName(), true);
//			return true;
//		}
//		
//		// Go through all the accessible states and find something marked using bfs
//		LinkedList<S> fsmStatesToProcess = new LinkedList<S>();
//		LinkedList<S1> prodStatesToProcess = new LinkedList<S1>();
//		fsmStatesToProcess.add(fsmState);
//		prodStatesToProcess.add(prodState);
//		while(!fsmStatesToProcess.isEmpty() && !prodStatesToProcess.isEmpty()) {
//			S currFSMState = fsmStatesToProcess.poll();
//			S1 currProdState = prodStatesToProcess.poll();
//			
//			// Go through the neighbours of fsmState
//			ArrayList<T> thisTransitions = fsm.transitions.getTransitions(currFSMState);
//			if(thisTransitions != null) for(T t : thisTransitions) {
//				// Only proceed if the event is acceptable in the product as well
//				ArrayList<S1> prodNextList = product.transitions.getTransitionStates(currProdState, product.events.getEvent(t.getTransitionEvent()));
//				if(prodNextList != null && prodNextList.size() != 0) {
//					
//					S1 prodNext = prodNextList.get(0);
//					// Check if the state is dead/bad in the product.
//					boolean dead = (badStates.contains(prodNext.getStateName())) ? true : false;
//					if(!dead) {
//						// Go through all the transition states (there should only be one, but anyways...)
//						for(S toState : t.getTransitionStates()) {
//							// Return true if it's marked
//							if(toState.getStateMarked()) {
//								results.put(currFSMState.getStateName() + VISITED_STATE_SEPARATOR + currProdState.getStateName(), true);
//								return true;
//							} // if it's marked
//							// Return true if it's already known to be good
//							if(results.get(toState.getStateName() + VISITED_STATE_SEPARATOR + prodNext.getStateName()) == true)
//								return true;
//							if(!visited.contains(toState.getStateName() + VISITED_STATE_SEPARATOR + prodNext.getStateName())) {
//								fsmStatesToProcess.add(toState);
//								prodStatesToProcess.add(prodNext);
//							} // if haven't visited the nodes
//						} // for all the toStates
//					} // if not all states are dead
//				} // if event is OK in product
//			} // for all the transitions
//		} // while queue is not empty
//		
//		// If made it here, it's not accessible.
//		results.put(fsmState.getStateName() + VISITED_STATE_SEPARATOR + prodState.getStateName(), false);
//		return false;
//	} // recursivelyFindMarked
	
//---  Operations for converting the observer view of the fsm at any given state   -----------------------------------------------------------------------
	
	/**
	 * Creates a universal observer view that allows you to look up possible transitions from any possible
	 * initial state. It makes the observer view such that if AT LEAST one state is marked, then the ENTIRE
	 * observer state is marked (which is more useful for trying to find if you can possibly reach a marked
	 * state from a given initial state).
	 * 
	 * @param fsm FSM with which to create the observer view
	 * @param newFSM FSM to fill with all the observer view states and transitions, etc.
	 * @return HashMap mapping the name of the original states in the fsm to the name of its epsilon reach state
	 * which is present in the resulting observer view FSM. 
	 */
	
	public static <S extends State, T extends Transition<S, E>, E extends Event> HashMap<String, String> createUniversalObserverView(FSM<S, T, E> fsm, DetObsContFSM newFSM) {
		// Get the epsilon reaches of each state
		HashMap<S, HashSet<S>> epsilonReaches = fsm.transitions.getEpsilonReaches(fsm.states.getStates());
		// Store what the name of any given state's epsilon reach is
		HashMap<String, String> startingPositions = new HashMap<String, String>();
		// Store what states still need to be processed
		LinkedList<State> statesToProcess = new LinkedList<State>();
		// Keep track of states that were already processed
		HashSet<State> statesAlreadyCreated = new HashSet<State>();
		
		// Go through every single state (each could be initial)
		for(S state : fsm.states.getStates()) {
			// Make an initial state from the epsilon reach
			State newState = addComposedState(newFSM, epsilonReaches.get(state));
			if(!statesAlreadyCreated.contains(newState)) {
				statesToProcess.add(newState);
				statesAlreadyCreated.add(newState);
			} // if state does not already exist
			startingPositions.put(state.getStateName(), newState.getStateName());
		} // for every state in the fsm
		
		// Go through all the observer view states that have not been processed.
		while(!statesToProcess.isEmpty()) {
			State currState = statesToProcess.poll();
			// Now, go through all the OBSERVABLE transitions from all of these states
			HashMap<ObsControlEvent, HashSet<S>> toStates = new HashMap<ObsControlEvent, HashSet<S>>();
			for(State s : newFSM.getStateComposition(currState)) {
				// Go through all the transitions
				for(T t : fsm.transitions.getTransitions((S)s)) {
					E e = t.getTransitionEvent();
					
					// Only continue if the event is observable (unobservable events are dealt with in epsilon reaches already)
					if(((EventObservability)e).getEventObservability()) {
						// Add the event, and map it to all the toStates's epsilon reaches.
						ObsControlEvent newEvent = newFSM.events.addEvent(e);
						HashSet<S> thisToStates = toStates.get(newEvent);
						if(thisToStates == null) thisToStates = new HashSet<S>();
						for(S toState : t.getTransitionStates()) {
							HashSet<S> epsilonReach = epsilonReaches.get(toState);
							thisToStates.addAll(epsilonReach);
						} // for all the states the transition leads to
						
						// Put the new list of toStates in the hashmap, but only if it's not empty
						if(!thisToStates.isEmpty()) toStates.put(newEvent, thisToStates);
					} // if we have an observable event
				} // for each transition
			} // for each state in the composition
			
			// Now, we get to add the transitions from the composed state to all the state sets!
			for(Map.Entry<ObsControlEvent, HashSet<S>> entry : toStates.entrySet()) {
				ObsControlEvent e = entry.getKey();
				State newState = addComposedState(newFSM, entry.getValue());
				DetTransition<State, ObsControlEvent> newTransition = new DetTransition<State, ObsControlEvent>(e, newState);
				newFSM.addTransition(currState, newTransition);
				
				if(!statesAlreadyCreated.contains(newState)) {
					statesToProcess.add(newState);
					statesAlreadyCreated.add(newState);
				} // if state does not already exist
			} // for all the observable events (with their accompanying states)
		}
		
		return startingPositions;
	} // createUniversalObserverView(FSM)
	
	/**
	 * Adds a composed state made of all the all the states in the parameter HashSet to the parameter FSM
	 * and returns the state.
	 * 
	 * @param fsm FSM to which to add a new state.
	 * @param stateCollection HashSet of states to combine into a single state in the FSM.
	 * @return State that was composed from the state collection and added to the FSM.
	 */
	private static <S extends State, S1 extends State, T extends Transition<S, E>, E extends Event> S addComposedState(FSM<S, T, E> fsm, HashSet<S1> stateCollection) {
		// Make the new state name
		StringBuilder sb = new StringBuilder();
		sb.append('{');
		
		// Keep track of the new state's properties
		boolean hasAMarkedState = false; // Mark it if AT LEAST ONE state is marked.
		boolean onlyHasSecret = true;
		
		ArrayList<S1> stateList = new ArrayList<S1>(stateCollection);
		Collections.sort(stateList);
		Iterator<S1> itr = stateList.iterator();
		while(itr.hasNext()) {
			S1 curr = itr.next();
			if(curr.getStateMarked()) hasAMarkedState = true;
			if(!curr.getStatePrivate()) onlyHasSecret = false;
			
			// Add the state to the name of the new state
			sb.append(curr.getStateName());
			if(itr.hasNext())	sb.append(',');
			else					sb.append('}');
		}
		
		// Make the new state
		S newState = fsm.addState(sb.toString());
		newState.setStateMarked(hasAMarkedState);
		newState.setStatePrivate(onlyHasSecret);
		fsm.setStateComposition(newState, (S[])stateList.toArray(new State[stateCollection.size()]));
		
		return newState;
	}
	
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
