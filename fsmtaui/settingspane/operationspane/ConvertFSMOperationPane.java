package fsmtaui.settingspane.operationspane;

import fsm.*;
import fsmtaui.Model;
import fsmtaui.popups.Alerts;
import fsmtaui.settingspane.FileSettingsPane;

import javafx.scene.control.Button;
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
	/** ChoiceBox of Strings allowing the user to choose which type of FSM to convert to. */
	private ChoiceBox<String> conversionChoiceBox;
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
		
		Label sectionTitle = new Label(TITLE_MSG);
		HBox operationSelector = makeConversionSelector();
		HBox nameField = makeFSMNameField();
		convertBtn = new Button("Perform Conversion");
		
		getChildren().addAll(sectionTitle, operationSelector, nameField, convertBtn);
		
		makeConvertEventHandler();
	} // ConvertFSMOperationPane()
	
	/**
	 * Makes a ChoiceBox for the user to select what conversion s/he
	 * wishes to perform.
	 * 
	 * @return - HBox with the ChoiceBox and its Label.
	 */
	private HBox makeConversionSelector() {
		Label operationLabel = new Label("Type to convert to:");
		conversionChoiceBox = new ChoiceBox<String>(FileSettingsPane.FSM_TYPES);
		return new HBox(operationLabel, conversionChoiceBox);
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
			String operation = conversionChoiceBox.getSelectionModel().getSelectedItem();
			if(id.equals("") || model.fsmExists(id)) {
				// Then must force the user to name the FSM
				Alerts.makeError(Alerts.ERROR_OPERATION_NO_NAME);
			} else if(currFSM == null) {
				// Then cannot perform operation
				Alerts.makeError(Alerts.ERROR_OPERATION_NO_FSM);
			} else {
				if(operation.equals(FileSettingsPane.FSM_TYPES.get(0))) {
					// Deterministic conversion
					DetFSM newFSM = new DetFSM(currFSM, id);
					addFSM(newFSM);
				} else if(operation.equals(FileSettingsPane.FSM_TYPES.get(1))) {
					// Non-deterministic conversion
					// TODO: perform the conversion
				} else if(operation.equals(FileSettingsPane.FSM_TYPES.get(2))) {
					// Observable conversion
					// TODO: perform the conversion
				} else {
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
