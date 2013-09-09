package gov.nih.nlm.ceb.lpf.imagestats.client;

import gov.nih.nlm.ceb.lpf.imagestats.client.PLRecordListView.Resources;
import gov.nih.nlm.ceb.lpf.imagestats.client.PLRecordListView.Style;
import gov.nih.nlm.ceb.lpf.imagestats.shared.ISConstants;
import gov.nih.nlm.ceb.lpf.imagestats.shared.ImageRegionModel;

import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.Image;
import org.vaadin.gwtgraphics.client.VectorObject;
//import org.vaadin.gwtgraphics.client.shape.Rectangle;
import org.vaadin.gwtgraphics.client.shape.Text;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;

public class MoveBoundingBoxHandler implements MouseDownHandler,
		MouseUpHandler, MouseMoveHandler, MouseOutHandler {
  int x, y = 0;
  Text debugText = new Text(0,0,"");
  DrawingArea canvas = null;
	final Resources resources;
	final Style style;
  
  int mode = -1; // 0 => move, 1 => resizeX, 2 => resizeY, 3 => resizeXY
  public MoveBoundingBoxHandler(DrawingArea aCanvas) {
    resources = GWT.create(Resources.class);
    resources.css().ensureInjected();
    style = resources.css();
		canvas = aCanvas;
	  canvas.add(debugText);
	}
	
	@Override
	public void onMouseDown(MouseDownEvent event) {
    AnnotationBox rect = (AnnotationBox) event.getSource();
		x = event.getX();
		y = event.getY();
		if(event.getX() >= rect.getWidth() &&
			 event.getY() >= rect.getHeight()) {
		  mode = 3;
		  rect.setStyleName(style.box_resize_corner());
		  //rect.getElement().getStyle().setCursor(Cursor.N_RESIZE);
		}
		else if(event.getY() >= rect.getHeight()) {
			mode = 2;
		  rect.setStyleName(style.box_resize_bottom());
		  //rect.getElement().getStyle().setCursor(Cursor.N_RESIZE);
		}
		else if(event.getX() >= rect.getWidth()) {
			mode = 1;
		  rect.setStyleName(style.box_resize_right());
		  //rect.getElement().getStyle().setCursor(Cursor.E_RESIZE);
		}
		else { 
			mode = 0;
		  rect.setStyleName(style.box_move());
		}
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		mode = -1;
		AnnotationBox rect = (AnnotationBox) event.getSource();
	  rect.setStyleName(style.box_grab());
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		AnnotationBox source = (AnnotationBox) event.getSource();
		
		if(mode == 0) {
			int new_x = source.getX()+event.getX() - x;
			int new_y = source.getY()+event.getY() - y;
			VectorObject v = canvas.getVectorObject(0);
			Image i = (Image)v;
			int limit_x = i.getWidth();
			int limit_y = i.getHeight();
			
			if(new_x >= 0 &&
				 new_y >= 0 &&
				 new_x + source.getWidth() <= limit_x &&
				 new_y + source.getHeight() <= limit_y) {
			  source.setX(new_x);
			  source.setY(new_y);
			  x = event.getX();
			  y = event.getY();
			}
			source.setStyleName(style.box_move());
		}
		else if(mode == 1) {
			if(event.getX()+2 >= ISConstants.MIN_W || 
					source.getAnnType().equalsIgnoreCase(ImageRegionModel.SKIN)) {
			  source.setWidth(event.getX()+2);
			}
			source.setStyleName(style.box_resize_right());
		}
		else if(mode == 2) {
			if(event.getY()+2 >= ISConstants.MIN_H || 
					source.getAnnType().equalsIgnoreCase(ImageRegionModel.SKIN)) {
			  source.setHeight(event.getY()+2);
			}
			source.setStyleName(style.box_resize_bottom());
		}
		else if(mode == 3) {
			if(event.getX()+2 >= ISConstants.MIN_W || 
					source.getAnnType().equalsIgnoreCase(ImageRegionModel.SKIN)) {
			  source.setWidth(event.getX()+2);
			}
			if(event.getY()+2 >= ISConstants.MIN_H || 
					source.getAnnType().equalsIgnoreCase(ImageRegionModel.SKIN)) {
			  source.setHeight(event.getY()+2);
			}
			source.setStyleName(style.box_resize_corner());
		}
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		mode = -1;
		AnnotationBox rect = (AnnotationBox) event.getSource();
	  rect.setStyleName(style.box_grab());
	}

}
