/**
 * Class Report
 * 
 * This class holds attributes that represent the
 * performance of a bundling algorithm. This includes
 * energy, latency, total cost and grant count. It also 
 * holds the default cost attribute that is associated 
 * with the default algorithm.
 * 
 * @author Ali Sehati
 * @version 1.0
 * 
 */
public class Report {

	private double  energy = 0.0;
	private double latency = 0.0;
	private double totalCost = 0.0;
	private double defaultCost = 0.0;
	private int grantCount = 0;
	
	/**
	 * Default Constructor
	 * 
	 * Creates a report object with all the attributes
	 * set to 0.
	 */
	public Report() {
	}
	
	/**
	 * Constructor
	 * 
	 * Creates a report that is initialized with the given values for
	 * different attributes of the Report class
	 * 
	 * @param energy energy cost of the algorithm
	 * @param latency latency cost of the algorithm
	 * @param totalCost total cost of the algorithm
	 * @param defaultCost total cost of the default algorithm
	 * @param grantCount number of grants made by the algorithm
	 */
	public Report( double energy, double latency, double totalCost, double defaultCost, int grantCount) {
		this.energy = energy;
		this.latency = latency;
		this.totalCost = totalCost;
		this.grantCount = grantCount;
		this.defaultCost = defaultCost;
	}
	
	/**
	 * Returns a string representation of the report
	 */
	public String toString(){
		String output = "";
		
		output += "Grant Count is: " + grantCount;
		output += "\nEnergy cost is: " + energy;
		output += "\nLatency cost is: " + latency;
		output += "\nTotal cost is: " + totalCost;
		output += "\nDefault Cost is: " + defaultCost;
		
		return output;		
	}
	
	/**
	 * Getter for grantCount
	 * 
	 * @return the number of grants
	 */
	public double getGrantCount(){
		return grantCount;
	}
	
	/**
	 * Getter for defaultCost
	 * 
	 * @return The cost of the default algorithm
	 */
	public double getDefaultCost(){
		return defaultCost;
	}
	
	/**
	 * Getter for totalCost
	 * 
	 * @return total cost of the algorithm
	 */
	public double getTotalCost(){
		return totalCost; 
	}
	
	/**
	 * Getter for latency
	 * 
	 * @return The latency cost of the algorithm
	 */
	public double getLatency(){
		return latency;
	}
	
	/**
	 * Getter for energy
	 * 
	 * @return The energy cost of the algorithm
	 */
	public double getEnergy(){
		return energy;
	}
	
}
