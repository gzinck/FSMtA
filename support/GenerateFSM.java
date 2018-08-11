package support;

import java.util.*;
import java.io.*;

/**
 * This class is used to generate files readable as Finite State Machines to the constructors
 * that accept file input from the classes in the fsm package. It does so randomly with no
 * oversight to handle strange productions.
 * 
 * The class creates a file in a location defined by a class constant and named howsoever the
 * user of this class wants; any usage of the class' production is then done by accessing that
 * location in your file system, given as a String returned by this method or by directly accessing
 * your file system yourself.
 * 
 * Important note: Must write to file the following:
 * # of special types
 * # of elements of special type 'n'
 * 	- the elements
 *  - repeat for all special types
 * All transitions (State, State, Event)
 * 
 * This class is a part of the support package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 *
 */

public class GenerateFSM {

//--- Constant Values  ------------------------------------------------------------------------
	
	/** String constant referenced for consistent naming practices of States*/
	private static final String ALPHABET_STATE = "0123456789";
	/** String constants referenced for consistent naming practices of Events*/
	private static final String ALPHABET_EVENT = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
//--- Instance Variables  ---------------------------------------------------------------------
	
	/** ArrayList<String> used to hold the produced event names for back-referencing when re-using*/
	private static ArrayList<String> events; 
	/** ArrayList<String> used to hold the produced state names for back-referencing when re-using*/
	private static ArrayList<String> states;
	
//--- Operations  -----------------------------------------------------------------------------
	
	/**
	 * This method generates a file corresponding to the nomenclature of an FSM's file input via the
	 * properties described by its parameters: Number of States, Marked States, Unique Events, Paths,
	 * Initial States, Private States, Unobservable Events, and Uncontrollable Events.
	 * 
	 * The produced FSM object will be randomized according to the bounds described by the provided arguments,
	 * saving the produced file to the location described by the input and returning the exact address of this
	 * new file as a String object.
	 * 
	 * File type: .fsm
	 * 
	 * @param sizeStates - int value describing how many States to include in the FSM.
	 * @param sizeMarked - int value describing how many Marked States to include in the FSM.
	 * @param sizeEvents - int value describing how many Unique Events to include in the FSM.
	 * @param sizePaths - int value describing the maximal number of Paths leading out from a given State in the FSM.
	 * @param sizeInitial - int value describing how many Initial States to include in the FSM.
	 * @param sizePrivate - int value describing how many Private States to include in the FSM.
	 * @param sizeUnobserv - int value describing how many Events to mark as Unobservable to the System in the FSM. 
	 * @param sizeAtacker - int value describing how many Events to mark as Unobservable to the Attacker in the FSM on top of those to the System.
	 * @param sizeControl - int value describing how many Events to mark as Uncontrollable in the FSM.
	 * @param nonDet - boolean value denoting whether or not the FSM is Deterministic or Non-Deterministic.
	 * @param name - String object used to denote the title of the File being generated.
	 * @param filePath - String object specifying where in the file system to place the file.
	 * @return - Returns a String describing the location of the generated File in the file system.
	 */

	public static String createNewFSM(int sizeStates, int sizeMarked, int sizeEvents, int sizePaths, int sizeInitial, int sizePrivate, int sizeUnobserv, int sizeAttacker, int sizeControl, boolean nonDet, String name, String filePath) {
		File f = new File(filePath + name + ".fsm");		//Creates a new file with a unique extension
		f.delete();											//If there already exists a file, remove it.
		events = new ArrayList<String>();					//Initiates the ArrayLists for usage
		states = new ArrayList<String>();
		String os = System.getProperty("os.name").split(" ")[0];
		String lineSkip = os.equals("Windows") ? (char)13 + "" + (char)10 : (char)10 + "";
		for(int i = 0; i < sizeEvents; i++) {				//Fills the ArrayList with requested number of Events immediately
			String newName = "";
			int copy = i;
			do {
				newName = ALPHABET_EVENT.charAt(copy%10) + newName;
				copy /= 10;
			} while(copy != 0);
			events.add(newName);
		}
		try{													//To catch IOExceptions
		  RandomAccessFile raf = new RandomAccessFile(f, "rw");	//Allows us to write to the file
		  Random rand = new Random();				//Create random object for randomness
		  raf.writeBytes("6");			//Initial, Marked, Private, Unobservable, TODO: Controllable
		  raf.writeBytes(lineSkip);
		  raf.writeBytes(("" + sizeInitial));
		  raf.writeBytes(lineSkip);
		  HashSet<Integer> initial = new HashSet<Integer>();
		  for(int i = 0; i < sizeInitial; i++) {
			  int rand1 = rand.nextInt(sizeStates);
			  while(initial.contains(rand1) && sizeInitial <= sizeStates)
				  rand1 = rand.nextInt(sizeStates);
			  initial.add(rand1);
			  raf.writeBytes(rand1+"");		//Pick a state at random to be initial states
			  raf.writeBytes(lineSkip);										//Again, redundancy!
		  }
		  raf.writeBytes(("" + sizeMarked));		//Strings are basically arrays of characters, which can trivially convert
		  raf.writeBytes(lineSkip);					//to bytes, so pass a String to the writeBytes command to write to file. (write 10 for line return)
		  HashSet<Integer> marked = new HashSet<Integer>();
		  for(int i = 0; i < sizeMarked; i++) {		//Now to decide the marked states, again randomly
			  int rand1 = rand.nextInt(sizeStates);
			  while(marked.contains(rand1) && sizeMarked <= sizeStates)
				  rand1 = rand.nextInt(sizeStates);
			  marked.add(rand1);
			  raf.writeBytes(rand1+"");		//Pick states at random to be marked
			  raf.writeBytes(lineSkip);										//This does allow for redundancies, which do nothing but waste space.
		  }
		  raf.writeBytes("" + sizePrivate);
		  raf.writeBytes(lineSkip);
		  HashSet<Integer> privacy = new HashSet<Integer>();
		  for(int i = 0; i < sizePrivate; i++) {
			  int rand1 = rand.nextInt(sizeStates);
			  while(privacy.contains(rand1) && sizePrivate <= sizeStates)
				  rand1 = rand.nextInt(sizeStates);
			  marked.add(rand1);
			  raf.writeBytes(rand1+"");
			  raf.writeBytes(lineSkip);
		  }
		  
		  HashSet<String> unsee = new HashSet<String>();
		  
		  raf.writeBytes(("" + sizeUnobserv));
		  raf.writeBytes(lineSkip);
		  for(int i = 0; i < sizeUnobserv; i++) {
			  String val = events.get(rand.nextInt(sizeEvents));
			  while(unsee.contains(val))
				  val = events.get(rand.nextInt(sizeEvents));
			  unsee.add(val);
			  raf.writeBytes(events.get(rand.nextInt(sizeEvents)));
			  raf.writeBytes(lineSkip);
		  }
		  
		  while(unsee.size() < sizeAttacker + sizeUnobserv) {
			  String val = events.get(rand.nextInt(sizeEvents));
			  while(unsee.contains(val))
				  val = events.get(rand.nextInt(sizeEvents));
			  unsee.add(val);
		  }
		  
		  raf.writeBytes(("" + (sizeAttacker + sizeUnobserv)));
		  raf.writeBytes(lineSkip);
		  for(String s : unsee)
			  raf.writeBytes(s + lineSkip);
		  
		  raf.writeBytes((""+sizeControl));
		  raf.writeBytes(lineSkip);
		  for(int i = 0; i < sizeControl; i++) {
			  raf.writeBytes(events.get(rand.nextInt(sizeEvents)));
			  raf.writeBytes(lineSkip);
		  }
		  for(int i = 0; i < sizeStates; i++) {		//Each state as defined by sizeStates is processed herein
			  String nom = "";						//Holds the State's name to differentiate between them
			  int copy = i;							//So that we may manipulate the value but not mess up our loop
			  do {									//At least once, perform the following operation
				nom = ALPHABET_STATE.charAt(copy % ALPHABET_STATE.length()) + nom;		//Access the alphabet for the corresponding character
				copy /= ALPHABET_STATE.length();				//Reduce copy by the division of the length of the alphabet.
			  } while (copy != 0);					//Until it reaches zero. I promise this makes sense and does work.
			  int numFriends = 1 + rand.nextInt(sizePaths);	//How many paths leading out? 1 + (0 -> The log-base-2 of the number of States in the Finite State Machine).
			  states.add(nom);						//Add the new name to the states list for back-referencing
			  ArrayList<String> usedEvents = new ArrayList<String>();
			  for(int j = 0; j < numFriends; j++) {	//For as many friends as defined:
				  String theEvent = events.get(rand.nextInt(sizeEvents));
				  while(!nonDet && usedEvents.indexOf(theEvent) != -1 && usedEvents.size() < sizeEvents) {
					  theEvent = events.get(rand.nextInt(sizeEvents));
				  }
				  usedEvents.add(theEvent);			//Keep track of used Events for non-replication
				  if(j == 0) {						//Ensure at least one event-path is not self-referential
					  String nonSame = nom;
					  while(nonSame.equals(nom)) {	//So long as they are the same, keep generating States
						  nonSame = rand.nextInt(sizeStates) + "";
					  }
					  raf.writeBytes(nom + " " + nonSame + " " + theEvent);
				  }
				  else {
				      raf.writeBytes(nom + " " + rand.nextInt(sizeStates) + " " + theEvent);	//Add a line of State1 State2 Event(Generated on the spot)
				  }
				  raf.writeBytes(lineSkip);					//Line return - For Windows computers, needs to be Carriage Feed + Line Return, 13 10
			  }
		  }
		  raf.close();				//Like a good Girl Scout
		}
		catch(Exception e) {			//Probably won't ever come up.
			e.printStackTrace();
		}
		return f.getAbsolutePath();	//For convenience, have the exact file location back in the calling method!		
	}

	/**
	 * This method generates a file corresponding to the nomenclature of a ModalSpecification's file input via
	 * the properties described by its parameters: Number of States, Marked States, Unique Events, Paths,
	 * Initial States, Private States, Unobservable Events, and Uncontrollable Events.
	 * 
	 * The produced ModalSpecification object will be randomized according to the bounds described by the provided
	 * arguments, saving the produced file to the location described by the input and returning the exact address of
	 * this new file as a String object.
	 * 
	 * File type: .mdl
	 * 
	 * @param sizeStates - int value describing how many States to include in the Modal Specification.
	 * @param sizeMarked - int value describing how many Marked States to include in the Modal Specification.
	 * @param sizeEvents - int value describing how many Unique Events to include in the Modal Specification.
	 * @param sizePaths - int value describing the maximal number of Paths leading out from a given State in the Modal Specification.
	 * @param sizePrivate - int value describing how many Private States to include in the Modal Specification.
	 * @param sizeUnobserv - int value describing how many Events to mark as Unobservable to the System in the Modal Specification. 
	 * @param sizeAtacker - int value describing how many Events to mark as Unobservable to the Attacker in the Modal Specification on top of those to the System.
	 * @param sizeControl - int value describing how many Uncontrollable States to include in the Modal Specification.
	 * @param sizeMust - int value describing how many Must Transitions to include in the Modal Specification.
	 * @param name - String object used to denote the title of the File being generated.
	 * @param filePath - String object specifying where in the file system to place the file.
	 * @return - Returns a String describing the location of the generated File in the file system.
	 */
	
	public static String createModalSpec(int sizeStates, int sizeMarked, int sizeEvents, int sizePaths, int sizePrivate, int sizeUnobserv, int sizeAttacker, int sizeControl, int sizeMust, String name, String filePath) {
		File f = new File(filePath + name + ".mdl");		//Creates a new file with a unique extension
		f.delete();											//If there already exists a file, remove it.
		events = new ArrayList<String>();					//Initiates the ArrayLists for usage
		states = new ArrayList<String>();
		String os = System.getProperty("os.name").split(" ")[0];
		String lineSkip = os.equals("Windows") ? (char)13 + "" + (char)10 : (char)10 + "";
		for(int i = 0; i < sizeEvents; i++) {				//Fills the ArrayList with requested number of Events immediately
			String newName = "";
			int copy = i;
			do {
				newName = ALPHABET_EVENT.charAt(copy%10) + newName;
				copy /= 10;
			} while(copy != 0);
			events.add(newName);
		}
		try{													//To catch IOExceptions
		  RandomAccessFile raf = new RandomAccessFile(f, "rw");	//Allows us to write to the file
		  Random rand = new Random();				//Create random object for randomness
		  raf.writeBytes("7");			//Initial, Marked, Private, Must
		  raf.writeBytes(lineSkip);
		  raf.writeBytes(("" + 1));
		  raf.writeBytes(lineSkip);
		  HashSet<Integer> initial = new HashSet<Integer>();
		  for(int i = 0; i < 1; i++) {
			  int rand1 = rand.nextInt(sizeStates);
			  while(initial.contains(rand1) && 1 <= sizeStates)
				  rand1 = rand.nextInt(sizeStates);
			  initial.add(rand1);
			  raf.writeBytes(rand1+"");		//Pick a state at random to be initial states
			  raf.writeBytes(lineSkip);										//Again, redundancy!
		  }
		  raf.writeBytes(("" + sizeMarked));		//Strings are basically arrays of characters, which can trivially convert
		  raf.writeBytes(lineSkip);					//to bytes, so pass a String to the writeBytes command to write to file. (write 10 for line return)
		  HashSet<Integer> marked = new HashSet<Integer>();
		  for(int i = 0; i < sizeMarked; i++) {		//Now to decide the marked states, again randomly
			  int rand1 = rand.nextInt(sizeStates);
			  while(marked.contains(rand1) && sizeMarked <= sizeStates)
				  rand1 = rand.nextInt(sizeStates);
			  marked.add(rand1);
			  raf.writeBytes(rand1+"");		//Pick states at random to be marked
			  raf.writeBytes(lineSkip);										//This does allow for redundancies, which do nothing but waste space.
		  }
		  raf.writeBytes("" + sizePrivate);
		  raf.writeBytes(lineSkip);
		  HashSet<Integer> privacy = new HashSet<Integer>();
		  for(int i = 0; i < sizePrivate; i++) {
			  int rand1 = rand.nextInt(sizeStates);
			  while(privacy.contains(rand1) && sizePrivate <= sizeStates)
				  rand1 = rand.nextInt(sizeStates);
			  marked.add(rand1);
			  raf.writeBytes(rand1+"");
			  raf.writeBytes(lineSkip);
		  }
		  
		  HashSet<String> unsee = new HashSet<String>();
		  
		  raf.writeBytes(("" + sizeUnobserv));
		  raf.writeBytes(lineSkip);
		  for(int i = 0; i < sizeUnobserv; i++) {
			  String val = events.get(rand.nextInt(sizeEvents));
			  while(unsee.contains(val))
				  val = events.get(rand.nextInt(sizeEvents));
			  unsee.add(val);
			  raf.writeBytes(events.get(rand.nextInt(sizeEvents)));
			  raf.writeBytes(lineSkip);
		  }
		  
		  while(unsee.size() < sizeUnobserv + sizeAttacker) {
			  String val = events.get(rand.nextInt(sizeEvents));
			  while(unsee.contains(val))
				  val = events.get(rand.nextInt(sizeEvents));
			  unsee.add(val);
		  }
		  
		  raf.writeBytes(("" + (unsee.size())));
		  raf.writeBytes(lineSkip);
		  for(String s : unsee)
			  raf.writeBytes(s + lineSkip);
		  
		  raf.writeBytes((""+sizeControl));
		  raf.writeBytes(lineSkip);
		  for(int i = 0; i < sizeControl; i++) {
			  raf.writeBytes(events.get(rand.nextInt(sizeEvents)));
			  raf.writeBytes(lineSkip);
		  }
		  StringBuilder reference = new StringBuilder();
		  for(int i = 0; i < sizeStates; i++) {		//Each state as defined by sizeStates is processed herein
			  String nom = "";						//Holds the State's name to differentiate between them
			  int copy = i;							//So that we may manipulate the value but not mess up our loop
			  do {									//At least once, perform the following operation
				nom = ALPHABET_STATE.charAt(copy % ALPHABET_STATE.length()) + nom;		//Access the alphabet for the corresponding character
				copy /= ALPHABET_STATE.length();				//Reduce copy by the division of the length of the alphabet.
			  } while (copy != 0);					//Until it reaches zero. I promise this makes sense and does work.
			  int numFriends = 1 + rand.nextInt(sizePaths);	//How many paths leading out? 1 + (0 -> The log-base-2 of the number of States in the Finite State Machine).
			  states.add(nom);						//Add the new name to the states list for back-referencing
			  ArrayList<String> usedEvents = new ArrayList<String>();
			  for(int j = 0; j < numFriends; j++) {	//For as many friends as defined:
				  String theEvent = events.get(rand.nextInt(sizeEvents));
				  while(usedEvents.indexOf(theEvent) != -1 && usedEvents.size() < sizeEvents) {
					  theEvent = events.get(rand.nextInt(sizeEvents));
				  }
				  usedEvents.add(theEvent);			//Keep track of used Events for non-replication
				  if(j == 0) {						//Ensure at least one event-path is not self-referential
					  String nonSame = nom;
					  while(nonSame.equals(nom)) {	//So long as they are the same, keep generating States
						  nonSame = rand.nextInt(sizeStates) + "";
					  }
					  reference.append(nom + " " + nonSame + " " + theEvent);
				  }
				  else {
				      reference.append(nom + " " + rand.nextInt(sizeStates) + " " + theEvent);	//Add a line of State1 State2 Event(Generated on the spot)
				  }
				  if(j + 1 < numFriends || i + 1 < sizeStates)
					  reference.append(lineSkip);					//Line return - For Windows computers, needs to be Carriage Feed + Line Return, 13 10
			  }
		  }

		  raf.writeBytes(("" + sizeMust));
		  raf.writeBytes(lineSkip);
		  String[] refe = reference.toString().split("\n\n");
		  for(int i = 0; i < sizeMust; i++) {
			  raf.writeBytes(refe[rand.nextInt(refe.length)]);
			  raf.writeBytes(lineSkip);
		  }
		  
		  raf.writeBytes(reference.toString());
		  
		  raf.close();				//Like a good Girl Scout
		}
		catch(Exception e) {			//Probably won't ever come up.
			e.printStackTrace();
		}
		return f.getAbsolutePath();	//For convenience, have the exact file location back in the calling method!
	}
	
}
