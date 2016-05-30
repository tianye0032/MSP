package MSP.server.central.config.parity;

import MSP.file.mapping.parity.compute.BasicMatrix;
import MSP.server.central.Configure;

public class ParityConfig {

	private final double precision = 0.001;
	
	public boolean isValid(double[][] coeff) {
		BasicMatrix basic = new BasicMatrix();
		coeff = basic.mutiply(coeff, Configure.getIntValue(Configure.EXTEND));
		double det = basic.detValue(coeff);
		System.out.println(det);
		if (basic.detValue(coeff) < precision) {
			System.out.println("The det value of this set of coefficient is not appropriate.");
			return false;
		}
		return true; 
	}
	
	
	public static void main(String[] args){
		ParityConfig parity = new ParityConfig();
		
		double[][] coeff = Configure.getCoefficient();
		if (parity.isValid(coeff)) {
			System.out.println("valid");
		} else {
			System.out.println("not valid");
		}
	}
}
