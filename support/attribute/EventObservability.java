package support.attribute;

/**
 * This interface defines what methods should be implemented in any class in the Event
 * family that possess the attributes of an Observable Event. 
 * 
 * It is used for ensuring the implementation of certain features in other classes.
 * 
 * This interface is a part of the support.attribute package
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public interface EventObservability {
	
//---  Setter Methods   -----------------------------------------------------------------------

	/**
	 * Setter method to assign a new status to the Event implementing this interface's being Observable to the system.
	 * 
	 * @param newObserv - boolean value representing the new status of the Event implementing this interface's being Observable to the system
	 */
	
	public abstract void setEventObservability(boolean newObserv);
	
	/**
	 * Setter method to assign a new status to the Event implementing this interface's being Observable to the attacker.
	 * 
	 * @param newObserv - boolean value representing the new status of the Event implementing this interface's being Observable to the attacker
	 */
	
	public abstract void setEventAttackerObservability(boolean newObserv);
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	/**
	 * Getter method to request the status of the Event implementing this interface's being Observable to the system.
	 * 
	 * @return - Returns a boolean value representing the status of the Event implementing this interface's being Observable to the system.
	 */
	
	public abstract boolean getEventObservability();
	
	/**
	 * Getter method to request the status of the Event implementing this interface's being Observable to the attacker.
	 * 
	 * @return - Returns a boolean value representing the status of the Event implementing this interface's being Observable to the attacker.
	 */
	
	public abstract boolean getEventAttackerObservability();
	
}
