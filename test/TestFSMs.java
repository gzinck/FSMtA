package test;

import java.util.*;
import fsm.*;
import graphviz.FSMToDot;
import support.GenerateFSM;
import java.io.*;
import org.junit.Test;

public class TestFSMs {

	private static final String GRAEME_WORKING_FOLDER = "/Users/graemezinck/Documents/OneDrive/Documents/Work/2018 Summer Research/GraphViz";
	private static final String GRAEME_CONFIG_FILE_PATH = "/Users/graemezinck/Documents/OneDrive/Documents/Personal/Eclipse Workspace/Summer Research/config.properties";
	
	private static final String MAC_WORKING_FOLDER = "/Users/mac/Documents/TestGraph/";
	private static final String MAC_CONFIG_FILE_PATH = "/Users/mac/Documents/FSM-Implementation-2/config.properties";
	
	@Test
	public void test() {
		File f1 = new File(GenerateFSM.createNewFSM(3, 3, 2, 2, 3, 1, 1, 2, false, "fileName1", MAC_WORKING_FOLDER));
		File f2 = new File(GenerateFSM.createNewFSM(3, 3, 2, 2, 3, 1, 1, 2, false, "fileName2", MAC_WORKING_FOLDER));
		File f3 = new File(GenerateFSM.createNewFSM(10, 4, 3, 2, 2, 1, 1, 2, false, "fileName3", MAC_WORKING_FOLDER));
		
			DetObsContFSM fsm = new DetObsContFSM(f1, "fsm");
		
		System.out.println(fsm.makeDotString());
		FSMToDot.createImgFromFSM(fsm, MAC_WORKING_FOLDER + "test1", MAC_WORKING_FOLDER, MAC_CONFIG_FILE_PATH);
		
			DetObsContFSM fsm2 = new DetObsContFSM(f2, "fsm2");
		
		System.out.println(fsm2.makeDotString());
		FSMToDot.createImgFromFSM(fsm2, MAC_WORKING_FOLDER + "test2", MAC_WORKING_FOLDER, MAC_CONFIG_FILE_PATH);
		
			DetObsContFSM fsm3 = new DetObsContFSM(f3, "fsm3");
		
		System.out.println(fsm3.makeDotString());
		FSMToDot.createImgFromFSM(fsm3, MAC_WORKING_FOLDER + "test3", MAC_WORKING_FOLDER, MAC_CONFIG_FILE_PATH);
		
			DetObsContFSM fsm4 = fsm.product(fsm2);

		System.out.println(fsm4.makeDotString());
		FSMToDot.createImgFromFSM(fsm4, MAC_WORKING_FOLDER + "test4", MAC_WORKING_FOLDER, MAC_CONFIG_FILE_PATH);
	/*
		FSMToDot.createImgFromFSM(fsm1, MAC_WORKING_FOLDER + "test2", MAC_WORKING_FOLDER, MAC_CONFIG_FILE_PATH);
		DetFSM fsm2 = fsm1.determinize();
		FSMToDot.createImgFromFSM(fsm2, MAC_WORKING_FOLDER + "test3", MAC_WORKING_FOLDER, MAC_CONFIG_FILE_PATH);
		System.out.println(fsm2.makeDotString());
		
		/*NonDetFSM fsm3 = fsm1.union(fsm2);
		FSMToDot.createImgFromFSM(fsm3, MAC_WORKING_FOLDER + "test4", MAC_WORKING_FOLDER, MAC_CONFIG_FILE_PATH);
		
	/*
		
		NonDetFSM fsm1 = new NonDetFSM();
		fsm1.addTransition("1", "a", "2");
		fsm1.addTransition("2", "a", "3");
		fsm1.toggleMarkedState("3");
		fsm1.addTransition("1", "d", "6");
		fsm1.addTransition("4", "c", "5");
		fsm1.addState("45");
		fsm1.toggleMarkedState("45");
		fsm1.addInitialState("1");
		fsm1.addInitialState("2");
		
//		DetFSM fsm2 = new DetFSM();
//		fsm2.addTransition("1", "a", "2");
//		fsm2.addTransition("2", "a", "3");
//		fsm2.toggleMarkedState("3");
//		fsm2.addTransition("1", "a", "6");
//		fsm2.addTransition("4", "a", "5");
//		fsm2.addState("45");
//		fsm2.toggleMarkedState("45");
//		fsm2.addInitialState("1");
		
		NonDetFSM fsm3 = (NonDetFSM)fsm1.makeAccessible();
		
		System.out.println(fsm3.makeDotString());
		FSMToDot.createImgFromFSM(fsm1, GRAEME_WORKING_FOLDER + "/" + "test1", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
		FSMToDot.createImgFromFSM(fsm3, GRAEME_WORKING_FOLDER + "/" + "test2", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
//		
//		FSM anotherFSM = newFSM.trim();
//		System.out.println(anotherFSM.makeDotString());
//		FSMToDot.createImgFromFSM(anotherFSM, GRAEME_WORKING_FOLDER + "/" + "test2", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
 
 */
	}

}
