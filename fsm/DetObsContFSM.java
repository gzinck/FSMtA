package fsm;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import fsm.attribute.*;
import support.event.Event;
import support.event.ObsControlEvent;
import support.*;
import support.attribute.EventControllability;
import support.attribute.EventObservability;
import support.transition.*;
import java.util.*;

/**
 * This class 
 * 
 * This class is a part of the fsm package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public class DetObsContFSM extends FSM<State, DetTransition<State, ObsControlEvent>, ObsControlEvent>
		implements Deterministic<State, DetTransition<State, ObsControlEvent>, ObsControlEvent>,
		Observability<State, DetTransition<State, ObsControlEvent>, ObsControlEvent>,
		Controllability<State, DetTransition<State, ObsControlEvent>, ObsControlEvent>,
		OpacityTest<State>{
	
//--- Constant Values  -------------------------------------------------------------------------

	/** String constant designating this object as a specific type of FSM for clarification purposes*/
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
		states = new StateMap<State>(State.class);	//Initialize the storage for States, Event, and Transitions
		events = new EventMap<ObsControlEvent>(ObsControlEvent.class);	//51: Create a ReadWrite object for file reading/writing (reading in this case), denote generics
		transitions = new TransitionFunction<State, DetTransition<State, ObsControlEvent>, ObsControlEvent>(new DetTransition<State, ObsControlEvent>());
		
		ReadWrite<State, DetTransition<State, ObsControlEvent>, ObsControlEvent> redWrt = new ReadWrite<State, DetTransition<State, ObsControlEvent>, ObsControlEvent>();
		ArrayList<ArrayList<String>> special = redWrt.readFromFile(states, events, transitions, in);
		initialState = states.getState(special.get(0).get(0));	//Special ArrayList 0-entry is InitialState
		states.getState(initialState).setStateInitial(true);
		for(int i = 0; i < special.get(1).size(); i++)			//Special ArrayList 1-entry is MarkedState
			states.getState(special.get(1).get(i)).setStateMarked(true);
		for(int i = 0; i <  special.get(2).size(); i++)			//Special ArrayList 2-entry is PrivateState
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
	 * user to add those elements themselves.
	 * 
	 * @param inId - String object representing the id for this FSM object.
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
	 * user to add those elements themselves, requesting no id for the FSM.
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
	public NonDetObsContFSM createObserverView() {
		NonDetObsContFSM newFSM = new NonDetObsContFSM();			//Create new FSM to hold result of operation
		HashMap<State, HashSet<State>> map = new HashMap<State, HashSet<State>>();	//Maps a State to all States it is attached to
		HashMap<String, String> name = new HashMap<String, String>();		//Maps all State names to their new names via attached States
		for(State s : this.getStates()) {						//For all States in the FSM:
			HashSet<State> thisSet = new HashSet<State>();			//Keeps track of all States attached to this State
			ArrayList<String> nameSet = new ArrayList<String>();		//Holds the names in a format that can be easily sorted
			thisSet.add(s);											//Add the original State as one in the group
			nameSet.add(s.getStateName());							//Add the original State to be concatenated into a name
			LinkedList<State> queue = new LinkedList<State>();		//Queue to process all States connected via Unobservable Events
			queue.add(s);											//First Queue entry is the original State
			HashSet<State> visited = new HashSet<State>();			//Keeps track of revisited States
			while(!queue.isEmpty()) {					//While there are more States to look at:
				State top = queue.poll();					//Get the next State
				if(visited.contains(top))					//If already processed, don't re-process the State
					continue;
				visited.add(top);							//Mark it as visited
				for(DetTransition<State, ObsControlEvent> t : this.getTransitions().getTransitions(top)) {	//Process all the State's Transitions
					if(!t.getTransitionEvent().getEventObservability() && !thisSet.contains(t.getTransitionState())) {	
						thisSet.add(t.getTransitionState());			//If the Event is unobservable and has not yet been seen, add the State
						nameSet.add(t.getTransitionState().getStateName());	//Duplicates handled by second condition
						queue.add(t.getTransitionState());			//As the State is a part of the new aggregated State, check its transitions too
					}
				}
			}
			Collections.sort(nameSet);		//For consistency, sort all included State names so that aggregates of same States are the same.
			StringBuilder sb = new StringBuilder();	//Better than a String, try it in Kattis problems that are too slow.
			
			Iterator<State> iter = thisSet.iterator();	//HashSet used to avoid duplicates, now process the State objects
			boolean on = true;							//Should be Marked?
			boolean priv = true;							//Should be Secret?
			while(iter.hasNext()) {						//For all States, check each's Status as Marked/Secret. If ever no, negate.
				State sit = iter.next();
				if(!sit.getStateMarked())
					on = false;
				if(!sit.getStatePrivacy())
					priv = false;
			}
			for(int i = 0; i < nameSet.size(); i++)		//Now build the new State's name via the sorted NameSet
				sb.append(nameSet.get(i) + (i + 1 < nameSet.size() ? ", " : "}"));
			name.put(s.getStateName(), "{" + sb.toString());		//Preferred nomenclature is to use brackets,
			map.put(s, thisSet);				//Keep track of pairings between States and their new Names or all included States
			if(on) {							//If no included State were found to be unmarked, the aggregate is Marked
				State in = newFSM.addState(name.get(s.getStateName()));
				in.setStateMarked(true);
			}
			if(priv) {						//If no included State were found to be not secret, the aggregate is Secret
				State in = newFSM.addState(name.get(s.getStateName()));
				in.setStatePrivate(true);
			}
		}
		
		for(State ar : map.keySet()) {						//For all States (which have been paired to aggregates)
			StringBuilder newState = new StringBuilder();		//Gotta build the name of the target State to go towards
			Iterator<State> iter = map.get(ar).iterator();		//Each State has a set of included States, so process through them!
			ArrayList<State> aggregate = new ArrayList<State>();		//Keep track of all States that are a part of the translated State to sort
			while(iter.hasNext()) {						//For all included States:
				State s = iter.next();						//Get the next State 
				aggregate.add(s);							//Add to collection of States making it up
				for(DetTransition<State, ObsControlEvent> dT : this.getTransitions().getTransitions(s)) {	//For all Transitions at this State:
					if(dT.getTransitionEvent().getEventObservability()) {			//If the observed Transition is Observable (not to be excised):
						NonDetTransition<State, ObsControlEvent> newTrans = new NonDetTransition<State, ObsControlEvent>();	//New Transition
						newTrans.setTransitionEvent(dT.getTransitionEvent());		//Assign it the old Event for consistency
						newTrans.setTransitionState(newFSM.addState(name.get(dT.getTransitionState().getStateName())));	//Only one target State
						newFSM.addTransition(newFSM.addState(name.get(ar.getStateName())), newTrans);	//Now add the Transition to the FSM as an
					}															//extension of the newly defined State
				}
			}
			Collections.sort(aggregate);						//Sort aggregate of States for naming purposes
			for(int i = 0; i < aggregate.size(); i++)		//Now build its name
				newState.append(aggregate.get(i).getStateName() + (i + 1 < aggregate.size() ? ", " : "}"));
			newFSM.addState("{" + newState.toString());		//And add the State just in case, probably redundant
		}
		
		newFSM.addInitialState(name.get(this.getInitialState().getStateName()));	//And assign the new Initial State
		
		return newFSM;						//Good work!
	}
	
	@Override
	public DetObsContFSM determinize() {
		NonDetObsContFSM newFSM1 = this.createObserverView();
		return newFSM1.determinize();
	}

	@Override
	public void toTextFile(String filePath, String name) {
		if(name == null)
			name = id;
		String truePath = "";
		truePath = filePath + (filePath.charAt(filePath.length()-1) == '/' ? "" : "/") + name;
		String special = "3\n";
		ArrayList<String> init = new ArrayList<String>();
		ArrayList<String> mark = new ArrayList<String>();
		ArrayList<String> unob = new ArrayList<String>();
		for(State s : this.getStates()) {
			if(s.getStateMarked()) 
				mark.add(s.getStateName());
			if(s.getStateInitial()) 
				init.add(s.getStateName());
		}
		for(ObsControlEvent e : this.getEvents()) {
			if(!e.getEventObservability())
				unob.add(e.getEventName());
		}
		special += init.size() + "\n";
		for(String s : init)
			special += s + "\n";
		special += mark.size() + "\n";
		for(String s : mark)
			special += s + "\n";
		special += unob.size() + "\n";
		for(String s : unob)
			special += s + "\n";
		ReadWrite<State, DetTransition<State, ObsControlEvent>, ObsControlEvent> rdWrt = new ReadWrite<State, DetTransition<State, ObsControlEvent>, ObsControlEvent>();
		rdWrt.writeToFile(truePath,  special, this.getTransitions());
	}
	
	@Override
	public <S1 extends State, T1 extends Transition<S1, E1>, E1 extends Event> DetObsContFSM getSupremalControllableSublanguage(FSM<S1, T1, E1> other) {
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
					for(DetTransition<State, ObsControlEvent> t : transitions) {
						ObsControlEvent e = t.getTransitionEvent();
						if(disabled.eventIsEnabled(e.getEventName())) {
							State toState = t.getTransitionState();
							if(!disabledMap.get(toState.getStateName()).stateIsDisabled())
								allowedTransitions.add(new DetTransition<State, ObsControlEvent>(e, toState));
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
		ArrayList<DetTransition<State, ObsControlEvent>> thisTransitions = transitions.getTransitions(curr);
		if(thisTransitions != null)
		for(DetTransition<State, ObsControlEvent> t : thisTransitions) {
			
			DisabledEvents nextDE = getDisabledEvents(t.getTransitionState(), otherFSM, visitedStates, disabledMap);
			ObsControlEvent e = t.getTransitionEvent();
			
			// If the event is not present in the specification, then break
			if(!otherFSM.transitions.eventExists(otherFSM.getState(t.getTransitionState()), e)) {
				currDE.disableEvent(e.getEventName());
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
			if(s.getStatePrivacy())
				secrets.add(s);
		}
		return secrets;
	}
	
//---  Multi-FSM Operations   -----------------------------------------------------------------
	
	@Override
	public <S1 extends State, T1 extends Transition<S1, E1>, E1 extends Event> NonDetObsContFSM union(FSM<S1, T1, E1> other) {
		NonDetObsContFSM newFSM = new NonDetObsContFSM();
		unionHelper(other, newFSM);
		return newFSM;
	}

	@Override
	public <S1 extends State, T1 extends Transition<S1, E1>, E1 extends Event> DetObsContFSM product(FSM<S1, T1, E1> other) {
		DetObsContFSM newFSM = new DetObsContFSM();
		productHelper(other, newFSM);
		return newFSM;
	}

	@Override
	public <S1 extends State, T1 extends Transition<S1, E1>, E1 extends Event> DetObsContFSM parallelComposition(FSM<S1, T1, E1> other) {
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
		if(initialState == null) return false;
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
