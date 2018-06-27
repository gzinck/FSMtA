package fsmtaui;

import fsm.*;
import fsmtaui.settingspane.SettingsPane;

import java.io.File;
import java.util.*;
import javafx.application.*;
import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;
import javafx.scene.layout.*;
import java.io.*;

/**
 * This class creates a user interface for a Finite State Machine. Key features include:
 * 1) Reading, writing, generating, saving, and displaying FSMs.
 * 2) Modifying FSMs.
 * 3) Performing operations on fSMs.
 * 
 * This is part of the fsmtaui package, which holds all the UI elements.
 * 
 * @author Mac Clevinger and Graeme Zinck
 *
 */

public class FSMtAUI extends Application {
	/** String representing the name for the application, appearing at the top of the window. */
	private static final String APP_NAME = "FSMtA";
	/** Integer for the number of pixels wide and high the content (i.e., the FSM's visual representation) will be. */
	private static final int CONTENT_SIZE = 600;
	/** Integer for the number of pixels wide the settings sidebar will be. */
	private static final int SIDEBAR_WIDTH = 360;
	/** Integer for the number of pixels high the settings sidebar will be. */
	private static final int SIDEBAR_HEIGHT = 600;
	/** String used to ask the user for a working directory when beginning an FSMtA session. */
	private static final String CHOOSE_WORKING_DIRECTORY_MSG = "Choose a working directory for your FSMtA session in the following file chooser.\nPlease note that FSMtA requires that you install GraphViz before use.";
	/** String used for the title when asking the user for a working directory. */
	private static final String CHOOSE_WORKING_DIRECTORY_TITLE = "Choose Working Directory";
	/** String used for when the user fails to choose a working directory. */
	private static final String CHOOSE_WORKING_DIRECTORY_FAIL_MSG = "You must choose a working directory if you wish to start an FSMtA session. Do you wish to quit the application?";
	/** String used for the title when notifying the user that choosing a working directory failed. */
	private static final String CHOOSE_WORKING_DIRECTORY_FAIL_TITLE = "Choose Working Directory Failed";
	
	/** Observable ArrayList of FSM objects that are open at any given time, as shown in the settings sidebar. */
	private ObservableList<FSM> openFSMs;
	/** 
	 * Observable ArrayList of the names of the FSM objects in openFSMs, used for various UI elements. It is
	 * automatically updated whenever the original FSM list is updated.
	 */
	private ObservableList<String> openFSMStrings;
	/** Model object modeling everything that happens in the UI. It has the openFSMs
	 * and Strings, as well as the working directory, stored to pass to other objects. */
	private Model model;
	
	/** Stage used for the GUI. */
	private Stage stage;
	/** BorderPane used as the root for all GUI elements. */
	private BorderPane root;
	/** Scene set on the stage. */
	private Scene scene;
	/** SettingsPane object which holds all the tabs of settings tabs with options for the user. This is a left sidebar. */
	private SettingsPane settingsPane;
	/** ContentPane object which holds all the tabs of FSMViewports that are open, with visual representations of the FSMs. */
	private ContentPane content;
	
	/** File object representing the directory being used for temporary files. */
	private File workingDirectory;
	/** Configuration path for GraphViz, which is calculated based on the location of files in the project. */
	private String graphVizConfigPath;
	
	/**
	 * Starts the application.
	 * @param args
	 */
	public static void main(String[] args)
	{
		launch(args);
	} // main(String[])
	
	/**
	 * Starts the application and places all the GUI elements in
	 * the window.
	 * 
	 * @param inStage - Stage to place everything in the application
	 */
	public void start(Stage inStage)
	{
		// Create a list of FSMs
		ArrayList<FSM> fsmSet = new ArrayList<FSM>();
        // ... and add observability by wrapping it with ObservableList.
		openFSMs = FXCollections.observableList(fsmSet);
		
		stage = inStage;
		root = new BorderPane();
		root.setFocusTraversable(true);
		scene = new Scene(root);
		scene.getStylesheets().add(this.getClass().getResource("styles.css").toExternalForm());
		
		// Get the working directory from user
		workingDirectory = getWorkingDirectory();
		
		// Only run the application if we got a working directory
		if(workingDirectory != null) {
			
			// Find where GraphViz has its configuration file
			graphVizConfigPath = this.getClass().getClassLoader().getResource("config.properties").getPath().replaceAll("%20", " ");
			
			// Make the model to pass to all the elements of the UI
			model = new Model(openFSMs, openFSMStrings, workingDirectory, graphVizConfigPath);
			
			// Set up the pane for all the tabs of settings
			settingsPane = new SettingsPane(model);
			settingsPane.setPrefSize(SIDEBAR_WIDTH, SIDEBAR_HEIGHT);
			root.setLeft(settingsPane);
			
			// Set up the pane for all the tabs of open FSM visualizations.
			content = new ContentPane(model);
			content.setPrefSize(CONTENT_SIZE, CONTENT_SIZE);
			root.setCenter(content);
			
			// Show the stage
			stage.setScene(scene);
			stage.setTitle(APP_NAME);
			stage.show();
		} // if
	} // start(Stage)
	
	/**
	 * Gets a directory from the user.
	 * 
	 * @return - The File object representing the directory that the user
	 * chose.
	 * @throws FileNotFoundException - Throws a FileNotFoundException if there
	 * was no directory selected by the user. 
	 */
	private File getDirectoryFromUser() throws FileNotFoundException {
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle(CHOOSE_WORKING_DIRECTORY_TITLE);
		File file = chooser.showDialog(stage);
		if(file != null) {
			return file;
		} else {
			throw new FileNotFoundException("No directory chosen by user.");
		} // if/else
	} // getDirectoryFromUser()
	
	/**
	 * Gets the working directory from the user. If the user refuses to
	 * select a working directory, it prompts the user if s/he wishes to
	 * quit the application or try selecting again.
	 * 
	 * @return - A File representing the working directory selected, or null
	 * if the user wishes to quit.
	 */
	private File getWorkingDirectory() {
		// Alerts the user of what's about to happen
		Alert request = new Alert(Alert.AlertType.INFORMATION, CHOOSE_WORKING_DIRECTORY_MSG);
		request.setHeaderText(CHOOSE_WORKING_DIRECTORY_TITLE);
		request.showAndWait();
		File file = null;
		boolean quit = false;
		
		// Keep prompting if no input and user does not wish to quit
		while(file == null && !quit) {
			try {
				file = getDirectoryFromUser();
			} catch(FileNotFoundException e) {
				
				// Since there was no selected directory, asks user if s/he wishes to
				// quit or try again.
				Alert quitAlert = new Alert(Alert.AlertType.CONFIRMATION, CHOOSE_WORKING_DIRECTORY_FAIL_MSG);
				quitAlert.setHeaderText(CHOOSE_WORKING_DIRECTORY_FAIL_TITLE);
				ButtonType quitAppBtn = new ButtonType("Quit FSMtA");
				ButtonType setDirBtn = new ButtonType("Set Working Directory");
				quitAlert.getButtonTypes().setAll(quitAppBtn, setDirBtn);
				Optional<ButtonType> result = quitAlert.showAndWait();
				
				// If user wanted to quit, quit.
				if(result.get() == quitAppBtn) {
					quit = true;
				} // if
			} // try/catch
		} // while
		
		return file;
	} // getWorkingDirectory()
} // FSMtAUI class