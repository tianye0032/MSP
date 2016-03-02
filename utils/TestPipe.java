package MSP.utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;

public class TestPipe {
	public static void main(String[] args) throws IOException{
		  Pipe pipe = Pipe.open();
		  final Pipe.SinkChannel psic = pipe.sink();
		  final Pipe.SinkChannel psic2 = pipe.sink();
		  final Pipe.SourceChannel psoc = pipe.source();
		  
		 
		  Thread tPwriter = new Thread() {
		   public void run() {
		    try {
		     psic.write(ByteBuffer.wrap("Pipe!"
		       .getBytes("utf-16BE")));
//		     psic.write(ByteBuffer.wrap("Pipe".getBytes()));
		    } catch (IOException e) {
		     // TODO Auto-generated catch block
		     e.printStackTrace();
		    }
		   }
		  };
		  Thread tPwriter2 = new Thread() {
			   public void run() {
			    try {
			    
			     psic2.write(ByteBuffer.wrap("Again"
			       .getBytes("utf-16BE")));
			    } catch (IOException e) {
			     // TODO Auto-generated catch block
			     e.printStackTrace();
			    }
			   }
			  };
		 
		  Thread tPreader = new Thread() {
		   public void run() {
		    int bbufferSize = 10;
		    ByteBuffer bbuffer = ByteBuffer.allocate(bbufferSize);
		    try {
		     psoc.read(bbuffer);
		     System.out.println("Content:" + ByteBufferToString(bbuffer));
		     bbuffer = ByteBuffer.allocate(bbufferSize);
		     psoc.read(bbuffer);
		     System.out.println("Content:" + ByteBufferToString(bbuffer));
		    } catch (IOException e) {
		     // TODO Auto-generated catch block
		     e.printStackTrace();
		    }
		   }
		  };
		  tPwriter.start();
		  tPwriter2.start();
		  tPreader.start();
	}
	public static String ByteBufferToString(ByteBuffer content) {
		  if (content == null || content.limit() <= 0
		    || (content.limit() == content.remaining())) {
		   System.out.println("ByteBufferToString");
		   return null;
		  }
		  int contentSize = content.limit() - content.remaining();
		  StringBuffer resultStr = new StringBuffer();
		  for (int i = 0; i < contentSize; i += 2) {
		   resultStr.append(content.getChar(i));
		  }
		  return resultStr.toString();
		 }
}
