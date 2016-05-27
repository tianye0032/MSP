package MSP.file.mapping.parity.compute;

public class Server {

	private double[] coefficient;
	
	private long parityData;
	
	
	public Server () {
		
	}

	public Server(double[] coefficient) {
		super();
		this.coefficient = coefficient;
	}

	public double[] getCoefficient() {
		return coefficient;
	}

	public void setCoefficient(double[] coefficient) {
		this.coefficient = coefficient;
	}

	public long getParityData() {
		return parityData;
	}

	public void setParityData(long parityData) {
		this.parityData = parityData;
	}
	
	
}
