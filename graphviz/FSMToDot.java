package graphviz;

import java.io.File;
import fsm.*;
import support.SVGtoTikZ;

/**
 * This class handles the transfer of an object from the fsm package to a .dot format
 * so that it may be visually represented as a graph via graphviz, producing a .jpg
 * image at a hard-coded location using a name given at the time of creation.
 * 
 * In addition, it also serves to create an .svg file type as well as convert that
 * file into a .tikz file for use in LaTEX or other programs that read through that.
 * 
 * TODO: Config File is referenced straight from the package, definite location, do not need file pathing.
 * 
 * This class is a part of the graphviz package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 *
 */

public class FSMToDot {
	
//---  Operations   ---------------------------------------------------------------------------
	
	/**
	 * This method takes any object extending the TransitionSystem class and converts it to the 
	 * dot-String-format, using that to generates a .jpg image from it using the GraphViz library.
	 * 
	 * @param fsm - A generic TransitionSystem object that will be converted into a .jpg image.
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
	
	/**
	 * This method creates a file representing the provided TransitionSystem object in the .svg format,
	 * saving it to a location as defined by the caller. (.svg format is a graphical view of the graph,
	 * but is composed of a series of instructions on how to draw the graph to be interpreted by another
	 * program. This permits its conversion in another method in this class.)
	 * 
	 * @param fsm - A TransitionSystem extending object that will be converted into .svg format.
	 * @param path - A String object denoting the path to which the file should be saved, including its name.
	 * @param workingPath - A String object denoting the path to the GraphViz working directory.
	 * @param configPath - A String object denoting the path to the GraphViz config file.
	 */
	
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
	
	/**
	 * This method converts a provided file in the .svg format into a file in the .tikz
	 * format for use with LaTEX programs. It calls a support class that can be used disjoint
	 * from this project to perform the same feat.
	 * 
	 * @param svgFile - A File object containing a TransitionSystem described in the .svg format.
	 * @param path - A String object representing the file path to save the new file to.
	 */
	
	public static void createTikZFromSVG(File svgFile, String path) {
		SVGtoTikZ.convertSVGToTikZ(svgFile, path);
	}
	
	/**
	 * This method takes in a TransitionSystem object and converts it into a representative file
	 * of the .tikz format; it firts converts the object to .svg from which it is converted to .tikz
	 * via the createTikZFromSVG() method included in this class. (Deleting the interim file.)
	 * 
	 * @param fsm - A TransitionSystem extending object that will be converted into .tikz format.
	 * @param path - A String object denoting the path to which the file should be saved, including its name.
	 * @param workingPath - A String object denoting the path to the GraphViz working directory.
	 * @param configPath - A String object denoting the path to the GraphViz config file.
	 */

	public static void createTikZFromFSM(TransitionSystem fsm, String path, String workingPath, String configPath) {
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
	    SVGtoTikZ.convertSVGToTikZ(out, path);
	    out.delete();
	}
	
}