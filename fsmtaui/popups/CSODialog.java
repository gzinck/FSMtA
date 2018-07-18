package fsmtaui.popups;

import fsm.*;
import fsm.attribute.*;

import java.util.ArrayList;
import java.util.Iterator;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import support.State;
import support.transition.Transition;

public class CSODialog {
	/** String for the prompt title. */
	private static final String PROMPT_TITLE_STR = "Check Current State Opacity";
	/** String for the prompt header. */
	private static final String PROMPT_HEADER_STR = "FSM Must Be Determinized";
	/** String for the prompt text. */
	private static final String PROMPT_TEXT_STR = "To perform this operation correctly, the FSM must first be determinized. Would you like to determinize the current FSM?";
	
	/** String for the results title. */
	private static final String RESULTS_TITLE_STR = "Current State Opacity Results";
	/** String for the opaque results header. */
	private static final String RESULTS_OPAQUE_HEADER_STR = "The FSM is Opaque";
	/** String for the opaque results header. */
	private static final String RESULTS_OPAQUE_TEXT_STR = "Current-state opacity held for the current FSM.";
	/** String for the non-opaque results header. */
	private static final String RESULTS_NON_OPAQUE_HEADER_STR = "The FSM is Not Opaque";
	
	/**
	 * Allows a user to check if the input FSM is current-state opaque. It requires that the input FSM
	 * is already determinized, and it asks the user if they would like to determinize it first.
	 * 
	 * @param fsmToUse FSM object for checking current state opacity.
	 * @return Determinized version of the FSM, if the user asked to determinize the old FSM.
	 */
	public static FSM<? extends Transition> testCSO(FSM<?> fsmToUse) {
		FSM<? extends Transition> newFSM = null;
		
		if(fsmToUse == null)
			Alerts.makeError(Alerts.ERROR_NO_FSM);
		else {
			Alert promptAlert = new Alert(AlertType.INFORMATION);
			promptAlert.setTitle(PROMPT_TITLE_STR);
			promptAlert.setHeaderText(PROMPT_HEADER_STR);
			promptAlert.setContentText(PROMPT_TEXT_STR);
			promptAlert.getButtonTypes().clear();
			promptAlert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
			ButtonType result = promptAlert.showAndWait().get();
			
			if(result != ButtonType.CANCEL) {
				if(result == ButtonType.YES) {
					String newId = fsmToUse.getId() + "-determinized";
					fsmToUse = (FSM<?>)((Observability<?>)fsmToUse).createObserverView();
					fsmToUse.setId(newId);
					newFSM = fsmToUse;
				}
				ArrayList<State> states = null;
				if(fsmToUse instanceof OpacityTest) {
					states = ((OpacityTest)fsmToUse).testCurrentStateOpacity();
				}
				if(states == null)
					System.err.println("Error: when testing for current state opacity, null was returned. There should have been at least an empty arraylist of states.");
				else if(states.size() > 0) {
					StringBuilder stateNames = new StringBuilder();
					Iterator<State> itr = states.iterator();
					while(itr.hasNext()) {
						stateNames.append(itr.next().getStateName());
						if(itr.hasNext()) stateNames.append(System.getProperty("line.separator"));
					} // Go through all the states
					
					// Alert that there are bad states
					Alert resultsAlert = new Alert(AlertType.INFORMATION);
					resultsAlert.setTitle(RESULTS_TITLE_STR);
					resultsAlert.setHeaderText(RESULTS_NON_OPAQUE_HEADER_STR);
					resultsAlert.setContentText(stateNames.toString());
					resultsAlert.getButtonTypes().clear();
					resultsAlert.getButtonTypes().addAll(ButtonType.OK);
					resultsAlert.showAndWait();
				} else {
					// Alert that there are no bad states
					Alert goodResultsAlert = new Alert(AlertType.INFORMATION);
					goodResultsAlert.setTitle(RESULTS_TITLE_STR);
					goodResultsAlert.setHeaderText(RESULTS_OPAQUE_HEADER_STR);
					goodResultsAlert.setContentText(RESULTS_OPAQUE_TEXT_STR);
					goodResultsAlert.getButtonTypes().clear();
					goodResultsAlert.getButtonTypes().addAll(ButtonType.OK);
					goodResultsAlert.showAndWait();
				}
			} // if the user did not cancel
		} // if the current FSM is not null
		return newFSM; // Return determinized FSM, which will be added to the GUI.
	} // CSODialog(Model)
}
