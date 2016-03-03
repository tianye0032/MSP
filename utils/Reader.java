package MSP.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.CharBuffer;
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
	public char[] getChars(){
		CharBuffer buff = CharBuffer.allocate(10000); // Only process samll files right now!
		int len=0;
		try {
			len = bf.read(buff);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return buff.array();		
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
		
	        
		Reader r = new Reader("data/conf/center.conf");		
		char[] content = r.getChars();
		for(char ch : content)System.out.print(ch);
	
	}
	

}