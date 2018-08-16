package fsmtaui.settingspane.operationspane;

import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import support.transition.Transition;
import javafx.scene.input.KeyCode;
import javafx.scene.control.*;
import fsmtaui.popups.Alerts;
import javafx.scene.layout.*;
import fsmtaui.Model;
import fsm.*;

/**
 * This class creates a Javafx VBox element which has all the options to perform operations on a
 * single FSM (the FSM that is currently in view)
 * 
 * This class is a part of the fsmtaui.settingspane.operationspane package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public class SingleFSMOperationPane extends VBox {
	
//---  Constants   ----------------------------------------------------------------------------
	
	/** String object constant representing the message for what the section does. */
	private static final String TITLE_MSG = "Perform an Operation on the Current FSM";
	
//---  Instance Variables   -------------------------------------------------------------------
	
	/** Model object instance variable containing all the important information to display in the GUI. */
	private Model model;
	/** ChoiceBox of String object instance variables allowing the user to choose which operation to perform. */
	private ChoiceBox<String> operationChoiceBox;
	/** Field object instance variable for the name of the new FSM to create. */
	private TextField fsmNameField;
	/** ObservableList of String object instance variables with all the possible operations involving one FSM that a user can choose. */
	private static final ObservableList<String> SINGLE_FSM_OPERATIONS = FXCollections.observableArrayList("Determinize", 
				"Make Accessible", "Make CoAccessible", "Trim", "Build Optimal Opaque Controller");
	/** Button object instance variable allowing the user to perform the selection operation on the selected FSM. */
	private Button performOperationBtn;
	
//---  Constructors   -------------------------------------------------------------------------
	
	/**
	 * Constructor for a SingleFSMOperationPane that creates a pane with all the single FSM operations.
	 * 
	 * @param inModel - Model object
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

//---  Operations   ---------------------------------------------------------------------------
	
	/**
	 * This method makes a ChoiceBox for the user to select what operation s/he
	 * wishes to perform.
	 * 
	 * @return - Returns an HBox object with the ChoiceBox and its Label.
	 */

	private HBox makeOperationSelector() {
		Label operationLabel = new Label("Pick an operation:");
		operationChoiceBox = new ChoiceBox<String>(SINGLE_FSM_OPERATIONS);
		return new HBox(operationLabel, operationChoiceBox);
	} // makeOperationSelector()
	
	/**
	 * This method makes a name TextField for the new FSM that is created.
	 * 
	 * @return - Returns an HBox object containing the name TextField and its Label.
	 */

	private HBox makeFSMNameField() {
		Label fsmNameLabel = new Label("New FSM Name:");
		fsmNameField = new TextField();
		return new HBox(fsmNameLabel, fsmNameField);
	} // makeFSMNameField()
	
	/**
	 * This method makes the event handler for when the "Perform Operation" button is pressed to
	 * perform the operation on the current FSM.
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
			TransitionSystem<? extends Transition> currTS = model.getCurrTS();
			String operation = operationChoiceBox.getSelectionModel().getSelectedItem();
			if(id.equals("") || model.tsExists(id)) {
				// Then must force the user to name the FSM
				Alerts.makeError(Alerts.ERROR_OPERATION_NO_NAME);
			} else if(currTS == null) {
				// Then cannot perform operation
				Alerts.makeError(Alerts.ERROR_OPERATION_NO_FSM);
			} else {
				if(operation.equals(SINGLE_FSM_OPERATIONS.get(0))) {
					// Determinize
					if(currTS instanceof FSM<?>) 
						addTS(((FSM<?>)currTS).buildObserver(), id);
					else
						Alerts.makeError(Alerts.ERROR_OPERATION_ONLY_FOR_FSMS);
				} else if(operation.equals(SINGLE_FSM_OPERATIONS.get(1))) {
					// Accessible
					addTS(currTS.makeAccessible(), id);
				} else if(operation.equals(SINGLE_FSM_OPERATIONS.get(2))) {
					// CoAccessible
					addTS(currTS.makeCoAccessible(), id);
				} else if(operation.equals(SINGLE_FSM_OPERATIONS.get(3))) {
					// Trim
					addTS(currTS.trim(), id);
				} else if(operation.equals(SINGLE_FSM_OPERATIONS.get(4))) {
					if(currTS instanceof ModalSpecification)
						addTS(((ModalSpecification)currTS).buildOptimalOpaqueController(), id);
					else
						Alerts.makeError(Alerts.ERROR_OPERATION_ONLY_FOR_FSMS);
				} else {
					// No option chosen
					Alerts.makeError(Alerts.ERROR_OPERATION_NO_OP);
				}
			} // if/else
		});
	} // makePerformOperationEventHandler()
	
//---  Manipulations   ------------------------------------------------------------------------
	
	/**
	 * Helper method to add an FSM to the model and reset the text field.
	 * 
	 * @param newTS - TransitionSystem object to add to the model.
	 * @param id - String object representing the id of the FSM.
	 */
	
	private void addTS(TransitionSystem<? extends Transition> newTS, String id) {
		newTS.setId(id);
		model.addTS(newTS);
		fsmNameField.setText("");
		fsmNameField.requestFocus();
	} // addFSM(DeterministicFSM, String)

} // class SingleFSMOperationPane