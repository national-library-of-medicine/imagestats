package gov.nih.nlm.ceb.lpf.imagestats.client;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.Image;
import org.vaadin.gwtgraphics.client.VectorObject;
import org.vaadin.gwtgraphics.client.shape.Rectangle;
//import org.vaadin.gwtgraphics.client.shape.Rectangle;

import gov.nih.nlm.ceb.lpf.imagestats.shared.ImageDetails;
import gov.nih.nlm.ceb.lpf.imagestats.shared.ImageRegionModel;
import gov.nih.nlm.ceb.lpf.imagestats.shared.GroundTruthRecord;
import gov.nih.nlm.ceb.lpf.imagestats.shared.PLRecord;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.core.client.util.Padding;
import com.sencha.gxt.core.client.util.ToggleGroup;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.Radio;
 

public class ImageEditContainer implements
IsWidget {

  private DetailRenderer renderer;

  private ImageDetails imageDetailsRecord = null;
  private GroundTruthRecord imageStats = null;
  
  private ContentPanel lccenter;
  private ContentPanel lcsouth;
  private TextButton typeHolder = new TextButton();
  private TextButton saveFinalButton = null;
  private TextButton saveInitialButton = null;
  //private TextButton restoreFaceFinderButton = null;
  private TextButton restoreInitialButton = null;
  private TextButton restoreFinalButton = null;
  private TextButton nextButton = null;
  private TextButton prevButton = null;
  
  private Radio fradio = null;
  private Radio pradio = null;
  private Radio sradio = null;
  private Radio aradio = null;
  ToggleGroup toggle = new ToggleGroup();
  ScrollPanel container = new ScrollPanel();

  static int IMAGE_FRAME_WIDTH = 300;
  static int IMAGE_FRAME_HEIGHT = 300;
  final static String MODE_ALL = "all";
  
  HTML details = new HTML();
	DrawingArea canvas = new DrawingArea(IMAGE_FRAME_WIDTH, IMAGE_FRAME_HEIGHT);
//	Group boxGroup = new Group();
  Image sourceImage = new Image(0, 0, IMAGE_FRAME_WIDTH, IMAGE_FRAME_HEIGHT, "");
  //ImageStats mainPanel = null;
  ImageStatsServiceAsync imagestatsService = null;
  final CheckBox authorTypeHolder = new CheckBox();
  PLRecordListView resultsView = null;
  ValueUpdater<PLRecord> listViewCellUpdater = null;
  private Cell.Context cellContext = null;


  interface DetailRenderer extends XTemplates {
    @XTemplate(source = "ImageDetails.html")
    public SafeHtml render(ImageDetails record);
  }
  
  public ImageEditContainer(PLRecordListView aResultsView) {
  	//mainPanel = theMainPanel;
  	resultsView = aResultsView;
  	imagestatsService = resultsView.getImageStatsService();
  }

	@Override
	public Widget asWidget() {
    renderer = GWT.create(DetailRenderer.class);
    container = new ScrollPanel();
    container.getElement().getStyle().setMargin(10, Unit.PX);
 
    ContentPanel panel = new ContentPanel();
    panel.setHeaderVisible(false);
    panel.setPixelSize(600, 500);
 
    BorderLayoutContainer border = new BorderLayoutContainer();
    panel.setWidget(border);
 
    VerticalPanel lcnorth = new VerticalPanel();
    //lcnorth.setHeaderVisible(false);
    HBoxLayoutContainer buttonBar = new HBoxLayoutContainer();
    HBoxLayoutContainer optionsBar = new HBoxLayoutContainer();
    
    buttonBar.setPadding(new Padding(2));
    optionsBar.setPadding(new Padding(2));
    lcnorth.add(buttonBar);
    lcnorth.add(optionsBar);
  
    BorderLayoutData north = new BorderLayoutData(60);
    north.setMargins(new Margins(2));
    // west.setSplit(true);
 
    border.setNorthWidget(lcnorth, north);
    lcsouth = new ContentPanel();
    lcsouth.setHeaderVisible(false);
    lcsouth.add(details);
    BorderLayoutData south = new BorderLayoutData(115);
    south.setMargins(new Margins(5));
    border.setSouthWidget(lcsouth, south);
 
    lccenter = new ContentPanel();
    lccenter.setHeaderVisible(false);

    
  	DrawBoundingBoxHandler draw_handler = new DrawBoundingBoxHandler(canvas, typeHolder);
    sourceImage.addMouseDownHandler(draw_handler);
    sourceImage.addMouseMoveHandler(draw_handler);
    sourceImage.addMouseUpHandler(draw_handler);
    //sourceImage.addMouseOutHandler(draw_handler);

    Element el = sourceImage.getElement();
    el.setAttribute("preserveAspectRatio", "xMinYMin meet");
    canvas.add(sourceImage);
    lccenter.add(canvas);
    lccenter.add(details);
 
    MarginData center = new MarginData(new Margins(5));
 
    border.setCenterWidget(lccenter, center);
    
    
    BoxLayoutData vBoxData = new BoxLayoutData(new Margins(2, 2, 2, 2));
    //vBoxData.setFlex(1);
 
    /*restoreFaceFinderButton = createButton("Restore FaceFinder", new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				resetEdits(false);
		  	//addFaceMatcherBoxes(imageDetailsRecord.getUrl());
			}
 
    });*/
 
    //buttonBar.add(restoreFaceFinderButton, vBoxData);

    restoreInitialButton = createButton("Restore Initial", new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				authorTypeHolder.setValue(false, false);
				restoreEditsFromDB();
			}
 
    });
    
    //buttonBar.add(restoreInitialButton, vBoxData);
 
    restoreFinalButton = createButton("Restore", new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				authorTypeHolder.setValue(true, false);
				restoreEditsFromDB();
			}
    });
    
    buttonBar.add(restoreFinalButton, vBoxData);

    saveInitialButton = createButton("Save Initial", new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				authorTypeHolder.setValue(true, false);
				saveEditsToDB();
			}
    });
    
    // TODO Kludge - Disable initial save for gciusers. 
    saveInitialButton.disable();

    //buttonBar.add(saveInitialButton, vBoxData);

    saveFinalButton = createButton("Save", new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				authorTypeHolder.setValue(false, false);
				saveEditsToDB();
			}
 
    });
    buttonBar.add(saveFinalButton, vBoxData);

    buttonBar.add(createButton("Clear all boxes", new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				resetEdits(true);
			}
 
    }), vBoxData);
 

    fradio = new Radio();
    fradio.setBoxLabel("<span style=\"background-color: yellow\">face</span>");
    fradio.setName(ImageRegionModel.FACE);
    //fradio.setValue(true);
    fradio.setToolTip("Yellow boxes");
		//typeHolder.setValue(fradio.getName()); 
 
    pradio = new Radio();
    pradio.setBoxLabel("<span style=\"background-color: red; color: white\">profile</span>");
    pradio.setName(ImageRegionModel.PROFILE);
    pradio.setToolTip("Red boxes");
    sradio = new Radio();
    //sradio.setStyleName(style.);
    sradio.setBoxLabel("<span style=\"background-color: cyan\">skin</span>");
    sradio.setName(ImageRegionModel.SKIN);
    sradio.setToolTip("Cyan boxes");
 
    aradio = new Radio();
    aradio.setBoxLabel("show all");
    aradio.setName(MODE_ALL);
    aradio.setToolTip("Show all boxes.");
 
    HorizontalPanel hp = new HorizontalPanel();
    hp.add(fradio);
    hp.add(pradio);
    hp.add(sradio);
    hp.add(aradio);
 
    optionsBar.add(new FieldLabel(hp, "Mode"));
 
    // we can set name on radios or use toggle group
    toggle.add(fradio);
    toggle.add(pradio);
    toggle.add(sradio);
    toggle.add(aradio);

    toggle.addValueChangeHandler(new ValueChangeHandler<HasValue<Boolean>>() {
 
      @Override
      public void onValueChange(ValueChangeEvent<HasValue<Boolean>> event) {
        ToggleGroup group = (ToggleGroup)event.getSource();
        Radio radio = (Radio)group.getValue();
				typeHolder.setValue(radio.getName()); 
				displayDesiredBoxes();
      }
    });
    
    toggle.setValue(fradio);
    prevButton = createButton("Previous", new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				ListView<PLRecord, PLRecord> view = resultsView.getListView();
				if(cellContext.getIndex() > 0) {
					authorTypeHolder.setValue(true, false);
					  saveEditsToDB();
				  XElement el = view.getElement(cellContext.getIndex() - 1);
				  fireDblClick(el);
				  
				}
				/*
				else {
					resultsView.getPagingToolBar().previous();
					view.refresh();
				  XElement el = view.getElement(view.getItemCount() - 1); // Last element of the page
				  fireDblClick(el);
				}
				*/
			}
    });

    nextButton = createButton("Next", new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				ListView<PLRecord, PLRecord> view = resultsView.getListView();
				if(cellContext.getIndex() < (view.getItemCount() - 1)) {
					authorTypeHolder.setValue(true, false);
					  saveEditsToDB();
				  XElement el = view.getElement(cellContext.getIndex() + 1);
				  fireDblClick(el);
				}
				/*
				else {
					resultsView.getPagingToolBar().next();
					view.refresh();
				  XElement el = view.getElement(0); // First element of the page
				  fireDblClick(el);
				}
				*/
			}
    });

    hp = new HorizontalPanel();
    hp.add(prevButton);
    hp.add(nextButton);
    optionsBar.add(new FieldLabel(hp, ""));

    container.add(panel);
    return container;
	}


	void enablePrevNextButtons() {
		ListView<PLRecord, PLRecord> view = resultsView.getListView();
	  if(cellContext.getIndex() < (view.getItemCount() - 1)) {
	  	nextButton.enable();
	  }
	  else {
	  	nextButton.disable();
	  }
	  
	  if(cellContext.getIndex() <= 0) {
	  	prevButton.disable();
	  }
	  else {
	  	prevButton.enable();
	  }
	}
	
  public void updateContainer(Cell.Context context, PLRecord plrecord, int itemIndex, ValueUpdater<PLRecord> updater) {
  	if(plrecord == null) {
  		lccenter.clear();
  		imageDetailsRecord = null;
  		return;
  	}
  	cellContext = context;
  	listViewCellUpdater = updater;
   	imageDetailsRecord = new ImageDetails(plrecord, itemIndex);
  	imageDetailsRecord.setCanvas(canvas);
  	// A hack to avoid drawing default boxes for skin tasks for GCI program
  	if(plrecord.getEventShortName().matches(".*skin.*")) {
  	  resetEdits(true);
  	}
  	else {
  		resetEdits(false);
  	}
  	enablePrevNextButtons();
    if(imageDetailsRecord.getEventShortName().matches(".*skin.*")) {
    	//restoreFaceFinderButton.disable();
    	//sradio.setValue(true);
    	toggle.setValue(sradio);
    	//fradio.setValue(false);
    	typeHolder.setValue(sradio.getName());
    }
    else {
    	//restoreFaceFinderButton.enable();
    	//fradio.setValue(true);
    	toggle.setValue(fradio);
    	//sradio.setValue(false);
    	typeHolder.setValue(fradio.getName());
    }
  	
    lccenter.clear();
    lccenter.add(canvas);

    SafeHtml htmlRender = renderer.render(imageDetailsRecord);
    details.setHTML(htmlRender.asString());
    lcsouth.add(details);
    lccenter.forceLayout();

    container.setWidth(String.valueOf(sourceImage.getWidth()));
    container.setHeight(String.valueOf(sourceImage.getHeight()));
    
    updater.update(plrecord);
  }

  public void updateDetails(ImageDetails rec) {
    SafeHtml htmlRender = renderer.render(rec);
    details.setHTML(htmlRender.asString());
  }
  
  public void saveEditsToDB() {
  	int editMode = 0;
  	if(authorTypeHolder.getValue()) {
  		editMode = 2;
  	}
  	else {
  		editMode = 3;
  	}
  	BigDecimal nFactor = getNormalizationFactor(imageDetailsRecord.getImageWidth(), imageDetailsRecord.getImageHeight());
  	ImageRegionModel[] regions = getModelsFromCanvas(nFactor, canvas);
  	
  	imagestatsService.saveRegionsToDB(imageDetailsRecord.get_image_id(), editMode, regions, new AsyncCallback<Void>() {

		  @Override
		  public void onFailure(Throwable caught) {
			  MessageBox m = new MessageBox(caught.getMessage());
			  m.show();
		  }


		  @Override
		  public void onSuccess(Void result) {
		  	getImageStatsRecord();
		  }
    });
  }
  
  /*
   * clean=true indicates, clear all the boxes and keep it that way.
   * clean=false indicates clear all boxes and redraw default boxes.
   */
  public void resetEdits(boolean clean) {
  	canvas.clear();
  	sourceImage.setHref(imageDetailsRecord.getUrl());
  	canvas.add(sourceImage);
  	getImageStatsRecord();

  	if(!clean) {
  	  //addFaceMatcherBoxes(imageDetailsRecord.getUrl());
  	}
  }
  
  public void restoreEditsFromDB() {
  	resetEdits(true);
  	addDBBoxes(imageDetailsRecord.get_image_id());
  }
  

  void getImageStatsRecord() {
  	imagestatsService.getGroundTruthFromDB(imageDetailsRecord.get_image_id(), new AsyncCallback<GroundTruthRecord>() {

		  @Override
		  public void onFailure(Throwable caught) {
			  MessageBox m = new MessageBox(caught.getMessage());
			  m.show();
		  }


		  @Override
		  public void onSuccess(GroundTruthRecord result) {
        imageStats = result;
        updatePLRecord(result);
        enableRestoreButtons();
		  	listViewCellUpdater.update(imageDetailsRecord);
      }
    });  
  }
 
  void updatePLRecord(GroundTruthRecord gtRec) {
  	if(gtRec != null && (imageDetailsRecord.get_image_id()).equalsIgnoreCase(gtRec.get_image_id())) {
  		imageDetailsRecord.set_final_regions(gtRec.get_final_regions());
   		imageDetailsRecord.set_initial_regions(gtRec.get_initial_regions());
   		imageDetailsRecord.set_gt_initial_person(gtRec.get_gt_initial_person());
   		imageDetailsRecord.set_gt_final_person(gtRec.get_gt_final_person());
   		imageDetailsRecord.set_initial_update_time(gtRec.get_initial_update_time());
   		imageDetailsRecord.set_final_update_time(gtRec.get_final_update_time());
   		imageDetailsRecord.setGroundTruthStatus(gtRec.get_grounfTruthStatus());
   		updateDetails(imageDetailsRecord);
  	}
  }
  
  void enableRestoreButtons() {
    if(imageStats == null) {
    	restoreInitialButton.disable();
    	restoreFinalButton.disable();
    }
    else {
    	ImageRegionModel[] regions = imageStats.get_final_regions();
    	if(regions == null || regions.length == 0) {
      	restoreFinalButton.disable();
    	}
    	else {
      	restoreFinalButton.enable();
    	}
    	regions = imageStats.get_initial_regions();
    	if(regions == null || regions.length == 0) {
      	restoreInitialButton.disable();
    	}
    	else {
    		restoreInitialButton.enable();
    	}
    }
    
    // Disable skin annotations for black and white pictures. 
    if(imageDetailsRecord.getColorChannels() < 3) {
    	sradio.setValue(false); // If set, reset skin radio button
    	sradio.disable(); 
    	MessageBox m = new MessageBox("No skin annotations are required on this pictures");
      m.show();
    }
    else {
    	sradio.enable();
    }
  }
  
  void addDBBoxes(String image_id) {
  	
  	imagestatsService.getGroundTruthFromDB(image_id, new AsyncCallback<GroundTruthRecord>() {

			  @Override
			  public void onFailure(Throwable caught) {
				  MessageBox m = new MessageBox(caught.getMessage());
				  m.show();
			  }


			  @Override
			  public void onSuccess(GroundTruthRecord result) {
			  	imageStats = result;
	        enableRestoreButtons();
	        
			  	if(authorTypeHolder.getValue()) {
			  	  drawBoxes(imageStats.get_final_regions());
			  	}
			  	else {
			  	  drawBoxes(imageStats.get_initial_regions());
			  	}
			  }
      });
  }
  

  
  /*void addFaceMatcherBoxes(String url) {
  	
    if(url.length() > 0) {
    	imagestatsService.getFaceMatchRegions(url, new AsyncCallback<ImageRegionModel[]>() {

			  @Override
			  public void onFailure(Throwable caught) {
				  MessageBox m = new MessageBox(caught.getMessage());
				  m.show();
			  }


			  @Override
			  public void onSuccess(ImageRegionModel[] result) {
          drawBoxes(result);
			  }
      });
    }
  }*/
  

	void drawBoxes(ImageRegionModel[] models) {
		BigDecimal nFactor = getNormalizationFactor(
				imageDetailsRecord.getImageWidth(), imageDetailsRecord.getImageHeight());
		for (ImageRegionModel region : models) {
			if (region.getType().equals(ImageRegionModel.FACE)
					|| region.getType().equals(ImageRegionModel.PROFILE)
					|| region.getType().equals(ImageRegionModel.SKIN)) {
				AnnotationBox box = new AnnotationBox(
						(new BigDecimal(region.getX()).divide(nFactor, 4,
								RoundingMode.HALF_UP)).intValue(),
						(new BigDecimal(region.getY()).divide(nFactor, 4,
								RoundingMode.HALF_UP)).intValue(),
						(new BigDecimal(region.getWidth()).divide(nFactor, 4,
								RoundingMode.HALF_UP)).intValue(),
						(new BigDecimal(region.getHeight()).divide(nFactor, 4,
								RoundingMode.HALF_UP)).intValue(), region.getType());
				box.setFillOpacity(0);
				box.setStrokeWidth(1);
				if (region.getType().equals(ImageRegionModel.FACE)) {
					box.setStrokeColor("yellow");
				} else if (region.getType().equals(ImageRegionModel.PROFILE)) {
					box.setStrokeColor("red");
				} else if (region.getType().equals(ImageRegionModel.SKIN)) {
					box.setStrokeColor("cyan");
				} else {
					continue;
				}
				BoundingBoxHandler box_handler = new BoundingBoxHandler(canvas);
				MoveBoundingBoxHandler move_handler = new MoveBoundingBoxHandler(canvas);

				box.addClickHandler(box_handler);
				box.addMouseDownHandler(move_handler);
				box.addMouseMoveHandler(move_handler);
				box.addMouseUpHandler(move_handler);
				box.addMouseOverHandler(box_handler);
				box.addMouseOutHandler(box_handler);
				box.addHandler(box_handler, KeyPressEvent.getType());

				canvas.add(box);
			}
		}
		displayDesiredBoxes();
	}
  
  void displayDesiredBoxes() {
  	String type = typeHolder.getValue();
    	 
   	for(int i = 0; i < canvas.getVectorObjectCount(); i++) {
   		VectorObject vo = canvas.getVectorObject(i);
   		if (vo instanceof AnnotationBox) {
   			String objType = ((AnnotationBox)vo).getAnnType();
   			if(objType.equalsIgnoreCase(type) || type.equalsIgnoreCase(MODE_ALL)) {
   				vo.setVisible(true);
   			}
   			else {
   				vo.setVisible(false);
   			}
   		}
   	}
  }
  
  public static ImageRegionModel[] getModelsFromCanvas(BigDecimal nFactor, DrawingArea canvas) {
  	ImageRegionModel[] ret = null;
    //BigDecimal nFactor = getNormalizationFactor(plRecord.getImageWidth(), plRecord.getImageHeight());
  	ArrayList<ImageRegionModel> mList = new ArrayList<ImageRegionModel>();
  	for(int i = 0; canvas != null && i < canvas.getVectorObjectCount(); i++) {
  		VectorObject vo = canvas.getVectorObject(i);
  		if(vo instanceof AnnotationBox) {
  			String type = ImageRegionModel.FACE;
  			String color = ((AnnotationBox) vo).getStrokeColor();
  			if(color.equalsIgnoreCase("red")) {
  				type = ImageRegionModel.PROFILE;
  			}
  			else if(color.equalsIgnoreCase("yellow")) {
  				type = ImageRegionModel.FACE;
  			}
  			else if(color.equalsIgnoreCase("cyan")) {
  				type = ImageRegionModel.SKIN;
  			}
  			ImageRegionModel sm = 
  					new ImageRegionModel(type, 
  							(new BigDecimal(((AnnotationBox) vo).getX())).multiply(nFactor).intValue(), 
  							(new BigDecimal(((AnnotationBox) vo).getY())).multiply(nFactor).intValue(), 
  							(new BigDecimal(((AnnotationBox) vo).getWidth())).multiply(nFactor).intValue(), 
  							(new BigDecimal(((AnnotationBox) vo).getHeight())).multiply(nFactor).intValue());
  			mList.add(sm);
  		}
  	}
  	ret = mList.toArray(new ImageRegionModel[0]);
  	return ret;
  }
  

  private TextButton createButton(String name, SelectHandler handler) {
    TextButton button = new TextButton(name);
    button.addSelectHandler(handler);
    return button;
  }
  
  public static BigDecimal getNormalizationFactor(int w, int h) {
  	BigDecimal ret = new BigDecimal(1.0);

  	boolean landscape = (w >= h) ? true : false;
  	
  	if(landscape) {
  		ret = (new BigDecimal(w)).divide(new BigDecimal(IMAGE_FRAME_WIDTH), 4, RoundingMode.HALF_UP);
  	}
  	else {
  		ret = (new BigDecimal(h)).divide(new BigDecimal(IMAGE_FRAME_HEIGHT), 4, RoundingMode.HALF_UP);
  	}
  	return ret;
  	
  }
  
  public void fireDblClickEvent(HasHandlers el) {
  	NativeEvent event = Document.get().createDblClickEvent(
  			0,0,0,0,0,false,false,false,false
  			);
  	DomEvent.fireNativeEvent(event, el);
  }
  
  public static void fireDblClick(Element elem) {
    elem.dispatchEvent(Document.get().createDblClickEvent(
  			0,0,0,0,0,false,false,false,false
  			));
  }
  
}
