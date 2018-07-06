package fsmtaui.popups;

import java.util.*;

import fsm.FSM;
import fsmtaui.Model;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * Allows the program to create a dialog box where the user can select various open FSMs.
 *  
 * @author Mac Clevinger and Graeme Zinck
 *
 */

public class SelectFSMDialog {
	Dialog<LinkedList<FSM>> dialog;
	ListView<String> openFSMBox;
	TextField fsmNameField;
	String id;
	
	/**
	 * Creates a dialog for selecting FSMs that are open. It's possible to get the
	 * FSMs selected as a collection from the object.
	 * 
	 * @param model Model object modeling everything that happens in the UI.
	 * @param title Title for the dialog box.
	 * @param header Header for the dialog box.
	 */
	public SelectFSMDialog(Model model, String title, String header) {
		dialog = new Dialog<LinkedList<FSM>>();
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
            		LinkedList<FSM> fsms = new LinkedList<FSM>();
            		for(String n : fsmNames) {
            			FSM fsm = model.getFSM(n);
            			if(fsm != null) fsms.add(fsm);
            		} // for each fsm name
            		
            		// Must have an id
            		id = fsmNameField.getText();
            		return fsms;
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
	        } else if(id.equals("") || model.fsmExists(id)) {
				Alerts.makeError(Alerts.ERROR_OPERATION_NO_NAME);
				event.consume();
	    		} // if illegal name
		});
	}
	
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
	 * Gets the user's FSM selection that they specify in the dialog and returns it.
	 * 
	 * @return Collection of FSM objects to perform the operation.
	 */
	public LinkedList<FSM> getFSMs() {
		// Shows the dialog and returns results
		Optional<LinkedList<FSM>> optionalResult = dialog.showAndWait();
		try {
			return optionalResult.get();
		} catch(NoSuchElementException e) {
			return null;
		} // try/catch
	} // getFSMs()
	
	/**
	 * Gets the id of the FSM to create.
	 * 
	 * @return String representing the id for the FSM to create
	 */
	public String getId() {
		return id;
	}
}
