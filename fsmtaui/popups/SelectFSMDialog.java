package fsmtaui.popups;

import java.util.*;

import fsm.FSM;
import fsm.TransitionSystem;
import fsmtaui.Model;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import support.transition.Transition;

/**
 * Allows the program to create a dialog box where the user can select various open TransitionSystems.
 *  
 * @author Mac Clevinger and Graeme Zinck
 *
 */

public class SelectFSMDialog {
	Dialog<LinkedList<FSM<? extends Transition>>> dialog;
	ListView<String> openFSMBox;
	ListView<String> selectedFSMBox;
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
	 * Gets the user's FSM selection that they specify in the dialog and returns it.
	 * 
	 * @return Collection of FSM objects to perform the operation.
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
	 * @return String representing the id for the FSM to create
	 */
	public String getId() {
		return id;
	}
	
	public class FSMNameCell extends ListCell<String> {
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
		@Override
		protected void updateItem(String text, boolean empty) {
	         super.updateItem(text, empty);
	         setText(text);
		}
	}
}
