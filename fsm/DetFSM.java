package fsm;

import java.io.File;
import java.util.ArrayList;

import fsm.attribute.Deterministic;
import support.*;
import support.transition.*;
import support.event.Event;

/**
 * This class models a Deterministic FSM that expands upon the abstract FSM class to
 * implement the Deterministic characteristics of an FSM - A Single Initial State, and
 * only one State being led to by each Event at a given State.
 * 
 * This class is a part of the fsm package.
 * 
 * Implements the Interface(s): Deterministic
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public class DetFSM extends FSM<State, DetTransition<State, Event>, Event>
		implements Deterministic<State, DetTransition<State, Event>, Event> {
	
//---  Constant Values   ----------------------------------------------------------------------

	/** String constant designating this object as a specific type of FSM for clarification purposes*/
	public static final String FSM_TYPE = "Deterministic FSM";
	
//---  Instance Variables   -------------------------------------------------------------------
	
	/** State object with the initial state for the deterministic FSM. */
	protected State initialState;
	
//---  Constructors   -------------------------------------------------------------------------

	/**
	 * Constructor for a DetFSM object that takes in a file encoding the contents of the FSM.
	 * 
	 * DetFSM File Order for Special: Initial, Marked.
	 * 
	 * @param in - File object whose contents are read in order to create the FSM.
	 * @param id - String object representing the id for this DetFSM object.
	 */
	
	public DetFSM(File in, String inId) {
		id = inId;									//Assign id
		states = new StateMap<State>(State.class);	//Initialize the storage for States, Event, and Transitions
		events = new EventMap<Event>(Event.class);	//51: Create a ReadWrite object for file reading/writing (reading in this case), denote generics
		transitions = new TransitionFunction<State, DetTransition<State, Event>, Event>(new DetTransition<State, Event>());
		
		ReadWrite<State, DetTransition<State, Event>, Event> redWrt = new ReadWrite<State, DetTransition<State, Event>, Event>();
		ArrayList<ArrayList<String>> special = redWrt.readFromFile(states, events, transitions, in);	//Process input file, assigns values to States,
																	//(con't) Events, and Transitions, returning package of info 	for other features
		initialState = states.getState(special.get(0).get(0));		//First portion of Special ArrayList are the Initial State(s) 
		states.getState(initialState).setStateInitial(true);			//Deterministic, only one Initial
		for(int i = 0; i < special.get(1).size(); i++) {				//Second portion are the Marked States
			states.getState(special.get(1).get(i)).setStateMarked(true);	
		}
	} // DetFSM(File)
	
	/**
	 * Constructor for a DetFSM that takes any FSM as a parameter and creates a new
	 * DetFSM using that as the basis. Any information which is not permissible in a
	 * DetFSM is thrown away, because it does not have any means to handle it.
	 * 
	 * @param other - FSM object to copy as a DetFSM (can be any kind of FSM).
	 * @param inId - String object representing the id for the new FSM.
	 */
	
	public DetFSM(FSM<State, Transition<State, Event>, Event> other, String inId) {
		id = inId;
		states = new StateMap<State>(State.class);
		events = new EventMap<Event>(Event.class);
		transitions = new TransitionFunction<State, DetTransition<State, Event>, Event>(new DetTransition<State, Event>());
		
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
	 * user to add those elements their-self. Only initializes all instance variables.
	 * 
	 * @param - String object representing the id for the new FSM.
	 */
	
	public DetFSM(String inId) {
		id = inId;
		states = new StateMap<State>(State.class);
		events = new EventMap<Event>(Event.class);
		transitions = new TransitionFunction<State, DetTransition<State, Event>, Event>(new DetTransition<State, Event>());
		initialState = null;
	} // DetFSM(String)
	
	/**
	 * Constructor for an FSM object that contains no transitions or states, allowing the
	 * user to add those elements their-self. It has no id, either. Initializes all instance
	 * variables and defaults the id to an empty String.
	 */
	
	public DetFSM() {
		id = "";
		states = new StateMap<State>(State.class);
		events = new EventMap<Event>(Event.class);
		transitions = new TransitionFunction<State, DetTransition<State, Event>, Event>(new DetTransition<State, Event>());
		initialState = null;
	} // DetFSM()

//---  Single-FSM Operations   ----------------------------------------------------------------
	
	@Override
	public void toTextFile(String filePath, String name) {
		if(name == null)					//File is named after the FSM if no name is given
			name = id;
		String truePath = "";			//Creates the full file path, name included, and handles questionable '/' cases
		truePath = filePath + (filePath.charAt(filePath.length()-1) == '/' ? "" : "/") + name;
										//The String special will be written to the file as the first portion describing special cases in the FSM.
		String special = "2\n1\n" + this.getInitialState().getStateName() + "\n?\n";	//Denote 2 kinds of special, 1 Initial State (and add it), ? Marked States
		int counter = 0;							//To count how many Marked States there are
		for(State s : this.getStates()) {		//Process all States and check if they're Marked or not; if so, increment and add to 'special'
			if(s.getStateMarked()) {
				counter++;
				special += s.getStateName() + "\n";	//Line skips for consistent file-formatting
			}
		}
		special = special.replace("?", counter+"");	//Now replace the planted '?' character with the actual size of the Marked States
		ReadWrite<State, DetTransition<State, Event>, Event> rdWrt = new ReadWrite<State, DetTransition<State, Event>, Event>();	//Prep a ReadWrite
		rdWrt.writeToFile(truePath, special, this.getTransitions());		//Pass the filePath, pre-computed Special states section, and the Transitions to be written for us 
	}
	
//---  Multi-FSM Operations   -----------------------------------------------------------------

	@Override
	public <S1 extends State, T1 extends Transition<S1, E1>, E1 extends Event> NonDetFSM union(FSM<S1, T1, E1> other) {
		NonDetFSM newFSM = new NonDetFSM();
		unionHelper(other, newFSM);
		return newFSM;
	}

	@Override
	public <S1 extends State, T1 extends Transition<S1, E1>, E1 extends Event> DetFSM product(FSM<S1, T1, E1> other) {
		DetFSM newFSM = new DetFSM();
		productHelper(other, newFSM);
		return newFSM;
	}
	
	@Override
	public <S1 extends State, T1 extends Transition<S1, E1>, E1 extends Event> DetFSM parallelComposition(FSM<S1, T1, E1> other) {
		DetFSM newFSM = new DetFSM();
		parallelCompositionHelper(other, newFSM);	
		return newFSM;
	}

//---  Getter Methods   -----------------------------------------------------------------------
	
	@Override
	public ArrayList<State> getInitialStates() {
		ArrayList<State> initial = new ArrayList<State>();	//For compatability with certain functions, take one Initial as ArrayList
		if(initialState != null)								//Only append a State if it exists, no null entries
		  initial.add(initialState);
		return initial;
	} // getInitialStates()
	
	@Override
	public boolean hasInitialState(String stateName) {
		return initialState.getStateName().equals(stateName);
	}
	
	@Override
	public State getInitialState() {
		return initialState;
	}

//---  Add Setter Methods   -----------------------------------------------------------------------
	
	@Override
	public void addInitialState(String newInitial) {
		State theState = states.addState(newInitial); // Attempt to add the State to StateMap; if new, will generate, and always returns that State
		theState.setStateInitial(true);				
		if(initialState != null) 					//If replacing the InitialState, remove its previous status as Initial
			initialState.setStateInitial(false);
		initialState = theState;						//Reassign
	}

	@Override
	public void addInitialState(State newState) {
		State obt = states.addState(newState);
		obt.setStateInitial(true);
		if(initialState != null) 
			initialState.setStateInitial(false);
		initialState = obt;
	}
	
//---  Remove Setter Methods   ------------------------------------------------------------------------

	@Override
	public boolean removeInitialState(String stateName) {
		if(initialState != null && stateName.equals(initialState.getStateName())) {
			initialState.setStateInitial(false);
			initialState = null;
			return true;
		}
		return false;
	}
	
} // class DetFSM