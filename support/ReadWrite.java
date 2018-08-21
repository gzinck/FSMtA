package support;

import support.map.TransitionFunction;
import support.map.StateMap;
import support.map.EventMap;
import support.transition.*;
import support.Event;
import support.State;
import java.util.*;
import java.io.*;

/**
 * This class is used for conversions between FSM objects and their Text File representations for reading/writing.
 * 
 * This class is a part of the support package.
 * 
 * @author Mac Clevinger and Graeme Zinck 
 */

public class ReadWrite <T extends Transition>{

//---  Operations   ---------------------------------------------------------------------------
	
	/**
	 * This method converts the data structures configuring the provided FSM to a File format.
	 * 
	 * @param - String object representing the File Path to write the converted FSM to.
	 * @param - String object representing the pre-calculated special attributes that are written before any Transitions.
	 * @param - TransitionFunction<<r>T> object representing the Transitions that are written after the Special Attributes.
	 * @return - Returns a boolean value representing the result of this method's attempt to write to the File.
	 */
	
	public boolean writeToFile(String filePath, String special, TransitionFunction<T> transF, String ext) {
		try {
			File f = new File(filePath + ext);
			f.delete();
			RandomAccessFile raf = new RandomAccessFile(f, "rw");
			try {
			  raf.writeBytes(special);
			  String build = "";
			  for(State state1 : transF.getStates()) {
				for(T trans : transF.getTransitions(state1)) {
					for(State state2 : trans.getTransitionStates()) {
						build += state1.getStateName() + " " + state2.getStateName() + " " + trans.getTransitionEvent().getEventName() + "\n";
				  }
				}
			  }
			  if(build.length() > 0)
			  	build = build.substring(0, build.length()-1);
			  raf.writeBytes(build);
			  raf.close();
			}
			catch(IOException e1) {
				e1.printStackTrace();
				return false;
			}
			return true;
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * This method reads from the provided file information that is used to fill the sets of States,
	 * Events, and Transitions that are passed to this method, returning the additional information
	 * that each class handles separately. (Initial States, Marked States, etc.)
	 * 
	 * @param states - StateMap object that represents the empty set of States to be filled.
	 * @param events - EventMap object that represents the empty set of Events to be filled.
	 * @param transitions - TransitionFunction<<r>T> object that represents the empty set of Transitions to be filled.
	 * @param file - File object that holds the provided information instructing how to construct the FSM object.
	 * @return - Returns an ArrayList<<r>ArrayList<<r>String>> object that contains the additional information about this FSM object based on its type.
	 */
	
	public ArrayList<ArrayList<String>> readFromFile(StateMap states, EventMap events, TransitionFunction<T> transitions, File file){
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
				if(in.length < 3)
					break;
				State fromState = states.addState(in[0]);
				State toState = states.addState(in[1]);
				Event event = events.addEvent(in[2]);
				
				// See if there is already a transition with the event...
				ArrayList<T> thisTransitions = transitions.getTransitions(fromState);
				boolean foundTransition = false;
				if(thisTransitions != null) {
					Iterator<T> itr = thisTransitions.iterator();
					while(!foundTransition && itr.hasNext()) {
						T t = itr.next();
						if(t.getTransitionEvent().equals(event)) {
							t.setTransitionState(toState);
							foundTransition = true;
						} // if equal
					} // for every transition
				} // if not null
				if(!foundTransition) {
					T outbound = transitions.getEmptyTransition();
					outbound.setTransitionEvent(event);
					outbound.setTransitionState(toState);
					transitions.addTransition(fromState, outbound);
				} // if did not find transition
			} // while sc has next line
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
