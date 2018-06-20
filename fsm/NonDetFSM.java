package fsm;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import support.*;
import support.transition.*;
import support.event.Event;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import support.event.ObservableEvent;

/**
 * This class models a NonDeterministic FSM that expands upon the abstract FSM class to
 * implement the Non-Deterministic characteristics of an FSM - Multiple Initial States, and
 * multiple States being permitted to be led to by each Event at a given State.
 * 
 * This class is a part of the fsm package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public class NonDetFSM extends FSM<State, NonDetTransition<State, Event>, Event>{
	
//--- Constant Values  -------------------------------------------------------------------------

	/** String constant designating this object as a specific type of FSM for clarification purposes*/
	public static final String FSM_TYPE = "NonDeterministic FSM";
			
//--- Instance Variables  ----------------------------------------------------------------------
			
	/** ArrayList<<s>State> object that holds a list of Initial States for this Non Deterministic FSM object. */
	protected ArrayList<State> initialStates;
		
//---  Constructors  --------------------------------------------------------------------------
		
	/**
	 * Constructor for an DetFSM object that takes in a file encoding the contents of the FSM.
	 * 
	 * NonDetFSM File Order for Special: Initial, Marked.
	 * 
	 * @param in - File read in order to create the FSM.
	 * @param id - The id for the FSM (can be any String).
	 */
	
	public NonDetFSM(File in, String inId) {
		id = inId;
		
		states = new StateMap<State>(State.class);
		events = new EventMap<Event>(Event.class);
		transitions = new TransitionFunction<State, NonDetTransition<State, Event>, Event>(new NonDetTransition<State, Event>());
		ReadWrite<State, NonDetTransition<State, Event>, Event> redWrt = new ReadWrite<State, NonDetTransition<State, Event>, Event>();
		
		ArrayList<ArrayList<String>> special = redWrt.readFromFile(states, events, transitions, in);
		initialStates = new ArrayList<State>();
		for(int i = 0; i < special.get(0).size(); i++) {
			states.getState(special.get(0).get(i)).setStateInitial(true);
			initialStates.add(states.getState(special.get(0).get(i)));
		}
		for(int i = 0; i < special.get(1).size(); i++) {
			states.getState(special.get(1).get(i)).setStateMarked(true);
		}
	} // DetFSM(File)
	
	/**
	 * Constructor for an FSM object that contains no transitions or states, allowing the
	 * user to add those elements him/herself.
	 */
	
	public NonDetFSM(String inId) {
		id = inId;
		events = new EventMap<Event>(Event.class);
		states = new StateMap<State>(State.class);
		transitions = new TransitionFunction<State, NonDetTransition<State, Event>, Event>(new NonDetTransition<State, Event>());
		initialStates = new ArrayList<State>();
	} // DetFSM()
	
	/**
	 * Constructor for an FSM object that contains no transitions or states, allowing the
	 * user to add those elements him/herself. It has no id, either.
	 */
	
	public NonDetFSM() {
		id = "";
		events = new EventMap<Event>(Event.class);
		states = new StateMap<State>(State.class);
		transitions = new TransitionFunction<State, NonDetTransition<State, Event>, Event>(new NonDetTransition<State, Event>());
		initialStates = new ArrayList<State>();
	} // DetFSM()

//---  Single-FSM Operations   ----------------------------------------------------------------
	
	@Override
	public FSM<State, NonDetTransition<State, Event>, Event> makeCoAccessible() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void toTextFile(String filePath, String name) {
		// TODO Auto-generated method stub
		
	}

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
		String init = "";
		Collections.sort(getInitialStates());
		for(State state : getInitialStates()) {
			init += state.getStateName() + ",";
		}
		init = init.substring(0, init.length()-1);
		queue.add(init);
		fsmOut.addInitialState(init);
		HashSet<String> processed = new HashSet<String>();
		
		while(!queue.isEmpty()) {
			String aggregate = queue.poll();
			if(processed.contains(aggregate))
				continue;
			processed.add(aggregate);
			String[] states = aggregate.split(",");
			HashMap<String, HashSet<String>> eventStates = new HashMap<String, HashSet<String>>();
			TransitionFunction<State, NonDetTransition<State, Event>, Event> allTrans = this.getTransitions();
			for(String targetState : states) {
				ArrayList<NonDetTransition<State, Event>> transitions = allTrans.getTransitions(new State(targetState));
				for(NonDetTransition<State, Event> oneTransition : transitions) {
					if(eventStates.get(oneTransition.getTransitionEvent().getEventName()) == null) {
						eventStates.put(oneTransition.getTransitionEvent().getEventName(), new HashSet<String>());
					}
					for(State outState : oneTransition.getTransitionStates())
						eventStates.get(oneTransition.getTransitionEvent().getEventName()).add(outState.getStateName());
				}
			}
			for(String event : eventStates.keySet()) {
				ArrayList<String> outboundStates = new ArrayList<String>();
				Iterator<String> iter = eventStates.get(event).iterator();
				while(iter.hasNext()) {
					outboundStates.add(iter.next());
				}
				Collections.sort(outboundStates);
				String collec = "";
				for(String s : outboundStates)
					collec += s + ",";
				collec = collec.substring(0, collec.length() - 1);
				queue.add(collec);
				fsmOut.addTransition("{"+aggregate+"}", event, "{"+collec+"}");
			}
			
			
		}
		
		return fsmOut;
	}
	
//---  Multi-FSM Operations   -----------------------------------------------------------------
	
	@Override
	public NonDetFSM union(FSM<State, NonDetTransition<State, Event>, Event> other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NonDetFSM product(FSM<State, NonDetTransition<State, Event>, Event> other) {
		NonDetFSM newFSM = new NonDetFSM();
		productHelper(other, newFSM);
		return newFSM;
	}
	
	@Override
	public NonDetFSM parallelComposition(FSM<State, NonDetTransition<State, Event>, Event> other) {
		NonDetFSM newFSM = new NonDetFSM();
		parallelCompositionHelper(other, newFSM);
		return newFSM;
	}

//---  Getter Methods   -----------------------------------------------------------------------

	@Override
	public ArrayList<State> getInitialStates() {
		return initialStates;
	}
	
//---  Manipulations   ------------------------------------------------------------------------
	
	@Override
	public void addInitialState(String newInitial) {
		State theState = states.addState(newInitial);
		theState.setStateInitial(true);
		initialStates.add(theState);
	}
	
	@Override
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
}
