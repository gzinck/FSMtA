package fsm;

import fsm.attribute.Observability;
import java.util.*;
import support.event.ObservableEvent;

/**
 * This class models a Deterministic Observable FSM that expands upon the Deterministic FSM class to
 * implement the Observable characteristics of an FSM - Events being capable of being UnObservable.
 * 
 * This class is a part of the fsm package
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public class DetObsFSM extends DetFSM implements Observability{
	
//--- Constant Values  -------------------------------------------------------------------------

	/** String constant designating this object as a specific type of FSM for clarification purposes*/
	public static final String FSM_TYPE = "DetObs FSM";
		
//--- Instance Variables  ----------------------------------------------------------------------
		
	/** ArrayList<ObservableEvent> object */
	protected ArrayList<ObservableEvent> observableEvents;
	
//---  Constructors  --------------------------------------------------------------------------
	
}
