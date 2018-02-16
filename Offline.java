import java.util.ArrayList;

/**
 * Class Offline
 * 
 * This class is responsible for calculating the optimal  
 * offline solution. It is a dynamic programming-based 
 * solution with the runtime of O(n^2) where n is the  
 * number of arrivals.
 * 
 * @author Ali Sehati
 * @version 1.0
 *
 */
public class Offline {
	
	// parameters of the bundling problem
	private double alpha;
	private int T;
	
	//array representing inter-arrival times
	private int[] IAT;
	
	//array representing actual arrival times
	private int[] arrival;
	
	//performance metrics characterizing the optimal result
	private double latency;
	private double totalCost;
	private int grantCount;

	/**
	 * Constructor
	 * 
	 * @param T tail time of the radio
	 * @param IAT Array representing inter-arrival times
	 */
	public Offline(int T, int[] IAT)
	{
		this.T = T;
		this.IAT = IAT;
		arrival = new int[IAT.length + 1];
	}
	
	/**
	 * Initialize all performance metrics to 0
	 */
	public void initialize()
	{
		totalCost = 0;
		latency = 0;
		grantCount = 0;
	}
	
	/**
	 * Sets the alpha value of the problem
	 * 
	 * @param alpha Weight associated with delay
	 */
	public void setAlpha(double alpha)
	{
		this.alpha = alpha;
	}
	
	/**
	 * Main body of the dynamic-programming solution.
	 * Computes the values of optimal latency, total cost
	 * and grant count.
	 */
	public void run()
	{
		double C = 0.0;
		double D_temp = 0.0;				

		double[] Cost = new double[arrival.length];
		double[] D = new double[arrival.length];
		ArrayList<Boolean>[] Seq = new ArrayList[arrival.length];
		
		for (int i = 0; i < Seq.length; i++)
			Seq[i] = new ArrayList<Boolean>();
				
		arrival[0] = 0; 
		arrival[1] = IAT[0];
		
		for (int i = 2; i < arrival.length; i++)
		{
			arrival[i] = IAT[i - 1] + arrival[i - 1];
		}
		
		Cost[0] = 0; Cost[1] = 0;		
		D[0] = 0; D[1] = 0;
		Seq[1].add(true);
		
		
		for (int i = 2; i < arrival.length; i++)
		{
			Cost[i] = accumulated_delay(1,i);
			D[i] = Cost[i];
			
			for (int k = 1; k < i; k++)
				Seq[i].add(false);
			
			Seq[i].add(true);
			
			for (int j = 1; j < i; j++)
			{
				C = Cost[i - j] + Math.min(arrival[i] - arrival[i - j], T);
				D_temp = accumulated_delay(i - j + 1, i);
				
				C += D_temp;
				D_temp += D[i - j];
				
				if(C < Cost[i]){
					Cost[i] = C;
					D[i] = D_temp;
					
					Seq[i].clear();
					Seq[i].addAll(Seq[i - j]);
					
					for (int k = 1; k < j; k++)
						Seq[i].add(false);
					
					Seq[i].add(true);
				}					
			}
		}
		
		int lastIndex =  arrival.length - 1;
		
		totalCost = Cost[lastIndex] + T;
		latency = D[lastIndex];
		
		for(int i = 0;i < Seq[lastIndex].size(); i++)
		{
			if(Seq[lastIndex].get(i)){
				grantCount++;				
			}
		}
		
	}	
	
	/**
	 * Calculates the weighted delay cost in case requests 
	 * from startIndex up to endIndex are bundled together  
	 * and granted at time endIndex.
	 * 
	 * @param startIndex start position of a bundle in the arrival array
	 * @param endIndex end position of a bundle in the arrival array
	 * @return the value of the weighted delay cost incurred due to bundling
	 */
	public double accumulated_delay(int startIndex, int endIndex)
	{
		double delay = 0;
		
		for (int k = startIndex; k <= endIndex; k++)
		{
			delay += (arrival[endIndex] - arrival[k]);
		}
		
		delay = alpha * delay;
		return delay;
	}
	
	/**
	 * Creates a report object representing performance
	 * metrics of the optimal solution
	 * 
	 * @return A report object representing optimal solution
	 */
	public Report getReport(){
		
		System.out.println("Opt#: " + grantCount);
		System.out.println("Opt Cost: " + totalCost + "\tOpt Delay(alpha_D): " + latency);
		
		return new Report(totalCost - latency, latency, totalCost, 0, grantCount);
	}
}
