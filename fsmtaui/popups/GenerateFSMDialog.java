package fsmtaui.popups;

import java.util.NoSuchElementException;
import java.util.Optional;

import fsmtaui.settingspane.FileSettingsPane;
import fsmtaui.settingspane.FileSettingsPane.FSM_TYPE;
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
	
	Dialog<FSMParameters> dialog;
	GridPane optionGrid;
	TextField sizeStates, sizeMarked, sizeEvents, sizePaths, sizeInitial, sizeSecret, sizeUnobserv, sizeUncontrol;
	boolean deterministic;
	
	/**
	 * Creates a dialog box for getting information on what kind
	 * of FSM should be created.
	 * 
	 * @param inDeterministic Boolean representing if the new FSM should be a deterministic FSM (i.e.,
	 * only one initial state and no repeat states).
	 */
	
	public GenerateFSMDialog(boolean inDeterministic) {
		deterministic = inDeterministic;
		// Create the dialog box
		dialog = new Dialog<FSMParameters>();
		dialog.setTitle("Select FSM Parameters");
		dialog.setHeaderText("Select parameters to generate a new FSM:");
		
		// Create the grid for all the options
		optionGrid = new GridPane();
		Label sizeStatesLabel = new Label("Number of States");
		sizeStates = new TextField("10");
		Label sizeMarkedLabel = new Label("Number of Marked States");
		sizeMarked = new TextField("3");
		Label sizeEventsLabel = new Label("Number of Events");
		sizeEvents = new TextField("2");
		Label sizePathsLabel = new Label("Max Number of Paths Leaving a State");
		sizePaths = new TextField("2");
		Label sizeSecretLabel = new Label("Number of Secret States");
		sizeSecret = new TextField("3");
		Label sizeUnobservLabel = new Label("Number of Unobservable Events");
		sizeUnobserv = new TextField("1");
		Label sizeUncontrolLabel = new Label("Number of Uncontrollable Events");
		sizeUncontrol = new TextField("2");
		
		optionGrid.addColumn(0, sizeStatesLabel, sizeMarkedLabel, sizeEventsLabel, sizePathsLabel, sizeSecretLabel, sizeUnobservLabel, sizeUncontrolLabel);
		optionGrid.addColumn(1, sizeStates, sizeMarked, sizeEvents, sizePaths, sizeSecret, sizeUnobserv, sizeUncontrol);
		
		// Add extra option if the type is non-deterministic:
		if(!deterministic) {
			Label sizeInitialLabel = new Label("Number of Initial States");
			sizeInitial = new TextField("3");
			optionGrid.addRow(7, sizeInitialLabel, sizeInitial);
		} // if non-deterministic
		
		// Add the options and buttons.
		DialogPane dPane = dialog.getDialogPane();
		dPane.setContent(optionGrid);
		dPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		// Sets up the return result as an FSMParameter object
		dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
            		try {
            			// Use certain default parameters for deterministic FSMs
            			if(deterministic) {
	            			return new FSMParameters(sizeStates.getText(),
	            					sizeMarked.getText(), sizeEvents.getText(),
	            					sizePaths.getText(), sizeSecret.getText(),
	            					sizeUnobserv.getText(), sizeUncontrol.getText(), "1");
            			} else {
            				return new FSMParameters(sizeStates.getText(),
	            					sizeMarked.getText(), sizeEvents.getText(),
	            					sizePaths.getText(), sizeSecret.getText(),
	            					sizeUnobserv.getText(), sizeUncontrol.getText(), sizeInitial.getText());
            			} // if/else if
            		} catch (NumberFormatException e) {
            			// If some parameters were NAN, error box
            			Alerts.makeError(Alerts.ERROR_ILLEGAL_FSM_PARAMETERS);
            		} // try/catch
            }// if
            return null;
        });
	} // GenerateFSMDialog
	
	/**
	 * Gets the user's FSM parameters that they specify in the dialog and returns it.
	 * 
	 * @return FSMParameters object with all the information needed to generate an FSM.
	 */
	public FSMParameters getFSMParametersFromUser() {
		// Shows the dialog and returns results
		Optional<FSMParameters> optionalResult = dialog.showAndWait();
		try {
			return optionalResult.get();
		} catch(NoSuchElementException e) {
			return null;
		} // try/catch
	} // getFSMParametersFromUser()
	
	/**
	 * Class that allows the javafx dialog box to return a set
	 * of parameters for a new FSM to generate.
	 * 
	 * @author Mac Clevinger and Graeme Zinck
	 *
	 */
	public class FSMParameters {
		public int sizeStates;
		public int sizeMarked;
		public int sizeEvents;
		public int sizePaths;
		public int sizeSecret;
		public int sizeUnobserv;
		public int sizeUncontrol;
		public int sizeInitial;
		
		FSMParameters(String states, String marked, String events, String paths, String secret, String unobservable, String uncontrollable, String initial) {
			sizeStates = Integer.parseInt(states);
			sizeMarked = Integer.parseInt(marked);
			sizeEvents = Integer.parseInt(events);
			sizePaths = Integer.parseInt(paths);
			sizeSecret = Integer.parseInt(secret);
			sizeUnobserv = Integer.parseInt(unobservable);
			sizeUncontrol = Integer.parseInt(uncontrollable);
			sizeInitial = Integer.parseInt(initial);
		} // FSMParameters
	} // static class FSMParameters
} // class GenerateFSMDialog
