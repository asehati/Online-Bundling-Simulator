import java.util.ArrayList;
import java.util.Random;

/**
 * Class RandomIAT
 * 
 * Responsible for generating random sequences
 * of inter-arrival times.
 * 
 * @author Ali Sehati
 * @version 1.0
 *
 */
public class RandomIAT {

	public static final int SEED = 111;
	public static final int INITIAL_SIZE =  1000;
	public static final int MAX_BATCH_SIZE = 14;
	public static final int MEAN_SHORT_GAP_TIME = 40;
	public static final int MEAN_LONG_GAP_TIME = 400;
	public static final int BURSTY_SEQUENCE_LENGTH = 500;
	
	private Random random;
	
	// variables related to bursty arrival pattern
	private int currentBatchSize;
	private int currentInterBatch;
	private int currentBatchUnitGap;
	private int[] batchSize;
	private double[] longGap;
	private double[] shortGap;
	
	/**
	 * Default constructor
	 */
	public RandomIAT(){
		this.random = new Random(SEED);
		this.longGap = this.random.doubles((long)INITIAL_SIZE).toArray();
		this.shortGap = this.random.doubles((long)INITIAL_SIZE).toArray();
		this.batchSize = this.random.ints((long)INITIAL_SIZE, 1, MAX_BATCH_SIZE + 1).toArray();
	}
	
	/**
	 * Generates next random number for a long gap
	 * chosen from an exponential distribution
	 * 
	 * @return Next random number for long interval
	 */
	private int nextLongGap()
	{
		int gap = (int)((-MEAN_LONG_GAP_TIME) * Math.log(longGap[currentInterBatch++]));
		return gap;
	}
	
	/**
	 * Generates next random number for a short gap
	 * chosen from an exponential distribution
	 * 
	 * @return Next random number for short interval
	 */
	private int nextShortGap()
	{
		int gap = (int)((-MEAN_SHORT_GAP_TIME) * Math.log(shortGap[currentBatchUnitGap++]));
		return gap;
	}
	
	/**
	 * Generates next random number for burst size
	 * chosen from a uniform distribution.
	 * 
	 * @return Next random number for burst size
	 */
	private int nextBatchSize()
	{
		int size = batchSize[currentBatchSize++];
		return size;
	}
	
	/**
	 * generates a bursty arrival sequence. It follows 
	 * a pattern where a burst is followed with a short 
	 * gap, which is followed by a single arrival and  
	 * then a long gap. Then the pattern continues.
	 * Short and long gaps are exponentially distributed.
	 * Burst size is uniformly distributed.
	 * 
	 * @return An array representing a bursty arrival sequence
	 */
	public int[] generateBurstyIAT()
	{				
		int burstSize = 0;
		int count = 0;		
		
		ArrayList<Integer> IAT = new ArrayList<Integer>();
		
		while (count <= BURSTY_SEQUENCE_LENGTH)
		{					
			IAT.add(this.nextLongGap());			
			
			burstSize = this.nextBatchSize();
			
			for (int i = 0; i < burstSize; i++)
			{			
				IAT.add(1);			
			}
			
			IAT.add(this.nextShortGap());			

			//2 = 1 for shortGap + 1 for longGap
			count += (burstSize + 2);
		}
		
		int[] times = new int[IAT.size()];
		
		for(int i = 0;i < IAT.size(); i++)
		{
			times[i] = IAT.get(i);
		}
		
		return times;
	}
	
	/**
	 * Generates an array of length itemLen where all
	 * the elements are equal to each other and have 
	 * the value of interval
	 * 
	 * @param itemLen size of the sequence
	 * @param interval Value of each interval
	 * @return
	 */
	public int[] generateConstantIAT(int itemLen, int interval)
	{
		int[] IAT = new int[itemLen];

		for(int i = 0; i < itemLen; i++)
			IAT[i] = interval;

		return IAT;
	}
	
	/**
	 * Generates a random sequence where numbers are 
	 * sampled from a log-normal distribution with mean 
	 * mean and standard deviation std.
	 * 
	 * @param itemLen Size of the generated sequence
	 * @param T Radio's tail time
	 * @param mean Mean parameter of the distribution
	 * @param std Standard deviation parameter of the distribution
	 * @return An array containing log-normal inter-arrival times
	 */
	public int[] generateLogNormalIAT(int itemLen, int T, double mean, double std)
	{		
		double nextRan = 0.0;
		int lessCounter = 0;
		int highCounter = 0;
		int zeroCounter = 0;
		int[] IAT = new int[itemLen];

		double varx = std * std;
		double m2 = mean * mean;

		double mu = Math.log((m2)/(Math.sqrt(varx + m2)));
		double sigma = Math.sqrt(Math.log(1 + (varx/m2)));

		for (int i = 0; i < itemLen; i++){
			nextRan  = Math.exp(mu + (random.nextGaussian() * sigma));						

			IAT[i] = (int)nextRan;

			if(IAT[i] <= 0)
				zeroCounter++;

			if(IAT[i] <= T)
				lessCounter++;
			else
				highCounter++;
		}

		System.out.println("\nZero: " + zeroCounter + "\tless: " + lessCounter + "\thigh: " + highCounter);

		return IAT;
	}

	/**
	 * Generates a sequence of normal inter-arrival times
	 * where the values are sampled from a normal distribution
	 * with mean mu and standard deviation sigma
	 * 
	 * @param itemLen Size of the sequence
	 * @param T Tail time
	 * @param mu Mean value of the normal distribution
	 * @param sigma Standard deviation of the normal distribution
	 * @return Array containing normal inter-arrival times
	 */
	public int[] generateNormalIAT(int itemLen, int T, double mu, double sigma)
	{
		double nextRan = 0.0;
		int lessCounter = 0;
		int highCounter = 0;
		int zeroCounter = 0;
		int[] IAT = new int[itemLen];

		for (int i = 0; i < itemLen; i++){
			nextRan  = Math.abs(mu + (random.nextGaussian() * sigma));						

			IAT[i] = (int)nextRan;

			if(IAT[i] <= 0)
				zeroCounter++;

			if(IAT[i] <= T)
				lessCounter++;
			else
				highCounter++;

			System.out.printf("%d\t",IAT[i]);
		}

		System.out.println("\nZero: " + zeroCounter + "\tless: " + lessCounter + "\thigh: " + highCounter);

		return IAT;

	}

}
