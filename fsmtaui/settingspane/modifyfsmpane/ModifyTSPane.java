package fsmtaui.settingspane.modifyfsmpane;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import fsmtaui.Model;

/**
 * ModifyTSPane is a specialized javafx VBox element which contains all the settings
 * for modifying characteristics of a single TransitionSystem in the GUI.
 * 
 * This class is a part of the fsmtaui.settingspane.modifyfsmpane package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public class ModifyTSPane extends VBox {
	
//---  Constants   ----------------------------------------------------------------------------
	
	/** int constant value for the number of pixels wide the settings sidebar will be. */
	private static final int OPTIONS_WIDTH = 382;
	
//---  Instance Variables   -------------------------------------------------------------------
	
	/** Model object instance variable containing all the important information to display in the GUI. */
	private Model model;
	/** VBox object instance variable accordion with all the options for the pane. */
	private VBox optionBoxes;
	/** TitledPane object instance variable containing all the options for the events. */
	private TitledPane eventOptions;
	/** TitledPane object instance variable containing all the options for adding/removing states. */
	private TitledPane stateOptions;
	/** TitledPane object instance variable containing all the options for the editing the properties of preexisting states. */
	private TitledPane statePropertiesOptions;
	/** TitledPane object instance variable containing all the options for editing the properties of preexisting events. */
	private TitledPane eventPropertiesOptions;
	
//---  Constructors   -------------------------------------------------------------------------
	
	/**
	 * Constructor for a ModifyTSPane object that creates a new ModifyFSMPane, which allows the user to change
	 * the events, transitions, etc. of the currently open FSM.
	 * 
	 * @param inModel - Model object with all the important information to display in the GUI.
	 */
	
	public ModifyTSPane(Model inModel) {
		super();
		model = inModel;
		Label titleLabel = new Label("Modify FSM");
		titleLabel.getStyleClass().add("section-header");
		
		// Create the event/state add and remove options
		eventOptions = new TitledPane("Event Options", new ModifyTSEventsPane(model));
		eventPropertiesOptions = new TitledPane("Event Properties Options", new ModifyTSEventPropertiesPane(model));
		stateOptions = new TitledPane("State Options", new ModifyTSStatesPane(model));
		statePropertiesOptions = new TitledPane("State Properties Options", new ModifyTSStatePropertiesPane(model));
		
		optionBoxes = new VBox(eventOptions, eventPropertiesOptions, stateOptions, statePropertiesOptions);
		optionBoxes.setPrefWidth(OPTIONS_WIDTH);
		ScrollPane scrollable = new ScrollPane();
		scrollable.setContent(optionBoxes);
		
		getChildren().addAll(titleLabel, scrollable);
	} // SettingsPane()

} // class ModifyFSMPane