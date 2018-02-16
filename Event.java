/**
 * Class Event 
 * 
 * A generic Event which could occur during a discrete event simulation
 *
 * @author Ali Sehati
 * @version 1.0
 */
public abstract class Event implements Comparable {

	private int eventTime;	// The time when this event will occur

	/**
	 * Generate a new Event at the specified time.
	 * 
	 * @param time The time of this event
	 */
	public Event( int time ) {
		this.eventTime = time;
	}

	/**
	 * Returns the time this event will occur at.
	 *
	 * @return	The time when this event will occur.
	 */
	public int getTime() {
		return this.eventTime;
	}

	/**
	 * We wish to store events in a priority queue, which means they must be sorted.
	 * 
	 *
	 * @param other	The Event object to compare against.
	 * @return 1 if this object should follow other, -1 if it should precede other, and 0 otherwise.
	 * @throws ClassCastException	If other is not an Event
	 */
	public int compareTo( Object other ) {

		if ( other instanceof Event ) {

			int timeComparison = Integer.compare( this.eventTime, ((Event)other).getTime() );
			
			// If these events occur at the same time, then ensure
			// that departures (Grant) occur before anything else, 
			// and end events only happen once all other events at 
			// the same time have been processed.
			if ( timeComparison == 0 ){
				if ( other instanceof Grant ){
					return 1;
				} else if ( this instanceof Grant ){
					return -1;
				} else if ( other instanceof End ){
					return -1;
				} else if ( this instanceof End ){
					return 1;
				}
			}
			
			// In general, order events by their time of occurrence.
			return timeComparison;
		}

		throw new ClassCastException();
	}

}
