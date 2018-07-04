package fsm.attribute;

import java.util.*;
import support.State;

/**
 * This interface defines the capabilities of the FSM in regards to testing the
 * FSM for its status as Opaque in varying cases.
 * 
 * This interface is a part of the fsm.attribute package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public interface OpacityTest <S extends State>{

//---  Operations   ---------------------------------------------------------------------------
	
	/**
	 * This method tests the FSM implementing this interface for Current-State Opacity by
	 * searching for any explicitly labeled Secret States in the FSM (according to an interpretation
	 * of the FSM's structure post-determinization/creating the Observer View.)
	 * 
	 * The result of the test is given as a list of States that are found to violate Current-State
	 * Opacity, denoting a 'success' if the list is empty.
	 * 
	 * @return - Returns an ArrayList<<r>S> object containing any States that were found to be Private; if none, successful test. 
	 */
	
	public abstract ArrayList<S> testCurrentStateOpacity();
	
}
