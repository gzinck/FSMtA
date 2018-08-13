package test;


import fsm.*;
import graphviz.FSMToDot;
import support.GenerateFSM;
import support.SVGtoTikZ;

import java.io.*;
import org.junit.Test;

public class windows {

	/**
	 * TODO:  Update Composition to account for Graeme's changes - Greatest Lower Bound pruning.
	 * 
	 * TODO:  Turn .svg file into TikZ language format. (Parse the former, convert into latter.)
	 * TODO:  Read some Case Studies and try to make into a working example. (Have the handout; Mac computer wiped via security stuff.)
	 * TODO:  User Interface up-to-date.
	 * TODO:  Update commenting all over.
	 * TODO:  Aug 16'th - Demo the Software, make sure it can be exported and works.
	 * TODO:  Instruction Manual!
	 * 
	 * TODO:  August 17'th is last day of work, excluding desired excess work.
	 */
	
	
	/*
	 * 
	 * Distinction in the isBadState() method between a good state, a bad state, and a bad state that
	 * can be ignored for breaking its parent. Only distinguishes good/bad (generic)
	 * 
	 * 
	 * 
	 */
	
	private static final String MAC_WORKING_FOLDER = "C:\\Users\\Reithger\\Documents\\TestGraph\\";
	private static final String MAC_CONFIG_FILE_PATH = "C:\\Users\\Reithger\\Documents\\GitHub\\FSM-Implementation-2\\config.properties";
	
	@Test
	public void test() {
		int sizeState = 50;
		int sizeMarked = 22;
		int sizeEvents = 7;
		int sizePaths = 3;
		int sizePrivate = 6;
		int sizeUnobserv = 1;
		int sizeAttacker = 1;
		int sizeControl = 1;
		int sizeMust = 12;
		
		

		File m1 = new File(GenerateFSM.createModalSpec(sizeState, sizeMarked, sizeEvents, sizePaths, sizePrivate, sizeUnobserv, sizeAttacker, sizeControl, sizeMust, "modalSpec1", MAC_WORKING_FOLDER));
		File m2 = new File(GenerateFSM.createModalSpec(sizeState, sizeMarked, sizeEvents, sizePaths, sizePrivate, sizeUnobserv, sizeAttacker, sizeControl, sizeMust, "modalSpec2", MAC_WORKING_FOLDER));

		ModalSpecification mod1 = new ModalSpecification(m1, "mod1");
		FSMToDot.createTikZFromFSM(mod1, MAC_WORKING_FOLDER + "out_test_1", MAC_WORKING_FOLDER, MAC_CONFIG_FILE_PATH);
		FSMToDot.createImgFromFSM(mod1, MAC_WORKING_FOLDER + "out_test_im_1", MAC_WORKING_FOLDER, MAC_CONFIG_FILE_PATH);
		
	/*
		ModalSpecification mod2 = new ModalSpecification(m2, "mod2");
				
		ModalSpecification mod3 = mod1.getGreatestLowerBound(mod2);

		if(mod3.getTransitions().getTransitions(mod3.getInitialState()).size() > 1) {
			FSMToDot.createImgFromFSM(mod1, MAC_WORKING_FOLDER + "_img_1",  MAC_WORKING_FOLDER, MAC_CONFIG_FILE_PATH);
		  	FSMToDot.createImgFromFSM(mod2, MAC_WORKING_FOLDER + "_img_2",  MAC_WORKING_FOLDER, MAC_CONFIG_FILE_PATH);
		  	FSMToDot.createImgFromFSM(mod3, MAC_WORKING_FOLDER + "_img_3",  MAC_WORKING_FOLDER, MAC_CONFIG_FILE_PATH);
		}
	/*
		System.out.println("A");
		File f = new File(GenerateFSM.createNewModalSpecification(10, 4, 3, 2, 1, 2, 3, 1, 2, false, "fileName", MAC_WORKING_FOLDER));
		System.out.println("A");
		DetObsContFSM fsm = new NonDetObsContFSM(f, "fsm").buildObserver();
		System.out.println("A");
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
