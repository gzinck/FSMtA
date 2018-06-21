package fsmtaui;

import java.io.File;
import java.util.ArrayList;

import fsm.*;
import javafx.collections.*;
import javafx.scene.control.*;
import javafx.stage.*;

/**
 * Model is a class which stores the required backend of the FSMtA GUI
 * that need to be accessed throughout many components where events are
 * controlled and the model is viewed.
 * 
 * @author Mac Clevinger and Graeme Zinck
 *
 */
public class Model {
	/** String for requesting a new file path. */
	private static final String CHOOSE_NEW_FILE_PATH_MSG = "Choose a path for the new file";
	/** String for when a user creates an FSM with a name that is already used by another FSM. */
	private static final String PREUSED_FSM_NAME_MSG = "The FSM name you chose already exists. Please select another name.";
	/** String for when a user gives a new FSM no name. */
	private static final String EMPTY_FSM_NAME_MSG = "An FSM cannot have an empty string as its name. Please type a name.";
	/** String for the an invalid fsm title error's title bar. */
	private static final String INVALID_FSM_NAME_TITLE = "Invalid FSM Name";
	
	/** List of openFSMs. */
	private ObservableList<FSM> openFSMs;
	/** List of openFSM ids, used for the ListView. */
	private ObservableList<String> openFSMStrings;
	/** TabPane containing all the tabs of open FSMs. This is kept in the Model class
	 * so that other panes can add on openFSMs. */
	private TabPane openFSMTabs;
	/** File object representing the directory being used for temporary files. */
	private File workingDirectory;
	/** String representing the path to the GraphViz configuration file. */
	private String graphVizConfigPath;
	
	/**
	 * Creates a new Model object where other classes in the UI can
	 * access important information from the Model.
	 * 
	 * @param inOpenFSMs - ObservableList of all the open FSM objects.
	 * @param inOpenFSMStrings - ObservableList of all the String ids of the open FSM objects.
	 * @param inWorkingDirectory - File representing the working path for the FSMtA session.
	 */
	Model(ObservableList<FSM> inOpenFSMs, ObservableList<String> inOpenFSMStrings,
			File inWorkingDirectory, String inGraphVizConfigPath) {
		openFSMs = inOpenFSMs;
		openFSMStrings = inOpenFSMStrings;
		workingDirectory = inWorkingDirectory;
		graphVizConfigPath = inGraphVizConfigPath;
		openFSMTabs = new TabPane();
		makeOpenFSMStrings();
	} // Model(ObservableList, ObservableList, File)
	
	/**
	 * Makes the openFSMStrings object, which is an observable ArrayList
	 * storing all the IDs for the FSMs that are open. A listener is added
	 * to make sure the list is updated whenever the openFSMs list is updated.
	 */
	public void makeOpenFSMStrings() {
		// Make the observable list
		ArrayList<String> openFSMStingSet = new ArrayList<String>();
		openFSMStrings = FXCollections.observableList(openFSMStingSet);
		
		// Add all the FSMs
		for(FSM fsm : openFSMs) {
			openFSMStrings.add(fsm.getId());
		} // for
		
		// Add a listener so the list reflects the list of actual FSM objects
		openFSMs.addListener(new ListChangeListener<FSM>() {
			@Override
			public void onChanged(ListChangeListener.Change<? extends FSM> change) {
				while(change.next()) {
					if(change.wasAdded()) {
						for(FSM fsm : change.getAddedSubList()) {
							openFSMStrings.add(fsm.getId());
							addViewport(fsm, fsm.getId());
						} // for
					} // if
					if(change.wasRemoved()) {
						for(FSM fsm : change.getRemoved()) {
							openFSMStrings.remove(fsm.getId());
							removeViewport(fsm.getId());
						} // for
					} // if
				} // while
			} // onChanged(ListChangeListener.Change)
		}); // addListener(ListChangeListener<DeterministicFSM>)
	} // makeOpenFSMStrings()
	
	//----------------------------------------------------------------------
	// Getter methods.
	//----------------------------------------------------------------------
	/**
	 * Getter method to get the String id's of all the open FSMs.
	 * 
	 * @return - ObservableList of Strings representing the ids
	 * of all the open FSMs.
	 */
	public ObservableList<String> getOpenFSMStrings() {
		return openFSMStrings;
	} // getOpenFSMStrings()
	
	/**
	 * Getter method to get the open FSM tabs as a TabPane object for
	 * display in the UI.
	 * 
	 * @return - TabPane object with all the open FSM viewports and their
	 * String ids as the tab identifiers.
	 */
	public TabPane getOpenFSMTabs() {
		return openFSMTabs;
	} // getOpenFSMTabs()
	
	//----------------------------------------------------------------------
	// Methods to access files.
	//----------------------------------------------------------------------
	/**
	 * Getter method to get the working directory as a file.
	 * 
	 * @return - File representing the working directory of the
	 * FSMtA session.
	 */
	public File getWorkingDirectoryFile() {
		return workingDirectory;
	} // getWorkingDirectoryFile()
	
	/**
	 * Getter method to get the working directory as a String.
	 * 
	 * @return - String representing the working directory of the
	 * FSMtA session.
	 */
	public String getWorkingDirectoryString() {
		return workingDirectory.getPath();
	} // getWorkingDirectoryString()
	
	/**
	 * Getter method to get the GraphViz config file path as a
	 * String.
	 * 
	 * @return - String representing the GraphViz config file path.
	 */
	public String getGraphVizConfigPath() {
		return graphVizConfigPath;
	} // getGraphVizConfigPath()
	
	//----------------------------------------------------------------------
	// Methods to add, remove, and check FSMs.
	//----------------------------------------------------------------------
	/**
	 * Adds a new FSM to the set of OpenFSMs
	 * 
	 * @param fsm - DeterministicFSM to add to the set of
	 * open FSMs in the GUI.
	 */
	public void addFSM(FSM fsm) {
		openFSMs.add(fsm);
	} // addFSM(DeterministicFSM)
	
	/**
	 * Helper method that removes an FSM with a given String id from the
	 * list of open FSMs by searching through all the open FSMs.
	 * 
	 * @param id - String representing the id of the FSM to remove.
	 * @return - True if the FSM was found and removed, false otherwise.
	 */
	public boolean removeFSM(String id) {
		for(int i = 0; i < openFSMs.size(); i++) {
			FSM curr = openFSMs.get(i);
			if(curr.getId().equals(id)) {
				openFSMs.remove(i);
				return true;
			} // if
		} // for
		return false;
	} // removeFSM(String)
	
	/**
	 * Gets the FSM with the desired String id and returns it.
	 * 
	 * @param id - String representing the id associated with an open FSM.
	 * @return - The FSM associated with the id.
	 */
	public FSM getFSM(String id) {
		for(FSM curr : openFSMs) {
			if(curr.getId().equals(id)) {
				return curr;
			} // if
		} // for
		return null;
	} // getFSM(String)
	
	/**
	 * Checks if an FSM with a given id exists in the open FSM list.
	 * 
	 * @param id - String id of the FSM to look for.
	 * @return - True if the FSM exists, false otherwise.
	 */
	public boolean fsmExists(String id) {
		return openFSMStrings.contains(id);
	} // fsmExists(String)
	
	/**
	 * Checks if an input String name for an FSM already exists in
	 * the list of currently open FSMs.
	 * 
	 * @param id - String representing the desired name for a new FSM.
	 * @return - True if the name is acceptable, false if it already exists
	 * or it is an empty string.
	 */
	public boolean checkIfValidFSMId(String id) {
		// Error if name already exists, or if empty.
		if(fsmExists(id)) {
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
	} // checkIfValidFSMId(String)
	
	/**
	 * Gets the current FSM visible in the viewport.
	 * 
	 * @return - DeterministicFSM that is visible in the current viewport.
	 */
	public FSM getCurrFSM() {
		Tab currTab = openFSMTabs.getSelectionModel().getSelectedItem();
		if(currTab == null) return null;
		FSMViewport currViewport = (FSMViewport) currTab.getContent();
		return currViewport.getFSM();
	} // getCurrFSM()
	
//	/**
//	 * Gets if the current FSM extends NonDeterministic.
//	 * 
//	 * @return - True if the current FSM is non-deterministic,
//	 * false otherwise (or if there is no current FSM).
//	 */
//	public boolean currFSMIsNonDeterministic() {
//		FSM fsm = getCurrFSM();
//		if(fsm instanceof NonDeterministicFSM) {
//			return true;
//		} // if
//		return false;
//	} // currFSMIsNonDeterministic()
	
//	/**
//	 * Gets if the current FSM extends Observable.
//	 * 
//	 * @return - True if the current FSM is an observable non-deterministic
//	 * FSM, false otherwise (or if there is no current FSM).
//	 */
//	public boolean currFSMIsObservable() {
//		FSM fsm = getCurrFSM();
//		if(fsm instanceof ObservableFSM) {
//			return true;
//		} // if
//		return false;
//	} // currFSMIsObservable()
	
	//----------------------------------------------------------------------
	// General I/O
	//----------------------------------------------------------------------
	/**
	 * Prompts the user for a path to save a file and returns the
	 * corresponding File object.
	 * 
	 * @return - File object representing the path for the new file.
	 */
	public static File getPathToSaveFile() {
		// Prompts the user for a file
		FileChooser chooser = new FileChooser();
		chooser.setTitle(CHOOSE_NEW_FILE_PATH_MSG);
		File file = chooser.showSaveDialog(null);
		return file;
	} // getPathToSaveFile()
	
	//----------------------------------------------------------------------
	// Methods to work with FSM viewports.
	//----------------------------------------------------------------------
	/**
	 * Checks if a viewport already exists for a given FSM id.
	 * 
	 * @param id - String representing the id of the FSM which is
	 * being checked.
	 * @return - True if the FSM already has a viewport; false
	 * otherwise.
	 */
	public boolean viewportExists(String id) {
		ObservableList<Tab> tabs = openFSMTabs.getTabs();
		for(int i = 0; i < tabs.size(); i++)
			if(tabs.get(i).getText().equals(id))
				return true;
		return false;
	} // viewportExists(String)
	
	/**
	 * Adds a viewport to the ContentPane to display another FSM in a new tab.
	 * 
	 * @param fsm - A new FSM to display.
	 * @param name - The FSM's String id.
	 * @return - True if the viewport was added, false if the viewport already
	 * existed.
	 */
	public boolean addViewport(FSM fsm, String name) {
		// First, check if the viewport exists, and if it does,
		// open it
		ObservableList<Tab> tabs = openFSMTabs.getTabs();
		for(int i = 0; i < tabs.size(); i++) {
			if(tabs.get(i).getText().equals(name)) {
				openFSMTabs.getSelectionModel().select(i);
				openFSMTabs.requestFocus();
				return false;
			} // if
		} // for
		// If it does not exist, open a new viewport
		FSMViewport newViewport = new FSMViewport(fsm, this);
		tabs.add(new Tab(name, newViewport));
		int size = tabs.size();
		// Opens the new tab in the foreground
		openFSMTabs.getSelectionModel().select(size - 1);
		openFSMTabs.requestFocus();
		return true;
	} // addViewport(DeterministicFSM, String)
	
	/**
	 * Removes a viewport using a String id by going through all the tabs and
	 * closing the one with the matching id.
	 * 
	 * @param name - String id of the FSM tab to remove.
	 * @return - True if the FSM was removed; false otherwise.
	 */
	public boolean removeViewport(String name) {
		ObservableList<Tab> tabs = openFSMTabs.getTabs();
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
		Tab currTab = openFSMTabs.getSelectionModel().getSelectedItem();
		if(currTab == null) return;
		FSMViewport currViewport = (FSMViewport) currTab.getContent();
		currViewport.refreshImage();
	} // getCurrFSMViewport()
	
	/**
	 * Gets the current FSM viewport.
	 * 
	 * @return - FSMViewport that is currently visible.
	 */
	public FSMViewport getCurrViewport() {
		Tab currTab = openFSMTabs.getSelectionModel().getSelectedItem();
		if(currTab == null) return null;
		return (FSMViewport) currTab.getContent();
	} // getCurrViewport()
} // class Model
