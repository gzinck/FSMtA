package fsmtaui;

import fsmtaui.popups.ImageLegend;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.geometry.*;

/**
 * ContentPane holds all the openFSMTabs in a TabPane and handles
 * key events associated with its content.
 * 
 * This is part of the fsmtaui package, which holds all the UI elements.
 * 
 * @author Mac Clevinger and Graeme Zinck
 *
 */
class ContentPane extends BorderPane {
	/** Model containing all the important information to display in the GUI. */
	Model model;
	/** Button to zoom in. */
	Button zoomInBtn;
	/** Button to zoom out. */
	Button zoomOutBtn;
	/** Button to get help and see the legend for the image. */
	Button helpBtn;
	
	/**
	 * Creates a ContentPane to hold the tabs.
	 * 
	 * @param inModel - Model with all the important information to
	 * display in the GUI.
	 */
	ContentPane(Model inModel) {
		model = inModel;
		HBox zoomBtns = makeBtns();
		
		setCenter(model.getOpenFSMTabs());
		BorderPane.setAlignment(zoomBtns, Pos.BOTTOM_RIGHT);
		setBottom(zoomBtns);
		
		makeZoomKeyboardEventHandler();
		makeZoomBtnEventHandler();
		makeHelpBtnEventHandler();
	} // ContentPane()
	
	/**
	 * Makes buttons to zoom in and out of the image and to get the legend popup.
	 * 
	 * @return - HBox containing zoom buttons.
	 */
	HBox makeBtns() {
		HBox btnBox = new HBox();
		btnBox.setAlignment(Pos.CENTER_RIGHT);
		zoomInBtn = new Button();
		zoomInBtn.getStyleClass().add("img-btn");
		zoomInBtn.setId("zoom-in-btn");
		zoomOutBtn = new Button();
		zoomOutBtn.getStyleClass().add("img-btn");
		zoomOutBtn.setId("zoom-out-btn");
		helpBtn = new Button();
		helpBtn.getStyleClass().add("img-btn");
		helpBtn.setId("help-btn");
		btnBox.getChildren().addAll(zoomInBtn, zoomOutBtn, helpBtn);
		return btnBox;
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
		zoomInBtn.setOnAction(e -> {
			model.getCurrViewport().zoomIn();
		}); // setOnAction
		
		zoomOutBtn.setOnAction(e -> {
			model.getCurrViewport().zoomOut();
		}); // setOnAction
	}
	
	void makeHelpBtnEventHandler() {
		helpBtn.setOnAction(e -> {
			new ImageLegend();
		}); // setOnAction
	}
} // class ContentPane
