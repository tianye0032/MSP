package MSP.server.central.config.parity;

import MSP.file.mapping.parity.compute.BasicMatrix;
import MSP.server.central.Configure;

public class ParityConfig {

	private final double precision = 0.001;
	
	public boolean isValid(double[][] coeff) {
		BasicMatrix basic = new BasicMatrix();
		coeff = basic.mutiply(coeff, Configure.getIntValue(Configure.EXTEND));
		if (basic.detValue(coeff) < precision) {
			System.out.println("The det value of this set of coefficient is not appropriate.");
			return false;
		}
		return true; 
	}
}
