package test;

import fsm.*;
import graphviz.FSMToDot;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TestFSMs {

	private static final String WORKING_FOLDER = "/Users/graemezinck/Documents/OneDrive/Documents/Work/2018 Summer Research/GraphViz";
	private static final String CONFIG_FILE_PATH = "/Users/graemezinck/Documents/OneDrive/Documents/Personal/Eclipse Workspace/Summer Research/config.properties";
	
	@Test
	void test() {
		DetFSM newFSM = new DetFSM();
		newFSM.addEvent("1", "a", "2");
		newFSM.addEvent("2", "a", "3");
		newFSM.addInitialState("1");
		newFSM.removeTransition("1", "a", "2");
		newFSM.removeState("1");
		System.out.println(newFSM.makeDotString());
		//FSMToDot.createImgFromFSM(newFSM, WORKING_FOLDER + "/" + "test", WORKING_FOLDER, CONFIG_FILE_PATH);
		fail("Not yet implemented");
	}

}
