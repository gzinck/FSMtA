package fsmtaui.settingspane.modifyfsmpane;

import fsmtaui.Model;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * ModifyFSMPane is a specialized javafx VBox element which contains all the settings
 * for modifying characteristics of a single FSM in the GUI.

 * @author Mac Clevinger and Graeme Zinck
 *
 */
public class ModifyFSMPane extends VBox {
	
	/** Model containing all the important information to display in the GUI. */
	private Model model;
	
	/** Accordion with all the options for the pane. */
	private Accordion optionBoxes;
	
	/** TitledPane containing all the options for the events. */
	private TitledPane eventOptions;
	/** TitledPane containing all the options for adding/removing states. */
	private TitledPane stateOptions;
	/** TitledPane containing all the options for the initial state. */
	private TitledPane initialStateOptions;
	/** TitledPane containing all the options for the marked states. */
	private TitledPane markedStateOptions;
	/** TitledPane containing all the below elements for the observability options. */
	private TitledPane observabilityOptions;
	
	/**
	 * Creates a new ModifyFSMPane, which allows the user to change the
	 * events, transitions, etc. of the currently open FSM.
	 * 
	 * @param inModel - Model with all the important information to
	 * display in the GUI.
	 */
	public ModifyFSMPane(Model inModel) {
		super();
		model = inModel;
		Label titleLabel = new Label("Modify FSM");
		
		// Create the event/state add and remove options
		eventOptions = new TitledPane("Event Options", new ModifyFSMEventsPane(model));
		stateOptions = new TitledPane("State Options", new ModifyFSMStatesPane(model));
		initialStateOptions = new TitledPane("Initial State Options", new ModifyFSMInitialStatesPane(model));
		markedStateOptions = new TitledPane("Marked State Options",new ModifyFSMMarkedStatesPane(model));
		observabilityOptions = new TitledPane("Observability Options", new ModifyFSMObservablePane(model));
		
		optionBoxes = new Accordion(eventOptions, stateOptions, initialStateOptions, markedStateOptions, observabilityOptions);
		optionBoxes.setExpandedPane(eventOptions);
		getChildren().addAll(titleLabel, optionBoxes);
		
		// Make event handlers
		
		refreshOptions();
	} // SettingsPane()
	
	/**
	 * Handles whenever the current FSM changes. When the FSM changes, if
	 * the new FSM is nondeterministic, then the options reflect that.
	 */
	private void refreshOptions() {
		model.getOpenFSMTabs().getSelectionModel().selectedItemProperty().addListener(
			(ObservableValue<? extends Tab> value, Tab oldTab, Tab newTab) -> {
				if(newTab != null) {
					optionBoxes.setExpandedPane(eventOptions);
				} // if
			});
	} // refreshOptions()
} // class ModifyFSMPane