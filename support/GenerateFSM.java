package support;

import java.io.*;
import java.util.*;

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
	 * This method generates a file corresponding to the nomenclature of a DeterministicFSM's file input
	 * via the properties described by its parameters: Number of States, Number of Marked States, the 
	 * Number of Initial States, and the Number of Paths leading out. It uses randomization in the production
	 * of this file, maintaining usability as an input file while having no assurance of being a sensically
	 * designed Finite State Machine.
	 * 
	 * It creates beauty.
	 * 
	 * File type: .fsm
	 * 
	 * @param sizeStates - int value describing how many discrete States there exist in the produced Finite State Machine
	 * @param sizeMarked - int value describing how many States are Marked states
	 * @param sizeEvents - int value describing how many events there are
	 * @param sizePaths - int value describing how many (maximally) paths can lead out from a State
	 * @param name - String object used to denote the file's name within the directory defined by a constant value
	 * @param filePath - String object representing the path to the folder to which the user wishes to save the file
	 * @return - Returns a String form of the file path to access this newly created file by
	 */
	
	public static String createNewDeterministicFSM(int sizeStates, int sizeMarked, int sizeEvents, int sizePaths, String name, String filePath) {
		File f = new File(filePath + name + ".fsm");		//Creates a new file with a unique extension
		f.delete();											//If there already exists a file, remove it.
		events = new ArrayList<String>();					//Initiates the ArrayLists for usage
		states = new ArrayList<String>();
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
		  raf.writeBytes("2");						//How many special sections
		  raf.write(10);
		  raf.writeBytes("1");						//How many initial
		  raf.write(10);
		  raf.writeBytes(rand.nextInt(sizeStates)+"");		//Pick a state at random to be initial states
		  raf.write(10);										//Again, redundancy!
		  raf.writeBytes((""+sizeMarked));		//Strings are basically arrays of characters, which can trivially convert
		  raf.write(10);					//to bytes, so pass a String to the writeBytes command to write to file. (write 10 for line return)
		  HashSet<Integer> marked = new HashSet<Integer>();
		  for(int i = 0; i < sizeMarked; i++) {		//Now to decide the marked states, again randomly
			  int rand1 = rand.nextInt(sizeStates);
			  while(marked.contains(rand1) && sizeMarked <= sizeStates)
				  rand1 = rand.nextInt(sizeStates);
			  marked.add(rand1);
			  raf.writeBytes(rand1+"");		//Pick states at random to be marked
			  raf.write(10);										//This does allow for redundancies, which do nothing but waste space.
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
				  while(usedEvents.indexOf(theEvent) != -1 && usedEvents.size() < sizeEvents) {
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
				  raf.write(10);						//Line return - For Windows computers, needs to be Carriage Feed + Line Return, 13 10
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
	 * This method generates a file corresponding to the nomenclature of a NonDeterministicFSM's file input
	 * via the properties described by its parameters: Number of States, Marked States, Unique Events, Paths,
	 * and Initial States. Functions similarly to the createNewDeterministicFSM() method, differentiated
	 * by permitting multiple Initial States and States to have multiple paths of the same identifier
	 * leading out from them.
	 * 
	 * File type: .fsm
	 * 
	 * @param sizeStates - int value describing how many States to include in the FSM.
	 * @param sizeMarked - int value describing how many Marked States to include in the FSM.
	 * @param sizeEvents - int value describing how many Unique Events to include in the FSM.
	 * @param sizePaths - int value describing the maximal number of Paths leading out from a given State in the FSM.
	 * @param sizeInitial - int value describing how many Initial States to include in the FSM.
	 * @param name - String object used to denote the title of the File being generated.
	 * @param filePath - String object specifying where in the file system to place the file.
	 * @return - Returns a String describing the location of the generated File in the file system.
	 */

	public static String createNewNonDeterministicFSM(int sizeStates, int sizeMarked, int sizeEvents, int sizePaths, int sizeInitial, String name, String filePath) {
		File f = new File(filePath + name + ".fsm");		//Creates a new file with a unique extension
		f.delete();											//If there already exists a file, remove it.
		events = new ArrayList<String>();					//Initiates the ArrayLists for usage
		states = new ArrayList<String>();
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
		  raf.writeBytes("2");						//How many special sections
		  raf.write(10);
		  raf.writeBytes(sizeInitial+"");						//How many initial
		  raf.write(10);										//Again, redundancy!
		  HashSet<Integer> initial = new HashSet<Integer>();
		  for(int i = 0; i < sizeInitial; i++) {
			  int rand1 = rand.nextInt(sizeStates);
			  while(initial.contains(rand1) && sizeInitial <= sizeStates)
				  rand1 = rand.nextInt(sizeStates);
			  raf.writeBytes(rand1+"");		//Pick a state at random to be initial states
			  raf.write(10);										//Again, redundancy!
		  }
		  raf.writeBytes(("" + sizeMarked));		//Strings are basically arrays of characters, which can trivially convert
		  raf.write(10);					//to bytes, so pass a String to the writeBytes command to write to file. (write 10 for line return)							
		  HashSet<Integer> marked = new HashSet<Integer>();
		  for(int i = 0; i < sizeMarked; i++) {		//Now to decide the marked states, again randomly
			  int rand1 = rand.nextInt(sizeStates);
			  while(marked.contains(rand1) && sizeMarked <= sizeStates)
				  rand1 = rand.nextInt(sizeStates);
			  initial.add(rand1);
			  marked.add(rand1);
			  raf.writeBytes(rand1+"");		//Pick states at random to be marked
			  raf.write(10);										//This does allow for redundancies, which do nothing but waste space.
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
			  for(int j = 0; j < numFriends; j++) {	//For as many friends as defined:
				  String theEvent = events.get(rand.nextInt(sizeEvents));	
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
				  raf.write(10);						//Line return - For Windows computers, needs to be Carriage Feed + Line Return, 13 10
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
	 * This method generates a file corresponding to the nomenclature of an ObservableFSM's file input
	 * via the properties described by its parameters: Number of States, Marked States, Unique Events, Paths,
	 * Initial States, and Unobservable Events. This method functions similarly to simpler iterations of
	 * the generation algorithm, adding on the feature of designating certain Events as Unobservable.
	 * 
	 * File type: .fsm
	 * 
	 * @param sizeStates - int value describing how many States to include in the FSM.
	 * @param sizeMarked - int value describing how many Marked States to include in the FSM.
	 * @param sizeEvents - int value describing how many Unique Events to include in the FSM.
	 * @param sizePaths - int value describing the maximal number of Paths leading out from a given State in the FSM.
	 * @param sizeInitial - int value describing how many Initial States to include in the FSM.
	 * @param sizeUnobserv - int value describing how many Events to mark as Unobservable in the FSM. 
	 * @param name - String object used to denote the title of the File being generated.
	 * @param filePath - String object specifying where in the file system to place the file.
	 * @return - Returns a String describing the location of the generated File in the file system.
	 */

	public static String createNewObservableFSM(int sizeStates, int sizeMarked, int sizeEvents, int sizePaths, int sizeInitial, int sizeUnobserv, String name, String filePath) {
		File f = new File(filePath + name + ".fsm");		//Creates a new file with a unique extension
		f.delete();											//If there already exists a file, remove it.
		events = new ArrayList<String>();					//Initiates the ArrayLists for usage
		states = new ArrayList<String>();
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
		  raf.writeBytes("3");
		  raf.write(10);
		  raf.writeBytes(("" + sizeInitial));
		  raf.write(10);
		  HashSet<Integer> initial = new HashSet<Integer>();
		  for(int i = 0; i < sizeInitial; i++) {
			  int rand1 = rand.nextInt(sizeStates);
			  while(initial.contains(rand1) && sizeInitial <= sizeStates)
				  rand1 = rand.nextInt(sizeStates);
			  initial.add(rand1);
			  raf.writeBytes(rand1+"");		//Pick a state at random to be initial states
			  raf.write(10);										//Again, redundancy!
		  }
		  raf.writeBytes(("" + sizeMarked));		//Strings are basically arrays of characters, which can trivially convert
		  raf.write(10);					//to bytes, so pass a String to the writeBytes command to write to file. (write 10 for line return)
		  HashSet<Integer> marked = new HashSet<Integer>();
		  for(int i = 0; i < sizeMarked; i++) {		//Now to decide the marked states, again randomly
			  int rand1 = rand.nextInt(sizeStates);
			  while(marked.contains(rand1) && sizeMarked <= sizeStates)
				  rand1 = rand.nextInt(sizeStates);
			  marked.add(rand1);
			  raf.writeBytes(rand1+"");		//Pick states at random to be marked
			  raf.write(10);										//This does allow for redundancies, which do nothing but waste space.
		  }
		  raf.writeBytes(("" + sizeUnobserv));
		  raf.write(10);
		  for(int i = 0; i < sizeUnobserv; i++) {
			  raf.writeBytes(events.get(rand.nextInt(sizeEvents)));
			  raf.write(10);
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
				  while(usedEvents.indexOf(theEvent) != -1 && usedEvents.size() < sizeEvents) {
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
				  raf.write(10);						//Line return - For Windows computers, needs to be Carriage Feed + Line Return, 13 10
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
	 * This method generates a file corresponding to the nomenclature of a ControllableFSM's file input
	 * via the properties described by its parameters: Number of States, Marked States, Unique Events, Paths,
	 * Initial States, Unobservable Events, and whatever the new feature is. This method functions similarly to
	 * simpler iterations of the generation algorithm, adding on the feature of doing whatever it is Controllable
	 * permits.
	 * 
	 * This is a placeholder for when we actually write it.
	 * 
	 * File type: .fsm
	 * 
	 * @param sizeStates - int value describing how many States to include in the FSM.
	 * @param sizeMarked - int value describing how many Marked States to include in the FSM.
	 * @param sizeEvents - int value describing how many Unique Events to include in the FSM.
	 * @param sizePaths - int value describing the maximal number of Paths leading out from a given State in the FSM.
	 * @param sizeInitial - int value describing how many Initial States to include in the FSM.
	 * @param sizeUnobserv - int value describing how many Events to mark as Unobservable in the FSM.
	 * @param somethingControllable 
	 * @param name - String object used to denote the title of the File being generated.
	 * @param filePath - String object specifying where in the file system to place the file.
	 * @return - Returns a String describing the location of the generated File in the file system.
	 */
	
	public static String createNewControllableFSM(int sizeStates, int sizeMarked, int sizeEvents, int sizePaths, int sizeInitial, int sizeUnobserv, int somethingControllable, String name, String filePath) {
	
		return null;
	}

}
