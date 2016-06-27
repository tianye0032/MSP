package MSP.server.central;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import MSP.server.watcher.WatchDir;
import MSP.utils.FileUtils;
import MSP.utils.StringUtils;

public class CentralServer extends Thread{
	Configure config;
	
	Hashtable<String,String> messagePool; // The arrived message list
	
	MainWorker worker;
//	private InstantJob mergeJob;
	
	
	private StringBuffer buffer;
	// The variables below should be re-organized
	Pipe pipe;
	Pipe.SinkChannel[] psics;
	Pipe.SourceChannel psoc;

	public CentralServer(){			
		this(new Configure("data/conf/center.conf"));	
		Configure.config = new Configure(Configure.CONFIGPATH);
	}
	public CentralServer(Configure config){
		
		messagePool = new Hashtable<String,String>();
		this.config = config;
		worker = new MainWorker(messagePool,config);
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
		
		for(File file:FileUtils.getFilesIn(filePath)){
			
			if(file.isDirectory()){
				//If the change is a directory change, then do nothing
				//add about folder
				this.messagePool.put(file.getAbsolutePath(), message);
			}else{				
				this.messagePool.put(file.getAbsolutePath(), message);				
			}			
		}
		
		//add about folder
		if (config.getType(message) == JobType.DELETE) {
			
//			System.out.println("***" + filePath);
//			System.out.println("****" + new File(filePath).getAbsolutePath());
			this.messagePool.put(new File(filePath).getAbsolutePath(), message);
		}
	
		
	}
    public void run() {
    	worker.start();
    	work();
    }
	public void work(){
		 while(true){
		    	try{		    		
			    	String message = this.getMessage(); 
			    	this.processMessage(message);	   	

		    	}catch(Exception e){
		    		System.out.println("Error In Central Server!     "+e.getMessage());
		    	}
		    }
	}
	
	public static void main(String[] args)throws IOException{
		CentralServer server = new CentralServer();
		
		server.start();
	        
	}
	
	
	
	
}
