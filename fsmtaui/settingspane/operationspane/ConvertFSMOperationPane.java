package fsmtaui.settingspane.operationspane;

import fsm.*;
import fsmtaui.Model;
import fsmtaui.popups.Alerts;
import fsmtaui.settingspane.FileSettingsPane;
import fsmtaui.settingspane.FileSettingsPane.FSM_TYPE;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;

public class ConvertFSMOperationPane extends VBox {
	/** String message for what the section does. */
	private static final String TITLE_MSG = "Convert the Current FSM";
	
	/** Model containing all the important information to display in the GUI. */
	private Model model;
	/** Box with the possible types of FSMs to convert to. */
	private ChoiceBox<String> fsmTypeChoiceBox;
	/** Field for the name of the new FSM to create. */
	private TextField fsmNameField;
	/** Button allowing the user to perform the conversion on the selected FSM. */
	private Button convertBtn;
	
	/**
	 * Creates a DeterminizationPane with all the options
	 * to determinize an FSM.
	 */
	public ConvertFSMOperationPane(Model inModel) {
		model = inModel;
		this.getStyleClass().add("operations-subpane");
		
		Label sectionTitle = new Label(TITLE_MSG);
		sectionTitle.getStyleClass().add("subpane-section-title");
		HBox operationSelector = makeConversionSelector();
		HBox nameField = makeFSMNameField();
		convertBtn = new Button("Perform Conversion");
		
		getChildren().addAll(sectionTitle, operationSelector, nameField, convertBtn);
		
		makeConvertEventHandler();
	} // ConvertFSMOperationPane()
	
	/**
	 * Makes a VBox with options for the type of FSM the user wishes to convert to.
	 * 
	 * @return - VBox with a ChoiceBox (for determinism properties) and two CheckBoxes
	 * for enabling unobservable/uncontrollable events.
	 */
	private HBox makeConversionSelector() {
		Label operationLabel = new Label("Type to convert to:");
		fsmTypeChoiceBox = new ChoiceBox<String>(FXCollections.observableArrayList("Deterministic", "Non-Deterministic"));
		return new HBox(operationLabel, fsmTypeChoiceBox);
	} // makeConversionSelector()
	
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
	 * Makes the event handler for when the "Perform Conversion" button
	 * is pressed.
	 */
	private void makeConvertEventHandler() {
		fsmNameField.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER) convertBtn.fire();
		});
		convertBtn.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER) convertBtn.fire();
		});
		convertBtn.setOnAction(e -> {
			String id = fsmNameField.getText();
			FSM currFSM = model.getCurrFSM();
			String type = fsmTypeChoiceBox.getSelectionModel().getSelectedItem();
			if(id.equals("") || model.fsmExists(id)) {
				// Then must force the user to name the FSM
				Alerts.makeError(Alerts.ERROR_OPERATION_NO_NAME);
			} else if(currFSM == null) {
				// Then cannot perform operation
				Alerts.makeError(Alerts.ERROR_OPERATION_NO_FSM);
			} else {
				if(type.equals("Deterministic")) {
					// Deterministic conversion
					DetObsContFSM newFSM = new DetObsContFSM(currFSM, id);
					addFSM(newFSM);
				} else if(type.equals("Non-Deterministic")) {
					// Observable deterministic conversion
					NonDetObsContFSM newFSM = new NonDetObsContFSM(currFSM, id);
					addFSM(newFSM);
				} else {
					// TODO: add the other kinds of FSMs we need to convert
					// No option chosen
					Alerts.makeError(Alerts.ERROR_OPERATION_NO_OP);
				} // if/else if/else
			} // if/else
		});
	} // makePerformOperationEventHandler()
	
	/**
	 * Helper method o add an FSM to the model and reset the text field.
	 * 
	 * @param newFSM - DeterministicFSM to add to the model.
	 */
	private void addFSM(FSM newFSM) {
		model.addFSM(newFSM);
		fsmNameField.setText("");
		fsmNameField.requestFocus();
	} // addFSM(DeterministicFSM, String)
} // class ConvertFSMOperationPane
