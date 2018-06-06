package fsm;
import fsm.attribute.Observability;
import support.State;
import java.util.*;
import support.event.ObservableEvent;

public class DetObsFSM extends DetFSM implements Observability{
	
//--- Constant Values  -------------------------------------------------------------------------

	/** String constant designating this object as a specific type of FSM for clarification purposes*/
	public static final String FSM_TYPE = "DetObs FSM";
		
//--- Instance Variables  ----------------------------------------------------------------------
		
	/** State object with the initial state for the deterministic FSM. */
	protected ArrayList<ObservableEvent> observableEvents;
	
//---  Constructors  --------------------------------------------------------------------------
	
}
