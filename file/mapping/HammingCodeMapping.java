package MSP.file.mapping;

import java.io.DataInputStream;
import java.io.DataOutputStream;
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
	private boolean authentic = true;
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public boolean isAuthentic(String[] source) {
		String tempTarget = new String("C:\\data\\test\\central\\temp");
		merge(source, tempTarget);
		return authentic;
	}

	@Override
	public boolean merge(String[] source, String target) {
		FileUtils.createFiles(source);
		FileUtils.createFile(target);
		int len = source.length;
		List<DataInputStream> inList = new ArrayList<DataInputStream>();
		
		try {
			DataOutputStream out = new DataOutputStream(new FileOutputStream(target));
			for (int i = 0; i < len; i++) {
				inList.add(new DataInputStream(new FileInputStream(source[i])));
			}
			ArrayList<BitSet> listBit = new ArrayList<BitSet>();
			ArrayList<Byte> lastVerifyBytes = new ArrayList<Byte>();
			boolean[] failFiles = new boolean[len];
			int countFiles = len;
			while (countFiles > 0) {	
				for (int j = 0; j < inList.size(); j++) {
					byte[] byteSpace = new byte[1];
					if (failFiles[j]) {
						BitSet zeroBits = new BitSet();
						listBit.add(zeroBits);
						continue;
					}
					int c = inList.get(j).read(byteSpace);
					if (c != -1) {
						//read a byte from a file in the dropbox, and store it in listBit.
						BitSet bits = BitSet.valueOf(byteSpace);
						listBit.add(bits);							
					} else {
						inList.get(j).close();
//						inList.remove(j);						
//						j--;
						//if data missing in one file, then use 0 to fillup, 
						//and correct it when doing the hamming check
						BitSet zeroBits1 = new BitSet();
						listBit.add(zeroBits1);
						failFiles[j] = true;
						countFiles--;
					}					
				}				

				//restore these bitsets to hamming code
				if (listBit == null || listBit.size() == 0) {
					return true;
				} 
				
				if (countFiles == 0) {
					for (int i = 0; i < len; i++) {
						listBit.remove(listBit.size() - 1);
					}
				}
				if (listBit.size() == 14) {
					List<BitSet> tem = files7ToHammingCode(listBit.subList(0, 7)); 
					byte[] verifyRes = verifyHammingCode(tem);
					for (int k = 0; k < verifyRes.length; k++) {
						out.write(verifyRes[k]);
					}
					for (int i = 0; i < 7; i++) {
						listBit.remove(0);
					}
				}
				
			}

			if (listBit != null && listBit.size() != 0) {
				List<BitSet> tem = files7ToHammingCode(listBit); 
				byte[] lastResBytes = verifyHammingCode(tem);
				for (int i = 0; i < lastResBytes.length; i++) {
					lastVerifyBytes.add(lastResBytes[i]);
				}
				
				//get rid of the marked 0000... bytes at the end by spiting
				int size = lastVerifyBytes.size();
				for (int j = size - 1; j >= 0; j--) {
					if (lastVerifyBytes.get(j) == 0) {
						lastVerifyBytes.remove(j);
					}
				}
				//get rid of 111111..., the same as above
				lastVerifyBytes.remove(lastVerifyBytes.size() - 1);
				//write the last few bytes to file
				for (int k = 0; k < lastVerifyBytes.size(); k++) {
					out.write(lastVerifyBytes.get(k));
				}
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

	public byte[] verifyHammingCode(List<BitSet> temSets) {
		if (temSets.size() % 2 != 0) {
			return null;
		}
		byte[] res = new byte[4];
		ArrayList<BitSet> bitList = new ArrayList<BitSet>();
		for (int i = 0; i < temSets.size(); i++) {
			BitSet tem4bit = correct74HammingCode(temSets.get(i));
			bitList.add(tem4bit);
		}		
		//restore all these 4bits to real data bytes
		for (int j = 0; j < bitList.size(); j += 2) {
			BitSet set = new BitSet();
			set.or(bitList.get(j));
			for (int k = 0; k < 4; k++) {
				if (bitList.get(j + 1).get(k)) {
					set.set(4 + k);
				}
			}
			BitSet revBit = reverseBitSet(set);
			
			
			
			byte[] temBytes = revBit.toByteArray();
			
			if (temBytes == null || temBytes.length == 0) {
				res[j / 2] = (byte)0;
			} else {
				res[j / 2] = temBytes[0];
			}			
		}
		return res;
	}

	private BitSet reverseBitSet(BitSet set) {
		BitSet bset = new BitSet();
		for (int i = 0; i <= 7; i++) {
			if (set.get(i)) {
				bset.set(7 - i);
			}
		}
		
		return bset;
	}

	public BitSet correct74HammingCode(BitSet bitSet) {
		BitSet res = new BitSet();
//		res.or(bitSet);
		int xorData = 0;
		if (bitSet.get(3)) {
			xorData ^= 3;
		}
		for (int i = 5; i < 8; i++) {
			if (bitSet.get(i)) {
				xorData ^= i;
			}
		}
		int parity = 0;
		parity = parity | ((bitSet.get(4)) ? (1 << 2) : 0);
		parity = parity | ((bitSet.get(2)) ? (1 << 1) : 0);
		parity = parity | ((bitSet.get(1)) ? 1 : 0);
		
		int index = xorData ^ parity;
		//correction
		if (index != 0) {
			bitSet.flip(index);
			authentic = false;
		}
		//get the data code
		if (bitSet.get(3)) {
			res.set(0);
		}
		for (int i = 5; i <= 7; i++) {
			if (bitSet.get(i)) {
				res.set(i - 4);
			}
		}
		
		return res;
	}

	/*
	 * input: 7 bitset from 7 files
	 * output: transfer to hamming code 
	 * 
	 */
	private List<BitSet> files7ToHammingCode(List<BitSet> listBit) {
		if (listBit.size() != 7) {
			System.out.println("the size of listBit is not 7");
			return null;
		}
		List<BitSet> bits = new ArrayList<BitSet>();
		for (int i = 0; i < 8; i++) {
			BitSet set = new BitSet();
			for (int j = 0; j < 7; j++) {
				if (listBit.get(j).get(i)) {
					set.set(j + 1);
				}
			}
			bits.add(set);
		}
		
		return bits;
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
		DataOutputStream[] out = new DataOutputStream [len];		
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
						out[k].write(new byte[]{byteRes[k]});
					}
					listBit.clear();
				}
				cc = in.read();
			}
			if (listBit.size() != 0) {    //add 1 and several 0 at the end of each target
				byte[] byteRes = bits8ToBytes(listBit);
				for (int k = 0; k < 7; k++) {
					out[k].write(new byte[]{byteRes[k]});
				}
			} else {         //add 10000000 at the end of each target file
				for (int k = 0; k < 7; k++) {
					out[k].write(new byte[]{(byte)1});
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
		
		return true;
	}
	
	/*
	 * transfer the 8 BitSet to 7 bytes, and attach ***100** at the end
	 */
	public byte[] bits8ToBytes(ArrayList<BitSet> listBit) {
		ArrayList<BitSet> temList = new ArrayList<BitSet>(listBit);
		ArrayList<BitSet> resList = new ArrayList<BitSet>();
		if (temList.size() < 8) {
			BitSet bitTem = new BitSet(8);    //the initial size is still 64
			bitTem.set(1, 8);			
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
			if (i < 4) {
				if ((((text >> (7 - i)) & 1) == 1)) {
					set1.set(count1);						
				}
				count1++;
			} else {
				if ((((text >> (7 - i)) & 1) == 1)) {
					set2.set(count2);
				}
				count2++;				
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
		
		//xor
		for (int i = 1; i < 8; i++) {
			if (set1.get(i)) {
				xor1 ^= i;
			}
			if (set2.get(i)) {
				xor2 ^= i;
			}
		}
		
		//parity code
		for (int j = 0; j < 2; j++) {
			if (((xor1 >> j) & 1) == 1) {
				set1.set(j + 1);
			}
			if (((xor2 >> j) & 1) == 1) {
				set2.set( j+ 1);
			}
		}
		if (((xor1 >> 2) & 1) == 1) {
			set1.set(4);
		}
		if (((xor2 >> 2) & 1) == 1) {
			set2.set(4);
		}		

		return new BitSet[]{set1, set2};
	}
	
	public static void main(String[] args){
		
			HammingCodeMapping ham = new HammingCodeMapping();
			String[] target = new String[7];
			for (int i = 0; i < 7; i++) {
				target[i] = "data\\test\\box" + i + "\\hh";
			}
//			ham.split("data\\test\\central\\hh", target);
			
			ham.merge(target, "data\\test\\central\\hh");
			
	}

}
