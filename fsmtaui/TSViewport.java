package fsmtaui;

import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.ScrollPane;
import support.transition.Transition;
import javafx.geometry.Bounds;
import fsm.TransitionSystem;
import javafx.scene.image.*;
import graphviz.FSMToDot;
import javafx.scene.*;
import java.io.File;

/**
 * TSViewport is a Parent javafx node which holds a TransitionSystem and its image representation
 * inside of it. It also keeps track of the image files produced through GraphViz.
 * 
 * This class is a part of the fsmtaui package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public class TSViewport extends Parent {
	
//---  Constants   ----------------------------------------------------------------------------
	
	/** Default width of image. */
	private static int WIDTH = 600;
	/** Default height of image. */
	private static int HEIGHT = 500;
	/** double constant value representing the rate at which zooming occurs. */
	private static double ZOOM_SPEED = 1.4;
	
//---  Instance Variables   -------------------------------------------------------------------
	
	/** Model containing all the important information to display in the GUI. */
	private Model model;
	/** TransitionSystem object which is being displayed in the viewport. */
	private TransitionSystem<? extends Transition> ts;
	/** ScrollPane object which allows the user to view the GraphViz image of the TransitionSystem. */
	private ScrollPane scrollPane;
	/** ImageView object which shows the GraphViz image of the TransitionSystem. */
	private ImageView imageView;
	/** Image object which contains the GraphViz image of the TransitionSystem. */
	private Image image;
	/** Ratio for the zoom of the image. */
	private double ratio;
	
//---  Constructors   -------------------------------------------------------------------------
	
	/**
	 * Creates an FSMViewport to represent a given FSM. It will cause the creation
	 * of a JPG image in the working directory in order to visualize the FSM
	 * in the viewport.
	 * 
	 * @param inTS - TransitionSystem to be displayed.
	 * @param inModel - Model with all the important information to
	 * display in the GUI.
	 */
	
	public TSViewport(TransitionSystem<? extends Transition> inTS, Model inModel) {
		ts = inTS;
		model = inModel;
		
		// Create the image representation
		String imageName = model.getWorkingDirectoryString() + "/" + ts.getId();
		FSMToDot.createImgFromFSM(ts, imageName, model.getWorkingDirectoryString(), model.getGraphVizConfigPath());
		
		// Show the image in the ImageView
		image = new Image("file:" + imageName + ".jpg");
		imageView = new ImageView(image);
		imageView.setPreserveRatio(true);
		
		// Delete the image
		File imageFile = new File(imageName + ".jpg");
		imageFile.delete();
		
		scrollPane = new ScrollPane(imageView);
		scrollPane.setPannable(true);
		scrollPane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scrollPane.setPrefSize(WIDTH, HEIGHT);
		
		// Sets the initial zoom ratio for the image.
		ratio = 1.0;
		
		getChildren().setAll(scrollPane);
	} // FSMViewport(FSM, Model)
	
//---  Operations   ---------------------------------------------------------------------------
		
	/**
	 * Recomputes the image based on the TransitionSystem.
	 */

	public void refreshImage() {
		// Create the image representation
		String imageName = model.getWorkingDirectoryString() + "/" + ts.getId();
		FSMToDot.createImgFromFSM(ts, imageName, model.getWorkingDirectoryString(), model.getGraphVizConfigPath());
		// Show the image in the ImageView
		image = new Image("file:" + imageName + ".jpg");
		imageView.setImage(image);
	} // refreshImage()
		
	/**
	 * Zooms into the image shown using the ratio.
	 */

	public void zoomIn() {
		Bounds viewportSize = scrollPane.getViewportBounds();
		Bounds contentSize = imageView.getBoundsInParent();
		double centerX = (contentSize.getWidth() - viewportSize.getWidth()) * scrollPane.getHvalue() + viewportSize.getWidth() / 2.0;
		double centerY = (contentSize.getHeight() - viewportSize.getHeight()) * scrollPane.getVvalue() + viewportSize.getHeight() / 2.0;
			
		// Zoom in the image
		ratio *= ZOOM_SPEED;
		imageView.setFitWidth(ratio * image.getWidth());
		imageView.setFitHeight(ratio * image.getHeight());
		// Recenter
		double newCenterX = centerX * ZOOM_SPEED;
		double newCenterY = centerY * ZOOM_SPEED;
		scrollPane.setVvalue((newCenterY - viewportSize.getHeight()/2.0) / (contentSize.getHeight() * ZOOM_SPEED - viewportSize.getHeight()));
		scrollPane.setHvalue((newCenterX - viewportSize.getWidth()/2.0) / (contentSize.getWidth() * ZOOM_SPEED - viewportSize.getWidth()));
	} // zoomImage(double)
		
	/**
	 * Zooms out of the image shown using the ratio.
	 */

	public void zoomOut() {
		Bounds viewportSize = scrollPane.getViewportBounds();
		Bounds contentSize = imageView.getBoundsInParent();
		double centerX = (contentSize.getWidth() - viewportSize.getWidth()) * scrollPane.getHvalue() + viewportSize.getWidth() / 2.0;
		double centerY = (contentSize.getHeight() - viewportSize.getHeight()) * scrollPane.getVvalue() + viewportSize.getHeight() / 2.0;
			
		// Zoom out from the image
		ratio /= ZOOM_SPEED;
		imageView.setFitWidth(ratio * image.getWidth());
		imageView.setFitHeight(ratio * image.getHeight());
		// Recenter
		double newCenterX = centerX * ZOOM_SPEED;
		double newCenterY = centerY * ZOOM_SPEED;
		scrollPane.setVvalue((newCenterY - viewportSize.getHeight()/2.0) / (contentSize.getHeight() * ZOOM_SPEED - viewportSize.getHeight()));
		scrollPane.setHvalue((newCenterX - viewportSize.getWidth()/2.0) / (contentSize.getWidth() * ZOOM_SPEED - viewportSize.getWidth()));
	} // zoomImage(double)
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	/**
	 * Gets the id of the FSM being represented.
	 * 
	 * @return - String representing the FSM's id.
	 */

	public String getFSMId() {
		return ts.getId();
	} // getFSMId()
	
	/**
	 * Gets the TransitionSystem being represented.
	 * 
	 * @return - TransitionSystem which is being represented.
	 */

	public TransitionSystem<? extends Transition> getFSM() {
		return ts;
	} // getFSM()

} // class FSMViewport
