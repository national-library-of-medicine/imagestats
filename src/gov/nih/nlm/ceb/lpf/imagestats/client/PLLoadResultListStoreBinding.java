package gov.nih.nlm.ceb.lpf.imagestats.client;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import gov.nih.nlm.ceb.lpf.imagestats.shared.ClientUtils;
import gov.nih.nlm.ceb.lpf.imagestats.shared.FacetModel;
import gov.nih.nlm.ceb.lpf.imagestats.shared.PLPagingLoadResultBean;
import gov.nih.nlm.ceb.lpf.imagestats.shared.PLRecord;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.loader.LoadEvent;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.widget.core.client.box.MessageBox;

public class PLLoadResultListStoreBinding<C> extends
		LoadResultListStoreBinding<C, PLRecord, PLPagingLoadResultBean>  {

	TreeStore<FacetModel> fstore = null;
	public PLLoadResultListStoreBinding (ListStore<PLRecord> resultsStore, TreeStore<FacetModel> facetsStore) {
		super(resultsStore);
		fstore = facetsStore;
	}
	
	@Override
	public void onLoad(LoadEvent<C, PLPagingLoadResultBean> event) {
		try {
			super.onLoad(event);
			if (event.getLoadResult().getTotalLength() == 0) {
				MessageBox m = new MessageBox("No results found.");
				m.show();
			}
			if (fstore != null) {
				fstore.clear();
				Map<String, List<FacetModel>> facets = event.getLoadResult()
						.getFacets();
				Iterator<String> fields_iter = facets.keySet().iterator();
				while (fields_iter.hasNext()) {
					String field = fields_iter.next();
					FacetModel parent = new FacetModel(field, ClientUtils.getFieldLabel(field));
					fstore.add(parent);
					fstore.add(parent, facets.get(field));
				}
			}
		} catch (Exception e) {
			MessageBox m = new MessageBox(
					"Error in loading the results from PL server. " + e.getMessage());
			m.show();
		} finally {
			ImageStats.resetProgress();
		}
	}

	public TreeStore<FacetModel> getFacetStore() {
		return fstore;
	}
}
