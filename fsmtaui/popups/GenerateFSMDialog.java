package fsmtaui.popups;

import java.util.NoSuchElementException;
import java.util.Optional;

import fsmtaui.settingspane.FileSettingsPane;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * Provides methods to create FSM parameters from the user in order to
 * generate an FSM.
 *  
 * @author Mac Clevinger and Graeme Zinck
 *
 */

public class GenerateFSMDialog {
	/**
	 * Asks the user for parameters for creating a DeterministicFSM and
	 * returns these as an FSMParameters object (a class at the bottom
	 * of this one).
	 * 
	 * @param type - The type of FSM to get the parameters for.
	 * @return - FSMParameters object representing the selected parameters,
	 * or null if some parameters given were illegal.
	 */
	public static FSMParameters getFSMParametersFromUser(String type) {
		// Create the dialog box
		Dialog<FSMParameters> dialog = new Dialog<FSMParameters>();
		dialog.setTitle("Select FSM Parameters");
		dialog.setHeaderText("Select parameters to generate a new FSM:");
		
		// Create the grid for all the options
		GridPane dGrid = new GridPane();
		Label sizeStatesLabel = new Label("Number of States");
		TextField sizeStates = new TextField();
		Label sizeMarkedLabel = new Label("Number of Marked States");
		TextField sizeMarked = new TextField();
		Label sizeEventsLabel = new Label("Number of Events");
		TextField sizeEvents = new TextField();
		Label sizePathsLabel = new Label("Max Number of Paths Leaving a State");
		TextField sizePaths = new TextField();
		dGrid.addColumn(0, sizeStatesLabel, sizeMarkedLabel, sizeEventsLabel, sizePathsLabel);
		dGrid.addColumn(1, sizeStates, sizeMarked, sizeEvents, sizePaths);
		
		// Add options if the type is non-deterministic:
		Label sizeInitialLabel = new Label("Number of Initial States");
		TextField sizeInitial = new TextField();
		if(type.equals(FileSettingsPane.FSM_TYPES_STR[1]) || type.equals(FileSettingsPane.FSM_TYPES_STR[2])) {
			dGrid.addRow(4, sizeInitialLabel, sizeInitial);
		} // if non-deterministic
		
		// Add options if the type is observable
		Label sizeUnobservLabel = new Label("Number of unobservable events");
		TextField sizeUnobserv = new TextField();
		if(type.equals(FileSettingsPane.FSM_TYPES_STR[2])) {
			dGrid.addRow(5, sizeUnobservLabel, sizeUnobserv);
		} // if non-deterministic
		
		// Add the options and buttons.
		DialogPane dPane = dialog.getDialogPane();
		dPane.setContent(dGrid);
		dPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		// Sets up the return result as an FSMParameter object
		dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
            		try {
            			if(type.equals(FileSettingsPane.FSM_TYPES_STR[0])) {
	            			return new FSMParameters(sizeStates.getText(),
	            					sizeMarked.getText(), sizeEvents.getText(),
	            					sizePaths.getText());
            			} else if(type.equals(FileSettingsPane.FSM_TYPES_STR[1])) {
            				return new NonDeterministicFSMParameters(sizeStates.getText(),
            						sizeMarked.getText(), sizeEvents.getText(),
            						sizePaths.getText(), sizeInitial.getText());
            			} else if(type.equals(FileSettingsPane.FSM_TYPES_STR[2])) {
            				return new ObservableFSMParameters(sizeStates.getText(),
            						sizeMarked.getText(), sizeEvents.getText(),
            						sizePaths.getText(), sizeInitial.getText(),
            						sizeUnobserv.getText());
            			} // if/else if
            		} catch (NumberFormatException e) {
            			// If some parameters were NAN, error box
            			Alerts.makeError(Alerts.ERROR_ILLEGAL_FSM_PARAMETERS);
            		} // try/catch
            }// if
            return null;
        });
		
		// Shows the dialog and returns results
		Optional<FSMParameters> optionalResult = dialog.showAndWait();
		try {
			return optionalResult.get();
		} catch(NoSuchElementException e) {
			return null;
		} // try/catch
	} // getFSMParametersFromUser()
	
	/**
	 * Private class that allows the javafx dialog box to return a set
	 * of parameters for a new FSM to generate.
	 * 
	 * @author Mac Clevinger and Graeme Zinck
	 *
	 */
	public static class FSMParameters {
		public int sizeStates;
		public int sizeMarked;
		public int sizeEvents;
		public int sizePaths;
		
		FSMParameters(String states, String marked, String events, String paths) {
			sizeStates = Integer.parseInt(states);
			sizeMarked = Integer.parseInt(marked);
			sizeEvents = Integer.parseInt(events);
			sizePaths = Integer.parseInt(paths);
		} // FSMParameters(String, String, String, String)
	} // static class FSMParameters

	/**
	 * Allows the javafx dialog box to return a set
	 * of parameters for a new non-deterministic FSM to generate.
	 * 
	 * @author Mac Clevinger and Graeme Zinck
	 *
	 */
	public static class NonDeterministicFSMParameters extends FSMParameters {
		public int sizeInitial;
		
		NonDeterministicFSMParameters(String states, String marked, String events, String paths, String initial) {
			super(states, marked, events, paths);
			sizeInitial = Integer.parseInt(initial);
		} // NonDeterministicFSMParameters(String, String, String, String, String)
	} // static class NonDeterministicFSMParameters
	
	/**
	 * Allows the javafx dialog box to return a set of parameters
	 * for a new non-deterministic observable FSM to generate.
	 * 
	 * @author Mac Clevinger and Graeme Zinck
	 *
	 */
	public static class ObservableFSMParameters extends NonDeterministicFSMParameters {
		public int sizeUnobserv;
		
		ObservableFSMParameters(String states, String marked, String events, String paths, String initial, String unobservable) {
			super(states, marked, events, paths, initial);
			sizeUnobserv = Integer.parseInt(unobservable);
		} // NonDeterministicFSMParameters(String, String, String, String, String)
	} // static class ObservableFSMParameters
} // class GenerateFSMDialog
