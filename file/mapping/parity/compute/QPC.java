package MSP.file.mapping.parity.compute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import MSP.server.central.Configure;

public class QPC {
	private double[][] coefficient;		
	private int row;
	private int column;
	
	private int extend;   //the multiple to extend the coefficient
	
	private BasicMatrix basic;
	
	private Server[] servers;
	
	/**
	 * extend the value of coefficient to be an integer
	 * @param coefficient
	 */
	public QPC(double[][] coefficient) {
		this.extend = Configure.getIntValue(Configure.EXTEND);
		this.basic = new BasicMatrix();
		this.coefficient = basic.mutiply(coefficient, extend);
		this.row = coefficient.length;
		this.column = coefficient[0].length;		
		
		init();		
	}

	private void init() {
		servers = new Server[row];
		for (int i = 0; i < row; i++) {
			servers[i] = new Server(coefficient[i]);		
		}			
	}

	/**
	 * calculate all the parity data in a codeword
	 * @param dataList
	 * @return
	 */
	public List<Long> calParity(List<Long> dataList) {
		ArrayList<Long> res = new ArrayList<Long>();
		Long[] B = new Long[dataList.size()];
		
		if (column != dataList.size()) {
			System.out.println("QPC: the column parameter is not consistent.");
			System.exit(-1);
		}		
		B = (Long[]) dataList.toArray(B);
		double[] data = convertLongToDouble(B);
		
		double[] parities = basic.mutiply(coefficient, data);
		res = convertDoubleToLong(parities);
		
		return res;
	}
	
	/**
	 * authentication procedure
	 * @param codeword
	 */
	public boolean authenticate(long[] codeword) {
		List<Long> original = new ArrayList<Long>();
		List<Long> parities = new ArrayList<Long>();
		for (int i = 0; i < column; i++) {
			original.add(codeword[i]);
		}
		parities = calParity(original);
		for (int j = 0; j < row; j++) {
			if (parities.get(j) != codeword[column + j]) {
				return false;
			}			
		}
		return true;
	}
	
	public List<Long> correction(long[] codeword) {
		List<Long> original = new ArrayList<Long>();
//		long[] parities = new long[row];
		Server[] servers = new Server[row + column];
		for (int i = 0; i < column; i++) {
			original.add(codeword[i]);
		}
		
		double[][] extendCoeffi = equationSystem(original);
		
		for (int i = 0; i < column; i++) {
			servers[i] = new Server();
			servers[i].setParityData(original.get(i));
			servers[i].setCoefficient(extendCoeffi[i]);
		}	
		
		for (int j = 0; j < row; j++) {
			servers[column + j] = new Server();
			servers[column + j].setParityData(codeword[column + j]);
			servers[column + j].setCoefficient(extendCoeffi[column + j]);
		}
		
		List<List<Server>> subsets = subsets(servers);

		return mostPopularSol(subsets);
	}
	
	private double[][] equationSystem(List<Long> original) {
		double[][] extendCoeffi = new double[row + column][column];
		for (int i = 0; i < column; i++) {
			extendCoeffi[i][i] = 1;
//			extendCoeffi[i][i] = extend;
		}
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < column; j++) {
				extendCoeffi[column + i][j] = coefficient[i][j];
			}
		}
		return extendCoeffi;
	}

	
	private List<Long> mostPopularSol(List<List<Server>> subsets) {
		Map<String, Integer> solMap = new HashMap<String, Integer>();
		for (int i = 0; i < subsets.size(); i++) {
			List<Server> partialPari = subsets.get(i);   //A x = B
			double[][] aLeft = new double[partialPari.size()][column];
			Long[] bRight = new Long[partialPari.size()]; 
			for (int j = 0; j < partialPari.size(); j++) {		
				aLeft[j] = partialPari.get(j).getCoefficient();
				bRight[j] = partialPari.get(j).getParityData(); 
			}
			double[] sol = basic.divide(aLeft, convertLongToDouble(bRight));
			if (sol == null) {
				continue;
			}
			String solStr = convertDoubleToString(sol);

			if (solMap.containsKey(solStr)) {
				solMap.put(solStr, solMap.get(solStr) + 1);
			} else {
				solMap.put(solStr, 1);
			}						
		}
		
		PriorityQueue<Map.Entry<String, Integer>> queue = new PriorityQueue<Map.Entry<String, Integer>>( 
				new Comparator<Map.Entry<String, Integer>>() {				
					public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
						return o2.getValue() - o1.getValue();
					}	
				}
				);
		for (Map.Entry<String, Integer> entry : solMap.entrySet()) {
			queue.add(entry);
		}		

		String popularStr = queue.poll().getKey();
		double[] popularSol = converStringToDouble(popularStr);
		return convertDoubleToLong(popularSol);
	}

	private double[] converStringToDouble(String popularStr) {		
		String[] strs = popularStr.split("\\|");
		double[] res = new double[strs.length];
		for (int i = 0; i < strs.length; i++) {
			res[i] = Double.valueOf(strs[i]);
		}
		return res;
	}

	private String convertDoubleToString(double[] sol) {
		String res = "";
		for (int i = 0; i < sol.length; i++) {
			res = res + String.valueOf(sol[i]) + "|";
		}
		return res;
	}

	/**
	 * calculate the subsets of parities
	 * @param nums
	 * @return
	 */
	public List<List<Server>> subsets(Server[] nums) {
//		Arrays.sort(nums, new CompareServ());
		
	    List<List<Server>> res = new ArrayList<List<Server>>();
	    List<Server> each = new ArrayList<Server>();
	    helper(res, each, 0, nums);
	    return res;
	}
	
	
	private void helper(List<List<Server>> res, List<Server> each, int pos, Server[] n) {
	    if (each.size() == column) {
	        res.add(each);
	    }
	    int i = pos;
	    while (i < n.length) {
	        each.add(n[i]);
	        helper(res, new ArrayList<Server>(each), i + 1, n);
	        each.remove(each.size() - 1);
	        i++;
//	        while (i < n.length && n[i] == n[i - 1]) {i++;}
	    }
	    return;
	}

	private ArrayList<Long> convertDoubleToLong(double[] parities) {
		ArrayList<Long> res = new ArrayList<Long>();
		for (int i = 0; i < parities.length; i++) {
			res.add((long)parities[i]);
		}
		return res;
	}

	private double[] convertLongToDouble(Long[] b) {
		double[] data = new double[b.length];
		for (int i = 0; i < b.length; i++) {
			data[i] = (double)b[i];
		}
		return data;
	}
	
	


	public static void main(String[] args){
//		long[] B = new long[]{1, 2, 3, 4, 5};
//		double[][] A = new double[][]{{1, 2, 7}, {2, 3, 5}, {5, 4, 1}};
		double[][] A = new double[][]{{1, 2, 7}, {2, 3, 5}, {3, 2, 1}, {4, 5, 2}, {1, 3, 5}};
		double[] B = new double[]{2600, 2300, 1000, 2000, 2200};
//		List<List<Long>> res = new QPC(A).subsets(B);
		
//		for (int i = 0; i < res.size(); i++) {
//			for (Long l : res.get(i)) {
//				System.out.print(l + " | ");
//			}
//			System.out.println();
//		}
		
		
//		double[][] newCoeff = new QPC(A).equationSystem();
//	
//		
		List<Long> res = new QPC(A).correction(new long[]{1, 2, 3, 2000, 2300, 1000, 2000, 2200});
		for (int i = 0; i < res.size(); i++) {
			System.out.println(res.get(i));
		}
		
	}
}
