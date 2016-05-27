package MSP.file.mapping.parity.compute;

import java.util.Comparator;

public class CompareServ implements Comparator<Server>{
		
	@Override
	public int compare(Server o1, Server o2) {
		if (o1.getParityData() - o2.getParityData() > 0l) {
			return 1;
		} else if (o1.getParityData() - o2.getParityData() < 0l) {
			return -1;
		} else {
			return 0;
		}				
	}
}
