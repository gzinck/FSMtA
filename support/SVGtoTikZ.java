package support;

import java.io.*;
import java.util.*;

/**
 * This class serves to provide its user with the ability to convert a .svg describing
 * a directed graph (as provided by the GraphViz software explicitly) into the .tikz
 * format, usable with programs such as LaTEX for its inclusion and manipulation.
 * 
 * It was originally created as a supplement to the FSMtA software developed at Mount
 * Allison University in the summer of 2018 by Mac Clevinger and Graeme Zinck, but is
 * usable independent from that software.
 * 
 * For the needs described by that software this works fine, however there are cases of
 * input that need to be handled to ensure its robustness with varied usage. If you happen
 * to have documentation on SVG and TikZ file formats, please send those resources to
 * Mac Clevinger (aka: Reithger).
 * 
 * @author Mac Clevinger and Graeme Zinck
 *
 */

public class SVGtoTikZ {

	/**
	 * This method converts a provided File (assumed to be in the .svg format) into the .tikz format
	 * for use in programs such as LaTEX. 
	 * 
	 * @param f - File object representing a .svg file to convert to .tikz.
	 * @param path - String object representing a file path to write the new file to.
	 * @return - Returns a File object representing the newly created .tikz file.
	 */
	
	public static File convertSVGToTikZ(File f, String path) {
		File out = new File(path + ".tikz");
		out.delete();
		try {
			RandomAccessFile raf = new RandomAccessFile(out, "rw");
			
			raf.writeBytes("\\begin{figure}[h!] \n \\begin{center} \n \\begin{tikzpicture} \n");
			
			BufferedReader br = new BufferedReader(new FileReader(f.getAbsolutePath()));
			String in = br.readLine();
			
			ArrayList<String> nodes = new ArrayList<String>();
			ArrayList<String> edges = new ArrayList<String>();
			
			double width = 0.0;
			double height = 0.0;
			
			while(in != null) {
				if(in.matches("<svg(.*)")) {
					width = 10.0 / Double.parseDouble(in.replaceAll("(.*)width=\"", "").replaceAll("pt\"(.*)", ""));
					height = 10.0 / Double.parseDouble(in.replaceAll("(.*)height=\"", "").replaceAll("pt\"(.*)", ""));
					width = width > 0.015 ? width : 0.015;
					height = height > 0.015 ? height : 0.015;
				}
				else if(in.matches("(.*)class=\"node\"(.*)")) {	
					String name = in.replaceAll("(.*)<title>", "").replaceAll("</titl(.*)", "").replaceAll("_(.*)_(.*)", "");
					double[] values = new double[3];
					boolean doubleCircle = false;
					in = br.readLine();
			  		values[0] = width * Double.parseDouble(in.replaceAll("(.*)cx=\"", "").replaceAll("\"(.*)", ""));
			  		values[1] = height * (height - Double.parseDouble(in.replaceAll("(.*)cy=\"", "").replaceAll("\"(.*)", "")));
			  		values[2] = (width < height ? width : height) *  Double.parseDouble(in.replaceAll("(.*)rx=\"", "").replaceAll("\"(.*)", ""));
			  		String color = in.replaceAll("(.*)stroke=\"", "").replaceAll("\"(.*)", "");
			  		in = br.readLine();
			  		if(in.matches("<e(.*)")) {
			  			doubleCircle = true;
			  		}
					nodes.add(drawCircleNode(values[0], values[1], values[2], doubleCircle, name, color)); 
				}
				else if(in.matches("(.*)class=\"edge\"(.*)")) {	
					String name = "";
					in = br.readLine();
					
					ArrayList<Double> pathEdge = new ArrayList<Double>();
					
		  			String[] points = in.replaceAll("(.*)d=\"M", "").replaceAll("\"/(.*)","").replaceAll("C"," ").split(" ");
		  			for(String s : points) {
		  				pathEdge.add(width * Double.parseDouble(s.split(",")[0]));
		  				pathEdge.add(height * (height - Double.parseDouble(s.split(",")[1])));
		  			}
			  		String color = in.replaceAll("(.*)stroke=\"", "").replaceAll("\"(.*)", "");
			  		boolean dotted = in.contains("stroke-dasharray");
		  			edges.add(drawEdges(pathEdge, color, dotted));
		  			pathEdge.clear();
		  			in = br.readLine();
		  			
		  			while(!in.equals("</g>")) {
			  			if(in.matches("<ellipse(.*)")) {
			  				double[] values = new double[3];
					  		values[0] = width * Double.parseDouble(in.replaceAll("(.*)cx=\"", "").replaceAll("\"(.*)", ""));
					  		values[1] = height * (height - Double.parseDouble(in.replaceAll("(.*)cy=\"", "").replaceAll("\"(.*)", "")));
					  		values[2] = (width < height ? width : height) *  Double.parseDouble(in.replaceAll("(.*)rx=\"", "").replaceAll("\"(.*)", ""));
					  		nodes.add(drawCircleNode(values[0], values[1], values[2], false, " ", color));
			  			}
			  			else if(in.matches("<polygon(.*)")) {
				  			points = in.replaceAll("(.*)points=\"", "").replaceAll("\"(.*)", "").split(" ");
				  			for(String s : points) {
				  				pathEdge.add(width * Double.parseDouble(s.split(",")[0]));
				  				pathEdge.add(height * (height - Double.parseDouble(s.split(",")[1])));
				  			}
				  			edges.add(drawPolygon(pathEdge, color));
				  			pathEdge.clear();
			  			}
			  			else if(in.matches("<text(.*)")) {
			  				double[] values = new double[2];
			  				values[0] = width * Double.parseDouble(in.replaceAll("(.*)x=\"", "").replaceAll("\" y(.*)", ""));
				  			values[1] = height * (height - Double.parseDouble(in.replaceAll("(.*) y=\"", "").replaceAll("\" f(.*)", "")));
				  			name = in.replaceAll("(.*)\">", "").replaceAll("<(.*)", "");
				  			edges.add(drawLabel(values[0], values[1], name));
			  			}
		  				in = br.readLine();
		  			}
				}
			  in = br.readLine();
			}
			for(String s : edges)
				raf.writeBytes(s);
			for(String s : nodes)
				raf.writeBytes(s);
			raf.writeBytes("\\end{tikzpicture} \n \\end{center} \n \\end{figure} \n");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return out;
	}
	
	/**
	 * This helper method takes in the relevant information necessary to draw Node objects in the .tikz
	 * format (note: Not the node objects in .tikz that already exist, but nodes from the Discrete Event
	 * Systems that this is built in mind for.)
	 * 
	 * It can be used for arbitrary circles with manipulation of its parameters. (Empty String).
	 * 
	 * @param x - double value representing the location to draw to in the x coordinate (center of circle).
	 * @param y - double value representing the location to draw to in the y coordinate (center of circle).
	 * @param rad - double value representing the radius of the ensuing circle to be drawn.
	 * @param mark - boolean value describing whether or not to have a double-bordered circle or not.
	 * @param name - String object describing what to label the circle as.
	 * @param color - String object describing what color to draw the circle in.
	 * @return - Returns a String object representing the .tikz formatted version of the node described by the input.
	 */
	
	public static String drawCircleNode(double x, double y, double rad, boolean mark, String name, String color) {
		return (mark ? "\\filldraw [" + color + ", very thick, fill=white] (" + x + "," + y + ") circle [radius=" + (rad*1.2) + "] node {" + name + "};\n" : "")
				+ "\\filldraw [" + color + ", very thick, fill=" + (name.equals("") ? "black" : "white") + "] (" + x + "," + y + ") circle [radius=" + rad + "] node {" + name + "};\n";
	}
	
	/**
	 * This helper method takes in the relevant information necessary to draw an arbitrary edge in
	 * coordinate space. (Such as denoting the connections between nodes in a Discrete Event System's
	 * graph.) It permits multiple points along a single route, as described by information derived
	 * from the .svg file's format.
	 * 
	 * @param path - ArrayList<<r>Double> object containing sets of point denoting x and y locations of a route.
	 * @param color - String object representing the Color to draw the line in.
	 * @param dotted - boolean value denoting whether or not the line is dotted.
	 * @return - Returns a String object representing the .tikz formatted version of the edge described by the input.
	 */
	
	public static String drawEdges(ArrayList<Double> path, String color, boolean dotted) {
		StringBuilder sb = new StringBuilder();
		sb.append("\\draw [color = " + color + ", thick, " + (dotted ? "dash dot" : "") + "] (" + path.get(0) + "," + path.get(1) + ")\n ");
		for(int i = 2; i < path.size(); i+=2) 
			sb.append("to (" + path.get(i) + "," + path.get(i+1) + ") \n ");
		return sb.toString() + "; \n";
	}
	
	/**
	 * This helper method takes in the relevant information necessary to draw an arbitrary polygon in
	 * coordinate space. (Such as drawing an arrow head to denote direction of an edge in a Discrete
	 * Event System's graph.)
	 * 
	 * @param path - ArrayList<<r>Double> object containing sets of x, y points denoting corners of the polygon.
	 * @param color - String object describing what color to draw the polygon in.
	 * @return - Returns a String object representing the .tikz formatted version of the polygon described by the input.
	 */
	
	public static String drawPolygon(ArrayList<Double> path, String color) {
		StringBuilder sb = new StringBuilder();
		sb.append("\\draw [color = " + color + ", thick, fill=" + color + "] (" + path.get(0) + "," + path.get(1) + ")\n ");
		for(int i = 2; i < path.size(); i+=2)
			sb.append("to (" + path.get(i) + "," + path.get(i+1) + ") \n ");
		return sb.toString()  + "; \n ";
	}
	
	/**
	 * This helper method takes in the relevant information necessary to draw a label to an arbitrary location
	 * in coordinate space. (Such as labeling an edge in a Discrete Event System's graph.)
	 * 
	 * @param x - double value representing the location to draw to in the x coordinate.
	 * @param y - double value representing the location to draw to in the y coordinate.
	 * @param name - String object representing what to write at the defined location.
	 * @return - Returns a String object representing the .tikz formatted version of the label described by the input.
	 */
	
	public static String drawLabel(double x, double y, String name) {
		return "\\draw [black](" + x + "," + y + ") node {" + name + "}; \n";
	}
	
}
