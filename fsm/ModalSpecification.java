package fsm;

import support.*;
import support.attribute.EventControllability;
import support.attribute.EventObservability;
import support.event.Event;
import support.transition.DetTransition;
import support.transition.Transition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.io.*;
import fsm.attribute.Deterministic;

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

public class ModalSpecification extends TransitionSystem<DetTransition> implements Deterministic<DetTransition> {
	
//--- Constants  ----------------------------------------------------------------------
	
	public static final String MODAL_EXTENSION = ".mdl";
	
//--- Instance Variables  ----------------------------------------------------------------------
	
	/** ArrayList<<r>State> object that holds a list of Initial States for this Modal Specification object. */
	protected State initialState;
	/** TransitionFunction object mapping states to sets of "must" transitions for the Modal Specification.
	 * These are transitions which must be present in the controlled FSM in order to satisfy the spec. */
	protected TransitionFunction<DetTransition> mustTransitions;
	
//---  Constructors  --------------------------------------------------------------------------
	
	/**
	 * 
	 * 
	 * @param in
	 * @param inId
	 */
	
	public ModalSpecification(File in, String inId) {
		id = inId;
		events = new EventMap();
		states = new StateMap();
		transitions = new TransitionFunction<DetTransition>(new DetTransition());
		mustTransitions = new TransitionFunction<DetTransition>(new DetTransition());

		ReadWrite<DetTransition> redWrt = new ReadWrite<DetTransition>();
		ArrayList<ArrayList<String>> special = redWrt.readFromFile(states, events, transitions, in);
		
		if(special.get(0).size() > 0) {
			if(states.getState(special.get(0).get(0)) == null)
				states.addState(new State(special.get(0).get(0)));
			initialState = states.getState(special.get(0).get(0));	//Special ArrayList 0-entry is InitialState
			states.getState(initialState).setStateInitial(true);
		}
		for(int i = 0; i < special.get(1).size(); i++) {			//Special ArrayList 1-entry is MarkedState
			if(states.getState(special.get(1).get(i)) == null)
				states.addState(new State(special.get(1).get(i)));
			states.getState(special.get(1).get(i)).setStateMarked(true);
		}
		for(int i = 0; i <  special.get(2).size(); i++) {			//Special ArrayList 2-entry is PrivateState
			if(states.getState(special.get(2).get(i)) == null)
				states.addState(new State(special.get(2).get(i)));
			states.getState(special.get(2).get(i)).setStatePrivate(true);
		}
		for(int i = 0; i < special.get(3).size(); i++) {			//Special ArrayList 4-entry is Controllable Event
			if(events.getEvent(special.get(3).get(i)) == null)
				events.addEvent(special.get(3).get(i));
			events.getEvent(special.get(3).get(i)).setEventControllability(false);
		}
		for(int i = 0; i < special.get(4).size(); i++) {
			String grab[] = special.get(4).get(i).split(" ");
			mustTransitions.addTransitionState(states.getState(grab[0]), events.getEvent(grab[2]), states.getState(grab[1]));
		}	
	}
	
	/**
	 * Constructor for a ModalSpecification that takes any TransitionSystem as a parameter and
	 * creates a new ModalSpecification using that as the basis. Any information which is not
	 * permissible in a ModalSpecification is thrown away, because it does not have any means to handle it.
	 * 
	 * @param other - TransitionSystem object to copy as a ModalSpecification.
	 * @param inId - String object representing Id for the new ModalSpecification to carry.
	 */
	
	public <T1 extends Transition> ModalSpecification(TransitionSystem<T1> other, String inId) {
		id = inId;
		states = new StateMap();
		events = new EventMap();
		transitions = new TransitionFunction<DetTransition>(new DetTransition());
		
		copyStates(other); // Add in all the states (also sets up the initial states)
		copyEvents(other); // Add in all the events
		copyTransitions(other); // Add in all the transitions
		
		if(other instanceof ModalSpecification) 
			copyMustTransitions((ModalSpecification)other);
	} // ModalSpecification(TransitionSystem, String)
	
	/**
	 * Constructor for an ModalSpecification object that contains no transitions or states, allowing the
	 * user to add those elements themselves.
	 * 
	 * @param inId - String object representing the id representing this ModalSpecification object.
	 */
	
	public ModalSpecification(String inId) {
		id = inId;
		events = new EventMap();
		states = new StateMap();
		transitions = new TransitionFunction<DetTransition>(new DetTransition());
		mustTransitions = new TransitionFunction<DetTransition>(new DetTransition());
	} // ModalSpecification()
	
	/**
	 * Constructor for an ModalSpecification object that contains no transitions or states, allowing the
	 * user to add those elements themselves. It has no id, either.
	 */
	
	public ModalSpecification() {
		id = "";
		events = new EventMap();
		states = new StateMap();
		transitions = new TransitionFunction<DetTransition>(new DetTransition());
		mustTransitions = new TransitionFunction<DetTransition>(new DetTransition());
	} // ModalSpecification()
	
//---  Operations   -----------------------------------------------------------------------
	
	@Override
	public void toTextFile(String filePath, String name) {
		//Initial, Marked, Secret, Must-Transition
		if(name == null)
			name = id;
		String truePath = "";
		truePath = filePath + (filePath.charAt(filePath.length()-1) == '/' ? "" : "/") + name;
		String special = "5\n";
		
		ArrayList<String> init = new ArrayList<String>();
		ArrayList<String> mark = new ArrayList<String>();
		ArrayList<String> priv = new ArrayList<String>();
		ArrayList<String> cont = new ArrayList<String>();
		ArrayList<String> must = new ArrayList<String>();
		
		for(State s : this.getStates()) {
			if(s.getStateMarked()) 
				mark.add(s.getStateName());
			if(s.getStateInitial()) 
				init.add(s.getStateName());
			if(s.getStatePrivate())
				priv.add(s.getStateName());
		}
		
		for(Event e : this.getEvents()) {
			if(!e.getEventControllability())
				cont.add(e.getEventName());
		}
		
		for(Map.Entry<State, ArrayList<DetTransition>> map : mustTransitions.getAllTransitions()) {
			for(DetTransition t : map.getValue()) {
				must.add(map.getKey() + " " + t.getTransitionState().getStateName() + " " + t.getTransitionEvent().getEventName());
			}
		}
		
		special += init.size() + "\n";
		for(String s : init)
			special += s + "\n";
		special += mark.size() + "\n";
		for(String s : mark)
			special += s + "\n";
		special += priv.size() + "\n";
		for(String s : priv)
			special += s + "\n";
		special += cont.size() + "\n";
		for(String s : cont)
			special += s + "\n";
		special += must.size() + "\n";
		for(String s : must)
			special += s + "\n";

		ReadWrite<DetTransition> rdWrt = new ReadWrite<DetTransition>();
		rdWrt.writeToFile(truePath,  special, this.getTransitions(), MODAL_EXTENSION);
	}
	
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
	 * @param fsm - FSM<<r>State, T, Event> object 
	 * @return - Returns a maximally permissive controller for the original FSM.
	 * @throws IllegalArgumentException - Throws an illegal argument exception if the
	 * FSM defines one of the events in the modal specification as an unobservable event, which
	 * is illegal in this implementation.
	 */
	
	public <T extends Transition>
	DetObsContFSM makeOptimalSupervisor(FSM<T> fsm) throws IllegalArgumentException {
		//--------------------------------------------
		// Step 1: Create the reachable part of the combo
//		FSM newFSM = fsm.buildObserver();
		DetObsContFSM universalObserverView = new DetObsContFSM("UniObsView");
		HashMap<String, String> universalObserverViewMap = createUniversalObserverView(fsm, universalObserverView);
		String universalInitial = universalObserverViewMap.get(fsm.getInitialStates().get(0).getStateName());
		universalObserverView.addInitialState(universalInitial);
		
		DetObsContFSM specFSM = getUnderlyingFSM();
		
		// If we have unobservable events in the specification, that's illegal
		for(Event e : specFSM.events.getEvents()) {
			// See if it's visible in the FSM
			Event otherE = fsm.events.getEvent(e);
			if(otherE instanceof EventObservability && !((EventObservability)otherE).getEventObservability())
				throw new IllegalArgumentException("The modal specification has the event \"" + e.getEventName() + "\", which is an unobservable event in the plant passed in for getting the supervisor. The specification should only have observable events.");
		}
		
		FSM product = universalObserverView.product(specFSM);
		
		//--------------------------------------------
		// Step 2: Mark the bad states
		HashSet<String> badStates = new HashSet<String>();
		boolean keepGoing = true;
		
		while(keepGoing) {
			boolean keepGoing1 = markBadStates(fsm, specFSM, product, badStates);
			System.out.println(badStates.toString());
			boolean keepGoing2 = markDeadEnds(universalObserverView, universalObserverViewMap, product, badStates);
			System.out.println(badStates.toString());
			
			keepGoing = keepGoing1 || keepGoing2;
		} // while
		
		// Now, we have to actually create our FSM
		DetObsContFSM supervisor = new DetObsContFSM(product, badStates, fsm.id + " Supervisor");
		return supervisor.makeAccessible();
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
	
	private <T extends Transition>
	boolean markBadStates(FSM<T> fsm, DetObsContFSM specFSM, FSM<DetTransition> product, HashSet<String> badStates) {
		// We need to parse every state in the product, check every component state if there is some uncontrollable observable
		// event that isn't defined in the product.
		// Also look if must transitions exist...
		boolean foundABadOne = false;
		
		System.out.println("Going through bad states");
	
		// Go through every state in the product
		for(State s : product.getStates()) {
			// Don't process the state if it's already bad
			if(badStates.contains(s.getStateName())) continue;
			
			// If a must transition does not exist at the state, mark the state
			String specStateName = product.getStateComposition(s).get(1).getStateName(); // Gets the specification
			ArrayList<DetTransition> specTransitions = this.mustTransitions.getTransitions(this.getState(specStateName));
			if(specTransitions != null) for(DetTransition t : specTransitions) {
				Event event = t.getTransitionEvent();
				ArrayList<State> toStates = product.transitions.getTransitionStates(s, product.events.getEvent(event));
				// Mark the state as bad if the must transition does not exist
				if(toStates == null) {
					System.out.println("There was no must transition for an event, " + event.getEventName() + ", at state " + s.getStateName());
					badStates.add(s.getStateName());
					foundABadOne = true;
				} else {
					// Mark the state as bad if all the states it leads to are bad
					boolean itsBad = true;
					for(State toState : toStates)
						if(!badStates.contains(toState.getStateName())) itsBad = false;
					if(itsBad) {
						badStates.add(s.getStateName());
						foundABadOne = true;
					}
				}
			} // for all the state's transitions
			
			// If an uncontrollable observable event exists from any of the states in the original fsm, and
			// the event not allowed in the product, then UH-NO NOT HAP'NIN (mark the state)
			State observerState = product.getStateComposition(s).get(0);
			ArrayList<State> origStates = product.getStateComposition(observerState);
			for(State fromState : origStates) {
				// Go through all the original transitions
				ArrayList<T> origTransitions = fsm.transitions.getTransitions((State)fromState);
				if(origTransitions != null) for(T t : origTransitions) {
					Event event = t.getTransitionEvent();
					// If the event is observable but NOT controllable, we have a problem
					if(event instanceof EventObservability && ((EventObservability)event).getEventObservability() && event instanceof EventControllability && !((EventControllability)event).getEventControllability()) {
						// Then the event must be present in the product
						ArrayList<State> toStates = product.transitions.getTransitionStates(product.getState(s), product.events.getEvent(event));
						// Mark the state as bad if the event is not allowed in the product.
						if(toStates == null) {
							System.out.println("There was an uncontrollable, observable event, " + event.getEventName() + ", that did not exist in the spec at state " + s.getStateName());
							badStates.add(s.getStateName());
							foundABadOne = true;
						} else {
							// Mark the state as bad if all the states it leads to are bad
							boolean itsBad = true;
							for(State toState : toStates)
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
	 * Marks the dead ends where there is some state q in the set of states P in the product (P, s)
	 * that cannot reach a marked state in the universalObserverView using only transitions that are
	 * allowed in the product.
	 * 
	 * @param universalObserverView Observer view of the original FSM with states for every possible
	 * starting state (for instance, if the FSM had a state 2, then there will be a state representing
	 * the epsilon-reach of 2 in this universal view).
	 * @param universalObserverViewMap Map between the original FSM's states and their epsilon reach state
	 * name, which is present in the universalObserverView.
	 * @param product Product FSM between the observer view of the original fsm and the specification.
	 * @param badStates HashSet of String state names which are bad and to be removed.
	 * @return True if the method marked a bad state, false otherwise.
	 */

	static public boolean markDeadEnds(FSM<DetTransition> universalObserverView, HashMap<String, String> universalObserverViewMap, FSM<DetTransition> product, HashSet<String> badStates) {
		// For every combo of states (q,(P,s)) such that q is an element of P and (P,s) is the product, we want to
		// perform the product with initial states being the parameter product and every possible q. If it is
		// possible to reach a marked state in the product from this initial point, then it's ok! If there's one
		// q for the state which cannot reach a good state, then we have to add the state to the bad states.
		
		boolean removedAState = false;
		
		// To recover after operations:
		ArrayList<State> productInitialStates = product.getInitialStates();
		
		// Go through all the good product states
		for(State productState : product.getStates()) if(!badStates.contains(productState.getStateName())) {
			// Get all the states in the original FSM
			State leftSideOfProduct = product.getStateComposition(productState).get(0);
			ArrayList<State> originalStates = product.getStateComposition(leftSideOfProduct);
			for(State q : originalStates) {
				String universalInitial = universalObserverViewMap.get(q.getStateName());
				universalObserverView.addInitialState(universalInitial);
				product.addInitialState(productState);
				FSM<DetTransition> massiveProduct = universalObserverView.product(product);
				// Now, we get to look through the massive product for a marked state
				boolean reachesMarked = canReachMarked(massiveProduct, massiveProduct.getInitialStates().get(0), badStates);
				if(!reachesMarked) {
					System.out.println(massiveProduct.getInitialStates().get(0).getStateName() + " could not reach a marked state.");
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
	
	/**
	 * Checks if the FSM can reach a marked state from the initial state given. It excludes checking any
	 * states that have a badState in the right part of the product (the second element in the state name).
	 * 
	 * @param fsm Deterministic FSM to check.
	 * @param state State to look 
	 * @param badStates
	 * @return
	 */
	
	static protected 
	boolean canReachMarked(FSM<DetTransition> fsm, State state, HashSet<String> badStates) {
		// Get the name of the state in the right side of the product in the fsm (second part of the state).
		String name = fsm.getStateComposition(state).get(1).getStateName();
		if(badStates.contains(name)) return false;
		if(state.getStateMarked()) return true;
		
		HashSet<State> visited = new HashSet<State>();
		LinkedList<State> queue = new LinkedList<State>();
		visited.add(state);
		queue.add(state);
		
		while(!queue.isEmpty()) {
			// Go through all neighbours in BFS
			State curr = queue.poll();
			for(DetTransition t : fsm.transitions.getTransitions(curr)) for(State toState : t.getTransitionStates()) {
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
	
	public static <T extends Transition> HashMap<String, String> createUniversalObserverView(FSM<T> fsm, DetObsContFSM newFSM) {
		// Get the epsilon reaches of each state
		HashMap<State, HashSet<State>> epsilonReaches = fsm.transitions.getEpsilonReaches(fsm.states.getStates());
		// Store what the name of any given state's epsilon reach is
		HashMap<String, String> startingPositions = new HashMap<String, String>();
		// Store what states still need to be processed
		LinkedList<State> statesToProcess = new LinkedList<State>();
		// Keep track of states that were already processed
		HashSet<State> statesAlreadyCreated = new HashSet<State>();
		
		// Go through every single state (each could be initial)
		for(State state : fsm.states.getStates()) {
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
			HashMap<Event, HashSet<State>> toStates = new HashMap<Event, HashSet<State>>();
			for(State s : newFSM.getStateComposition(currState)) {
				// Go through all the transitions
				for(T t : fsm.transitions.getTransitions((State)s)) {
					Event e = t.getTransitionEvent();
					
					// Only continue if the event is observable (unobservable events are dealt with in epsilon reaches already)
					if(((EventObservability)e).getEventObservability()) {
						// Add the event, and map it to all the toStates's epsilon reaches.
						Event newEvent = newFSM.events.addEvent(e);
						HashSet<State> thisToStates = toStates.get(newEvent);
						if(thisToStates == null) thisToStates = new HashSet<State>();
						for(State toState : t.getTransitionStates()) {
							HashSet<State> epsilonReach = epsilonReaches.get(toState);
							thisToStates.addAll(epsilonReach);
						} // for all the states the transition leads to
						
						// Put the new list of toStates in the hashmap, but only if it's not empty
						if(!thisToStates.isEmpty()) toStates.put(newEvent, thisToStates);
					} // if we have an observable event
				} // for each transition
			} // for each state in the composition
			
			// Now, we get to add the transitions from the composed state to all the state sets!
			for(Map.Entry<Event, HashSet<State>> entry : toStates.entrySet()) {
				Event e = entry.getKey();
				State newState = addComposedState(newFSM, entry.getValue());
				DetTransition newTransition = new DetTransition(e, newState);
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

	private static <T extends Transition> State addComposedState(FSM<T> fsm, HashSet<State> stateCollection) {
		// Make the new state name
		StringBuilder sb = new StringBuilder();
		sb.append('{');
		
		// Keep track of the new state's properties
		boolean hasAMarkedState = false; // Mark it if AT LEAST ONE state is marked.
		boolean onlyHasSecret = true;
		
		ArrayList<State> stateList = new ArrayList<State>(stateCollection);
		Collections.sort(stateList);
		Iterator<State> itr = stateList.iterator();
		while(itr.hasNext()) {
			State curr = itr.next();
			if(curr.getStateMarked()) hasAMarkedState = true;
			if(!curr.getStatePrivate()) onlyHasSecret = false;
			
			// Add the state to the name of the new state
			sb.append(curr.getStateName());
			if(itr.hasNext())	sb.append(',');
			else					sb.append('}');
		}
		
		// Make the new state
		State newState = fsm.addState(sb.toString());
		newState.setStateMarked(hasAMarkedState);
		newState.setStatePrivate(onlyHasSecret);
		fsm.setStateComposition(newState, (State[])stateList.toArray(new State[stateCollection.size()]));
		
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
			ArrayList<DetTransition> thisTransitions = other.mustTransitions.getTransitions(s);
			if(thisTransitions != null)
				for(DetTransition t : thisTransitions) {
					// Add every state the transition leads to
					for(State toState : t.getTransitionStates())
						this.addMustTransition(s.getStateName(), t.getTransitionEvent().getEventName(), toState.getStateName());
				} // for every transition
		} // for every state
	} // copyMustTransitions(ModalSpecification)
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	@Override
	public ArrayList<State> getInitialStates() {
		ArrayList<State> s = new ArrayList<State>();
		if(initialState != null)
			s.add(initialState);
		return s;
	}
	
	@Override
	public State getInitialState() {
		return initialState;
	}

	@Override
	public boolean hasInitialState(String stateName) {
		State s = getState(stateName);
		return initialState.equals(s);
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
		ArrayList<DetTransition> thisTransitions = mustTransitions.getTransitions(s1);
		if(thisTransitions != null) {
			for(DetTransition t : thisTransitions) {
				if(t.getTransitionEvent().equals(e)) {
					t.setTransitionState(s2);
					return;
				} // if equal
			} // for every transition
		} // if not null
		
		// Otherwise, make a new Transition object
		DetTransition outbound = transitions.getEmptyTransition();
		outbound.setTransitionEvent(e);
		outbound.setTransitionState(s2);
		mustTransitions.addTransition(s1, outbound);
	}
	
	@Override
	public void addInitialState(String newInitial) {
		State theState = states.addState(newInitial);
		theState.setStateInitial(true);
		initialState = theState;
	}

	@Override
	public void addInitialState(State newState) {
		State theState = states.addState(newState);
		theState.setStateInitial(true);
		initialState = theState;
	}

	@Override
	public boolean removeInitialState(String stateName) {
		State theState = states.getState(stateName);
		if(theState != null) {
			theState.setStateInitial(false);
			initialState = null;
			return true;
		}
		return false;
	}
}
