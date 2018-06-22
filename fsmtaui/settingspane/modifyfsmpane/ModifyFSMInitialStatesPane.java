package fsmtaui.settingspane.modifyfsmpane;

import fsm.*;
import fsm.attribute.*;
import fsmtaui.FSMViewport;
import fsmtaui.Model;
import fsmtaui.popups.Alerts;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;

/**
 * Class extending the javafx VBox element which stores all the
 * options for adding and removing states from the open FSM.
 * 
 * @author Mac Clevinger and Graeme Zinck
 *
 */
public class ModifyFSMInitialStatesPane extends VBox {
	/** String which is used for the title of the section when the current FSM
	 * is deterministic.
	 */
	private static final String LABEL_STR = "Change Initial State";
	
	private static final String TOGGLE_STATE_STR = "Toggle Initial State";
	private static final String CHANGE_STATE_STR = "Change Initial State";
	
	/** Model containing all the important information to display in the GUI. */
	private Model model;
	/** Label for the section (changes depending on the type of FSM). */
	private Label initialOptionsLabel;
	/** TextField to name an initial state as the user specifies. */
	private TextField initialStateNameField;
	/** Button to add/remove an initial state to the FSM open in the current tab. */
	private Button toggleInitialStateBtn;
	
	/**
	 * Creates a box with all the initial state options in it.
	 * Allows the user to add and remove initial states.
	 * 
	 * @param inModel - Model containing all the important information
	 * to display in the GUI.
	 */
	public ModifyFSMInitialStatesPane(Model inModel) {
		model = inModel;
		
		initialOptionsLabel = new Label(LABEL_STR);
		
		Label stateNameLabel = new Label("State Name:");
		initialStateNameField = new TextField();
		HBox stateName= new HBox(stateNameLabel, initialStateNameField);
		
		toggleInitialStateBtn = new Button(TOGGLE_STATE_STR);
		
		getChildren().addAll(initialOptionsLabel, stateName, toggleInitialStateBtn);
		
		makeToggleInitialStateEventHandler();
		refreshOptions();
	} // ModifyFSMInitialStatesPane(Model)
	
	/**
	 * Creates the event handler for changing initial states.
	 */
	private void makeToggleInitialStateEventHandler() {
		initialStateNameField.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER) toggleInitialStateBtn.fire();
		});
		toggleInitialStateBtn.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER) toggleInitialStateBtn.fire();
		});
		toggleInitialStateBtn.setOnAction(e -> {
			String state = initialStateNameField.getText();
			if(state.equals("")) {
				Alerts.makeError(Alerts.ERROR_TOGGLE_INITIAL_NO_NAME);
			} else {
				// Then no errors
				FSM currFSM = model.getCurrFSM();
				if(currFSM == null) {
					// Then cannot add an event
					Alerts.makeError(Alerts.ERROR_TOGGLE_INITIAL_NO_FSM);
				} else {
					// If nondeterministic, then add/remove the initial state;
					// otherwise, replace the pre-existing initial state.
					if(currFSM instanceof NonDeterministic) {
						if(currFSM.hasInitialState(state)) {
							currFSM.removeInitialState(state);
						} else {
							currFSM.addInitialState(state);
						} // if/else
					} else {
						currFSM.addInitialState(state);
					} // if/else
					initialStateNameField.setText("");
					initialStateNameField.requestFocus();
					model.refreshViewport();
				} // if/else
			} // if/else
		}); // setOnAction(EventHandler<ActionEvent>)
	} // makeToggleInitialStateEventHandler()
	
	/**
	 * Shows the options in the pane for a deterministic FSM.
	 */
	private void showDeterministicOptions() {
		toggleInitialStateBtn.setText(CHANGE_STATE_STR);
	} // showDeterministicOptions()
	
	/**
	 * Shows the options in the pane for a non-deterministic FSM.
	 */
	private void showNonDeterministicOptions() {
		toggleInitialStateBtn.setText(TOGGLE_STATE_STR);
	} // showNonDeterministicOptions()
	
	/**
	 * Handles whenever the current FSM changes. When the FSM changes, if
	 * the new FSM is nondeterministic, then the options reflect that.
	 */
	private void refreshOptions() {
		model.getOpenFSMTabs().getSelectionModel().selectedItemProperty().addListener(
			(ObservableValue<? extends Tab> value, Tab oldTab, Tab newTab) -> {
				if(newTab != null) {
					// Only show add/remove initial state if the FSM is nondeterministic;
					// otherwise, it should show change initial state instead.
					FSM fsm = ((FSMViewport) newTab.getContent()).getFSM();
					if(fsm instanceof NonDeterministic) {
						showNonDeterministicOptions();
					} else {
						showDeterministicOptions();
					} // if/else
				} // if
			});
	} // refreshOptions()
} // class ModifyFSMInitialStatesPane
