	package test;

import java.util.*;
import fsm.*;
import graphviz.FSMToDot;
import support.GenerateFSM;
import java.io.*;
import org.junit.Test;
import support.State;

public class TestFSMs {

	private static final String GRAEME_WORKING_FOLDER = "/Users/graemezinck/Documents/OneDrive/Documents/Work/2018 Summer Research/GraphViz";
	private static final String GRAEME_CONFIG_FILE_PATH = "/Users/graemezinck/Documents/OneDrive/Documents/Personal/Eclipse Workspace/Summer Research/config.properties";
	
	private static final String MAC_WORKING_FOLDER = "/Users/mac/Documents/TestGraph/";
	private static final String MAC_CONFIG_FILE_PATH = "/Users/mac/Documents/FSM-Implementation-2/config.properties";
	
	
	
	/**
	 * DONE TODO:  Events need second type of Observability
	 * DONE TODO:  Modal Specification needs Observable Events in File constructor, and a second type of Observable
	 * DONE TODO:  Attacker Observability and System Observability in FSM as well. 
	 * DONE TODO:  Adjust GenerateFSM class for new data types
	 * 
	 * TODO:  Determinization of ModalSpec using second kind of Observable Event; if May and Must of same Event, for now, force Must. 
	 * TODO:  Make some examples of two opaque combining to make non-opaque
	 * DONE TODO:  remove Union, it was Parallel Composition all along, don't need it.
	 * TODO:  Randomly generate some Modal Specifications with constraints to achieve our desires.
	 * TODO:  We are working on the last of the project, square this section away, nothing else is ahead.
	 * 
	 * TODO:  Graeme gone August 2'nd, 3'rd, 7'th.
	 * TODO:  August 17'th is last day of work, excluding work on the Paper.
	 */
	
	
	
	@Test
	public void test() {
		int count = 0;
		int max = 1;
	while(count < max) {
		System.out.println(count++);
		
		int sizeState = 5;
		int sizeMarked = 2;
		int sizeEvents = 5;
		int sizePaths = 3;
		int sizePrivate = 1;
		int sizeUnobserv = 1;
		int sizeAttacker = 0;
		int sizeControl = 1;
		int sizeMust = 3;
		
		File m1 = new File(GenerateFSM.createModalSpec(sizeState, sizeMarked, sizeEvents, sizePaths, sizePrivate, sizeUnobserv, sizeAttacker, sizeControl, sizeMust, "modalSpec1", MAC_WORKING_FOLDER));
		
		ModalSpecification mod1 = new ModalSpecification(m1, "mod1");

		FSMToDot.createImgFromFSM(mod1, MAC_WORKING_FOLDER + "mod1", MAC_WORKING_FOLDER, MAC_CONFIG_FILE_PATH);
		mod1.toTextFile(MAC_WORKING_FOLDER, "modalSpec2");
		
		ModalSpecification mod2 = new ModalSpecification(new File(MAC_WORKING_FOLDER + "modalSpec2" + ".mdl"), "mod2").buildObserver();
		
		FSMToDot.createImgFromFSM(mod2, MAC_WORKING_FOLDER + "mod2", MAC_WORKING_FOLDER, MAC_CONFIG_FILE_PATH);
		System.out.println(count);
	}
		
	/*
	
		File f1 = new File(GenerateFSM.createNewFSM(7, 4, 4, 2, 1, 1, 1, 1, false, "fileName1", MAC_WORKING_FOLDER));
		File f2 = new File(GenerateFSM.createNewFSM(7, 4, 4, 2, 2, 1, 1, 1, false, "fileName2", MAC_WORKING_FOLDER));
		File f3 = new File(GenerateFSM.createNewFSM(4, 2, 2, 2, 2, 1, 1, 1, false, "fileName3", MAC_WORKING_FOLDER));
		File f4 = new File(GenerateFSM.createNewFSM(5, 2, 2, 2, 3, 1, 1, 1, false, "fileName4", MAC_WORKING_FOLDER));
		
		
		
		
			DetObsContFSM fsm = new DetObsContFSM(f1, "fsm");
		
		//System.out.println(fsm.makeDotString());
		FSMToDot.createImgFromFSM(fsm, MAC_WORKING_FOLDER + "test1", MAC_WORKING_FOLDER, MAC_CONFIG_FILE_PATH);
		
			NonDetObsContFSM fsm2 = new NonDetObsContFSM(f2, "fsm2");
			
			NonDetObsContFSM fsm5 = new NonDetObsContFSM(f3, "fsm3");
		
		//System.out.println(fsm2.makeDotString());
		FSMToDot.createImgFromFSM(fsm2, MAC_WORKING_FOLDER + "test2", MAC_WORKING_FOLDER, MAC_CONFIG_FILE_PATH);
		
			DetObsContFSM fsm3 = fsm.getSupremalControllableSublanguage(fsm);
		
		//System.out.println(fsm3.getComposedStates());
		FSMToDot.createImgFromFSM(fsm3, MAC_WORKING_FOLDER + "test3", MAC_WORKING_FOLDER, MAC_CONFIG_FILE_PATH);
		
			NonDetObsContFSM fsm4 = fsm2.getSupremalControllableSublanguage(fsm2);
			//fsm4 = fsm4.product(fsm);
		
		//System.out.println(fsm4.getComposedStates());
		FSMToDot.createImgFromFSM(fsm4, MAC_WORKING_FOLDER + "test4", MAC_WORKING_FOLDER, MAC_CONFIG_FILE_PATH);
		fsm.toTextFile(MAC_WORKING_FOLDER, fsm.getId());
		fsm2.toTextFile(MAC_WORKING_FOLDER, fsm2.getId());
		fsm3.setId("fsm3");
		fsm3.toTextFile(MAC_WORKING_FOLDER, fsm3.getId());
		fsm4.setId("fsm4");
		fsm4.toTextFile(MAC_WORKING_FOLDER, fsm4.getId());
		fsm = new DetObsContFSM(new File(MAC_WORKING_FOLDER + fsm.getId() + ".fsm"), fsm.getId());
		fsm2 = new NonDetObsContFSM(new File(MAC_WORKING_FOLDER + fsm2.getId() + ".fsm"), fsm2.getId());
		fsm3 = new DetObsContFSM(new File(MAC_WORKING_FOLDER + fsm3.getId() + ".fsm"), fsm3.getId());
		fsm4 = new NonDetObsContFSM(new File(MAC_WORKING_FOLDER + fsm4.getId() + ".fsm"), fsm4.getId());
	//}
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
