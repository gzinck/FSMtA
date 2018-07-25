package test;

import fsm.*;
import fsm.attribute.Observability;
import graphviz.FSMToDot;
import support.GenerateFSM;
import support.attribute.EventObservability;
import support.transition.DetTransition;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.Test;

public class DontLookAtMe {

	private static final String GRAEME_WORKING_FOLDER = "/Users/graemezinck/Documents/OneDrive/Documents/Work/2018 Summer Research/GraphViz/";
	private static final String GRAEME_CONFIG_FILE_PATH = "/Users/graemezinck/Documents/OneDrive/Documents/Personal/Eclipse Workspace/Summer Research/config.properties";
	
	private static final String MAC_WORKING_FOLDER = "/Users/mac/Documents/TestGraph/";
	private static final String MAC_CONFIG_FILE_PATH = "/Users/mac/Documents/FSM-Implementation-2/config.properties";
	
	public void modalTest1() {
		DetObsContFSM fsm1 = new DetObsContFSM("OK");
		fsm1.addTransition("1", "a", "2");
		fsm1.addTransition("2", "b", "3");
		fsm1.addTransition("1", "b", "4");
		fsm1.addInitialState("1");
		fsm1.toggleMarkedState("3");
		fsm1.setEventObservability("a", false);
		FSMToDot.createImgFromFSM(fsm1, GRAEME_WORKING_FOLDER + "originalFSM", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
		FSM obs = fsm1.buildObserver();
		FSMToDot.createImgFromFSM(obs, GRAEME_WORKING_FOLDER + "observer", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
		
		ModalSpecification ms = new ModalSpecification("OK");
		ms.addState("1");
		ms.addInitialState("1");
		ms.addTransition("1", "b", "2");
		ms.addMustTransition("2", "b", "3");
		ms.toggleMarkedState("b");
		FSMToDot.createImgFromFSM(ms, GRAEME_WORKING_FOLDER + "testms", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
		DetObsContFSM supervisor = ms.makeOptimalSupervisor(fsm1);
		FSMToDot.createImgFromFSM(supervisor, GRAEME_WORKING_FOLDER + "supervisor", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
		
		DetObsContFSM specFSM = ms.getUnderlyingFSM();
		FSM product = obs.product(specFSM);
		FSMToDot.createImgFromFSM(product, GRAEME_WORKING_FOLDER + "product", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
	}
	
	public void modalTest2() {
		NonDetObsContFSM fsm1 = new NonDetObsContFSM("OK");
		fsm1.addTransition("1", "b", "3");
		fsm1.addTransition("1", "a", "2");
		fsm1.addTransition("2", "b", "4");
//		fsm1.addTransition("4", "c", "5");
		fsm1.addInitialState("1");
		fsm1.toggleMarkedState("5");
		fsm1.toggleMarkedState("3");
		
		fsm1.setEventObservability("a", false);
		FSMToDot.createImgFromFSM(fsm1, GRAEME_WORKING_FOLDER + "originalFSM", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
		FSM obs = fsm1.buildObserver();
		FSMToDot.createImgFromFSM(obs, GRAEME_WORKING_FOLDER + "observer", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
		
		ModalSpecification ms = new ModalSpecification("OK");
		ms.addState("1");
		ms.addInitialState("1");
		ms.addTransition("1", "b", "2");
		ms.addMustTransition("2", "c", "3");
		ms.toggleMarkedState("3");
		ms.toggleMarkedState("2");
		FSMToDot.createImgFromFSM(ms, GRAEME_WORKING_FOLDER + "testms", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
		DetObsContFSM supervisor = ms.makeOptimalSupervisor(fsm1);
		FSMToDot.createImgFromFSM(supervisor, GRAEME_WORKING_FOLDER + "supervisor", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
		
		DetObsContFSM specFSM = ms.getUnderlyingFSM();
		FSM product = obs.product(specFSM);
		FSMToDot.createImgFromFSM(product, GRAEME_WORKING_FOLDER + "product", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
	}
	
	public void modalTest3() {
		File f1 = new File("/Users/graemezinck/Documents/OneDrive/Documents/Work/2018 Summer Research/GraphViz/coffee-machine.fsm");
		DetObsContFSM fsm1 = new DetObsContFSM(f1, "OK");
		FSMToDot.createImgFromFSM(fsm1, GRAEME_WORKING_FOLDER + "plant", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
		
		ModalSpecification ms = new ModalSpecification("OK");
		ms.addMustTransition("s0", "?E", "s1");
		ms.addMustTransition("s1", "?T", "s2");
		ms.addTransition("s1", "?C", "s3");
		ms.addTransition("s3", "!C", "s0");
		ms.addTransition("s2", "!T", "s0");
		ms.addTransition("s3", "!E", "s5");
		ms.addTransition("s2", "!E", "s5");
		ms.addTransition("s5", "?E", "s4");
		ms.addTransition("s4", "?T", "s2");
		ms.addTransition("s4", "?C", "s3");
		
		ms.toggleMarkedState("s0");
		ms.toggleMarkedState("s5");
		ms.addInitialState("s0");
		FSMToDot.createImgFromFSM(ms, GRAEME_WORKING_FOLDER + "specification", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
		
		DetObsContFSM supervisor = ms.makeOptimalSupervisor(fsm1);
		FSMToDot.createImgFromFSM(supervisor, GRAEME_WORKING_FOLDER + "supervisor", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
	}
	
	private void modalTest4() {
		// Test for the modal specification lower bound operation
		File f1 = new File("/Users/graemezinck/Documents/OneDrive/Documents/Work/2018 Summer Research/GraphViz/ModalSpecTests/modal1.mdl");
		ModalSpecification ms1 = new ModalSpecification(f1, "OK");
		File f2 = new File("/Users/graemezinck/Documents/OneDrive/Documents/Work/2018 Summer Research/GraphViz/ModalSpecTests/modal2.mdl");
		ModalSpecification ms2 = new ModalSpecification(f2, "OK");
		ms2.addMustTransition("2", "c", "4");
		ModalSpecification ms3 = ms1.getGreatestLowerBound(ms2);
		
		FSMToDot.createImgFromFSM(ms1, GRAEME_WORKING_FOLDER + "modal1", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
		FSMToDot.createImgFromFSM(ms2, GRAEME_WORKING_FOLDER + "modal2", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
		FSMToDot.createImgFromFSM(ms3, GRAEME_WORKING_FOLDER + "modal3", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
	}
	
	private void modalTest5() {
		// Test for the modal specification lower bound operation
		File f1 = new File("/Users/graemezinck/Documents/OneDrive/Documents/Work/2018 Summer Research/GraphViz/ModalSpecTests/modal3.mdl");
		ModalSpecification ms1 = new ModalSpecification(f1, "OK");
//		ms1.addMustTransition("1", "a", "2");
		ms1.addMustTransition("2", "b", "4");
		ms1.addMustTransition("4", "c", "5");
		ms1.addMustTransition("5", "d", "6");
		ModalSpecification ms2 = new ModalSpecification(f1, "OK");
		ModalSpecification ms3 = ms1.getGreatestLowerBound(ms2);
		
		FSMToDot.createImgFromFSM(ms1, GRAEME_WORKING_FOLDER + "modal1", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
		FSMToDot.createImgFromFSM(ms2, GRAEME_WORKING_FOLDER + "modal2", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
		FSMToDot.createImgFromFSM(ms3, GRAEME_WORKING_FOLDER + "modal3", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
	}
	
	private void modalTest6() {
		// Test for the modal specification lower bound operation
		ModalSpecification ms1 = new ModalSpecification("OK");
		ms1.addInitialState("1");
		ms1.addMustTransition("1", "a", "2");
		ModalSpecification ms2 = new ModalSpecification("OK");
		ms2.addTransition("1", "b", "3");
		ms2.addInitialState("1");
		ModalSpecification ms3 = ms1.getGreatestLowerBound(ms2);
		
		FSMToDot.createImgFromFSM(ms1, GRAEME_WORKING_FOLDER + "modal1", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
		FSMToDot.createImgFromFSM(ms2, GRAEME_WORKING_FOLDER + "modal2", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
		FSMToDot.createImgFromFSM(ms3, GRAEME_WORKING_FOLDER + "modal3", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
	}
	
	private void modalTest7() {
		// Test for the modal specification lower bound operation
		ModalSpecification ms1 = new ModalSpecification("OK");
		ms1.addInitialState("1");
		ms1.addMustTransition("1", "a", "2");
		ms1.addTransition("1", "b", "3");
//		ms1.addTransition("2", "b", "3");
		ModalSpecification ms2 = new ModalSpecification("OK");
		ms2.addInitialState("1");
		ms2.addTransition("1", "b", "2");
		ms2.addTransition("2", "b", "1");
		
		ModalSpecification ms3 = ms1.getGreatestLowerBound(ms2);
		
		FSMToDot.createImgFromFSM(ms1, GRAEME_WORKING_FOLDER + "modal1", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
		FSMToDot.createImgFromFSM(ms2, GRAEME_WORKING_FOLDER + "modal2", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
		FSMToDot.createImgFromFSM(ms3, GRAEME_WORKING_FOLDER + "modal3", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
	}
	
	private void supremalContSubTest1() {
		File f1 = new File("/Users/graemezinck/Documents/OneDrive/Documents/Work/2018 Summer Research/GraphViz/SupremalCont/FullLang.fsm");
		NonDetObsContFSM fsm1 = new NonDetObsContFSM(f1, "OK");
		File f2 = new File("/Users/graemezinck/Documents/OneDrive/Documents/Work/2018 Summer Research/GraphViz/SupremalCont/SmallLang.fsm");
		NonDetObsContFSM fsm2 = new NonDetObsContFSM(f2, "OK");
		
		NonDetObsContFSM fsm3 = fsm1.getSupremalControllableSublanguage(fsm2);
		FSMToDot.createImgFromFSM(fsm1, GRAEME_WORKING_FOLDER + "sbigger", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
		FSMToDot.createImgFromFSM(fsm2, GRAEME_WORKING_FOLDER + "smaller", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
		FSMToDot.createImgFromFSM(fsm3, GRAEME_WORKING_FOLDER + "supremal", GRAEME_WORKING_FOLDER, GRAEME_CONFIG_FILE_PATH);
	}
	
	@Test
	public void test() {
		modalTest3();
		// (int sizeStates, int sizeMarked, int sizeEvents, int sizePaths, int sizeInitial, int sizePrivate, int sizeUnobserv, int sizeControl, boolean nonDet, String name, String filePath)
//		File f1 = new File(GenerateFSM.createNewFSM(7, 3, 2, 2, 2, 1, 1, 2, true, "fileName1", GRAEME_WORKING_FOLDER));
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
		
//		System.out.println(ModalSpecification.getSpecificationState("(5,({1,2,{3,4}},4))").toString());
		
		
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
