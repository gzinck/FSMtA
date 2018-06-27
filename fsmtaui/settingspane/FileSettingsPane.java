package fsmtaui.settingspane;

import fsm.*;
import fsmtaui.Model;
import fsmtaui.popups.*;
import support.*;
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
	private Button newFSMBtn;
	/** Button for generating a new FSM file which is read in. */
	private Button genFSMBtn;
	/** Button for closing the selected FSMs. */
	private Button closeFSMBtn;
	/** Button for saving the selected FSMs as FSM files. */
	private Button saveFSMBtn;
	/** Button for saving the selected FSMs as JPG files. */
	private Button saveJPGBtn;
	/** Button for saving the selected FSMs as PDF files. */
	private Button savePDFBtn;
	/**
	 * Box with all the openFSMs listed. Double clicking on an element opens
	 * the FSM as a viewport, if it is not already.
	 */
	ListView<String> openFSMBox;
	
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
		titleLabel.getStyleClass().add("padded");
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
		makeNewFSMEventHandler();
		makeGenFSMEventHandler();
		makeSelectFSMEventHandler();
		makeCloseFSMEventHandler();
		makeSaveFSMEventHandler();
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
		newFSMBtn = new Button("Create Empty FSM");
		genFSMBtn = new Button("Generate New FSM");
		HBox fileBtns = new HBox(readInFileBtn, newFSMBtn, genFSMBtn);
		
		return new VBox(fsmName, fsmType, fileBtns);
	} // makeMainFileOptions()
	
	/**
	 * Makes the box containing all the FSMs that are currently open.
	 * 
	 * @return - VBox with all the FSMs currently open.
	 */
	private VBox makeOpenFSMBox() {
		Label openFSMBoxLabel = new Label("Open FSMs:");
		openFSMBox = new ListView<String>(model.getOpenFSMStrings());
		openFSMBox.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		return new VBox(openFSMBoxLabel, openFSMBox);
	} // makeOpenFSMBox()
	
	/**
	 * Creates a box with buttons for closing or saving the
	 * selected FSMs from the FSMBox.
	 * 
	 * @return - VBox with all the buttons for closing/saving FSMs.
	 */
	private VBox makeSaveBtns() {
		closeFSMBtn = new Button("Close selected FSM");
		saveFSMBtn = new Button("Save selected FSM as FSM file");
		saveJPGBtn = new Button("Save selected FSM as JPG file");
		savePDFBtn = new Button("Save selected FSM as PDF file");
		return new VBox(closeFSMBtn, saveFSMBtn, saveJPGBtn, savePDFBtn);
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
	 * Checks if the FSM type was selected; if not, return false.
	 * 
	 * @return - True if the user selected an FSM type; else, false.
	 */
	private boolean checkIfValidFSMType() {
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
		readInFileBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				// Get the name of the new FSM being read in, and if the name
				// is unique continue to add it
				String newFSMName = fsmNameField.getText();
				if(model.checkIfValidFSMId(newFSMName) && checkIfValidFSMType()) {
					try {
						// Get the FSM file and create the FSM
						File file = getFileFromUser();
						FSM newFSM = null;
						
						String fsmClass = fsmTypeChoiceBox.getSelectionModel().getSelectedItem();
						if(fsmClass.equals("Deterministic")) {
							newFSM = new DetObsContFSM(file, newFSMName);
						} else if(fsmClass.equals("Non-Deterministic")) {
							newFSM = new NonDetObsContFSM(file, newFSMName);
						}
						model.addFSM(newFSM);
						fsmNameField.setText("");
					} catch(FileNotFoundException exception) {
						// Do nothing, since error message already produced in another method.
					} catch(Exception exception) {
						// Show error that file format was not legal.
						Alerts.makeError(Alerts.ERROR_FILE_FORMAT);
					} // try/catch
				} // if valid FSM name and type
			} // handle()
		}); // setOnAction()
	} // makeNewFSMFromFileEventHandler()
	
	/**
	 * Makes an event handler when clicking on the newFSMBtn such that
	 * it creates a new, empty FSM with the specified name.
	 */
	private void makeNewFSMEventHandler() {
		newFSMBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				String newFSMName = fsmNameField.getText();
				// If the new FSM name is unique and the type is valid, make the new FSM.
				if(model.checkIfValidFSMId(newFSMName) && checkIfValidFSMType()) {
					FSM newFSM = null;
					
					String fsmClass = fsmTypeChoiceBox.getSelectionModel().getSelectedItem();
					if(fsmClass.equals("Deterministic")) {
						newFSM = new DetObsContFSM(newFSMName);
					} else if(fsmClass.equals("Non-Deterministic")) {
						newFSM = new NonDetObsContFSM(newFSMName);
					}
					if(newFSM != null) {
						model.addFSM(newFSM);
						fsmNameField.setText("");
					}
				} // if
			} // handle(ActionEvent)
		}); // setOnAction()
	} // makeNewFSMEventHandler()
	
	/**
	 * Generates a new FSM by asking the user various parameters (number of states,
	 * number of marked states, number of initial states).
	 */
	private void makeGenFSMEventHandler() {
		genFSMBtn.setOnAction(e -> {
			String newFSMName = fsmNameField.getText();
			// If the new FSM name is unique and the type is valid, make the new FSM.
			if(model.checkIfValidFSMId(newFSMName) && checkIfValidFSMType()) {
				FSM newFSM = null;
				
				String fsmClass = fsmTypeChoiceBox.getSelectionModel().getSelectedItem();
				if(fsmClass.equals("Deterministic")) {
					// Deterministic, basic FSM
					GenerateFSMDialog.FSMParameters parameters = GenerateFSMDialog.getFSMParametersFromUser(FSM_TYPE.DETERMINISTIC, false);
					if(parameters != null) {
						File fsmFile = new File(GenerateFSM.createNewDeterministicFSM(
								parameters.sizeStates, parameters.sizeMarked, parameters.sizeEvents,
								parameters.sizePaths, newFSMName, model.getWorkingDirectoryString() + "/"));
						newFSM = new DetFSM(fsmFile, newFSMName);
					} // if
				} else if(fsmClass.equals("Non-Deterministic")) {
					// NonDeterministic with observability
					GenerateFSMDialog.NonDetObsFSMParameters parameters =
							(GenerateFSMDialog.NonDetObsFSMParameters) GenerateFSMDialog.getFSMParametersFromUser(FSM_TYPE.NON_DETERMINISTIC, true);
					if(parameters != null) {
						File fsmFile = new File(GenerateFSM.createNewObservableFSM(
								parameters.sizeStates, parameters.sizeMarked, parameters.sizeEvents,
								parameters.sizePaths, parameters.sizeInitial, parameters.sizeUnobserv,
								newFSMName, model.getWorkingDirectoryString() + "/"));
						newFSM = new NonDetObsFSM(fsmFile, newFSMName);
					} // if
				} // if/else if
				// TODO: add the other kinds of FSMs we need to generate
				
				if(newFSM != null) {
					model.addFSM(newFSM);
					fsmNameField.setText("");
				} // if
			} // if
		}); // setOnAction()
	} // makeNewFSMEventHandler()
	
	/**
	 * Creates an event handler to deal with when a user double-clicks
	 * on an openFSM in the openFSMBox in the FileSettingsPane.
	 * When this occurs, handle() is called, which gets the selected FSMs
	 * and opens them in the ContentPane as viewports for their graphs
	 * computed by GraphViz.
	 */
	private void makeSelectFSMEventHandler() {
		openFSMBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				if(e.getButton() == MouseButton.PRIMARY && e.getClickCount() > 1) {
					ObservableList<String> selected = openFSMBox.getSelectionModel().getSelectedItems();
					for(String curr : selected) {
						FSM currFSM = model.getFSM(curr);
						if(currFSM != null) {
							model.addViewport(currFSM, curr);
						} // if
					} // for
				} // if
			} // handle(MouseEvent)
		}); // setOnMouseClicked(EventHandler<MouseEvent>)
	} // makeSelectFSMEventHandler()
	
	/**
	 * Creates an event handler for the closeFSMBtn that removes all the
	 * FSMs the user selected in the openFSMBox (a ListView object) from
	 * the openFSMs list.
	 */
	private void makeCloseFSMEventHandler() {
		closeFSMBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				ObservableList<String> selected = openFSMBox.getSelectionModel().getSelectedItems();
				model.removeFSM(selected.get(0));
			} // handle(ActionEvent)
		}); // setOnAction(EventHandler<ActionEvent>)
	} // makeCloseFSMEventHandler()
	
	/**
	 * Creates an event handler for the saveFSMBtn to save an FSM as a
	 * FSM file in the proprietary FSM format.
	 * This can be used to read in the FSM into FSMtA later on.
	 */
	private void makeSaveFSMEventHandler() {
		saveFSMBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				// Gets the selected FSMs and prompts the user for the path to save it to.
				String fsmId = openFSMBox.getSelectionModel().getSelectedItem();
				FSM fsmToSave = model.getFSM(fsmId);
				if(fsmToSave == null) {
					Alerts.makeError(Alerts.ERROR_NO_FSM_SELECTED);
					return;
				} // if
				File file = Model.getPathToSaveFile();
				if(file == null) {
					Alerts.makeError(Alerts.ERROR_FILE_FORMAT);
					return;
				} // if
				fsmToSave.toTextFile(file.getParent(), file.getName());
			} // handle(MouseEvent)
		}); // setOnMouseClicked(EventHandler<MouseEvent>)
	} // makeSaveFSMEventHandler()
} // class FileSettingsPane
