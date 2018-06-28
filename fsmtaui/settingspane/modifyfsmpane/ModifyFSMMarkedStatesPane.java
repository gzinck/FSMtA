package fsmtaui.settingspane.modifyfsmpane;

import fsm.*;
import fsmtaui.Model;
import fsmtaui.popups.Alerts;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.input.KeyCode;

/**
 * Class extending the javafx VBox element which stores all the
 * options for adding and removing marked states from the open FSM.
 * 
 * @author Mac Clevinger and Graeme Zinck
 *
 */
public class ModifyFSMMarkedStatesPane extends VBox {
	/** Model containing all the important information to display in the GUI. */
	private Model model;
	
	/** TextField to name a marked state as the user specifies. */
	private TextField markedStateNameField;
	/** Button to add/remove a marked state to the FSM open in the current tab. */
	private Button toggleMarkedBtn;
	
	/**
	 * Creates a pane for marking states.
	 * 
	 * @param inModel - Model with the important information all GUI elements
	 * need access to.
	 */
	public ModifyFSMMarkedStatesPane(Model inModel) {
		model = inModel;
		this.getStyleClass().add("modify-fsm-subpane");
		
		Label markedOptionsLabel = new Label("Add/Remove Marked State");
		markedOptionsLabel.getStyleClass().add("subpane-section-title");
		Label stateNameLabel = new Label("State Name:");
		markedStateNameField = new TextField();
		HBox stateName= new HBox(stateNameLabel, markedStateNameField);
		toggleMarkedBtn = new Button("Toggle Marked");
		
		getChildren().addAll(markedOptionsLabel, stateName, toggleMarkedBtn);
		
		makeToggleMarkedEventHandler();
	} // ModifyFSMMarkedStates()
	
	/**
	 * Creates the event handler for changing marked states.
	 */
	private void makeToggleMarkedEventHandler() {
		markedStateNameField.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER) toggleMarkedBtn.fire();
		});
		toggleMarkedBtn.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER) toggleMarkedBtn.fire();
		});
		toggleMarkedBtn.setOnAction(e -> {
			String state = markedStateNameField.getText();
			if(state.equals("")) {
				Alerts.makeError(Alerts.ERROR_TOGGLE_MARKED_NO_NAME);
			} else {
				// Then no errors
				FSM currFSM = model.getCurrFSM();
				if(currFSM == null) {
					// Then cannot toggle
					Alerts.makeError(Alerts.ERROR_TOGGLE_MARKED_NO_FSM);
				} else {
					// Toggle the marked property
					currFSM.toggleMarkedState(state);
					markedStateNameField.setText("");
					markedStateNameField.requestFocus();
					model.refreshViewport();
				} // if/else
			} // if/else
		}); // setOnAction(EventHandler<ActionEvent>)
	} // makeToggleMarkedEventHandler()
} // class ModifyFSMMarkedStates
