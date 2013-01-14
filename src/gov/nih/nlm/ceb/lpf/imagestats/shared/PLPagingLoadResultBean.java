package gov.nih.nlm.ceb.lpf.imagestats.shared;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.sencha.gxt.data.shared.loader.PagingLoadResultBean;

public class PLPagingLoadResultBean extends PagingLoadResultBean<PLRecord>
		implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Map<String, List<FacetModel>> facets = null;
		

	public PLPagingLoadResultBean() {
	  super();
	}	
	
	public PLPagingLoadResultBean(List<PLRecord> data, int total, int offset, Map<String, List<FacetModel>> aFacetList) {
		super(data, total, offset);
		facets = aFacetList;
	}
	
	public Map<String, List<FacetModel>> getFacets() {
		return facets;
	}
}
