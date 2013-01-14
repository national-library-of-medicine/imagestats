package gov.nih.nlm.ceb.lpf.imagestats.shared;

import java.io.Serializable;

public class PLEvent implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String displayName;
	String shortname;
	
	public String getDisplayName() {
		return displayName;
	}
	
	public void setDisplayName(String name) {
		this.displayName = name;
	}
	
	public String getShortname() {
		return shortname;
	}

	public void setShortname(String sname) {
		this.shortname = sname;
	}
	
}
