package fsmtaui.settingspane.operationspane;

import fsm.attribute.*;
import fsmtaui.Model;
import fsmtaui.popups.Alerts;
import fsm.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;

/**
 * Javafx VBox element which has all the options to perform
 * operations on a single FSM (the FSM that is currently in view)
 * 
 * @author Mac Clevinger and Graeme Zinck
 *
 */
public class SingleFSMOperationPane extends VBox {
	/** String message for what the section does. */
	private static final String TITLE_MSG = "Perform an Operation on the Current FSM";
	
	/** Model containing all the important information to display in the GUI. */
	private Model model;
	/** ChoiceBox of Strings allowing the user to choose which operation to perform. */
	private ChoiceBox<String> operationChoiceBox;
	/** Field for the name of the new FSM to create. */
	private TextField fsmNameField;
	/** ObservableList of Strings with all the possible operations involving
	 * one FSM that a user can choose. */
	private static final ObservableList<String> SINGLE_FSM_OPERATIONS = FXCollections.observableArrayList("Determinize", "Make Accessible", "Make CoAccessible", "Trim", "Make Observer View");
	/** Button allowing the user to perform the selection operation on the selected FSM. */
	private Button performOperationBtn;
	
	/**
	 * Creates a pane with all the single FSM operations.
	 */
	public SingleFSMOperationPane(Model inModel) {
		model = inModel;
		this.getStyleClass().add("operations-subpane");
		
		Label sectionTitle = new Label(TITLE_MSG);
		sectionTitle.getStyleClass().add("subpane-section-title");
		HBox operationSelector = makeOperationSelector();
		HBox nameField = makeFSMNameField();
		performOperationBtn = new Button("Perform Operation");
		
		getChildren().addAll(sectionTitle, operationSelector, nameField, performOperationBtn);
		
		makePerformOperationEventHandler();
	} // SingleFSMOperationPane()
	
	/**
	 * Makes a ChoiceBox for the user to select what operation s/he
	 * wishes to perform.
	 * 
	 * @return - HBox with the ChoiceBox and its Label.
	 */
	private HBox makeOperationSelector() {
		Label operationLabel = new Label("Pick an operation:");
		operationChoiceBox = new ChoiceBox<String>(SINGLE_FSM_OPERATIONS);
		return new HBox(operationLabel, operationChoiceBox);
	} // makeOperationSelector()
	
	/**
	 * Makes a name TextField for the new FSM that is created.
	 * 
	 * @return - HBox containing the name TextField and its Label.
	 */
	private HBox makeFSMNameField() {
		Label fsmNameLabel = new Label("New FSM Name:");
		fsmNameField = new TextField();
		return new HBox(fsmNameLabel, fsmNameField);
	} // makeFSMNameField()
	
	/**
	 * Makes the event handler for when the "Perform Operation"
	 * button is pressed to perform the operation on the current
	 * FSM.
	 */
	private void makePerformOperationEventHandler() {
		fsmNameField.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER) performOperationBtn.fire();
		});
		performOperationBtn.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER) performOperationBtn.fire();
		});
		performOperationBtn.setOnAction(e -> {
			String id = fsmNameField.getText();
			FSM currFSM = model.getCurrFSM();
			String operation = operationChoiceBox.getSelectionModel().getSelectedItem();
			if(id.equals("") || model.fsmExists(id)) {
				// Then must force the user to name the FSM
				Alerts.makeError(Alerts.ERROR_OPERATION_NO_NAME);
			} else if(currFSM == null) {
				// Then cannot perform operation
				Alerts.makeError(Alerts.ERROR_OPERATION_NO_FSM);
			} else {
				if(operation.equals(SINGLE_FSM_OPERATIONS.get(0))) {
					// Determinize
					addFSM(currFSM.determinize(), id);
				} else if(operation.equals(SINGLE_FSM_OPERATIONS.get(1))) {
					// Accessible
					addFSM(currFSM.makeAccessible(), id);
				} else if(operation.equals(SINGLE_FSM_OPERATIONS.get(2))) {
					// CoAccessible
					addFSM(currFSM.makeCoAccessible(), id);
				} else if(operation.equals(SINGLE_FSM_OPERATIONS.get(3))) {
					// Trim
					addFSM(currFSM.trim(), id);
				} else if(operation.equals(SINGLE_FSM_OPERATIONS.get(4))) {
					// Make Observer View
					if(currFSM instanceof Observability) {
						addFSM((FSM)((Observability)currFSM).createObserverView(), id);
					} else {
						Alerts.makeError(Alerts.ERROR_ALREADY_OBSERVABLE);
					} // if/else
				} else {
					// No option chosen
					Alerts.makeError(Alerts.ERROR_OPERATION_NO_OP);
				}
			} // if/else
		});
	} // makePerformOperationEventHandler()
	
	/**
	 * Helper method o add an FSM to the model and reset the text field.
	 * 
	 * @param newFSM - DeterministicFSM to add to the model.
	 * @param id - String representing the id of the FSM.
	 */
	private void addFSM(FSM newFSM, String id) {
		newFSM.setId(id);
		model.addFSM(newFSM);
		fsmNameField.setText("");
		fsmNameField.requestFocus();
	} // addFSM(DeterministicFSM, String)
} // class SingleFSMOperationPane
