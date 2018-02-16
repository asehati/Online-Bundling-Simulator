import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Queue;

/**
 * Class Simulator
 * 
 * A discrete-event simulator that simulates the
 * behavior of the break-even bundling algorithm.
 * A grant is made when energy cost and weighted 
 * delay cost of that grant become equal.
 * 
 * Energy is captured using On/Off model. Delay 
 * is defined using the cumulative delay function, 
 * where we consider the summation of delays of 
 * all the requests.
 * 
 * @author Ali Sehati
 * @version 1.0
 */

public class Simulator {
	public static final boolean DEBUG = false;

	private double alpha;
	private int T;
	private int[] IAT;

	private int clock;
	private int lastArrival;
	private int lastGrant;	
	private int bufferCount;
	private int grantCount;
	private int current_IAT_Index;

	private double accumulatedDelay; // in each aggregation cycle

	private boolean isRunning ;

	private ArrayList<Integer> bufferedArrivals;	

	Queue<Event> eventList;

	//handles for logging files
	private PrintWriter arrivalWriter;
	private PrintWriter grantWriter;
	private PrintWriter delayWriter;

	//desire performance metrics
	private double energy;
	private double latency;
	private double totalCost;
	private double defaultCost;

	/**
	 * Constructor
	 * 
	 * Initializes tail time and inter-arrival
	 * time sequence. Then it creates the priority
	 * list used for scheduling events. It also
	 * creates files used for logging purposes.
	 * 
	 * @param T Radio tail time
	 * @param IAT Sequence of inter-arrival times
	 */
	public Simulator(int T, int[] IAT){
		this.T = T;
		this.IAT = IAT;
		eventList = new java.util.PriorityQueue<Event>();
		bufferedArrivals = new ArrayList<Integer>();
		
		createLogFiles();
	}
	
	/**
	 * Creates the files used for logging purposes.
	 * These files hold arrival times, delay of individual
	 * requests and also inter-grant times.
	 * 
	 */
	public void createLogFiles()
	{
		String arrivalFileName = "log_arrival" + ".txt";
		String delayFileName = "log_delay" + ".txt";
		String grantFileName = "log_grant" + ".txt";

		try {
			arrivalWriter = new PrintWriter(arrivalFileName, "UTF-8");
			delayWriter = new PrintWriter(delayFileName, "UTF-8");
			if(DEBUG)
			{
				grantWriter = new PrintWriter(grantFileName, "UTF-8");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// Arrival times are independent of alpha values
		// So we write in arrival log file only once
		// in the constructor.
		for(int i = 0; i < IAT.length; i++)
			arrivalWriter.println(IAT[i]);
	}

	/**
	 * Initializer
	 * 
	 * Sets most class attributes to 0.
	 * CLears previous contents of the 
	 * event list. Adds the first arrival
	 * to the priority queue.
	 * 
	 */
	public void initialize()
	{		
		this.clock = 0;
		this.lastArrival = 0;
		this.lastGrant = 0;
		this.bufferCount = 0;
		this.grantCount = 0;
		this.current_IAT_Index = 0;
		this.accumulatedDelay = 0;

		this.energy = 0;
		this.latency = 0;
		this.totalCost = 0;
		this.defaultCost = 0;

		this.isRunning = true;

		eventList.clear();
		eventList.add( new Arrival(IAT[0]));
	}

	/**
	 * Setter for alpha parameter
	 * 
	 * @param alpha Weight given to delay
	 */
	public void setAlpha(double alpha)
	{
		this.alpha = alpha;			
	}
	
	/**
	 * Starts a new section in log files for delay and
	 * grant. This new section is marked by the value 
	 * of the associated alpha for that experiment.
	 * 
	 */
	public void setupLogFiles()
	{	
		delayWriter.println("\n=========================================");
		delayWriter.println("Alpha = " + alpha);

		if(DEBUG){
			grantWriter.println("\n=========================================");
			grantWriter.println("Alpha = " + alpha);
		}
	}


	/**
	 * Main running loop of the simulator
	 */
	public void run() {

		while(isRunning){
			Event currentEvent = eventList.remove();

			clock = currentEvent.getTime();

			// Process the next event.
			this.handle( currentEvent );
		}
	}

	/**
	 * General handle method that calls specific
	 * handle methods depending on the type of the
	 * event object passed as argument.
	 * 
	 * @param event The event popped from priority queue
	 */
	private void handle( Event event ) {		

		if ( event instanceof Arrival ) {
			this.handle( (Arrival) event );
		} else if ( event instanceof End ) {
			this.handle( (End) event );
		} else if ( event instanceof Grant ) {
			this.handle( (Grant) event );
		} else {
			throw new IllegalArgumentException( "Event type not recognized: " + event );
		}
	}

	/**
	 * Handle for request arrival event.
	 * Schedules the next grant event based on the online
	 * policy of break-even algorithm. It also schedules
	 * the next arrival event using the next element from 
	 * IAT. It also updates variables related to performance
	 * metrics.
	 * 
	 * @param event Event representing a request arrival
	 */
	private void handle( Arrival event ) {
		double t_1 = 0.0, t_2 = 0.0, nextIAT = 0.0;
		
		if(lastArrival > 0)
			defaultCost += Math.min(clock - lastArrival, T);

		lastArrival = clock;
		bufferedArrivals.add(clock);				

		accumulatedDelay += (bufferCount * IAT[current_IAT_Index]);
		
		bufferCount++;
		current_IAT_Index++;

		if(current_IAT_Index == IAT.length)
			nextIAT = Double.MAX_VALUE;
		else
			nextIAT = IAT[current_IAT_Index];

		t_1 =  (T - (alpha * accumulatedDelay))/(alpha * bufferCount);

		if(alpha >= 1)
			eventList.add( new Grant( clock ) );

		else if ( ((clock - lastGrant) >= T) || (lastGrant == 0))
		{					
			if ((t_1 > 0) && (t_1 < nextIAT)){
				eventList.add( new Grant( clock + (int)t_1 ) );
			}
			else if (t_1 <= 0){
				eventList.add( new Grant( clock ) );
			}
		}
		else
		{
			t_2 = (clock - lastGrant - (alpha * accumulatedDelay)) / ((alpha * bufferCount) - 1);

			if (((int)t_2) == 0){
				eventList.add(new Grant(clock));
			}
			else if ((t_2 > 0) && (clock + t_2 - lastGrant < T) && (t_2 < nextIAT)){
				eventList.add( new Grant( clock + (int)t_2 ) );
			}
			else if ((t_1 > 0) && (clock + t_1 - lastGrant >= T) && (t_1 < nextIAT)){
				eventList.add( new Grant( clock + (int)t_1 ) );
			}
		}

		//schedule the next arrival
		if(current_IAT_Index < IAT.length)
			eventList.add( new Arrival( clock + IAT[current_IAT_Index] ) );
	}

	/**
	 * Handle for grant event.
	 * Updates simulation variables related to performance
	 * metrics. In case no more arrival is left from IAT array,
	 * it schedules the end event. 
	 * 
	 * @param event Event representing granting the current request bundle
	 */
	private void handle( Grant event ) {
		if (lastGrant > 0)
			energy += Math.min(clock - lastGrant, T);

		latency += alpha * (accumulatedDelay + (bufferCount * (clock - lastArrival)));

		if(DEBUG){
			grantWriter.printf("%d\t", clock - lastGrant);
		}

		lastGrant = clock;
		grantCount++;
		accumulatedDelay = 0;
		bufferCount = 0;

		if(current_IAT_Index == IAT.length)
			eventList.add( new End( clock ) );

		for(int i = 0; i < bufferedArrivals.size(); i++){			
			delayWriter.printf("%d\t", clock - bufferedArrivals.get(i));
		}

		bufferedArrivals.clear();
	}

	/**
	 * Handle for End event
	 * Updates simulation variables and finalizes
	 * some logging tasks.
	 * 
	 * @param event Event representing the end of simulation
	 */
	private void handle( End event ) {

		this.isRunning = false;
		energy += T; // Add the tail energy after last grant
		defaultCost += T;

		bufferedArrivals.clear();

		if(DEBUG){
			System.out.println("End happened at " + clock);
			grantWriter.close();
		}		
	}

	/**
	 * Close previously opened log files.
	 */
	public void closeLogFiles()
	{
		arrivalWriter.close();
		delayWriter.close();
	}

	/**
	 * Creates a report object of the performance 
	 * metrics of the algorithm's run
	 * 
	 * @return A report object representing performance of the algorithm
	 */
	public Report getReport(){
		totalCost = energy + latency;

		if(DEBUG){
			System.out.println("\n==============================================");
			System.out.println("Total Arrivals: " + IAT.length);
			System.out.println("Grant Count is: " + grantCount);
			System.out.println("Energy cost is: " + energy);
			System.out.println("Latency cost is: " + latency);
			System.out.println("Total cost is: " + totalCost);
			System.out.println("Default Cost is: " + defaultCost);
			System.out.println("Average Delay: " + latency/(alpha * IAT.length));
		}

		return new Report(energy, latency, totalCost, defaultCost, grantCount);
	}
}

