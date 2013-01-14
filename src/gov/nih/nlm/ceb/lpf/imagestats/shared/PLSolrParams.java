package gov.nih.nlm.ceb.lpf.imagestats.shared;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class PLSolrParams extends HashMap<String, Set<String>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PLSolrParams add(String p, String v) {
		Set<String> vals = get(p);
		if(vals == null) {
			vals = new HashSet<String>();
		}
		vals.add(v);
		put(p, vals);
		return this;
	}
	
	public PLSolrParams add(String p, List<String> v) {
		Set<String> vals = get(p);
		if(vals == null) {
			vals = new HashSet<String>();
		}
		vals.addAll(v);
		put(p, vals);
		return this;
	}
	
	public PLSolrParams add(String p, Set<String> v) {
		Set<String> vals = get(p);
		if(vals == null) {
			vals = new HashSet<String>();
		}
		vals.addAll(v);
		put(p, vals);
		return this;
	}
	
	
	public PLSolrParams add(String p, String [] v) {
		return add(p, Arrays.asList(v));
	}
	
	public PLSolrParams addAll(PLSolrParams otherParams) {
		if(otherParams == null) 
			return this;
		
		Iterator<String> iter = otherParams.keySet().iterator();
		while (iter.hasNext()) {
			String k = iter.next();
			add(k, otherParams.get(k));
		}
		
		return this;
	}	

}
