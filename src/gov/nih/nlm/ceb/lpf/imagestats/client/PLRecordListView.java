package gov.nih.nlm.ceb.lpf.imagestats.client;

import gov.nih.nlm.ceb.lpf.imagestats.shared.ISConstants;
import gov.nih.nlm.ceb.lpf.imagestats.shared.PLPagingLoadResultBean;
import gov.nih.nlm.ceb.lpf.imagestats.shared.ImageDetails;
import gov.nih.nlm.ceb.lpf.imagestats.shared.PLRecord;
import gov.nih.nlm.ceb.lpf.imagestats.shared.PLSolrParams;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.AbstractSafeHtmlRenderer;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;

import com.sencha.gxt.cell.core.client.SimpleSafeHtmlCell;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.XTemplates.Formatter;
import com.sencha.gxt.core.client.XTemplates.FormatterFactories;
import com.sencha.gxt.core.client.XTemplates.FormatterFactory;
import com.sencha.gxt.core.client.resources.CommonStyles;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.StringLabelProvider;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.ListViewCustomAppearance;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.AutoProgressMessageBox;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.RefreshEvent;
import com.sencha.gxt.widget.core.client.event.RemoveEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.ShowEvent;
import com.sencha.gxt.widget.core.client.event.SubmitCompleteEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.RemoveEvent.RemoveHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.event.ShowEvent.ShowHandler;
import com.sencha.gxt.widget.core.client.event.SubmitCompleteEvent.SubmitCompleteHandler;
import com.sencha.gxt.widget.core.client.event.SubmitEvent;
import com.sencha.gxt.widget.core.client.form.FormPanel;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;
import com.sencha.gxt.widget.core.client.toolbar.LabelToolItem;
import com.sencha.gxt.widget.core.client.toolbar.PagingToolBar;
import com.sencha.gxt.widget.core.client.toolbar.SeparatorToolItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

public class PLRecordListView implements
		IsWidget {
	ListView<PLRecord, PLRecord> view;
  private FramedPanel panel = null;
  final MyFormPanel downloadPanel = new MyFormPanel();
  final TextButton exportAllInPage = new TextButton("Export all on this page");
  final TextButton exportSelected = new TextButton("Export Selected");
  final TextButton exportAll = new TextButton("Export all results");
  private ImageEditContainer imageEdit = null;
  ImageStats mainPanel;
	ListStore<PLRecord> store;
	PagingLoader<PagingLoadConfig, PLPagingLoadResultBean> loader = null;
	SimpleComboboxItem sourceUrlItem; 
  Dialog editDialog = new Dialog();
  final ArrayList<String> regionSpecs = new ArrayList<String>();

	final SimpleComboBox<SimpleComboboxItem> format = new SimpleComboBox<SimpleComboboxItem>(new StringLabelProvider<SimpleComboboxItem>(){
		@Override
		public String getLabel(SimpleComboboxItem obj) {
			return obj.getLabel();
		}
	});
  final PagingToolBar ptoolBar = new PagingToolBar(20);
	
	final Resources resources;
	final Style style;
	final Renderer r;

	@FormatterFactories(@FormatterFactory(factory = ShortenFactory.class, name = "shorten"))
  interface Renderer extends XTemplates {
    @XTemplate(source = "PLRecordListView.html")
    public SafeHtml renderItem(PLRecord plrecord, Style style, String styleClass);
  }
 
  interface Resources extends ClientBundle {
    @Source("PLRecordListView.css")
    Style css();
  }

  public interface Style extends CssResource {
    String over();
    String select();
    String thumbStatus0();
    String thumbStatus1();
    String thumbStatus2();
    String thumbStatus3();
    
    String thumbWrap();
    String comboBox();
    String details();
    String details_info();
    String details_image();
    String box_move();
    String box_grab();
    String box_resize_corner();
    String box_resize_bottom();
    String box_resize_right();
  }

  static class Shorten implements Formatter<String> {
 
    private int length;
 
    public Shorten(int length) {
      this.length = length;
    }
 
    @Override
    public String format(String data) {
      return Format.ellipse(data, length);
    }
  }
 
  static class ShortenFactory {
    public static Shorten getFormat(int length) {
      return new Shorten(length);
    }
  }
 
	public PLRecordListView(ListStore<PLRecord> aStore, 
			//ImageStatsServiceAsync service, 
			ImageStats aMainPanel) {
		mainPanel = aMainPanel;
		//imageStatsService = service;
		store = aStore;
    resources = GWT.create(Resources.class);
    resources.css().ensureInjected();
    style = resources.css();
    r = GWT.create(Renderer.class);
    ListViewCustomAppearance<PLRecord> appearance = new ListViewCustomAppearance<PLRecord>("." + style.thumbWrap(),
        style.over(), style.select()) {
 
      @Override
      public void renderEnd(SafeHtmlBuilder builder) {
        String markup = new StringBuilder("<div class=\"").append(CommonStyles.get().clear()).append("\"></div>").toString();
        builder.appendHtmlConstant(markup);
      }
 
      @Override
      public void renderItem(SafeHtmlBuilder builder, SafeHtml content) {
        builder.appendHtmlConstant("<div class='" + style.thumbWrap() + "' style='border: 1px solid white'>");
        builder.append(content);
        builder.appendHtmlConstant("</div>");
      }
 
    };
 
    view = new ListView<PLRecord, PLRecord>(store, new IdentityValueProvider<PLRecord>() {
 
      @Override
      public void setValue(PLRecord object, PLRecord value) {
 
      }
    }, appearance);
    
    view.addRefreshHandler(new RefreshEvent.RefreshHandler() {

			@Override
			public void onRefresh(RefreshEvent event) {
				if(view.getItemCount() <= 0) {
    	    exportSelected.disable();
    	    exportAllInPage.disable();
    	    exportAll.disable();
    	    format.enable();
    	    ptoolBar.disable();
        }
				else {
					// FOR GCI disable these buttons.
    	    exportSelected.disable();
    	    exportAllInPage.disable();
    	    exportAll.enable();
    	    format.enable();
    	    ptoolBar.enable();
				}
			}
    	
    });

    imageEdit = new ImageEditContainer(this);

	}

	public ImageStatsServiceAsync getImageStatsService() {
		return mainPanel.getImageStatsService();
	}
	
	public String getSourceServerUrl() {
		return ImageStats.sourceMap.get(sourceUrlItem.getValue());
	}

	public String getSourceServerLabel() {
		if(sourceUrlItem == null) {
			return "";
		}
		return sourceUrlItem.getLabel();
	}

	public void setSourceServer(SimpleComboboxItem urlItem) {
		sourceUrlItem = urlItem;
	}
	
  public Widget asWidget() {
		if (panel == null) {
			imageEdit = new ImageEditContainer(this);
			panel = new FramedPanel();
			panel.setCollapsible(true);
			panel.setAnimCollapse(false);
			panel.setId("images-view2");
			panel.setHeadingText("PL Results");
			panel.setWidth(700);
			panel.setHeight(600);
			panel.setBodyBorder(false);
			panel.setCollapsible(false);
			panel.add(createEditDialog());
			final ToolBar bar = new ToolBar();
			exportSelected.addSelectHandler(new ExportHandler());
			exportAllInPage.addSelectHandler(new ExportHandler());
			exportAll.addSelectHandler(new ExportHandler());
			exportAllInPage.setBorders(true);
			exportSelected.setBorders(true);
			exportAll.setBorders(true);
			bar.add(exportSelected);
			bar.add(exportAllInPage);
			bar.add(exportAll);

			bar.add(new SeparatorToolItem());

			bar.add(new LabelToolItem("Export format:"));
			// format.setWidth(3);
//			SimpleComboboxItem selected = new SimpleComboboxItem(
//					"IplImageData to CSV file      ", ISConstants.FORMAT_CSV);
			SimpleComboboxItem selected = 
					                  new SimpleComboboxItem("Final GroundTruthData to TSV file", 
					                                         ISConstants.FINAL_FORMAT_GROUNDTRUTH_CSV);
		
//			format.add(new SimpleComboboxItem("Initial GroundTruthData to TSV file", 
//					                              ISConstants.INITIAL_FORMAT_GROUNDTRUTH_CSV));
//		format.add(new SimpleComboboxItem("IplImageData to XML file", ISConstants.FORMAT_XML));
//		format.add(new SimpleComboboxItem("Images to ZIP file", ISConstants.FORMAT_ZIP));
			format.add(selected);
			format.setValue(selected);
			format.setTriggerAction(TriggerAction.ALL);
			format.setEditable(false);
			format.setWidth(200);

			bar.add(format);

			view.setCell(new SimpleSafeHtmlCell<PLRecord>(
					new AbstractSafeHtmlRenderer<PLRecord>() {

						@Override
						public SafeHtml render(PLRecord object) {
							String highlightClass = style.thumbStatus0();
							switch(object.getGroundTruthStatus()) {
							case 0:
								highlightClass = style.thumbStatus0();
								break;
							case 1:
								highlightClass = style.thumbStatus1();
								break;
							case 2:
								highlightClass = style.thumbStatus2();
								break;
							case 3:
								highlightClass = style.thumbStatus3();
								break;
							default:
								break;
							}
							
							return r.renderItem(object, style, highlightClass);
						}
					}, "dblclick") {

				@Override
				public void onBrowserEvent(Cell.Context context, Element parent,
						PLRecord value, NativeEvent event,
						ValueUpdater<PLRecord> valueUpdater) {

					super.onBrowserEvent(context, parent, value, event, valueUpdater);
					// Check that the value is not null.
					if (value == null) {
						return;
					}
					if ("dblclick".equals(event.getType())) {
						int itemNum = ((ptoolBar.getActivePage() - 1) * ptoolBar
								.getPageSize()) + context.getIndex() + 1;
						imageEdit.updateContainer(context, value, itemNum, valueUpdater);
						editDialog.getButtonById(PredefinedButton.CLOSE.name()).enable();
						showDialog();
					}
				}
			});
			panel.addStyleName("margin-10");
			VerticalLayoutContainer con = new VerticalLayoutContainer();
			con.setBorders(true);
			con.add(bar, new VerticalLayoutData(1, -1));
			con.add(view, new VerticalLayoutData(1, 1));
			ptoolBar.getElement().getStyle().setProperty("borderBottom", "none");
			con.add(ptoolBar, new VerticalLayoutData(1, -1));
			panel.setWidget(con);
		}
    return panel;
  }
  
  public void addSelectionChangeHandler(SelectionChangedHandler<PLRecord> handler) {
  	view.getSelectionModel().addSelectionChangedHandler(handler);
  }
  
  public ListView<PLRecord, PLRecord> getListView() {
  	return view;
  }
  
  public FramedPanel getView() {
  	return panel;
  }
  
  public String getFormatValue() {
  	return format.getValue().getValue();
  }
  
  public void bind(PagingLoader<PagingLoadConfig, PLPagingLoadResultBean> aLoader) {
    ptoolBar.bind(aLoader);
    loader = aLoader;
  }
  
  public PagingLoader<PagingLoadConfig, PLPagingLoadResultBean> getLoader() {
  	return loader;
  }
 
  public PagingToolBar getPagingToolBar() {
    return ptoolBar;	
  }

  void exportAll() {
  	//MyFormPanel fp = buildDownloadFormPanel();
  	downloadPanel.removeAllFields();
  	downloadPanel.add(new Hidden("file", String.valueOf(true)));
  	downloadPanel.add(new Hidden("format", getExportFormat()));
  	downloadPanel.add(new Hidden("sourceUrl", getSourceServerUrl()));
  	downloadPanel.add(new Hidden("q", mainPanel.searchBox.getText()));
  	addFacetParams();
  	//downloadPanel.add(new Hidden("event", mainPanel.eventListBox.getValue().getValue()));
  	downloadPanel.add(new Hidden("rows", String.valueOf(ptoolBar.getPageSize()*ptoolBar.getTotalPages())));
  	downloadPanel.submit();
  }

  void addFacetParams() {
  	PLSolrParams params = mainPanel.buildFacetFilters();
  	//params.add("fl", "url");
  	Iterator<String> iter = params.keySet().iterator();
  	while(iter.hasNext()) {
  		String name = iter.next();
  		Iterator<String> it = params.get(name).iterator();
  		while(it.hasNext()) {
  			downloadPanel.add(new Hidden(name, it.next()));
  		}
  	}
  }
  
  void exportPage() {
  	//MyFormPanel fp = buildDownloadFormPanel();
  	downloadPanel.removeAllFields();
  	downloadPanel.add(new Hidden("file", String.valueOf(true)));
  	downloadPanel.add(new Hidden("format", getExportFormat()));
  	downloadPanel.add(new Hidden("sourceUrl", getSourceServerUrl()));
  	
    downloadPanel.add(new Hidden("q", mainPanel.searchBox.getText()));
  	addFacetParams();
    //downloadPanel.add(new Hidden("event", mainPanel.eventListBox.getValue().getValue()));
    downloadPanel.add(new Hidden("start", String.valueOf((ptoolBar.getActivePage() - 1)*ptoolBar.getPageSize())));
    downloadPanel.add(new Hidden("rows", String.valueOf(ptoolBar.getPageSize())));
    downloadPanel.submit();
  }
  
  void exportSelected() {
  	
  	downloadPanel.removeAllFields();
  	//getExportFormat();
  	List<PLRecord> selected = view.getSelectionModel().getSelectedItems();
  	downloadPanel.add(new Hidden("file", String.valueOf(true)));
  	downloadPanel.add(new Hidden("format", getExportFormat()));
  	downloadPanel.add(new Hidden("sourceUrl", getSourceServerUrl()));
  	for(int i = 0; i < selected.size(); i++) {
  		PLRecord record = selected.get(i);
  		downloadPanel.add(new Hidden("url",record.getUrl()));
  	}
  	downloadPanel.submit();
  }  

	public void setVisible(boolean flag) {
		panel.setVisible(flag);
	}

	public String getExportFormat() {
	  int i  = format.getSelectedIndex();
 	  if(i < 0) i = 0; 
  	return format.getStore().get(i).getValue();
	}
  /* 
  MyFormPanel buildDownloadFormPanel() {
  	MyFormPanel fp = new MyFormPanel();
    fp.add(new Hidden("file", String.valueOf(true)));
    fp.add(new Hidden("format", format.getValue()));
  	return fp;
  }
*/
  class MyFormPanel extends FormPanel {
  	VerticalLayoutContainer vlc = new VerticalLayoutContainer();
    final AutoProgressMessageBox progressIndicator = new AutoProgressMessageBox("", "");
  	public MyFormPanel() {
  		super();
    	RootPanel.get().add(this);
    	setVisible(false);
    	setMethod(Method.POST);
    	sinkEvents(Event.ONLOAD);
      setAction(GWT.getModuleBaseURL().replaceAll("/$", ""));
      setWidget(vlc);
      addSubmitCompleteHandler(new SubmitCompleteHandler(){
				@Override
				public void onSubmitComplete(SubmitCompleteEvent event) {
					//super.onSubmitComplete(event);
					progressIndicator.hide();
					MessageBox m = new MessageBox("Form Panel submitted.");
					m.show();
				}
      });

      addSubmitHandler(new SubmitEvent.SubmitHandler(){
				@Override
				public void onSubmit(SubmitEvent event) {
					//super.onSubmitComplete(event);
					//MessageBox m = new MessageBox("Form Panel submitted."+event.toDebugString());
					//m.show();
				}
      });

      addRemoveHandler(new RemoveHandler(){

				@Override
				public void onRemove(RemoveEvent event) {
		    	RootPanel.get().remove(MyFormPanel.this);
				}
      });
   	}
  	
  	@Override
  	public void onBrowserEvent(Event event) {
			if (DOM.eventGetType(event) == Event.ONLOAD) {
				unsinkEvents(Event.ONLOAD);
				DOM.eventCancelBubble(event, true);
				RootPanel.get().remove(this);
			} else {
				super.onBrowserEvent(event);
			}
  	}
  	
  	
  	
  	@Override
  	public void add(Widget w) {
  		vlc.add(w);
  	}
  	//@Override
  	public void removeAllFields() {
  		int i = vlc.getWidgetCount();
  		while(i > 0) {
  			vlc.remove(0);
  			i--;
  		}
  	}
  	
  }

	public void showDialog() {
	  editDialog.show();	
	}
	
	Widget createEditDialog() {
    editDialog.setId("img-chooser-dlg");
    editDialog.setHeadingText("Detect the face");
		editDialog.add(imageEdit);
    editDialog.setPredefinedButtons(PredefinedButton.CLOSE);
    editDialog.setHideOnButtonClick(true);
    editDialog.addHideHandler(new HideHandler() {

      @Override
      public void onHide(HideEvent event) {
        PLRecord record = view.getSelectionModel().getSelectedItem();
        if (record != null) {
          if (editDialog.getHideButton() == editDialog.getButtonById(PredefinedButton.CLOSE.name())) {
        	  imageEdit.saveEditsToDB();
          	//editDialog.getParent().fireEvent(GwtEvent<EventHandler>.Type);
          }
        }
      }
    });
		return editDialog;
	}
/*
  Widget createEditDialog() {
    editDialog.setId("img-chooser-dlg");
    editDialog.setHeadingText("Detect the face");
    editDialog.setWidth(500);
    editDialog.setHeight(300);
    editDialog.setModal(true);
    editDialog.setBodyStyle("background: none");
    editDialog.setBodyBorder(false);
    editDialog.setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CANCEL);
    editDialog.setHideOnButtonClick(true);

    editDialog.addHideHandler(new HideHandler() {

      @Override
      public void onHide(HideEvent event) {
        PLRecord record = view.getSelectionModel().getSelectedItem();
        if (record != null) {
          if (editDialog.getHideButton() == editDialog.getButtonById(PredefinedButton.OK.name())) {
          	//TODO Save user edits?. 
          }
        }
      }
    });

    VerticalLayoutContainer con = new VerticalLayoutContainer();
    details.getElement().getStyle().setBackgroundColor("white");
    canvas.setStyleName(style.details_image());
    canvas.setStyleName(style.details_image());
    sourceImage.setStyleName(style.details_image());
    sourceImage.getElement().setAttribute("preserveAspectRatio", "defer xMidYMid");
    
    
    canvas.add(sourceImage);
    con.add(canvas);
    con.add(details);

    editDialog.add(con);
    return editDialog;
  }
*/
/*
  public void onDoubleClick(DoubleClickEvent event, SimpleSafeHtmlCell<PLRecord> cell ) {
 
  }
*/
/*
  public void onSelectionChange(SelectionChangedEvent<ImageDetails> se) {
    if (se.getSelection().size() > 0) {
    	ImageDetails plrecord = se.getSelection().get(0);
    	int itemNum = ((ptoolBar.getActivePage()-1) * ptoolBar.getPageSize())+se.getSource().getSelectedItem().getItemIndex()+1;
    	imageEdit.updateContainer(plrecord, itemNum);
    	//sourceImage = new Image(0,0,300,0,plrecord.getUrl());
      editDialog.getButtonById(PredefinedButton.OK.name()).enable();
    } else {
    	editDialog.getButtonById(PredefinedButton.OK.name()).disable();
    	imageEdit.updateContainer(null, 0);
    }
  }
  */
/*
  public void onSelection(SelectionChangedEvent<PLRecord> se) {
  	
  }  
  */
  class ExportHandler implements SelectHandler{

		@Override
		public void onSelect(SelectEvent event) {
			TextButton btn = (TextButton) event.getSource();
			if(exportSelected == btn) {
				exportSelected();
			}
			else if(exportAllInPage == btn) {
				exportPage();				
			}
			else if(exportAll == btn) {
			  exportAll();
			}
		}
  }

	
  public void addDoubleClickHandler(DoubleClickHandler doubleClickHandler) {
		// TODO Auto-generated method stub
		
	}
  
}
