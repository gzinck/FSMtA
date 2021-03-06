package fsm;

import support.attribute.EventControllability;
import support.attribute.EventObservability;
import support.map.TransitionFunction;
import support.DisabledEvents;
import support.map.StateMap;
import support.map.EventMap;
import support.transition.*;
import support.ReadWrite;
import fsm.attribute.*;
import graphviz.FSMToDot;
import support.Event;
import support.State;
import java.io.File;
import java.util.*;
import test.windows;

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

public class ModalSpecification extends TransitionSystem<DetTransition> implements Deterministic<DetTransition>,
																				 Observability,
																				 Controllability,
																				 OpacityTest{
	
//--- Constants  ----------------------------------------------------------------------
	
	/** String object constant representing the file extension of a file representing a ModalSpecification object. */
	public static final String MODAL_EXTENSION = ".mdl";
	
//--- Instance Variables  ----------------------------------------------------------------------
	
	/** ArrayList<<r>State> object that holds a list of Initial States for this Modal Specification object. */
	protected State initialState;
	/** TransitionFunction object mapping states to sets of "must" transitions for the Modal Specification.
	 * These are transitions which must be present in the controlled FSM in order to satisfy the spec. */
	protected TransitionFunction<DetTransition> mustTransitions;
	
//---  Constructors  --------------------------------------------------------------------------
	
	/**
	 * Constructor for a ModalSpecification that takes in a file and a String id,
	 * reading and interpreting the file as a ModalSpecification.
	 * 
	 * @param in - File input with the ModalSpecification's information (states, transitions, events...)
	 * @param inId - String representing the ModalSpecification's id.
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
		for(int i = 0; i < special.get(3).size(); i++) {			//Special ArrayList 3-entry is Controllable Event
			if(events.getEvent(special.get(3).get(i)) == null)
				events.addEvent(special.get(3).get(i));
			events.getEvent(special.get(3).get(i)).setEventObservability(false);
		}
		for(int i = 0; i < special.get(4).size(); i++) {			//Special ArrayList 4-entry is ObservableEvent
			if(events.getEvent(special.get(4).get(i)) == null)
				events.addEvent(special.get(4).get(i));
			events.getEvent(special.get(4).get(i)).setEventAttackerObservability(false);
		}
		for(int i = 0; i < special.get(5).size(); i++) {			//Special ArrayList 5-entry is Attacker Observable Event
			if(events.getEvent(special.get(5).get(i)) == null)
				events.addEvent(special.get(5).get(i));
			events.getEvent(special.get(5).get(i)).setEventControllability(false);
		}
		for(int i = 0; i < special.get(6).size(); i++) {			//Special ArrayList 6-entry is Must Transitions
			String grab[] = special.get(6).get(i).split(" ");
			if(states.getState(grab[0]) == null)
				states.addState(new State(grab[0]));
			if(states.getState(grab[1]) == null)
				states.addState(new State(grab[1]));
			if(events.getEvent(grab[2]) == null)
				events.addEvent(grab[2]);
			mustTransitions.addTransitionState(states.getState(grab[0]), events.getEvent(grab[2]), states.getState(grab[1]));
			transitions.addTransitionState(states.getState(grab[0]), events.getEvent(grab[2]), states.getState(grab[1]));
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
	 * Constructor for a ModalSpecification that takes any TransitionSystem as a parameter and
	 * creates a new ModalSpecification using that as the basis. Any information which is not
	 * permissible in a ModalSpecification is thrown away, because it does not have any means to handle it.
	 * It excludes any states included in the HashSet of badStates.
	 * 
	 * @param other - TransitionSystem object to copy as a ModalSpecification.
	 * @param badStates - HashSet of Strings representing the states which are bad and should not be copied
	 * to the new ModalSpecification in any way.
	 * @param inId - String object representing Id for the new ModalSpecification to carry.
	 */
	
	public ModalSpecification(ModalSpecification other, HashSet<String> badStates, String inId) {
		id = inId;
		states = new StateMap();
		events = new EventMap();
		transitions = new TransitionFunction<DetTransition>(new DetTransition());
		mustTransitions = new TransitionFunction<DetTransition>(new DetTransition());
		
		// If the initial state is bad, then we don't do anything
		initialState = other.getInitialState();
		copyStates(other, badStates); // Add in all the states NOT in the badStates set
		copyEvents(other); // Add in all the events
		// Add in all the transitions (but only take the first state it transitions to) IF NOT in badStates set
		for(State s : other.states.getStates()) if(!badStates.contains(s.getStateName())) {
			for(Transition t : other.transitions.getTransitions(s)) {
				String toStateName = t.getTransitionStates().get(0).getStateName();
				if(!badStates.contains(toStateName))
					this.addTransition(s.getStateName(), t.getTransitionEvent().getEventName(), toStateName);
			} // for every transition
		} // for every state
		if(other instanceof ModalSpecification) {
			copyMustTransitions((ModalSpecification)other, badStates);
		}
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
	
//---  Operations   ---------------------------------------------------------------------------

	/**
	 * Gets the underlying FSM representation of the Modal Specification by copying all the
	 * data to a new deterministic FSM. It loses information about the must transitions.
	 * 
	 * @return - Returns a DetObsContFSM which uses the underlying transition system of the modal specification.
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
	 * This method builds the optimal opaque controller of a provided Modal Specification,
	 * producing a DetObsContFSM object.
	 * 
	 * @return - Returns a DetObsContFSM object representing the optimal controller of the calling ModalSpecification object.
	 */
	
	public DetObsContFSM buildOptimalOpaqueController() {
		DetObsContFSM optimal = new DetObsContFSM();
		
		LinkedList<State> queue = new LinkedList<State>();
		HashSet<State> initial = epsilonReach(this, getInitialState(), true); 
		State init = new State(initial.toArray(new State[initial.size()]));
		init.setStateName(getInitialState().getStateName() + "," + init.getStateName());
		queue.add(init);
		optimal.addInitialState(init);
		boolean secret = true;
		for(State s : initial) {
			if(!s.getStatePrivate())
				secret = false;
		}
		init.setStatePrivate(secret);
		
		HashMap<State, HashSet<State>> map = new HashMap<State, HashSet<State>>();
		HashMap<State, State> original = new HashMap<State, State>();
		original.put(init, getInitialState());
		map.put(init, new HashSet<State>(initial));
		
		HashSet<State> visited = new HashSet<State>();
		
		while(!queue.isEmpty()) {
			State top = queue.poll();
			if(visited.contains(top)) {
				continue;
			}
			visited.add(top);
			
			State orig = original.get(top);
			ArrayList<State> components = new ArrayList<State>(map.get(top));
			Collections.sort(components);
			
			secret = true;
			for(State s : components) {
				if(!s.getStatePrivate())
					secret = false;
			}
			top.setStatePrivate(secret);
			
			for(DetTransition t : getTransitions().getTransitions(orig)) {
				State newReal = t.getTransitionState();
				HashSet<State> newReach = epsilonReach(this, newReal, true);
					
				if(!t.getTransitionEvent().getEventAttackerObservability()) {
					newReach.addAll(components);
				}
					
				State added = new State(newReach.toArray(new State[newReach.size()]));
				added = optimal.addState(newReal.getStateName() + "," + added.getStateName());
				map.put(added, newReach);
				original.put(added, newReal);
				queue.add(added);
				optimal.addTransition(top, t.getTransitionEvent(), added);
					
			}
			
		}
		
		return optimal;
	}

	/**
	 * This method finds the epsilon-reach of a given State in a ModalSpecification (can be generalized easily);
	 * that is, all States that that first State can reach via Unobservable Events, returned as a HashSet to avoid
	 * duplicates.
	 * 
	 * @param ts - ModalSpecification object provided to have its States searched through.
	 * @param s - State object provided as the root State to search for's epsilon-reach.
	 * @param must - boolean value describing whether or not the algorithm should respect May/Must Transitions or not.
	 * @return - Returns a HashSet<<r>State> object containing all States in the epsilon-reach of the provided first State.
	 */
	
	public HashSet<State> epsilonReach(ModalSpecification ts, State s, boolean must){
		HashSet<State> outbound = new HashSet<State>();
		
		LinkedList<State> queue = new LinkedList<State>();
		HashSet<State> visited = new HashSet<State>();
		queue.add(s);
		
		while(!queue.isEmpty()) {
			State st = queue.poll();
			if(visited.contains(st))
				continue;
			visited.add(st);
			outbound.add(st);
			for(DetTransition t : ts.getTransitions().getTransitions(st)) {
				if(!t.getTransitionEvent().getEventAttackerObservability()) {
					queue.add(t.getTransitionState());
				}
			}
		}
		return outbound;
	}
	
	@Override
	public void toTextFile(String filePath, String name) {
		//Initial, Marked, Secret, Must-Transition
		if(name == null)
			name = id;
		String truePath = "";
		truePath = filePath + (filePath.charAt(filePath.length()-1) == '/' ? "" : "/") + name;
		String special = "7\n";
		
		ArrayList<String> init = new ArrayList<String>();
		ArrayList<String> mark = new ArrayList<String>();
		ArrayList<String> priv = new ArrayList<String>();
		ArrayList<String> unob = new ArrayList<String>();
		ArrayList<String> atta = new ArrayList<String>();
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
			if(!e.getEventObservability())
				unob.add(e.getEventName());
			if(!e.getEventAttackerObservability())
				atta.add(e.getEventName());
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
		special += unob.size() + "\n";
		for(String s : unob)
			special += s + "\n";
		special += atta.size() + "\n";
		for(String s : atta)
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
		String transitionsInDot = transitions.makeDotStringExcluding(new TransitionFunction(new DetTransition()));	//Have the TransitionFunction do its thing
		String mustTransitionsInDot = mustTransitions.makeDotString();
		return statesInDot + transitionsInDot + mustTransitionsInDot;	//Return 'em all
	}

	@Override
	public ModalSpecification makeAccessible() {
		// Make a queue to keep track of states that are accessible and their neighbours.
		LinkedList<State> queue = new LinkedList<State>();
		
		// Initialize a new ModalSpecification with initial states.
		ModalSpecification newMS = new ModalSpecification(this.id + " Accessible");
		if(initialState != null) {
			State newInitial = newMS.addState(initialState);
			newMS.addInitialState(newInitial);
			queue.add(initialState);
			
			while(!queue.isEmpty()) {
				State curr = queue.poll();
				// Go through the transitions
				ArrayList<DetTransition> currMayTransitions = this.transitions.getTransitions(curr);
				ArrayList<DetTransition> currMustTransitions = this.mustTransitions.getTransitions(curr);
				if(currMayTransitions != null) for(DetTransition t : currMayTransitions) {
					// Add the states; it goes to to the queue if not already present in the newFSM
					State s = t.getTransitionState();
					if(!newMS.stateExists(s.getStateName())) {
						newMS.addState(s);
						queue.add(s);
					}
					// Add the transition by copying the old one.
					newMS.addTransition(newMS.getState(curr), t);
				}
				if(currMustTransitions != null) for(DetTransition t : currMustTransitions) {
					// Add the states; it goes to to the queue if not already present in the newFSM
					State s = t.getTransitionState();
					if(!newMS.stateExists(s.getStateName())) {
						newMS.addState(s);
						queue.add(s);
					}
					// Add the transition by copying the old one.
					newMS.addMustTransition(newMS.getState(curr), t);
				}
			} // while
		} // if initial is not null
		return newMS;	
	} // makeAccessible()

	@Override
	public FSM getSupremalControllableSublanguage(FSM other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<State> testCurrentStateOpacity() {
		ArrayList<State> secrets = new ArrayList<State>();
		for(State s : this.getStates()) {
			if(s.getStatePrivate())
				secrets.add(s);
		}
		return secrets;
	}

//---  Multi-FSM Operations   -----------------------------------------------------------------
	
	/**
	 * This method performs a Product(or Intersection) operation between multiple FSM objects, one provided as an
	 * argument and the other being the FSM object calling this method, and returns the resulting FSM object.
	 * 
	 * TODO: Make sure this works, as it is repurposed from FSM algorithms
	 * 
	 * @param other - Array of FSM extending objects that performs the product operation on with the current FSM.
	 * @return - Returns a FSM extending object representing the FSM object resulting from all Product operations.
	 */
	
	public ModalSpecification product(ModalSpecification ... other) {
		ModalSpecification newFSM = new ModalSpecification();
		this.productHelper(other[0], newFSM);
		for(int i = 1; i < other.length; i++) {
			ModalSpecification newerFSM = new ModalSpecification();
			newFSM.productHelper(other[i], newerFSM);
			newFSM = newerFSM;
		}
		return newFSM;
	}
	
	/**
	 * Helper method that performs the brunt of the operations involved with a single Product operation
	 * between two FSM objects, leaving the specialized features in more advanced FSM types to their
	 * own interpretations after this function has occurred.
	 * 
	 * Performs a product operation on the calling FSM with the first parameter FSM, and builds the
	 * resulting FSM in the second FSM. Has no return, does its action by side-effect.
	 * 
	 * TODO: Make sure this works, as it is repurposed from FSM algorithms
	 * 
	 * @param other - FSM object representing the FSM object performing the Product operation with the calling FSM object.
	 * @param newFSM - FSM object representing the FSM holding the contents of the product of the Product operation.
	 */
	
	protected void productHelper(ModalSpecification other, ModalSpecification newFSM) {
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
					ArrayList<DetTransition> thisTransitions = this.transitions.getTransitions(thisState);
					ArrayList<DetTransition> otherTransitions = other.transitions.getTransitions(otherState);
					if(thisTransitions != null && otherTransitions != null) {
						for(DetTransition thisTrans : thisTransitions) {
							for(DetTransition otherTrans : otherTransitions) {
								
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
	 * This method performs the Parallel Composition of multiple FSMs: the FSM calling this method and the FSMs
	 * provided as arguments. The resulting, returned, FSM will be the same type as the calling FSM.
	 * 
	 * TODO: Make sure this works, as it is repurposed from FSM algorithms
	 * 
	 * @param other - Array of FSM extending objects provided to perform Parallel Composition with the calling FSM object.
	 * @return - Returns a FSM extending object representing the result of all Parallel Composition operations.
	 */
	
	public ModalSpecification parallelComposition(ModalSpecification ... other) {
		ModalSpecification newFSM = this;
		for(int i = 0; i < other.length; i++) {
			ModalSpecification newerFSM = new ModalSpecification();
			newFSM.parallelCompositionHelper(other[i], newerFSM);
			newFSM = newerFSM;
		}
		return newFSM;
	}
	
	/**
	 * Helper method that performs the brunt of the operations involved with a single Parallel Composition
	 * operation between two FSM objects, leaving the specialized features in more advanced FSM types to
	 * their own interpretations after this function has occurred.
	 * 
	 * Performs a Parallel Composition operation on the FSM object calling this method with the FSM object provided as
	 * an argument (other), and places the results of this operation into the provided FSM object (newFSM).
	 * 
	 * TODO: Make sure this works, as it is repurposed from FSM algorithms
	 * 
	 * @param other - FSM extending object that performs the Parallel Composition operation with FSM object calling this method.
	 * @param newFSM - FSM extending object that is provided to contain the results of this Parallel Composition operation.
	 */
	
	protected void parallelCompositionHelper(ModalSpecification other, ModalSpecification newFSM) {
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
					ArrayList<DetTransition> thisTransitions = this.transitions.getTransitions(thisState);
					ArrayList<DetTransition> otherTransitions = other.transitions.getTransitions(otherState);
					if(thisTransitions != null && otherTransitions != null) {
						for(DetTransition thisTrans : thisTransitions) {
							for(DetTransition otherTrans : otherTransitions) {
								
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
						for(DetTransition thisTrans : thisTransitions) {
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
						for(DetTransition otherTrans : other.transitions.getTransitions(otherState)) {
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
	
//---  Operations for pruning a MS   ----------------------------------------------------------

	/**
	 * This method implements the new method of performing a 'prune' on a Modal Specification as
	 * described by Graeme Zinck's research; the crux point of it being that if a State is found
	 * to be Bad, it may not be by virtue of finding a legalizing Transition using only Events
	 * unique to one of the combining Modal Specifications.
	 * 
	 * tldr; There's a second way for a State to be Good.
	 * 
	 * Overall the method finds all bad states which break the rules of a Modal Specification
	 * and removes them alongside their Transitions, leaving only a non-blocking workable system.
	 * 
	 * @param modal1 - The first of the two Modal Specification objects being combined in a Greatest Lower Bound operation.
	 * @param modal2 - The second of the two Modal Specification objects being combined in a Greatest Lower Bound operation.
	 * @param composedModal - The composed Modal Specification object that we are pruning after having been created.
	 * @return - Returns the pruned version of the provided composed Modal Specification, composedModal.
	 */
	
	public ModalSpecification newPrune(ModalSpecification modal1, ModalSpecification modal2, ModalSpecification composedModal) {
		boolean mustIterate = true;
		HashSet<State> badStates = new HashSet<State>();
		
		while(mustIterate) {				//Iterate through all States until a pass through does not change anything
			mustIterate = false;			//In each iteration, check if the State is bad via stateIsBad() method.
			for(State s : composedModal.getStates()) {
				if(badStates.contains(s))	//Unless already dealt with and found to be bad. (If good, need check again.)
					continue;
				if(stateIsBad(modal1, modal2, composedModal, s)) {
					badStates.add(s);		//If found bad, add to list, and remove from the Modal Specification entirely.
					mustIterate = true;		//Requires n-state traversal because we don't have back-referencing.
					for(State top : composedModal.getStates()) {
						for(int i = 0; i < composedModal.getTransitions().getTransitions(top).size(); i++){
							DetTransition t = composedModal.getTransitions().getTransitions(top).get(i);
							if(s.equals(t.getTransitionState()))
								composedModal.getTransitions().getTransitions(top).remove(t);
						}
					}
				}
			}
		}
		
		composedModal.getStateMap().removeStates(badStates);	//Now remove the bad States
		composedModal.getTransitions().removeStates(badStates);
		for(State s : composedModal.getStates()) {				//Some retention of bad Transitions; we know the State is good, just remove the Transition.
			ArrayList<DetTransition> mustTrans = composedModal.getMustTransitions().getTransitions(s);
			ArrayList<DetTransition> mayTrans = composedModal.getTransitions().getTransitions(s);
			for(int i = 0; i < mustTrans.size(); i++) {
				DetTransition t = mustTrans.get(i);
				if(!mayTrans.contains(t)) {
					composedModal.getMustTransitions().getTransitions(s).remove(t);
					i--;
				}
			}
		}
		for(State s : badStates) {
			composedModal.getMustTransitions().removeState(s);
			composedModal.getTransitions().removeState(s);
		}
		
		return composedModal.makeAccessible();		//And some bits will be left in but disjoint, so clean that up.
	}
	

	/**
	 * This is a helper method to the newPrune algorithm that searches through a provided State's transitions
	 * to decide if it is a 'good' State or a 'bad' State.
	 * 
	 * @param modal1 - Modal Specification object representing the first of the two composed objects.
	 * @param modal2 - Modal Specification object representing the second of the two composed objects.
	 * @param composedModal - Modal Specification object representing the composition of the two Modal Specifications
	 * @param s - State object representing the State being checked for its status as Good or Bad.
	 * @return - Returns a boolean value describing whether or not a State is good or bad.
	 */
	
	public boolean stateIsBad(ModalSpecification modal1, ModalSpecification modal2, ModalSpecification composedModal, State s) {
		ArrayList<DetTransition> mustTrans = composedModal.getMustTransitions().getTransitions(s);
		ArrayList<DetTransition> mayTrans = composedModal.getTransitions().getTransitions(s);
		ArrayList<State> compos = composedModal.getStateComposition(s);
		
		State modalState1 = compos.get(0);	//Get Transitions of the composed Modal Spec. and a State from one of its forebears
											//Need to get info on event privacy and a reference into one of the composing Modal Spec.
		EventMap shared = modal1.getEventMap().getSharedEvents(modal2.getEventMap());
		
		for(DetTransition t : mustTrans) {	//For each Must Transition, there needs to be a May as well
			if(!mayTrans.contains(t)) {		//If there isn't, can it reach via the second approach? (Traverse along invisible events)
				boolean isBad = true;		//Use reference State from original Modal Spec. to see which events are allowed.
				if(!modal1.getMustTransitions().getTransitions(modalState1).contains(t)) {
					isBad = composedModal.privateEventSearch(modal1.getEventMap(), s, shared, t.getTransitionEvent());
				}
				else {
					isBad = composedModal.privateEventSearch(modal2.getEventMap(), s, shared, t.getTransitionEvent());
				}
				if(isBad) {					//If the result was a positive, then it's bad (no alternate route), say so.
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * This helper method searches through the calling Modal Specification for a specified Event, traversing using
	 * only those Events from the provided EventMap that are not found in the second provided EventMap; the former
	 * representing Event from one of the two Modal Specifications that composed to create the calling Modal Specification,
	 * and the latter representing the Events shared between them. (Used to find unique Events.) 
	 * 
	 * @param mod - EventMap object holding Events from a Modal Specification that was used to create the calling object.
	 * @param modState - State object representing the starting State to perform a search from.
	 * @param shared - EventMap object holding the Events shared between the two Modal Specifications that formed the calling object.
	 * @param e - Event object representing the Event being searched for via an alternate route.
	 * @return - Returns a boolean denoting the result of the search; true if it did not find the event, false otherwise.
	 */
	
	public boolean privateEventSearch(EventMap mod, State modState, EventMap shared, Event e) {
		LinkedList<State> queue = new LinkedList<State>();
		HashSet<State> visited = new HashSet<State>();
		queue.add(modState);
		boolean isBad = true;
		bfs:
		while(!queue.isEmpty()) {	//Breadth-First Search approach
			State top = queue.poll();
			if(visited.contains(top)) 
				continue;
			visited.add(top);
			for(DetTransition trans : this.getMustTransitions().getTransitions(top)){	//Find matching Transitions
				if(this.getTransitions().contains(top, trans)) {						//Either add to queue or finish.
					if(!shared.contains(trans.getTransitionEvent()) && mod.contains(trans.getTransitionEvent())) {
						queue.add(trans.getTransitionState());
					}
					else if(trans.getTransitionEvent().equals(e)) {
						isBad = false;
						break bfs;
					}
				}
			}
		}
		return isBad;
	}
	
	/**
	 * This method prunes a ModalSpecification by going through all states and removing those that
	 * have a must transition without a corresponding may transition, and then removing all states
	 * with a transition going to those bad states.
	 * 
	 * Superceded by the newPrune() algorithm.
	 * 
	 * @return - Returns a pruned ModalSpecification object.
	 */

	public ModalSpecification prune() {
		// First, get all the inconsistent states
		HashSet<String> badStates = transitions.getInconsistentStates(mustTransitions);
		// Now, go through all the states and see if there are must transitions to these inconsistent
		// states. If there are, those states must be removed (iteratively).
		while(getBadMustTransitionStates(badStates));
		
		return (new ModalSpecification(this, badStates, this.id)).makeAccessible();
	}
	
	/**
	 * Gets all the states that have must transitions to a state marked as a bad state
	 * in the badStates parameter. All the states with these must transitions are then added
	 * to badStates.
	 * 
	 * @param badStates - HashSet of Strings representing the States which are bad and will
	 * be removed from the TransitionSystem.
	 * @return - Returns a boolean value representing if the method added any bad states to the set.
	 */

	private boolean getBadMustTransitionStates(HashSet<String> badStates) {
		boolean markedAState = false;
		// Go through all OK nodes, and if there is a state that leads to a badState with a
		// must transition, add the state to the badStates.
		for(State curr : states.getStates()) if(!badStates.contains(curr.getStateName())) {
			for(DetTransition transition : mustTransitions.getTransitions(curr)) {
				if(badStates.contains(transition.getTransitionState().getStateName())) {
					badStates.add(curr.getStateName());
					markedAState = true;
				}
			}
		}
		return markedAState;
	} // getBadMustTransitionStates(HashSet<String>)
	
//---  Operations for getting the optimal supervisor   ----------------------------------------
	
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
	
	public <T extends Transition> DetObsContFSM makeOptimalSupervisor(FSM<T> fsm) throws IllegalArgumentException {
		//--------------------------------------------
		// Step 1: Create the reachable part of the combo
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
		
		DetObsContFSM product = universalObserverView.product(specFSM);
		
		//--------------------------------------------
		// Step 2: Mark the bad states
		HashSet<String> badStates = new HashSet<String>();
		boolean keepGoing = true;
		
		while(keepGoing) {
			boolean keepGoing1 = markBadStates(fsm, specFSM, product, badStates);
			boolean keepGoing2 = markDeadEnds(universalObserverView, universalObserverViewMap, product, badStates);
			
			keepGoing = keepGoing1 || keepGoing2;
		} // while
		
		// Now, we have to actually create our FSM
		DetObsContFSM supervisor = new DetObsContFSM(product, badStates, fsm.id + " Supervisor");
		return (DetObsContFSM)supervisor.makeAccessible();
	}
	
	/**
	 * This marks states that are bad states in a supervisor for an FSM that is trying to satisfy a
	 * modal specification. There are two types of bad states:<br/>
	 * 1) When there is some uncontrollable and observable event where there is a possible state in
	 * the determinized collection of states where the event is possible, but there is no such possible
	 * transition in the specification; and<br/>
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
	 * @param universalObserverView - Observer view of the original FSM with states for every possible
	 * starting state (for instance, if the FSM had a state 2, then there will be a state representing
	 * the epsilon-reach of 2 in this universal view).
	 * @param universalObserverViewMap - Map between the original FSM's states and their epsilon reach state
	 * name, which is present in the universalObserverView.
	 * @param product - Product FSM between the observer view of the original fsm and the specification.
	 * @param badStates - HashSet of String state names which are bad and to be removed.
	 * @return - Returns a boolean value; true if the method marked a bad state, false otherwise.
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
				DetObsContFSM massiveProduct = (DetObsContFSM)universalObserverView.product(product);
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
	 * @return - Returns TODO:
	 */
	
	static protected boolean canReachMarked(FSM<DetTransition> fsm, State state, HashSet<String> badStates) {
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
	
//---  Operations for converting the observer view of the fsm at any given state   ------------
	
	/**
	 * Creates a universal observer view that allows you to look up possible transitions from any possible
	 * initial state. It makes the observer view such that if AT LEAST one state is marked, then the ENTIRE
	 * observer state is marked (which is more useful for trying to find if you can possibly reach a marked
	 * state from a given initial state).
	 * 
	 * @param fsm - FSM with which to create the observer view
	 * @param newFSM - FSM to fill with all the observer view states and transitions, etc.
	 * @return - Returns a HashMap mapping the name of the original states in the fsm to the name of its epsilon reach state
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
	 * @param fsm - FSM to which to add a new state.
	 * @param stateCollection - HashSet of states to combine into a single state in the FSM.
	 * @return - Returns a State object that was composed from the state collection and added to the FSM.
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
	
//---  Multi-MS Operations   ------------------------------------------------------------------
	
	/**
	 * This method gets the ModalSpecification representing the greatest lower bound between
	 * the calling and parameter ModalSpecifications.
	 * 
	 * @param other - ModalSpecification to use for calculating the greatest lower bound.
	 * @return - Returns a ModalSpecification representing the greatest lower bound between the two
	 * ModalSpecifications.
	 */
	
	public ModalSpecification getGreatestLowerBound(ModalSpecification other) {
		ModalSpecification newMS = getPseudoLowerBound(other);
		FSMToDot.createImgFromFSM(newMS, windows.MAC_WORKING_FOLDER + "mod3_1", windows.MAC_WORKING_FOLDER, windows.MAC_CONFIG_FILE_PATH);
		return newPrune(this, other, newMS);
	}
	
	/**
	 * This method gets the pseudo-modal specification representing the lower bound of
	 * the calling and parameter ModalSpecification inputs. This means:
	 * - All "may" transitions for a common event must be present in both to exist.
	 * - All "may" transitions for a private event must only be present in one to exist.
	 * - All "must" transitions for any event must be present in at least one to exist.
	 * 
	 * @param other - ModalSpecification which will be used in conjunction with the calling
	 * modal specification to create a new ModalSpecification. The result will be the lower
	 * bound of the two.
	 * @return - Returns a Pseudo modal specification representing the lower bound of the two.
	 * This result needs to be pruned to remove states where there exists a must transition but
	 * no corresponding may transition.
	 */
	
	private ModalSpecification getPseudoLowerBound(ModalSpecification other) {
		ModalSpecification newMS = new ModalSpecification(this.id + " Lower Bound");
		
		// Also, identify which states are already visited so we don't go in loops
		HashSet<String> visited = new HashSet<String>();
		
		// Start at the beginning of each MS
		LinkedList<NextStates> next = new LinkedList<NextStates>();
		if(this.initialState != null && other.initialState != null) { // If one doesn't have an initial, we have a problem.
			State newInitial = newMS.states.addState(this.initialState, other.initialState);
			newMS.initialState = newInitial;
			next.add(new NextStates(this.initialState, other.initialState, newInitial));
		}
		while(!next.isEmpty()) {
			NextStates curr = next.poll();
			
			if(visited.contains(curr.stateNew.getStateName()))
				continue; // If we already added the state, skip this iteration
			curr.addToComposition(newMS);
			visited.add(curr.stateNew.getStateName());
			
			// Go through all the MAY transitions common in both
			next.addAll(newMS.copyCommonTransitions(curr, this, other));
			next.addAll(newMS.copyPrivateTransitions(curr, this, other));
			next.addAll(newMS.copyMustTransitions(curr, this, other));
		} // while there are states in the queue
		return newMS;
	}
	
//---  Copy methods that steal from other systems   -----------------------------------------------------------------------
	
	/**
	 * This helper method copies transitions in common between two Modal Specifications which are
	 * in common. That is, a transition must exist in both thisTransitions and otherTransitions to
	 * be copied into the calling MS, with a transition starting at the State newCurr.
	 * 
	 * @param curr - NextStates object with the current node in msA, msB and the calling ModalSpecifications.
	 * @param msA - The first ModalSpecification from which to copy common transitions.
	 * @param msB - The second ModalSpecification from which to copy common transitions. 
	 * @return - Returns a LinkedList<<r>NextStates> object
	 */
	
	private LinkedList<NextStates> copyCommonTransitions(NextStates curr, ModalSpecification msA, ModalSpecification msB) {
		ArrayList<DetTransition> transitionsA = msA.transitions.getSortedTransitions(curr.stateA);
		ArrayList<DetTransition> transitionsB = msB.transitions.getSortedTransitions(curr.stateB);
		// Go through all the MAY transitions common in both
		LinkedList<NextStates> nextStates = new LinkedList<NextStates>();
		int indexA = 0, indexB = 0;
		while(indexA < transitionsA.size() && indexB < transitionsB.size()) {
			// Increment thisIndex and otherIndex
			while(indexA < transitionsA.size() && indexB < transitionsB.size() && transitionsA.get(indexA).compareTo(transitionsB.get(indexB)) < 0) indexA++;
			while(indexA < transitionsA.size() && indexB < transitionsB.size() && transitionsA.get(indexA).compareTo(transitionsB.get(indexB)) > 0) indexB++;
			// If they share the same event
			if(indexA < transitionsA.size() && indexB < transitionsB.size() && transitionsA.get(indexA).compareTo(transitionsB.get(indexB)) == 0) {
				Event e = transitionsA.get(indexA).getTransitionEvent();
				State thisTo = transitionsA.get(indexA).getTransitionState();
				State otherTo = transitionsB.get(indexB).getTransitionState();
				State newTo = this.states.addState(thisTo, otherTo);
				this.addTransition(curr.stateNew, this.events.addEvent(e), newTo);
				nextStates.add(new NextStates(thisTo, otherTo, newTo));
				indexA++;
				indexB++;
			} // if shared the event
		} // while
		return nextStates;
	} // copyCommonTransitions(NextStates, ModalSpeciication, ModalSpecification)
	
	/**
	 * This helper method copies transitions private to one of the two Modal Specifications.
	 * That is, the transition's event must exist in only one of the two Modal Specifications
	 * be copied into the calling MS, with a transition starting at the State curr.stateNew.
	 * 
	 * @param curr - NextStates object with the current node in msA, msB and the calling ModalSpecification.
	 * @param msA - The first ModalSpecification from which to copy private transitions.
	 * @param msB - The second ModalSpecification from which to copy private transitions. 
	 * @return - Returns a LinkedList<<r>NextStates> object.
	 */
	
	private LinkedList<NextStates> copyPrivateTransitions(NextStates curr, ModalSpecification msA, ModalSpecification msB) {
		// First, we need to identify which events are shared and which are private
		HashSet<String> privateEventsA = msA.events.getPrivateEvents(msB.events);
		HashSet<String> privateEventsB = msB.events.getPrivateEvents(msA.events);
		
		// Now, get the transitions
		ArrayList<DetTransition> transitionsA = msA.transitions.getSortedTransitions(curr.stateA);
		ArrayList<DetTransition> transitionsB = msB.transitions.getSortedTransitions(curr.stateB);
		
		// Now, just go through all the private events for MAY transitions and add the transitions
		LinkedList<NextStates> next = new LinkedList<NextStates>();
		for(DetTransition thisT : transitionsA) if(privateEventsA.contains(thisT.getTransitionEvent().getEventName())) {
			State thisTo = thisT.getTransitionState();
			State newTo = this.states.addState(thisTo, curr.stateB);
			this.addTransition(curr.stateNew, this.events.addEvent(thisT.getTransitionEvent()), newTo);
			next.add(new NextStates(thisTo, curr.stateB, newTo));
		} // for each thisT with a private event
		for(DetTransition otherT : transitionsB) if(privateEventsB.contains(otherT.getTransitionEvent().getEventName())) {
			State otherTo = otherT.getTransitionState();
			State newTo = this.states.addState(curr.stateA, otherTo);
			this.addTransition(curr.stateNew, this.events.addEvent(otherT.getTransitionEvent()), newTo);
			next.add(new NextStates(curr.stateA, otherTo, newTo));
		} // for
		return next;
	} // copyPrivateTransitions(NextStates, ModalSpeciication, ModalSpecification)
	
	/**
	 * This helper method copies all must transitions from either ModalSpecification msA or msB.
	 * That is, if a must transition exists in either (or both), it is copied to the calling
	 * ModalSpecification. 
	 * 
	 * @param curr - NextStates object with the current node in msA, msB and the calling ModalSpecification.
	 * @param msA - The first ModalSpecification from which to copy must transitions.
	 * @param msB - The second ModalSpecification from which to copy must transitions. 
	 * @return - Returns a LinkedList<<r>NextStates> object.
	 */
	
	private LinkedList<NextStates> copyMustTransitions(NextStates curr, ModalSpecification msA, ModalSpecification msB) {
		LinkedList<NextStates> next = new LinkedList<NextStates>();
		
		// Go through all the MUST transitions in either
		ArrayList<DetTransition> transitionsA = msA.mustTransitions.getSortedTransitions(curr.stateA);
		ArrayList<DetTransition> transitionsB = msB.mustTransitions.getSortedTransitions(curr.stateB);
		
		// We are going through by incrementing indices until one ModalSpecification runs out of transitions
		int msAIndex = 0, msBIndex = 0;
		while(msAIndex < transitionsA.size() && msBIndex < transitionsB.size()) {
			int compareResult = transitionsA.get(msAIndex).compareTo(transitionsB.get(msBIndex));
			if(compareResult < 0) {
				Event e = transitionsA.get(msAIndex).getTransitionEvent();
				State aTo = transitionsA.get(msAIndex).getTransitionState();
				
				// Get where the other modal specification goes with the event
				ArrayList<State> bToPossible = msB.transitions.getTransitionStates(curr.stateB, msB.events.getEvent(e));
				State bTo = (bToPossible != null && bToPossible.size() > 0) ? bToPossible.get(0) : curr.stateB;
				
				State newTo = this.states.addState(aTo, bTo);
				this.mustTransitions.addTransitionState(curr.stateNew, this.events.addEvent(e), newTo);
				next.add(new NextStates(aTo, bTo, newTo));
				msAIndex++;
			} 
			else if(compareResult > 0) {
				Event e = transitionsB.get(msBIndex).getTransitionEvent();
				State bTo = transitionsB.get(msBIndex).getTransitionState();
				
				// Get where the other modal specification goes with the event
				ArrayList<State> aToPossible = msA.transitions.getTransitionStates(curr.stateA, msA.events.getEvent(e));
				State aTo = (aToPossible != null && aToPossible.size() > 0) ? aToPossible.get(0) : curr.stateA;
				
				State newTo = this.states.addState(aTo, bTo);
				this.mustTransitions.addTransitionState(curr.stateNew, this.events.addEvent(e), newTo);
				next.add(new NextStates(aTo, bTo, newTo));
				msBIndex++;
			} 
			else {
				Event e = transitionsA.get(msAIndex).getTransitionEvent();
				State thisTo = transitionsA.get(msAIndex).getTransitionState();
				State otherTo = transitionsB.get(msBIndex).getTransitionState();
				State newTo = this.states.addState(thisTo, otherTo);
				this.mustTransitions.addTransitionState(curr.stateNew, this.events.addEvent(e), newTo);
				next.add(new NextStates(thisTo, otherTo, newTo));
				msAIndex++;
				msBIndex++;
			} // if shared the event
		} // while
		// Now, increment the indices of whichever MS did not reach the end.
		while(msAIndex < transitionsA.size()) {
			Event e = transitionsA.get(msAIndex).getTransitionEvent();
			State aTo = transitionsA.get(msAIndex).getTransitionState();
			
			// Get where the other modal specification goes with the event
			ArrayList<State> bToPossible = msB.transitions.getTransitionStates(curr.stateB, msB.events.getEvent(e));
			State bTo = (bToPossible != null && bToPossible.size() > 0) ? bToPossible.get(0) : curr.stateB;
			
			State newTo = this.states.addState(aTo, bTo);
			this.mustTransitions.addTransitionState(curr.stateNew, this.events.addEvent(e), newTo);
			next.add(new NextStates(aTo, bTo, newTo));
			msAIndex++;
		}
		while(msBIndex < transitionsB.size()) {
			Event e = transitionsB.get(msBIndex).getTransitionEvent();
			State bTo = transitionsB.get(msBIndex).getTransitionState();
			
			// Get where the other modal specification goes with the event
			ArrayList<State> aToPossible = msA.transitions.getTransitionStates(curr.stateA, msA.events.getEvent(e));
			State aTo = (aToPossible != null && aToPossible.size() > 0) ? aToPossible.get(0) : curr.stateA;
			
			State newTo = this.states.addState(aTo, bTo);
			this.mustTransitions.addTransitionState(curr.stateNew, this.events.addEvent(e), newTo);
			next.add(new NextStates(aTo, bTo, newTo));
			msBIndex++;
		}
		
		return next;
	} // copyMustTransitions(NextStates, ModalSpeciication, ModalSpecification)
	
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
					// Add the state the transition leads to
					this.addMustTransition(s.getStateName(), t.getTransitionEvent().getEventName(), t.getTransitionState().getStateName());
				} // for every transition
		} // for every state
	} // copyMustTransitions(ModalSpecification)
	
	/**
	 * Copies the must transitions of another ModalSpecification into the current ModalSpecification,
	 * excluding the transitions in the HashSet of bad states.
	 * 
	 * @param other - ModalSpecification object whose transitions are to be copied.
	 */
	
	public void copyMustTransitions(ModalSpecification other, HashSet<String> badStates) {
		for(State s : other.states.getStates()) if(!badStates.contains(s.getStateName())) {
			ArrayList<DetTransition> thisTransitions = other.mustTransitions.getTransitions(s);
			if(thisTransitions != null) for(DetTransition t : thisTransitions) {
				// Add every state the transition leads to
				String toStateName = t.getTransitionState().getStateName();
				if(!badStates.contains(toStateName))
					this.addMustTransition(s.getStateName(), t.getTransitionEvent().getEventName(), toStateName);
			} // for every transition
		} // for every state
	} // copyMustTransitions(ModalSpecification)
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	/**
	 * Getter method that returns the Transition Function object containing the Must Transitions
	 * associated to the calling Modal Specification object.
	 * 
	 * @return - Returns a TransitionFunction<<r>DetTransition> object containing the Must Transitions associated to this ModalSpecificaiton object.
	 */
	
	public TransitionFunction<DetTransition> getMustTransitions() {
		return mustTransitions;
	}
	
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

	@Override
	public DisabledEvents getDisabledEvents(State curr, FSM otherFSM, HashSet visitedStates, HashMap disabledMap) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean getEventControllability(String eventName) {
		Event curr = events.getEvent(eventName);
		if(curr != null)
			return curr.getEventControllability();
		return null;
	}
	
	@Override
	public Boolean getEventObservability(String eventName) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Boolean getEventAttackerObservability(String eventName) {
		Event curr = events.getEvent(eventName);
		if(curr != null)
			return curr.getEventAttackerObservability();
		return null;
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	@Override
	public void setEventControllability(String eventName, boolean value) {
		Event curr = events.getEvent(eventName);
		if(curr != null)
			curr.setEventControllability(value);
	}
	
	@Override
	public boolean setEventObservability(String eventName, boolean status) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean setEventAttackerObservability(String eventName, boolean status) {
		Event curr = events.getEvent(eventName);
		if(curr != null)
			curr.setEventAttackerObservability(status);
		else
			return false;
		return true;
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
	
	/**
	 * This method handles the adding of a new must transition to the calling ModalSpecification by
	 * passing in a state and transition from another transition system. It copies the pieces into
	 * the current ModalSpecification.
	 * 
	 * @param state - State extending object representing the State acquiring a new Transition.
	 * @param transition - Transition extending object representing the Transition being added to the provided State extending object.
	 */
	
	public void addMustTransition(State state, DetTransition transition) {
		State fromState = states.addState(state); // Get the state or make it
		Event e = events.addEvent(transition.getTransitionEvent()); // Get the event or make it
		DetTransition outbound = transitions.getEmptyTransition(); // New transition object
		outbound.setTransitionEvent(e);
		outbound.setTransitionState(states.addState(transition.getTransitionState()));
		mustTransitions.addTransition(fromState, outbound);
	}

	@Override
	public void addInitialState(String newInitial) {
		if(initialState != null) initialState.setStateInitial(false);
		State theState = states.addState(newInitial);
		theState.setStateInitial(true);
		initialState = theState;
	}

	@Override
	public void addInitialState(State newState) {
		if(initialState != null) initialState.setStateInitial(false);
		State theState = states.addState(newState);
		theState.setStateInitial(true);
		initialState = theState;
	}

	@Override
	public boolean removeInitialState(String stateName) {
		State theState = states.getState(stateName);
		if(theState != null && theState.getStateInitial()) {
			theState.setStateInitial(false);
			initialState = null;
			return true;
		}
		return false;
	}
	
	@Override
	public boolean removeTransition(String state1, String eventName, String state2) {
		mustTransitions.removeTransition(getState(state1), getEvent(eventName), getState(state2));
		return(super.removeTransition(state1, eventName, state2));
	}

//---  Miscellaneous   ------------------------------------------------------------------------
	
	/**
	 * This method checks whether or not the Modal Specification contains the denoted
	 * State and Transition, calling a method in its TransitionFunction to query for
	 * their existence.
	 * 
	 * @param s - State object that would potentially possess the Transition t.
	 * @param t - DetTransition object to search for in the TransitionFunction of the calling Modal Specification.
	 * @return - Returns a boolean value denoting the result of the search; true if it exists, false otherwise.
	 */
	
	public boolean contains(State s, DetTransition t) {
		return getTransitions().contains(s, t);
	}
	
//---  Support Classes   ----------------------------------------------------------------------
	
	/**
	 * This class models an object containing three states: the state from some transition system A,
	 * the state from some transition system B, and the state from some new transition system which
	 * is being created. This simply makes cleaner code in other areas.
	 * 
	 * This class is a part of the fsm package.
	 * 
	 * @author Mac Clevinger and Graeme Zinck
	 */

	class NextStates {
		
		/** State objects representing composite States and their joined product.*/
		State stateA, stateB, stateNew;
		
		/**
		 * Constructor for a NextStates object.
		 * 
		 * @param stateFromA - State object representing the State from the first Modal Specification being joined.
		 * @param stateFromB - State object representing the State from the second Modal Specification being joined.
		 * @param stateFromNew - State object representing the produced State from joining two States.
		 */
		
		public NextStates(State stateFromA, State stateFromB, State stateFromNew) {
			stateA = stateFromA;
			stateB = stateFromB;
			stateNew = stateFromNew;
			if(stateFromA.getStatePrivate() || stateFromB.getStatePrivate())
				stateFromNew.setStatePrivate(true);
			else
				stateFromNew.setStatePrivate(false);
			}		
		
		/**
		 * Helper method to assist in building the library of States and their compositions; i.e, what
		 * States were adjoined to create a new State.
		 * 
		 * @param ms - ModalSpecification object into which the new pairing of States will be added.
		 */
		
		public void addToComposition(ModalSpecification ms) {
			ms.setStateComposition(stateNew, stateA, stateB);
			}
		
		}
	
		

}
