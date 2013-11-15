package gov.nih.nlm.ceb.lpf.imagestats.server;

import java.security.Principal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.sql.DataSource;

import gov.nih.nlm.ceb.lpf.imagestats.client.ImageStatsService;
import gov.nih.nlm.ceb.lpf.imagestats.shared.ClientUtils;
import gov.nih.nlm.ceb.lpf.imagestats.shared.FacetModel;
import gov.nih.nlm.ceb.lpf.imagestats.shared.ISConstants;
import gov.nih.nlm.ceb.lpf.imagestats.shared.ImageRegionModel;
import gov.nih.nlm.ceb.lpf.imagestats.shared.GroundTruthRecord;
import gov.nih.nlm.ceb.lpf.imagestats.shared.PLPagingLoadResultBean;
import gov.nih.nlm.ceb.lpf.imagestats.shared.PLRecord;
import gov.nih.nlm.ceb.lpf.imagestats.shared.PLSolrParams;
import gov.nih.nlm.ceb.lpf.imagestats.shared.Utils;
import gov.nih.nlm.ceb.lpf.imagestats.shared.ImageStatsException;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ImageStatsServiceImpl extends RemoteServiceServlet implements
		ImageStatsService {
	public static Pattern region_pat = Pattern.compile("^\\s*([a-zA-Z]+)\\[(\\d+),(\\d+);(\\d+),(\\d+)\\]\\s*$");
	//SearchPLUsingSOLR pl = null;
	SearchFileSystem pl = null;
	ImageStatsDB imageStatsDB = null;
	String source = null;
	String virtualAddress = null;
	@Override
	public void init() throws ServletException{
		super.init();
		try {

			InitialContext cxt = new InitialContext();

			DataSource plDataSource = (DataSource) cxt.lookup( "java:/comp/env/jdbc/pl" );
			Connection con = plDataSource.getConnection();
			con.close();
			con = plDataSource.getConnection();
			con.close();
			imageStatsDB = new ImageStatsDB(plDataSource);
			pl = new SearchFileSystem(imageStatsDB);
		} catch (Exception e) {
			throw new ServletException(e);
		}
		try{
			InitialContext cxt = new InitialContext();
	    	source = (String)cxt.lookup("java:comp/env/repository/directory");
	    }catch(NamingException e){throw new ServletException("Configuration Problem: Environment variable repository directory");}
		try{
	    	InitialContext ic = new InitialContext();
	    	virtualAddress = (String)ic.lookup("java:comp/env/repository/virtual");
	    }catch(NamingException e){throw new ServletException("Configuration Problem: Environment variable Virtual Repository");}
	}

	@Override
	public Map<String, List<FacetModel>> searchSOLRForEvents(String source, PLSolrParams searchParams) throws ImageStatsException {
		return pl.searchSOLRForEvents(source, searchParams);
	}
	
	public String searchSOLR(String sourceServer, PLSolrParams searchParams) throws ImageStatsException {
      return null;
	}

	@Override
	public PLPagingLoadResultBean searchSOLRForPaging(String sourceServer, PLSolrParams searchParams) throws ImageStatsException {
	  return pl.searchSOLRForPaging(sourceServer, searchParams);	
	}
	
	int extractTotalCount(JsonObject solrResults) throws ImageStatsException {
		int ret = -1;
		if(getStatusCode(solrResults) >= 400) {
			throw new ImageStatsException (getErrorMessage(solrResults));
		}
		else {
		  JsonElement je = solrResults.get("response");
		  if(je != null) {
			  ret = je.getAsJsonObject().get("numFound").getAsInt();
		  }
		}
		return ret;
	}
	
	int getStatusCode(JsonObject solrResults) {
		int ret = solrResults.get("responseHeader").getAsJsonObject().get("status").getAsInt();
		return ret;
	}
	
	String getErrorMessage(JsonObject solrResults) {
		String ret = null;
		StringBuffer buf = new StringBuffer();
		JsonElement je = solrResults.get("responseHeader").getAsJsonObject().get("error");
		if(je != null) {
			buf.append(je.getAsJsonObject().get("msg").getAsString());
			buf.append(" (code:");
			buf.append(je.getAsJsonObject().get("code").getAsString());
			buf.append(")");
			ret = buf.toString();
		}
		return ret;
	}
	

	Map<String, List<FacetModel>> parsePLFacets(JsonObject solrResults) {
		if(solrResults == null || solrResults.isJsonNull()) {
			return null;
		}
		JsonObject facets = solrResults.get("facet_counts").getAsJsonObject();
		
		if(facets == null || facets.isJsonNull()) {
			return null;
		}

		Map<String, List<FacetModel>> ret = parseFacetFields(facets);
		ret.putAll(parseFacetQueries(facets, "type"));
		String [] rangeFacets = {ISConstants.FIELD_IMAGE_WIDTH, ISConstants.FIELD_IMAGE_HEIGHT};
		ret.putAll(parseFacetRanges(facets, rangeFacets));
		
		return ret;
	}

	Map<String, List<FacetModel>> parseFacetRanges(JsonObject facets, String [] facetNames) {
		Map<String, List<FacetModel>> ret = new LinkedHashMap<String, List<FacetModel>>();
		if(facets == null || facets.isJsonNull()) {
			return ret;
		}

		JsonElement je = facets.get("facet_ranges");
		if(je == null || je.isJsonNull()) {
			return ret;
		}

		JsonObject rangeFacets = je.getAsJsonObject();
		if(rangeFacets == null || rangeFacets.isJsonNull()) {
			return ret;
		}

		for (String name : facetNames) {
			je = rangeFacets.get(name);

			if (je != null && !je.isJsonNull()) {
				JsonObject jo = je.getAsJsonObject();
				if (jo != null && !jo.isJsonNull()) {
					JsonArray children = jo.get("counts").getAsJsonArray();
					Iterator<JsonElement> child_iter = children.iterator();
					List<FacetModel> facetList = new ArrayList<FacetModel>();
					while (child_iter.hasNext()) {
						String field = child_iter.next().getAsString();
						FacetModel facetModel = new FacetModel(getFacetRangeQuery(jo, name, field), getFacetRangeLabel(jo,field));
						facetModel.setParent(name);
						facetModel.setCount(child_iter.next().getAsInt());
						facetList.add(facetModel);
					}
					FacetModel facetModel = new FacetModel(getFacetRangeQuery(jo, name,String.valueOf(jo.get("end").getAsInt())), getFacetRangeLabel(jo, String.valueOf(jo.get("end").getAsInt())));
					facetModel.setCount(jo.get("after").getAsInt());
					facetModel.setParent(name);
					facetList.add(facetModel);
					ret.put(name, facetList);
				}
			}
		}
		return ret;
	}
	

	String getFacetRangeQuery(JsonObject jo, String parent, String rangeStart) {
		int start = -1;
		try {
			start = Integer.parseInt(rangeStart);
		}
		catch (NumberFormatException e) {
			
		}
		int gap = jo.get("gap").getAsInt();
		int end = jo.get("end").getAsInt();

		StringBuffer ret = new StringBuffer();
		String field_name = parent;
		ret.append(field_name).append(":")
		   .append("[").append(start)
		   .append(" TO ");

		if(start == end) {
			ret.append("*]"); 
		}
		else {
		  ret.append(start+gap-1)
		  .append("]");
		}
		return ret.toString();
	}
	
	String getFacetRangeLabel(JsonObject jo, String rangeStart) {
		int start = -1;
		try {
			start = Integer.parseInt(rangeStart);
		}
		catch (NumberFormatException e) {
			
		}
		int gap = jo.get("gap").getAsInt();
		int end = jo.get("end").getAsInt();

		StringBuffer ret = new StringBuffer();
    ret.append(start);
		if(start == end) {
			ret.append(" OR more"); 
		}
		else {
		  ret.append(" TO ").append(start+gap-1);
		}
		return ret.toString();
	}
	
	Map<String, List<FacetModel>> parseFacetFields(JsonObject facets) {
		Map<String, List<FacetModel>> ret = new LinkedHashMap<String, List<FacetModel>>();
		if(facets == null || facets.isJsonNull()) {
			return ret;
		}

		JsonObject jo = facets.get("facet_fields").getAsJsonObject();
		if(jo == null || jo.isJsonNull()) {
			return ret;
		}

		// Impose order of insertion. 
		String [] field_list = new String[]
			{
			  ISConstants.FIELD_EVENT_NAME, 
			  ISConstants.FIELD_GROUNDTRUTH_STATUS,
			  ISConstants.FIELD_COLOR_CHANNELS
			};
		
		for(String f : field_list) {
			JsonArray children = jo.getAsJsonArray(f);
			if(children != null) {
			Iterator<JsonElement> child_iter = children.iterator();
			List<FacetModel> facetList = new ArrayList<FacetModel>();
			while(child_iter.hasNext()) {
				String val = child_iter.next().getAsString();
				String label = val;
				if(f.equals(ISConstants.FIELD_EVENT_NAME) ||
						f.equals(ISConstants.FIELD_GROUNDTRUTH_STATUS)) {
					label = ClientUtils.getFieldLabel(val);
				}
				FacetModel facetModel = new FacetModel(val, label);
				facetModel.setParent(f);
				facetModel.setCount(child_iter.next().getAsInt());
				facetList.add(facetModel);
			}			
			ret.put(f, facetList);
			}
		}
		
		return ret;
	}
	
	Map<String, List<FacetModel>> parseFacetQueries(JsonObject facets, String parentName) {
		
		if(facets == null || facets.isJsonNull()) {
			return null;
		}

		JsonObject jo = facets.get("facet_queries").getAsJsonObject();
		if(jo == null || jo.isJsonNull()) {
			return null;
		}

		Map<String, List<FacetModel>> ret = new LinkedHashMap<String, List<FacetModel>>();

		Iterator<Entry<String,JsonElement>> iter = jo.entrySet().iterator();
		
		List<FacetModel> facetList = new ArrayList<FacetModel>();
		while(iter.hasNext()) {
			Entry<String, JsonElement> e = iter.next();
			FacetModel facetModel = new FacetModel(e.getKey(), ClientUtils.getFieldLabel(e.getKey()));
			facetModel.setParent(parentName);
			facetModel.setCount(e.getValue().getAsInt());
			facetList.add(facetModel);
		}
  	ret.put(parentName, facetList);
  	return ret;
  }
	
	List<PLRecord> parseToPLRecords(String sourceServer, JsonObject jsonResultSet) {

		List<PLRecord> ret = new ArrayList<PLRecord>();
  	try {
    	JsonElement jelement = jsonResultSet.get("response").getAsJsonObject().get("docs");
			if (jelement != null && !jelement.isJsonNull()) {
				JsonArray ja = jelement.getAsJsonArray();
				for (int i = 0; i < ja.size(); i++) {
					PLRecord record = Utils.parseJsonDoc(sourceServer, ja.get(i)
							.getAsJsonObject());
					ret.add(record);
				}
			}
  	}
  	catch (Throwable e) {
  		return ret;
  	}
  	
  	return ret;
  }

	@Override
	public ImageRegionModel[] getFaceMatchRegions(String imageURL) {
    //String regionsSpec = faceFinder.getFaceFinderRegions(imageURL);
    //return Utils.parseToRegionModels(regionsSpec);
		return null;
	}

	@Override
	public void saveRegionsToDB(String image_id, int groundTruthStatus, ImageRegionModel[] regions) throws ImageStatsException {
		try {
			String authorName = getUser();
			if(authorName == null || authorName.trim().length() == 0) {
				authorName = "unauthenticated";
			}
			GroundTruthRecord previous = imageStatsDB.getGroundTruthRecordWithId(image_id);
			if(previous!=null){
				if((regions==null || regions.length==0))
					return;
			ImageRegionModel[] previousRegions = previous.get_final_regions();
			// Check if the regions have changed before saving.
			boolean flag = true;
			if(previousRegions.length != regions.length)
				flag = false;
			else {
				for (int i = 0; i < regions.length;  i++) {
					if (!previousRegions[i].isEqual(regions[i])) {
						flag = false;
						break;
					}
				}
			}
			if(flag == false)
		      imageStatsDB.saveRegions(image_id, authorName, groundTruthStatus, regions);
			}
			else{
				if(!(regions==null || regions.length==0))
					imageStatsDB.saveRegions(image_id, authorName, groundTruthStatus, regions);
			}
		}
		
		catch (SQLException sqle) {
			ImageStatsException ise = new ImageStatsException(sqle.getMessage());
			ise.setStackTrace(sqle.getStackTrace());
			throw ise;
		}
	}
	


	
	@Override
	public GroundTruthRecord getGroundTruthFromDB(String image_id) throws ImageStatsException{
		try {
		  return imageStatsDB.getGroundTruthRecordWithId(image_id);
		}
		catch(SQLException sqle) {
			ImageStatsException ise = new ImageStatsException(sqle.getMessage());
			ise.setStackTrace(sqle.getStackTrace());
			throw ise;
		}
	}	
	
	@Override
	public PLSolrParams getHeaders() {
		PLSolrParams ret = new PLSolrParams();
		Principal p = getThreadLocalRequest().getUserPrincipal();
		String user = getThreadLocalRequest().getRemoteUser();
		
		ret.add("RemoteUser", user);
		ret.add("PrincipalName", p.getName());
		ret.add("PrincipalToString", p.toString());
		return ret;
	}

	@Override
	public String getUser() {
		return Utils.getUser(getThreadLocalRequest());
	}
	
	@Override
	public void logout() {
		getThreadLocalRequest().getSession().invalidate();
	}
	
	
}
