package support;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.ArrayList;
import fsm.DetObsContFSM;
import support.Agent;
import support.map.TransitionFunction;
import support.transition.DetTransition;
import java.util.HashMap;

public class UStructure {

	private static final String UNOBSERVED_EVENT = "w";
	
	private DetObsContFSM plantFSM;
	private Agent[] agents;
	private HashMap<String, ArrayList<DetTransition>> badTransitions;
	private DetObsContFSM uStructure;
	private HashMap<State, State[]> compositeMapping;
	private HashSet<State> goodBadStates;
	private HashSet<State> badGoodStates;
	
	public UStructure(DetObsContFSM thePlant, TransitionFunction<DetTransition> theBadTransitions, Agent ... theAgents) {
		plantFSM = thePlant;
		badTransitions = new HashMap<String, ArrayList<DetTransition>>();
		for(State s : theBadTransitions.getStates()) {
			ArrayList<DetTransition> newTrans = new ArrayList<DetTransition>();
			for(DetTransition t : theBadTransitions.getTransitions(s)) {
				newTrans.add(t);
			}
			badTransitions.put(s.getStateName(), newTrans);
		}
		for(State s : thePlant.getStates()) {
			if(badTransitions.get(s.getStateName()) == null)
				badTransitions.put(s.getStateName(), new ArrayList<DetTransition>());
		}
		agents = new Agent[theAgents.length + 1];
		agents[0] = new Agent(thePlant.getEventMap().getEvents().toArray(new Event[thePlant.getEventMap().getEvents().size()]));
		for(int i = 0; i < theAgents.length; i++)
			agents[i+1] = theAgents[i];
		HashSet<String> allEvents = new HashSet<String>();
		allEvents.add(UNOBSERVED_EVENT);
		for(Agent a : agents)									//Bad habits! But it's so small...
			for(Event e : a.getAgentEvents())
				allEvents.add(e.getEventName());
		for(Agent a : agents)
			for(String e : allEvents)
				if(!a.contains(e))
					a.addNonPresentEvent(e);
		createUStructure();
		findIllegalStates();
	}
	
	public void createUStructure() {
		uStructure = new DetObsContFSM();
		compositeMapping = new HashMap<State, State[]>();
		LinkedList<BatchAgentStates> queue = new LinkedList<BatchAgentStates>();
		HashSet<State> visited = new HashSet<State>();
		State[] starting = new State[agents.length];
		for(int i = 0; i < starting.length; i++)
			starting[i] = plantFSM.getInitialState();
		State init = uStructure.addState(starting);
		uStructure.addInitialState(init);
		compositeMapping.put(init, starting);
		queue.add(new BatchAgentStates(starting, uStructure.addState(starting)));
		while(!queue.isEmpty()) {
			BatchAgentStates stateSet = queue.poll();
			if(visited.contains(stateSet.getIdentityState()))
				continue;
			visited.add(stateSet.getIdentityState());
			HashSet<String> viableEvents = new HashSet<String>();
			for(State s : stateSet.getStates()) {
				for(DetTransition t : plantFSM.getTransitions().getTransitions(s))
					viableEvents.add(t.getTransitionEvent().getEventName());
			}
			for(String s : viableEvents) {
				boolean[] canAct = new boolean[stateSet.getStates().length];
				for(int i = 0; i < stateSet.getStates().length; i++) {
					if(!agents[i].getObservable(s)) {
						State[] newSet = new State[stateSet.getStates().length];
						String eventName = "<";
						for(int j = 0; j < stateSet.getStates().length; j++) {
							if(i == j) {
								newSet[j] = stateSet.getStates()[j];
								for(DetTransition t : plantFSM.getStateTransitions(stateSet.getStates()[j])) {
									if(t.getTransitionEvent().getEventName().equals(s))
										newSet[j] = t.getTransitionState();
								}
								eventName += s + (j + 1 < stateSet.getStates().length ? ", " : ">");
							}
							else {
								newSet[j] = stateSet.getStates()[j];
								eventName += "w" + (j + 1 < stateSet.getStates().length ? ", " : ">");
							}
						}
						boolean fail = false;
						for(State state : newSet)
							if(state == null)
								fail = true;
						if(!fail) {
							queue.add(new BatchAgentStates(newSet, uStructure.addState(new State(newSet))));
							uStructure.addTransition(uStructure.addState(new State(stateSet.getStates())), uStructure.getEventMap().addEvent(eventName), uStructure.addState(new State(newSet)));
							compositeMapping.put(uStructure.addState(new State(newSet)), newSet);
						}
					}
					else {
						canAct[i] = true;
					}
				}
				State[] newSet = new State[stateSet.getStates().length];
				String eventName = "<";
				for(int i = 0; i < canAct.length; i++) {
					if(canAct[i]) {
						eventName += s + (i + 1 < canAct.length ? ", " : ">");
						for(DetTransition t : plantFSM.getStateTransitions(stateSet.getStates()[i])) {
							if(t.getTransitionEvent().getEventName().equals(s)){
								newSet[i] = t.getTransitionState();
							}
						}
					}
					else {
						eventName += "w" + (i + 1 < canAct.length ? ", " : ">");
						newSet[i] = stateSet.getStates()[i];
					}
				}
				boolean fail = false;
				for(State state : newSet)
					if(state == null)
						fail = true;
				if(!fail) {
					queue.add(new BatchAgentStates(newSet, uStructure.addState(new State(newSet))));
					uStructure.addTransition(uStructure.addState(new State(stateSet.getStates())), uStructure.getEventMap().addEvent(eventName), uStructure.addState(new State(newSet)));
					compositeMapping.put(uStructure.addState(new State(newSet)), newSet);
				}
			}
		}
	}

	public void findIllegalStates() {
		goodBadStates = new HashSet<State>();
		badGoodStates = new HashSet<State>();
		for(State s : uStructure.getStates()) {
			for(DetTransition t : uStructure.getStateTransitions(s)) {
				Event e = t.getTransitionEvent();
				String[] event = e.getEventName().substring(1, e.getEventName().length()-1).split(", ");
				State[] states = compositeMapping.get(s);
				Boolean[] legality = new Boolean[states.length];
				for(int i = 0; i < states.length; i++) {
					if(agents[i].getControllable(event[i])) {
						legality[i] = true;
						for(DetTransition bad : badTransitions.get(states[i].getStateName())) {
							if(bad.getTransitionEvent().getEventName().equals(event[i])) {
								legality[i] = false;
							}
						}
					}
					else{
						legality[i] = null;
					}
				}
				Boolean outcome;
				if(legality[0] == null) {
					outcome = null;
				}
				else {
					boolean first = legality[0];
					outcome = first;
					for(int i = 1; i < legality.length; i++)
						if(legality[i] == null || legality[i] != first)
							outcome = outcome;
						else
							outcome = null;
				}
				if(outcome != null) {
					if(outcome) {
						goodBadStates.add(s);
					}
					else {
						badGoodStates.add(s);
					}
				}
			}
		}
	}
	
	public DetObsContFSM getUStructure() {
		return uStructure;
	}
		
	public DetObsContFSM getPlantFSM() {
		return plantFSM;
	}

	public HashSet<State> getIllegalConfigOneStates(){
		return badGoodStates;
	}
	
	public HashSet<State> getIllegalConfigTwoStates(){
		return goodBadStates;
	}
}

class BatchAgentStates implements Comparable<BatchAgentStates>{
	
	State[] currentStates;
	State confirmedObject;
	
	public BatchAgentStates(State[] states, State identity) {
		currentStates = states;
		confirmedObject = identity;
	}
	
	public State[] getStates() {
		return currentStates;
	}
	
	public void setState(State in) {
		confirmedObject = in;
	}

	@Override
	public int compareTo(BatchAgentStates o) {
		boolean fail = false;
		for(int i = 0; i < this.getStates().length; i++)
			if(!this.getStates()[i].getStateName().equals(o.getStates()[i].getStateName()))
				fail = true;
		if(!fail)
			return 0;
		else
			return -1;
	}
		
	public State getIdentityState() {
		return confirmedObject;
	}
	
	@Override
	public boolean equals(Object o1) {
		return this.confirmedObject.equals(((BatchAgentStates)o1).confirmedObject);
	}
	
}
