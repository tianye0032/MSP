package MSP.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataOwner {
	private List<Integer> data;
	
	private TableMapping tableMapping = new TableMapping();
	public DataOwner(){
		
	}
	public DataOwner(String file){
		data = new ArrayList<Integer>();
		data.add(Integer.MIN_VALUE);
		try {
			BufferedReader bf = new BufferedReader(new FileReader(new File(file)));
			while(bf.ready()){
				String line = bf.readLine();
				if (!line.isEmpty())
					{
						Integer num = Integer.parseInt(line);
						data.add(num);
					}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		data.add(Integer.MAX_VALUE);
		Collections.sort(data);
	}
	
	// distribute data to local servers
	public void distribute(String source, String[] target){
		tableMapping.split(source, target);
	}

	public List<Integer> processQuery(int lower,int upper){
		List<Integer> ret = new ArrayList<Integer>();
		int ind = Collections.binarySearch(data, lower);
		if (ind<0)ind = -1 -ind;
		while(ind>0 && data.get(ind)>=lower){
			ind --;
		}
		ind ++;
		while(ind<data.size()-1 && data.get(ind)<=upper){
			ret.add(data.get(ind));
			ind ++;
		}		
		return ret;
	}
	public static void main(String[] args){
		DataOwner owner  = new DataOwner("data/OriginalData"); 
		for (int x:owner.processQuery(6, 7)){
			System.out.println(x);
		}
	}

}
