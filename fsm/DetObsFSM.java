package fsm;

import fsm.attribute.Observability;
import java.util.*;
import support.event.ObservableEvent;
import support.transition.Transition;
import java.io.*;

/**
 * This class models a Deterministic Observable FSM that expands upon the Deterministic FSM class to
 * implement the Observable characteristics of an FSM - Events being capable of being UnObservable.
 * 
 * This class is a part of the fsm package
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public class DetObsFSM extends DetFSM implements Observability<Transition, ObservableEvent>{
	
//--- Constant Values  -------------------------------------------------------------------------

	/** String constant designating this object as a specific type of FSM for clarification purposes*/
	public static final String FSM_TYPE = "DetObs FSM";
	
//---  Constructors  --------------------------------------------------------------------------
	
	/**
	 * 
	 * 
	 * @param in
	 * @param id
	 */
	
	public DetObsFSM(File in, String id) {
		super();
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	@Override
	public FSM<Transition, ObservableEvent> createObserverView() {
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
