package support;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.ArrayList;
import fsm.DetObsContFSM;
import support.Agent;
import support.map.TransitionFunction;
import support.transition.DetTransition;

public class UStructure {

	private DetObsContFSM plantFSM;
	private Agent[] agents;
	private TransitionFunction<DetTransition> badTransitions;
	private DetObsContFSM uStructure;
	
	public UStructure(DetObsContFSM thePlant, TransitionFunction<DetTransition> theBadTransitions, Agent ... theAgents) {
		plantFSM = thePlant;
		badTransitions = theBadTransitions;
		agents = new Agent[theAgents.length + 1];
		agents[0] = new Agent(thePlant.getEventMap().getEvents().toArray(new Event[thePlant.getEventMap().getEvents().size()]));
		for(int i = 0; i < theAgents.length; i++)
			agents[i+1] = theAgents[i];
		HashSet<String> allEvents = new HashSet<String>();
		for(Agent a : agents)									//Bad habits! But it's so small...
			for(Event e : a.getAgentEvents())
				allEvents.add(e.getEventName());
		for(Agent a : agents)
			for(String e : allEvents)
				if(!a.contains(e))
					a.addNonPresentEvent(e);
		uStructure = new DetObsContFSM();
	}
	
	public void createUStructure() {
		LinkedList<BatchAgentStates> queue = new LinkedList<BatchAgentStates>();
		HashSet<BatchAgentStates> visited = new HashSet<BatchAgentStates>();
		State[] starting = new State[agents.length];
		for(int i = 0; i < starting.length; i++)
			starting[i] = plantFSM.getInitialState();
		queue.add(new BatchAgentStates(starting));
		
		while(!queue.isEmpty()) {
			BatchAgentStates stateSet = queue.poll();
			if(visited.contains(stateSet))
				continue;
			visited.add(stateSet);
			uStructure.addState(stateSet.getStates());
			HashSet<String> viableEvents = new HashSet<String>();
			for(State s : stateSet.getStates()) {
				for(DetTransition t : plantFSM.getTransitions().getTransitions(s))
					viableEvents.add(t.getTransitionEvent().getEventName());
			}
			for(String s : viableEvents) {
				boolean[] canAct = new boolean[stateSet.getStates().length];
				for(int i = 0; i < stateSet.getStates().length; i++) {
					//Two case: Can do it (Add to total list) or Can't See It/Can't Do It
					State st = stateSet.getStates()[i];
					Event e = null;
					for(DetTransition t : plantFSM.getStateTransitions(st))
						if(t.getTransitionEvent().getEventName().equals(s))
							e = t.getTransitionEvent();
					if(e == null || !e.getEventObservability()) {
						State[] newSet = new State[stateSet.getStates().length];
						String eventName = "<";
						for(int j = 0; j < stateSet.getStates().length; j++) {
							if(i == j) {
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
						queue.add(new BatchAgentStates(newSet));
						uStructure.addTransition(uStructure.addState(new State(stateSet.getStates())), uStructure.getEventMap().addEvent(eventName), uStructure.addState(new State(newSet)));
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
				queue.add(new BatchAgentStates(newSet));
				uStructure.addTransition(uStructure.addState(new State(stateSet.getStates())), uStructure.getEventMap().addEvent(eventName), uStructure.addState(new State(newSet)));
			}
		}
	}

	public DetObsContFSM getUStructure() {
		return uStructure;
	}
}

class BatchAgentStates{
	
	State[] currentStates;
	
	public BatchAgentStates(State[] states) {
		currentStates = states;
	}
	
	public State[] getStates() {
		return currentStates;
	}
	
	
	
}
