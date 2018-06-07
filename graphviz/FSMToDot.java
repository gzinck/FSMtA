package graphviz;

import java.io.File;
import java.util.*;

import fsm.*;

/**
 * This class handles the transfer of an object from the fsm package to a .dot format
 * so that it may be visually represented as a graph via graphviz, producing a .jpg
 * image at a hard-coded location using a name given at the time of creation.
 * 
 * This class is a part of the support package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 *
 */

public class FSMToDot {
	
//--- Operations  -------------------------------------------------------------------------
	
	/**
	 * This method takes any generic object in the fsm package via descendence from DeterministicFSM
	 * and calls its conversion method to dot-String-format and generates a .jpg image from it using
	 * the GraphViz library.
	 * 
	 * @param fsm - A generic FSM object in the family descended from DeterministicFSM.
	 * @param path - A String denoting the path to which the file should be saved, including its name.
	 * @param workingPath - A String denoting the path to the GraphViz working directory.
	 * @param configPath - A String denoting the path to the GraphViz config file.
	 */
	
	public static <FSM extends DeterministicFSM> void createGraphFromFSM(FSM fsm, String path, String workingPath, String configPath){
		fsm.aggregateAndSortHashMap();
	    GraphViz gv=new GraphViz(workingPath, configPath);
	    gv.addln(gv.start_graph());
	    gv.add(convertFSMToDot(fsm));
	    gv.addln(gv.end_graph());
	    String type = "jpg";
	    //gv.increaseDpi();
	    gv.decreaseDpi();
	    gv.decreaseDpi();
	    File out = new File(path + "." + type); 
	    gv.writeGraphToFile( gv.getGraph( gv.getDotSource(), type ), out );
	}
	
	/**
	 * This method handles the conversion of a FSM to a String in .dot format via
	 * a ConvertToDot BiConsumer object in the support package.
	 * 
	 * @param fsm - A generic FSM object to be converted into the dot-String form.
	 * @return - Returns a String of the dot-String format from the FSM object passed in.
	 */
	
	public static <FSM extends DeterministicFSM> String convertFSMToDot(FSM fsm) {
		HashMap<String, String> fsmForm = fsm.getFSMHashMap();		//Gets the HashMap of the DeterministicFSM
		ConvertToDot makeDot = new ConvertToDot(fsm.getSpecialArrays());				//Makes a new object of the ConvertToDot class
		fsmForm.forEach(makeDot);		//Iterates through all values in the HashMap to adjust an instance variable in ConvertToDot
		return(makeDot.getDotForm());		//Returns that instance variable as the dot-String form of the DeterministicFSM
	}

}