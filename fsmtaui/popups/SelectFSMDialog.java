package fsmtaui.popups;

import javafx.scene.input.ClipboardContent;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.scene.input.TransferMode;
import support.transition.Transition;
import javafx.scene.input.Dragboard;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import fsm.TransitionSystem;
import fsmtaui.Model;
import java.util.*;
import fsm.FSM;

/**
 * This class allows the program to create a dialog box where the user can select various open TransitionSystems.
 *  
 * This class is a part of the fsmtaui.popups package.
 *  
 * @author Mac Clevinger and Graeme Zinck
 */

public class SelectFSMDialog {
	
//---  Instance Variables   -------------------------------------------------------------------
	
	/** Dialog<<r>LinkedList<<r>FSM<<r>Transition>>> instance variable which asks the user for FSMs to perform an operation upon. */
	Dialog<LinkedList<FSM<? extends Transition>>> dialog;
	/** ListView<<r>String> instance variable representing a list of all the open FSMs when the dialog opened. */
	ListView<String> openFSMBox;
	/** ListView<<r>String> instance variable representing a list of all the FSMs the user selects for the operation. */
	ListView<String> selectedFSMBox;
	/** TextField instance variable for the name for the new FSM to create */
	TextField fsmNameField;
	/** String instance variable for the id of the FSM to create */
	String id;
	
//---  Constructors   -------------------------------------------------------------------------
	
	/**
	 * Creates a dialog for selecting FSMs that are open. It's possible to get the FSMs selected as a collection from the object.
	 * 
	 * @param model - Model object modeling everything that happens in the UI.
	 * @param title - String object representing the title for the dialog box.
	 * @param header - String object representing the header for the dialog box.
	 */
	
	public SelectFSMDialog(Model model, String title, String header) {
		dialog = new Dialog<LinkedList<FSM<? extends Transition>>>();
		dialog.setTitle(title);
		dialog.setHeaderText(header);
		
		// Create the grid for all the options
		HBox name = makeFSMNameField();
		String[] openFSMs = model.getOpenFSMStrings().toArray(new String[model.getOpenFSMStrings().size()]);
		
		Label openFSMBoxLabel = new Label("Available FSMs");
		openFSMBox = new ListView<String>(FXCollections.observableArrayList(openFSMs));
		openFSMBox.setCellFactory(param -> new FSMNameCell());
		openFSMBox.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		
		Label selectedFSMBoxLabel = new Label("Selected FSMs");
		selectedFSMBox = new ListView<String>();
		selectedFSMBox.setCellFactory(param -> new FSMNameCell());
		selectedFSMBox.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		addDragHandlers(openFSMBox);
		addDragHandlers(selectedFSMBox);
		
		GridPane fsmChooser = new GridPane();
		fsmChooser.addRow(0, openFSMBoxLabel, selectedFSMBoxLabel);
		fsmChooser.addRow(1, openFSMBox, selectedFSMBox);
		
		// Add the options and buttons.
		DialogPane dPane = dialog.getDialogPane();
		dPane.setContent(new VBox(name, fsmChooser));
		dPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		// Sets up the return result as an FSMParameter object
		dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
            		Collection<String> fsmNames = selectedFSMBox.getItems();
            		LinkedList<FSM<? extends Transition>> tsList = new LinkedList<FSM<? extends Transition>>();
            		for(String fsmName : fsmNames) {
            			TransitionSystem<? extends Transition> fsm = model.getTS(fsmName);
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
			if (selectedFSMBox.getItems().size() < 2) {
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
	 * @return - Returns an HBox object containing the name TextField and its Label.
	 */

	private HBox makeFSMNameField() {
		Label fsmNameLabel = new Label("New FSM Name:");
		fsmNameField = new TextField();
		return new HBox(fsmNameLabel, fsmNameField);
	} // makeFSMNameField()

//---  Getter Methods   -----------------------------------------------------------------------
	
	/**
	 * Getter method that TODO:
	 * 
	 * @param listView - ListView<<r>String> object that TODO:
	 */
	
	private void addDragHandlers(ListView<String> listView) {
		listView.setOnDragOver(e -> {
			Dragboard db = e.getDragboard();
			if(db.hasString()) e.acceptTransferModes(TransferMode.MOVE);
			e.consume();
		});
		
		listView.setOnDragDropped(e -> {
			ObservableList<String> items = listView.getItems();
			Dragboard db = e.getDragboard();
			
			boolean success = false;
			if(db.hasString()) {
				items.add(db.getString());
				success = true;
			}
			
			e.setDropCompleted(success);
			e.consume();
		});
	}
	
	/**
	 * Getter method that requests the user's FSM selection that they specify in the dialog and returns it.
	 * 
	 * @return - Returns a LinkedList Collection of FSM objects.
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
	 * Getter method that requests the id of the FSM to create.
	 * 
	 * @return - Returns a String object representing the id for the FSM to create
	 */

	public String getId() {
		return id;
	}

//---  Support Classes   ----------------------------------------------------------------------
	
	/**
	 * This class is for each cell containing an FSM name in the fsm name lists. This enables
	 * the contents to be copied elsewhere for drag-and-drop operations.
	 * 
	 * This class is a part of the fsmtaui.popups package.
	 * 
	 * @author Mac Clevinger and Graeme Zinck
	 */
	
	public class FSMNameCell extends ListCell<String> {
		
	//-- Constructors  ----------------------------------
		
		/**
		 * Creates a new FSMNameCell with all the event handlers
		 * for drag-and-drop interactions.
		 */
		
		public FSMNameCell() {
			this.setOnDragDetected(e -> {
				if(getItem() == null) return;
				Dragboard db = startDragAndDrop(TransferMode.MOVE);
				ClipboardContent content = new ClipboardContent();
				content.putString(getItem());
				db.setContent(content);
				e.consume();
			});
			
			this.setOnDragOver(e -> {
				if(e.getGestureSource() != this && e.getDragboard().hasString())
					e.acceptTransferModes(TransferMode.MOVE);
				e.consume();
			});
			
			this.setOnDragDropped(e -> {
				ObservableList<String> items = getListView().getItems();
				Dragboard db = e.getDragboard();
				
				if(getItem() == null) {
					return;
				} else {
					boolean success = false;
					if(db.hasString()) {
						items.add(db.getString());
						success = true;
					}
					
					e.setDropCompleted(success);
					e.consume();
				}
			});
			
			this.setOnDragDone(e -> {
				if (e.getTransferMode() == TransferMode.MOVE) {
					ObservableList<String> items = getListView().getItems();
					items.remove(getItem());
				}
			});
		}

	//-- Operations  ------------------------------------
		
		@Override
		protected void updateItem(String text, boolean empty) {
	         super.updateItem(text, empty);
	         setText(text);
		}
	}
}
