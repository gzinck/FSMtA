package fsm;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import fsm.attribute.*;
import support.event.ControllableEvent;
import support.event.Event;
import support.event.ObsControlEvent;
import support.event.ObservableEvent;
import support.*;
import support.attribute.EventObservability;
import support.transition.*;

public class NonDetObsContFSM extends FSM<State, NonDetTransition<State, ObsControlEvent>, ObsControlEvent>
		implements NonDeterministic<State, NonDetTransition<State, ObsControlEvent>, ObsControlEvent>,
		Observability<State, NonDetTransition<State, ObsControlEvent>, ObsControlEvent>,
		Controllability<State, NonDetTransition<State, ObsControlEvent>, ObsControlEvent> {
	
//--- Constant Values  -------------------------------------------------------------------------

	/** String constant designating this object as a specific type of FSM for clarification purposes*/
	public static final String FSM_TYPE = "NonDeterministic FSM with Observability and Controllability";
			
//--- Instance Variables  ----------------------------------------------------------------------
			
	/** ArrayList<<j>State> object that holds a list of Initial States for this Non Deterministic FSM object. */
	protected ArrayList<State> initialStates;
	
//--- Constructors  ----------------------------------------------------------------------
	
	/**
	 * Constructor for an NonDetObsContFSM object that takes in a file encoding the contents of the FSM.
	 * 
	 * NonDetObsContFSM File Order for Special: Initial, Marked.
	 * 
	 * @param in - File read in order to create the FSM.
	 * @param id - The id for the FSM (can be any String).
	 */
	
	public NonDetObsContFSM(File in, String inId) {
		// TODO: Implement the file input
	}
	
	/**
	 * Constructor for a NonDetObsContFSM that takes any FSM as a parameter and creates a new
	 * NonDetObsContFSM using that as the basis. Any information which is not permissible in a
	 * NonDetObsContFSM is thrown away, because it does not have any means to handle it.
	 * 
	 * @param other FSM to copy as a NonDetObsContFSM (can be any kind of FSM).
	 * @param inId Id for the new FSM to carry.
	 */
	
	public NonDetObsContFSM(FSM<State, Transition<State, Event>, Event> other, String inId) {
		id = inId;
		states = new StateMap<State>(State.class);
		events = new EventMap<ObsControlEvent>(ObsControlEvent.class);
		transitions = new TransitionFunction<State, NonDetTransition<State, ObsControlEvent>, ObsControlEvent>(new NonDetTransition<State, ObsControlEvent>());
		
		// Add in all the states
		for(State s : other.states.getStates())
			this.states.addState(s);
		// Add in all the events
		for(Event e : other.events.getEvents())
			this.events.addEvent(e);
		// Add in all the transitions
		for(State s : other.states.getStates()) {
			for(Transition<State, Event> t : other.transitions.getTransitions(s)) {
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
	
	@Override
	public DetFSM determinize(){
		/*
		 * Create newFSM
		 * Create queue to process states
		 * First entry in queue is aggregate of initial states
		 * For queue:
		 *   Break the entry apart into composite States
		 *   Process States and aggregate their Transitions
		 *   For each event in these Transitions:
		 *     Aggregate target States into new entities, add to queue
		 *     Set that aggregate as the single target State for the associated Event
		 *   If all composite States are Marked, set conglomerate as Marked
		 * Return newFSM 
		 * 
		 */
		
		DetFSM fsmOut = new DetFSM("Determinized " + this.getId());
		LinkedList<String> queue = new LinkedList<String>();
		StringBuilder init = new StringBuilder();
		Collections.sort(getInitialStates());
		boolean mark1 = true;
		
		// Make the initial state
		Iterator<State> itr = getInitialStates().iterator();
		while(itr.hasNext()) {
			State curr = itr.next();
			init.append(curr.getStateName());
			if(itr.hasNext()) init.append(","); // Add a comma if there is another item to add
			mark1 = curr.getStateMarked() ? mark1 : false; // TODO: isn't it supposed to mark it if at least 1 state is marked? Might be wrong, not sure
		}
		queue.add(init.toString());
		fsmOut.addInitialState("{" + init.toString() + "}");
		
		// Mark the initial state if all of the states are marked
		if(mark1)
			fsmOut.getState("{"+init+"}").setStateMarked(true);
		HashSet<String> processed = new HashSet<String>();
		
		// Go through all the states names added to the queue
		while(!queue.isEmpty()) {
			String aggregate = queue.poll();
			if(processed.contains(aggregate)) // Don't reprocess if already done
				continue;
			processed.add(aggregate);
			String[] states = aggregate.split(","); // Break up the states into separate ones
			HashMap<String, HashSet<String>> eventStates = new HashMap<String, HashSet<String>>();
			TransitionFunction<State, NonDetTransition<State, ObsControlEvent>, ObsControlEvent> allTrans = this.getTransitions();
			
			// Go through every state and add 
			for(String targetState : states) {
				ArrayList<NonDetTransition<State, ObsControlEvent>> transitions = allTrans.getTransitions(getState(targetState));
				for(NonDetTransition<State, ObsControlEvent> oneTransition : transitions) {
					if(eventStates.get(oneTransition.getTransitionEvent().getEventName()) == null) {
						eventStates.put(oneTransition.getTransitionEvent().getEventName(), new HashSet<String>());
					}
					for(State outState : oneTransition.getTransitionStates())
						eventStates.get(oneTransition.getTransitionEvent().getEventName()).add(outState.getStateName());
				}
			}
			for(String event : eventStates.keySet()) {
				ArrayList<String> outboundStates = new ArrayList<String>();
				boolean mark = true;
				Iterator<String> iter = eventStates.get(event).iterator();
				while(iter.hasNext()) {
					String markCheck = iter.next();
					mark = this.getState(markCheck).getStateMarked() ? mark : false;
					outboundStates.add(markCheck);
				}
				Collections.sort(outboundStates);
				String collec = "";
				for(String s : outboundStates)
					collec += s + ",";
				collec = collec.substring(0, collec.length() - 1);
				queue.add(collec);
				fsmOut.addTransition("{"+aggregate+"}", event, "{"+collec+"}");
				if(mark)
					fsmOut.getState("{"+collec+"}").setStateMarked(true);
			}
		}
		return fsmOut;
	}
	
	@Override
	public NonDetObsContFSM getSupremalControllableSublanguage(FSM other) {
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
//		System.out.println(disabledMap.toString());
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
		ArrayList<? extends Transition> thisTransitions = transitions.getTransitions(curr);
		if(thisTransitions != null)
		for(Transition t : thisTransitions) {
			DisabledEvents tempDE = new DisabledEvents(false);
			boolean transitionEventDisabled = false;
			
			loopThroughTransitionStates:
			for(State s : (ArrayList<State>)(t.getTransitionStates())) {
				DisabledEvents nextDE = getDisabledEvents(s, otherFSM, visitedStates, disabledMap);
				Event e = t.getTransitionEvent();
				
				// If the event is not present in the specification, then break
				if(!otherFSM.transitions.eventExists(otherFSM.getState(s), e)) {
					currDE.disableEvent(e.getEventName());
					transitionEventDisabled = true;
					break loopThroughTransitionStates;
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
		State theState = states.getState(stateName);
		if(theState != null) {
			theState.setStateInitial(false);
			if(initialStates.remove(theState)) return true;
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
