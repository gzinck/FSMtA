package fsmtaui.popups;

import javafx.scene.control.Alert;

/**
 * Alerts is an easy static class that creates an alert.
 * It holds possible messages that can be used for the alert.
 * 
 * @author Mac Clevinger and Graeme Zinck
 *
 */
public class Alerts {
	//--------------------------------------------------------------------------------
	// For the ModifyFSMPane
	//--------------------------------------------------------------------------------
	public static final String[] ERROR_NO_FSM = {"No FSM Open", "Please open an FSM first."};
	public static final String[] ERROR_NO_FSM_SELECTED = {"No FSM Selected", "Please select an FSM in the left sidebar first."};
	
	// Errors for adding events
	public static final String[] ERROR_ADD_EVENT_NO_STATES = {"Add Event Error", "To add an event, you must define the initial state and final state (from and to)."};
	public static final String[] ERROR_ADD_EVENT_NO_NAME = {"Add Event Error", "To add an event, you must define the event's name."};
	public static final String[] ERROR_ADD_EVENT_NO_FSM = {"Add Event Error", "To add an event, you must have an FSM open and active in the right pane."};
	
	// Errors for removing events
	public static final String[] ERROR_REMOVE_EVENT_NO_STATES = {"Remove Event Error", "To remove an event, you must define the initial state and final state (from and to)."};
	public static final String[] ERROR_REMOVE_EVENT_NO_NAME = {"Remove Event Error", "To remove an event, you must define the event's name."};
	public static final String[] ERROR_REMOVE_EVENT_NO_FSM = {"Remove Event Error", "To remove an event, you must have an FSM open and active in the right pane."};
	
	// Errors for editing events
	public static final String[] ERROR_EDIT_EVENT_NO_NAME = {"Edit Event Error", "To edit an event, you must specify a pre-existing event name."};
	public static final String[] ERROR_EDIT_EVENT_NO_FSM = {"Edit Event Error", "To edit an event, you must have an FSM open and active in the right pane."};
	
	// Errors for removing states
	public static final String[] ERROR_REMOVE_STATE_NO_NAME = {"Remove State Error", "To remove a state, you must define the state's name."};
	public static final String[] ERROR_REMOVE_STATE_NO_FSM = {"Remove State Error", "To remove a state, you must have an FSM open and active in the right pane."};
	
	// Errors for adding states
	public static final String[] ERROR_ADD_STATE_NO_NAME = {"Add State Error", "To add a state, you must define the state's name."};
	public static final String[] ERROR_ADD_STATE_NO_FSM = {"Add State Error", "To add a state, you must have an FSM open and active in the right pane."};
	
	// Errors for toggling initial states
	public static final String[] ERROR_TOGGLE_INITIAL_NO_NAME = {"Toggle Initial Error", "To toggle a state's initial property, you must define the state's name."};
	public static final String[] ERROR_TOGGLE_INITIAL_NO_FSM = {"Toggle Initial Error", "To toggle a state's initial property, you must have an FSM open and active in the right pane."};
	
	// Errors for toggling marked states
	public static final String[] ERROR_TOGGLE_MARKED_NO_NAME = {"Toggle Marked Error", "To toggle a state's marked property, you must define the state's name."};
	public static final String[] ERROR_TOGGLE_MARKED_NO_FSM = {"Toggle Marked Error", "To toggle a state's marked property, you must have an FSM open and active in the right pane."};
	
	//--------------------------------------------------------------------------------
	// For the FSMOperationsPane
	//--------------------------------------------------------------------------------
	public static final String[] ERROR_OPERATION_NO_NAME = {"New FSM Naming Error", "Please give a unique name to the FSM that results from the operation."};
	public static final String[] ERROR_OPERATION_NO_FSM = {"No FSM Selected Error", "Please select the FSM(s) you wish to use before performing an operation."};
	public static final String[] ERROR_OPERATION_NO_OP = {"No Operation Selected Error", "No operation selected. Please select an operation to perform on the two FSMs."};
	
	public static final String[] ERROR_DETERMINIZE_ALREADY_DONE = {"Determinization Error", "The selected FSM was already deterministic."};
	public static final String[] ERROR_ALREADY_OBSERVABLE = {"Observer View Error", "The selected FSM was already in observer view—there were no unobservable events."};
	
	public static final String[] ERROR_MULTI_OPERATION_NO_FSM = {"Multi-FSM Operation Error", "To perform an operation on two FSMs, you must select two FSMs."};
	
	//--------------------------------------------------------------------------------
	// For the FileSettingsPane
	//--------------------------------------------------------------------------------
	public static final String[] ERROR_FILE_FORMAT = {"File Format Error", "The file you entered was not formatted correctly."};
	public static final String[] ERROR_FILE_NOT_FOUND = {"File Not Found", "The file path you entered could not be found. Please try another path."};
	public static final String[] ERROR_NO_FSM_TYPE = {"No FSM Type", "No FSM type was selected. Please select a type and try again."};
	public static final String[] ERROR_ILLEGAL_FSM_PARAMETERS = {"Illegal FSM Parameters", "All parameters for generating an FSM must be integers."};
	
	/**
	 * Makes an error dialog with a message and header.
	 * 
	 * @param message Array with 2 elements: the header text for
	 * the error message, and the error message. This should be chosen
	 * from the constants in this class, but could be from anywhere.
	 */
	public static void makeError(String[] message) {
		Alert error = new Alert(Alert.AlertType.ERROR, message[1]);
		error.setHeaderText(message[0]);
		error.showAndWait();
	} // makeError(String[] message)
	
	
	/**
	 * Makes an information dialog box with a message and header.
	 * 
	 * @param header The title for the information box. 
	 * @param message The message for the user in the box.
	 */
	public static void makeInfoBox(String header, String message) {
		Alert box = new Alert(Alert.AlertType.INFORMATION, message);
		box.setHeaderText(header);
		box.showAndWait();
	} // makeInfoBox(String, String)
} // Alerts
