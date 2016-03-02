/**
 * Author: Shixin Tian
 * Date:2016.1.5
 * A file is just duplicated once in this mapping method.
 */

package MSP.file.mapping;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import MSP.server.central.Configure;
import MSP.utils.FileUtils;
import MSP.utils.Reader;

public class DuplicateMapping implements MappingMethod{
	public boolean merge(String[] source,String target){
		
		try {
			if(this.isAuthentic(source))
				FileUtils.copyFile(source[0], target);
			else{
				String[] texts = new String[source.length];
				HashMap<String,Integer> map = new HashMap<String,Integer>();
				for(int i=0;i<source.length;i++){
					Reader reader = new Reader(source[i]);
					texts[i] = reader.get();
					if(!map.containsKey(texts[i])){
						map.put(texts[i], 1);
					}else{
						map.put(texts[i], 1+map.get(texts[i]));
					}
				}
				int max = 0,index=0;
				String authenticText=null;
				for(Entry<String, Integer> entry:map.entrySet()){
					if(entry.getValue()>max){
						max = entry.getValue();
						authenticText = entry.getKey();
					}
				}
				if(max>source.length/2){
					System.out.println("Inconsistency Would Be Corrected!");
					for(int i=0;i<source.length;i++){
						if(!texts[i].equals(authenticText)){
							System.out.println("Data from server "+ (i+1)+ " is Not Authentic");
						}else index = i;
					}
					
					FileUtils.copyFile(source[index], target);
				}else{
					System.out.println("Inconsistency Cannot Be Corrected!");
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public boolean split(String source, String[] target){
		
			try {
				for(String file : target)
					FileUtils.copyFile(source, file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		return true;
	}
	@Override
	public boolean isAuthentic(String[] target) {
		// TODO Auto-generated method stub
		String[] texts = new String[target.length];
		
		for(int i=0;i<target.length;i++){
			Reader reader = new Reader(target[i]);
			texts[i] = reader.get();
			if(i>0)if(!texts[i].equals(texts[i-1])){
				System.out.println("Data Inconsistency is Detected!");
				return false;
			}
		}		
		return true;
	}
}
