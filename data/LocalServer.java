package MSP.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LocalServer {
	public List<Long> data;
	public LocalServer(String file){
		data = new ArrayList<Long>();
		try {
			BufferedReader bf = new BufferedReader(new FileReader(new File(file)));
			while(bf.ready()){
				String line = bf.readLine();
				if (!line.isEmpty())
					{
					Long num = Long.parseLong(line.trim());
						data.add(num);
					}
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public List<Long> processQuery(Long lower, Long upper)
	//The first element is the index of the first number which is less than lower bound
	{
		List<Long> ret = new ArrayList<Long>();
		int ind = Collections.binarySearch(data, lower);
		if (ind<0)ind = -1 -ind;
		while(ind>0 && data.get(ind)>=lower){
			ind --;
		}
		ret.add((long)ind);
		while(ind < data.size()-1 && data.get(ind) <= upper){
			ret.add(data.get(ind));
			ind++;
		}
		ret.add(data.get(ind));
		return ret;
	}
	
	public static void main(String[] args){
		LocalServer ls = new LocalServer("data/LocalData1");		
		long lower = 5;
		long upper = 6;
		List<Long> ans = ls.processQuery(lower, upper);
		for (long x: ans){
			System.out.println(x);
		}
		
	}
}
