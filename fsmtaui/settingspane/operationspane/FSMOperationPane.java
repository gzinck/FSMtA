package fsmtaui.settingspane.operationspane;

import fsmtaui.Model;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * FSMOperationsPane is a specialized javafx VBox element which contains all the settings
 * for performing operations on multiple FSMs in the GUI.
 * 
 * This class is a part of the fsmtaui.settingspane.operationspane package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public class FSMOperationPane extends VBox {
	
//---  Constants   ----------------------------------------------------------------------------
	
	/** Integer for the number of pixels wide the settings sidebar will be. */
	private static final int OPTIONS_WIDTH = 382;
	
//---  Instance Variables   -------------------------------------------------------------------
	
	/** Model containing all the important information to display in the GUI. */
	private Model model;
	/** Accordion with all the options for the pane. */
	private VBox optionBoxes;
	/** Pane with the options to evaluate properties of a single FSM. */
	private TitledPane evaluateFSMOperationPane;
	/** Pane with the options to perform an operation on a single FSM. */
	private TitledPane singleFSMOperationPane;
	/** Pane with the options to perform an conversion on an FSM. */
	private TitledPane convertFSMOperationPane;
	/** Pane with the options to perform an operation on multiple FSMs. */
	private TitledPane multiFSMOperationPane;
	
//---  Constructors   -------------------------------------------------------------------------
	
	/**
	 * Creates a new FSMOperationsPane which has all the options for performing
	 * operations on the FSMs the user has already opened.
	 * 
	 * @param inOpenFSMStrings - An observable ArrayList of the openFSMs' ids, used
	 * for listing all the open FSMs in various ChoiceBoxes.
	 */
	
	public FSMOperationPane(Model inModel) {
		model = inModel;
		
		// Make the title for the section
		Label titleLabel = new Label("FSM Operations");
		titleLabel.getStyleClass().add("section-header");
		
		evaluateFSMOperationPane = new TitledPane("FSM Evaluation Operations", new EvaluateFSMOperationPane(model));
		singleFSMOperationPane = new TitledPane("Single-FSM Operations", new SingleFSMOperationPane(model));
		convertFSMOperationPane = new TitledPane("Conversion Operations", new ConvertFSMOperationPane(model));
		multiFSMOperationPane = new TitledPane("Multi-FSM Operations", new MultiFSMOperationPane(model));
		
		optionBoxes = new VBox(evaluateFSMOperationPane, singleFSMOperationPane, convertFSMOperationPane, multiFSMOperationPane);
		optionBoxes.setPrefWidth(OPTIONS_WIDTH);
		ScrollPane scrollable = new ScrollPane();
		scrollable.setContent(optionBoxes);
		
		getChildren().addAll(titleLabel, scrollable);
	} // FSMOperationsPane(ObservableList<String>)

} // class FSMOperationsPane
