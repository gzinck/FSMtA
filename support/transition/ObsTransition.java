package support.transition;

import support.transition.Transition;
import support.attribute.ObsTransInterface;
import support.State;

/**
 * 
 * 
 * This class is a part of the support.attribute package
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public class ObsTransition extends Transition implements ObsTransInterface{

	/** */
	private boolean observability;
	
	/**
	 * 
	 * @param event
	 * @param state
	 */
	
	public ObsTransition(String event, State state) {
		super(event, state);
		observability = false;
	}
	
	/**
	 * 
	 * @param event
	 * @param state
	 * @param obs
	 */
	
	public ObsTransition(boolean obs, String event, State state) {
		super(event, state);
		observability = obs;
	}
	
	/**
	 * 
	 * @return - Returns
	 */
	
	public boolean getObervability() {
		return observability;		
	}
	
	/**
	 * 
	 * @param obs - 
	 */

	public void setObservability(boolean obs) {
		observability = obs;
	}

}
