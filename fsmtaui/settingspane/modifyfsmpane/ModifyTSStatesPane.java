package fsmtaui.settingspane.modifyfsmpane;

import fsm.TransitionSystem;
import fsmtaui.Model;
import fsmtaui.popups.Alerts;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import support.transition.Transition;

/**
 * Class extending the javafx VBox element which stores all the
 * options for adding and removing states from the open FSM.
 * 
 * @author Mac Clevinger and Graeme Zinck
 *
 */
public class ModifyTSStatesPane extends VBox {
	/** Model containing all the important information to display in the GUI. */
	private Model model;
	
	/** TextField to name a new state as the user specifies. */
	private TextField stateNameField;
	/** Button to add a new state to the FSM open in the current tab. */
	private Button addStateBtn;
	/** Button to remove a state from the FSM open in the current tab. */
	private Button removeStateBtn;
	
	/**
	 * Creates a new pane to add/remove states.
	 * 
	 * @param inModel - Model with the important information all GUI elements
	 * need access to.
	 */
	public ModifyTSStatesPane(Model inModel) {
		model = inModel;
		this.getStyleClass().add("modify-fsm-subpane");
		
		Label stateOptionsLabel = new Label("Add/Remove State");
		stateOptionsLabel.getStyleClass().add("subpane-section-title");
		
		Label stateNameLabel = new Label("State Name:");
		stateNameField = new TextField();
		HBox stateName= new HBox(stateNameLabel, stateNameField);
		
		addStateBtn = new Button("Add State");
		removeStateBtn = new Button("Remove State");
		HBox stateBtnPane = new HBox(addStateBtn, removeStateBtn);
		
		getChildren().addAll(stateOptionsLabel, stateName, stateBtnPane);
		
		makeAddStateEventHandler();
		makeRemoveStateEventHandler();
	} // ModifyFSMStatesPane(Model)
	
	/**
	 * Creates the event handler for creating states.
	 */
	private void makeAddStateEventHandler() {
		stateNameField.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER) addStateBtn.fire();
		});
		addStateBtn.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER) addStateBtn.fire();
		});
		addStateBtn.setOnAction(e -> {
			String stateToAdd = stateNameField.getText();
			if(stateToAdd.equals("")) {
				Alerts.makeError(Alerts.ERROR_ADD_STATE_NO_NAME);
			} else {
				// Then no errors
				TransitionSystem<? extends Transition> currTS = model.getCurrTS();
				if(currTS == null) {
					// Then cannot add an event
					Alerts.makeError(Alerts.ERROR_ADD_STATE_NO_FSM);
				} else {
					currTS.addState(stateToAdd);
					stateNameField.setText("");
					stateNameField.requestFocus();
					model.refreshViewport();
				} // if/else
			} // if/else
		}); // setOnAction(EventHandler<ActionEvent>)
	} // makeAddStateEventHandler()
	
	/**
	 * Creates the event handler for removing states.
	 */
	private void makeRemoveStateEventHandler() {
		removeStateBtn.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER) removeStateBtn.fire();
		});
		removeStateBtn.setOnAction(e -> {
			String stateToRemove = stateNameField.getText();
			if(stateToRemove.equals("")) {
				Alerts.makeError(Alerts.ERROR_REMOVE_STATE_NO_NAME);
			} else {
				// Then no errors
				TransitionSystem<? extends Transition> currTS = model.getCurrTS();
				if(currTS == null) {
					// Then cannot remove event
					Alerts.makeError(Alerts.ERROR_REMOVE_STATE_NO_FSM);
				} else {
					currTS.removeState(stateToRemove);
					stateNameField.setText("");
					stateNameField.requestFocus();
					model.refreshViewport();
				} // if/else
			} // if/else
		}); // setOnAction(EventHandler<ActionEvent>)
	} // makeRemoveStateEventHandler()
} // class ModifyFSMStatesPane
