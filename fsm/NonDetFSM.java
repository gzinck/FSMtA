package fsm;

import java.util.ArrayList;

import support.State;
import support.transition.Transition;

public class NonDetFSM extends FSM{

	@Override
	public FSM makeAccessible() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FSM union(FSM other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FSM product(FSM other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FSM makeCoAccessible() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FSM trim() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void toTextFile(String filePath, String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean addState(String newState) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeState(String state) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean toggleMarkedState(String state) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addInitialState(String newState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean removeInitialState(String state) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addStateTransitions(State state, ArrayList<Transition> transitions) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addEvent(String state1, String eventName, String state2) {
		// TODO Auto-generated method stub
		
	}

}
