package support.transition;

import support.transition.NonDetTransition;
import support.attribute.ObsTransInterface;
import support.State;

/**
 * This class models a path that connects a State to another State in an FSM, storing an Event
 * and the States that it leads to. This is the Non-Deterministic and Observable variation of
 * a Transition, storing numerous States and information about the Observability of the associated
 * Event.
 * 
 * This class is a part of the support.transition package
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public class NonDetObsTransition extends NonDetTransition implements ObsTransInterface{

//---  Instance Variables   -------------------------------------------------------------------
	
	/** boolean instance variable representing the status of this Transition Object's Event's Observability (True: Can be seen, False: Can not)*/
	private boolean observability;
	
//---  Constructors   -------------------------------------------------------------------------
	
	/**
	 * 
	 * 
	 * @param event
	 * @param states
	 */
	
	public NonDetObsTransition(String event, State ...states) {
		super(event, states);
		observability = true;
	}
	
	/**
	 * 
	 * @param obs
	 * @param event
	 * @param states
	 */
	
	public NonDetObsTransition(boolean obs, String event, State ... states) {
		super(event, states);
		observability = obs;
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	/**
	 * 
	 */

	@Override
	public boolean getObervability() {
		return observability;
	}
	
//---  Setter Methods   -----------------------------------------------------------------------

	/**
	 * 
	 */
	
	@Override
	public void setObservability(boolean obs) {
		observability = obs;
		
	}
	
	
	
}
