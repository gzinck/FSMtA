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
 * This class models a Finite State Machine of the Deterministic variety, extending the
 * Abstract Class FSM for generic FSM characteristics and implementing a variety of interfaces
 * that denote more advanced features, such as Observable and Controllable Events.
 * 
 * This class is a part of the fsm package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public class DetObsContFSM extends FSM<DetTransition> implements Deterministic<DetTransition>{
	
//--- Constant Values  -------------------------------------------------------------------------

	/** String object constant designating this object as a specific type of FSM for clarification purposes*/
	public static final String FSM_TYPE = "Deterministic FSM with Observability and Controllability";
			
//--- Instance Variables  ----------------------------------------------------------------------
			
	/** State object that holds the initial state for this Non Deterministic FSM object. */
	protected State initialState;
	
//--- Constructors  ----------------------------------------------------------------------
	
	/**
	 * Constructor for a DetObsContFSM object that takes in a file encoding the contents of the FSM.
	 * 
	 * DetObsContFSM File Order for Special: Initial, Marked, Private, ObservableEvent, ControllableEvent.
	 * 
	 * @param in - File object to read in order to create the FSM.
	 * @param id - String object representing the id for the FSM.
	 */
	
	public DetObsContFSM(File in, String inId) {
		id = inId;									//Assign id
		states = new StateMap();	//Initialize the storage for States, Event, and Transitions
		events = new EventMap();	//51: Create a ReadWrite object for file reading/writing (reading in this case), denote generics
		transitions = new TransitionFunction<DetTransition>(new DetTransition());
		
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
	 * Constructor for a DetObsContFSM that takes any FSM as a parameter and creates a new
	 * DetFSM using that as the basis. Any information which is not permissible in a
	 * DetObsContFSM is thrown away, because it does not have any means to handle it.
	 * 
	 * @param other - TransitionSystem object to copy as a DetObsContFSM (can be any kind of TS).
	 * @param inId - String object representing the id for the new FSM.
	 */
	
	public DetObsContFSM(TransitionSystem<?> other, String inId) {
		id = inId;
		states = new StateMap();
		events = new EventMap();
		transitions = new TransitionFunction<DetTransition>(new DetTransition());
		
		// Add in all the states
		for(State s : other.states.getStates())
			this.states.addState(s).setStateInitial(false);
		// Add in all the events
		for(Event e : other.events.getEvents())
			this.events.addEvent(e);
		// Add in all the transitions (but only take the first state it transitions to)
		for(State s : other.states.getStates()) {
			for(Transition t : other.transitions.getTransitions(s)) {
				ArrayList<State> toStates = t.getTransitionStates();
				this.addTransition(s.getStateName(), t.getTransitionEvent().getEventName(), toStates.get(0).getStateName());
			} // for every transition
		} // for every state
		// Add in the initial state
		ArrayList<State> initial = other.getInitialStates();
		initialState = this.getState(initial.get(0));
		initialState.setStateInitial(true);
	} // DetFSM(FSM, String)
	
	/**
	 * Constructor for a DetObsContFSM that takes any FSM as a parameter and creates a new
	 * DetFSM using that as the basis. Any information which is not permissible in a
	 * DetObsContFSM is thrown away, because it does not have any means to handle it.
	 * 
	 * @param other - FSM object to copy as a DetObsContFSM (can be any kind of FSM).
	 * @param inId - String object representing the id for the new FSM.
	 */
	
	public DetObsContFSM(FSM<?> other, HashSet<String> badStates, String inId) {
		id = inId;
		states = new StateMap();
		events = new EventMap();
		transitions = new TransitionFunction<DetTransition>(new DetTransition());
		
		// If the initial state is bad, then we don't do anything
		if(!badStates.contains(other.getInitialStates().get(0).getStateName())) {
			// Add in all the states NOT in the badStates set
			for(State s : other.states.getStates())
				if(!badStates.contains(s.getStateName())) this.states.addState(s).setStateInitial(false);
			// Add in all the events
			for(Event e : other.events.getEvents())
				this.events.addEvent(e);
			// Add in all the transitions (but only take the first state it transitions to) IF NOT in badStates set
			for(State s : other.states.getStates()) if(!badStates.contains(s.getStateName())) {
				for(Transition t : other.transitions.getTransitions(s)) {
					String toStateName = t.getTransitionStates().get(0).getStateName();
					if(!badStates.contains(toStateName))
						this.addTransition(s.getStateName(), t.getTransitionEvent().getEventName(), toStateName);
				} // for every transition
			} // for every state
			// Add in the initial state
			ArrayList<State> initial = other.getInitialStates();
			initialState = this.getState(initial.get(0));
			if(initialState != null)
				initialState.setStateInitial(true);
		}
	} // DetFSM(FSM, String)
	
	/**
	 * Constructor for an FSM object that contains no transitions or states, allowing the
	 * user to add those elements themselves.
	 * 
	 * @param inId - String object representing the id for this FSM object.
	 */
	
	public DetObsContFSM(String inId) {
		id = inId;
		events = new EventMap();
		states = new StateMap();
		transitions = new TransitionFunction<DetTransition>(new DetTransition());
		initialState = null;
	} // DetFSM()
	
	/**
	 * Constructor for an FSM object that contains no transitions or states, allowing the
	 * user to add those elements themselves, requesting no id for the FSM.
	 */
	
	public DetObsContFSM() {
		id = "";
		events = new EventMap();
		states = new StateMap();
		transitions = new TransitionFunction<DetTransition>(new DetTransition());
		initialState = null;
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
			    for(DetTransition t : getTransitions().getTransitions(top)) {
				   if(!t.getTransitionEvent().getEventObservability()) {
					  queue.add(t.getTransitionState());
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
		
		queue.add(map.get(getInitialState()));
		newFSM.addState(map.get(getInitialState()));
		
		while(!queue.isEmpty()) {
			State top = queue.poll();
			if(visited.contains(top.getStateName()))
				continue;
			visited.add(top.getStateName());
			HashMap<Event, HashSet<State>> tran = new HashMap<Event, HashSet<State>>();
			for(State s : newFSM.getStateComposition(top)) {
				for(DetTransition t : getTransitions().getTransitions(s)) {
					if(t.getTransitionEvent().getEventObservability()) {
						if(tran.get(t.getTransitionEvent()) == null) {
							tran.put(t.getTransitionEvent(), new HashSet<State>());
						}
						tran.get(t.getTransitionEvent()).addAll(newFSM.getStateComposition(map.get(t.getTransitionState())));
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
		newFSM.addInitialState(map.get(getInitialState()));
		return newFSM;
		
	}
	
	@Override
	public void toTextFile(String filePath, String name) {
		//Initial, Marked, Private, ObservableEvent, ControllableEvent.
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
		ReadWrite<DetTransition> rdWrt = new ReadWrite<DetTransition>();
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
		ArrayList<DetTransition> thisTransitions = transitions.getTransitions(curr);
		if(thisTransitions != null)
		for(DetTransition t : thisTransitions) {
			
			DisabledEvents nextDE = getDisabledEvents(t.getTransitionState(), otherFSM, visitedStates, disabledMap);
			Event e = t.getTransitionEvent();
			
			// If the event is not present in the specification, then break
			if(!otherFSM.transitions.eventExists(otherFSM.getState(curr), e)) {
//				System.out.println("Event did not exist in other: " + e.getEventName() + " for state " + curr.getStateName());
				currDE.disableEvent(e.getEventName());
			} // if event not present in spec
			else {
//				System.out.println("Event DID exist in other: " + e.getEventName() + " for state " + curr.getStateName());
			}
			
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
				} // if state is disabled
				
				// If the state is good, but has disabled events, AND the transition event to the toState is unobservable
				// remove the disabled events from this state.
				if(!nextDE.allEventsEnabled() && e instanceof EventObservability && !((EventObservability)e).getEventObservability()) {
					currDE.disableEvents(nextDE);
				} // if there are disabled events and the transition's event is unobservable
			} // if the disabled events for the next state is NOT null
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

	//TODO: Figure out product Det x NonDet, produces multiple initial States but only accesses one due to Det nature.
	
	@Override
	public DetObsContFSM product(FSM<?> ... other) {
		DetObsContFSM newFSM = new DetObsContFSM();
		this.productHelper(other[0], newFSM);
		for(int i = 1; i < other.length; i++) {
			DetObsContFSM newerFSM = new DetObsContFSM();
			newFSM.productHelper(other[i], newerFSM);
			newFSM = newerFSM;
		}
		return newFSM;
	}

	@Override
	public DetObsContFSM parallelComposition(FSM<?> ... other){
		DetObsContFSM newFSM = this;
		for(int i = 0; i < other.length; i++) {
			DetObsContFSM newerFSM = new DetObsContFSM();
			newFSM.parallelCompositionHelper(other[i], newerFSM);
			newFSM = newerFSM;
		}
		return newFSM;
	}

	@Override
	public <T1 extends Transition> DetObsContFSM getSupremalControllableSublanguage(FSM<T1> other) {
		// Store what events are disabled in the map.
		HashMap<String, DisabledEvents> disabledMap = new HashMap<String, DisabledEvents>();
		// Parse the graph and identify disabled states and disabled events
		for(State s : states.getStates()) {
			HashSet<String> visitedStates = new HashSet<String>();
			disabledMap.put(s.getStateName(), getDisabledEvents(s, other, visitedStates, disabledMap));
		} // for every state
		System.out.println(disabledMap.toString());
		
		// Now, build the FSM that we will return
		DetObsContFSM newFSM = new DetObsContFSM(this.id + " supremal controllable sublanguage");
		newFSM.copyStates(this);
		newFSM.copyEvents(this);
		ArrayList<State> statesToRemove = new ArrayList<State>();
		// Identify and copy the transitions which are legal at each state
		for(State s : newFSM.getStates()) {
			DisabledEvents disabled = disabledMap.get(s.getStateName());
			if(disabled.stateIsDisabled()) {
				statesToRemove.add(s);
			} else {
				ArrayList<DetTransition> allowedTransitions = new ArrayList<DetTransition>();
				ArrayList<DetTransition> transitions = this.transitions.getTransitions(getState(s));
				if(transitions != null) {
					for(DetTransition t : transitions) {
						Event e = t.getTransitionEvent();
						if(disabled.eventIsEnabled(e.getEventName())) {
							State toState = t.getTransitionState();
							if(!disabledMap.get(toState.getStateName()).stateIsDisabled())
								allowedTransitions.add(new DetTransition(e, toState));
						} // if the event is enabled
					} // for every transition
				} // if there are any transitions
				if(allowedTransitions.size() > 0) {
					newFSM.addStateTransitions(s, allowedTransitions);
				} // if there are allowed transitions
			} // if disabled state/else
		} // for every state
		newFSM.states.removeStates(statesToRemove);
		return newFSM;
	} // getSupremalControllableSublanguage(FSM)
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	@Override
	public ArrayList<State> getInitialStates() {
		ArrayList<State> initial = new ArrayList<State>();
		if(initialState != null)
			initial.add(initialState);
		return initial;
	}
	
	@Override
	public State getInitialState() {
		return initialState;
	}

	@Override
	public boolean hasInitialState(String stateName) {
		return initialState.getStateName().equals(stateName);
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
		if(initialState != null)
			initialState.setStateInitial(false);
		State curr = states.addState(newInitial);
		curr.setStateInitial(true);
		initialState = curr;
	}

	@Override
	public void addInitialState(State newState) {
		if(initialState != null)
			initialState.setStateInitial(false);
		State curr = states.addState(newState);
		curr.setStateInitial(true);
		initialState = curr;
	}

	@Override
	public boolean removeInitialState(String stateName) {
		if(initialState == null) return false;
		if(initialState.getStateName().equals(stateName)) {
			initialState = null;
			return true;
		}
		return false;
	}

}
