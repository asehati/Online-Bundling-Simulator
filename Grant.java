/**
 * Class grant
 * 
 * Event representing the departure (making a grant)
 * in simulator.
 * 
 * @author Ali Sehati
 * @version 1.0
 */
public class Grant extends Event {

	/**
	 * Create a departure (grant) event at the specified time.
	 *
	 * @param time	The time of the arrival event.
	 */
	public Grant( int time ) {
		super( time );
	}

}
