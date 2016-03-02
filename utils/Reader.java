package msp.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class Reader {
	String path;
	File file;
	BufferedReader bf;
	public Reader(String path){
		this.path = path;
		file = new File(this.path);
		try {
			bf = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	public Reader(File file){
		this.path = file.getAbsolutePath();
		this.file = file;
		try {
			bf = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	public Reader(String path,String code){
		this.path = path;
		file = new File(this.path);
		try {
				bf = new BufferedReader(new InputStreamReader(new FileInputStream(file),code));		

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
	}
		public boolean ready(){
		try {
			return this.bf.ready();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	public String getline(){
		try {
			return this.bf.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public String get(){		
			String temp ="";
			while(this.ready())temp= temp+this.getline();
			return temp;		
	}
	public static void main(String[] args) {
//			String text = "A sep Text sep With sep Many sep Separators";
//	        
//	        String patternString = "sep";
//	        Pattern pattern = Pattern.compile(patternString);
//	        
//	        Matcher matcher = pattern.matcher(text);
//	        matcher.
//	        
//	        String[] split = pattern.split(text);
//	        
//	        System.out.println("split.length = " + split.length);
//	        
//	        for(String element : split){
//	            
//	        	System.out.println("element = " + element);
//	        }
		
		Pattern p=Pattern.compile("\\(.*?\\)"); 
		
	        
		Reader r = new Reader("data/key");		
		while(r.ready()){
			String line = r.getline();
			if(line!=null && !line.isEmpty()){
				Matcher m=p.matcher(line); 
				
				while(m.find()) {
					 String seg = m.group();
					 if(!seg.isEmpty()){
						Double x = Double.parseDouble(seg.substring(1, seg.indexOf(",")));
						System.out.print(x+" ");
						Double y = Double.parseDouble(seg.substring(seg.indexOf(",")+2,seg.lastIndexOf("")-1));
						System.out.println(y);
					 }
				 } 
			} 
		}
	
	}
	

}