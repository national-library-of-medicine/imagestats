package gov.nih.nlm.ceb.lpf.imagestats.client;

import gov.nih.nlm.ceb.lpf.imagestats.shared.ImageRegionModel;

import org.vaadin.gwtgraphics.client.shape.Rectangle;

public class AnnotationBox extends Rectangle {

	String annType = ImageRegionModel.FACE;
	public AnnotationBox(int x, int y, int width, int height, String anAnnType) {
		super(x, y, width, height);
		annType = anAnnType;
		// TODO Auto-generated constructor stub
	}
	
	public String getAnnType() {
		return annType;
	}
	
	public void setAnnType(String anAnnType) {
		annType = anAnnType;
	}
}
