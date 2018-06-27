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

public class NonDetObsContFSM extends FSM<State, NonDetTransition<State, ObsControlEvent>, ObsControlEvent>
		implements Observability<State, NonDetTransition<State, ObsControlEvent>, ObsControlEvent>,
		Controllability<State, NonDetTransition<State, ObsControlEvent>, ObsControlEvent> {
	
//--- Constant Values  -------------------------------------------------------------------------

	/** String constant designating this object as a specific type of FSM for clarification purposes*/
	public static final String FSM_TYPE = "NonDeterministic FSM with Observability and Controllability";
			
//--- Instance Variables  ----------------------------------------------------------------------
			
	/** ArrayList<<j>State> object that holds a list of Initial States for this Non Deterministic FSM object. */
	protected ArrayList<State> initialStates;
	
//--- Constructors  ----------------------------------------------------------------------
	
	/**
	 * Constructor for an FSM object that contains no transitions or states, allowing the
	 * user to add those elements him/herself.
	 */
	
	public NonDetObsContFSM(String inId) {
		id = inId;
		events = new EventMap<ObsControlEvent>(ObsControlEvent.class);
		states = new StateMap<State>(State.class);
		transitions = new TransitionFunction<State, NonDetTransition<State, ObsControlEvent>, ObsControlEvent>(new NonDetTransition<State, ObsControlEvent>());
		initialStates = new ArrayList<State>();
	} // DetFSM()
	
	/**
	 * Constructor for an FSM object that contains no transitions or states, allowing the
	 * user to add those elements him/herself. It has no id, either.
	 */
	
	public NonDetObsContFSM() {
		id = "";
		events = new EventMap<ObsControlEvent>(ObsControlEvent.class);
		states = new StateMap<State>(State.class);
		transitions = new TransitionFunction<State, NonDetTransition<State, ObsControlEvent>, ObsControlEvent>(new NonDetTransition<State, ObsControlEvent>());
		initialStates = new ArrayList<State>();
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
	
	/**
	 * This creates a new FSM which represents the supremal controllable sublanguage
	 * of the calling FSM with respect to the language of the parameter FSM, other.
	 * This factors in both the observability and controllability of different events in the
	 * FSMs.
	 * 
	 * @param other The FSM representing the language to which the the calling FSM must be
	 * controllable. 
	 * @return The FSM representing the supremal controllable sublanguage of the calling FSM
	 * with respect to the parameter FSM.
	 */
	public NonDetObsContFSM getSupremalControllableSublanguage(FSM other) {
		// Store what events are disabled in the map.
		HashMap<String, DisabledEvents> disabledMap = new HashMap<String, DisabledEvents>();
		// Parse the graph and identify disabled states and disabled events
		for(State s : states.getStates()) {
			HashSet<String> visitedStates = new HashSet<String>();
			disabledMap.put(s.getStateName(), getDisabledPortions(s, other, visitedStates, disabledMap));
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
				ArrayList<NonDetTransition<State, ObsControlEvent>> allowedTransitions = new ArrayList<NonDetTransition<State, ObsControlEvent>>();
				ArrayList<NonDetTransition<State, ObsControlEvent>> transitions = this.transitions.getTransitions(getState(s));
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
		
		System.out.println(disabledMap.toString());
		
		newFSM.states.removeStates(statesToRemove);
		
		return newFSM;
	} // getSupremalControllableSublanguage(FSM)
	
	/**
	 * Recursively goes through states and examines what should be disabled. The results of what states
	 * to disable and events to disable (at enabled states) are stored in the disabledMap HashMap using
	 * DisabledEvents objects.
	 * 
	 * @param curr State in the current FSM that is being evaluated for disabled events.
	 * @param otherFSM FSM representing the desired maximum specification for the final FSM product.
	 * @param visitedStates HashSet of state names (Strings) which indicate which states have already
	 * been recursed through (thereby preventing loops). Because of the fact that loops are not allowed
	 * (which in turn allows this to not enter an infinite loop), this whole process must be repeated for
	 * every single state in the FSM (aside from ones we already have a guarantee are disabled).
	 * @param disabledMap Results of what to disable at each state.
	 * @return A DisabledEvents object with what needs to be disabled at any given state.
	 */
	private DisabledEvents getDisabledPortions(State curr, FSM otherFSM, HashSet<String> visitedStates, HashMap<String, DisabledEvents> disabledMap) {
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
		
		System.out.println("Main: " + curr.getStateName());
		
		// Otherwise, go through the neighbours and identify which events we need to disable.
		DisabledEvents currDE = new DisabledEvents(false);
		for(Transition t : transitions.getTransitions(curr)) {
			DisabledEvents tempDE = new DisabledEvents(false);
			boolean transitionEventDisabled = false;
			
			loopThroughTransitionStates:
			for(State s : (ArrayList<State>)(t.getTransitionStates())) {
				System.out.println("Connected with " + t.getTransitionEvent().getEventName() + " to " + s.getStateName());
				DisabledEvents nextDE = getDisabledPortions(s, otherFSM, visitedStates, disabledMap);
				Event e = t.getTransitionEvent();
				
				if(nextDE != null) { // As long as we're not backtracking...
					// If the transition state is bad, must disable the event.
					if(nextDE.stateIsDisabled()) {
						// If the event is uncontrollable, disable this entire state
						if(e instanceof ControllableEvent && !((ControllableEvent)e).getEventControllability()) {
							currDE.disableState();
							disabledMap.put(currName, currDE);
							System.out.println("DISABLED ENTIRE STATE");
							return currDE;
						} // if uncontrollable event
						// Else, disable the transition event (but none of the other events)
						currDE.disableEvent(e.getEventName());
						transitionEventDisabled = true;
						System.out.println("DISABLED ENTIRE EVENT " + e.getEventName());
						break loopThroughTransitionStates;
					} // if state is disabled
					
					// If the state is good, but has disabled events, AND the transition event to the toState is unobservable
					// remove the disabled events from this state.
					if(!nextDE.allEventsEnabled()) {
						System.out.println("Not all children are great.");
					}
					if(!nextDE.allEventsEnabled() && e instanceof EventObservability && !((EventObservability)e).getEventObservability()) {
						tempDE.disableEvents(nextDE);
						System.out.println("Disabling events because of later problems... " + nextDE.toString());
					} // if there are disabled events and the transition's event is unobservable
				} // if the disabled events for the next state is NOT null
			} // for each destination state
			
			// As long as the event hasn't been disabled (in which case, the event was already marked as disabled),
			// add the temporary disabled events in for the current state.
			if(!transitionEventDisabled)
				currDE.disableEvents(tempDE);
		} // for each transition
		return currDE;
	} // getDisabledPortions

//---  Multi-FSM Operations   -----------------------------------------------------------------
	
	@Override
	public NonDetObsContFSM union(FSM<State, NonDetTransition<State, ObsControlEvent>, ObsControlEvent> other) {
		NonDetObsContFSM newFSM = new NonDetObsContFSM();
		unionHelper(other, newFSM);
		return newFSM;
	}

	@Override
	public NonDetObsContFSM product(FSM<State, NonDetTransition<State, ObsControlEvent>, ObsControlEvent> other) {
		NonDetObsContFSM newFSM = new NonDetObsContFSM();
		productHelper(other, newFSM);
		return newFSM;
	}

	@Override
	public NonDetObsContFSM parallelComposition(
			FSM<State, NonDetTransition<State, ObsControlEvent>, ObsControlEvent> other) {
		NonDetObsContFSM newFSM = new NonDetObsContFSM();
		parallelCompositionHelper(other, newFSM);
		return newFSM;
	}

//---  Getter Methods   -----------------------------------------------------------------------
	
	@Override
	public ArrayList<State> getInitialStates() {
		return initialStates;
	}

	@Override
	public boolean hasInitialState(String stateName) {
		return initialStates.contains(stateName);
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
		State curr = states.addState(newInitial);
		curr.setStateInitial(true);
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
		State curr = getState(stateName);
		if(curr != null)
			return initialStates.remove(curr);
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
