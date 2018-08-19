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
import support.Event;
import support.State;
import java.io.File;
import java.util.*;

/**
 * This class models a Finite State Machine of the NonDeterministic variety, extending the
 * Abstract Class FSM for generic FSM characteristics and implementing a variety of interfaces
 * that denote more advanced features, such as Observable and Controllable Events.
 * 
 * This class is a part of the fsm package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public class NonDetObsContFSM extends FSM<NonDetTransition> implements NonDeterministic<NonDetTransition> {
	
//--- Constant Values  -------------------------------------------------------------------------

	/** String constant designating this object as a specific type of FSM for clarification purposes*/
	public static final String FSM_TYPE = "NonDeterministic FSM with Observability and Controllability";
			
//--- Instance Variables  ----------------------------------------------------------------------
			
	/** ArrayList<<r>State> object that holds a list of Initial States for this Non Deterministic FSM object. */
	protected ArrayList<State> initialStates;
	
//--- Constructors  ----------------------------------------------------------------------
	
	/**
	 * Constructor for an NonDetObsContFSM object that takes in a file encoding the contents of the FSM.
	 * 
	 * NonDetObsContFSM File Order for Special: Initial, Marked.
	 * 
	 * @param in - File object provided in order to create the FSM.
	 * @param id - String object representing the id for the FSM (can be any String).
	 */
	
	public NonDetObsContFSM(File in, String inId) {
		id = inId;									//Assign id
		states = new StateMap();	//Initialize the storage for States, Event, and Transitions
		events = new EventMap();	//51: Create a ReadWrite object for file reading/writing (reading in this case), denote generics
		transitions = new TransitionFunction<NonDetTransition>(new NonDetTransition());
		initialStates = new ArrayList<State>();
		
		ReadWrite<NonDetTransition> redWrt = new ReadWrite<NonDetTransition>();
		ArrayList<ArrayList<String>> special = redWrt.readFromFile(states, events, transitions, in);
		
		for(int i = 0; i < special.get(0).size(); i++) {	//Special ArrayList 0-entry is InitialState
			if(states.getState(special.get(0).get(i)) == null)
				states.addState(new State(special.get(0).get(i)));
			states.getState(special.get(0).get(i)).setStateInitial(true);
			initialStates.add(states.addState(special.get(0).get(i)));
		}
		for(int i = 0; i < special.get(1).size(); i++) {			//Special ArrayList 1-entry is MarkedState
			if(states.getState(special.get(1).get(i)) == null)
				states.addState(new State(special.get(1).get(i)));
			states.getState(special.get(1).get(i)).setStateMarked(true);
		}
		for(int i = 0; i < special.get(2).size(); i++) {			//Special ArrayList 2-entry is Private State
			if(states.getState(special.get(2).get(i)) == null)
				states.addState(new State(special.get(2).get(i)));
			states.getState(special.get(2).get(i)).setStatePrivate(true);
		}
		for(int i = 0; i < special.get(3).size(); i++) {			//Special ArrayList 3-entry is ObservableEvent
			if(events.getEvent(special.get(3).get(i)) == null)
				events.addEvent(special.get(3).get(i));
			events.getEvent(special.get(3).get(i)).setEventObservability(false);
		}
		for(int i = 0; i < special.get(4).size(); i++) {			//Special ArrayList 4-entry is ObservableEvent
			if(events.getEvent(special.get(4).get(i)) == null)
				events.addEvent(special.get(4).get(i));
			events.getEvent(special.get(4).get(i)).setEventAttackerObservability(false);
		}
		for(int i = 0; i < special.get(5).size(); i++) {			//Special ArrayList 5-entry is Controllable Event
			if(events.getEvent(special.get(5).get(i)) == null)
				events.addEvent(special.get(5).get(i));
			events.getEvent(special.get(5).get(i)).setEventControllability(false);
		}
	}
	
	/**
	 * Constructor for a NonDetObsContFSM that takes any FSM as a parameter and creates a new
	 * NonDetObsContFSM using that as the basis. Any information which is not permissible in a
	 * NonDetObsContFSM is thrown away, because it does not have any means to handle it.
	 * 
	 * @param other - TransitionSysem object to copy as a NonDetObsContFSM (can be any kind of TS).
	 * @param inId - String object representing the Id for the new FSM to carry.
	 */
	
	public NonDetObsContFSM(TransitionSystem<?> other, String inId) {
		id = inId;
		states = new StateMap();
		events = new EventMap();
		transitions = new TransitionFunction<NonDetTransition>(new NonDetTransition());
		
		// Add in all the states
		for(State s : other.states.getStates())
			this.states.addState(s);
		// Add in all the events
		for(Event e : other.events.getEvents())
			this.events.addEvent(e);
		// Add in all the transitions
		for(State s : other.states.getStates()) {
			for(Transition t : other.transitions.getTransitions(s)) {
				// Add every state the transition leads to
				for(State toState : t.getTransitionStates())
					this.addTransition(s.getStateName(), t.getTransitionEvent().getEventName(), toState.getStateName());
			} // for every transition
		} // for every state
		// Add in the initial states
		initialStates = new ArrayList<State>();
		for(State s: other.getInitialStates())
			initialStates.add(this.getState(s));
	} // NonDetFSM(FSM, String)
	
	/**
	 * Constructor for an FSM object that contains no transitions or states, allowing the
	 * user to add those elements themselves.
	 * 
	 * @param - String object representing the new id for this NonDetObsContFSM object
	 */
	
	public NonDetObsContFSM(String inId) {
		id = inId;
		events = new EventMap();
		states = new StateMap();
		transitions = new TransitionFunction<NonDetTransition>(new NonDetTransition());
		initialStates = new ArrayList<State>();
	} // DetFSM()
	
	/**
	 * Constructor for an FSM object that contains no transitions or states, allowing the
	 * user to add those elements themselves. It has no id, either.
	 */
	
	public NonDetObsContFSM() {
		id = "";
		events = new EventMap();
		states = new StateMap();
		transitions = new TransitionFunction<NonDetTransition>(new NonDetTransition());
		initialStates = new ArrayList<State>();
	} // NonDetObsContFSM()

//---  Single-FSM Operations   ----------------------------------------------------------------
	
	@Override
	public DetObsContFSM buildObserver() {
		DetObsContFSM newFSM = new DetObsContFSM();
		
		HashMap<State, State> map = new HashMap<State, State>();
		
		for(State s : getStates()) {
			
			HashSet<State> reach = new HashSet<State>();
			LinkedList<State> queue = new LinkedList<State>();
			queue.add(s);
			
			while(!queue.isEmpty()) {
				State top = queue.poll();
				if(reach.contains(top))
					continue;
				reach.add(top);
			    for(NonDetTransition t : getTransitions().getTransitions(top)) {
				   if(!t.getTransitionEvent().getEventObservability()) {
					  queue.addAll(t.getTransitionStates());
			 	  }
			  }
			}
			ArrayList<State> composite = new ArrayList<State>(reach);
			Collections.sort(composite);
			State made = new State(composite.toArray(new State[composite.size()]));
			newFSM.setStateComposition(made, composite.toArray(new State[composite.size()]));
			map.put(s, made);
		}
		
		LinkedList<State> queue = new LinkedList<State>();
		HashSet<String> visited = new HashSet<String>();
		
		HashSet<State> initialStates = new HashSet<State>();
		for(State s : getInitialStates())
			initialStates.addAll(newFSM.getStateComposition(map.get(s)));
		State init = newFSM.addState(initialStates.toArray(new State[initialStates.size()]));
		newFSM.setStateComposition(init,  initialStates.toArray(new State[initialStates.size()]));
		queue.addFirst(init);
		newFSM.addInitialState(init);
		newFSM.addState(init);
		
		while(!queue.isEmpty()) {
			State top = queue.poll();
			if(visited.contains(top.getStateName()))
				continue;
			visited.add(top.getStateName());
			HashMap<Event, HashSet<State>> tran = new HashMap<Event, HashSet<State>>();
			for(State s : newFSM.getStateComposition(top)) {
				for(NonDetTransition t : getTransitions().getTransitions(s)) {
					if(t.getTransitionEvent().getEventObservability()) {
						if(tran.get(t.getTransitionEvent()) == null) {
							tran.put(t.getTransitionEvent(), new HashSet<State>());
						}
						for(State led : t.getTransitionStates())
							tran.get(t.getTransitionEvent()).addAll(newFSM.getStateComposition(map.get(led)));
					}
				}
			}
			for(Event e : tran.keySet()) {
				State bot = newFSM.addState(tran.get(e).toArray(new State[tran.get(e).size()]));
				newFSM.setStateComposition(bot, tran.get(e).toArray(new State[tran.get(e).size()]));
				queue.add(bot);
				newFSM.addTransition(top, e, bot);
				newFSM.addState(top);
				newFSM.addState(bot);
			}
		}
		return newFSM;
	}
	
	@Override
	public void toTextFile(String filePath, String name) {
		if(name == null)
			name = id;
		String truePath = "";
		truePath = filePath + (filePath.charAt(filePath.length()-1) == '/' ? "" : "/") + name;
		String special = "6\n";
		ArrayList<String> init = new ArrayList<String>();
		ArrayList<String> mark = new ArrayList<String>();
		ArrayList<String> priv = new ArrayList<String>();
		ArrayList<String> unob = new ArrayList<String>();
		ArrayList<String> atta = new ArrayList<String>();
		ArrayList<String> cont = new ArrayList<String>();
		for(State s : this.getStates()) {
			if(s.getStateMarked()) 
				mark.add(s.getStateName());
			if(s.getStateInitial()) 
				init.add(s.getStateName());
			if(s.getStatePrivate())
				priv.add(s.getStateName());
		}
		for(Event e : this.getEvents()) {
			if(!e.getEventObservability())
				unob.add(e.getEventName());
			if(!e.getEventControllability())
				cont.add(e.getEventName());
			if(!e.getEventAttackerObservability())
				atta.add(e.getEventName());
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
		ReadWrite<NonDetTransition> rdWrt = new ReadWrite<NonDetTransition>();
		rdWrt.writeToFile(truePath,  special, this.getTransitions(), FSM_EXTENSION);
	}
	
	@Override
	public <T1 extends Transition> DisabledEvents getDisabledEvents(State curr, FSM<T1> otherFSM, HashSet<String> visitedStates, HashMap<String, DisabledEvents> disabledMap) {
		String currName = curr.getStateName();
		State otherCurr = otherFSM.getState(currName);
		
		// If we already have an answer for the state, return it
		if(disabledMap.containsKey(currName))
			return disabledMap.get(currName);
		// If we already visited the state, return null
		if(visitedStates.contains(currName))
			return null;
		
		visitedStates.add(currName); // Mark the state
		// If we need to disable the state...
		if(otherCurr == null) {
			DisabledEvents de = new DisabledEvents(true);
			disabledMap.put(currName, de);
			return de; // Then we need to disable the state
		}
		
		// Otherwise, go through the neighbours and identify which events we need to disable.
		DisabledEvents currDE = new DisabledEvents(false);
		ArrayList<NonDetTransition> thisTransitions = transitions.getTransitions(curr);
		if(thisTransitions != null)
		for(NonDetTransition t : thisTransitions) {
			DisabledEvents tempDE = new DisabledEvents(false);
			boolean transitionEventDisabled = false;
			
			loopThroughTransitionStates:
			for(State s : (ArrayList<State>)(t.getTransitionStates())) {
				DisabledEvents nextDE = getDisabledEvents(s, otherFSM, visitedStates, disabledMap);
				Event e = t.getTransitionEvent();
				
				// If the event is not present in the specification, then break
				if(!otherFSM.transitions.eventExists(otherFSM.getState(curr), e)) {
					currDE.disableEvent(e.getEventName());
					transitionEventDisabled = true;
					break loopThroughTransitionStates;
				} // if event not present in spec
				
				if(nextDE != null) { // As long as we're not backtracking...
					// If the transition state is bad, must disable the event.
					if(nextDE.stateIsDisabled()) {
						// If the event is uncontrollable, disable this entire state
						if(e instanceof EventControllability && !((EventControllability)e).getEventControllability()) {
							currDE.disableState();
							disabledMap.put(currName, currDE);
							return currDE;
						} // if uncontrollable event
						// Else, disable the transition event (but none of the other events)
						currDE.disableEvent(e.getEventName());
						transitionEventDisabled = true;
						break loopThroughTransitionStates;
					} // if state is disabled
					
					// If the state is good, but has disabled events, AND the transition event to the toState is unobservable
					// remove the disabled events from this state.
					if(!nextDE.allEventsEnabled() && e instanceof EventObservability && !((EventObservability)e).getEventObservability()) {
						tempDE.disableEvents(nextDE);
					} // if there are disabled events and the transition's event is unobservable
				} // if the disabled events for the next state is NOT null
			} // for each destination state
			
			// As long as the event hasn't been disabled (in which case, the event was already marked as disabled),
			// add the temporary disabled events in for the current state.
			if(!transitionEventDisabled)
				currDE.disableEvents(tempDE);
		} // for each transition
		return currDE;
	} // getDisabledEvents
	
	@Override
	public ArrayList<State> testCurrentStateOpacity(){
		ArrayList<State> secrets = new ArrayList<State>();
		for(State s : this.getStates()) {
			if(s.getStatePrivate())
				secrets.add(s);
		}
		return secrets;
	}
	
//---  Multi-FSM Operations   -----------------------------------------------------------------

	@Override
	public NonDetObsContFSM product(FSM<?> ... other) {
		NonDetObsContFSM newFSM = new NonDetObsContFSM();
		this.productHelper(other[0], newFSM);
		for(int i = 1; i < other.length; i++) {
			NonDetObsContFSM newerFSM = new NonDetObsContFSM();
			newFSM.productHelper(other[i], newerFSM);
			newFSM = newerFSM;
		}
		return newFSM;
	}
	
	@Override
	public NonDetObsContFSM parallelComposition(FSM<?> ... other){
		NonDetObsContFSM newFSM = this;
		for(int i = 0; i < other.length; i++) {
			NonDetObsContFSM newerFSM = new NonDetObsContFSM();
			newFSM.parallelCompositionHelper(other[i], newerFSM);
			newFSM = newerFSM;
		}
		return newFSM;
	}

	@Override
	public <T1 extends Transition> NonDetObsContFSM getSupremalControllableSublanguage(FSM<T1> other) {
		// Store what events are disabled in the map.
		HashMap<String, DisabledEvents> disabledMap = new HashMap<String, DisabledEvents>();
		// Parse the graph and identify disabled states and disabled events
		for(State s : states.getStates()) {
			HashSet<String> visitedStates = new HashSet<String>();
			disabledMap.put(s.getStateName(), getDisabledEvents(s, other, visitedStates, disabledMap));
		} // for every state
		
		// Now, build the FSM that we will return
		NonDetObsContFSM newFSM = new NonDetObsContFSM(this.id + " supremal controllable sublanguage");
		newFSM.copyStates(this);
		newFSM.copyEvents(this);
		ArrayList<State> statesToRemove = new ArrayList<State>();
		// Identify and copy the transitions which are legal at each state
		for(State s : newFSM.getStates()) {
			DisabledEvents disabled = disabledMap.get(s.getStateName());
			if(disabled.stateIsDisabled()) {
				statesToRemove.add(s);
			} else {
				ArrayList<NonDetTransition> allowedTransitions = new ArrayList<NonDetTransition>();
				ArrayList<NonDetTransition> transitions = this.transitions.getTransitions(getState(s));
				if(transitions != null) {
					for(NonDetTransition t : transitions) {
						Event e = t.getTransitionEvent();
						if(disabled.eventIsEnabled(e.getEventName())) {
							// Create a list of the states the event leads to
							ArrayList<State> toStates = new ArrayList<State>();
							for(State toS : (ArrayList<State>)t.getTransitionStates()) {
								if(!disabledMap.get(toS.getStateName()).stateIsDisabled())
									toStates.add(getState(toS.getStateName()));
							} // for every toState
							if(toStates.size() > 0)
								allowedTransitions.add(new NonDetTransition(e, toStates));
						} // if the event is enabled
					} // for every transition
				} // if there are any transitions
				if(allowedTransitions.size() > 0) {
					newFSM.addStateTransitions(s, allowedTransitions);
				} // if there are allowed transitions
			} // if disabled state/else
		} // for every state
//		System.out.println(disabledMap.toString());
		newFSM.states.removeStates(statesToRemove);
		return newFSM;
	} // getSupremalControllableSublanguage(FSM)
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	@Override
	public ArrayList<State> getInitialStates() {
		return initialStates;
	}

	@Override
	public boolean hasInitialState(String stateName) {
		return initialStates.contains(getState(stateName));
	}
	
	@Override
	public Boolean getEventObservability(String eventName) {
		Event curr = events.getEvent(eventName);
		if(curr != null)
			return curr.getEventObservability();
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
	public Boolean getEventAttackerObservability(String eventName) {
		Event curr = events.getEvent(eventName);
		if(curr != null)
			return curr.getEventAttackerObservability();
		return null;
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	@Override
	public boolean setEventObservability(String eventName, boolean status) {
		Event curr = events.getEvent(eventName);
		if(curr != null) {
			curr.setEventObservability(status);
			return true;
		}
		return false;
	}
	
	@Override
	public void setEventControllability(String eventName, boolean value) {
		Event curr = events.getEvent(eventName);
		if(curr != null)
			curr.setEventControllability(value);
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
	
//---  Manipulations   ------------------------------------------------------------------------
	
	@Override
	public void addInitialState(String newInitial) {
		State curr = states.addState(newInitial);
		curr.setStateInitial(true);
		// Look and see if it's already an initial state
		if(!initialStates.contains(curr))
			initialStates.add(curr);
	}

	@Override
	public void addInitialState(State newState) {
		State curr = states.addState(newState);
		curr.setStateInitial(true);
		initialStates.add(curr);
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
