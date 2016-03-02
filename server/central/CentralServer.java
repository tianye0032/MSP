package msp.server.central;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import msp.server.central.event.IndexTree;
import msp.server.central.event.Version;
import msp.server.watcher.WatchDir;
import msp.utils.FileUtils;
import msp.utils.StringUtils;

public class CentralServer {
	Configure config;
	Map<String,InstantJob> jobPool;
	IndexTree indexTree;
//	private InstantJob mergeJob;
	private ArrayList<String> pool = new ArrayList<String>();
	
	private StringBuffer buffer;
	// The variables below should be re-organized
	Pipe pipe;
	Pipe.SinkChannel[] psics;
	Pipe.SourceChannel psoc;

	public CentralServer(){
		config = new Configure("data/conf/center.conf");
		jobPool = new HashMap<String,InstantJob>();
		indexTree = new IndexTree();
		try {
			pipe = Pipe.open();
		} catch (IOException e) {
			e.printStackTrace();
		}
		psics = new Pipe.SinkChannel[this.config.getBoxNum()+1];
		for(int i=0;i<this.config.getBoxNum()+1;i++)
			psics[i]= pipe.sink();
		
		psoc = pipe.source();
		Path centralDir = Paths.get(this.config.getCentralPath());
		try {
		    WatchDir central = new WatchDir(centralDir, true,psics[0]);
		    central.start();
		    WatchDir[] boxes = new WatchDir[this.config.getBoxNum()];
		    for(int i=0;i<this.config.getBoxNum();i++){
		    	Path boxDir = Paths.get(this.config.getDistributedPath()[i]);
		    	boxes[i] = new WatchDir(boxDir, true,psics[i+1]);
		    	boxes[i].start();
		    }
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.buffer = new StringBuffer();
		 
	}
	public String getMessage()
	/**
	 * Get next message from watchers
	 */
	{
		int bbufferSize = 4096;
	    ByteBuffer bbuffer = ByteBuffer.allocate(bbufferSize);
	    String ret=null;
	    int index = buffer.indexOf("\n");
	    if(this.buffer.length()==0||index<0){
		    try {
		     psoc.read(bbuffer);
	//	     ret=StringUtils.ByteBufferToString(bbuffer);
		     buffer=buffer.append(StringUtils.ByteBufferToString(bbuffer));
		     index = buffer.indexOf("\n");
		    } catch (IOException e) {
		     e.printStackTrace();
		    }
	    }
	    ret = buffer.substring(0,index);
		buffer=buffer.delete(0, index+1);
		return ret;
	}

	public void processMessage(String message) throws IOException {
	/**
	 * Process the message
	 * Initial or start an instant job when a new message comes
	 */
		System.out.println("Message Received : "+ message);
	    //Leave recursion to the end! Now focus on single file!
//		String filename = this.config.getRelativePath(message);
//		if (filename == null) {
//			return;
//		}		
		
		//add start	
		String filePath = config.getPath(message);
//		List<File> list = FileUtils.getFilesIn(filePath);
		for(File file:FileUtils.getFilesIn(filePath)){
			if(file.isDirectory()){
				//If the change is a directory change, then do nothing
			}else{
				String filename = this.config.getRelativePath(file.getAbsolutePath());
				Version version = new Version(file.getPath(),config);
				
				
				if(config.isFromCentral(message)){
					if ((!indexTree.isNewVersion(version)||(config.getType(message)!=JobType.UPDATE)))
{
						System.out.println("Duplicate Message Ignored!   "+version.getFile()+version.getVersionId());
						return;
					}
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
					splitJob.start();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}else{
					if ((!indexTree.isNewVersion(version)||(config.getType(message)!=JobType.UPDATE)))
					{
						System.out.println("Duplicate Message Ignored!   "+version.getFile()+version.getVersionId());
						return;				
					} 
					int boxInd = config.fromWhichBox(message);
					System.out.println("A message from Box : " + boxInd);
					
//					String fileInitName = config.getRelativePath(filename);
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
						if(mergeInst.getDistributed()[boxInd-1]!=null)return;
						
						mergeInst.getDistributed()[boxInd-1] = filePath;
						mergeInst.count++;	
						if(mergeInst.isFinished()){	
							indexTree.addNewVersion(version);	//Add this version to the IndexTree			
							
							mergeInst.start();
							jobPool.remove(filename);
							pool.clear();
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
	
	public static void main(String[] args)throws IOException{
		CentralServer server = new CentralServer();
	    while(true){
	    	try{
	    		
	    	String message = server.getMessage(); 
	    	server.processMessage(message);	    	

	    	}catch(Exception e){
	    		System.out.println("Error In Main Method!     "+e.getMessage());
	    	}
	    }
	        
	}
	
	
	
	
}
