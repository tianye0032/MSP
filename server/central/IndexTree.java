package MSP.server.central;

import java.util.Date;
import java.util.List;

public class IndexTree {
	
	public IndexTree(){
		
	}
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
		return null;
	}
	
	public String getVersion(String file)
	/** 
	 * Get the version of a given file
	 */
	{
		return null;
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
