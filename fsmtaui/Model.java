package fsmtaui;

import support.Event;
import support.transition.Transition;
import javafx.scene.control.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.*;
import java.util.ArrayList;
import javafx.stage.*;
import java.io.File;
import fsm.*;

/**
 * This class stores the required back-end of the FSMtA GUI
 * that need to be accessed throughout many components where events are
 * controlled and the model is viewed.
 * 
 * This class is a part of the fsmtaui package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public class Model {
	
//---  Constants   ----------------------------------------------------------------------------
	
	/** String for requesting a new file path. */
	private static final String CHOOSE_NEW_FILE_PATH_MSG = "Choose a path for the new file";
	/** String for when a user creates an FSM with a name that is already used by another FSM. */
	private static final String PREUSED_FSM_NAME_MSG = "The FSM name you chose already exists. Please select another name.";
	/** String for when a user gives a new FSM no name. */
	private static final String EMPTY_FSM_NAME_MSG = "An FSM cannot have an empty string as its name. Please type a name.";
	/** String for the an invalid fsm title error's title bar. */
	private static final String INVALID_FSM_NAME_TITLE = "Invalid FSM Name";
	
//---  Instance Variables   -------------------------------------------------------------------
	
	/** List of open TransitionSystems. */
	private ObservableList<TransitionSystem<? extends Transition>> openTSs;
	/** List of openFSM ids, used for the ListView. */
	private ObservableList<String> openTSStrings;
	/** Transition system which is currently open in the UI. */
	private TransitionSystem<?> currTS;
	/** List of all the events which are open in a ListView for the current TS. */
	private ObservableList<Event> currTSEvents;
	/** TabPane containing all the tabs of open FSMs. This is kept in the Model class so that other panes can add on openFSMs. */
	private TabPane openTSTabs;
	/** File object representing the directory being used for temporary files. */
	private File workingDirectory;
	/** String representing the path to the GraphViz configuration file. */
	private String graphVizConfigPath;
	
//---  Constructors   -------------------------------------------------------------------------
	
	/**
	 * Creates a new Model object where other classes in the UI can
	 * access important information from the Model.
	 * 
	 * @param inOpenTSs - ObservableList of all the open transition system objects.
	 * @param inOpenTSStrings - ObservableList of all the String ids of the open transition system objects.
	 * @param inWorkingDirectory - File representing the working path for the FSMtA session.
	 */
	
	public Model(ObservableList<TransitionSystem<? extends Transition>> inOpenTSs, ObservableList<String> inOpenTSStrings, File inWorkingDirectory, String inGraphVizConfigPath) {
		openTSs = inOpenTSs;
		openTSStrings = inOpenTSStrings;
		workingDirectory = inWorkingDirectory;
		graphVizConfigPath = inGraphVizConfigPath;
		openTSTabs = new TabPane();
		makeOpenTSStrings();
		
		makeCurrentTSListener();
		// Get the events for the current FSM
		TransitionSystem<?> curr = getCurrTS();
		if(curr != null) currTSEvents = FXCollections.observableArrayList(curr.getEvents());
		else currTSEvents = FXCollections.observableArrayList();
	} // Model(ObservableList, ObservableList, File)
	
	/**
	 * Creates a listener that updates the current TS every time the TS tab changes.
	 * It also updates all the editable events in the UI to reflect the change in TS.
	 */
	
	private void makeCurrentTSListener() {
		openTSTabs.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
			@Override
			public void changed(ObservableValue<? extends Tab> ov, Tab t, Tab t1) {
				Tab currTab = ov.getValue();
				if(currTab != null) {
					TSViewport currViewport = (TSViewport)(currTab.getContent());
					currTS = currViewport.getFSM();
					// Also, update the events
					currTSEvents.clear();
					currTSEvents.addAll(currTS.getEvents());
				} else {
					currTS = null;
				}
			}
		});
	}

//---  Operations   ---------------------------------------------------------------------------
	
	/**
	 * Makes the openTSStrings object, which is an observable ArrayList
	 * storing all the IDs for the Transition Systems that are open. A listener is added
	 * to make sure the list is updated whenever the openTSs list is updated.
	 */

	public void makeOpenTSStrings() {
		// Make the observable list
		ArrayList<String> openTSStringList = new ArrayList<String>();
		openTSStrings = FXCollections.observableList(openTSStringList);
		
		// Add all the FSMs
		for(TransitionSystem<? extends Transition> fsm : openTSs) {
			openTSStrings.add(fsm.getId());
		} // for
		
		// Add a listener so the list reflects the list of actual FSM objects
		openTSs.addListener(new ListChangeListener<TransitionSystem<? extends Transition>>() {
			@Override
			public void onChanged(ListChangeListener.Change<? extends TransitionSystem<? extends Transition>> change) {
				while(change.next()) {
					if(change.wasAdded()) {
						for(TransitionSystem<? extends Transition> ts : change.getAddedSubList()) {
							openTSStrings.add(ts.getId());
							addViewport(ts, ts.getId());
						} // for
					} // if
					if(change.wasRemoved()) {
						for(TransitionSystem<? extends Transition> ts : change.getRemoved()) {
							openTSStrings.remove(ts.getId());
							removeViewport(ts.getId());
						} // for
					} // if
				} // while
			} // onChanged(ListChangeListener.Change)
		}); // addListener(ListChangeListener<TransitionSystem<Transition>>)
	} // makeOpenFSMStrings()
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	/**
	 * Getter method to get the String id's of all the open TransitionSystems.
	 * 
	 * @return - Returns an ObservableList of String objects representing the identifications of all the open TransitionSystems.
	 */

	public ObservableList<String> getOpenTSStrings() {
		return openTSStrings;
	} // getOpenFSMStrings()
	
	/**
	 * Getter method to get the String id's of all the open TransitionSystems
	 * that also happen to be instances of the FSM class.
	 * 
	 * @return - Returns an ObservableList of String objects representing the identifications of all the open FSMs.
	 */

	public ObservableList<String> getOpenFSMStrings() {
		ArrayList<String> openFSMStringList = new ArrayList<String>();
		
		// Get all the FSMs and add them to a list
		for(TransitionSystem<?> ts : openTSs)
			if(ts instanceof FSM<?>)
				openFSMStringList.add(ts.getId());
		
		return FXCollections.observableList(openFSMStringList);
	}
	
	/**
	 * Getter method to get the open TransitionSystem tabs as a TabPane object for display in the UI.
	 * 
	 * @return - Returns a TabPane object with all the open TransitionSystem viewports and their String identifications as the tab identifiers.
	 */

	public TabPane getOpenTSTabs() {
		return openTSTabs;
	} // getOpenFSMTabs()
	
	/**
	 * Gets the current TransitionSystem visible in the viewport.
	 * 
	 * @return - Returns a TransitionSystem object that is visible in the current viewport.
	 */

	public TransitionSystem<? extends Transition> getCurrTS() {
		return currTS;
	} // getCurrFSM()
	
	/**
	 * Gets the ObservableList of Events which are applicable to the current TS in the
	 * viewport.
	 * 
	 * @return - Returns an ObservableList of Event objects.
	 */
	
	public ObservableList<Event> getCurrTSEvents() {
		return currTSEvents;
	}
	
	/**
	 * Gets the TransitionSystem with the desired String id and returns it.
	 * 
	 * @param id - String object representing the id associated with an open TransitionSystem.
	 * @return - Returns a TransitionSystem object associated with the provided String object id.
	 */

	public TransitionSystem<? extends Transition> getTS(String id) {
		for(TransitionSystem<? extends Transition> curr : openTSs) {
			if(curr.getId().equals(id)) {
				return curr;
			} // if
		} // for
		return null;
	} // getTS(String)
	
	/**
	 * Checks if a TransitionSystem with a given id exists in the open TS list.
	 * 
	 * @param id - String object id of the TS to look for.
	 * @return - Returns a boolean value; true if the TS exists, false otherwise.
	 */

	public boolean tsExists(String id) {
		return openTSStrings.contains(id);
	} // tsExists(String)
	
	/**
	 * Checks if an input String name for an FSM already exists in
	 * the list of currently open FSMs.
	 * 
	 * @param id - String object representing the desired name for a new FSM.
	 * @return - Returns a boolean value; true if the name is acceptable, false if it already exists or is an empty string.
	 */

	public boolean checkIfValidTSId(String id) {
		// Error if name already exists, or if empty.
		if(tsExists(id)) {
			Alert error = new Alert(Alert.AlertType.ERROR, PREUSED_FSM_NAME_MSG);
			error.setHeaderText(INVALID_FSM_NAME_TITLE);
			error.showAndWait();
			return false;
		} else if(id.equals("")) {
			Alert error = new Alert(Alert.AlertType.ERROR, EMPTY_FSM_NAME_MSG);
			error.setHeaderText(INVALID_FSM_NAME_TITLE);
			error.showAndWait();
			return false;
		} // if/elseif
		return true;
	} // checkIfValidTSId(String)

//---  Getter Methods - File Access   ---------------------------------------------------------
	
	/**
	 * Getter method to request the working directory as a file.
	 * 
	 * @return - Returns a File object representing the working directory of the FSMtA session.
	 */

	public File getWorkingDirectoryFile() {
		return workingDirectory;
	} // getWorkingDirectoryFile()
	
	/**
	 * Getter method to request the working directory as a String.
	 * 
	 * @return - Returns a String object representing the working directory of the FSMtA session.
	 */

	public String getWorkingDirectoryString() {
		return workingDirectory.getPath();
	} // getWorkingDirectoryString()
	
	/**
	 * Getter method to request the GraphViz config file path as a String.
	 * 
	 * @return - Returns a String object representing the GraphViz config file path.
	 */

	public String getGraphVizConfigPath() {
		return graphVizConfigPath;
	} // getGraphVizConfigPath()

	/**
	 * Prompts the user for a path to save a file and returns the corresponding File object.
	 * 
	 * @return - Returns a File object representing the newly generated File.
	 */

	public static File getPathToSaveFile() {
		// Prompts the user for a file
		FileChooser chooser = new FileChooser();
		chooser.setTitle(CHOOSE_NEW_FILE_PATH_MSG);
		File file = chooser.showSaveDialog(null);
		return file;
	} // getPathToSaveFile()

//---  Manipulations   ------------------------------------------------------------------------

	/**
	 * Adds a new Transition System to the set of OpenTSs
	 * 
	 * @param ts - TransitionSystem object to add to the set of open transition systems in the GUI.
	 */

	public void addTS(TransitionSystem<? extends Transition> ts) {
		openTSs.add(ts);
	} // addTS(TransitionSystem<Transition>)
	
	/**
	 * Helper method that removes an TransitionSystem with a given String id from the
	 * list of open TSs by searching through all the open TSs.
	 * 
	 * @param id - String object representing the id of the transition system to remove.
	 * @return - Returns a boolean value; true if the FSM was found and removed, false otherwise.
	 */

	public boolean removeTS(String id) {
		for(int i = 0; i < openTSs.size(); i++) {
			TransitionSystem<? extends Transition> curr = openTSs.get(i);
			if(curr.getId().equals(id)) {
				openTSs.remove(i);
				return true;
			} // if
		} // for
		return false;
	} // removeTS(String)

//---  Viewport Facilitation   ----------------------------------------------------------------

	/**
	 * Checks if a viewport already exists for a given TransitionSystem id.
	 * 
	 * @param id - String object representing the id of the TS which is being checked.
	 * @return - Returns a boolean value; true if the TS already has a viewport; false otherwise.
	 */

	public boolean viewportExists(String id) {
		ObservableList<Tab> tabs = openTSTabs.getTabs();
		for(int i = 0; i < tabs.size(); i++)
			if(tabs.get(i).getText().equals(id))
				return true;
		return false;
	} // viewportExists(String)
	
	/**
	 * Adds a viewport to the ContentPane to display another FSM in a new tab.
	 * 
	 * @param ts - A new TransitionSystem object to display.
	 * @param name - The TransitionSystem object's String id.
	 * @return - Returns a boolean value; true if the viewport was added, false if the viewport already existed.
	 */

	public boolean addViewport(TransitionSystem<? extends Transition> ts, String name) {
		// First, check if the viewport exists, and if it does,
		// open it
		ObservableList<Tab> tabs = openTSTabs.getTabs();
		for(int i = 0; i < tabs.size(); i++) {
			if(tabs.get(i).getText().equals(name)) {
				openTSTabs.getSelectionModel().select(i);
				openTSTabs.requestFocus();
				return false;
			} // if
		} // for
		// If it does not exist, open a new viewport
		TSViewport newViewport = new TSViewport(ts, this);
		tabs.add(new Tab(name, newViewport));
		int size = tabs.size();
		// Opens the new tab in the foreground
		openTSTabs.getSelectionModel().select(size - 1);
		openTSTabs.requestFocus();
		return true;
	} // addViewport(TransitionSystem<Transition>, String)
	
	/**
	 * Removes a viewport using a String id by going through all the tabs and
	 * closing the one with the matching id.
	 * 
	 * @param name - String object representing the id of the TransitionSystem tab to remove.
	 * @return - Returns a boolean value; true if the TransitionSystem was removed; false otherwise.
	 */

	public boolean removeViewport(String name) {
		ObservableList<Tab> tabs = openTSTabs.getTabs();
		for(int i = 0; i < tabs.size(); i++) {
			Tab curr = tabs.get(i);
			if(curr.getText().equals(name)) {
				tabs.remove(curr);
				return true;
			} // if
		} // for
		return false;
	} // removeViewport(String)
	
	/**
	 * Refreshes the viewport with the current image.
	 */

	public void refreshViewport() {
		Tab currTab = openTSTabs.getSelectionModel().getSelectedItem();
		if(currTab == null) return;
		TSViewport currViewport = (TSViewport) currTab.getContent();
		currViewport.refreshImage();
	} // getCurrFSMViewport()
	
	/**
	 * Gets the current FSM viewport.
	 * 
	 * @return - Returns a FSMViewport object that is currently visible.
	 */

	public TSViewport getCurrViewport() {
		Tab currTab = openTSTabs.getSelectionModel().getSelectedItem();
		if(currTab == null) return null;
		return (TSViewport) currTab.getContent();
	} // getCurrViewport()

} // class Model
