package support;

import java.util.*;
import support.event.*;
import support.transition.*;
import support.State;
import java.io.*;

/**
 * This class is used for conversions between FSM objects and their Text File representations for reading/writing.
 * 
 * This class is a part of the support package.
 * 
 * @author Mac Clevinger and Graeme Zinck 
 */

public class ReadWrite<S extends State, T extends Transition<S, E>, E extends Event> {

//---  Operations   ---------------------------------------------------------------------------
	
	/**
	 * This method converts the data structures configuring the provided FSM to a File format.
	 * 
	 * @return - Returns a boolean value representing the result of this method'ss attempt to write to the File.
	 */
	
	public boolean writeToFile() {
		
		return false;
	}
	
	/**
	 * This method reads from the provided file information that is used to fill the sets of States,
	 * Events, and Transitions that are passed to this method, returning the additional information
	 * that each class handles separately. (Initial States, Marked States, etc.)
	 * 
	 * @param states - StateMap<<s>S> object that represents the empty set of States to be filled.
	 * @param events - EventMap<<s>E> object that represents the empty set of Events to be filled.
	 * @param transitions - TransitionFunction<s>S, T, E> object that represents the empty set of Transitions to be filled.
	 * @param file - File object that holds the provided information instructing how to construct the FSM object.
	 * @return - Returns an ArrayList<<s>ArrayList<<s>String>> object that contains the additional information about this FSM object based on its type.
	 */
	
	public ArrayList<ArrayList<String>> readFromFile(StateMap<S> states, EventMap<E> events, TransitionFunction<S, T, E> transitions, File file){
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
			  T outbound = transitions.getEmptyTransition();
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
