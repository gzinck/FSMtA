package fsmtaui;

import java.io.File;

import fsm.FSM;
import graphviz.FSMToDot;
import javafx.geometry.Bounds;
import javafx.scene.*;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.*;
import javafx.scene.layout.StackPane;

/**
 * FSMViewport is a Parent javafx node which holds an FSM and its image representation
 * inside of it. It also keeps track of the image files produced through GraphViz.
 * 
 * @author Mac Clevinger and Graeme Zinck
 *
 */
public class FSMViewport extends Parent {
	/** Default width of image. */
	private static int WIDTH = 600;
	/** Default height of image. */
	private static int HEIGHT = 500;
	private static double ZOOM_SPEED = 1.4;
	/** Model containing all the important information to display in the GUI. */
	private Model model;
	/** DeterministicFSM object which is being displayed in the viewport. */
	private FSM fsm;
	/** ScrollPane object which allows the user to view the GraphViz image of the fsm. */
	private ScrollPane scrollPane;
	/** ImageView object which shows the GraphViz image of the fsm. */
	private ImageView imageView;
	/** Image object which contains the GraphViz image of the fsm. */
	private Image image;
	/** Ratio for the zoom of the image. */
	private double ratio;
	
	/**
	 * Creates an FSMViewport to represent a given FSM. It will cause the creation
	 * of a JPG image in the working directory in order to visualize the FSM
	 * in the viewport.
	 * 
	 * @param inFSM - FSM to be displayed.
	 * @param inModel - Model with all the important information to
	 * display in the GUI.
	 */
	public FSMViewport(FSM inFSM, Model inModel) {
		fsm = inFSM;
		model = inModel;
		
		// Create the image representation
		String imageName = model.getWorkingDirectoryString() + "/" + fsm.getId();
		FSMToDot.createImgFromFSM(fsm, imageName, model.getWorkingDirectoryString(), model.getGraphVizConfigPath());
		
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
	
	/**
	 * Gets the id of the FSM being represented.
	 * 
	 * @return - String representing the FSM's id.
	 */
	public String getFSMId() {
		return fsm.getId();
	} // getFSMId()
	
	/**
	 * Gets the FSM being represented.
	 * 
	 * @return - DeterministicFSM which is being represented.
	 */
	public FSM getFSM() {
		return fsm;
	} // getFSM()
	
	/**
	 * Recomputes the image based on the FSM.
	 */
	public void refreshImage() {
		// Create the image representation
		String imageName = model.getWorkingDirectoryString() + "/" + fsm.getId();
		FSMToDot.createImgFromFSM(fsm, imageName, model.getWorkingDirectoryString(), model.getGraphVizConfigPath());
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
} // class FSMViewport
