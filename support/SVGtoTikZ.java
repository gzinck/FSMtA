package support;

import java.io.*;
import java.util.*;

public class SVGtoTikZ {

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
	
	public static String drawCircleNode(double x, double y, double rad, boolean mark, String name, String color) {
		return (mark ? "\\filldraw [" + color + ", very thick, fill=white] (" + x + "," + y + ") circle [radius=" + (rad*1.2) + "] node {" + name + "};\n" : "")
				+ "\\filldraw [" + color + ", very thick, fill=" + (name.equals("") ? "black" : "white") + "] (" + x + "," + y + ") circle [radius=" + rad + "] node {" + name + "};\n";
	}
	
	public static String drawEdges(ArrayList<Double> path, String color, boolean dotted) {
		StringBuilder sb = new StringBuilder();
		sb.append("\\draw [color = " + color + ", thick, " + (dotted ? "dash dot" : "") + "] (" + path.get(0) + "," + path.get(1) + ")\n ");
		for(int i = 2; i < path.size(); i+=2) 
			sb.append("to (" + path.get(i) + "," + path.get(i+1) + ") \n ");
		return sb.toString() + "; \n";
	}
	
	public static String drawPolygon(ArrayList<Double> path, String color) {
		StringBuilder sb = new StringBuilder();
		sb.append("\\draw [color = " + color + ", thick, fill=" + color + "] (" + path.get(0) + "," + path.get(1) + ")\n ");
		for(int i = 2; i < path.size(); i+=2)
			sb.append("to (" + path.get(i) + "," + path.get(i+1) + ") \n ");
		return sb.toString()  + "; \n ";
	}
	
	public static String drawLabel(double x, double y, String name) {
		return "\\draw [black](" + x + "," + y + ") node {" + name + "}; \n";
	}
	
}
