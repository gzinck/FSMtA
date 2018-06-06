package support.attribute;

/**
 * This interface defines what methods should be implemented in any class in the Event
 * family that possess the attributes of an Observable Event. 
 * 
 * This interface is a part of the support.attribute package
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public interface EventObservability {
	
//---  Setter Methods   -----------------------------------------------------------------------

	/**
	 * Setter method to assign a new status to the Event implementing this interface's being Observable
	 * 
	 * @param newObserv - boolean value representing the new status of the Event implementing this interface's being Observable
	 */
	
	public abstract void setEventObservability(boolean newObserv);
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	/**
	 * Getter method to request the status of the Event implementing this interface's being Observable
	 * 
	 * @return - Returns a boolean value representing the status of the Event implementing this interface's being Observable
	 */
	
	public abstract boolean getEventObservability();
	
}
