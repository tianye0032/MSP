package MSP.server.central.event;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class IndexTree {
	
	//store the path: data/test/box1/a  0f719c0a5106ec4fd26879ad9d621edd
//	public static HashSet<String> versionSet = new HashSet<String>();
//	public static HashSet<Version> versionSet = new HashSet<Version>();
	
	private HashMap<String,List<String>> map;
	public IndexTree(){
		map = new HashMap<String,List<String>>();
	}
	public boolean isNewVersion(Version version) {

		if(map.containsKey(version.getFile())){
			List<String> history = map.get(version.getFile());
			for(int i= history.size()-1;i>=0;i--){
				if(history.get(i).equals(version.getVersionId()))return false;
			}			
		}
		return true;
	}
	public String getLastVersion(String file){
		if(map.containsKey(file)){
			List<String> history = map.get(file);
			if(!history.isEmpty())return history.get(history.size()-1);
		}
		return "";
	}
	public void addNewVersion(Version version) {
		if(map.containsKey(version.getFile())){
			List<String> history = map.get(version.getFile());
			history.add(version.getVersionId());
		}else{
			List<String> history = new ArrayList<String>();
			history.add(version.getVersionId());
			map.put(version.getFile(), history);
		}
	}
//	public static boolean isNewVersion(Version version) {
//		if (versionSet.contains(version.getFile() + "_" + version.getVersionId())) {
//			return false;
//		}
////		if (versionSet.contains(version)) {
////			return false;
////		}
//		return true;
//	}
	
//	public static boolean isNewVersion(String versionId) {
//		if (versionSet.contains(versionId)) {
//			return false;
//		}
//		return true;
//	}
	
//	public static void addNewVersion(Version version) {
//		versionSet.add(version.getFile() + "_" + version.getVersionId());
////		versionSet.add(version);
//	}
	

	public boolean initial(String path)
	/**
	 * Read the index tree structure and initial an IndexTree object
	 * Scan the local boxes and rebuild the IndexTree if necessary
	 * Return true if succeed and false if fail
	 */
	{
		return true;
	}
	
	public List<String> getVersionHistory(String file)
	/** 
	 * Get the version history of a given file
	 */
	{
		return map.get(file);
	}
	
	
	public boolean setVersion(String file)
	/**
	 * Set a new version of a file
	 */
	{
		return false; 
	}
	public int getHash(String version)
	/** 
	 * Get the hash code of the file from version number
	 */
	{
		return 0;
	}
	public Date getTime(String version)
	/** 
	 * Get the time stamp of the file from version number
	 */
	{
		return new Date();
	}
	
}
class TreeNode{
	List<String> versionHistory;
	String currentVersion;
	
	public String getCurrentVersion()
	/** 
	 * Get the current version
	 */
	{
		return this.currentVersion;
	}
	
	public List<String> getVersionHistory()
	/** 
	 * Get the version history
	 */
	{
		return null;
	}
	
	public boolean addNewVersion()
	/** 
	 * Add a new version to the history list
	 */
	{
		return false;
	}	
}
