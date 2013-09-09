package gov.nih.nlm.ceb.lpf.imagestats.client;

import gov.nih.nlm.ceb.lpf.imagestats.shared.ImageRegionModel;

import org.vaadin.gwtgraphics.client.Line;
import org.vaadin.gwtgraphics.client.shape.Rectangle;

public class AnnotationBox extends Rectangle {

	Line bottomEdge = new Line(0,0,0,0);
	Line rightEdge = new Line(0,0,0,0);
	String annType = ImageRegionModel.FACE;
	public AnnotationBox(int x, int y, int width, int height, String anAnnType) {
		super(x, y, width, height);
		bottomEdge = new Line(x, y, x+width, y);
		rightEdge = new Line(x, y, x, y+height);
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
