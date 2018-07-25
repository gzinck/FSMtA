package test;


import fsm.*;
import graphviz.FSMToDot;
import support.GenerateFSM;
import java.io.*;
import org.junit.Test;

public class windows {

	private static final String MAC_WORKING_FOLDER = "C:\\Users\\Borinor\\Documents\\testGraph\\";
	private static final String MAC_CONFIG_FILE_PATH = "C:\\Users\\Borinor\\Documents\\GitHub\\FSM-Implementation-2\\config.properties";
	
	@Test
	public void test() {
		File f = new File(GenerateFSM.createNewFSM(10, 4, 3, 2, 2, 3, 1, 2, false, "fileName", MAC_WORKING_FOLDER));
		DetObsContFSM fsm = new NonDetObsContFSM(f, "fsm").buildObserver();
		System.out.println(fsm.makeDotString());
		
		FSMToDot.createImgFromFSM(fsm, MAC_WORKING_FOLDER + "test1", MAC_WORKING_FOLDER, MAC_CONFIG_FILE_PATH);
		DetObsContFSM fsm2 = (DetObsContFSM)fsm.makeAccessible();
		System.out.println(fsm2.makeDotString());
		FSMToDot.createImgFromFSM(fsm2, MAC_WORKING_FOLDER + "test2", MAC_WORKING_FOLDER, MAC_CONFIG_FILE_PATH);
		DetObsContFSM fsm3 = (DetObsContFSM)fsm2.makeCoAccessible();
		System.out.println(fsm3.makeDotString());
		FSMToDot.createImgFromFSM(fsm3, MAC_WORKING_FOLDER + "test3", MAC_WORKING_FOLDER, MAC_CONFIG_FILE_PATH);
		
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
