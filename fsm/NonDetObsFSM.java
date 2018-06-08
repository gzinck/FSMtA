package fsm;

import fsm.attribute.Observability;
import support.State;
import support.transition.NonDetTransition;
import support.event.ObservableEvent;
import java.io.*;

/**
 * This class models an Observable NonDeterministic FSM that expands upon the NonDetFSM class to
 * implement the Observable characteristics of an FSM - Events storing information about their
 * Observability, and some operations being made available to interact with such information.
 * 
 * This class is a part of the fsm packge.
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public class NonDetObsFSM extends NonDetFSM implements Observability<State, NonDetTransition, ObservableEvent>{
	
//--- Constant Values  -------------------------------------------------------------------------

	/** String constant designating this object as a specific type of FSM for clarification purposes*/
	public static final String FSM_TYPE = "NonDetObs FSM";

//---  Constructors  --------------------------------------------------------------------------

	/**
	 * Constructor for a NonDetObsFSM object that takes in a file and String id as input, processing
	 * the file to fill the object's contents and titling it as the defined String input.
	 * 
	 * @param in - File object provided to be read to generate the NonDetObsFSM object.
	 * @param id - String object provided to be the name of the generated NonDetObsFSM object.
	 */
	
	public NonDetObsFSM(File in, String id) {
		super();
	}
		
//---  Operations   ---------------------------------------------------------------------------
		
	@Override
	public FSM<State, NonDetTransition, ObservableEvent> createObserverView() {
		// TODO Auto-generated method stub
		return null;
	}
	
//---  Getter Methods   -----------------------------------------------------------------------

	@Override
	public Boolean getEventObservability(ObservableEvent event) {
		// TODO Auto-generated method stub
		return null;
	}
	
//---  Setter Methods   -----------------------------------------------------------------------

	@Override
	public void setEventObservability(ObservableEvent event, boolean status) {
		// TODO Auto-generated method stub
		
	}	
	
}
