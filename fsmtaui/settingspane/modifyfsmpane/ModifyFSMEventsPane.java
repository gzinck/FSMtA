package fsmtaui.settingspane.modifyfsmpane;

import fsm.FSM;
import fsmtaui.Model;
import fsmtaui.popups.Alerts;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * Class extending the javafx VBox element which stores all the
 * options for adding and removing events from the open FSM.
 * 
 * @author Mac Clevinger and Graeme Zinck
 *
 */
public class ModifyFSMEventsPane extends VBox {
	/** Model containing all the important information to display in the GUI. */
	private Model model;
	
	/** TextField for the initial state for a new event the user wishes to create. */
	private TextField stateFromField;
	/** TextField for the resulting state for a new event the user wishes to create. */
	private TextField stateToField;
	/** TextField for the name of the event/transition to get from the initial to the resulting state. */
	private TextField eventNameField;
	/** Button to add the event. */
	private Button addEventBtn;
	/** Button to remove the event. */
	private Button removeEventBtn;
	
	/**
	 * Creates a new pane to add/remove events.
	 * 
	 * @param inModel - Model with the important information all GUI elements
	 * need access to.
	 */
	public ModifyFSMEventsPane(Model inModel) {
		model = inModel; 
		
		Label addEventLabel = new Label("Add/Remove Event");
		GridPane addRemoveEventFields = new GridPane();
		
		// Buttons to define what event to consider
		Label fromLabel = new Label("From state:");
		stateFromField = new TextField();
		addRemoveEventFields.addColumn(0, fromLabel, stateFromField);
		
		Label toLabel = new Label("To state:");
		stateToField = new TextField();
		addRemoveEventFields.addColumn(1, toLabel, stateToField);
		
		Label nameLabel = new Label("Name:");
		eventNameField = new TextField();
		addRemoveEventFields.addColumn(2, nameLabel, eventNameField);
		eventNameField.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER) {
				addEventBtn.fire();
			} // if
		});
		
		// Buttons to actually add/remove the event
		addEventBtn = new Button("Add Event");
		removeEventBtn = new Button("Remove Event");
		GridPane addRemoveEventBtns = new GridPane();
		addRemoveEventBtns.addColumn(0, addEventBtn);
		addRemoveEventBtns.addColumn(1, removeEventBtn);
		
		getChildren().addAll(addEventLabel, addRemoveEventFields, addRemoveEventBtns);
		
		makeAddEventHandler();
		makeRemoveEventHandler();
	} // ModifyFSMEventsPane(Model)
	
	/**
	 * Creates an event handler to add a new event connecting two states.
	 */
	private void makeAddEventHandler() {
		addEventBtn.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER) addEventBtn.fire();
		});
		addEventBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				if(stateFromField.getText().equals("") || stateToField.getText().equals("")) {
					// Then must force the user to define states
					Alerts.makeError(Alerts.ERROR_ADD_EVENT_NO_STATES);
				} else {
					// Then check if the user defined an event
					if(eventNameField.getText().equals("")) {
						// Then must force the user to define name
						Alerts.makeError(Alerts.ERROR_ADD_EVENT_NO_NAME);
					} else {
						// Then check if an FSM is open in a viewport
						FSM currFSM = model.getCurrFSM();
						if(currFSM == null) {
							// Then cannot add an event
							Alerts.makeError(Alerts.ERROR_ADD_EVENT_NO_FSM);
						} else {
							// Then create the event
							currFSM.addTransition(stateFromField.getText(), eventNameField.getText(), stateToField.getText());
							stateFromField.setText("");
							stateFromField.requestFocus();
							stateToField.setText("");
							eventNameField.setText("");
							model.refreshViewport();
						} // if/else
					} // if/else
				} // if/else
			} // handle(ActionEvent)
		}); // setOnAction(EventHandler<ActionEvent>)
	} // makeAddEventHandler()
	
	private void makeRemoveEventHandler() {
		removeEventBtn.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER) removeEventBtn.fire();
		});
		removeEventBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				if(stateFromField.getText().equals("") || stateToField.getText().equals("")) {
					// Then must force the user to define states
					Alerts.makeError(Alerts.ERROR_REMOVE_EVENT_NO_STATES);
				} else {
					// Then check if the user defined an event
					if(eventNameField.getText().equals("")) {
						// Then must force the user to define name
						Alerts.makeError(Alerts.ERROR_REMOVE_EVENT_NO_NAME);
					} else {
						// Then check if an FSM is open in a viewport
						FSM currFSM = model.getCurrFSM();
						if(currFSM == null) {
							// Then cannot remove an event
							Alerts.makeError(Alerts.ERROR_REMOVE_EVENT_NO_FSM);
						} else {
							// Then remove the event
							currFSM.removeTransition(stateFromField.getText(), eventNameField.getText(), stateToField.getText());
							stateFromField.setText("");
							stateFromField.requestFocus();
							stateToField.setText("");
							eventNameField.setText("");
							model.refreshViewport();
						} // if/else
					} // if/else
				} // if/else
			} // handle(ActionEvent)
		}); // setOnAction(EventHandler<ActionEvent>)
	} // makeRemoveEventHandler()
}
