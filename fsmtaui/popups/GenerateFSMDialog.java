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
	
	Dialog<FSMParameters> dialog;
	GridPane optionGrid;
	TextField sizeStates, sizeMarked, sizeEvents, sizePaths, sizeInitial, sizeSecret, sizeUnobserv, sizeUncontrol, sizeMay, sizeMust;
	
	/**
	 * Creates a dialog box for getting information on what kind
	 * of FSM should be created.
	 * 
	 * @param inDeterministic Boolean representing if the new FSM should be a deterministic FSM (i.e.,
	 * only one initial state and no repeat states).
	 */
	
	public GenerateFSMDialog(String fsmClass) {
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
		Label sizeInitialLabel = new Label("Number of Initial States");
		sizeInitial = new TextField("3");
		Label sizeMayLabel = new Label("Max number of may transitions per state");
		sizeMay = new TextField("2");
		Label sizeMustLabel = new Label("Max number of must transitions per state");
		sizeMust = new TextField("1");
		
		// Add extra option if the type is non-deterministic:
		if(fsmClass.equals(FileSettingsPane.TS_TYPES.get(0))) {
			// If deterministic
			optionGrid.addColumn(0, sizeStatesLabel, sizeMarkedLabel, sizeEventsLabel, sizePathsLabel, sizeSecretLabel, sizeUnobservLabel, sizeUncontrolLabel);
			optionGrid.addColumn(1, sizeStates, sizeMarked, sizeEvents, sizePaths, sizeSecret, sizeUnobserv, sizeUncontrol);
		} else if(fsmClass.equals(FileSettingsPane.TS_TYPES.get(1))) {
			// If nondeterministic
			optionGrid.addColumn(0, sizeStatesLabel, sizeMarkedLabel, sizeEventsLabel, sizePathsLabel, sizeSecretLabel, sizeUnobservLabel, sizeUncontrolLabel, sizeInitialLabel);
			optionGrid.addColumn(1, sizeStates, sizeMarked, sizeEvents, sizePaths, sizeSecret, sizeUnobserv, sizeUncontrol, sizeInitial);
		} else if(fsmClass.equals(FileSettingsPane.TS_TYPES.get(2))) {
			// If modal specification
			optionGrid.addColumn(0, sizeStatesLabel, sizeMarkedLabel, sizeEventsLabel, sizeSecretLabel, sizeUncontrolLabel, sizeMayLabel, sizeMustLabel);
			optionGrid.addColumn(1, sizeStates, sizeMarked, sizeEvents, sizeSecret, sizeUncontrol, sizeMay, sizeMust);
		}
		
		// Add the options and buttons.
		DialogPane dPane = dialog.getDialogPane();
		dPane.setContent(optionGrid);
		dPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		// Sets up the return result as an FSMParameter object
		dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
            		try {
            			FSMParameters param = new FSMParameters();
            			// Use certain default parameters for deterministic FSMs
            			if(fsmClass.equals(FileSettingsPane.TS_TYPES.get(0))) {
            				// Deterministic
            				return param.setDeterministicParameters(
            						sizeStates.getText(), sizeMarked.getText(),
            						sizeEvents.getText(), sizePaths.getText(),
            						sizeSecret.getText(), sizeUnobserv.getText(),
            						sizeUncontrol.getText());
            			} else if(fsmClass.equals(FileSettingsPane.TS_TYPES.get(1))) {
            				// Non-deterministic
            				return param.setNonDeterministicParameters(sizeStates.getText(),
	            					sizeMarked.getText(), sizeEvents.getText(),
	            					sizePaths.getText(), sizeSecret.getText(),
	            					sizeUnobserv.getText(), sizeUncontrol.getText(), sizeInitial.getText());
            			} else if(fsmClass.equals(FileSettingsPane.TS_TYPES.get(2))) {
            				// Modal specification
            				return param.setModalSpecParameters(sizeStates.getText(),
            						sizeMarked.getText(), sizeEvents.getText(),
            						sizeSecret.getText(), sizeUncontrol.getText(),
            						sizeMay.getText(), sizeMust.getText());
            			}
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
		public int sizeMay;
		public int sizeMust;
		
		FSMParameters() {
			// Do nothing
		}
		
		FSMParameters setNonDeterministicParameters(String states, String marked, String events, String paths, String secret, String unobservable, String uncontrollable, String initial) {
			sizeStates = Integer.parseInt(states);
			sizeMarked = Integer.parseInt(marked);
			sizeEvents = Integer.parseInt(events);
			sizePaths = Integer.parseInt(paths);
			sizeSecret = Integer.parseInt(secret);
			sizeUnobserv = Integer.parseInt(unobservable);
			sizeUncontrol = Integer.parseInt(uncontrollable);
			sizeInitial = Integer.parseInt(initial);
			return this;
		} // setNonDeterministicParameters()
		
		FSMParameters setDeterministicParameters(String states, String marked, String events, String paths, String secret, String unobservable, String uncontrollable) {
			sizeStates = Integer.parseInt(states);
			sizeMarked = Integer.parseInt(marked);
			sizeEvents = Integer.parseInt(events);
			sizePaths = Integer.parseInt(paths);
			sizeSecret = Integer.parseInt(secret);
			sizeUnobserv = Integer.parseInt(unobservable);
			sizeUncontrol = Integer.parseInt(uncontrollable);
			sizeInitial = 1;
			return this;
		} // setDeterministicParameters()
		
		FSMParameters setModalSpecParameters(String states, String marked, String events, String secret, String uncontrollable, String may, String must) {
			sizeStates = Integer.parseInt(states);
			sizeMarked = Integer.parseInt(marked);
			sizeEvents = Integer.parseInt(events);
			sizeSecret = Integer.parseInt(secret);
			sizeUncontrol = Integer.parseInt(uncontrollable);
			sizeMay = Integer.parseInt(may);
			sizeMust = Integer.parseInt(must);
			return this;
		}
	} // static class FSMParameters
} // class GenerateFSMDialog
