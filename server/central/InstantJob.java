package MSP.server.central;

import java.io.File;
import java.io.IOException;


import MSP.file.mapping.MappingMethod;
import MSP.utils.FileUtils;

public class InstantJob extends Thread{
	private JobType type;
	private String central;
	private String[] distributed;
	private boolean fromCentral;
	private String[] fileToDelete;
	private long timestampStart;
	public MappingMethod method;

	
	public int count;// Simply count the message for this file
	
	public InstantJob(){
		this.count=0;
		this.timestampStart=System.currentTimeMillis();
	}
	
	public boolean isWaitingTooLong(){
		int threshold = 5000;
		return System.currentTimeMillis()-this.timestampStart>threshold;
	}
	public boolean isReady(){
		if(fromCentral){
			return count>0;
		}else return count>= distributed.length;
	}
//	public boolean isFinished(){
////		return count == method.getBoxNum() + 1;
//		//add start
//		return count >= distributed.length;
//		//add end
//	}

	public void run()
	/**
	 * Run the instant job
	 */
	{
		System.out.println("An instant job starts");
		if(this.getType()==JobType.ADD||this.getType()==JobType.UPDATE){
			if(this.fromCentral){
				method.split(central, distributed);
			}else{
				method.merge(distributed, central);
			}
		}else if(this.getType()==JobType.DELETE){
			
		}
		if(this.fileToDelete!=null)
			for(String file: this.fileToDelete){
				FileUtils.deleteFile(file);
			}
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
	public MappingMethod getMethod() {
		return method;
	}
	public void setMethod(MappingMethod method) {
		this.method = method;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public boolean isFromCentral() {
		return fromCentral;
	}
	public void setFileToDelete(String[] fileToDelete) {
		this.fileToDelete = fileToDelete;
	}

	public void setFromCentral(boolean fromCentral) {
		this.fromCentral = fromCentral;
	}
}

