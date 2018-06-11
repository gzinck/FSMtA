package test;

import fsm.*;
import graphviz.FSMToDot;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TestFSMs {

	private static final String GRAEME_WORKING_FOLDER = "/Users/graemezinck/Documents/OneDrive/Documents/Work/2018 Summer Research/GraphViz";
	private static final String GRAEME_CONFIG_FILE_PATH = "/Users/graemezinck/Documents/OneDrive/Documents/Personal/Eclipse Workspace/Summer Research/config.properties";
	
	@Test
	void test() {
		DetFSM fsm1 = new DetFSM();
		fsm1.addTransition("1", "a", "2");
		fsm1.addTransition("2", "a", "3");
		fsm1.toggleMarkedState("3");
		fsm1.addTransition("1", "a", "6");
		fsm1.addTransition("4", "a", "5");
		fsm1.addState("45");
		fsm1.toggleMarkedState("45");
		fsm1.addInitialState("1");
		
		DetFSM fsm2 = new DetFSM();
		fsm2.addTransition("1", "a", "2");
		fsm2.addTransition("2", "a", "3");
		fsm2.toggleMarkedState("3");
		fsm2.addTransition("1", "a", "6");
		fsm2.addTransition("4", "a", "5");
		fsm2.addState("45");
		fsm2.toggleMarkedState("45");
		fsm2.addInitialState("1");
		
		NonDetFSM fsm3 = fsm1.union(fsm2);
		
		System.out.println(fsm3.makeDotString());
		FSMToDot.createImgFromFSM(fsm3, GRAEME_WORKING_FOLDER + "/" + "test", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
//		
//		FSM anotherFSM = newFSM.trim();
//		System.out.println(anotherFSM.makeDotString());
//		FSMToDot.createImgFromFSM(anotherFSM, GRAEME_WORKING_FOLDER + "/" + "test2", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
	}

}
