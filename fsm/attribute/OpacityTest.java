package fsm.attribute;

import support.State;
import java.util.*;

/**
 * This interface defines the capabilities of the FSM in regards to testing the
 * FSM for its status as Opaque in varying cases.
 * 
 * It is used for ensuring the implementation of certain features in other classes.
 * 
 * This interface is a part of the fsm.attribute package.
 * 
 * @author Mac Clevinger and Graeme Zinck
 */

public interface OpacityTest {

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
	
	public abstract ArrayList<State> testCurrentStateOpacity();
	
}
