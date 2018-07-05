package fsmtaui.settingspane.modifyfsmpane;

import fsm.attribute.*;
import fsmtaui.Model;
import fsmtaui.popups.Alerts;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.input.KeyCode;

/**
 * Class extending the javafx VBox element which stores all the
 * options for managing observability of states.
 * 
 * @author Mac Clevinger and Graeme Zinck
 *
 */
public class ModifyFSMEventPropertiesPane extends VBox {
	
	/** Model containing all the important information to display in the GUI. */
	private Model model;
	
	/** TextField to name an event to change observability. */
	private TextField eventNameField;
	/** Button to toggle the observability of an event. */
	private Button toggleObservabilityBtn;
	/** Button to toggle the controllability of an event. */
	private Button toggleControllabilityBtn;
	
	/**
	 * Creates a pane that controls the observability of events in
	 * the current FSM, if the current FSM extends Observable.
	 * 
	 * @param inModel - Model with the important information all GUI elements
	 * need access to.
	 */
	public ModifyFSMEventPropertiesPane(Model inModel) {
		model = inModel;
		this.getStyleClass().add("modify-fsm-subpane");
		
		Label titleLabel = new Label("Modify Event Properties");
		titleLabel.getStyleClass().add("subpane-section-title");
		
		Label eventNameLabel = new Label("Event Name:");
		eventNameField = new TextField();
		toggleObservabilityBtn = new Button("Toggle Observability");
		toggleControllabilityBtn = new Button("Toggle Controllability");
		
		GridPane grid = new GridPane();
		grid.addRow(0, eventNameLabel, eventNameField);
		grid.addRow(1, toggleObservabilityBtn, toggleControllabilityBtn);
		getChildren().addAll(titleLabel, grid);
		
		makeToggleObservableEventHandler();
		makeToggleControllableEventHandler();
	} // ModifyFSMObservablePane()
	
	/**
	 * Creates the event handler for changing observability of an event.
	 */
	private void makeToggleObservableEventHandler() {
		eventNameField.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER) toggleObservabilityBtn.fire();
		});
		toggleObservabilityBtn.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER) toggleObservabilityBtn.fire();
		});
		toggleObservabilityBtn.setOnAction(e -> {
			String event = eventNameField.getText();
			if(event.equals("")) {
				Alerts.makeError(Alerts.ERROR_EDIT_EVENT_NO_NAME);
			} else {
				// Then no errors
				try {
					Observability currFSM = (Observability) model.getCurrFSM();
					if(currFSM == null) {
						// Then cannot add an event
						Alerts.makeError(Alerts.ERROR_ADD_STATE_NO_FSM);
					} else {
						// Toggle the observability of the event.
						currFSM.setEventObservability(event, !currFSM.getEventObservability(event));
						eventNameField.setText("");
						eventNameField.requestFocus();
						model.refreshViewport();
					} // if/else
				} catch(NullPointerException err) {
					Alerts.makeError(Alerts.ERROR_EDIT_EVENT_NO_NAME);
				}
			} // if/else
		}); // setOnAction(EventHandler<ActionEvent>)
	} // makeToggleObservableEventHandler()
	
	/**
	 * Creates the event handler for changing observability of an event.
	 */
	private void makeToggleControllableEventHandler() {
		toggleControllabilityBtn.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER) toggleControllabilityBtn.fire();
		});
		toggleControllabilityBtn.setOnAction(e -> {
			String event = eventNameField.getText();
			if(event.equals("")) {
				Alerts.makeError(Alerts.ERROR_EDIT_EVENT_NO_NAME);
			} else {
				// Then no errors
				try {
					Controllability currFSM = (Controllability) model.getCurrFSM();
					if(currFSM == null) {
						// Then cannot add an event
						Alerts.makeError(Alerts.ERROR_ADD_STATE_NO_FSM);
					} else {
						// Toggle the observability of the event.
						currFSM.setEventControllability(event, !currFSM.getEventControllability(event));
						eventNameField.setText("");
						eventNameField.requestFocus();
						model.refreshViewport();
					} // if/else
				} catch(NullPointerException err) {
					Alerts.makeError(Alerts.ERROR_EDIT_EVENT_NO_NAME);
				}
			} // if/else
		}); // setOnAction(EventHandler<ActionEvent>)
	} // makeToggleControllableEventHandler()
} // class ModifyFSMObservablePane
