package gov.nih.nlm.ceb.lpf.imagestats.client;

import java.util.List;
import java.util.Map;

import gov.nih.nlm.ceb.lpf.imagestats.shared.FacetModel;
import gov.nih.nlm.ceb.lpf.imagestats.shared.ImageRegionModel;
import gov.nih.nlm.ceb.lpf.imagestats.shared.GroundTruthRecord;
import gov.nih.nlm.ceb.lpf.imagestats.shared.ImageStatsException;
import gov.nih.nlm.ceb.lpf.imagestats.shared.PLPagingLoadResultBean;
import gov.nih.nlm.ceb.lpf.imagestats.shared.PLSolrParams;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("stats")
public interface ImageStatsService extends RemoteService {
//	GetEventListUserResponseType getEventList();
//	Map<String, List<FacetModel>> getEventListMap()throws IOException;
	Map<String, List<FacetModel>> searchSOLRForEvents(String sourceServer, PLSolrParams searchParams) throws ImageStatsException;
//	int getSearchCount(String query, String event);
	
	//SearchWithAuthResponseType getSearchResponse(String query, String event, int start, int size, String sortBy);

//	String getImageStats(String query, String event, int start, int size, String sortBy);

	PLPagingLoadResultBean searchSOLRForPaging(String sourceServer, PLSolrParams params)throws ImageStatsException;
	String searchSOLR(String sourceServer, PLSolrParams params)throws ImageStatsException;
/*			
	PagingLoadResult<PLRecord> getSearchResults(
			String query, 
			String event, 
			int start, 
			int size, 
			String sortBy) throws IOException;
			*/
	ImageRegionModel[] getFaceMatchRegions(String imageURL);
	void saveRegionsToDB(int image_id, int groundTruthStatus, ImageRegionModel[] regions) throws ImageStatsException;
	GroundTruthRecord getGroundTruthFromDB(int image_id) throws ImageStatsException;
	PLSolrParams getHeaders();
	String getUser();
	void logout();
	
}
