package MSP.utils;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Writer {
	String path;
	File file;
//	FileWriter fw;
	BufferedWriter bw;
	boolean start;
	public Writer(String path)
	{
		this.path = path;
		start = true;
		file = new File(path);
		if(!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		try {
			FileWriter fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	public Writer(String path,String code)
	{
		this.path = path;
		start = true;
		file = new File(path);
		if(!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		try {
//			FileWriter fw = new FileWriter(file);
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true),code));		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	public void writeline(String line){
		try {
//			if(line == null)line ="";
			if(this.start)
			{
				start = false;
			}else{
				bw.newLine();
			}
			bw.write(line);
			bw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void writeline(){
		writeline("");
	}

public static void main(String[] args) {
	int max = 11000;
	Writer w = new Writer("Data/MinHash/test/test1.txt");
	for(int i=1000;i<max;i=i+4)w.writeline(""+i);
	Writer w2 = new Writer("Data/MinHash/test/test2.txt");
	for(int i=0;i<max;i=i+6)w2.writeline(""+i);
	System.out.println("over!\nover!");
}
}