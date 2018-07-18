package fsmtaui.settingspane.operationspane;

import fsmtaui.Model;
import fsmtaui.popups.Alerts;
import fsmtaui.popups.CSODialog;
import fsm.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import support.transition.Transition;

/**
 * Javafx VBox element which has all the options for evaluating the
 * current FSM in various ways (i.e., there is *no new fsm* generated
 * by operations in this pane).
 * 
 * @author Mac Clevinger and Graeme Zinck
 *
 */
public class EvaluateFSMOperationPane extends VBox {
	/** String message for what the section does. */
	private static final String TITLE_MSG = "Evaluate the Current FSM";
	/** String message for the results dialog box. */
	private static final String RESULT_TITLE = "Operation Result";
	/** String message for the results dialog box when the FSM is blocking. */
	private static final String IS_BLOCKING_MSG = "The FSM is blocking. The bad states are coloured in the image.";
	/** String message for the results dialog box when the FSM is non-blocking. */
	private static final String NOT_BLOCKING_MSG = "The FSM is non-blocking.";
	
	/** Model containing all the important information to display in the GUI. */
	private Model model;
	/** ChoiceBox of Strings allowing the user to choose which operation to perform. */
	private ChoiceBox<String> evaluationChoiceBox;
	/** ObservableList of Strings with all the possible operations involving
	 * one FSM that a user can choose. */
	private static final ObservableList<String> EVALUATION_OPTIONS = FXCollections.observableArrayList("Check Blocking", "Check for Current State Opacity");
	/** Button allowing the user to perform the selection evaluatioln on the selected FSM. */
	private Button evaluateBtn;
	
	/**
	 * Creates a pane with all the evaluation options for an FSM.
	 */
	public EvaluateFSMOperationPane(Model inModel) {
		model = inModel;
		this.getStyleClass().add("operations-subpane");
		
		Label sectionTitle = new Label(TITLE_MSG);
		sectionTitle.getStyleClass().add("subpane-section-title");
		HBox evaluationSelector = makeEvaluationSelector();
		evaluateBtn = new Button("Evaluate FSM");
		
		getChildren().addAll(sectionTitle, evaluationSelector, evaluateBtn);
		
		makeEvaluateEventHandler();
	} // SingleFSMOperationPane()
	
	/**
	 * Makes a ChoiceBox for the user to select what evaluation s/he
	 * wishes to perform.
	 * 
	 * @return HBox with the ChoiceBox and its Label.
	 */
	private HBox makeEvaluationSelector() {
		Label evaluationLabel = new Label("Pick an operation:");
		evaluationChoiceBox = new ChoiceBox<String>(EVALUATION_OPTIONS);
		return new HBox(evaluationLabel, evaluationChoiceBox);
	} // makeOperationSelector()
	
	/**
	 * Makes the event handler for when the "Perform Operation"
	 * button is pressed to perform the operation on the current
	 * FSM.
	 */
	private void makeEvaluateEventHandler() {
		evaluateBtn.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER) evaluateBtn.fire();
		});
		evaluateBtn.setOnAction(e -> {
			TransitionSystem<? extends Transition> currFSM = model.getCurrTS();
			String operation = evaluationChoiceBox.getSelectionModel().getSelectedItem();
			if(currFSM == null) {
				// Then cannot perform evaluation
				Alerts.makeError(Alerts.ERROR_OPERATION_NO_FSM);
			} else {
				if(operation.equals(EVALUATION_OPTIONS.get(0))) {
					// Check if blocking
					boolean isBlocking = currFSM.isBlocking();
					model.refreshViewport();
					String message = isBlocking ? IS_BLOCKING_MSG : NOT_BLOCKING_MSG;
					Alerts.makeInfoBox(RESULT_TITLE, message);
				} else if(operation.equals(EVALUATION_OPTIONS.get(1))) {
					// Check if current state opaque
					if(currFSM instanceof FSM<?>) {
						FSM<? extends Transition> newFSM = CSODialog.testCSO((FSM<?>)currFSM);
						if(newFSM != null) model.addTS(newFSM);
					} else {
						Alerts.makeError(Alerts.ERROR_OPERATION_ONLY_FOR_FSMS);
					}
				} else {
					// No option chosen
					Alerts.makeError(Alerts.ERROR_OPERATION_NO_OP);
				} // if/else 
			} // if/else
		});
	} // makePerformOperationEventHandler()
} // class SingleFSMOperationPane
