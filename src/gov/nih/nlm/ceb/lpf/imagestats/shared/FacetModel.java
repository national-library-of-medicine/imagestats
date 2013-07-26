package gov.nih.nlm.ceb.lpf.imagestats.shared;

import java.io.Serializable;

public class FacetModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String field = null;
	String parent = "root";
	String displayLabel = null;
	int count = -1;
	
	
	public FacetModel() {
	}
	
	
	public FacetModel(String fieldName, String label) {
		field = fieldName;
		displayLabel = label;
	}
	
	public FacetModel(String fieldName, String label, String parentName, int facetCount) {
		field = fieldName;
		displayLabel = label;
		parent = parentName;
		count = facetCount;
	}
	public String getField() {
		return field;
	}
	
	public String getDisplayLabel() {
		return displayLabel;
	}
	
	
	
	public String getParent() {
		return parent;
	}
	
	public int getCount() {
		return count;
	}
	
	public void setField(String f) {
		field = f;
	}
	
	public void setDisplayLabel(String f) {
		displayLabel = f;
	}
	
	public void setParent(String f) {
		parent = f;
	}
	
	public void setCount(int c) {
		count = c;
	}
	
	public String getKey() {
		return parent+field;
	}

}
