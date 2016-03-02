package MSP.utils;

import java.nio.ByteBuffer;

public class StringUtils {
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
