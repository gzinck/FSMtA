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
 * This class provides methods to create FSM parameters from the user in order to generate an FSM.
 *  
 * This class is a part of the fsmtaui.popups package.
 *  
 * @author Mac Clevinger and Graeme Zinck
 */

public class GenerateFSMDialog {
	
//---  Instance Variables   -------------------------------------------------------------------
	
	//TODO: Fill in comments
	
	/** Dialog<<r>FSMParameters> instance variable object */
	Dialog<FSMParameters> dialog;
	/** GridPane instance variable object */
	GridPane optionGrid;
	/** TextField instance variable objects */
	TextField sizeStates, sizeMarked, sizeEvents, sizePaths, sizeInitial, sizeSecret, sizeUnobserv, sizeAttacker, sizeUncontrol, sizeMay, sizeMust;
	
//---  Constructors   -------------------------------------------------------------------------
	
	/**
	 * Constructor for a GenerateFSMDialog object that creates a dialog box for getting information
	 * on what kind of FSM should be created.
	 * 
	 * @param fsmClass - String object that TODO: Fill in
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
		Label sizeUnobservLabel = new Label("Number of Unobservable Events to the System");
		sizeUnobserv = new TextField("1");
		Label sizeAttackerLabel = new Label("Number of Unobservable Events to the Attacker");
		sizeAttacker = new TextField("1");
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
			optionGrid.addColumn(0, sizeStatesLabel, sizeMarkedLabel, sizeEventsLabel, sizePathsLabel, sizeSecretLabel, sizeUnobservLabel, sizeAttackerLabel, sizeUncontrolLabel);
			optionGrid.addColumn(1, sizeStates, sizeMarked, sizeEvents, sizePaths, sizeSecret, sizeUnobserv, sizeAttacker, sizeUncontrol);
		} else if(fsmClass.equals(FileSettingsPane.TS_TYPES.get(1))) {
			// If nondeterministic
			optionGrid.addColumn(0, sizeStatesLabel, sizeMarkedLabel, sizeEventsLabel, sizePathsLabel, sizeSecretLabel, sizeUnobservLabel, sizeAttackerLabel, sizeUncontrolLabel, sizeInitialLabel);
			optionGrid.addColumn(1, sizeStates, sizeMarked, sizeEvents, sizePaths, sizeSecret, sizeUnobserv, sizeAttacker, sizeUncontrol, sizeInitial);
		} else if(fsmClass.equals(FileSettingsPane.TS_TYPES.get(2))) {
			// If modal specification
			optionGrid.addColumn(0, sizeStatesLabel, sizeMarkedLabel, sizeEventsLabel, sizeSecretLabel, sizeUncontrolLabel, sizeUnobservLabel, sizeAttackerLabel, sizeMayLabel, sizeMustLabel);
			optionGrid.addColumn(1, sizeStates, sizeMarked, sizeEvents, sizeSecret, sizeUnobserv, sizeAttacker, sizeUncontrol, sizeMay, sizeMust);
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
	            					sizeUnobserv.getText(), sizeAttacker.getText(), 
	            					sizeUncontrol.getText(), sizeInitial.getText());
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
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	/**
	 * Getter method that requests the user's FSM parameters that they specify in the dialog and returns it.
	 * 
	 * @return - Returns a FSMParameters object with all the information needed to generate an FSM.
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
	
//---  Supplementary Classes   ----------------------------------------------------------------
	
	/**
	 * Class that allows the javafx dialog box to return a set
	 * of parameters for a new FSM to generate.
	 * 
	 * This class is a part of the fsmtaui.popups package.
	 * 
	 * @author Mac Clevinger and Graeme Zinck
	 *
	 */

	public class FSMParameters {
		
		//-- Instance Variables  ------------------------
		
		//TODO: Fill these in
		
		/** integer instance variable */
		public int sizeStates;
		/** integer instance variable */ 
		public int sizeMarked;
		/** integer instance variable */
		public int sizeEvents;
		/** integer instance variable */
		public int sizePaths;
		/** integer instance variable */
		public int sizeSecret;
		/** integer instance variable */
		public int sizeUnobserv;
		/** integer instance variable */
		public int sizeAttacker;
		/** integer instance variable */
		public int sizeUncontrol;
		/** integer instance variable */
		public int sizeInitial;
		/** integer instance variable */
		public int sizeMay;
		/** integer instance variable */
		public int sizeMust;
		
		//-- Constructors  ------------------------------
		
		/**
		 *  TODO:
		 */
		
		FSMParameters() {
			// Do nothing
		}
		
		//-- Setter Methods  ----------------------------
		
		/**
		 * TODO:
		 * 
		 * @param states
		 * @param marked
		 * @param events
		 * @param paths
		 * @param secret
		 * @param unobservable
		 * @param attacker
		 * @param uncontrollable
		 * @param initial
		 * @return
		 */
		
		FSMParameters setNonDeterministicParameters(String states, String marked, String events, String paths, String secret, String unobservable, String attacker, String uncontrollable, String initial) {
			sizeStates = Integer.parseInt(states);
			sizeMarked = Integer.parseInt(marked);
			sizeEvents = Integer.parseInt(events);
			sizePaths = Integer.parseInt(paths);
			sizeSecret = Integer.parseInt(secret);
			sizeUnobserv = Integer.parseInt(unobservable);
			sizeAttacker = Integer.parseInt(attacker);
			sizeUncontrol = Integer.parseInt(uncontrollable);
			sizeInitial = Integer.parseInt(initial);
			return this;
		} // setNonDeterministicParameters()
		
		/**
		 * TODO:
		 * 
		 * @param states
		 * @param marked
		 * @param events
		 * @param paths
		 * @param secret
		 * @param unobservable
		 * @param uncontrollable
		 * @return
		 */
		
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
		
		/**
		 * TODO:
		 * 
		 * @param states
		 * @param marked
		 * @param events
		 * @param secret
		 * @param uncontrollable
		 * @param may
		 * @param must
		 * @return
		 */
		
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