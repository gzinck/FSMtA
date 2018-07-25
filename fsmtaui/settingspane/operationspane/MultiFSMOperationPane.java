package fsmtaui.settingspane.operationspane;

import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import fsmtaui.popups.SelectFSMDialog;
import support.transition.Transition;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.LinkedList;
import fsmtaui.popups.Alerts;
import fsmtaui.Model;
import fsm.*;

/**
 * This class extends the JavaFX VBox class, providing options to perform an operation involving multiple FSMs (typically 2).
 * 
 * This class is a part of the fsmtaui.settingspane.operationspane package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public class MultiFSMOperationPane extends VBox {

//---  Constants   ----------------------------------------------------------------------------
	
	/** String object constant representing the message for what the section does. */
	private static final String TITLE_MSG = "Perform Operations with Multiple FSMs";
	/** ObservableList of String object constants with all the possible operations involving multiple FSMs that a user can choose. */
	private static final ObservableList<String> MULTI_FSM_OPERATIONS = FXCollections.observableArrayList("Product", "Parallel Composition", "Get Supremal Controllable Sublanguage");
	
//---  Instance Variables   -------------------------------------------------------------------
	
	/** Model object instance variable containing all the important information to display in the GUI. */
	private Model model;
	/** ChoiceBox of String object instance variables allowing the user to choose which operation to perform. */
	private ChoiceBox<String> operationChoiceBox;
	/** Button object instance variable allowing the user to perform the selection operation on the selected FSMs. */
	private Button performOperationBtn;
	
//---  Constructors   -------------------------------------------------------------------------
	
	/**
	 * Constructor for a MultiFSMOperationsPane object that creates a pane for all the operations involving multiple FSMs.
	 * 
	 * @param inModel - Model object TODO:
	 */
	
	public MultiFSMOperationPane(Model inModel) {
		model = inModel;
		this.getStyleClass().add("operations-subpane");
		
		Label sectionTitle = new Label(TITLE_MSG);
		sectionTitle.getStyleClass().add("subpane-section-title");
		VBox operationSelector = makeOperationSelector();
		performOperationBtn = new Button("Perform Operation");
		
		getChildren().addAll(sectionTitle, operationSelector, performOperationBtn);
		
		makePerformOperationEventHandler();
	} // MultiFSMOperationPane(Model)
	
//---  Operations   ---------------------------------------------------------------------------
	
	/**
	 * This method makes a ChoiceBox for the user to select what operation they wish to perform.
	 * 
	 * @return - Returns an HBox object with the ChoiceBox and its Label.
	 */

	private VBox makeOperationSelector() {
		Label operationLabel = new Label("Pick an operation:");
		operationChoiceBox = new ChoiceBox<String>(MULTI_FSM_OPERATIONS);
		return new VBox(operationLabel, operationChoiceBox);
	} // makeOperationSelector()
	
	/**
	 * This method creates an event handler for when the perform operation button is pressed, performing the operation.
	 */

	private void makePerformOperationEventHandler() {
		performOperationBtn.setOnAction(e -> {
			String operation = operationChoiceBox.getSelectionModel().getSelectedItem();
			if(operation == null) {
				Alerts.makeError(Alerts.ERROR_OPERATION_NO_NAME);
			} else {
				SelectFSMDialog selectionDialog = new SelectFSMDialog(model, "Operation Selection Options", "Write a name for the new FSM, and drag the desired FSMs to the right box to perform the operation.");
				LinkedList<FSM<? extends Transition>> fsmList = selectionDialog.getTSs();
				
				// Exit if there was a problem
				if(fsmList == null || fsmList.size() < 2) return;
				
				String id = selectionDialog.getId();
				FSM<?> fsm1 = fsmList.removeFirst();
				FSM<?>[] fsms = fsmList.toArray(new FSM<?>[fsmList.size()]);
				if(operation.equals(MULTI_FSM_OPERATIONS.get(0))) {
					// Perform product
					addFSM(fsm1.product(fsms), id);
				} else if(operation.equals(MULTI_FSM_OPERATIONS.get(1))) {
					// Perform parallel composition
					addFSM(fsm1.parallelComposition(fsms), id);
				} else if(operation.equals(MULTI_FSM_OPERATIONS.get(2))) {
					// Get Supremal Controllable Sublanguage
					if(fsm1 instanceof NonDetObsContFSM)
						addFSM(((NonDetObsContFSM)fsm1).getSupremalControllableSublanguage(fsms[0]), id);
					else
						Alerts.makeError(Alerts.ERROR_INCOMPATIBLE_FSM_OBSCONT);
				} else {
					// Error message if no operation was selected
					Alerts.makeError(Alerts.ERROR_OPERATION_NO_OP);
				} // if/else if/else
			} // if/else
		}); // setOnAction(EventHandler<ActionEvent>)
	} // makePerformOperationEventHandler()
	
//---  Operations   ---------------------------------------------------------------------------
	
	/**
	 * Helper method o add an FSM to the model and reset the text field.
	 * 
	 * @param newTS - TransitionSystem object to add to the model.
	 * @param id - String object representing the id of the FSM.
	 */

	private void addFSM(TransitionSystem<?> newTS, String id) {
		newTS.setId(id);
		model.addTS(newTS);
	} // addFSM(TransitionSystem, String)

} // class MultiFSMOperationPane