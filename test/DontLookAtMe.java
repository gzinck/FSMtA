package test;

import fsm.*;
import graphviz.FSMToDot;
import support.GenerateFSM;
import java.io.*;
import org.junit.Test;

public class DontLookAtMe {

	private static final String GRAEME_WORKING_FOLDER = "/Users/graemezinck/Documents/OneDrive/Documents/Work/2018 Summer Research/GraphViz/";
	private static final String GRAEME_CONFIG_FILE_PATH = "/Users/graemezinck/Documents/OneDrive/Documents/Personal/Eclipse Workspace/Summer Research/config.properties";
	
	private static final String MAC_WORKING_FOLDER = "/Users/mac/Documents/TestGraph/";
	private static final String MAC_CONFIG_FILE_PATH = "/Users/mac/Documents/FSM-Implementation-2/config.properties";
	
	@Test
	public void test() {
		File f1 = new File(GenerateFSM.createNewDeterministicFSM(4, 3, 2, 3, "fil1", GRAEME_WORKING_FOLDER));
		File f2 = new File(GenerateFSM.createNewDeterministicFSM(4, 2, 2, 3, "fil2", GRAEME_WORKING_FOLDER));
//		File f1 = new File(GRAEME_WORKING_FOLDER + "/fil1.fsm");
//		File f2 = new File(GRAEME_WORKING_FOLDER + "/fil2.fsm");
		DetFSM fsm1 = new DetFSM(f1, "fs1");
		DetFSM fsm2 = new DetFSM(f2, "fs2");
		FSMToDot.createImgFromFSM(fsm1, GRAEME_WORKING_FOLDER + "test2", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
		FSMToDot.createImgFromFSM(fsm2, GRAEME_WORKING_FOLDER + "test3", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
		System.out.println(fsm2.makeDotString());
		DetFSM fsm3 = fsm1.product(fsm2);
		FSMToDot.createImgFromFSM(fsm3, GRAEME_WORKING_FOLDER + "test4", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
		
		
//		DetFSM fsm1 = new DetFSM();
//		fsm1.addTransition("1", "a", "2");
//		fsm1.addTransition("2", "a", "3");
//		fsm1.toggleMarkedState("3");
//		fsm1.addTransition("1", "d", "6");
//		fsm1.addTransition("4", "c", "5");
//		fsm1.addState("45");
//		fsm1.toggleMarkedState("45");
//		fsm1.addInitialState("1");
//		fsm1.addInitialState("2");
//		
//		DetFSM fsm2 = new DetFSM();
//		fsm2.addTransition("1", "a", "2");
//		fsm2.addTransition("2", "a", "3");
//		fsm2.toggleMarkedState("3");
//		fsm2.addTransition("1", "a", "6");
//		fsm2.addTransition("4", "a", "5");
//		fsm2.addState("45");
//		fsm2.toggleMarkedState("45");
//		fsm2.addInitialState("1");
//		
//		DetFSM fsm3 = (DetFSM)fsm1.product(fsm2);
//		
//		System.out.println(fsm3.makeDotString());
//		FSMToDot.createImgFromFSM(fsm1, GRAEME_WORKING_FOLDER + "/" + "test1", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
//		FSMToDot.createImgFromFSM(fsm2, GRAEME_WORKING_FOLDER + "/" + "test2", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
//		FSMToDot.createImgFromFSM(fsm3, GRAEME_WORKING_FOLDER + "/" + "test3", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
////		
//		FSM anotherFSM = newFSM.trim();
//		System.out.println(anotherFSM.makeDotString());
//		FSMToDot.createImgFromFSM(anotherFSM, GRAEME_WORKING_FOLDER + "/" + "test2", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
 
	}

}
