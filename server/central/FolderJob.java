package MSP.server.central;

import java.io.File;

import MSP.server.central.event.Version;
import MSP.utils.FileUtils;


public class FolderJob extends Thread{	
	private static final int versionLen = 32;
	
	private JobType type;	
	private String central;	
	private String[] distributed;	
	private boolean fromCentral;
	
	public void run() {
		if (this.type == JobType.ADD) {
			if (fromCentral) {
				FileUtils.createFiles(distributed);
			} else {
				FileUtils.createFile(central);
			}
		} else if (this.type == JobType.DELETE) {
			if (fromCentral) {				
				deleteVersionFiles(distributed);
			} else {
				deleteNonVersionFile(central);
			}
		}
	}
	
	public void deleteNonVersionFile(String dest) {
		dest = dest.replace('\\', '/');
		int lastSlash = dest.lastIndexOf("/");					
		String folder = dest.substring(0, lastSlash);
		String oldfilename = dest.substring(lastSlash + 1);
		
		Version ver = new Version();
		String newfilename = ver.correctFormat(oldfilename);  //get rid of id from oldfile		
		
		File file = new File(folder + "/" + newfilename);
		
//		System.out.println("@@@delete from central: " + file.getAbsolutePath());
		if (file.exists()) {
			if (file.isFile()) {
				String newId = createId(folder + "/" + newfilename);
				String oldId = cutOutId(oldfilename);
				if (newId.equals(oldId)) {
					file.delete();
				}
			} else {
				file.delete();
			}	
		}
		
//		String[] list = new File(folder).list();
//		for (int j = 0; j < list.length; j++) {			
//			File file = new File(folder + "/" + list[j]);
//			if (list[j].equals(filename)) {
//				file.delete();
//			} else if (list[j].startsWith(filename)) {
//				String append = list[j].substring(filename.length());
//				if (append.length() == versionLen + 1 && isVersionFile(append)) {
//					file.delete();
//				}
//			}	
//		}
	}
	
	private String cutId(String filename) {
		int index = filename.lastIndexOf("_");
		if (index > 0) {
			return filename.substring(0, index);
		} else {
			return filename;
		}
		
	}
	private String cutOutId(String filename) {
		int index = filename.lastIndexOf("_");
		if (index > 0) {
			return filename.substring(index + 1);
		} else {
			return filename;
		}
	}

	public void deleteVersionFiles(String[] dest) {
		for (int i = 0; i < dest.length; i++) {
			File file = new File(dest[i]);
//			if (file.exists()) {
//				deleteVersionFile(dest[i]);			
//			} 
			deleteVersionFile(dest[i]);	
		}		
	}
	
	public void deleteVersionFile(String dest) {
		dest = dest.replace('\\', '/');
		int lastSlash = dest.lastIndexOf("/");					
		String folder = dest.substring(0, lastSlash);
		String filename = dest.substring(lastSlash + 1);
		File fo = new File(folder);
		if (!fo.exists()) {
			return;
		}
		
		String[] list = new File(folder).list();
		for (int j = 0; j < list.length; j++) {			
			File file = new File(folder + "/" + list[j]);
			if (!file.exists()) {
				return;
			}			
			
			if (list[j].equals(filename)) {
//				file.delete();
				FileUtils.deleteDir(file);
			} else if (list[j].startsWith(filename)) {
				String append = list[j].substring(filename.length());
				if (append.length() == versionLen + 1 && isVersionFile(append)) {
					file.delete();
				}
			}	
		}
	}	
	
	public String createId(String file) {
		Version ver = new Version();
		String newId = ver.generaterVersion(file);
		return newId;
	}

	public boolean isVersionFile(String filename) {
		int index = filename.lastIndexOf("_");
		String id = null;
		if (index >= 0 && index < filename.length() - 1) {
			id = filename.substring(index + 1);
		}		
		if (id == null) {
			return false;
		} else if (id.length() == versionLen) {
			return true;
		} else {
			return false;
		}
	}
	
	public void setCentDistri(String centralFolder, String[] distributedFolder, String filename) {
		this.setCentral(centralFolder + filename);
		String[] disPath = new String[distributedFolder.length];
		for(int i = 0; i < disPath.length; i++) {		
			disPath[i] = distributedFolder[i] + filename;	
		}
		this.setDistributed(disPath);
	}
	
	
	
	public JobType getType() {
		return type;
	}
	public void setType(JobType type) {
		this.type = type;
	}
	public String getCentral() {
		return central;
	}
	public void setCentral(String central) {
		this.central = central;
	}
	public String[] getDistributed() {
		return distributed;
	}
	public void setDistributed(String[] distributed) {
		this.distributed = distributed;
	}
	public boolean isFromCentral() {
		return fromCentral;
	}
	public void setFromCentral(boolean fromCentral) {
		this.fromCentral = fromCentral;
	}
	
	
	
	public static void main(String[] args) {
		File file = new File("data\\test\\box1\\aa\\box2\\hh");
		if (file.exists()) {
			System.out.println("yes");
		} else {
			System.out.println("no");
		}
	}

}
