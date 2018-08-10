package graphviz;

import java.io.File;
import fsm.*;

/**
 * This class handles the transfer of an object from the fsm package to a .dot format
 * so that it may be visually represented as a graph via graphviz, producing a .jpg
 * image at a hard-coded location using a name given at the time of creation.
 * 
 * This class is a part of the graphviz package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 *
 */

public class FSMToDot {
	
//---  Operations   ---------------------------------------------------------------------------
	
	/**
	 * This method takes any generic object in the fsm package and calls its conversion
	 * method to dot-String-format and generates a .jpg image from it using
	 * the GraphViz library.
	 * 
	 * @param fsm - A generic FSM object in the family descended from the abstract class FSM.
	 * @param path - A String object denoting the path to which the file should be saved, including its name.
	 * @param workingPath - A String object denoting the path to the GraphViz working directory.
	 * @param configPath - A String object denoting the path to the GraphViz config file.
	 */
	
	public static void createImgFromFSM(TransitionSystem fsm, String path, String workingPath, String configPath){
	    GraphViz gv=new GraphViz(workingPath, configPath);
	    gv.addln(gv.start_graph());
	    gv.add(fsm.makeDotString());
	    gv.addln(gv.end_graph());
	    String type = "jpg";
	    gv.increaseDpi();
	    gv.increaseDpi();
	    gv.increaseDpi();
	    gv.increaseDpi();
	    File out = new File(path + "." + type);
	    gv.writeGraphToFile( gv.getGraph( gv.getDotSource(), type ), out );
	}
	
	public static void createSVGFromFSM(TransitionSystem fsm, String path, String workingPath, String configPath){
	    GraphViz gv=new GraphViz(workingPath, configPath);
	    gv.addln(gv.start_graph());
	    gv.add(fsm.makeDotString());
	    gv.addln(gv.end_graph());
	    String type = "svg";
	    gv.increaseDpi();
	    gv.increaseDpi();
	    gv.increaseDpi();
	    gv.increaseDpi();
	    File out = new File(path + "." + type); 
	    gv.writeGraphToFile( gv.getGraph( gv.getDotSource(), type ), out );
	}

}