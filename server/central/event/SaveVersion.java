package MSP.server.central.event;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import MSP.utils.FileUtils;

public class SaveVersion implements Serializable {
	private String temFileAdd = "data/temp/indexTree";
	
	public void saveVersion(IndexTree index) {
		ObjectOutputStream os = null;
		try {
			File file = new File(temFileAdd);
			FileUtils.createFile(file);
			os = new ObjectOutputStream(new FileOutputStream(file));
			os.writeObject(index);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}        
	}
	
	public IndexTree recoverVersion() {
		ObjectInputStream is = null;
		IndexTree index = null;
		try {
			File file = new File(temFileAdd);
			if (!file.exists()) {
				return null;
			}
			is = new ObjectInputStream(new FileInputStream(temFileAdd));
			index = (IndexTree) is.readObject(); 			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {  
            e.printStackTrace();  
        } finally {
			try {
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return index;
	}
	
	
	public static void main(String[] args) {
		SaveVersion save = new SaveVersion();
		IndexTree index = new IndexTree();
		index.addNewVersion(new Version("/data/test/box0/1.txt", "1234567890"));
		save.saveVersion(index);		
		
		IndexTree index2 = save.recoverVersion();
		System.out.print(index2.getLastVersion("/data/test/box0/1.txt"));
	}
}
