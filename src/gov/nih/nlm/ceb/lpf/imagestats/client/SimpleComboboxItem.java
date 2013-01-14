package gov.nih.nlm.ceb.lpf.imagestats.client;

import java.io.Serializable;

public class SimpleComboboxItem implements Serializable {
  	/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		String label;
  	String value;
  	
  	public SimpleComboboxItem() {
  		
  	}
  	
  	public SimpleComboboxItem(String aLabel, String aValue) {
  		label = aLabel;
  		value = aValue;
  	}
  	
  	public String getLabel() {
  		return label;
  	}
  	
  	public void setLabel(String aLabel) {
  		label = aLabel;
  	}
  	
  	public String getValue() {
  		return value;
  	}
  	
  	public void setValue(String aValue) {
  		value = aValue;
  	}

}
