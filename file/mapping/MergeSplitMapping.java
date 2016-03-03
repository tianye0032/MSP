package MSP.file.mapping;

import java.io.IOException;

import MSP.utils.FileUtils;

public class MergeSplitMapping implements MappingMethod {

	private final String name = "MergeSplitMapping";

	@Override
	public boolean isAuthentic(String[] target) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean merge(String[] source, String target) {
		try {
			FileUtils.mergeFile(source, target);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean split(String source, String[] target) {
		try {
			FileUtils.splitFile(source, target);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}
	

}
