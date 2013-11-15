package gov.nih.nlm.ceb.lpf.imagestats.client;

import gov.nih.nlm.ceb.lpf.imagestats.shared.ISConstants;
import gov.nih.nlm.ceb.lpf.imagestats.shared.ImageRegionModel;

import org.vaadin.gwtgraphics.client.DrawingArea;
//import org.vaadin.gwtgraphics.client.shape.Rectangle;

import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;


public class DrawBoundingBoxHandler implements MouseDownHandler,
		MouseMoveHandler, MouseUpHandler, MouseOutHandler {

	boolean started = false;

	AnnotationBox bbox = null;
	DrawingArea canvas = null;
	TextButton typeHolder = null;
	BoxSizingHandler sizeHandler = new BoxSizingHandler(this);
	
	
	public DrawBoundingBoxHandler(DrawingArea aCanvas, TextButton aType) {
		canvas = aCanvas;
		typeHolder = aType;
	}
	
	@Override
	public void onMouseDown(MouseDownEvent event) {
			if(!started) {
	      String t = typeHolder.getValue();
				bbox = new AnnotationBox(event.getX(),event.getY(),0,0,t);
	      bbox.setFillOpacity(0);
	      bbox.setStrokeWidth(1);
	      if(t == null) {
	      	MessageBox m = new MessageBox("Select box type before drawing.");
	      	m.show();
	      	return;
	      }
	      if(t.equals(ImageRegionModel.FACE) ||
	    		  t.equals(ImageRegionModel.MOUTH)) {
	      	bbox.setStrokeColor("yellow");
        }
	      else if(t.equals(ImageRegionModel.PROFILE) ||
	    		  t.equals(ImageRegionModel.EYE)) {
	      	bbox.setStrokeColor("red");
	      }
	      else if(t.equals(ImageRegionModel.SKIN) ||
	    		  t.equals(ImageRegionModel.NOSE)) {
	      	bbox.setStrokeColor("green");
	      }
	      else if(t.equals(ImageRegionModel.EAR)) {
	      	bbox.setStrokeColor("purple");
	      }
	      else {
	      	return;
	      }
	      
	      bbox.addMouseMoveHandler(sizeHandler);
	      bbox.addMouseUpHandler(sizeHandler);
	      canvas.add(bbox);
	      
	      BoundingBoxHandler.highLight(canvas, bbox);
				started = true;
			}
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
			if (started) {
				int w = event.getX() - bbox.getX();
				bbox.setWidth(w);
				int h = event.getY() - bbox.getY();
				bbox.setHeight(h);
			}
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		endBoxHandling();
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		endBoxHandling();
	}
	
	private void endBoxHandling() {

		// if(started) {
		started = false;
		if (bbox != null) {
			if(bbox.getWidth() <= 0 || bbox.getHeight() <= 0) {
				canvas.remove(bbox);
				bbox = null;
			}
			else {
				AnnotationBox finalBox = new AnnotationBox(bbox.getX(), bbox.getY(),
						bbox.getWidth(), bbox.getHeight(), bbox.getAnnType());
				finalBox.setFillOpacity(bbox.getFillOpacity());
				finalBox.setStrokeColor(bbox.getStrokeColor());

				BoundingBoxHandler box_handler = new BoundingBoxHandler(canvas);
				MoveBoundingBoxHandler move_handler = new MoveBoundingBoxHandler(canvas);

				finalBox.addClickHandler(box_handler);
				finalBox.addMouseDownHandler(move_handler);
				finalBox.addMouseMoveHandler(move_handler);
				finalBox.addMouseUpHandler(move_handler);
				finalBox.addMouseOverHandler(box_handler);
				finalBox.addMouseOutHandler(box_handler);
				finalBox.addHandler(box_handler, KeyPressEvent.getType());

				canvas.remove(bbox);
				canvas.add(finalBox);
				BoundingBoxHandler.highLight(canvas, finalBox);
				bbox = null;
			}
		}
		// }
	}
	
	class BoxSizingHandler implements MouseMoveHandler, MouseUpHandler {

		DrawBoundingBoxHandler parentHandler = null;
		
		
		BoxSizingHandler(DrawBoundingBoxHandler aHandler) {
			parentHandler = aHandler;
		}
		
		
		@Override
		public void onMouseMove(MouseMoveEvent event) {
			AnnotationBox source = (AnnotationBox)event.getSource();
			int h = 16;
			int w = 16;		
			if(event.getX() > ISConstants.MIN_W) {
				w = event.getX();
			}
			else if(source.getAnnType().equalsIgnoreCase(ImageRegionModel.SKIN)){
				w = event.getX();
			}
			
			if(event.getY() > ISConstants.MIN_H) {
				h = event.getY();
			}
			else if(source.getAnnType().equalsIgnoreCase(ImageRegionModel.SKIN)){
				h = event.getY();
			}
			
		  source.setWidth(w);
		  source.setHeight(h);
		  
		}

		
		@Override
		public void onMouseUp(MouseUpEvent event) {
			// TODO Auto-generated method stub
			parentHandler.endBoxHandling();
		}
	}
}
