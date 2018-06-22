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
	
	public boolean writeToFile(String filePath, String special, TransitionFunction<S, T, E> transF) {
		try {
			File f = new File(filePath + ".fsm");
			f.delete();
			RandomAccessFile raf = new RandomAccessFile(f, "rw");
			try {
			  raf.writeBytes(special);
			  String build = "";
			  for(S state1 : transF.transitions.keySet()) {
				for(T trans : transF.getTransitions(state1)) {
					for(S state2 : trans.getTransitionStates()) {
						build += state1.getStateName() + " " + state2.getStateName() + " " + trans.getTransitionEvent().getEventName() + "\n";
				  }
				}
			  }
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
				S fromState = states.addState(in[0]);
				S toState = states.addState(in[1]);
				E event = events.addEvent(in[2]);
				
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
