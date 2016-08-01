package MSP.server.central;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import MSP.server.central.event.IndexTree;
import MSP.server.central.event.Version;
import MSP.utils.FileUtils;

public class MainWorker extends Thread{
	Configure config;
	ConcurrentHashMap<String,InstantJob> jobPool;
//	Hashtable<String,String> messagePool;
	ConcurrentHashMap<String,String> messagePool;
	IndexTree indexTree;
//	public MainWorker(Hashtable<String,String> messagePool,Configure config){
	public MainWorker(ConcurrentHashMap<String,String> messagePool,Configure config){
		jobPool = new ConcurrentHashMap<String,InstantJob>();
		this.messagePool = messagePool;
		this.config = config;
		indexTree = new IndexTree();
	}
	public void processMessage(String message) throws IOException {
	/**
	 * Process the message
	 * Initial or start an instant job when a new message comes
	 */
		System.out.println("Message Being Procceed : "+ message);
	    //Leave recursion to the end! Now focus on single file!
		String filePath = config.getPath(message);
		
		//add about folder
		if (config.getType(message) == JobType.DELETE) {
			deleteAll(message);
		}
		//add over
		
		for(File file:FileUtils.getFilesIn(filePath)){   //it is not suitable for "DELETE", since the folder is gone. It return null
			String filename = this.config.getRelativePath(file.getAbsolutePath());
			if(file.isDirectory()){
				//If the change is a directory change, then do nothing		
				//add about folder
				if (config.getType(message) == JobType.ADD) {
					FolderJob addJob = new FolderJob();
					addJob.setCentDistri(config.getCentralPath(), config.getDistributedPath(), filename);
					if (config.isFromCentral(message)) {
						addJob.setFromCentral(true);
					} else {
						addJob.setFromCentral(false);
					}
					addJob.setType(JobType.ADD);
					addJob.start();
				}
				
				//add over				
			} else {				
				Version version = new Version(file.getPath(),config);
				if (config.getType(message)==JobType.DELETE)
					{
						System.out.println("Delete Message Ignored!   "+version.getFile()+"_"+version.getVersionId());
						return;
					}
				
				if (!indexTree.isNewVersion(version))
					{
						System.out.println("Duplicate Message Ignored!   "+version.getFile()+"_"+version.getVersionId());
						return;
					}
				if(config.isFromCentral(message)){					
					InstantJob splitJob = new InstantJob();
					splitJob.method=config.getMappingMethod();
					splitJob.setCentral(config.getCentralPath() + filename);
					String[] disPath = new String[config.getDistributedPath().length];
					
					String lastVersion = indexTree.getLastVersion(version.getFile());
					String[] fileToDelete = null;
					if(!lastVersion.isEmpty()){
						fileToDelete = new String[config.getDistributedPath().length];
						for(int i = 0; i < fileToDelete.length; i++) {						
							fileToDelete[i] = config.getDistributedPath()[i] + filename + "_" + lastVersion;				
						}
					}
					for(int i = 0; i < disPath.length; i++) {		
						disPath[i] = config.getDistributedPath()[i] + filename + "_" + version.getVersionId();	
					}

					splitJob.setDistributed(disPath);
					splitJob.setFromCentral(true);
//					splitJob.setType(JobType.UPDATE);
					splitJob.setType(config.getType(message));
					splitJob.setFileToDelete(fileToDelete);
					indexTree.addNewVersion(version);	
//					splitJob.start();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					splitJob.start();
				}else{ // when the message comes from a local distributed box
					
					int boxInd = config.fromWhichBox(message);
					System.out.println("A message from Box : " + boxInd);
					
					if (!jobPool.containsKey(filename)) {
						InstantJob inst = new InstantJob();
						inst.method = config.getMappingMethod();
						inst.setCentral(config.getCentralPath() + version.getFile());
						inst.setDistributed(new String[config.getBoxNum()]);
						inst.count++;
						inst.getDistributed()[boxInd-1] = config.getDistributedPath()[boxInd-1]+filename;
						inst.setType(config.getType(message));
						jobPool.put(filename, inst);
					} else {
						InstantJob mergeInst = jobPool.get(filename);
						mergeInst.updateTime();
						if(mergeInst.getDistributed()[boxInd-1]!=null)return;
						
//						mergeInst.getDistributed()[boxInd-1] = filePath;
						
						mergeInst.getDistributed()[boxInd-1] = config.getDistributedPath()[boxInd-1] + filename;;
						
						mergeInst.count++;	
						if(mergeInst.isReady()){	
							indexTree.addNewVersion(version);	//Add this version to the IndexTree			
							
							mergeInst.start();
							jobPool.remove(filename);

							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}	
				
				}
			}			
		}
	
	}
	/**
	 * delete file or folder
	 * @param message
	 */
	private void deleteAll(String message) {
		String filePath = config.getPath(message);
//		System.out.println("******MainWorker, filePath: " + filePath);
		String filename = this.config.getRelativePath(filePath);
//		System.out.println("******MainWorker, filename: " + filename);
		
		FolderJob deleteJob = new FolderJob();				
		deleteJob.setCentDistri(config.getCentralPath(), config.getDistributedPath(), filename);
		if (config.isFromCentral(message)) {
			deleteJob.setFromCentral(true);
		} else {
			deleteJob.setFromCentral(false);
		}
		deleteJob.setType(JobType.DELETE);
		deleteJob.start();
	}
	
	public void run() {
    	work();
    }
	public void work(){
		 while(true){
		    	try{		    		
		    		Thread.sleep(1000);
//		    		System.out.println(this.messagePool.size()+" messages; "+this.jobPool.size()+" jobs in the pool.");
		    		Set<String> toDelete = new HashSet<String>();
		    		
		    		
		    		for(Entry<String,String> entry:this.messagePool.entrySet()){
		    			File file = new File(entry.getKey());
		    			if(FileUtils.isFileUnlocked(file)){
		    				String message = entry.getValue();
//		    				this.messagePool.remove(entry.getKey());
		    				toDelete.add(entry.getKey());
		    				System.out.println("Message <"+message + "> would be removed!");
		    				this.processMessage(message);		    				
		    			}else{
		    				System.out.println(file.getAbsolutePath()+" is being used!");
		    			}
		    		}
		    		for(String file:toDelete)
		    			messagePool.remove(file);
		    		
		    		for(Entry<String,InstantJob> entry:this.jobPool.entrySet()){
		    			String filename = entry.getKey();
		    			InstantJob mergeInst = entry.getValue();
		    			if((!mergeInst.isFromCentral())&&mergeInst.isWaitingTooLong()){
		    				System.out.println("A merge job starts after waiting too long : "+ filename);
		    				for(int boxInd = 0;boxInd<config.getBoxNum();boxInd++){
		    					if(mergeInst.getDistributed()[boxInd]==null)
		    						{
		    							mergeInst.getDistributed()[boxInd] = "data/temp/"+boxInd+"/" + filename;
		    						}
							}
		    				Version version = new Version(filename,config);
							indexTree.addNewVersion(version);	//Add this version to the IndexTree			
							
							mergeInst.start();
							jobPool.remove(filename);

							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
		    			}
		    		}
		    	}catch(Exception e){
//		    		System.out.println("Error In Main Worker!     " + e.getMessage());
		    		e.printStackTrace();
		    	}
		    }
	}
	
}
