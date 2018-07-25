package fsmtaui.settingspane.modifyfsmpane;

import fsm.TransitionSystem;
import fsmtaui.Model;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import support.Event;

/**
 * WORK IN PROGRESS. This holds SingleEventOptions for modifying the observability and controllability
 * properties of events in the current FSM.
 * Problems: when you add an event, it does NOT add it to this list. We need to manually do so in the 
 * other parts of the UI by updating the observablelist in the model.
 * @author graemezinck
 *
 */
public class ModifyTSEventPropertiesPane2 extends VBox {
	
//---  Instance Variables   -------------------------------------------------------------------

	/** Model object instance variable containing all the important information to display in the GUI. */
	private Model model;
	
	private ListView<SingleEventOptions> eventList;
	
	public ModifyTSEventPropertiesPane2(Model inModel) {
		model = inModel;
		this.getStyleClass().add("modify-fsm-subpane");
		
		Label titleLabel = new Label("Modify Event Properties");
		titleLabel.getStyleClass().add("subpane-section-title");
		
		eventList = new ListView<SingleEventOptions>();
		
		getChildren().addAll(titleLabel, eventList);
		updateEventList();
	}
	
	private void updateEventList() {
		model.getCurrTSEvents().addListener(new ListChangeListener<Event>() {
			@Override
			public void onChanged(Change<? extends Event> c) {
				if(c.wasRemoved()) for(Event removeEvent : c.getRemoved()) {
					removeEvent(removeEvent);
				}
				if(c.wasAdded()) for(Event addEvent : c.getAddedSubList()) {
					eventList.getItems().add(new SingleEventOptions(addEvent));
				}
			}
		});
	}
	
	private void removeEvent(Event eventToRemove) {
		ObservableList<SingleEventOptions> list = eventList.getItems();
		for(int i = 0; i < list.size(); i++) {
			if(list.get(i).getEvent().equals(eventToRemove)) {
				list.remove(i);
				break;
			}
		}
	}
}
