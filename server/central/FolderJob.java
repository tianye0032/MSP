package MSP.server.central;

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
				
				FileUtils.deleteVersionFiles(distributed, versionLen);
			} else {
				FileUtils.deleteFile(central);
			}
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

}
