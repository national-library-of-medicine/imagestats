package gov.nih.nlm.ceb.lpf.imagestats.shared;

import java.io.IOException;
import java.io.Serializable;

public class ImageRegionModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String FACE = "f";
	public static final String PROFILE = "p";
	public static final String SKIN = "s";

	//public static Pattern pat = Pattern.compile("^\\s*([a-zA-Z]+)\\[(\\d+),(\\d+);(\\d+),(\\d+)\\]\\s*$");

	int x = -1;
	int y = -1;
	int width = 0;
	int height = 0;
	String type = FACE;
	
	/*
	public ImageRegionModel(String regionSpec) {
    Matcher m = pat.matcher(regionSpec);
    if(m.matches()) {
      type = m.group(1);
      x = Integer.parseInt(m.group(2));
      y = Integer.parseInt(m.group(3));
      width = Integer.parseInt(m.group(4));
      height = Integer.parseInt(m.group(5));
    }
    else {
    	throw new IllegalArgumentException("Unrecognized region specification: "+regionSpec);
    }
	}
*/
	public ImageRegionModel() {

	}
	
	
	public ImageRegionModel(String t, int X, int Y, int W, int H) {
		type = t;
		x = X;
		y = Y;
		width = W;
		height = H;
	}

	public String getType() {
		return type;
	}
	
	
	public int getX() {
		return x;
	}
	
	
	public int getY() {
		return y;
	}
	
	
	public int getWidth() {
		return width;
	}
	
	
	public int getHeight() {
		return height;
	}
	
	
	public void setType(String aType) {
		type = aType;
	}
	
	
	public void setX(int X) {
		x = X;
	}
	
	
	public void setY(int Y) {
		y = Y;
	}
	
	
	public void setWidth(int W) {
		width = W;
	}
	
	
	public void setHeight(int H) {
		height = H;
	}

	
	public String toString() {
		StringBuffer sw = new StringBuffer();
	  writeTo(sw);
		return sw.toString();
	}
	

	public void writeTo(StringBuffer w) {
		if(w == null)
			return;
		
		w.append(type).append("[");
		w.append(String.valueOf(x)).append(",");
		w.append(String.valueOf(y)).append(";");
		w.append(String.valueOf(width)).append(",");
		w.append(String.valueOf(height)).append("]");
		
	}
}
