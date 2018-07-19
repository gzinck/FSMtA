package fsmtaui.settingspane;

import javafx.scene.control.*;
import javafx.scene.control.TabPane.*;
import javafx.scene.layout.*;
import fsmtaui.Model;
import fsmtaui.settingspane.modifyfsmpane.ModifyTSPane;
import fsmtaui.settingspane.operationspane.FSMOperationPane;
import javafx.collections.*;

/**
 * SettingsPane holds all the options to open, modify, and perform operations
 * on FSMs.
 * 
 * This class is a part of the fsmtaui.settingspane package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public class SettingsPane extends VBox {

//---  Constants   ----------------------------------------------------------------------------
	
	/** String array with all the names for settings tabs, */
	private static final String[] TAB_NAMES = { "File Options", "Modify FSM", "FSM Operations" };
	
//---  Instance Variables   -------------------------------------------------------------------
	
	/** Model containing all the important information to display in the GUI. */
	private Model model;
	/** TabPane for all the panes of settings. Tabs are not closable. */ 
	private TabPane tabPane;
	/** FileSettingsPane with all the options to open/close/save files. */
	FileSettingsPane fileSettingsPane;
	/** ModifyFSMPane with all the options to modify an FSM. */
	ModifyTSPane modifyFSMPane;
	/** FSMOperationsPane with all the options to perform operations on FSMs. */
	FSMOperationPane fsmOperationsPane;
	
//---  Constructors   -------------------------------------------------------------------------
	
	/**
	 * Creates a SettingsPane with all the panes of options for FSMs.
	 * 
	 * @param inModel - Model with all the important information to
	 * display in the GUI.
	 */
	
	public SettingsPane(Model inModel) {
		super();
		model = inModel;
		tabPane = new TabPane();
		makeTabs();
		
		getChildren().addAll(tabPane);
	} // SettingsPane()
	
//---  Operations   ---------------------------------------------------------------------------
	
	/**
	 * Helper method that creates all the tabs of settings panes.
	 */
	
	private void makeTabs() {
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		ObservableList<Tab> tabs = tabPane.getTabs();
		
		fileSettingsPane = new FileSettingsPane(model);
		modifyFSMPane = new ModifyTSPane(model);
		fsmOperationsPane = new FSMOperationPane(model);
		
		tabs.add(new Tab(TAB_NAMES[0], fileSettingsPane));
		tabs.add(new Tab(TAB_NAMES[1], modifyFSMPane));
		tabs.add(new Tab(TAB_NAMES[2], fsmOperationsPane));
	} // makeTabs()
	
} // class SettingsPane