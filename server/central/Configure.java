package MSP.server.central;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import MSP.file.mapping.DuplicateMapping;
import MSP.file.mapping.DuplicateTwiceMapping;
import MSP.file.mapping.MappingMethod;
import MSP.file.mapping.MergeSplitMapping;
import MSP.utils.Reader;

public class Configure {
	

	final String CENTRAL = "CentralPath";
	final String MAP_METHOD = "MappingMethod";
	final String DISTRIBUTED = "DistributedPath";
	
	private HashMap<String,String> map;
	String centralPath;
	String[] distributedPath;
	MappingMethod method;
	int boxNum;
	public Configure(){
	}
	public Configure(String path){
		map = new HashMap<String,String>();
		Reader reader = new Reader(path);
		while(reader.ready()){
			//add start
			String line = reader.getline();
			if (!line.startsWith("#")) {
				String[] segs = line.split("\t");
				map.put(segs[0], segs[1]);
			}
		}
	}
	public MappingMethod getMethod() {
		return method;
	}
	public void setMethod(MappingMethod method) {
		this.method = method;
	}
	public void setCentralPath(String centralPath) {
		this.centralPath = centralPath;
	}
	public void setDistributedPath(String[] distributedPath) {
		this.distributedPath = distributedPath;
		this.boxNum = this.distributedPath.length;
	}
	public MappingMethod getMappingMethod(){

		String method = this.map.getOrDefault(MAP_METHOD, "DuplicateTwiceMapping");			
		
		MappingMethod instance;
		switch(method){
		case "DuplicateTwiceMapping": instance = new DuplicateTwiceMapping();
		break;
		case "DuplicateMapping": instance = new DuplicateMapping();
		break;

		//add start
		case "MergeSplitMapping" : 
			instance = new MergeSplitMapping();
			break;
		//over
		
		default: instance = new DuplicateTwiceMapping();
		}
		return instance;
	}
	
	public List<MappingMethod> getImplementedMethods(){
		List<MappingMethod> list = new ArrayList<MappingMethod>();
		list.add(new DuplicateTwiceMapping());
		list.add(new DuplicateMapping());
		list.add(new MergeSplitMapping());
		return list;
	}
	
	public String getCentralPath(){
		if(this.centralPath==null || this.centralPath.isEmpty())
			this.centralPath=this.map.get(CENTRAL);
		return this.centralPath;
	}
	
	//add start
	
	
//	public String getFileInitName(String filename) {
//		int lastSlash = filename.lastIndexOf('\\');
//		int underline = filename.lastIndexOf('_');
//		
//		int underlineRe = (underline == -1) ? filename.length() : underline;
//		return filename.substring(lastSlash + 1, underlineRe);
//	}
	//add end
	
	public String[] getDistributedPath(){
		if(this.distributedPath==null){
			this.distributedPath = map.get(DISTRIBUTED).split(",");	
			for(String path:this.distributedPath){
				path=path.replace('\\', '/');
			}
		}
		
//		ret[0]="C:/Users/tianye0032/Dropbox/workspace/MSPproject/data/test/box1/";
//		ret[1]="C:/Users/tianye0032/Dropbox/workspace/MSPproject/data/test/box2/";
		return this.distributedPath;
	}
	public int getBoxNum(){
		if(this.distributedPath==null){
			this.distributedPath = map.get(DISTRIBUTED).split(",");			
		}
		return this.distributedPath.length;
	}
	public boolean isFromCentral(String message){
//		message = this.getPath(message);
		if(message.contains(this.getCentralPath()))return true;
		return false;
	}
	public int fromWhichBox(String message){
		if(this.isFromCentral(message))return 0;
//		message = this.getPath(message);
		for(int i=0;i<this.getDistributedPath().length;i++){
			if(message.contains(this.getDistributedPath()[i]))return i + 1;
		}
		return -1;
	}
	public String getPath(String message){ 
		// Process the message and parse out the path info
		String ret = message.substring(message.lastIndexOf(": ")+2);
//		ret=ret.replace('\\', '/');
		if(ret.charAt(ret.length()-1)=='\n'){
			ret=ret.substring(0,ret.length()-1);
		}
		return ret;
	}
	public String getRelativePath(String file){
		int ind = this.fromWhichBox(file);
		String pattern = null;
		if(ind == 0){
			pattern = this.getCentralPath();
		}else {
			pattern = this.getDistributedPath()[ind - 1];
		}
//		String path = this.getPath(message);	
		if (file.indexOf(pattern) == -1) {
			return null;
		}
		
		return file.substring(file.indexOf(pattern)+pattern.length());
	}
	public JobType getType(String message){
		if(message.startsWith("ENTRY_MODIFY")){
			return JobType.UPDATE;
		}else if(message.startsWith("ENTRY_CREATE")){
			return JobType.ADD;
		}else if(message.startsWith("ENTRY_DELETE")){
			return JobType.DELETE;
		}else return null;
	}
	public static void main(String[] args){
		Configure config = new Configure("data/conf/center.conf");
		String message = "ENTRY_MODIFY: C:\\Users\\tianye0032\\Dropbox\\workspace\\MSPproject\\data\\test\\box2\\hh";		
		System.out.println(config.getRelativePath(message));
		System.out.println(config.getCentralPath());
	}
}
