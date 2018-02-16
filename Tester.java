import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;


/**
 * A simple test driver for Simulator
 * 
 * @author Ali Sehati 
 * @version	1.0
 *
 */

public class Tester {				
	
	public static void main(String[] args) {
		int T = 200;
		
		RandomIAT ri = new RandomIAT();		
		int[] IAT = ri.generateNormalIAT(100, T, 200, 80);
		
		try {
			PrintWriter writer = new PrintWriter("log.txt", "UTF-8");

			double[] alpha = new double[]{0.0001, 0.001, 0.01, 0.1, 1, 10, 100, 1000, 10000, 100000};

			Simulator sim_online = new Simulator(T, IAT);
			Offline sim_offline = new Offline(T, IAT);

			for (int i = 0; i < alpha.length; i++){
				sim_online.setAlpha(alpha[i]);
				sim_online.setupLogFiles();
				sim_online.initialize();
				sim_online.run();
				Report goa_report  = sim_online.getReport();

				sim_offline.setAlpha(alpha[i]);
				sim_offline.run();				
				Report opt_report = sim_offline.getReport();
				
				double CR = goa_report.getTotalCost()/opt_report.getTotalCost();
				
				System.out.println("alpha: " + alpha[i]+ "\tCR is: " + CR + "\tGrant#: " + goa_report.getGrantCount());
				writer.println(alpha[i] + "\t" + CR + "\t" + opt_report.getTotalCost()  
						+ "\t" + goa_report.getLatency() + "\t" + goa_report.getEnergy() 
						+ "\t" + goa_report.getTotalCost() + "\t" +  goa_report.getGrantCount()
						+ "\t" + goa_report.getDefaultCost());
			}

			sim_online.closeLogFiles();
			writer.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	

}
