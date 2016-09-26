package MSP.data;

import java.util.ArrayList;
import java.util.List;

import MSP.server.central.Configure;

public class AuthenDataQuery {	
	private int numDataSer = Configure.getIntValue(Configure.NUM_DATASERVER);
	private int numParitySer = Configure.getIntValue(Configure.NUM_PARITYSERVER);
	private int extend = Configure.getIntValue(Configure.EXTEND);
	
	public static void main(String[] args) {
		new AuthenDataQuery().start();
	}
	public void start() {
		ownerProcess();
		
		int lower = 2;
		int upper = 5;		
		List<List<Long>> res = serverProcess(lower, upper);
		List<Long> corRes = correction(res);
		
		List<Long> filterRes = filterRes(corRes, lower, upper);
		
		for (int i = 0; i < filterRes.size(); i++) {
			System.out.println(filterRes.get(i));
		}
	}
	
	private List<Long> filterRes(List<Long> corRes, int lower, int upper) {
		List<Long> res = new ArrayList<Long>();
		for (Long tem : corRes) {
			if (tem >= lower && tem <= upper) {
				res.add(tem);
			}
		}
 		return res;
	}
	private List<Long> correction(List<List<Long>> res) {
		TableMapping table = new TableMapping();
		return table.correctMerge(res);
	}
	public void ownerProcess() {
		DataOwner owner = new DataOwner();
		String[] target = new String[7];
		int len = numDataSer + numParitySer;
		for (int i = 0; i < len; i++) {
			target[i] = "data\\test\\box" + i + "\\hh";
		}
		String source = "data\\test\\central\\hh";
		owner.distribute(source, target);
	}
	
	public List<List<Long>> serverProcess(long lower, long upper) {
		List<List<Long>> res = new ArrayList<List<Long>>();
		int len = numDataSer + numParitySer;
		String[] servers = new String[len];
		for (int i = 0; i < len; i++) {
			servers[i] = "data\\test\\box" + i + "\\hh";
			LocalServer localServer = new LocalServer(servers[i]);
			List<Long> subRes = new ArrayList<Long>();
			if (i < numDataSer) {
				subRes = localServer.processQuery(lower, upper);
			} else {
				subRes = localServer.processQuery(lower * extend, upper * extend);
			}	
			
			res.add(subRes);			
		}
		absorbCodeword(res);
		
		return res;
	}
	private void absorbCodeword(List<List<Long>> result) {
		int size = result.size();
		long maxStart = 0;
		for (int i = 0; i < size; i++) {
			if (result.get(i) != null) {
				maxStart = Math.max(maxStart, result.get(i).get(0));
			}
		}
		cleanStart(maxStart, result);
		
	}
	private void cleanStart(long maxStart, List<List<Long>> result) {
		int maxSize = 0;
		for (int i = 0; i < result.size(); i++) {
			if (maxStart != result.get(i).get(0)) {
				result.get(i).remove(0);
			}
			result.get(i).remove(0);      //remove the index and the redundant num in the front
			maxSize = Math.max(maxSize, result.get(i).size());
		}
		
		for (int j = 0; j < result.size(); j++) {
			if (maxSize == result.get(j).size()) {
				result.get(j).remove(maxSize - 1);   //remove the last row
			}
		}		
		
	}
}
