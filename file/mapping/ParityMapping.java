package MSP.file.mapping;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import MSP.file.mapping.parity.compute.QPC;
import MSP.server.central.Configure;
import MSP.utils.Casting;
import MSP.utils.FileUtils;

public class ParityMapping implements MappingMethod {
	private static final String name  = "ParityMapping";
	
	private int numDataSer = Configure.getIntValue(Configure.NUM_DATASERVER);
	private int numParitySer = Configure.getIntValue(Configure.NUM_PARITYSERVER);	
	
	private QPC qpc = new QPC(Configure.getCoefficient());
	
	@Override
	public String getName() {
		return name;
	}
	
	

	@Override
	public boolean isAuthentic(String[] target) {

		return false;
	}

	@Override
	public boolean merge(String[] source, String target) {
		init(target, source);
		int len = source.length;	
		
		List<DataInputStream> inList = new ArrayList<DataInputStream>();
		try {
			DataOutputStream out = new DataOutputStream(new FileOutputStream(target));
			for (int i = 0; i < len; i++) {
				inList.add(new DataInputStream(new FileInputStream(source[i])));
			}
			
			boolean flag = true;		
			int count = len;	
			HashSet<Integer> set = new HashSet<Integer>();
 			List<Long> previousOrig = new ArrayList<Long>();
			while (count > (numDataSer + numParitySer / 2)) {
				long[] codeword = new long[len];
				long[] originalData = new long[numDataSer];
				
				for (int k = 0; k < numDataSer; k++) {
					int ava = inList.get(k).available();
					if (ava >= 4) {
						codeword[k] = (long)inList.get(k).readInt();
					} else {
						codeword[k] = 0l;
						if (!set.contains(k)) {
							count--;
							set.add(k);
						}
					}
				}
				for (int k = 0; k < numParitySer; k++) {
					int avaPa = inList.get(numDataSer + k).available();
					if (avaPa >= 8) {
						codeword[numDataSer + k] = inList.get(numDataSer + k).readLong();
					} else {
						codeword[numDataSer + k] = 0l;
						if (!set.contains(k)) {
							count--;
							set.add(k);
						}
					}
				}
				if (count > (numDataSer + numParitySer / 2)) {
					if (!qpc.authenticate(codeword)) {
						originalData = Casting.listToLong(qpc.correction(codeword));
					} else {
						originalData = Arrays.copyOfRange(codeword, 0, numDataSer);
					}
					if (previousOrig.size() != 0) {
						for (int k = 0; k < numDataSer; k++) {
							long temData = previousOrig.get(k);
							out.writeInt((int)temData);
						}
					}
					previousOrig = Casting.LongToList(originalData);					
				}				
			}	
			ArrayList<Byte> bytesList = new ArrayList<Byte>();
			for (int i = 0; i < previousOrig.size(); i++) {
				long item = previousOrig.get(i);
				byte[] lastBytes = Casting.intToBytes((int)item);
				bytesList = clearReplenish(lastBytes);
				for (int k = 0; k < bytesList.size(); k++) {
					out.write(bytesList.get(k));
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
		
		return true;
	}

	private ArrayList<Byte> clearReplenish(byte[] lastBytes) {
		ArrayList<Byte> res = new ArrayList<Byte>();
		int i = 0;		
		while ((lastBytes[lastBytes.length - 1 - i]) == (byte)0x00) {
			i++;
		}
		if (lastBytes[lastBytes.length - 1 - i] == (byte)0x80) {
			i++;
		}
		for (int j = 0; j < lastBytes.length - i; j++) {
			res.add(lastBytes[j]);
		}
		return res;
	}


	@Override
	public boolean split(String source, String[] target) {
		init(source, target);
		int len = target.length;		
		
		DataOutputStream[] out = new DataOutputStream[len];			
		try {
			DataInputStream in = new DataInputStream(new FileInputStream(source));			
			for (int i = 0; i < len; i++) {	
				out[i] = new DataOutputStream(new FileOutputStream(target[i]));
			}			
			int flag = 0;
			List<Long> dataList = new ArrayList<Long>();
			while (in.available() >= 4) {
				System.out.println(in.available());
				if (dataList.size() == numDataSer) {					
					writeParities(out, dataList); 	//calculate the parity data
					dataList = new ArrayList<Long>();
				}
				
				int cc = in.readInt();	
				flag %= numDataSer;
				out[flag].writeInt(cc);
				
				dataList.add((long)cc);						
				flag++;
			}
			
			if (in.available() > 0) {
				ArrayList<Integer> listInt = new ArrayList<Integer>();
				ArrayList<Byte> temBytes = new ArrayList<Byte>();
				int ccc = in.read();
				while (ccc != -1) {
					temBytes.add(new Integer(ccc).byteValue());
					ccc = in.read();
				}
				int last = lastInt(temBytes);
				listInt.add(last);
				replenishInt(listInt, numDataSer - 1);
				
				for (int k = 0; k < listInt.size(); k++) {
					flag %= numDataSer;
					out[flag].writeInt(listInt.get(k));
					
					dataList.add((long)listInt.get(k));	
					
					flag++;
				}	
			}			
			writeParities(out, dataList);
			
			in.close();
			for (int i = 0; i < numDataSer; i++) {
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
	
	private void init(String source, String[] target) {
		FileUtils.createFile(source);
		FileUtils.createFiles(target);		
		if (target.length != numDataSer + numParitySer) {
			System.out.println("The total number of dataServer and parityServer does not match with target files.");
			System.exit(-1);
		}
	}

	/**
	 * write the parity data to files
	 * @param out
	 * @param dataList
	 */
	private void writeParities(DataOutputStream[] out, List<Long> dataList) {
		int size = dataList.size();
		List<Long> parityData = new ArrayList<Long>();
		int times = dataList.size() / numDataSer;
		int fromIndex = 0;
		for (int j = 0; j < times; j++) {
			try {
				parityData = qpc.calParity(dataList.subList(fromIndex, fromIndex + numDataSer));
				for (int k = 0; k < numParitySer; k++) {
					out[numDataSer + k].writeLong(parityData.get(k));
				}
				fromIndex += numDataSer;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
	}
	
	private int lastInt(ArrayList<Byte> temBytes) {
		int size = temBytes.size();
		temBytes.add(new Integer(0x80).byteValue());
		for (int j = size + 1; j < 4; j++) {
			temBytes.add(new Integer(0x00).byteValue());
		}		
		int temInt = (((temBytes.get(0) & 0xff) << 24) | ((temBytes.get(1) & 0xff) << 16) |
				  ((temBytes.get(2) & 0xff) << 8) | (temBytes.get(3) & 0xff));
		return temInt;
	}
	
	private void replenishInt(ArrayList<Integer> listInt, int numDataSer) {
		int tem = ((0x80 << 24) | ((0x00 & 0xff) << 16) |
				  ((0x00 & 0xff) << 8) | (0x00));
		for (int i = 0; i < numDataSer; i++) {
			listInt.add(tem);
		}		
	}
	

	public static void main(String[] args){
		
		ParityMapping par = new ParityMapping();
		String[] target = new String[6];
		double[][] coefficient = Configure.getCoefficient();
		
		for (int i = 0; i < 6; i++) {
			target[i] = "data\\test\\box" + i + "\\hh";
		}
		
//		par.split("data\\test\\central\\hh", target);
//		
		par.merge(target, "data\\test\\central\\hh");
		
//		byte[] lastBytes = new byte[]{new Integer(0x80).byteValue(), new Integer(0x00).byteValue()
//				, new Integer(0x00).byteValue(), new Integer(0x00).byteValue()};
//		
//		ArrayList<Byte> res = par.clearReplenish(lastBytes);
//		
//		System.out.println("");
		
}


}
