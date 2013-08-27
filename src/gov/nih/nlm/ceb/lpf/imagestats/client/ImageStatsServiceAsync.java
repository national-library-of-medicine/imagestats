package gov.nih.nlm.ceb.lpf.imagestats.client;

import java.io.IOException;
//import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import gov.nih.nlm.ceb.lpf.imagestats.shared.FacetModel;
import gov.nih.nlm.ceb.lpf.imagestats.shared.ImageRegionModel;
import gov.nih.nlm.ceb.lpf.imagestats.shared.GroundTruthRecord;
import gov.nih.nlm.ceb.lpf.imagestats.shared.PLPagingLoadResultBean;
import gov.nih.nlm.ceb.lpf.imagestats.shared.PLSolrParams;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>ImageStatsService</code>.
 */
public interface ImageStatsServiceAsync {
//	void getEventList(AsyncCallback<GetEventListUserResponseType> callback);
	
//	void getEventListMap(AsyncCallback<Map<String, List<FacetModel>>> asyncCallback) throws IOException;
//	void getSearchCount(String query, String event, AsyncCallback<Integer> callback);
	void searchSOLRForEvents(String sourceServer, PLSolrParams searchParams, AsyncCallback<Map<String, List<FacetModel>>> asyncCallback) throws IOException;

	void searchSOLRForPaging(
			String sourceServer, 
			PLSolrParams params,
			AsyncCallback<PLPagingLoadResultBean> callback ) throws IOException;

	void searchSOLR(
			String sourceServer, 
			PLSolrParams params,
			AsyncCallback<String> callback ) throws IOException;
	/*

	void getSearchResults(
			//PagingLoadConfig config,
			String query, 
			String event, 
			int start, 
			int size, 
			String sortBy,
			AsyncCallback<PagingLoadResult<PLRecord>> callback ) throws IOException;
	void getSearchResponse(
			String query, 
			String event, 
			int start, 
			int size, 
			String sortBy,
			AsyncCallback<SearchWithAuthResponseType> callback );
*/
/*
 	void getImageStats(String query, 
			String event, 
			int start, 
			int size, 
			String sortBy,
			AsyncCallback<String> callback);
*/

	void getFaceMatchRegions(String imageURL,
			AsyncCallback<ImageRegionModel[]> callback);

	void saveRegionsToDB(String image_id, int groundTruthStatus,
			ImageRegionModel[] regions, AsyncCallback<Void> callback);

	void getGroundTruthFromDB(String image_id, AsyncCallback<GroundTruthRecord> callback);
	void getHeaders(AsyncCallback<PLSolrParams> callback);
	void getUser(AsyncCallback<String> callback);
	void logout(AsyncCallback<Void> callback);
}
