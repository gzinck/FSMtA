package fsmtaui.settingspane;

import fsm.*;
import fsmtaui.Model;
import fsmtaui.popups.*;
import graphviz.FSMToDot;
import support.*;
import support.transition.Transition;

import java.io.File;
import java.io.FileNotFoundException;

import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

/**
 * FileSettingsPane is a specialized javafx VBox element which contains all the settings
 * for file I/O in the GUI. It allows loading in, generating, opening, closing, and saving
 * files.
 * 
 * @author Mac Clevinger and Graeme Zinck
 *
 */
public class FileSettingsPane extends VBox {
	/** String for requesting an FSM text file. */
	private static final String CHOOSE_FSM_FILE_MSG = "Choose a FSM text file";
	/** List of possible FSM types, which is passed to other methods. */
	public static enum FSM_TYPE {
		DETERMINISTIC, NON_DETERMINISTIC
	} // FSM_TYPE
	
	/** Model containing all the important information to display in the GUI. */
	private Model model;
	/** Instance variable for the name field for an FSM. */
	private TextField fsmNameField;
	/** Box with the possible types of FSMs. */
	private ChoiceBox<String> fsmTypeChoiceBox;
	/** Button for reading in a file. */
	private Button readInFileBtn;
	/** Button for creating a new FSM. */
	private Button newTSBtn;
	/** Button for generating a new FSM file which is read in. */
	private Button genFSMBtn;
	/** Button for closing the selected FSMs. */
	private Button closeTSBtn;
	/** Button for saving the selected FSMs as FSM files. */
	private Button saveFSMBtn;
	/** Button for saving the selected FSMs as JPG files. */
	private Button saveJPGBtn;
	/**
	 * Box with all the openFSMs listed. Double clicking on an element opens
	 * the FSM as a viewport, if it is not already.
	 */
	ListView<String> openTSBox;
	
	/**
	 * Creates a FileSettingsPane with all the options to open and save FSMs.
	 * 
	 * @param inModel - Model with all the important information to
	 * display in the GUI.
	 */
	public FileSettingsPane(Model inModel) {
		super();	
		model = inModel;
		
		// Add all the elements to the pane
		Label titleLabel = new Label("File Options");
		titleLabel.getStyleClass().add("section-header");
		VBox mainFileOptions = makeMainFileOptions();
		mainFileOptions.getStyleClass().add("padded");
		VBox openFSMBox = makeOpenFSMBox();
		openFSMBox.getStyleClass().add("padded");
		VBox saveBtns = makeSaveBtns();
		saveBtns.getStyleClass().add("padded");
		
		getChildren().addAll(
			titleLabel, new Separator(),
			mainFileOptions, new Separator(),
			openFSMBox, new Separator(),
			saveBtns
		); // addAll()
		
		// Event handlers
		makeNewFSMFromFileEventHandler();
		makeNewTSEventHandler();
		makeGenTSEventHandler();
		makeSelectTSEventHandler();
		makeCloseTSEventHandler();
		makeSaveTSEventHandler();
	} // SettingsPane()
	
	/**
	 * Makes the main file options section, which includes
	 * naming a new FSM, defining its type, and reading in
	 * the data (i.e., read in, create new, or generate).
	 * 
	 * @return - VBox with all the elements of the main file options
	 * section.
	 */
	private VBox makeMainFileOptions() {
		// Name of new FSM
		Label fsmNameLabel = new Label("FSM Name:");
		fsmNameField = new TextField();
		HBox fsmName = new HBox(fsmNameLabel, fsmNameField);
		
		// Type of new FSM
		Label fsmTypeLabel = new Label("FSM Type:");
		fsmTypeChoiceBox = new ChoiceBox<String>(FXCollections.observableArrayList("Deterministic", "Non-Deterministic"));
		VBox fsmType = new VBox(fsmTypeLabel, fsmTypeChoiceBox);
		
		// Buttons to pull in new FSM
		readInFileBtn = new Button("Read In File");
		newTSBtn = new Button("Create Empty FSM");
		genFSMBtn = new Button("Generate New FSM");
		HBox fileBtns = new HBox(readInFileBtn, newTSBtn, genFSMBtn);
		
		return new VBox(fsmName, fsmType, fileBtns);
	} // makeMainFileOptions()
	
	/**
	 * Makes the box containing all the FSMs that are currently open.
	 * 
	 * @return - VBox with all the FSMs currently open.
	 */
	private VBox makeOpenFSMBox() {
		Label openFSMBoxLabel = new Label("Open FSMs:");
		openTSBox = new ListView<String>(model.getOpenTSStrings());
		openTSBox.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		
		return new VBox(openFSMBoxLabel, openTSBox);
	} // makeOpenFSMBox()
	
	/**
	 * Creates a box with buttons for closing or saving the
	 * selected FSMs from the FSMBox.
	 * 
	 * @return - VBox with all the buttons for closing/saving FSMs.
	 */
	private VBox makeSaveBtns() {
		closeTSBtn = new Button("Close selected FSM");
		saveFSMBtn = new Button("Save selected FSM as FSM file");
		saveJPGBtn = new Button("Save selected FSM as JPG file");
		return new VBox(closeTSBtn, saveFSMBtn, saveJPGBtn);
	} // makeSaveBtns()
	
	/**
	 * Gets a file from the user. Shows an error message if no
	 * file is found.
	 * 
	 * @return - File that the user selects.
	 * @throws FileNotFoundException - Thrown if the user did not select
	 * a valid file.
	 */
	private File getFileFromUser() throws FileNotFoundException
	{
		// Prompts the user for a file
		FileChooser chooser = new FileChooser();
		chooser.setTitle(CHOOSE_FSM_FILE_MSG);
		File file = chooser.showOpenDialog(null);
		
		if(file != null) {
			return file;
		} else {
			Alerts.makeError(Alerts.ERROR_FILE_NOT_FOUND);
			throw new FileNotFoundException("No file received from user.");
		} // if/else
	} // getFileFromUser()
	
	/**
	 * Checks if the TransitionSystem type was selected; if not, return false.
	 * 
	 * @return - True if the user selected an TransitionSystem type; else, false.
	 */
	private boolean checkIfValidTSType() {
		String selected = fsmTypeChoiceBox.getSelectionModel().getSelectedItem();
		// If not selected, show error
		if(selected == null || selected.equals("")) {
			Alerts.makeError(Alerts.ERROR_NO_FSM_TYPE);
			return false;
		} // if
		return true;
	} // checkIfValidFSMType()
	
	/**
	 * Creates an event handler for clicking on the readInFileBtn so that
	 * a file gets read in.
	 */
	private void makeNewFSMFromFileEventHandler() {
		readInFileBtn.setOnAction(e -> {
			// Get the name of the new FSM being read in, and if the name
			// is unique continue to add it
			String newFSMName = fsmNameField.getText();
			if(model.checkIfValidTSId(newFSMName) && checkIfValidTSType()) {
				try {
					// Get the FSM file and create the FSM
					File file = getFileFromUser();
					TransitionSystem<? extends Transition> newFSM = null;
					
					String fsmClass = fsmTypeChoiceBox.getSelectionModel().getSelectedItem();
					if(fsmClass.equals("Deterministic")) {
						newFSM = new DetObsContFSM(file, newFSMName);
					} else if(fsmClass.equals("Non-Deterministic")) {
						newFSM = new NonDetObsContFSM(file, newFSMName);
					}
					model.addTS(newFSM);
					fsmNameField.setText("");
				} catch(FileNotFoundException exception) {
					// Do nothing, since error message already produced in another method.
				} catch(Exception exception) {
					// Show error that file format was not legal.
					exception.printStackTrace();
					Alerts.makeError(Alerts.ERROR_FILE_FORMAT);
				} // try/catch
			} // if valid FSM name and type
		}); // setOnAction()
	} // makeNewFSMFromFileEventHandler()
	
	/**
	 * Makes an event handler when clicking on the newTSBtn such that
	 * it creates a new, empty TransitionSystem with the specified name.
	 */
	private void makeNewTSEventHandler() {
		newTSBtn.setOnAction(e -> {
			String newFSMName = fsmNameField.getText();
			// If the new FSM name is unique and the type is valid, make the new FSM.
			if(model.checkIfValidTSId(newFSMName) && checkIfValidTSType()) {
				TransitionSystem<? extends Transition> newFSM = null;
				
				String fsmClass = fsmTypeChoiceBox.getSelectionModel().getSelectedItem();
				if(fsmClass.equals("Deterministic")) {
					newFSM = new DetObsContFSM(newFSMName);
				} else if(fsmClass.equals("Non-Deterministic")) {
					newFSM = new NonDetObsContFSM(newFSMName);
				}
				if(newFSM != null) {
					model.addTS(newFSM);
					fsmNameField.setText("");
				}
			} // if
		}); // setOnAction()
	} // makeNewTSEventHandler()
	
	/**
	 * Generates a new TransitionSystem by asking the user various parameters (number of states,
	 * number of marked states, number of initial states).
	 */
	private void makeGenTSEventHandler() {
		genFSMBtn.setOnAction(e -> {
			String newTSName = fsmNameField.getText();
			// If the new FSM name is unique and the type is valid, make the new FSM.
			if(model.checkIfValidTSId(newTSName) && checkIfValidTSType()) {
				TransitionSystem<? extends Transition> newTS = null;
				
				String tsClass = fsmTypeChoiceBox.getSelectionModel().getSelectedItem();
				GenerateFSMDialog dialog = new GenerateFSMDialog(tsClass.equals("Deterministic")); // pass boolean of whether the FSM is deterministic or not
				GenerateFSMDialog.FSMParameters parameters = dialog.getFSMParametersFromUser();
				if(parameters != null) {
					File fsmFile = new File(GenerateFSM.createNewFSM(
							parameters.sizeStates, parameters.sizeMarked, parameters.sizeEvents,
							parameters.sizePaths, parameters.sizeInitial, parameters.sizeSecret,
							parameters.sizeUnobserv, parameters.sizeUncontrol, tsClass.equals("Deterministic"),
							newTSName, model.getWorkingDirectoryString() + "/"));
					if(tsClass.equals("Deterministic")) newTS = new DetObsContFSM(fsmFile, newTSName);
					else	 newTS = new NonDetObsContFSM(fsmFile, newTSName);
					fsmFile.delete();
				} // if
				if(newTS != null) {
					model.addTS(newTS);
					fsmNameField.setText("");
				} // if
			} // if
		}); // setOnAction()
	} // makeNewFSMEventHandler()
	
	/**
	 * Creates an event handler to deal with when a user double-clicks
	 * on a TransitionSystem in the openFSMBox in the FileSettingsPane.
	 * When this occurs, handle() is called, which gets the selected FSMs
	 * and opens them in the ContentPane as viewports for their graphs
	 * computed by GraphViz.
	 */
	private void makeSelectTSEventHandler() {
		openTSBox.setOnMouseClicked(e -> {
			if(e.getButton() == MouseButton.PRIMARY && e.getClickCount() > 1) {
				ObservableList<String> selected = openTSBox.getSelectionModel().getSelectedItems();
				for(String curr : selected) {
					TransitionSystem<? extends Transition> currTS = model.getTS(curr);
					if(currTS != null) model.addViewport(currTS, curr);
				} // for
			} // if
		}); // setOnMouseClicked()
	} // makeSelectTSEventHandler()
	
	/**
	 * Creates an event handler for the closeTSBtn that removes all the
	 * TransitionSystems the user selected in the openTSBox (a ListView object) from
	 * the openTSs list.
	 */
	private void makeCloseTSEventHandler() {
		closeTSBtn.setOnAction(e -> {
			ObservableList<String> selected = openTSBox.getSelectionModel().getSelectedItems();
			model.removeTS(selected.get(0));
		}); // setOnAction()
	} // makeCloseTSEventHandler()
	
	/**
	 * Creates an event handler for the saveFSMBtn to save an FSM as a
	 * FSM file in the proprietary FSM format.
	 * This can be used to read in the FSM into FSMtA later on.
	 */
	private void makeSaveTSEventHandler() {
		saveFSMBtn.setOnMouseClicked(e -> {
			// Gets the selected FSMs and prompts the user for the path to save it to.
			String tsId = openTSBox.getSelectionModel().getSelectedItem();
			TransitionSystem<? extends Transition> tsToSave = model.getTS(tsId);
			if(tsToSave == null) {
				Alerts.makeError(Alerts.ERROR_NO_FSM_SELECTED);
				return;
			} // if
			File file = Model.getPathToSaveFile();
			if(file == null) {
				Alerts.makeError(Alerts.ERROR_FILE_FORMAT);
				return;
			} // if
			// TODO: FIX THIS!!!
			//tsToSave.toTextFile(file.getParent(), file.getName());
		}); // setOnMouseClicked
		
		saveJPGBtn.setOnMouseClicked(e -> {
			// Gets the selected FSMs and prompts the user for the path to save it to.
			String fsmId = openTSBox.getSelectionModel().getSelectedItem();
			TransitionSystem<? extends Transition> fsmToSave = model.getTS(fsmId);
			if(fsmToSave == null) {
				Alerts.makeError(Alerts.ERROR_NO_FSM_SELECTED);
				return;
			} // if
			File file = Model.getPathToSaveFile();
			if(file == null) {
				Alerts.makeError(Alerts.ERROR_FILE_FORMAT);
				return;
			} // if
			FSMToDot.createImgFromFSM(fsmToSave, file.getName(), file.getParent(), model.getGraphVizConfigPath());
		}); // setOnMouseClicked
	} // makeSaveTSEventHandler()
} // class FileSettingsPane
