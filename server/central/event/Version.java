package MSP.server.central.event;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import MSP.server.central.Configure;

public class Version {	
	private String file;
	
	private String versionId;
	
	public Version() {
		super();
	}
	
	public Version(String file, String versionId) {
		this.file = correctFormat(file);    //data/test/box1/a  0f719c0a5106ec4fd26879ad9d621edd
		this.versionId = versionId;
	}
	
	public Version(String path,Configure config) {
		int index = path.lastIndexOf("_");
		if (index > 0) {
			this.file = path.substring(0, index);
			this.file = config.getRelativePath(file);
			this.versionId = path.substring(index + 1);
		} else {
			this.file = config.getRelativePath(path);
			this.versionId = generaterVersion(path); 
		}		
	}
	
	public String correctFormat(String filename) {
		int index = filename.lastIndexOf("_");
		if (index > 0) {
			return filename.substring(0, index);
		} else {
			return filename;
		}		
	}
		
	public String generaterVersion(String path) {
		File file = new File(path);
		if (!file.exists()) {
			System.out.println(path + ": does't exist!");
			return null;
		} else {
			return hashFile(file, "MD5");
		}		
	}
	
	private String hashFile(File file, String algorithm) {
		FileInputStream inputStream = null;
		MessageDigest digest = null;
		byte[] bytesBuffer = new byte[1024];
		try {
			inputStream = new FileInputStream(file);
			digest = MessageDigest.getInstance(algorithm);
			int bytesRead = -1;
			while ((bytesRead = inputStream.read(bytesBuffer)) != -1) {
				digest.update(bytesBuffer, 0, bytesRead);
			}		
			inputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
			byte[] hashedBytes = digest.digest();
		
			
			return convertByteArrayToHexString(hashedBytes);
		
	}
	
	 private String convertByteArrayToHexString(byte[] arrayBytes) {
		    StringBuffer stringBuffer = new StringBuffer();
		    for (int i = 0; i < arrayBytes.length; i++) {
		        stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16)
		                .substring(1));
		    }
		    return stringBuffer.toString();
		}
	
	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}
	
	public String getVersionId() {
		return versionId;
	}

	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}
	
	
	public static void main(String[] args) {
		Version ver = new Version();
		String hh = ver.generaterVersion("data/test/central/hh");
//		String box1 = ver.generaterVersion("data/test/box1/f_9aecd8d57a425926a98caacbd18e79d7");
		String va = ver.generaterVersion("data/test/central/a");
		String vc = ver.generaterVersion("data/test/central/c");
		String ve = ver.generaterVersion("data/test/central/e");
		String vf = ver.generaterVersion("data/test/central/f");
		
		System.out.println("hh: " + hh);
//		System.out.println("box1: " + box1);
		System.out.println("a: " + va);
		System.out.println("c: " + vc);
		System.out.println("e: " + ve);
		System.out.println("f: " + vf);
		
	}
}