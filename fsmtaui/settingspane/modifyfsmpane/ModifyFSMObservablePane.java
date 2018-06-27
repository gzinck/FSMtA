package fsmtaui.settingspane.modifyfsmpane;

import fsm.*;
import fsm.attribute.*;
import fsmtaui.FSMViewport;
import fsmtaui.Model;
import fsmtaui.popups.Alerts;
import javafx.beans.value.ObservableValue;
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
public class ModifyFSMObservablePane extends VBox {
	/** Text shown when the observabilityBox is disabled. */
	private static final String OBSERVABILITY_DISABLED_MSG = "The current FSM does not allow observable properties. Please create an Observable FSM to enable this option.";
	/** Label shown when the observable event options are disabled for a non-observable FSM. */
	private static final Label OBSERVABILITY_DISABLED = new Label(OBSERVABILITY_DISABLED_MSG);
	
	/** Model containing all the important information to display in the GUI. */
	private Model model;
	
	/** All elements in the pane are in this VBox, and the VBox goes invisible if the
	 * current FSM is not of type Observable.
	 */
	private VBox observabilityBox;
	/** TextField to name an event to change observability. */
	private TextField eventObservabilityField;
	/** Button to toggle the observability of an event. */
	private Button toggleObservabilityBtn;
	
	/**
	 * Creates a pane that controls the observability of events in
	 * the current FSM, if the current FSM extends Observable.
	 * 
	 * @param inModel - Model with the important information all GUI elements
	 * need access to.
	 */
	public ModifyFSMObservablePane(Model inModel) {
		model = inModel;
		
		Label eventObservabilityLabel = new Label("Toggle Event Observability");
		Label eventNameLabel = new Label("Event Name:");
		eventObservabilityField = new TextField();
		HBox eventName = new HBox(eventNameLabel, eventObservabilityField);
		toggleObservabilityBtn = new Button("Toggle Observability");
		
		observabilityBox = new VBox(eventObservabilityLabel, eventName, toggleObservabilityBtn);
		getChildren().add(observabilityBox);
		
		makeToggleObservableEventHandler();
		refreshOptions();
	} // ModifyFSMObservablePane()
	
	/**
	 * Creates the event handler for changing initial states.
	 */
	private void makeToggleObservableEventHandler() {
		eventObservabilityField.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER) toggleObservabilityBtn.fire();
		});
		toggleObservabilityBtn.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER) toggleObservabilityBtn.fire();
		});
		toggleObservabilityBtn.setOnAction(e -> {
			String event = eventObservabilityField.getText();
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
						if(currFSM.getEventObservability(event)) {
							currFSM.setEventObservability(event, false);
						} else {
							currFSM.setEventObservability(event, true);
						} // if/else
						eventObservabilityField.setText("");
						eventObservabilityField.requestFocus();
						model.refreshViewport();
					} // if/else
				} catch(NullPointerException err) {
					Alerts.makeError(Alerts.ERROR_EDIT_EVENT_NO_NAME);
				}
			} // if/else
		}); // setOnAction(EventHandler<ActionEvent>)
	} // makeToggleObservableEventHandler()
	
	/**
	 * Handles whenever the current FSM changes. When the FSM changes, if
	 * the new FSM is observable, then the options reflect that.
	 */
	private void refreshOptions() {
		model.getOpenFSMTabs().getSelectionModel().selectedItemProperty().addListener(
			(ObservableValue<? extends Tab> value, Tab oldTab, Tab newTab) -> {
				if(newTab != null) {
					FSM fsm = ((FSMViewport)newTab.getContent()).getFSM();
					// Only show observable options if observable
					if(fsm instanceof Observability) {
						getChildren().setAll(observabilityBox);
					} else {
						getChildren().setAll(OBSERVABILITY_DISABLED);
					} // if/else
				} // if
			});
	} // refreshOptions()
} // class ModifyFSMObservablePane
