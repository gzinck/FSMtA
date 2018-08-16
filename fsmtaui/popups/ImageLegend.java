package fsmtaui.popups;

import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.scene.*;

/**
 * ImageLegend is an object which contains a whole new window that is always in front with all the
 * explanations of what the different colours and symbols mean in an FSM image.
 * 
 * This class is a part of the fsmtaui.popups package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public class ImageLegend {
	
//---  Instance Variables   -------------------------------------------------------------------
	
	/** GridPane with all the information of the legend contained within. */
	private HBox root;
	/** GridPane with the legend for what states mean. */
	private GridPane stateLegend;
	/** GridPane with the legend for what transitions mean. */
	private GridPane transitionLegend;
	/** Stage of the window. */
	private Stage stage;
	/** Scene to set on the stage of the window. */
	private Scene scene;
	
//---  Constructors   -------------------------------------------------------------------------
	
	/**
	 * Constructor for an ImageLegend object that creates a new window with the image's legend.
	 */

	public ImageLegend() {
		root = new HBox();
		
		makeStateLegend();
		makeTransitionLegend();
		root.getChildren().addAll(stateLegend, transitionLegend);
		
		scene = new Scene(root);
		scene.getStylesheets().add(this.getClass().getResource("/css/image-legend-styles.css").toExternalForm());
		
		// Create the window
        stage = new Stage();
        stage.setTitle("FSM Image Legend");
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.show();
	}

//---  Manipulations   ------------------------------------------------------------------------
	
	/**
	 * This method TODO:
	 */
	
	private void makeStateLegend() {
		stateLegend = new GridPane();
		stateLegend.setId("state-legend");
		
		// Default state
		Region defaultState = new Region();
		defaultState.setId("default-state");
		Label defaultStateLabel = new Label("Default State");
		stateLegend.addRow(0, defaultState, defaultStateLabel);
		
		// Secret state
		Region secretState = new Region();
		secretState.setId("secret-state");
		Label secretStateLabel = new Label("Secret State");
		stateLegend.addRow(1, secretState, secretStateLabel);
		
		// Bad state
		Region badState = new Region();
		badState.setId("bad-state");
		Label badStateLabel = new Label("Bad State");
		stateLegend.addRow(2, badState, badStateLabel);
		
		// Secret and Bad state
		Region secretBadState = new Region();
		secretBadState.setId("secret-bad-state");
		Label secretBadStateLabel = new Label("Secret and Bad State");
		stateLegend.addRow(3, secretBadState, secretBadStateLabel);
		
		// Marked state
		Region markedState = new Region();
		markedState.setId("marked-state");
		Label markedStateLabel = new Label("Marked State");
		stateLegend.addRow(4, markedState, markedStateLabel);
	}

	/**
	 * This method TODO:
	 */
	
	private void makeTransitionLegend() {
		transitionLegend = new GridPane();
		transitionLegend.setId("transition-legend");
		
		// Default transition
		Region defaultTransition = new Region();
		defaultTransition.setId("default-transition");
		Label defaultTransitionLabel = new Label("Default Transition");
		transitionLegend.addRow(0, defaultTransition, defaultTransitionLabel);
		
		// Unobservable transition
		Region unobservableTransition = new Region();
		unobservableTransition.setId("unobservable-transition");
		Label unobservableTransitionLabel = new Label("Unobservable Transition");
		transitionLegend.addRow(1, unobservableTransition, unobservableTransitionLabel);
		
		// Uncontrollable transition
		Region uncontrollableTransition = new Region();
		uncontrollableTransition.setId("uncontrollable-transition");
		Label uncontrollableTransitionLabel = new Label("Uncontrollable Transition");
		transitionLegend.addRow(2, uncontrollableTransition, uncontrollableTransitionLabel);
		
		// Unobservable and Uncontrollable transition
		Region unobservableUncontrollableTransition = new Region();
		unobservableUncontrollableTransition.setId("unobservable-uncontrollable-transition");
		Label unobservableUncontrollableTransitionLabel = new Label("Unobservable and Uncontrollable Transition");
		transitionLegend.addRow(3, unobservableUncontrollableTransition, unobservableUncontrollableTransitionLabel);
	}
	
}
