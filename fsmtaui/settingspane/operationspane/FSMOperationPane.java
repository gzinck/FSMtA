package fsmtaui.settingspane.operationspane;

import fsmtaui.Model;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * FSMOperationsPane is a specialized javafx VBox element which contains all the settings
 * for performing operations on multiple FSMs in the GUI.

 * @author Mac Clevinger and Graeme Zinck
 *
 */
public class FSMOperationPane extends VBox {	
	/** Model containing all the important information to display in the GUI. */
	private Model model;
	
	/** Accordion with all the options for the pane. */
	private Accordion optionBoxes;
	
	/** Pane with the options to perform an operation on multiple FSMs. */
	private TitledPane multiFSMOperationPane;
	/** Pane with the options to perform an operation on a single FSM. */
	private TitledPane singleFSMOperationPane;
	/** Pane with the options to perform an conversion on an FSM. */
	private TitledPane convertFSMOperationPane;
	
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
		
		multiFSMOperationPane = new TitledPane("Multi-FSM Operations", new MultiFSMOperationPane(model));
		singleFSMOperationPane = new TitledPane("Single-FSM Operations", new SingleFSMOperationPane(model));
		convertFSMOperationPane = new TitledPane("Conversion Operations", new ConvertFSMOperationPane(model));
		
		optionBoxes = new Accordion(multiFSMOperationPane, singleFSMOperationPane, convertFSMOperationPane);
		optionBoxes.setExpandedPane(multiFSMOperationPane);
		
		getChildren().addAll(titleLabel, optionBoxes);
	} // FSMOperationsPane(ObservableList<String>)
} // class FSMOperationsPane
