package fsmtaui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;

/**
 * ContentPane holds all the openFSMTabs in a TabPane and handles
 * key events associated with its content.
 * 
 * This is part of the fsmtaui package, which holds all the UI elements.
 * 
 * @author Mac Clevinger and Graeme Zinck
 *
 */
class ContentPane extends VBox {
	/** Model containing all the important information to display in the GUI. */
	Model model;
	/** Button to zoom in. */
	Button zoomInBtn;
	/** Button to zoom out. */
	Button zoomOutBtn;
	/**
	 * Creates a ContentPane to hold the tabs.
	 * 
	 * @param inModel - Model with all the important information to
	 * display in the GUI.
	 */
	ContentPane(Model inModel) {
		model = inModel;
		HBox zoomBtns = makeZoomBtns();
		
		getChildren().addAll(model.getOpenFSMTabs(), zoomBtns);
		
		makeZoomKeyboardEventHandler();
		makeZoomBtnEventHandler();
	} // ContentPane()
	
	/**
	 * Makes buttons to zoom in and out of the image.
	 * 
	 * @return - HBox containing zoom buttons.
	 */
	HBox makeZoomBtns() {
		zoomInBtn = new Button("Zoom In");
		zoomOutBtn = new Button("Zoom Out");
		return new HBox(zoomInBtn, zoomOutBtn);
	} // makeZoomBtns()
	
	/**
	 * Handles zooming into and out of FSMs via keyboard presses.
	 */
	void makeZoomKeyboardEventHandler() {
		setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent e) {
				switch(e.getCode()) {
				case PLUS:
				case EQUALS:
					model.getCurrViewport().zoomIn();
					break;
				case MINUS:
					model.getCurrViewport().zoomOut();
					break;
				default:
					break;
				} // switch/case
			} // handle(KeyEvent)
		}); // setOnKeyPressed(EventHandler<KeyEvent>)
	} // makeZoomInEventHandler()
	
	/**
	 * Handles zooming into and out of FSMs via buttons on screen.
	 */
	void makeZoomBtnEventHandler() {
		zoomInBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				e.consume();
				model.getCurrViewport().zoomIn();
			} // handle(ActionEvent)
		}); // setOnAction(EventHandler<ActionEvent>)
		
		zoomOutBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				e.consume();
				model.getCurrViewport().zoomOut();
			} // handle(ActionEvent)
		}); // setOnAction(EventHandler<ActionEvent>)
	}
} // class ContentPane
