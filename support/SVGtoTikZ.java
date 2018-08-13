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
			
			int state = -1;
			double[] values = new double[0];
			ArrayList<Double> edgePath = new ArrayList<Double>();
			ArrayList<Double> pathEdge = new ArrayList<Double>();
			
			String name = "";
			String color = "";
			
			while(in != null) {
			  if(state == -1) {
				if(in.matches("<g (.*)")) {
					if(in.matches("(.*)class=\"node\"(.*)")) {
						state = 0;
						name = in.replaceAll("(.*)<title>", "").replaceAll("</titl(.*)", "").replaceAll("_(.*)_(.*)", "");
						values = new double[4];
					}
					else if(in.matches("(.*)class=\"edge\"(.*)")) {
						state = 1;
						name = in.replaceAll("<(.*)>", "").replaceAll("&#45;&gt;"," ");
						values = new double[3];
						edgePath = new ArrayList<Double>();
						pathEdge = new ArrayList<Double>();
					}
					in = br.readLine();
					continue;
				}
				else if(in.matches("<svg(.*)")) {
					width = 10.0 / Double.parseDouble(in.replaceAll("(.*)width=\"", "").replaceAll("pt\"(.*)", ""));
					height = 10.0 / Double.parseDouble(in.replaceAll("(.*)height=\"", "").replaceAll("pt\"(.*)", ""));
					width = width > 0.015 ? width : 0.015;
					height = height > 0.015 ? height : 0.015;
					in = br.readLine();
					continue;
				}
			  }
			  switch(state) {
			  	case -1: break;
			  	case 0: 
			  		if(values[3] != 0)
			  			break;
			  		values[0] = width * Double.parseDouble(in.replaceAll("(.*)cx=\"", "").replaceAll("\"(.*)", ""));
			  		values[1] = height * (height - Double.parseDouble(in.replaceAll("(.*)cy=\"", "").replaceAll("\"(.*)", "")));
			  		values[2] = (width < height ? width : height) *  Double.parseDouble(in.replaceAll("(.*)rx=\"", "").replaceAll("\"(.*)", ""));
			  		color = in.replaceAll("(.*)stroke=\"", "").replaceAll("\"(.*)", "");
			  		in = br.readLine();
			  		if(in.matches("<e(.*)")) {
			  			values[3] = -1;
			  			in = br.readLine();
			  		}
			  		else
			  			values[3] = 1;
			  		break;
			  	case 1:
			  		if(values[2] == 0) {
			  			String[] points = in.replaceAll("(.*)d=\"M", "").replaceAll("\"/(.*)","").replaceAll("C"," ").split(" ");
			  			for(String s : points) {
			  				pathEdge.add(width * Double.parseDouble(s.split(",")[0]));
			  				pathEdge.add(height * (height - Double.parseDouble(s.split(",")[1])));
			  			}
			  			in = br.readLine();
			  			points = in.replaceAll("(.*)points=\"", "").replaceAll("\"(.*)", "").split(" ");
			  			for(String s : points) {
			  				edgePath.add(width * Double.parseDouble(s.split(",")[0]));
			  				edgePath.add(height * (height - Double.parseDouble(s.split(",")[1])));
			  			}
			  			in = br.readLine();
			  		}
			  		if (!in.equals("</g>")){
			  			values[0] = width * Double.parseDouble(in.replaceAll("(.*)x=\"", "").replaceAll("\" y(.*)", ""));
			  			values[1] = height * (height - Double.parseDouble(in.replaceAll("(.*) y=\"", "").replaceAll("\" f(.*)", "")));
			  			name = in.replaceAll("(.*)\">", "").replaceAll("<(.*)", "");
			  			values[2] = -1;
			  			in = br.readLine();
			  		}
			  		break;
			  	case 2:
			  		
			  		break;
			  	default:
			  }
			  System.out.println(in);
			  if(in.equals("</g>")) {
				  System.out.println();
				  switch(state) {
				  	case -1:
					  break;
				  	case 0: 
					  nodes.add(drawCircleNode(values[0], values[1], values[2], values[3] == -1, name, color)); 
				  	  break;
				  	case 1:
				  	  edges.add(drawEdges(pathEdge));
				  	  edges.add(drawArrowAndLabel(values[0], values[1], name, edgePath));
					  break;
				  	case 2:
				  	  
					  break;
				  	default:
					  break;
				  }
				  state = -1;
				  name = "";
				  color = "";
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
	
	public static String drawEdges(ArrayList<Double> path) {
		StringBuilder sb = new StringBuilder();
		sb.append("\\draw [thick] (" + path.get(0) + "," + path.get(1) + ")\n ");
		for(int i = 2; i < path.size(); i+=2) 
			sb.append("to (" + path.get(i) + "," + path.get(i+1) + ") \n ");
		return sb.toString() + "; \n";
	}
	
	public static String drawArrowAndLabel(double x, double y, String name, ArrayList<Double> path) {
		StringBuilder sb = new StringBuilder();
		sb.append("\\draw [thick, fill=black] (" + path.get(0) + "," + path.get(1) + ")\n ");
		for(int i = 2; i < path.size(); i+=2)
			sb.append("to (" + path.get(i) + "," + path.get(i+1) + ") \n ");
		return sb.toString()  + "; \n " + "\\draw (" + x + "," + y + ") node {" + name + "}; \n";
	}
	
}
