package fsmtaui.settingspane.modifyfsmpane;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import support.Event;

/**
 * WORK IN PROGRESS. This holds all the options for a single event, visible in
 * ModifyTSEventPropertiesPane2. It is not used in the UI currently.
 * @author graemezinck
 *
 */
public class SingleEventOptions extends BorderPane {
	
	private Event thisEvent;
	private Label eventLabel;
	private HBox checkBoxes;
	private CheckBox controllable;
	private CheckBox observable;
	
	public SingleEventOptions(Event e) {
		thisEvent = e;
		eventLabel = new Label(e.getEventName());
		setLeft(eventLabel);
		
		controllable = new CheckBox();
		controllable.setSelected(e.getEventControllability());
		observable = new CheckBox();
		observable.setSelected(e.getEventObservability());
		checkBoxes = new HBox(controllable, observable);
		setRight(checkBoxes);
		
		makeEventHandlers();
	}
	
	public void update() {
		controllable.setSelected(thisEvent.getEventControllability());
		observable.setSelected(thisEvent.getEventObservability());
	}
	
	public Event getEvent() {
		return thisEvent;
	}
	
	private void makeEventHandlers() {
		controllable.setOnAction(e -> {
			thisEvent.setEventControllability(controllable.isSelected());
		});
		
		observable.setOnAction(e -> {
			thisEvent.setEventObservability(observable.isSelected());
		});
	}
}
