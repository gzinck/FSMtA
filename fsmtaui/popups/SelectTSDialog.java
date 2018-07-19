package fsmtaui.popups;

import java.util.*;

import fsm.FSM;
import fsm.TransitionSystem;
import fsmtaui.Model;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import support.transition.Transition;

/**
 * Allows the program to create a dialog box where the user can select various open TransitionSystems.
 *  
 * This class is a part of the fsmtaui.popups package.
 *  
 * @author Mac Clevinger and Graeme Zinck
 */

public class SelectTSDialog {
	
//---  Instance Variables   -------------------------------------------------------------------
	
	/** */
	Dialog<LinkedList<FSM<? extends Transition>>> dialog;
	/** */
	ListView<String> openFSMBox;
	/** */
	TextField fsmNameField;
	/** */
	String id;
	
//---  Constructors   -------------------------------------------------------------------------
	
	/**
	 * Creates a dialog for selecting FSMs that are open. It's possible to get the
	 * FSMs selected as a collection from the object.
	 * 
	 * @param model Model object modeling everything that happens in the UI.
	 * @param title Title for the dialog box.
	 * @param header Header for the dialog box.
	 */
	public SelectTSDialog(Model model, String title, String header) {
		dialog = new Dialog<LinkedList<FSM<? extends Transition>>>();
		dialog.setTitle(title);
		dialog.setHeaderText(header);
		
		// Create the grid for all the options
		HBox name = makeFSMNameField();
		openFSMBox = new ListView<String>(model.getOpenFSMStrings());
		openFSMBox.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		// Add the options and buttons.
		DialogPane dPane = dialog.getDialogPane();
		dPane.setContent(new VBox(name, openFSMBox));
		dPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		// Sets up the return result as an FSMParameter object
		dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
            		Collection<String> fsmNames = openFSMBox.getSelectionModel().getSelectedItems();
            		LinkedList<FSM<? extends Transition>> tsList = new LinkedList<FSM<? extends Transition>>();
            		for(String n : fsmNames) {
            			TransitionSystem<? extends Transition> fsm = model.getTS(n);
            			if(fsm != null && fsm instanceof FSM<?>) tsList.add((FSM<?>)fsm); // Add the fsm, if it exists
            		} // for each fsm name
            		
            		// Must have an id
            		id = fsmNameField.getText();
            		return tsList;
            }// if
            return null;
        });
		
		// This makes sure that nothing closes when press OK and there aren't
		// enough FSMs selected.
		final Button btOk = (Button) dPane.lookupButton(ButtonType.OK);
		btOk.addEventFilter(ActionEvent.ACTION, event -> {
			// Check whether some conditions are fulfilled
			id = fsmNameField.getText();    
			if (openFSMBox.getSelectionModel().getSelectedItems().size() < 2) {
				Alerts.makeError(Alerts.ERROR_MULTI_OPERATION_NO_FSM);
	        		event.consume();
	        } else if(id.equals("") || model.tsExists(id)) {
				Alerts.makeError(Alerts.ERROR_OPERATION_NO_NAME);
				event.consume();
	    		} // if illegal name
		});
	}

//---  Manipulations   ------------------------------------------------------------------------
	
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

//---  Getter Methods   -----------------------------------------------------------------------
	
	/**
	 * Gets the user's FSM selection that they specify in the dialog and returns it.
	 * 
	 * @return - Returns a LinkedList Collection of FSM objects to perform the operation.
	 */

	public LinkedList<FSM<? extends Transition>> getTSs() {
		// Shows the dialog and returns results
		Optional<LinkedList<FSM<? extends Transition>>> optionalResult = dialog.showAndWait();
		try {
			return optionalResult.get();
		} catch(NoSuchElementException e) {
			return null;
		} // try/catch
	} // getFSMs()
	
	/**
	 * Gets the id of the FSM to create.
	 * 
	 * @return - Returns a String object representing the id for the FSM to create
	 */

	public String getId() {
		return id;
	}

}
