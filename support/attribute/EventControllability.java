package support.attribute;

/**
 * This interface defines what methods should be implemented in any class in the Event
 * family that possess the attributes of a Controllable Event. 
 * 
 * It is used for ensuring the implementation of certain features in other classes.
 * 
 * This interface is a part of the support.attribute package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public interface EventControllability {

//---  Setter Methods   -----------------------------------------------------------------------
	
	/**
	 * Setter method to assign the provided boolean value as the new status of the Event
	 * implementing this interface's being Controllable 
	 * 
	 * @param newControl - boolean value provided as the new status of the Event implementing this interface's being Controllable
	 */
	
	public abstract void setEventControllability(boolean newControl);

//---  Getter Methods   -----------------------------------------------------------------------
	
	/**
	 * Getter method to request the current status of the Event implementing this interface's
	 * being Controllable.
	 * 
	 * @return - Returns a boolean value representing the status of the Event implementing this interface's being Controllable
	 */
	
	public abstract boolean getEventControllability();
	
}
