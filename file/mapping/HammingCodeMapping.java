package MSP.file.mapping;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import MSP.utils.FileUtils;

public class HammingCodeMapping implements MappingMethod{
	private static final String name  = "HammingCodeMapping";
	@Override
	public boolean isAuthentic(String[] target) {
		// TODO Auto-generated method stub
		
		// For loop
		// Read 1 bit from each distributed file
		//  
				// Wrtie each block into a distributed file
		return false;
	}

	@Override
	public boolean merge(String[] source, String target) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * 	Read in byte , transform to bits
	 *  For loop
	 *  Read 4 bits, generate hamming code;
	 *  Split into 7 blocks, 
	 *  Wrtie each block into a distributed file
	 */
	@Override
	public boolean split(String source, String[] target) {
		FileUtils.createFile(source);
		FileUtils.createFiles(target);		
		int len = target.length;
		DataOutputStream [] out = new DataOutputStream [len];		
		try {
			DataInputStream in = new DataInputStream(new FileInputStream(source));
			
			for (int i = 0; i < len; i++) {
				out[i] = new DataOutputStream(new FileOutputStream(target[i]));
			}
			int cc = in.read();
			
			ArrayList<BitSet> listBit = new ArrayList<BitSet>();
			while (cc != -1) {
				byte value = new Integer(cc).byteValue();
				
				
				BitSet[] code = generateHammingCode(value);
				for (int j = 0; j < code.length; j++) {
					listBit.add(code[j]);
				}
				if (listBit.size() == 8) {
					byte[] byteRes = bits8ToBytes(listBit);
					for (int k = 0; k < 7; k++) {
						out[k].writeByte(byteRes[k]);
					}
					listBit.clear();
				}
				cc = in.read();
			}
			if (listBit.size() != 0) {    //add 1 and several 0 at the end of each target
				byte[] byteRes = bits8ToBytes(listBit);
				for (int k = 0; k < 7; k++) {
					out[k].writeByte(byteRes[k]);
				}
			} else {         //add 10000000 at the end of each target file
				for (int k = 0; k < 7; k++) {
					out[k].writeByte(256);
				}
			}

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
		
		return false;
	}
	
	/*
	 * transfer the 8 BitSet to 7 bytes, and attach ***100** at the end
	 */
	private byte[] bits8ToBytes(ArrayList<BitSet> listBit) {
		BitSet bitSet = new BitSet();
		ArrayList<BitSet> temList = new ArrayList<BitSet>(listBit);
		ArrayList<BitSet> resList = new ArrayList<BitSet>();
		if (temList.size() < 8) {
			BitSet bitTem = new BitSet(8);    //the initial size is still 64
			bitTem.set(1, 8);
			
			
			
//			for (int i = 0; i < 8; i++) {
//				if (bitTem.get(i)) {
//					System.out.println("i = " + i + " | " + 1);
//				} else {
//					System.out.println("i = " + i + " | " + 0);
//				}
//			}
						
			temList.add(bitTem);
			while (temList.size() < 8) {
				bitTem = new BitSet(8);
				temList.add(bitTem);
			}
		} 
		
		for (int i = 1; i < 8; i++) {    //initial the result
			BitSet bits = new BitSet();
			resList.add(bits);		
			for (int j = 0; j < 8; j++) {
				if (temList.get(j).get(i)) {
					bits.set(j);
				}
			}
		}
				
		//transfer the bitset to byte:  0000 0100 -> 32
		byte[] byteRes = new byte[7];
		for (int i = 0; i < resList.size(); i++) {
			byte[] temByte = resList.get(i).toByteArray();
			if (temByte == null || temByte.length == 0) {      // when all the bits in bitset is 0
				byteRes[i] = 0;
			} else {
				byteRes[i] = temByte[0];
			}
		}
		
		
		return byteRes;
	}
	
	public BitSet[] generateHammingCode(byte text) {
		BitSet set1 = new BitSet(8);    //start from index = 1
		BitSet set2 = new BitSet(8);
		int count1 = 4;
		int xor1 = 0;
		int count2 = 4;
		int xor2 = 0;
		//data code
		for (int i = 0; i < 8; i++) {
			if (((text >> i) & 1) == 1) {
				if (i < 4) {
					set1.set(count1);
					count1++;
					xor1 ^= (i + 1);
				} else {
					set2.set(count2);
					count2++;
					xor2 ^= (i + 1);
				}
			}
		}	
		//parity code
		for (int j = 0; j < 3; j++) {
			if (((xor1 >> j) & 1) == 1) {
				set1.set(j + 1);
			}
			if (((xor2 >> j) & 1) == 1) {
				set2.set(j + 1);
			}
		}
		if (set1.get(3) != set1.get(4)) {
			set1.flip(3);
			set1.flip(4);
		}
		if (set2.get(3) != set2.get(4)) {
			set2.flip(3);
			set2.flip(4);
		}
		
		return new BitSet[]{set1, set2};
	}
	
	
	public static void main(String[] args){
		
//			FileUtils.copyFile("data\\test\\central\\hh", "data\\test\\box1\\gg\\hh");
//			String dir = "data/test/";
//			File file=new File(dir);
//			List<File> fileList=getFilesIn(file);
//			for(File x:fileList)
//			System.out.println(x.getPath());
//			FileUtils.deleteFile("data\\test\\box1\\hh");
			
		
			HammingCodeMapping ham = new HammingCodeMapping();
			String[] target = new String[7];
			for (int i = 0; i < 7; i++) {
				target[i] = "data\\test\\box" + i + "\\hh";
			}
			ham.split("data\\test\\central\\hh", target);
			
//			BitSet set = new BitSet();
//			byte[] by = set.toByteArray();
//			System.out.println(by.toString());
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

}
