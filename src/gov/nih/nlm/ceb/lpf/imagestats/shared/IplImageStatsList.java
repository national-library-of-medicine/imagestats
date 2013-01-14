package gov.nih.nlm.ceb.lpf.imagestats.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class IplImageStatsList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int count = -1;
	private List<IplImageStats> iplImageSet = null;
	
	public void add(IplImageStats imageStats) {
		if(iplImageSet == null) {
			iplImageSet = new ArrayList<IplImageStats>();
		}
		iplImageSet.add(imageStats);
	}
	
	public void setCount(int c) {
		count = c;
	}
	
	public void setIplImageSet(List<IplImageStats> list) {
		iplImageSet = list;
	}
	
	public int getCount() {
		return count;
	}
	
	public List<IplImageStats> getIplImageSet() {
		return iplImageSet;
	}
}
