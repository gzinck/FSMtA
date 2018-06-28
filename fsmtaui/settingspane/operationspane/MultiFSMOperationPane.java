package fsmtaui.settingspane.operationspane;

import fsm.*;
import fsmtaui.Model;
import fsmtaui.popups.Alerts;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;

/**
 * Class extends the JavaFX VBox class, providing options
 * to perform an operation involving multiple FSMs (typically 2).
 * 
 * @author Mac Clevinger and Graeme Zinck
 *
 */

public class MultiFSMOperationPane extends VBox {
	/** String message for what the section does. */
	private static final String TITLE_MSG = "Perform Operations with Multiple FSMs";
	/** ObservableList of Strings with all the possible operations involving
	 * multiple FSMs that a user can choose. */
	private static final ObservableList<String> MULTI_FSM_OPERATIONS = FXCollections.observableArrayList("Union", "Product", "Parallel Composition", "Get Supremal Controllable Sublanguage");
	
	/** Model containing all the important information to display in the GUI. */
	private Model model;
	/** ChoiceBox of Strings allowing the user to choose which operation to perform. */
	private ChoiceBox<String> operationChoiceBox;
	/** ChoiceBox of Strings allowing the user to choose the first FSM on which to perform an operation. */
	private ChoiceBox<String> fsmChoiceBox1;
	/** ChoiceBox of Strings allowing the user to choose the second FSM on which to perform an operation. */
	private ChoiceBox<String> fsmChoiceBox2;
	/** TextField allowing the user to input a name for the new FSM. */
	private TextField fsmNameField;
	/** Button allowing the user to perform the selection operation on the selected FSMs. */
	private Button performOperationBtn;
	
	/**
	 * Creates a pane for all the operations involving multiple FSMs.
	 */
	public MultiFSMOperationPane(Model inModel) {
		model = inModel;
		this.getStyleClass().add("operations-subpane");
		
		Label sectionTitle = new Label(TITLE_MSG);
		sectionTitle.getStyleClass().add("subpane-section-title");
		HBox operationSelector = makeOperationSelector();
		GridPane fsmSelectors = makeFSMSelectors();
		HBox nameField = makeFSMNameField();
		performOperationBtn = new Button("Perform Operation");
		
		getChildren().addAll(sectionTitle, operationSelector, fsmSelectors, nameField, performOperationBtn);
		
		makePerformOperationEventHandler();
	} // MultiFSMOperationPane(Model)
	
	/**
	 * Makes a ChoiceBox for the user to select what operation s/he
	 * wishes to perform.
	 * 
	 * @return - HBox with the ChoiceBox and its Label.
	 */
	private HBox makeOperationSelector() {
		Label operationLabel = new Label("Pick an operation:");
		operationChoiceBox = new ChoiceBox<String>(MULTI_FSM_OPERATIONS);
		return new HBox(operationLabel, operationChoiceBox);
	} // makeOperationSelector()
	
	/**
	 * Makes the ChoiceBoxes for the user to select two FSMs to perform
	 * an operation. It does not enforce that these two FSMs be different,
	 * just that they both exist.
	 * 
	 * @return - GridPane with two ChoiceBoxes and their Labels.
	 */
	private GridPane makeFSMSelectors() {
		GridPane fsmSelectors = new GridPane();
		
		Label choice1 = new Label("First FSM to use:");
		fsmChoiceBox1 = new ChoiceBox<String>(model.getOpenFSMStrings());
		
		Label choice2 = new Label("Second FSM to use:");
		fsmChoiceBox2 = new ChoiceBox<String>(model.getOpenFSMStrings());
		
		fsmSelectors.addColumn(0, choice1, choice2);
		fsmSelectors.addColumn(1, fsmChoiceBox1, fsmChoiceBox2);
		
		return fsmSelectors;
	} // makeFSMSelectors()
	
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
	 * Creates an event handler for when the perform operation button
	 * is pressed, performing the operation.
	 */
	private void makePerformOperationEventHandler() {
		fsmNameField.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER) performOperationBtn.fire();
		});
		performOperationBtn.setOnAction(e -> {
			String id = fsmNameField.getText();
			String operation = operationChoiceBox.getSelectionModel().getSelectedItem();
			String fsm1String = fsmChoiceBox1.getSelectionModel().getSelectedItem();
			String fsm2String = fsmChoiceBox2.getSelectionModel().getSelectedItem();
			if(fsm1String.equals("") || fsm2String.equals("")) {
				// Error message if no FSMs were selected.
				Alerts.makeError(Alerts.ERROR_OPERATION_NO_FSM);
			} else if(id.equals("") || model.fsmExists(id)) {
				Alerts.makeError(Alerts.ERROR_OPERATION_NO_NAME);
			} else {
				// If all selected, continue.
				FSM fsm1 = model.getFSM(fsm1String);
				FSM fsm2 = model.getFSM(fsm2String);
				if(operation.equals(MULTI_FSM_OPERATIONS.get(0))) {
					// Perform union
					addFSM(fsm1.union(fsm2), id);
				} else if(operation.equals(MULTI_FSM_OPERATIONS.get(1))) {
					// Perform product
					addFSM(fsm1.product(fsm2), id);
				} else if(operation.equals(MULTI_FSM_OPERATIONS.get(2))) {
					// Perform parallel composition
					addFSM(fsm1.parallelComposition(fsm2), id);
				} else if(operation.equals(MULTI_FSM_OPERATIONS.get(3))) {
					// Get Supremal Controllable Sublanguage
					if(fsm1 instanceof NonDetObsContFSM)
						addFSM(((NonDetObsContFSM)fsm1).getSupremalControllableSublanguage(fsm2), id);
					else
						Alerts.makeError(Alerts.ERROR_INCOMPATIBLE_FSM_OBSCONT);
				} else {
					// Error message if no operation was selected
					Alerts.makeError(Alerts.ERROR_OPERATION_NO_OP);
				} // if/else if/else
			} // if/else
		}); // setOnAction(EventHandler<ActionEvent>)
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
} // class MultiFSMOperationPane
