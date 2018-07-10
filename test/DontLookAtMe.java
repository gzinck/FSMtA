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
		// (int sizeStates, int sizeMarked, int sizeEvents, int sizePaths, int sizeInitial, int sizePrivate, int sizeUnobserv, int sizeControl, boolean nonDet, String name, String filePath)
//		File f1 = new File(GenerateFSM.createNewFSM(3, 3, 2, 2, 2, 1, 1, 2, true, "fileName1", GRAEME_WORKING_FOLDER));
//		File f2 = new File(GenerateFSM.createNewFSM(3, 3, 2, 2, 1, 1, 1, 2, true, "fileName2", GRAEME_WORKING_FOLDER));
////		File f1 = new File(GRAEME_WORKING_FOLDER + "/fil1.fsm");
//		File f1 = new File("/Users/graemezinck/Documents/OneDrive/Documents/Work/2018 Summer Research/GraphViz/FSMI2/cantdeterminize.fsm");
//		File f2 = new File("/Users/graemezinck/Documents/OneDrive/Documents/Work/2018 Summer Research/GraphViz/FSMI2/simpledet2.fsm");
//		File f3 = new File("/Users/graemezinck/Documents/OneDrive/Documents/Work/2018 Summer Research/GraphViz/FSMI2/simpledet3.fsm");
//		NonDetObsContFSM fsm1 = new NonDetObsContFSM(f1, "fs1");
//		NonDetObsContFSM fsm2 = new NonDetObsContFSM(f2, "fs2");
//		FSMToDot.createImgFromFSM(fsm1, GRAEME_WORKING_FOLDER + "test2", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
//		FSMToDot.createImgFromFSM(fsm2, GRAEME_WORKING_FOLDER + "test3", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
//		System.out.println(fsm2.makeDotString());
//		DetFSM fsm3 = fsm1.product(fsm2);
//		FSMToDot.createImgFromFSM(fsm3, GRAEME_WORKING_FOLDER + "test4", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
		
		
		DetObsContFSM fsm1 = new DetObsContFSM("OK");
		fsm1.addTransition("1", "a", "2");
		fsm1.setEventControllability("a", false);
		fsm1.addInitialState("1");
		FSMToDot.createImgFromFSM(fsm1, GRAEME_WORKING_FOLDER + "test", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
		ModalSpecification ms = new ModalSpecification("OK");
		ms.addState("1");
		ms.addMustTransition("1", "a", "2");
		ms.addTransition("1", "b", "3");
		ms.addInitialState("1");
		FSMToDot.createImgFromFSM(ms, GRAEME_WORKING_FOLDER + "testms", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
		
//		ms.makeOptimalSupervisor(fsm1);
		
//		fsm1.addTransition("1", "a", "2");
//		fsm1.addTransition("2", "b", "3");
//		fsm1.addTransition("2", "a", "10");
//		fsm1.addTransition("10", "c", "1");
//		fsm1.addTransition("10", "b", "2");
//		fsm1.addInitialState("1");
//		fsm1.addInitialState("2");
//		//fsm1.setEventControllability("d", false);
//		fsm1.setEventObservability("a", false);
//		//fsm1.setEventObservability("e", false);
//		
//		
//		DetObsContFSM fsm2 = new DetObsContFSM();
//		fsm2.addTransition("1", "a", "2");
//		fsm2.addTransition("2", "b", "3");
//		fsm2.addTransition("2", "a", "10");
//		fsm2.addTransition("10", "c", "1");
//		fsm2.addTransition("10", "c", "2");
//		fsm2.addInitialState("1");
//		fsm2.addInitialState("2");
//		fsm1.setEventControllability("a", false);
//		
//		DetObsContFSM fsm3 = fsm1.parallelComposition(fsm2);
//	
//		System.out.println(fsm3.makeDotString());
//		FSMToDot.createImgFromFSM(fsm1, GRAEME_WORKING_FOLDER + "/" + "test1", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
//		FSMToDot.createImgFromFSM(fsm2, GRAEME_WORKING_FOLDER + "/" + "test2", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
//		FSMToDot.createImgFromFSM(fsm3, GRAEME_WORKING_FOLDER + "/" + "test3", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
//		
//		FSM anotherFSM = newFSM.trim();
//		System.out.println(anotherFSM.makeDotString());
//		FSMToDot.createImgFromFSM(anotherFSM, GRAEME_WORKING_FOLDER + "/" + "test2", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
 
	}

}
