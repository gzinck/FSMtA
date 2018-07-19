package fsmtaui.settingspane.modifyfsmpane;

import fsm.TransitionSystem;
import fsm.attribute.*;
import support.transition.*;
import fsmtaui.Model;
import fsmtaui.popups.Alerts;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.input.KeyCode;

/**
 * Class extending the javafx VBox element which stores all the options for managing observability of states.
 * 
 * This class is a part of the fsmtaui.settingspane.modifyfsmpane package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public class ModifyTSEventPropertiesPane extends VBox {
	
//---  Instance Variables   -------------------------------------------------------------------
	
	/** Model containing all the important information to display in the GUI. */
	private Model model;
	/** TextField to name an event to change observability. */
	private TextField eventNameField;
	/** Button to toggle the observability of an event. */
	private Button toggleObservabilityBtn;
	/** Button to toggle the controllability of an event. */
	private Button toggleControllabilityBtn;
	
//---  Constructors   -------------------------------------------------------------------------
	
	/**
	 * Creates a pane that controls the observability of events in
	 * the current FSM, if the current FSM extends Observable.
	 * 
	 * @param inModel - Model with the important information all GUI elements
	 * need access to.
	 */
	
	public ModifyTSEventPropertiesPane(Model inModel) {
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

//---  Operations   ---------------------------------------------------------------------------
	
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
				TransitionSystem<? extends Transition> currTS = model.getCurrTS();
				if(currTS == null) {
					Alerts.makeError(Alerts.ERROR_ADD_STATE_NO_FSM);
				} else if(currTS instanceof Observability<?>) {
					try {
						Observability<?> currObs = (Observability<?>) model.getCurrTS();
						// Toggle the observability of the event.
						currObs.setEventObservability(event, !currObs.getEventObservability(event));
						eventNameField.setText("");
						eventNameField.requestFocus();
						model.refreshViewport();
					} catch(NullPointerException err) {
						Alerts.makeError(Alerts.ERROR_EDIT_EVENT_NO_NAME);
					}
				} else {
					System.err.println("Error: the options for observable events were available for a TransitionSystem that did not have them enabled.");
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
				TransitionSystem<? extends Transition> currTS = model.getCurrTS();
				if(currTS == null) {
					Alerts.makeError(Alerts.ERROR_ADD_STATE_NO_FSM);
				} else if(currTS instanceof Controllability<?>) {
					try {
						Controllability<?> currControl = (Controllability<?>) model.getCurrTS();
						// Toggle the observability of the event.
						currControl.setEventControllability(event, !currControl.getEventControllability(event));
						eventNameField.setText("");
						eventNameField.requestFocus();
						model.refreshViewport();
					} catch(NullPointerException err) {
						Alerts.makeError(Alerts.ERROR_EDIT_EVENT_NO_NAME);
					}
				} else {
					System.err.println("Error: the options for controllable events were available for a TransitionSystem that did not have them enabled.");
				}
			} // if/else
		}); // setOnAction(EventHandler<ActionEvent>)
	} // makeToggleControllableEventHandler()

} // class ModifyFSMObservablePane
