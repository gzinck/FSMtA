package fsm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import fsm.attribute.*;
import support.event.ControllableEvent;
import support.event.Event;
import support.event.ObsControlEvent;
import support.event.ObservableEvent;
import support.*;
import support.attribute.EventObservability;
import support.transition.*;

public class DetObsContFSM extends FSM<State, DetTransition<State, ObsControlEvent>, ObsControlEvent>
		implements Deterministic<State, DetTransition<State, ObsControlEvent>, ObsControlEvent>,
		Observability<State, DetTransition<State, ObsControlEvent>, ObsControlEvent>,
		Controllability<State, DetTransition<State, ObsControlEvent>, ObsControlEvent> {
	
//--- Constant Values  -------------------------------------------------------------------------

	/** String constant designating this object as a specific type of FSM for clarification purposes*/
	public static final String FSM_TYPE = "Deterministic FSM with Observability and Controllability";
			
//--- Instance Variables  ----------------------------------------------------------------------
			
	/** State object that holds the initial state for this Non Deterministic FSM object. */
	protected State initialState;
	
//--- Constructors  ----------------------------------------------------------------------
	
	/**
	 * Constructor for a DetObsContFSM that takes any FSM as a parameter and creates a new
	 * DetFSM using that as the basis. Any information which is not permissible in a
	 * DetObsContFSM is thrown away, because it does not have any means to handle it.
	 * 
	 * @param other - FSM object to copy as a DetObsContFSM (can be any kind of FSM).
	 * @param inId - String object representing the id for the new FSM.
	 */
	
	public DetObsContFSM(FSM<State, Transition<State, Event>, Event> other, String inId) {
		id = inId;
		states = new StateMap<State>(State.class);
		events = new EventMap<ObsControlEvent>(ObsControlEvent.class);
		transitions = new TransitionFunction<State, DetTransition<State, ObsControlEvent>, ObsControlEvent>(new DetTransition<State, ObsControlEvent>());
		
		// Add in all the states
		for(State s : other.states.getStates())
			this.states.addState(s).setStateInitial(false);
		// Add in all the events
		for(Event e : other.events.getEvents())
			this.events.addEvent(e);
		// Add in all the transitions (but only take the first state it transitions to)
		for(State s : other.states.getStates()) {
			for(Transition<State, Event> t : other.transitions.getTransitions(s)) {
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
	 * Constructor for an FSM object that contains no transitions or states, allowing the
	 * user to add those elements him/herself.
	 */
	
	public DetObsContFSM(String inId) {
		id = inId;
		events = new EventMap<ObsControlEvent>(ObsControlEvent.class);
		states = new StateMap<State>(State.class);
		transitions = new TransitionFunction<State, DetTransition<State, ObsControlEvent>, ObsControlEvent>(new DetTransition<State, ObsControlEvent>());
		initialState = null;
	} // DetFSM()
	
	/**
	 * Constructor for an FSM object that contains no transitions or states, allowing the
	 * user to add those elements him/herself. It has no id, either.
	 */
	
	public DetObsContFSM() {
		id = "";
		events = new EventMap<ObsControlEvent>(ObsControlEvent.class);
		states = new StateMap<State>(State.class);
		transitions = new TransitionFunction<State, DetTransition<State, ObsControlEvent>, ObsControlEvent>(new DetTransition<State, ObsControlEvent>());
		initialState = null;
	} // NonDetObsContFSM()

//---  Single-FSM Operations   ----------------------------------------------------------------
	
	@Override
	public NonDeterministic createObserverView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void toTextFile(String filePath, String name) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public DetObsContFSM getSupremalControllableSublanguage(FSM other) {
		// Store what events are disabled in the map.
		HashMap<String, DisabledEvents> disabledMap = new HashMap<String, DisabledEvents>();
		// Parse the graph and identify disabled states and disabled events
		for(State s : states.getStates()) {
			HashSet<String> visitedStates = new HashSet<String>();
			disabledMap.put(s.getStateName(), getDisabledEvents(s, other, visitedStates, disabledMap));
		} // for every state
		
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
				ArrayList<DetTransition<State, ObsControlEvent>> allowedTransitions = new ArrayList<DetTransition<State, ObsControlEvent>>();
				ArrayList<DetTransition<State, ObsControlEvent>> transitions = this.transitions.getTransitions(getState(s));
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
	
	@Override
	public DisabledEvents getDisabledEvents(State curr, FSM otherFSM, HashSet<String> visitedStates, HashMap<String, DisabledEvents> disabledMap) {
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
		ArrayList<? extends DetTransition> thisTransitions = transitions.getTransitions(curr);
		if(thisTransitions != null)
		for(DetTransition t : thisTransitions) {
			boolean transitionEventDisabled = false;
			
			DisabledEvents nextDE = getDisabledEvents(t.getTransitionState(), otherFSM, visitedStates, disabledMap);
			Event e = t.getTransitionEvent();
			
			// If the event is not present in the specification, then break
			if(!otherFSM.transitions.eventExists(otherFSM.getState(t.getTransitionState()), e)) {
				currDE.disableEvent(e.getEventName());
				transitionEventDisabled = true;
			} // if event not present in spec
			
			if(nextDE != null) { // As long as we're not backtracking...
				// If the transition state is bad, must disable the event.
				if(nextDE.stateIsDisabled()) {
					// If the event is uncontrollable, disable this entire state
					if(e instanceof ControllableEvent && !((ControllableEvent)e).getEventControllability()) {
						currDE.disableState();
						disabledMap.put(currName, currDE);
						return currDE;
					} // if uncontrollable event
					// Else, disable the transition event (but none of the other events)
					currDE.disableEvent(e.getEventName());
					transitionEventDisabled = true;
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

//---  Multi-FSM Operations   -----------------------------------------------------------------
	
	@Override
	public NonDetObsContFSM union(FSM<State, DetTransition<State, ObsControlEvent>, ObsControlEvent> other) {
		NonDetObsContFSM newFSM = new NonDetObsContFSM();
		unionHelper(other, newFSM);
		return newFSM;
	}

	@Override
	public DetObsContFSM product(FSM<State, DetTransition<State, ObsControlEvent>, ObsControlEvent> other) {
		DetObsContFSM newFSM = new DetObsContFSM();
		productHelper(other, newFSM);
		return newFSM;
	}

	@Override
	public DetObsContFSM parallelComposition(
			FSM<State, DetTransition<State, ObsControlEvent>, ObsControlEvent> other) {
		DetObsContFSM newFSM = new DetObsContFSM();
		parallelCompositionHelper(other, newFSM);
		return newFSM;
	}

//---  Getter Methods   -----------------------------------------------------------------------
	
	@Override
	public ArrayList<State> getInitialStates() {
		ArrayList<State> initial = new ArrayList<State>();
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
		ObsControlEvent curr = events.getEvent(eventName);
		if(curr != null)
			return curr.getEventObservability();
		return null;
	}
	
	@Override
	public Boolean getEventControllability(String eventName) {
		ObsControlEvent curr = events.getEvent(eventName);
		if(curr != null)
			return curr.getEventControllability();
		return null;
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
		if(initialState.getStateName().equals(stateName)) {
			initialState = null;
			return true;
		}
		return false;
	}

	@Override
	public boolean setEventObservability(String eventName, boolean status) {
		ObsControlEvent curr = events.getEvent(eventName);
		if(curr != null) {
			curr.setEventObservability(status);
			return true;
		}
		return false;
	}
	
	@Override
	public void setEventControllability(String eventName, boolean value) {
		ObsControlEvent curr = events.getEvent(eventName);
		if(curr != null)
			curr.setEventControllability(value);
	}
}
