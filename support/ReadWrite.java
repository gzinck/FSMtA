package support;

import fsm.*;
import java.util.*;
import support.event.*;
import support.transition.*;
import support.State;
import java.io.*;

/**
 * This class is used for conversions between FSM objects and their Text File representations
 * for reading/writing.
 * 
 * This class is a part of the support package.
 * 
 * @author Mac Clevinger and Graeme Zinck 
 */

public class ReadWrite<S extends State, E extends Event, T extends Transition> {

	
	public boolean writeToFile() {
		
		return false;
	}
	
	public ArrayList<ArrayList<String>> readFromFile(StateMap<S> states, EventMap<E> events, TransitionFunction<S, T> transitions, File file){
		try {
		  Scanner sc = new Scanner(file);
		  int numSpec = sc.nextInt();
		  ArrayList<ArrayList<String>> specialInfo = new ArrayList<ArrayList<String>>();
		  for(int i = 0; i < numSpec; i++) {
			  int numIndSpec = sc.nextInt(); sc.nextLine();
			  ArrayList<String> oneBatch = new ArrayList<String>();
			  for(int j = 0; j < numIndSpec; j++) {
				  oneBatch.add(sc.nextLine());
			  }
			  specialInfo.add(oneBatch);
		  }
		  while(sc.hasNextLine()) {
			  String[] in = sc.nextLine().split(" ");
			  S leading = states.addState(in[0]);
			  E your = events.addEvent(in[1]);
			  S target = states.addState(in[2]);
			  T outbound = transitions.getTransitionFunctionClassType().newInstance();
			  outbound.setTransitionEvent(your);
			  outbound.setTransitionState(target);
			  transitions.addTransition(leading, outbound);
		  }
		  sc.close();
		  return specialInfo;
		}
		catch(Exception e) {
			e.printStackTrace();
			System.out.println("Failure during File Reading");
			return null;
		}
		
	}
	
}
