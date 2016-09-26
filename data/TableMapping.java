package MSP.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import MSP.file.mapping.parity.compute.QPC;
import MSP.server.central.Configure;
import MSP.utils.Casting;
import MSP.utils.FileUtils;

public class TableMapping {
	private int numDataSer = Configure.getIntValue(Configure.NUM_DATASERVER);
	private int numParitySer = Configure.getIntValue(Configure.NUM_PARITYSERVER);

	private QPC qpc = new QPC(Configure.getCoefficient());

	public boolean isAuthentic(String[] target) {

		return false;
	}

	public List<Long> correctMerge(List<List<Long>> source) {
		List<Long> res = new ArrayList<Long>();
		int len = source.size();
		List<Integer> errorServers = new ArrayList<Integer>();

		int count = len;
		HashSet<Integer> set = new HashSet<Integer>();
		while (count >= (numDataSer + numParitySer / 2)) {
			long[] codeword = new long[len];
			long[] originalData = new long[numDataSer];

			// read a codeword
			for (int k = 0; k < len; k++) {
				List<Long> ava = source.get(k);
				if (ava != null && ava.size() != 0) {
					codeword[k] = ava.get(0);
					source.get(k).remove(0);
				} else {
					codeword[k] = 0l;
					if (!set.contains(k)) {
						count--;
						set.add(k);
					}
				}

			}
			if (count >= (numDataSer + numParitySer / 2)) {
				if (!qpc.authenticate(codeword)) {
					originalData = Casting.listToLong(qpc.correction(codeword));
					List<Integer> errorSer = qpc.captureErrServer(originalData,
							codeword);
					addToErrorServs(errorServers, errorSer);
				} else {
					originalData = Arrays.copyOfRange(codeword, 0, numDataSer);
				}
				writeToRes(res, originalData);
			}
		}
		outprint(errorServers);
		return res;
	}

	private void writeToRes(List<Long> res, long[] originalData) {
		for (long ori : originalData) {
			res.add(ori);
		}
	}

	public boolean merge(String[] source, String target) {
		init(target, source);
		int len = source.length;
		List<Integer> errorServers = new ArrayList<Integer>();
		List<BufferedReader> inList = new ArrayList<BufferedReader>();
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(target));
			for (int i = 0; i < len; i++) {
				inList.add(new BufferedReader(new FileReader(source[i])));
			}

			int count = len;
			HashSet<Integer> set = new HashSet<Integer>();
			while (count >= (numDataSer + numParitySer / 2)) {
				long[] codeword = new long[len];
				long[] originalData = new long[numDataSer];

				// read a codeword
				for (int k = 0; k < len; k++) {
					String ava = inList.get(k).readLine();
					if (ava != null) {
						ava = ava.trim();
						codeword[k] = Long.parseLong(ava);
					} else {
						codeword[k] = 0l;
						if (!set.contains(k)) {
							count--;
							set.add(k);
						}
					}
				}
				if (count >= (numDataSer + numParitySer / 2)) {
					if (!qpc.authenticate(codeword)) {
						originalData = Casting.listToLong(qpc
								.correction(codeword));
						List<Integer> errorSer = qpc.captureErrServer(
								originalData, codeword);
						addToErrorServs(errorServers, errorSer);
					} else {
						originalData = Arrays.copyOfRange(codeword, 0,
								numDataSer);
					}
					writeTarget(out, originalData);
				}
			}

			for (int i = 0; i < len; i++) {
				inList.get(i).close();
			}
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		outprint(errorServers);
		return true;
	}

	private void writeTarget(BufferedWriter out, long[] originalData) {
		for (int k = 0; k < numDataSer; k++) {
			try {
				out.write(String.valueOf(originalData[k]) + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void addToErrorServs(List<Integer> errorSers, List<Integer> error) {
		for (int i = 0; i < error.size(); i++) {
			if (!errorSers.contains(error.get(i))) {
				errorSers.add(error.get(i));
			}
		}
	}

	private void outprint(List<Integer> errorServers) {
		if (errorServers.size() == 0) {
			return;
		}
		System.out
				.println("--------------The following servers are error: --------------");
		for (int i = 0; i < errorServers.size(); i++) {
			System.out.println("Server: " + errorServers.get(i));
		}
	}

	public boolean split(String source, String[] target) {
		init(source, target);
		int len = target.length;

		BufferedWriter[] out = new BufferedWriter[len];
		try {
			BufferedReader in = new BufferedReader(new FileReader(source));
			for (int i = 0; i < len; i++) {
				out[i] = new BufferedWriter(new FileWriter(target[i]));
			}
			// write Integer.MIN_VALUE in the first line
			writeStarting(out);

			// write the number in the middle
			int flag = 0;
			List<Long> dataList = new ArrayList<Long>();
			String line = in.readLine();
			while (line != null && line.trim().length() != 0) {
				line = line.trim();
				if (dataList.size() == numDataSer) {
					writeParities(out, dataList); // calculate the parity data
					dataList = new ArrayList<Long>();
				}
				flag %= numDataSer;
				out[flag].write(writeString(Integer.parseInt(line)));
				dataList.add((long) Integer.parseInt(line));
				flag++;
				line = in.readLine();
			}

			flag %= numDataSer;
			for (int i = flag; i < numDataSer; i++) {
				out[flag].write(writeString((long) Integer.MAX_VALUE));
				dataList.add((long) Integer.MAX_VALUE);
				flag++;
			}
			writeParities(out, dataList);

			in.close();
			for (int i = 0; i < len; i++) {
				out[i].flush();
				out[i].close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * write the parity data to files, if the last row is incomplete, then stop
	 * generating parities
	 * 
	 * @param out
	 * @param dataList
	 */
	private void writeParities(BufferedWriter[] out, List<Long> dataList) {
		List<Long> parityData = new ArrayList<Long>();
		int times = dataList.size() / numDataSer;
		int fromIndex = 0;
		for (int j = 0; j < times; j++) {
			try {
				parityData = qpc.calParity(dataList.subList(fromIndex,
						fromIndex + numDataSer));
				for (int k = 0; k < numParitySer; k++) {
					out[numDataSer + k].write(writeString(parityData.get(k)));
				}
				fromIndex += numDataSer;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void writeStarting(BufferedWriter[] out) {
		int len = out.length;
		long token = (long) Integer.MIN_VALUE;
		List<Long> dataList = new ArrayList<Long>();
		for (int i = 0; i < len; i++) {
			try {
				if (i < numDataSer) {
					out[i].write(writeString(token));
				} else {
					dataList.add(token);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		writeParities(out, dataList);
	}

	/**
	 * initiation work: check whether the configure is appropriate
	 * 
	 * @param source
	 * @param target
	 */
	private void init(String source, String[] target) {
		FileUtils.createFile(source);
		FileUtils.createFiles(target);
		if (target.length != numDataSer + numParitySer) {
			System.out
					.println("The total number of dataServer and parityServer does not match with target files.");
			System.exit(-1);
		}
	}

	private String writeString(long token) {
		return String.valueOf(token) + "\n";
	}

	public String getName() {

		return null;
	}

	public static void main(String[] args) {
		TableMapping par = new TableMapping();
		String[] target = new String[7];
		double[][] coefficient = Configure.getCoefficient();

		for (int i = 0; i < 7; i++) {
			target[i] = "data\\test\\box" + i + "\\hh";
		}

		// par.split("data\\test\\central\\hh", target);
		//
		par.merge(target, "data\\test\\central\\hh");

	}
}
