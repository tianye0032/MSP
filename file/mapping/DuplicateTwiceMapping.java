/**
 * Author: Shixin Tian
 * Date:2016.1.5
 * A file is just duplicated once in this mapping method.
 */

package msp.file.mapping;

import java.io.File;
import java.io.IOException;

import msp.server.central.Configure;
import msp.utils.FileUtils;

public class DuplicateTwiceMapping implements MappingMethod{
	public boolean merge(String[] source,String target){
		try {
			FileUtils.copyFile(source[0], target);
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
		return true;
	}

}
