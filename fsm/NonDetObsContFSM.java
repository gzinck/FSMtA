package fsm;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import fsm.attribute.*;
import support.event.*;
import support.*;
import support.attribute.EventControllability;
import support.attribute.EventObservability;
import support.transition.*;

/**
 * This class models a Finite State Machine of the NonDeterministic variety, extending the
 * Abstract Class FSM for generic FSM characteristics and implementing a variety of interfaces
 * that denote more advanced features, such as Observable and Controllable Events.
 * 
 * This class is a part of the fsm package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public class NonDetObsContFSM extends FSM<State, NonDetTransition<State, ObsControlEvent>, ObsControlEvent>
		implements NonDeterministic<State, NonDetTransition<State, ObsControlEvent>, ObsControlEvent>,
		Observability<State, NonDetTransition<State, ObsControlEvent>, ObsControlEvent>,
		Controllability<State, NonDetTransition<State, ObsControlEvent>, ObsControlEvent>,
		OpacityTest<State>{
	
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
	 * @param in - File object provided in order to create the FSM.
	 * @param id - String object representing the id for the FSM (can be any String).
	 */
	
	public NonDetObsContFSM(File in, String inId) {
		id = inId;									//Assign id
		states = new StateMap<State>(State.class);	//Initialize the storage for States, Event, and Transitions
		events = new EventMap<ObsControlEvent>(ObsControlEvent.class);	//51: Create a ReadWrite object for file reading/writing (reading in this case), denote generics
		transitions = new TransitionFunction<State, NonDetTransition<State, ObsControlEvent>, ObsControlEvent>(new NonDetTransition<State, ObsControlEvent>());
		initialStates = new ArrayList<State>();
		
		ReadWrite<State, NonDetTransition<State, ObsControlEvent>, ObsControlEvent> redWrt = new ReadWrite<State, NonDetTransition<State, ObsControlEvent>, ObsControlEvent>();
		ArrayList<ArrayList<String>> special = redWrt.readFromFile(states, events, transitions, in);
		
		for(int i = 0; i < special.get(0).size(); i++) {	//Special ArrayList 0-entry is InitialState
			states.getState(special.get(0).get(i)).setStateInitial(true);
			initialStates.add(states.addState(special.get(0).get(i)));
		}
		for(int i = 0; i < special.get(1).size(); i++)			//Special ArrayList 1-entry is MarkedState
			states.getState(special.get(1).get(i)).setStateMarked(true);
		for(int i = 0; i < special.get(2).size(); i++)			//Special ArrayList 2-entry is Private State
			states.getState(special.get(2).get(i)).setStatePrivate(true);
		for(int i = 0; i < special.get(3).size(); i++) {			//Special ArrayList 3-entry is ObservableEvent
			if(events.getEvent(special.get(3).get(i)) == null)
				events.addEvent(special.get(3).get(i));
			events.getEvent(special.get(3).get(i)).setEventObservability(false);
		}
		for(int i = 0; i < special.get(4).size(); i++) {			//Special ArrayList 4-entry is Controllable Event
			if(events.getEvent(special.get(4).get(i)) == null)
				events.addEvent(special.get(4).get(i));
			events.getEvent(special.get(4).get(i)).setEventControllability(false);
		}
	}
	
	/**
	 * Constructor for a NonDetObsContFSM that takes any FSM as a parameter and creates a new
	 * NonDetObsContFSM using that as the basis. Any information which is not permissible in a
	 * NonDetObsContFSM is thrown away, because it does not have any means to handle it.
	 * 
	 * @param other - FSM object to copy as a NonDetObsContFSM (can be any kind of FSM).
	 * @param inId - String object representing the Id for the new FSM to carry.
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
	 * user to add those elements themselves.
	 * 
	 * @param - String object representing the new id for this NonDetObsContFSM object
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
	 * user to add those elements themselves. It has no id, either.
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
	public NonDetObsContFSM createObserverView() {
		NonDetObsContFSM newFSM = new NonDetObsContFSM();		//See comments in DetObsContFSM for now, same process
		HashMap<State, HashSet<State>> map = new HashMap<State, HashSet<State>>();
		HashMap<String, String> name = new HashMap<String, String>();
		for(State s : this.getStates()) {
			HashSet<State> thisSet = new HashSet<State>();
			ArrayList<String> nameSet = new ArrayList<String>();
			thisSet.add(s);
			nameSet.add(s.getStateName());
			LinkedList<State> queue = new LinkedList<State>();
			queue.add(s);
			HashSet<State> visited = new HashSet<State>();
			while(!queue.isEmpty()) {
				State top = queue.poll();
				if(visited.contains(top))
					continue;
				visited.add(top);
				for(NonDetTransition<State, ObsControlEvent> t : this.getTransitions().getTransitions(top)) {
				  if(!t.getTransitionEvent().getEventObservability()) {
					for(State sr : t.getTransitionStates()) {
						if(!thisSet.contains(sr)) {
					 	  thisSet.add(sr);
						  nameSet.add(sr.getStateName());
						  queue.add(sr);
					}
				  }
				}
			  }
			}
			Collections.sort(nameSet);
			StringBuilder sb = new StringBuilder();
			
			Iterator<State> iter = thisSet.iterator();
			boolean on = true;
			boolean priv = true;
			while(iter.hasNext()) {
				State sit = iter.next();
				if(!sit.getStateMarked())
					on = false;
				if(!sit.getStatePrivacy())
					priv = false;
			}
			for(int i = 0; i < nameSet.size(); i++)
				sb.append(nameSet.get(i) + (i + 1 < nameSet.size() ? "," : "}"));
			name.put(s.getStateName(), "{" + sb.toString());
			map.put(s, thisSet);
			if(on) {
				State in = newFSM.addState(name.get(s.getStateName()));
				in.setStateMarked(true);
			}
			if(priv) {
				State in = newFSM.addState(name.get(s.getStateName()));
				in.setStatePrivate(true);
			}
				
		}
		
		for(State ar : map.keySet()) {
			StringBuilder newState = new StringBuilder();
			Iterator<State> iter = map.get(ar).iterator();
			ArrayList<State> aggregate = new ArrayList<State>();
			while(iter.hasNext()) {
				State s = iter.next();
				aggregate.add(s);
				for(NonDetTransition<State, ObsControlEvent> dT : this.getTransitions().getTransitions(s)) {
					if(dT.getTransitionEvent().getEventObservability()) {
					  NonDetTransition<State, ObsControlEvent> newTrans = new NonDetTransition<State, ObsControlEvent>();
					  newTrans.setTransitionEvent(dT.getTransitionEvent());
					  for(State sr : dT.getTransitionStates()) {
						newTrans.addTransitionState(newFSM.addState(name.get(sr.getStateName())));
					  }
					  newFSM.addTransition(newFSM.addState(name.get(ar.getStateName())), newTrans);
					}
				}
			}
			Collections.sort(aggregate);
			for(int i = 0; i < aggregate.size(); i++)
				newState.append(aggregate.get(i).getStateName() + (i + 1 < aggregate.size() ? "," : "}"));
			newFSM.addState("{" + newState.toString());
		}
		
		for(State sI : this.getInitialStates())
			newFSM.addInitialState(name.get(sI.getStateName()));
		
		return newFSM;
	}

	@Override
	public void toTextFile(String filePath, String name) {
		if(name == null)
			name = id;
		String truePath = "";
		truePath = filePath + (filePath.charAt(filePath.length()-1) == '/' ? "" : "/") + name;
		String special = "5\n";
		ArrayList<String> init = new ArrayList<String>();
		ArrayList<String> mark = new ArrayList<String>();
		ArrayList<String> priv = new ArrayList<String>();
		ArrayList<String> unob = new ArrayList<String>();
		ArrayList<String> cont = new ArrayList<String>();
		for(State s : this.getStates()) {
			if(s.getStateMarked()) 
				mark.add(s.getStateName());
			if(s.getStateInitial()) 
				init.add(s.getStateName());
			if(s.getStatePrivacy())
				priv.add(s.getStateName());
		}
		for(ObsControlEvent e : this.getEvents()) {
			if(!e.getEventObservability())
				unob.add(e.getEventName());
			if(!e.getEventControllability())
				cont.add(e.getEventName());
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
		special += cont.size() + "\n";
		for(String s : cont)
			special += s + "\n";
		ReadWrite<State, NonDetTransition<State, ObsControlEvent>, ObsControlEvent> rdWrt = new ReadWrite<State, NonDetTransition<State, ObsControlEvent>, ObsControlEvent>();
		rdWrt.writeToFile(truePath,  special, this.getTransitions());
	}
	
	@Override
	public DetObsContFSM determinize(){
		
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
		
		DetObsContFSM fsmOut = new DetObsContFSM("Determinized " + this.getId());
		LinkedList<String> queue = new LinkedList<String>();
		StringBuilder init = new StringBuilder();
		Collections.sort(getInitialStates());
		boolean mark1 = true;
		
		// Make the initial state
		Iterator<State> itr = getInitialStates().iterator();
		while(itr.hasNext()) {
			State curr = itr.next();
			init.append(curr.getStateName());
			if(itr.hasNext()) 
				init.append("-"); // Add a comma if there is another item to add
			mark1 = curr.getStateMarked() ? mark1 : false; 
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
			String[] states = aggregate.split("-"); // Break up the states into separate ones
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
				boolean priv = true;
				Iterator<String> iter = eventStates.get(event).iterator();
				while(iter.hasNext()) {
					String markCheck = iter.next();
					mark = this.getState(markCheck).getStateMarked() ? mark : false;
					priv = this.getState(markCheck).getStatePrivacy() ? priv : false;
					outboundStates.add(markCheck);
				}
				Collections.sort(outboundStates);
				String collec = "";
				for(String s : outboundStates)
					collec += s + "-";
				collec = collec.substring(0, collec.length() - 1);
				queue.add(collec);
				fsmOut.addTransition("{"+aggregate+"}", event, "{"+collec+"}");
				fsmOut.setEventControllability(event, this.getEventControllability(event));
				fsmOut.setEventObservability(event, this.getEventObservability(event));
				if(mark)
					fsmOut.getState("{"+collec+"}").setStateMarked(true);
				if(priv)
					fsmOut.getState("{"+collec+"}").setStatePrivate(true);
			}
		}
		return fsmOut;
	}
	
	@Override
	public <S1 extends State, T1 extends Transition<S1, E1>, E1 extends Event> NonDetObsContFSM getSupremalControllableSublanguage(FSM<S1, T1, E1> other) {
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
					for(NonDetTransition<State, ObsControlEvent> t : transitions) {
						ObsControlEvent e = t.getTransitionEvent();
						if(disabled.eventIsEnabled(e.getEventName())) {
							// Create a list of the states the event leads to
							ArrayList<State> toStates = new ArrayList<State>();
							for(State toS : (ArrayList<State>)t.getTransitionStates()) {
								if(!disabledMap.get(toS.getStateName()).stateIsDisabled())
									toStates.add(getState(toS.getStateName()));
							} // for every toState
							if(toStates.size() > 0)
								allowedTransitions.add(new NonDetTransition<State, ObsControlEvent>(e, toStates));
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
	public <S1 extends State, T1 extends Transition<S1, E1>, E1 extends Event> DisabledEvents getDisabledEvents(State curr, FSM<S1, T1, E1> otherFSM, HashSet<String> visitedStates, HashMap<String, DisabledEvents> disabledMap) {
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
		ArrayList<NonDetTransition<State, ObsControlEvent>> thisTransitions = transitions.getTransitions(curr);
		if(thisTransitions != null)
		for(NonDetTransition<State, ObsControlEvent> t : thisTransitions) {
			DisabledEvents tempDE = new DisabledEvents(false);
			boolean transitionEventDisabled = false;
			
			loopThroughTransitionStates:
			for(State s : (ArrayList<State>)(t.getTransitionStates())) {
				DisabledEvents nextDE = getDisabledEvents(s, otherFSM, visitedStates, disabledMap);
				ObsControlEvent e = t.getTransitionEvent();
				
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
			if(s.getStatePrivacy())
				secrets.add(s);
		}
		return secrets;
	}
	
//---  Multi-FSM Operations   -----------------------------------------------------------------
	
	@Override
	public <S1 extends State, T1 extends Transition<S1, E1>, E1 extends Event> NonDetObsContFSM union(FSM ... other) {
		NonDetObsContFSM newFSM = new NonDetObsContFSM();
		for(int i = 0; i < other.length; i++) {
			NonDetObsContFSM newerFSM = new NonDetObsContFSM();
			other[i].unionHelper(newFSM, newerFSM);
			newFSM = newerFSM;
		}
		NonDetObsContFSM newerFSM = new NonDetObsContFSM();
		this.unionHelper(newFSM, newerFSM);
		return newerFSM;
	}

	@Override
	public <S1 extends State, T1 extends Transition<S1, E1>, E1 extends Event> NonDetObsContFSM product(FSM ... other) {
		NonDetObsContFSM newFSM = new NonDetObsContFSM();
		this.productHelper(other[0], newFSM);
		for(int i = 1; i < other.length; i++) {
			NonDetObsContFSM newerFSM = new NonDetObsContFSM();
			other[i].productHelper(newFSM, newerFSM);
			newFSM = newerFSM;
		}
		return newFSM;
	}
	
	@Override
	public <S1 extends State, T1 extends Transition<S1, E1>, E1 extends Event> NonDetObsContFSM parallelComposition(FSM ... other){
		NonDetObsContFSM newFSM = new NonDetObsContFSM();
		for(int i = 0; i < other.length; i++) {
			NonDetObsContFSM newerFSM = new NonDetObsContFSM();
			other[i].parallelCompositionHelper(newFSM, newerFSM);
			newFSM = newerFSM;
		}
		NonDetObsContFSM newerFSM = new NonDetObsContFSM();
		this.parallelCompositionHelper(newFSM, newerFSM);
		return newerFSM;
	}

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
