package gov.nih.nlm.ceb.lpf.imagestats.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import gov.nih.nlm.ceb.lpf.imagestats.client.PLLoadResultListStoreBinding;
import gov.nih.nlm.ceb.lpf.imagestats.shared.ClientUtils;
import gov.nih.nlm.ceb.lpf.imagestats.shared.FacetModel;
import gov.nih.nlm.ceb.lpf.imagestats.shared.ImageStatsException;
import gov.nih.nlm.ceb.lpf.imagestats.shared.PLRecord;
import gov.nih.nlm.ceb.lpf.imagestats.shared.PLSolrParams;
import gov.nih.nlm.ceb.lpf.imagestats.shared.PLPagingLoadResultBean;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
 
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.SimpleSafeHtmlCell;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.StringLabelProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.loader.LoadEvent;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.box.AutoProgressMessageBox;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.tree.Tree.CheckCascade;
 

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ImageStats implements IsWidget, EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";
	 */

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final ImageStatsServiceAsync imageStatsService = GWT
			.create(ImageStatsService.class);

	final Label loginLabel = new Label("");
  final ImageStats thisRef = this;
  final TextField searchBox= new TextField();
  
  final static Map<String, String> sourceMap = new LinkedHashMap<String, String>();
  static {
  	sourceMap.put("pl",      "http://pl.nlm.nih.gov:8983/solr/imagestats/select");
  	sourceMap.put("plstage", "http://plstage.nlm.nih.gov:8983/solr/imagestats/select");
  	sourceMap.put("lhce-pl-web01", "http://lhce-pl-web01.nlm.nih.gov:8983/solr/imagestats/select");
  }

  final SimpleComboBox<SimpleComboboxItem>  sourceListBox = new SimpleComboBox<SimpleComboboxItem>(new StringLabelProvider<SimpleComboboxItem>(){
		@Override
		public String getLabel(SimpleComboboxItem obj) {
			return obj.getLabel();
		}
	});

  final TextButton searchButton = new TextButton("Search");

  final ListStore<PLRecord> resultStore = new ListStore<PLRecord>(new ModelKeyProvider<PLRecord>() {
    @Override
    public String getKey(PLRecord item) {
      return "" + item.getId();
    }
  });


  final TreeStore<FacetModel> facetStore = new TreeStore<FacetModel>(new ModelKeyProvider<FacetModel>() {

		@Override
		public String getKey(FacetModel item) {
			return item.getKey();
		}
  	
  });
  
  
  final Tree<FacetModel, String> facetTree = new Tree<FacetModel, String>(facetStore,
      new ValueProvider<FacetModel, String>() {

        @Override
        public String getValue(FacetModel object) {
        	String countStr = "";
        	if(object.getCount() >= 0) {
        		countStr = " ("+String.valueOf(object.getCount())+")";
        	}
          return object.getDisplayLabel()+countStr;
        }

        @Override
        public void setValue(FacetModel object, String value) {
        }

        @Override
        public String getPath() {
          return "name";
        }
      });

  final static AutoProgressMessageBox progressIndicator = new AutoProgressMessageBox("", "");
  int searchCount = -1;
	final Resources resources = GWT.create(Resources.class);;
	final Style style = resources.css();
	
  
  interface Resources extends ClientBundle {
    @Source("ImageStats_gxt.css")
    Style css();
  }

  interface Style extends CssResource {
    String homeBanner();
    String searchBox();
    String eventListBox();
    String loginUserText();
  }

  
  interface Renderer extends XTemplates {
    @XTemplate(source = "banner.html")
    public SafeHtml renderBanner(Style style, Label user);
  }

  final Renderer renderer = GWT.create(Renderer.class);
  final BorderLayoutContainer mainPanel = new BorderLayoutContainer();
	final CenterLayoutContainer banner = new CenterLayoutContainer();
  /**
	 * This is the entry point method.
	 */
  @Override
  public void onModuleLoad() {
    RootPanel.get().add(this);
  }
 
  @Override
  public Widget asWidget() {
    style.ensureInjected();
 
    //createLogout();

    searchBox.setAllowBlank(false);
    sourceListBox.setWidth(80);
    sourceListBox.setTriggerAction(TriggerAction.ALL);
    SimpleComboboxItem lhceplweb01 = new SimpleComboboxItem("LHCEPLWEB1", "lhce-pl-web01");
    sourceListBox.add(lhceplweb01);
    SimpleComboboxItem pl = new SimpleComboboxItem("PL", "pl");
    sourceListBox.add(pl);
    SimpleComboboxItem plstage = new SimpleComboboxItem("PLSTAGE", "plstage");
    sourceListBox.add(plstage);
    sourceListBox.setValue(lhceplweb01);
    // Testing on plstage
    // sourceListBox.setValue(plstage);
    
  	final RpcProxy<PagingLoadConfig, PLPagingLoadResultBean> resultsProxy = new RpcProxy<PagingLoadConfig, PLPagingLoadResultBean>() {
      @Override
      public void load(PagingLoadConfig loadConfig, AsyncCallback<PLPagingLoadResultBean> callback) {
      	try {
      		PLSolrParams solrParams = thisRef.buildSOLRParams();
      		solrParams.add("start", String.valueOf(loadConfig.getOffset()));
      		solrParams.add("rows", String.valueOf(loadConfig.getLimit()));
      		String source = sourceMap.get(sourceListBox.getValue().getValue());
      	  imageStatsService.searchSOLRForPaging(source, solrParams, callback);
      	}
      	catch (IOException ioe) {
      		MessageBox m = new MessageBox(ioe.getMessage());
      		m.setTitle("Error from PL server");
      		m.show();
      	}
      }
    };
 
    final PagingLoader<PagingLoadConfig, PLPagingLoadResultBean> resultsLoader = 
    	  new PagingLoader<PagingLoadConfig, PLPagingLoadResultBean>(resultsProxy);
    
    resultsLoader.setRemoteSort(true);
    PLLoadResultListStoreBinding<PagingLoadConfig> plStoreBinder = 
    	new PLLoadResultListStoreBinding<PagingLoadConfig>(resultStore, facetStore){
    	@Override
    	public void onLoad(LoadEvent<PagingLoadConfig, PLPagingLoadResultBean> event) {
    		List<FacetModel> checkedList = facetTree.getCheckedSelection();
    		super.onLoad(event);
    		searchCount = event.getLoadResult().getTotalLength();
    		for(FacetModel m : checkedList) {
    			facetTree.setChecked(m, Tree.CheckState.CHECKED);
    		}
    		facetTree.expandAll();
    		
    	}
    };
    resultsLoader.addLoadHandler(plStoreBinder);

    final PLRecordListView resultsDisplay = new PLRecordListView(
    		resultStore, 
    		this);
    resultsDisplay.bind(resultsLoader);
  
    resultsDisplay.addSelectionChangeHandler(new SelectionChangedHandler<PLRecord>() {
      @Override
      public void onSelectionChanged(SelectionChangedEvent<PLRecord> event) {
      	StringBuffer sb = new StringBuffer();
      	sb.append("PL Results from ")
      	  .append(resultsDisplay.getSourceServerLabel())
      	  .append(" (")
      	  .append(event.getSelection().size());
      	sb.append(" items selected)");
      	resultsDisplay.getView().setHeadingText(sb.toString());     	
      }
    });

    searchButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				resultStore.clear();
				resultsLoader.setOffset(0);
				indicateProgress("Searching...");
				try {
					resultsDisplay.setSourceServer(sourceListBox.getValue());
				  resultsLoader.load();
				}
				catch(Throwable t) {
			    MessageBox m = new MessageBox("Error in communicating with server. "+t.getMessage());
			    m.setStyleName(style.searchBox());
			    m.show();
					resetProgress();
				}
			}
    });
    Widget searchlayout = createSearchLayout();
    VerticalLayoutContainer vcon = new VerticalLayoutContainer();
    BorderLayoutData northData = new BorderLayoutData(100);
    northData.setMargins(new Margins(25));
    createBanner(); 
    mainPanel.setNorthWidget(banner, northData);

    BorderLayoutData westData = new BorderLayoutData(260);
    westData.setMargins(new Margins(0, 5, 0, 5));

    final VerticalLayoutContainer sidebar = new VerticalLayoutContainer();
    
    sidebar.setHeight(650);
    sidebar.setBorders(true);
    sidebar.setScrollMode(ScrollMode.ALWAYS);
  	sidebar.add(facetTree);
    facetTree.setCheckable(true);
    facetTree.setCheckStyle(CheckCascade.TRI);
    facetTree.setAutoLoad(true);
    facetTree.setAutoExpand(true);

    mainPanel.setWestWidget(sidebar, westData);
    vcon.add(searchlayout);
    vcon.add(resultsDisplay);
    mainPanel.setCenterWidget(vcon, new MarginData());
    return mainPanel;
  }

  
  Widget createSearchLayout() {
  	VerticalPanel vp = new VerticalPanel();
  	vp.add(createSearchBox());
  	vp.setStyleName(style.eventListBox());
  	return vp;
  }
  
  
	Widget createSearchBox() {
    HorizontalPanel searchlayout = new HorizontalPanel();
    
		try {
  		String source = sourceMap.get(sourceListBox.getValue().getValue());
			PLSolrParams solrParams = ClientUtils.buildDefaultSOLRParams();
			solrParams.add("q", "*:*");
			imageStatsService
					.searchSOLRForEvents(source, solrParams, new AsyncCallback<Map<String, List<FacetModel>>>() {

						public void onFailure(Throwable t) {
			        String details = t.getMessage();
			        MessageBox m = new MessageBox(details);
			        
			        if (t instanceof ImageStatsException) {
			          m.setMessage(((ImageStatsException)t).getISMessage());
			        }
		          m.show();
						}

						public void onSuccess(Map<String, List<FacetModel>> eventList) {
							Iterator<String> iter = eventList.keySet().iterator();
							while (iter.hasNext()) {
								String field = iter.next();
                
								FacetModel parent = new FacetModel(field, ClientUtils.getFieldLabel(field));
								parent.setField(field);
								facetStore.add(parent);
								facetStore.add(parent, eventList.get(field));
							}
							facetTree.expandAll();
						}
					});
		} catch (IOException e) {
			e.printStackTrace();
		}

		searchBox.addKeyDownHandler(new KeyDownHandler(){
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
				  searchButton.fireEvent(new SelectEvent());
				}
			}
    });

    searchBox.setAllowBlank(false);
    searchBox.setText("*:*");
    searchlayout.add(searchBox);
		searchlayout.add(searchButton);
		searchlayout.add(createLogout());
    searchlayout.setStyleName(style.searchBox());
    GwtEvent<SelectHandler> event = new GwtEvent<SelectHandler>(){

			@Override
			public com.google.gwt.event.shared.GwtEvent.Type<SelectHandler> getAssociatedType() {
				return SelectEvent.getType();
			}

			@Override
			protected void dispatch(SelectHandler handler) {
				handler.onSelect(null);
			}
    };
		searchButton.fireEvent(event);
		return searchlayout;
	}

	PLSolrParams buildFacetFilters(List<FacetModel> selected) {
		PLSolrParams ret = new PLSolrParams();
		Iterator<FacetModel> root_iter = facetTree.getStore().getRootItems().iterator();

		while(root_iter.hasNext()) {
			FacetModel rootFacet = root_iter.next();
			if(selected.contains(rootFacet)) {
				continue;
			}
			Iterator<FacetModel> iter = selected.iterator();
			List<String> children = new ArrayList<String>();
		  while(iter.hasNext()) {
		   FacetModel facet = iter.next();
		   if(rootFacet.getField().equals(facet.getParent())) {
		  	 children.add(ClientUtils.escapeSpecialChars(facet.getField()));
		   }
		  }
		  if(rootFacet.getField().equalsIgnoreCase("type")) {
			  String selected_query = join(children.toArray(new String[0]), " OR ");
			  if(selected_query.trim().length() > 0) {
			    ret.add("fq", selected_query);
			  }
		  }
		  else {
		    String selected_query = join(children.toArray(new String[0]), " OR ");
			  if(selected_query.trim().length() > 0) {
		      ret.add("fq", rootFacet.getField()+":"+selected_query);
			  }
		  }
		}
		return ret;
	}
	

	PLSolrParams buildFacetFilters() {
		List<FacetModel> selected = facetTree.getCheckedSelection();
		return buildFacetFilters(selected);
	}
	

	PLSolrParams buildSOLRParams() {
		PLSolrParams ret = ClientUtils.buildDefaultSOLRParams();
		ret.addAll(buildFacetFilters());
		String str = searchBox.getText();
    if(str != null && str.trim().length() > 0) {
			ret.add("q", str);
		}
		return ret;
	}
	
	
	String join (String [] list, String delim) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; list != null && list.length > i; i++) {
			if(list[i] != null && list[i].trim().length() > 0) {
			if(sb.length() > 0) {
				sb.append(delim);
			}
			sb.append(list[i].trim());
			}
		}
		if(sb.length() > 0) {
			sb.insert(0, "(").append(")");
		}
		return sb.toString();
	}
	
	
	String [] getSelectedFromListBox(SimpleComboBox<SimpleComboboxItem> comboBox) {
		ArrayList<String> arr = new ArrayList<String>();
		SimpleComboboxItem val = comboBox.getValue();
		if(val != null) {
			String v = val.getValue();
			if("All".equalsIgnoreCase(v)) {
				return null;
			}
		  arr.add(v);
    }
		return arr.toArray(new String[0]);
	}
	
	
	String [] getSelectedFromListBox(ListBox listBox) {
		ArrayList<String> arr = new ArrayList<String>();
		for(int i = listBox.getSelectedIndex(); listBox != null && i >= 0 && i < listBox.getItemCount(); i++) {
			if(listBox.isItemSelected(i)) {
				String val = listBox.getValue(i);
				if("All".equalsIgnoreCase(val)) {
					return null;
				}
			  arr.add(val);
			}
		}
		
		return arr.toArray(new String[0]);
	}
	
	
	TextField getSearchBox() {
		return searchBox;
	}

	
	void createBanner() {
    banner.setStyleName(style.homeBanner(), true);
		imageStatsService.getUser(new AsyncCallback<String>(){

			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(String result) {
				if(result == null || result.trim().length() == 0) {
					result = "unauthenticated";
				}
				loginLabel.setText(result);
				HTML html = new HTML(renderer.renderBanner(style, loginLabel));
		    banner.add(html);
			}
			
		});
	}


	Component createFooter() {
		VerticalLayoutContainer footer = new VerticalLayoutContainer();
		HTML html = new HTML("&nbsp;");
		footer.add(html);
		return footer;
	}

	final Anchor logoutLink = new Anchor("Logout");
	
	Component createLogout() {
		HorizontalLayoutContainer logout = new HorizontalLayoutContainer();
		logout.add(new HTML("<pre>                        </pre>"));
		logout.add(logoutLink);
		
		logoutLink.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				imageStatsService.logout(new AsyncCallback<Void>(){

					@Override
					public void onFailure(Throwable caught) {
						
					}

					@Override
					public void onSuccess(Void result) {
						loginLabel.setText("");
						Window.Location.assign("ImageStats.html");
					}
					
				});
				
			}
			
		});
		
		
		return logout;
	}
	
	public static void indicateProgress(String message) {
  	progressIndicator.setProgressText(message);
		progressIndicator.show();
  }
  
  public static void resetProgress() {
		progressIndicator.hide();
  }	
  
  public ImageStatsServiceAsync getImageStatsService() {
  	return imageStatsService;
  }
}
