package fsm;

import fsm.attribute.Observability;
import fsm.attribute.NonDeterministic;
import support.EventMap;
import support.ReadWrite;
import support.State;
import support.StateMap;
import support.TransitionFunction;
import support.transition.DetTransition;
import support.transition.NonDetTransition;
import support.transition.Transition;
import support.event.Event;
import support.event.ObservableEvent;
import java.io.*;
import java.util.ArrayList;

/**
 * This class models an Observable NonDeterministic FSM that expands upon the NonDetFSM class to
 * implement the Observable characteristics of an FSM - Events storing information about their
 * Observability, and some operations being made available to interact with such information.
 * 
 * This class is a part of the fsm packge.
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public class NonDetObsFSM extends FSM<State, NonDetTransition<State, ObservableEvent>, ObservableEvent>
		implements	NonDeterministic<State, NonDetTransition<State, ObservableEvent>, ObservableEvent>,
					Observability<State, NonDetTransition<State, ObservableEvent>, ObservableEvent>{
	
//--- Constant Values  -------------------------------------------------------------------------

	/** String constant designating this object as a specific type of FSM for clarification purposes*/
	public static final String FSM_TYPE = "NonDetObs FSM";
	
//--- Instance Variables  ----------------------------------------------------------------------
	
	/** ArrayList<<j>State> object that holds a list of Initial States for this Non Deterministic FSM object. */
	protected ArrayList<State> initialStates;
	
//---  Constructors  --------------------------------------------------------------------------

	/**
	 * Constructor for a NonDetObsFSM object that takes in a file and String id as input, processing
	 * the file to fill the object's contents and titling it as the defined String input.
	 * 
	 * NonDetObsFSM File Order for Special: Initial, Marked, Unobservable Events.
	 * 
	 * @param in - File object provided to be read to generate the NonDetObsFSM object.
	 * @param id - String object provided to be the name of the generated NonDetObsFSM object.
	 */
	
	public NonDetObsFSM(File in, String inId) {
		id = inId;
		
		states = new StateMap<State>(State.class);
		events = new EventMap<ObservableEvent>(ObservableEvent.class);
		transitions = new TransitionFunction<State, NonDetTransition<State, ObservableEvent>, ObservableEvent>(new NonDetTransition<State, ObservableEvent>());
		ReadWrite<State, NonDetTransition<State, ObservableEvent>, ObservableEvent> redWrt = new ReadWrite<State, NonDetTransition<State, ObservableEvent>, ObservableEvent>();
		
		ArrayList<ArrayList<String>> special = redWrt.readFromFile(states, events, transitions, in);
		initialStates = new ArrayList<State>();
		for(int i = 0; i < special.get(0).size(); i++) {
			states.getState(special.get(0).get(i)).setStateInitial(true);
			initialStates.add(states.getState(special.get(0).get(i)));
		}
		for(int i = 0; i < special.get(1).size(); i++) {
			states.getState(special.get(1).get(i)).setStateMarked(true);
		}
		for(int i = 0; i < special.get(2).size(); i++) {
			if(events.getEvent(special.get(2).get(i)) == null)
				events.addEvent(special.get(2).get(i));
			events.getEvent(special.get(2).get(i)).setEventObservability(false);
		}
	}
	
	/**
	 * Constructor for a NonDetObsFSM that takes any FSM as a parameter and creates a new
	 * NonDetObsFSM using that as the basis. Any information which is not permissible in a
	 * NonDetObsFSM is thrown away, because it does not have any means to handle it.
	 * 
	 * @param other FSM to copy as a NonDetObsFSM (can be any kind of FSM).
	 * @param inId Id for the new FSM to carry.
	 */
	
	public NonDetObsFSM(FSM<State, Transition<State, Event>, Event> other, String inId) {
		id = inId;
		states = new StateMap<State>(State.class);
		events = new EventMap<ObservableEvent>(ObservableEvent.class);
		transitions = new TransitionFunction<State, NonDetTransition<State, ObservableEvent>, ObservableEvent>(new NonDetTransition<State, ObservableEvent>());
		
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
	} // NonDetObsFSM(FSM, String)
	
	/**
	 * Constructor for an FSM object that has an ID.
	 * It contains no transitions or states, allowing the user to add those elements him/herself.
	 * 
	 * @param inID String representing the id for the FSM.
	 */
	
	public NonDetObsFSM(String inID) {
		id = inID;
		states = new StateMap<State>(State.class);
		events = new EventMap<ObservableEvent>(ObservableEvent.class);
		transitions = new TransitionFunction<State, NonDetTransition<State, ObservableEvent>, ObservableEvent>(new NonDetTransition<State, ObservableEvent>());
		initialStates = new ArrayList<State>();
	}
	
	/**
	 * Constructor for an FSM object that contains no transitions or states, allowing the
	 * user to add those elements him/herself. It has no id, either.
	 */
	
	public NonDetObsFSM() {
		id = "";
		states = new StateMap<State>(State.class);
		events = new EventMap<ObservableEvent>(ObservableEvent.class);
		transitions = new TransitionFunction<State, NonDetTransition<State, ObservableEvent>, ObservableEvent>(new NonDetTransition<State, ObservableEvent>());
		initialStates = new ArrayList<State>();
	}
		
//---  Operations   ---------------------------------------------------------------------------
		
	@Override
	public FSM<State, NonDetTransition<State, ObservableEvent>, ObservableEvent> createObserverView() {
		// TODO Auto-generated method stub
		return null;
	}
	
//---  Getter Methods   -----------------------------------------------------------------------

	@Override
	public Boolean getEventObservability(String eventName) {
		// TODO Auto-generated method stub
		return null;
	}
	
//---  Setter Methods   -----------------------------------------------------------------------

	@Override
	public void setEventObservability(String eventName, boolean status) {
		// TODO Auto-generated method stub
		
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
		for(ObservableEvent e : this.getEvents()) {
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
		ReadWrite<State, NonDetTransition<State, ObservableEvent>, ObservableEvent> rdWrt = new ReadWrite<State, NonDetTransition<State, ObservableEvent>, ObservableEvent>();
		rdWrt.writeToFile(truePath,  special, this.getTransitions());
		
	}

	@Override
	public NonDetObsFSM union(FSM<State, NonDetTransition<State, ObservableEvent>, ObservableEvent> other) {
		NonDetObsFSM newFSM = new NonDetObsFSM();
		unionHelper(other, newFSM);
		return newFSM;
	}

	@Override
	public NonDetObsFSM product(FSM<State, NonDetTransition<State, ObservableEvent>, ObservableEvent> other) {
		NonDetObsFSM newFSM = new NonDetObsFSM();
		productHelper(other, newFSM);
		return newFSM;
	}
	
	@Override
	public NonDetObsFSM parallelComposition(FSM<State, NonDetTransition<State, ObservableEvent>, ObservableEvent> other) {
		NonDetObsFSM newFSM = new NonDetObsFSM();
		parallelCompositionHelper(other, newFSM);
		return newFSM;
	}

	@Override
	public ArrayList<State> getInitialStates() {
		return initialStates;
	}
	
	@Override
	public boolean hasInitialState(String stateName) {
		for(State s : initialStates)
			if(s.getStateName().equals(stateName)) return true;
		return false;
	}

	@Override
	public void addInitialState(String newInitial) {
		State theState = states.addState(newInitial);
		theState.setStateInitial(true);
		initialStates.add(theState);
	}
	
	public void addInitialState(State newState) {
		State theState = states.addState(newState);
		theState.setStateInitial(true);
		initialStates.add(theState);
	}

	@Override
	public boolean removeInitialState(String stateName) {
		State theState = states.getState(stateName);
		theState.setStateInitial(false);
		if(initialStates.remove(theState)) return true;
		return false;
	}

	@Override
	public FSM determinize() {
		// TODO Auto-generated method stub
		return null;
	}	
	
}
