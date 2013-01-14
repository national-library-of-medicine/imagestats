package gov.nih.nlm.ceb.lpf.imagestats.client;

//import gov.nih.nlm.ceb.lpf.imagestats.client.PLRecordListView.Resources;
//import gov.nih.nlm.ceb.lpf.imagestats.client.PLRecordListView.Style;

import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.Shape;
import org.vaadin.gwtgraphics.client.VectorObject;
//import org.vaadin.gwtgraphics.client.shape.Rectangle;
import org.vaadin.gwtgraphics.client.shape.Text;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.CssResource;

  public class BoundingBoxHandler implements ClickHandler,  MouseOverHandler, MouseOutHandler, KeyPressHandler {
    boolean started = false;
    //boolean done = false;
//  	Rectangle rect;
//  	RectMouseHandler(Rectangle aRect) {
//  		rect = aRect;
//  	}
    Shape activeBox = null;
    Text debugText = new Text(0,270,"");
    int x, y = 0;
    DrawingArea canvas = null;  	
  	final Resources resources = GWT.create(Resources.class);
  	final Style style = resources.css();
    interface Resources extends ClientBundle {
      @Source("EditDialogBox.css")
      Style css();
    }
    interface Style  extends CssResource {
    String box_move();
    String box_grab();
    String box_resize_corner();
    String box_resize_bottom();
    String box_resize_right();
    }
    public BoundingBoxHandler(DrawingArea aCanvas) {
    	canvas = aCanvas;
    	canvas.add(debugText);
      style.ensureInjected();
    }
		
			//@Override
			public void onMouseDown(MouseDownEvent event) {
	      AnnotationBox rect = (AnnotationBox) event.getSource();
				if(!started) {
				//rect.setX(event.getX());
				//rect.setY(event.getY());
				//rect.setHeight(0);
				//rect.setWidth(0);
				x = rect.getX();
				y = rect.getY();
				started = true;
				//done = false;
				}
			}

    
			//@Override
			public void onMouseMove(MouseMoveEvent event) {
				AnnotationBox source = (AnnotationBox) event.getSource();
				if(started) {
					int ex = event.getX();
					int ey = event.getY();
					x = (ex);
					y = (ey);
					source.setX(x);
					source.setX(y);
				}
			}

			//@Override
			public void onMouseUp(MouseUpEvent event) {
				/*
				if(started) {
				  rect.setWidth(event.getX() - rect.getX());
				  rect.setHeight(event.getY() - rect.getY());
				}
				*/
				started = false;

			}
	
			@Override
			public void onClick(ClickEvent event) {
				Shape source = (Shape) event.getSource();
				if(source.equals(activeBox)) {
					if(event.isControlKeyDown() && event.isAltKeyDown()) {
						canvas.remove(source);
						activeBox = null;
					}
				}
				else {
					activeBox = source;
				}
				
				if(activeBox != null) {
          highLight(canvas, activeBox);
				}
			}
/*		
			public void resetRect(ClickEvent event) {
	      Rectangle rect = (Rectangle) event.getSource();
				rect.setX(0);
				rect.setY(0);
				rect.setHeight(0);
				rect.setWidth(0);
				started = false;
			}
*/
			@Override
			public void onMouseOut(MouseOutEvent event) {
				started = false;
				Shape source = (Shape) event.getSource();
				source.setFillOpacity(0);
				//source.setStrokeWidth(1);
			}


			@Override
			public void onMouseOver(MouseOverEvent event) {
				Shape source = (Shape) event.getSource();
				source.setFillOpacity(0.2);
			  
			  //debugText.setText(source.getStyleName());
			}

			public static void highLight(DrawingArea aCanvas, VectorObject frontObj) {
				if(frontObj instanceof Shape) {
					for(int i = 0; i < aCanvas.getVectorObjectCount(); i++) {
						VectorObject vo = aCanvas.getVectorObject(i);
						if(vo instanceof Shape) {
							((Shape) vo).setStrokeWidth(1);
						}
					}
					((Shape)frontObj).setStrokeWidth(3);
					aCanvas.bringToFront(frontObj);
				}
			}

			@Override
			public void onKeyPress(KeyPressEvent event) {
				Shape source = (Shape)event.getSource();
				if(source.equals(activeBox)) {
				  if(event.getCharCode() == KeyCodes.KEY_DELETE) {
					  canvas.remove(source);
				  }
				}
			}
  }

