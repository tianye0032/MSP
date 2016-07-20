package MSP.server.central.config.parity;

import java.util.ArrayList;
import java.util.List;

import MSP.file.mapping.parity.compute.BasicMatrix;
import MSP.file.mapping.parity.compute.Server;
import MSP.server.central.Configure;

public class ParityConfig {

	private final double precision = 0.001;	
	
//	private Configure config = new Configure("data/conf/center.conf");
	private static int extend;
	private static int numData;
	private static int numPari;
	
	
	//2 data servers, while 2 - 6 parity servers
	private static double[][] coe2 = new double[][]{{0.31, 0.69}, {0.21, 0.79}, 
			{0.65, 0.35}, {0.87, 0.13}, {0.75, 0.25}, {0.56, 0.44}};
	
	//3 data servers, while 2 - 6 parity servers
	private static double[][] coe3 = new double[][]{{0.18, 0.32, 0.5}, {0.2, 0.3, 0.5}, 
			{0.5, 0.4, 0.1}, {0.35, 0.45, 0.2}, {0.12, 0.20, 0.68}, {0.14, 0.40, 0.46}};
	
	//4 data servers, while 2 - 6 parity servers
	private static double[][] coe4 = new double[][]{{0.18, 0.32, 0.25, 0.25}, {0.2, 0.10, 0.62, 0.08}, 
			{0.4, 0.11, 0.37, 0.12}, {0.35, 0.12, 0.13, 0.4}, {0.22, 0.44, 0.16, 0.18}, 
			{0.48, 0.19, 0.25, 0.08}};
	
	
	public boolean isValid(double[][] coeff) {
		
		double[][] equation = replenishEquation(coeff, numData, numPari);
		int[] nums = new int[numData + numPari];
		for (int i = 0; i < numData + numPari; i++) {
			nums[i] = i;
		}
		List<List<Integer>> subset = subsets(nums);
		
		for (int i = 0; i < subset.size(); i++) {
			boolean testRes = testValid(subset.get(i), equation);
			if (!testRes) {
				return false;
			}
		}		
		
		return true; 
	}
	
	private boolean testValid(List<Integer> list, double[][] equation) {
		BasicMatrix basic = new BasicMatrix();
//		equation = basic.mutiply(equation, config.getIntValue(Configure.EXTEND));
		equation = basic.mutiply(equation, extend);
		double[][] matrix = new double[numData][numData];
		for (int i = 0; i < list.size(); i++) {
			matrix[i] = equation[list.get(i)];
		}
		
		double det = basic.detValue(matrix);
		System.out.println("the det is : " + det);
		if (Math.abs(det) < precision) {
			System.out.println("The det value of this list of coefficient is not appropriate.");
			for (int i = 0; i < list.size(); i++) {
				System.out.println(list.get(i));
			}
			return false;
		}
		return true; 
	}

	private double[][] replenishEquation(double[][] coeff, int numData,
			int numPari) {
		double[][] res = new double[numPari + numData][numData];
		for (int i = 0; i < numData; i++) {
			res[i][i] = 1.0;
		}
		for (int i = numData; i < numPari + numData; i++) {
			for (int j = 0; j < numData; j++) {
				res[i][j] = coeff[i - numData][j];
			}
		}
		
		return res;
	}

	public List<List<Integer>> subsets(int[] nums) {
//		Arrays.sort(nums, new CompareServ());
		
	    List<List<Integer>> res = new ArrayList<List<Integer>>();
	    List<Integer> each = new ArrayList<Integer>();
	    helper(res, each, 0, nums);
	    return res;
	}
	
	
	private void helper(List<List<Integer>> res, List<Integer> each, int pos, int[] n) {
	    if (each.size() == numData) {
	        res.add(each);
	    }
	    int i = pos;
	    while (i < n.length) {
	        each.add(n[i]);
	        helper(res, new ArrayList<Integer>(each), i + 1, n);
	        each.remove(each.size() - 1);
	        i++;
//	        while (i < n.length && n[i] == n[i - 1]) {i++;}
	    }
	    return;
	}
	
	
	public static void main(String[] args){
		ParityConfig parity = new ParityConfig();
		numData = 4;
		numPari = 6;
		extend = 100;
//		double[][] coeff = coe2;
//		double[][] coeff = coe3;
		double[][] coeff = coe4;
		if (parity.isValid(coeff)) {
			System.out.println("valid");
		} else {
			System.out.println("not valid");
		}
	}
}
