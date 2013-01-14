package gov.nih.nlm.ceb.lpf.imagestats.client;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import gov.nih.nlm.ceb.lpf.imagestats.shared.PLRecord;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

public class PLResultsGrid extends Grid<PLRecord> implements IsWidget{

	PagingLoader<PagingLoadConfig, PagingLoadResult<PLRecord>> loader = null;
	//ListStore<PLRecord> store = null;
	PLResultsGrid(ListStore<PLRecord> aStore, 
			PagingLoader<PagingLoadConfig, PagingLoadResult<PLRecord>> aLoader,
			ColumnModel<PLRecord> aModel) {
	  super(aStore, aModel);
		//store = aStore;
		loader = aLoader;
	}

	@Override
	public Widget asWidget() {
    getView().setForceFit(true);
    setLoadMask(true);
    setLoader(loader);
		return this;
	}

	@Override
  protected void onAfterFirstAttach() {
    super.onAfterFirstAttach();
    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
      @Override
      public void execute() {
        loader.load();
      }
    });
  }

}
