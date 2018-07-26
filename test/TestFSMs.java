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
		int max = 100;
		Random rand = new Random();
	while(count < max) {
		//System.out.println(count++ + " " + max++);
		
		int sizeState = 7;
		int sizeMarked = 5;
		int sizeEvents = 5;
		int sizePaths = 3;
		int sizePrivate = 2;
		int sizeUnobserv = 1;
		int sizeAttacker = 1;
		int sizeControl = 1;
		int sizeMust = 4;
		
		File m1 = new File(GenerateFSM.createModalSpec(sizeState, sizeMarked, sizeEvents, sizePaths, sizePrivate, sizeUnobserv, sizeAttacker, sizeControl, sizeMust, "modalSpec1", MAC_WORKING_FOLDER));
		File m2 = new File(GenerateFSM.createModalSpec(sizeState, sizeMarked, sizeEvents, sizePaths, sizePrivate, sizeUnobserv, sizeAttacker, sizeControl, sizeMust, "modalSpec2", MAC_WORKING_FOLDER));
		
		
		
		ModalSpecification test1 = new ModalSpecification(new File(MAC_WORKING_FOLDER + "m1.mdl.txt"), "m1");
		FSMToDot.createImgFromFSM(test1, MAC_WORKING_FOLDER + "_img_m1", MAC_WORKING_FOLDER, MAC_CONFIG_FILE_PATH);

		ModalSpecification test2 = new ModalSpecification(new File(MAC_WORKING_FOLDER + "m2.mdl.txt"), "m2");
		FSMToDot.createImgFromFSM(test2, MAC_WORKING_FOLDER + "_img_m2", MAC_WORKING_FOLDER, MAC_CONFIG_FILE_PATH);
		
		ModalSpecification test3 = test1.getGreatestLowerBound(test2);
		FSMToDot.createImgFromFSM(test3, MAC_WORKING_FOLDER + "_img_m3", MAC_WORKING_FOLDER, MAC_CONFIG_FILE_PATH);
		
		
		
		ModalSpecification mod1 = new ModalSpecification(m1, "mod1");
		
		ModalSpecification mod2 = new ModalSpecification(m2, "mod2");
		
		ModalSpecification mod3 = mod1.getGreatestLowerBound(mod2);

		//System.out.println(mod1.testCurrentStateOpacity().isEmpty() + " " + mod2.testCurrentStateOpacity().isEmpty() + " " + !mod3.testCurrentStateOpacity().isEmpty());
		

		FSMToDot.createImgFromFSM(mod1, MAC_WORKING_FOLDER + "_img_1", MAC_WORKING_FOLDER, MAC_CONFIG_FILE_PATH);
		mod1.toTextFile(MAC_WORKING_FOLDER, "_1");
		FSMToDot.createImgFromFSM(mod2, MAC_WORKING_FOLDER + "_img_2", MAC_WORKING_FOLDER, MAC_CONFIG_FILE_PATH);
		mod1.toTextFile(MAC_WORKING_FOLDER, "_2");
		FSMToDot.createImgFromFSM(mod3, MAC_WORKING_FOLDER + "_img_3", MAC_WORKING_FOLDER, MAC_CONFIG_FILE_PATH);
		mod1.toTextFile(MAC_WORKING_FOLDER, "_3");
		
		
		if(mod1.buildObserver().testCurrentStateOpacity().isEmpty() && mod2.buildObserver().testCurrentStateOpacity().isEmpty() && !mod3.buildObserver().testCurrentStateOpacity().isEmpty()) {
			int id = rand.nextInt(10000);
			System.out.println("success" + " " + id);
			FSMToDot.createImgFromFSM(mod1, MAC_WORKING_FOLDER + "toyCase/" + id + "_img_1", MAC_WORKING_FOLDER, MAC_CONFIG_FILE_PATH);
			mod1.toTextFile(MAC_WORKING_FOLDER + "toyCase/", id + "_1");
			FSMToDot.createImgFromFSM(mod2, MAC_WORKING_FOLDER + "toyCase/" + id + "_img_2", MAC_WORKING_FOLDER, MAC_CONFIG_FILE_PATH);
			mod1.toTextFile(MAC_WORKING_FOLDER + "toyCase/", id + "_2");
			FSMToDot.createImgFromFSM(mod3, MAC_WORKING_FOLDER + "toyCase/" + id + "_img_3", MAC_WORKING_FOLDER, MAC_CONFIG_FILE_PATH);
			mod1.toTextFile(MAC_WORKING_FOLDER + "toyCase/", id + "_3");
		}
		
		if(!mod1.buildObserver().testCurrentStateOpacity().isEmpty() && !mod2.buildObserver().testCurrentStateOpacity().isEmpty() && mod3.buildObserver().testCurrentStateOpacity().isEmpty()) {
			int id = rand.nextInt(10000);
			System.out.println("weird" + " " + id);
			FSMToDot.createImgFromFSM(mod1, MAC_WORKING_FOLDER + "weirdCase/" + id + "_img_1", MAC_WORKING_FOLDER, MAC_CONFIG_FILE_PATH);
			mod1.toTextFile(MAC_WORKING_FOLDER + "weirdCase/", id + "_1");
			FSMToDot.createImgFromFSM(mod2, MAC_WORKING_FOLDER + "weirdCase/" + id + "_img_2", MAC_WORKING_FOLDER, MAC_CONFIG_FILE_PATH);
			mod1.toTextFile(MAC_WORKING_FOLDER + "weirdCase/", id + "_2");
			FSMToDot.createImgFromFSM(mod3, MAC_WORKING_FOLDER + "weirdCase/" + id + "_img_3", MAC_WORKING_FOLDER, MAC_CONFIG_FILE_PATH);
			mod1.toTextFile(MAC_WORKING_FOLDER + "weirdCase/", id + "_3");
		}

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
