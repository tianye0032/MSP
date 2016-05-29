package MSP.utils;

import java.util.ArrayList;
import java.util.List;

public class Casting {
	
	public static byte[] intToBytes(int data) {
		byte[] res = new byte[4];
		for (int i = 3; i >= 0; i--) {
			res[4 - i - 1] = (byte) (data >> 8 * i & 0xff);
		}
		return res;
	}

	public static long[] listToLong(List<Long> lists) {
		long[] res = new long[lists.size()];
		for (int i = 0; i < lists.size(); i++) {
			res[i] = lists.get(i);
		}
		return res;
	}
	
	public static List<Long> LongToList(long[] data) {
		List<Long> res = new ArrayList<Long>();
		for (int i = 0; i < data.length; i++) {
			res.add(data[i]);
		}
		return res;
	}
}
