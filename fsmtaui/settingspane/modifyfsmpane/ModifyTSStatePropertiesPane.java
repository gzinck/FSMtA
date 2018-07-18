package fsmtaui.settingspane.modifyfsmpane;

import fsm.*;
import fsm.attribute.*;
import fsmtaui.Model;
import fsmtaui.popups.Alerts;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import support.transition.Transition;

/**
 * Class extending the javafx VBox element which stores all the
 * options for adding and removing states from the open TransitionSystem.
 * 
 * @author Mac Clevinger and Graeme Zinck
 *
 */
public class ModifyTSStatePropertiesPane extends VBox {
	/** String title for the section. */
	private static final String LABEL_STR = "Modify State Properties";
	/** String name for the button to toggle initial states. */
	private static final String TOGGLE_INITIAL_STR = "Toggle Initial State";
	/** String name for the button to toggle marked states. */
	private static final String TOGGLE_MARKED_STR = "Toggle Marked State";
	/** String name for the button to toggle secret states. */
	private static final String TOGGLE_SECRET_STR = "Toggle Secret State";
	/** String name for the button to toggle bad states. */
	private static final String TOGGLE_BAD_STR = "Toggle Bad State";
	
	/** String name for the button to mark all states. */
	private static final String MARK_ALL_STR = "Mark All States";
	/** String name for the button to unmark all states. */
	private static final String UNMARK_ALL_STR = "Unmark All States";
	/** String name for the button to make bad states good. */
	private static final String MAKE_ALL_GOOD_STR = "Make All States Good";
	/** String name for the button to remove bad states from the FSM. */
	private static final String REMOVE_ALL_BAD_STR = "Remove Bad States";
	/** String name for the button to rename all states in the FSM. */
	private static final String RENAME_ALL_STR = "Rename All States";
	
	/** Model containing all the important information to display in the GUI. */
	private Model model;
	/** Label for the section (changes depending on the type of FSM). */
	private Label initialOptionsLabel;
	/** TextField to name an initial state as the user specifies. */
	private TextField stateNameField;
	/** Button to add/remove an initial state to the FSM open in the current tab. */
	private Button toggleInitialStateBtn;
	/** Button to add/remove a marked state to the FSM open in the current tab. */
	private Button toggleMarkedBtn;
	/** Button to add/remove a secret state to the FSM open in the current tab. */
	private Button toggleSecretBtn;
	/** Button to add/remove a bad state to the FSM open in the current tab. */
	private Button toggleBadBtn;
	
	/** Button to mark all states. */
	private Button markAllBtn;
	/** Button to unmark all states. */
	private Button unmarkAllBtn;
	/** Button to make all bad states good again. */
	private Button makeAllGoodBtn;
	/** Button to remove all the bad states from the FSM. */
	private Button removeAllBadBtn;
	/** Button to rename states from the FSM open in the current tab. */
	private Button renameStatesBtn;
	
	/**
	 * Creates a box with all the initial state options in it.
	 * Allows the user to add and remove initial states.
	 * 
	 * @param inModel - Model containing all the important information
	 * to display in the GUI.
	 */
	public ModifyTSStatePropertiesPane(Model inModel) {
		model = inModel;
		this.getStyleClass().add("modify-fsm-subpane");
		
		initialOptionsLabel = new Label(LABEL_STR);
		initialOptionsLabel.getStyleClass().add("subpane-section-title");
		
		Label stateNameLabel = new Label("State Name:");
		stateNameField = new TextField();
		
		toggleInitialStateBtn = new Button(TOGGLE_INITIAL_STR);
		toggleMarkedBtn = new Button(TOGGLE_MARKED_STR);
		toggleSecretBtn = new Button(TOGGLE_SECRET_STR);
		toggleBadBtn = new Button(TOGGLE_BAD_STR);
		
		GridPane toggleGrid = new GridPane();
		toggleGrid.addRow(0, stateNameLabel, stateNameField);
		toggleGrid.addRow(1, toggleInitialStateBtn, toggleMarkedBtn);
		toggleGrid.addRow(2, toggleSecretBtn, toggleBadBtn);
		
		markAllBtn = new Button(MARK_ALL_STR);
		unmarkAllBtn = new Button(UNMARK_ALL_STR);
		makeAllGoodBtn = new Button(MAKE_ALL_GOOD_STR);
		removeAllBadBtn = new Button(REMOVE_ALL_BAD_STR);
		renameStatesBtn = new Button(RENAME_ALL_STR);
		
		GridPane allStateModifierGrid = new GridPane();
		allStateModifierGrid.addRow(0, markAllBtn, unmarkAllBtn);
		allStateModifierGrid.addRow(1, makeAllGoodBtn, removeAllBadBtn);
		allStateModifierGrid.addRow(2, renameStatesBtn);
		
		getChildren().addAll(initialOptionsLabel, toggleGrid, new Separator(), allStateModifierGrid);
		
		makeToggleInitialStateEventHandler();
		makeToggleMarkedEventHandler();
		makeToggleSecretEventHandler();
		makeToggleBadEventHandler();
		
		makeMarkAllEventHandler();
		makeUnmarkAllEventHandler();
		makeMakeAllGoodEventHandler();
		makeRemoveAllBadEventHandler();
		makeRenameStatesEventHandler();
	} // ModifyFSMInitialStatesPane(Model)
	
	/**
	 * Creates the event handler for changing initial states.
	 */
	private void makeToggleInitialStateEventHandler() {
		stateNameField.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER) toggleInitialStateBtn.fire();
		});
		toggleInitialStateBtn.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER) toggleInitialStateBtn.fire();
		});
		toggleInitialStateBtn.setOnAction(e -> {
			String state = stateNameField.getText();
			if(state.equals("")) {
				Alerts.makeError(Alerts.ERROR_TOGGLE_STATE_NO_NAME);
			} else {
				// Then no errors
				TransitionSystem<? extends Transition> currTS = model.getCurrTS();
				if(currTS == null) {
					// Then cannot add an event
					Alerts.makeError(Alerts.ERROR_TOGGLE_STATE_NO_FSM);
				} else {
					// If nondeterministic, then add/remove the initial state;
					// otherwise, replace the pre-existing initial state.
					if(currTS instanceof NonDeterministic) {
						if(currTS.hasInitialState(state)) {
							currTS.removeInitialState(state);
						} else {
							currTS.addInitialState(state);
						} // if/else
					} else {
						currTS.addInitialState(state);
					} // if/else
					stateNameField.setText("");
					stateNameField.requestFocus();
					model.refreshViewport();
				} // if/else
			} // if/else
		}); // setOnAction(EventHandler<ActionEvent>)
	} // makeToggleInitialStateEventHandler()
	
	/**
	 * Creates the event handler for changing marked states.
	 */
	private void makeToggleMarkedEventHandler() {
		toggleMarkedBtn.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER) toggleMarkedBtn.fire();
		});
		toggleMarkedBtn.setOnAction(e -> {
			String state = stateNameField.getText();
			if(state.equals("")) {
				Alerts.makeError(Alerts.ERROR_TOGGLE_STATE_NO_NAME);
			} else {
				// Then no errors
				TransitionSystem<? extends Transition> currTS = model.getCurrTS();
				if(currTS == null) {
					// Then cannot toggle
					Alerts.makeError(Alerts.ERROR_TOGGLE_STATE_NO_FSM);
				} else {
					// Toggle the marked property
					currTS.toggleMarkedState(state);
					stateNameField.setText("");
					stateNameField.requestFocus();
					model.refreshViewport();
				} // if/else
			} // if/else
		}); // setOnAction(EventHandler<ActionEvent>)
	} // makeToggleMarkedEventHandler()
	
	/**
	 * Creates the event handler for changing bad states.
	 */
	private void makeToggleSecretEventHandler() {
		toggleSecretBtn.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER) toggleSecretBtn.fire();
		});
		toggleSecretBtn.setOnAction(e -> {
			String state = stateNameField.getText();
			if(state.equals("")) {
				Alerts.makeError(Alerts.ERROR_TOGGLE_STATE_NO_NAME);
			} else {
				// Then no errors
				TransitionSystem<? extends Transition> currTS = model.getCurrTS();
				if(currTS == null) {
					// Then cannot toggle
					Alerts.makeError(Alerts.ERROR_TOGGLE_STATE_NO_FSM);
				} else {
					// Toggle the secret property
					currTS.toggleSecretState(state);
					stateNameField.setText("");
					stateNameField.requestFocus();
					model.refreshViewport();
				} // if/else
			} // if/else
		}); // setOnAction(EventHandler<ActionEvent>)
	} // makeToggleSecretEventHandler()
	
	/**
	 * Creates the event handler for changing bad states.
	 */
	private void makeToggleBadEventHandler() {
		toggleBadBtn.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER) toggleBadBtn.fire();
		});
		toggleBadBtn.setOnAction(e -> {
			String state = stateNameField.getText();
			if(state.equals("")) {
				Alerts.makeError(Alerts.ERROR_TOGGLE_STATE_NO_NAME);
			} else {
				// Then no errors
				TransitionSystem<? extends Transition> currTS = model.getCurrTS();
				if(currTS == null) {
					// Then cannot toggle
					Alerts.makeError(Alerts.ERROR_TOGGLE_STATE_NO_FSM);
				} else {
					// Toggle the secret property
					currTS.toggleBadState(state);
					stateNameField.setText("");
					stateNameField.requestFocus();
					model.refreshViewport();
				} // if/else
			} // if/else
		}); // setOnAction(EventHandler<ActionEvent>)
	} // makeToggleBadEventHandler()
	
	/**
	 * Creates the event handler for marking all states.
	 */
	private void makeMarkAllEventHandler() {
		markAllBtn.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER) markAllBtn.fire();
		});
		markAllBtn.setOnAction(e -> {
			// Then no errors
			TransitionSystem<? extends Transition> currTS = model.getCurrTS();
			if(currTS == null) {
				// Then cannot toggle
				Alerts.makeError(Alerts.ERROR_NO_FSM);
			} else {
				currTS.markAllStates();
				stateNameField.requestFocus();
				model.refreshViewport();
			} // if/else
		}); // setOnAction
	} // makeMarkAllEventHandler()
	
	/**
	 * Creates the event handler for marking all states.
	 */
	private void makeUnmarkAllEventHandler() {
		unmarkAllBtn.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER) unmarkAllBtn.fire();
		});
		unmarkAllBtn.setOnAction(e -> {
			// Then no errors
			TransitionSystem<? extends Transition> currTS = model.getCurrTS();
			if(currTS == null) {
				// Then cannot toggle
				Alerts.makeError(Alerts.ERROR_NO_FSM);
			} else {
				currTS.unmarkAllStates();
				stateNameField.requestFocus();
				model.refreshViewport();
			} // if/else
		}); // setOnAction
	} // makeUnmarkAllEventHandler()
	
	/**
	 * Creates the event handler for marking all states.
	 */
	private void makeMakeAllGoodEventHandler() {
		makeAllGoodBtn.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER) makeAllGoodBtn.fire();
		});
		makeAllGoodBtn.setOnAction(e -> {
			// Then no errors
			TransitionSystem<? extends Transition> currTS = model.getCurrTS();
			if(currTS == null) {
				// Then cannot toggle
				Alerts.makeError(Alerts.ERROR_NO_FSM);
			} else {
				currTS.makeAllStatesGood();
				stateNameField.requestFocus();
				model.refreshViewport();
			} // if/else
		}); // setOnAction
	} // makeMakeAllGoodEventHandler()
	
	/**
	 * Creates the event handler for marking all states.
	 */
	private void makeRemoveAllBadEventHandler() {
		removeAllBadBtn.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER) removeAllBadBtn.fire();
		});
		removeAllBadBtn.setOnAction(e -> {
			// Then no errors
			TransitionSystem<? extends Transition> currTS = model.getCurrTS();
			if(currTS == null) {
				// Then cannot toggle
				Alerts.makeError(Alerts.ERROR_NO_FSM);
			} else {
				currTS.removeBadStates();
				stateNameField.requestFocus();
				model.refreshViewport();
			} // if/else
		}); // setOnAction
	} // makeRemoveAllBadEventHandler()
	
	/**
	 * Creates the event handler for when the rename states button
	 * is pressed.
	 */
	private void makeRenameStatesEventHandler() {
		renameStatesBtn.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER) renameStatesBtn.fire();
		});
		renameStatesBtn.setOnAction(e -> {
			TransitionSystem<? extends Transition> currTS = model.getCurrTS();
			if(currTS == null) {
				// Then cannot remove event
				Alerts.makeError(Alerts.ERROR_NO_FSM);
			} else {
				currTS.renameStates();
				stateNameField.requestFocus();
				model.refreshViewport();
			} // if/else
		});
	} // makeRenameStatesEventHandler()
} // class ModifyFSMInitialStatesPane
