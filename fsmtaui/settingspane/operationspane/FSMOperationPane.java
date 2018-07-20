package fsmtaui.settingspane.operationspane;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import fsmtaui.Model;

/**
 * This class is a specialized javafx VBox element which contains all the settings for
 * performing operations on multiple FSMs in the GUI.
 * 
 * This class is a part of the fsmtaui.settingspane.operationspane package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public class FSMOperationPane extends VBox {
	
//---  Constants   ----------------------------------------------------------------------------
	
	/** int constant value representing the number of pixels wide the settings sidebar will be. */
	private static final int OPTIONS_WIDTH = 382;
	
//---  Instance Variables   -------------------------------------------------------------------
	
	/** Model object instance variable containing all the important information to display in the GUI. */
	private Model model;
	/** VBox object instance variable representing an accordion with all the options for the pane. */
	private VBox optionBoxes;
	/** TitledPane object instance variable with the options to evaluate properties of a single FSM. */
	private TitledPane evaluateFSMOperationPane;
	/** TitledPane object instance variable with the options to perform an operation on a single FSM. */
	private TitledPane singleFSMOperationPane;
	/** TitledPane object instance variable with the options to perform an conversion on an FSM. */
	private TitledPane convertFSMOperationPane;
	/** TitledPane object instance variable with the options to perform an operation on multiple FSMs. */
	private TitledPane multiFSMOperationPane;
	
//---  Constructors   -------------------------------------------------------------------------
	
	/**
	 * Constructor for an FSMOperationPane object that creates a new FSMOperationsPane which has all
	 * the options for performing operations on the FSMs the user has already opened.
	 * 
	 * @param inModel - Model object that TODO:
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
