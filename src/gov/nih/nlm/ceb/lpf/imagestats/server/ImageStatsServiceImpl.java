package gov.nih.nlm.ceb.lpf.imagestats.server;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.sql.DataSource;
import javax.swing.ImageIcon;
import javax.xml.ws.WebServiceException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import gov.nih.nlm.ceb.lpf.imagestats.client.ImageStatsService;
//import gov.nih.nlm.ceb.lpf.imagestats.server.FaceFinderClientFactory.FACE_FINDER_PACKAGE;
import gov.nih.nlm.ceb.lpf.imagestats.shared.ClientUtils;
import gov.nih.nlm.ceb.lpf.imagestats.shared.FacetModel;
//import gov.nih.nlm.ceb.lpf.imagestats.shared.FieldVerifier;
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
import com.google.gson.JsonParser;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ImageStatsServiceImpl extends RemoteServiceServlet implements
		ImageStatsService {
	public static Pattern region_pat = Pattern.compile("^\\s*([a-zA-Z]+)\\[(\\d+),(\\d+);(\\d+),(\\d+)\\]\\s*$");
	//SearchPL plWS = null;
	//SearchPLUsingSOLR pl = null;
	//public String DefaultSolrUrl = "http://plstage.nlm.nih.gov:8983/solr/imagestats/select";
	//FaceFinderClient faceFinder = null;
	ImageStatsDB imageStatsDB = null;
	UserRoleDB userDB = null;

	
	@Override
	public void init() throws ServletException{
		super.init();
		try {
			//plWS = new SearchPL();
			//plWS.init();
			//pl = new SearchPLUsingSOLR();
			try {
			  //faceFinder = FaceFinderClientFactory.getInstance(FACE_FINDER_PACKAGE.FACE_FINDER_FM15);
			  //faceFinder = FaceFinderClientFactory.getInstance(FACE_FINDER_PACKAGE.FACE_FINDER_FACEMATCHER);
			  //faceFinder.init();
			}
			catch(WebServiceException wse) {
				
			}

			InitialContext cxt = new InitialContext();

			DataSource plDataSource = (DataSource) cxt.lookup( "java:/comp/env/jdbc/pl" );
			Connection con = plDataSource.getConnection();
			con.close();
			con = plDataSource.getConnection();
			con.close();
			imageStatsDB = new ImageStatsDB(plDataSource);
/*			DataSource usersDataSource = (DataSource) cxt.lookup( "java:/comp/env/jdbc/users" );
			userDB = new UserRoleDB(usersDataSource);*/
			//imageStatsDB = null;
			userDB = null;
			//pl.setUserDB(userDB);
/*			
			try {
			  String solrUrl = (String) cxt.lookup( "java:/comp/env/defaultSolrUrl" );
			  if(solrUrl != null) {
				  DefaultSolrUrl = solrUrl;
			  }
			}
			catch(Exception e) {
				
			}
*/
			
		} catch (Exception e) {
			throw new ServletException(e);
		}
		try{
			InitialContext cxt = new InitialContext();
	    	String source = (String)cxt.lookup("java:comp/env/repository/directory");
	    }catch(NamingException e){throw new ServletException("Configuration Problem: Environment variable repository directory");}
		try{
	    	InitialContext ic = new InitialContext();
	    	String virtualAddress = (String)ic.lookup("java:comp/env/repository/virtual");
	    }catch(NamingException e){throw new ServletException("Configuration Problem: Environment variable Virtual Repository");}
	}

/*	
	public GetEventListUserResponseType getEventList() {
    return plWS.getEventListObj();
	}
*/	
	/*@Override
	public Map<String, List<FacetModel>> searchSOLRForEvents(String source, PLSolrParams searchParams) throws ImageStatsException {
		//String jsonStr = searchSOLR(source, searchParams);
		//JsonObject solrResults = new JsonParser().parse(jsonStr).getAsJsonObject();
		//Map <String, List<FacetModel>> facets = parsePLFacets(solrResults);
		Map <String, List<FacetModel>> facets = null;
		
		List<NameValuePair> data = new ArrayList<NameValuePair>();
	      Iterator<String> iter = searchParams.keySet().iterator();
	      while(iter.hasNext()) {
	      	String name = iter.next();
	      	Set<String> vals = searchParams.get(name);
	      	Iterator<String> vals_iter = vals.iterator();
	      	while(vals_iter.hasNext()) {
	          data.add(new BasicNameValuePair(name, vals_iter.next()));
	      	}
	      }
	      Iterator it = data.iterator();
	      while(it.hasNext()){
	    	  NameValuePair n = (NameValuePair)it.next();
	    	  if(n!=null)
	    		  System.out.println(n.getName()+'\t'+n.getValue()+'\n');
	      }
    return facets;
	}*/
	

	public Map<String, List<FacetModel>> searchSOLRForEvents(String source, PLSolrParams searchParams) throws ImageStatsException {
		
		//System.out.println(searchParams.toString());
		try{
	    	InitialContext ic = new InitialContext();
	    	source = (String)ic.lookup("java:comp/env/repository/directory");
	    }catch(NamingException e){e.printStackTrace();}
		//System.out.println(source);
		Map <String, List<FacetModel>> facets = new LinkedHashMap<String, List<FacetModel>>();
		Set<String> EventParams = searchParams.get(ISConstants.FIELD_EVENT_NAME);
		Set<String> TypeParams = searchParams.get(ISConstants.IMAGE_TYPE);
		Set<String> GroundTruthParams = searchParams.get(ISConstants.FIELD_GROUNDTRUTH_STATUS);
		Set<String> NameParams = searchParams.get(ISConstants.FIELD_FULL_NAME);
		String SearchName = "*";
		if(NameParams!=null &&NameParams.size()!=0){
			SearchName = NameParams.iterator().next();
		}
		if(SearchName==null||SearchName == "")
			SearchName = "*";
		int jpegCount=0, pngCount=0, gifCount=0;
		int gts0=0, gts1=0, gts2=0, gts3=0;
		ArrayList<FacetModel> EventFacets= new ArrayList<FacetModel>();
		File baseDir = new File(source);
		String[] listEvents = baseDir.list();
		if(listEvents == null)
			listEvents = new String[0];
		for(String Event:listEvents){
			boolean checkEvent = isParamSelected(Event, EventParams);
			int EventCount = 0;
			if(checkEvent){
				
				File EventDir = new File(source + "/" + Event);
				if(!EventDir.isDirectory())
					continue;
				String[] Images = EventDir.list();
				if(Images == null)
					Images = new String[0];
				for(String ImageName:Images){
					
					String mimetype="";
					try{
				        mimetype = Files.probeContentType(Paths.get(source+"/"+Event+"/"+ImageName));
				    }catch(IOException e){throw new ImageStatsException(e.getMessage());}
				    String type = "";
				    String subType = "";
					if(mimetype!=null){
				        type = mimetype.split("/")[0];
				        subType = mimetype.split("/")[1];
					}
					if(!searchBoxCriteria(SearchName, ImageName, Event))
						continue;
					
				    
					Connection con = null;
					Statement st = null;
					ResultSet rs = null;
					int GroundTruthStatus = 0;
					try{
						DataSource plDataSource = imageStatsDB.plstageDataSource;
						con = plDataSource.getConnection();
						st = con.createStatement();
						String query = "SELECT * FROM imagestats WHERE image_id=\""+(Event+"/"+ImageName+"\"");
						rs = st.executeQuery(query);
						//String final_regions = rs.getString("final_regions");
						//String initial_regions = rs.getString("initial_regions");
						try{
							GroundTruthStatus = (rs.getInt("groundTruthStatus"));
						}catch(Exception e){}
						
					} catch (SQLException e) {
						throw new ImageStatsException("Error created by PL Database");
					}finally{
							if (rs != null) try { rs.close(); } catch (SQLException ignore) {}
			        		if (st != null) try { st.close(); } catch (SQLException ignore) {}
			        		if (con != null) try { con.close(); } catch (SQLException ignore) {}
						}
					
					
				    if(type!=null&&type.equals("image")){
				    	boolean checkType = isParamSelected(subType, TypeParams);
				    	boolean checkGTS = isParamSelected(((Integer)GroundTruthStatus).toString(), GroundTruthParams);
				    	if(checkType && checkGTS){
				    		if(subType.equalsIgnoreCase("jpg")||subType.equalsIgnoreCase("jpeg"))
				    			jpegCount++;
				    		else if(subType.equalsIgnoreCase("png"))
				    			pngCount++;
				    		else if(subType.equalsIgnoreCase("gif"))
				    			gifCount++;
				    		
				    		if(GroundTruthStatus==0)
				    			gts0++;
				    		else if(GroundTruthStatus==1)
				    			gts1++;
				    		else if(GroundTruthStatus==2)
				    			gts2++;
				    		else if(GroundTruthStatus==3)
				    			gts3++;
				    		EventCount ++;
				    	}
				        
				    }
				}
			}
			EventFacets.add(new FacetModel(Event,ClientUtils.getFieldLabel(Event), ISConstants.FIELD_EVENT_NAME, EventCount));
		}
		
		ArrayList<FacetModel> type = new ArrayList<FacetModel>();
		type.add(new FacetModel(ISConstants.JPEG_TYPE_QUERY_LABEL,ISConstants.JPEG_TYPE_QUERY_LABEL, ISConstants.IMAGE_TYPE, jpegCount));
		type.add(new FacetModel(ISConstants.PNG_TYPE_QUERY_LABEL,ISConstants.PNG_TYPE_QUERY_LABEL, ISConstants.IMAGE_TYPE, pngCount));
		type.add(new FacetModel(ISConstants.GIF_TYPE_QUERY_LABEL,ISConstants.GIF_TYPE_QUERY_LABEL, ISConstants.IMAGE_TYPE, gifCount));
		
		ArrayList<FacetModel> gts = new ArrayList<FacetModel>();
		gts.add(new FacetModel(ISConstants.GROUNDTRUTH_STATUS_ZERO, ClientUtils.getFieldLabel(ISConstants.GROUNDTRUTH_STATUS_ZERO), ISConstants.FIELD_GROUNDTRUTH_STATUS, gts0));
		//gts.add(new FacetModel(ISConstants.GROUNDTRUTH_STATUS_ONE, ClientUtils.getFieldLabel(ISConstants.GROUNDTRUTH_STATUS_ONE), ISConstants.FIELD_GROUNDTRUTH_STATUS, gts1));
		//gts.add(new FacetModel(ISConstants.GROUNDTRUTH_STATUS_TWO, ClientUtils.getFieldLabel(ISConstants.GROUNDTRUTH_STATUS_TWO), ISConstants.FIELD_GROUNDTRUTH_STATUS, gts2));
		gts.add(new FacetModel(ISConstants.GROUNDTRUTH_STATUS_THREE, ClientUtils.getFieldLabel(ISConstants.GROUNDTRUTH_STATUS_THREE), ISConstants.FIELD_GROUNDTRUTH_STATUS, gts3));
		
		facets.put(ISConstants.FIELD_EVENT_NAME, EventFacets);
		facets.put(ISConstants.IMAGE_TYPE, type);
		facets.put(ISConstants.FIELD_GROUNDTRUTH_STATUS, gts);
		return facets;
	}
	
	boolean isParamSelected(String Event, Set<String> EventParams){
		boolean checkEvent = false;
		if (EventParams == null)
			EventParams = new HashSet<String>();
		for(String EventParam:EventParams){
			if(EventParam.equalsIgnoreCase(Event)||EventParam.equalsIgnoreCase("disable")){
				checkEvent = true;
				break;
			}
		}
		return checkEvent;
	}
/*	
	public Map<String, List<FacetModel>> getEventListMap() throws IOException{
		PLSolrParams searchParams = ClientUtils.buildDefaultSOLRParams();
		return searchSOLRForEvents(DefaultSolrUrl, searchParams);
	}
*/	
/*	
	public SearchWithAuthResponseType getSearchResponse(String query, String event, int start, int size, String sortBy) {
		SearchWithAuthResponseType ret = null;
    if(FieldVerifier.isValidQuery(query)) {
      ret = plWS.searchWithAuth(query, event, start, size, sortBy);
    }
		
		return ret;
	}
*/	
/*	
	public String searchSOLR(String source, String query, String event, int start, int size, String sortBy) throws Exception {
		PLSolrParams searchParams = new PLSolrParams();
		searchParams = searchParams.add("qt", "edismax")
		            .add("q", query)
		            .add("fq", ISConstants.FIELD_EVENT_NAME+": ("+event+")")
		            .add("fq", "url_thumb:[* TO *]")
		            .add("start", String.valueOf(start))
		            .add("rows", String.valueOf(size))
		            .add("wt", "json")
                .add("sort", "created desc");
		return searchSOLR(source, searchParams);
	}
*/	
	
	public String searchSOLR(String sourceServer, PLSolrParams searchParams) throws ImageStatsException {
		//try {
		  //return pl.search(sourceServer, searchParams, getUser());
			return null;
		/*}
		catch(IOException ioe) {
			ImageStatsException ise = new ImageStatsException(ioe.getMessage());
			ise.setStackTrace(ioe.getStackTrace());
			throw ise;
		}*/
	}

	@Override
	/*public PLPagingLoadResultBean searchSOLRForPaging(String sourceServer, PLSolrParams searchParams) throws ImageStatsException {
		PLPagingLoadResultBean ret = null;
		/*searchParams.add("wt", "json");
		String jsonStr = searchSOLR(sourceServer, searchParams);

		JsonObject solrResults = new JsonParser().parse(jsonStr).getAsJsonObject();
		int total = extractTotalCount(solrResults);
		List<PLRecord> recordList = parseToPLRecords(sourceServer, solrResults);
		Map <String, List<FacetModel>> facets = parsePLFacets(solrResults);

    Set<String> v = searchParams.get("start");
    int start = 0;
    if(v != null) {
    	start = Integer.parseInt(v.iterator().next());
    }
		ret = new PLPagingLoadResultBean(recordList, total, start, facets);*/
		
		//return ret;
	//}
	
	public PLPagingLoadResultBean searchSOLRForPaging(String sourceServer, PLSolrParams searchParams) throws ImageStatsException {
		String source = "";
		try{
	    	InitialContext ic = new InitialContext();
	    	source = (String)ic.lookup("java:comp/env/repository/directory");
	    }catch(NamingException e){e.printStackTrace();}
		sourceServer = source;
		PLPagingLoadResultBean retpaging = null;
		int total = 0;
		List<PLRecord> recordList = new ArrayList<PLRecord>();
		
/*
		System.out.println(getServletContext().getContextPath());
		try{
		System.out.println(getServletContext().getResource("/repo").toString());
		}catch(Exception e){System.out.println("Not found repo");}
		System.out.println(getServletContext().getRealPath("/repo"));
		Enumeration enumer =getServletContext().getAttributeNames();
	    while(enumer.hasMoreElements()){
	    	System.out.println(enumer.nextElement());
	    }
	    System.out.println(getServletContext().getContext("/repo"));*/
	    
		
		//System.out.println(searchParams.toString());
		Set<String> EventParams = searchParams.get(ISConstants.FIELD_EVENT_NAME);
		Set<String> TypeParams = searchParams.get(ISConstants.IMAGE_TYPE);
		Set<String> GroundTruthParams = searchParams.get(ISConstants.FIELD_GROUNDTRUTH_STATUS);
		Set<String> NameParams = searchParams.get(ISConstants.FIELD_FULL_NAME);
		String SearchName = "*";
		if(NameParams!=null &&NameParams.size()!=0){
			SearchName = NameParams.iterator().next();
		}
		if(SearchName == null || SearchName == "")
			SearchName = "*";
		//System.out.println(SearchName);
		boolean flag = false;
		for(String s:TypeParams){
			if(s.equalsIgnoreCase("jpg OR jpeg"))
				flag = true;
		}
		if(flag == true){
			TypeParams.remove("jpg OR jpeg");
			TypeParams.add("jpg");
			TypeParams.add("jpeg");
		}
		/*for(String s:TypeParams){
			System.out.println(s);
		}*/

		File baseDir = new File(sourceServer);
		String[] listEvents = baseDir.list();
		if(listEvents == null)
			listEvents = new String[0];
		for(String Event:listEvents){
			boolean checkEvent = isParamSelected(Event, EventParams);
			if(checkEvent){
				
				File EventDir = new File(sourceServer + "/" + Event);
				if(!EventDir.isDirectory())
					continue;
				String[] Images = EventDir.list();
				if(Images == null)
					Images = new String[0];
				for(String ImageName:Images){
				

					
					//if(SearchName.contains(ImageName));
					//System.out.println(ImageName);
					String mimetype="";
					try{
				        mimetype = Files.probeContentType(Paths.get(sourceServer+"/"+Event+"/"+ImageName));
				    }catch(IOException e){throw new ImageStatsException(e.getMessage());}
					String type = "";
					String subType = "";
					if(mimetype!=null){
						type = mimetype.split("/")[0];
						subType = mimetype.split("/")[1];
					}
					if(!searchBoxCriteria(SearchName, ImageName, Event))
						continue;
					
				    if(type!=null&&type.equals("image")){
				    	boolean checkType = isParamSelected(subType, TypeParams);
				    	if(checkType){
				    		PLRecord record = addPLRecord(sourceServer+"/"+Event+"/"+ImageName);
				    		int gts = record.getGroundTruthStatus();
				    		boolean checkGTS = isParamSelected(((Integer)gts).toString(), GroundTruthParams);
				    		if(checkGTS==true){
				    			recordList.add(record);
				    			total++;
				    		}
				    	}
				        
				    }
				}
			}
			
		}

		Set<String> v = searchParams.get("start");
		int start = 0;
	    if(v != null) {
	    	start = Integer.parseInt(v.iterator().next());
	    }
	    v = searchParams.get("rows");
		int row = 0;
	    if(v != null) {
	    	row = Integer.parseInt(v.iterator().next());
	    }
	    Map <String, List<FacetModel>> facets = searchSOLRForEvents(sourceServer, searchParams);
	    List<PLRecord> sortedRecordList = sortPLRecordList(recordList);
	    sortedRecordList = sample(sortedRecordList, start, row);
	    retpaging = new PLPagingLoadResultBean(sortedRecordList, total, start, facets);
		return retpaging;
	}
	
	private boolean searchBoxCriteria(String searchName, String ImageName, String Directory) throws ImageStatsException{
		String[] tempSearchName = searchName.split(":");
		
		ImageName = ImageName.toLowerCase();
		Directory = Directory.toLowerCase();
		String nameWithoutExt = "";
		try{
		if(ImageName.contains("."))
			nameWithoutExt = ImageName.substring(0,(ImageName.lastIndexOf(".", ImageName.length()-1)));
		else
			nameWithoutExt = ImageName;
		}catch(Exception e){}
		if(tempSearchName.length == 2){
			
			String field_name = tempSearchName[0].toLowerCase().trim();
			String query_text = tempSearchName[1].trim();
			if(query_text.equals("")){
				//TODO Throw an error Query Text not correct.
				throw new ImageStatsException("Query Text Empty");
			}
		if(field_name.equals("image_id")){
			return matchStrings(query_text, Directory+"/"+ImageName)||matchStrings(query_text, Directory+"/"+nameWithoutExt);
		}
		else if(field_name.equals("image_name")){
			return matchStrings(query_text, ImageName)||matchStrings(query_text, nameWithoutExt);
		}
		else if(field_name.equals("collection")){
			return matchStrings(query_text, Directory);
		}
		else if(field_name.equals("*")){
			return matchStrings(query_text, Directory)||matchStrings(query_text, ImageName)||matchStrings(query_text, Directory+'/'+ImageName)||matchStrings(query_text, nameWithoutExt)||matchStrings(query_text, Directory+'/'+nameWithoutExt);
		}
		else{
			//TODO Throw an error, field_name not valid.
			throw new ImageStatsException("Field Name not valid."+'\n'+"Valid Field names:"+'\n'+"Collections, image_name, image_id");

		}
		
		}
		else if(tempSearchName.length == 1){
			if(searchName.charAt(searchName.length()-1) == ':')
				throw new ImageStatsException("Query Text Empty");
			if(tempSearchName[0].trim()==""){
				//TODO Throw an error Query Text not correct.
				throw new ImageStatsException("Search Text Empty");
			}
			String query_text = tempSearchName[0];
			return matchStrings(query_text, Directory)||matchStrings(query_text, ImageName)||matchStrings(query_text, Directory+'/'+ImageName)||matchStrings(query_text, nameWithoutExt)||matchStrings(query_text, Directory+'/'+nameWithoutExt);
		}
		else{
			//TODO Throw an error.
			throw new ImageStatsException("More than 1 Field Qualifiers present.");
		}
	}
	
	private boolean matchStrings(String s1, String s2){
		String[] searchArray = s1.trim().split(" ");
		
		int i=0;
		for(String s:searchArray){
			if( s.equals("AND")){
				String compared = "";
				for(int j=0;j<i;j++){
					compared = compared + " " + searchArray[j];
				}
				String remaining = "";
				for(int j=i+1;j<searchArray.length;j++){
					remaining = remaining + " " + searchArray[j];
				}
				return matchStrings(compared.trim(), s2)&&matchStrings(remaining.trim(), s2);
			}
			i++;
		}
		for(String s:searchArray){
			if( !s.equals("OR")){
				if(!s.equals(""))
				if(matchStringsWithWildCards(s.toLowerCase(), s2))
					return true;
			}
		}
		return false;
	}
	private boolean matchStringsWithWildCards(String s1, String s2){
		
		if(s1.length()==0)
			if(s2.length()==0) return true;
			else return false;
		else if(s1.length()==1){
			if(s1.charAt(0)=='*')
				return true;
			else{
				if(s2.length()==1 && s1.charAt(0) == s2.charAt(0)) return true;
				else return false;
			}
		}
		else{
			if(s1.charAt(0)=='*'){
				String temp = s1.substring(1);
				if(s2.length()==0)
					return false;
				return(matchStringsWithWildCards(temp, s2)||matchStringsWithWildCards(s1, s2.substring(1)));
			}
			else{
				if(s2.length()==0)
					return false;
				else{
					if(s1.charAt(0)==s2.charAt(0))
						return matchStringsWithWildCards(s1.substring(1), s2.substring(1));
					else return false;
				}
			}
		}/*
		String[] a = s1.split("\\*");
		ArrayList<String> arl = new ArrayList<String>();
		if(s1.charAt(0)=='*'){
			if(a.length>1)
				if(s2.contains(a[1])){
					while(s2.contains(a[1])){
						s2 = s2.substring(s2.indexOf(a[1]));
						arl.add(s2);
						s2 = s2.substring(a[1].length());
					}
					
				}
				else
					return false;
			else
				return true;
		}
		else arl.add(s2);
		outer:
		for(String s3:arl){
		if(a.length>=1){
			if(s3.startsWith(a[0]))
				s3 = s3.replaceFirst(a[0], "");
			else
				continue;
		}
		else continue;
		int count =0;
		for(String s:a){
			if (count==0) continue;
			if(s3.startsWith(s))
				s3 = s3.replaceFirst(s, "");
			else{
				while((!s3.equals("")) && (!s3.startsWith(s)))
					s3 = s3.substring(1,s3.length());
				if(s2.equals(""))
					continue outer;
				else
					s3 = s3.replaceFirst(s, "");
			}
			count++;
		}
		if(s1.charAt(s1.length()-1) == '*')
			return true;
		if(!s3.equals(""))
			continue;
		return true;
		}
		return false;*/
	}
	private List<PLRecord> sample(List<PLRecord> recordList, int start,
			int row) {
		List<PLRecord> ret = new ArrayList<PLRecord>();
		Iterator<PLRecord> iter = recordList.iterator();
		int i=0;
		while(iter.hasNext()){
			i++;
			if(i>start&&i<=start+row)
				ret.add(iter.next());
			else
				iter.next();
		}
		return ret;
	}

	List<PLRecord> sortPLRecordList(List<PLRecord> recordList){
		List<PLRecord> ret = new ArrayList<PLRecord>();
		
		int num = recordList.size();
		for(int i=0;i<num;i++){
			Iterator<PLRecord> it = recordList.iterator();
			
			if(it==null){//System.out.println("Break");
				break;}
			PLRecord min = it.next();
			while(it.hasNext()){
				PLRecord curr = it.next();
				if((curr.get_image_id()).compareTo(min.get_image_id())<0){
					min = curr;
				}
				
			}
			it = null;
			ret.add(min);
			recordList.remove(min);
		}
		
		return ret;
	}
	
	PLRecord addPLRecord(String ImageUrl) throws ImageStatsException{
		PLRecord ret = new PLRecord();

	    File imageFile = new File(ImageUrl);

	    //System.out.println(javax.servlet.http.HttpServletrequest#getContextPath());
	    
	    String virtualAddress="";
	    try{
	    	InitialContext ic = new InitialContext();
	    	virtualAddress = (String)ic.lookup("java:comp/env/repository/virtual");
	    }catch(NamingException e){e.printStackTrace();}
	    
	    ret.setEventName(imageFile.getParentFile().getName());
	    ret.setName(imageFile.getName());
	    ret.setUrl_thumb(virtualAddress+"/"+ret.getEventName()+"/"+ret.getName());
	    
	    ret.set_image_id(ret.getEventName()+"/"+ret.getName());
	    ret.setUrl(virtualAddress+"/"+ret.getEventName()+"/"+ret.getName());
		ret.setColorChannels(3);
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try{
			DataSource plDataSource = imageStatsDB.plstageDataSource;
			con = plDataSource.getConnection();
			st = con.createStatement();
			String query = "SELECT * FROM imagestats WHERE image_id=\""+(ret.getEventName()+"/"+ret.getName())+"\"";
			rs = st.executeQuery(query);
			//String final_regions = rs.getString("final_regions");
			//String initial_regions = rs.getString("initial_regions");
			try{
				ret.set_final_update_time(rs.getDate("final_updated_time"));
				ret.set_initial_update_time(rs.getDate("final_updated_time"));
				ret.setGroundTruthStatus(rs.getInt("groundTruthStatus"));
			}catch(Exception e){}
			//System.out.println(ret.getGroundTruthStatus());
		} catch (SQLException e) {
			throw new ImageStatsException("Error created by PL Database");
		}finally{
				if (rs != null) try { rs.close(); } catch (SQLException ignore) {}
        		if (st != null) try { st.close(); } catch (SQLException ignore) {}
        		if (con != null) try { con.close(); } catch (SQLException ignore) {}
			}
		
		
		
		//Image image = Toolkit.getDefaultToolkit().getImage(ImageUrl);
		/*ImageIcon icon = new ImageIcon(image);*/
		/*Iterator<ImageReader> itera = ImageIO.getImageReaders(imageFile);
		try{
		if(itera!=null&&itera.hasNext()){
			ImageReader imr = itera.next();
			int height = imr.getHeight(0);
			int width = imr.getWidth(0);
			ret.setImageHeight(height);
			ret.setImageWidth(width);

		}}catch(Exception e){e.printStackTrace();}*/


		String mimetype="";
		try{
	        mimetype = Files.probeContentType(Paths.get(ImageUrl));
	    }catch(IOException e){throw new ImageStatsException(e.getMessage());}
		String subType = "";
		if(mimetype!=null){
			subType = mimetype.split("/")[1];
		}

		String suffix = subType;
	    Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix(suffix);
	    if (iter.hasNext()) {
	        ImageReader reader = iter.next();
	        try {
	            ImageInputStream stream = new FileImageInputStream(new File(ImageUrl));
	            reader.setInput(stream);
	            ret.setImageWidth(reader.getWidth(reader.getMinIndex()));
	            ret.setImageHeight(reader.getHeight(reader.getMinIndex()));
	        } catch (IOException e) {
	            log(e.getMessage());
	        } finally {
	            reader.dispose();
	        }
	    } else {
	        log("No reader found for given format: " + suffix);
	    }
	    return ret;
	}
	
/*
	public PagingLoadResult<PLRecord> getSearchResults(
			String query, 
			String event,
			int start,
			int size,
			String sortBy) throws Exception {
		PagingLoadResult<PLRecord> ret = null;
		String jsonStr = searchSOLR(DefaultSolrUrl, query, event, start, size, sortBy);

		JsonObject solrResults = new JsonParser().parse(jsonStr).getAsJsonObject();
		int total = extractTotalCount(solrResults);
		List<PLRecord> recordList = parseToPLRecords(DefaultSolrUrl, solrResults);

		Map <String, List<FacetModel>> facets = parsePLFacets(solrResults.get("facet_counts").getAsJsonObject());

		ret = new PLPagingLoadResultBean(recordList, total, start, facets);

		return ret;
	}

*/	
	
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
		
    /*
		Iterator<Entry<String,JsonElement>> iter = jo.entrySet().iterator();
		while(iter.hasNext()) {
			Entry<String, JsonElement> e = iter.next();
			String field = e.getKey();
			JsonArray children = e.getValue().getAsJsonArray();
			Iterator<JsonElement> child_iter = children.iterator();
			List<FacetModel> facetList = new ArrayList<FacetModel>();
			while(child_iter.hasNext()) {
				String shortname = child_iter.next().getAsString();
				FacetModel facetModel = new FacetModel(shortname, ClientUtils.getFieldLabel(shortname));
				facetModel.setParent(field);
				facetModel.setCount(child_iter.next().getAsInt());
				facetList.add(facetModel);
			}			
			ret.put(field, facetList);
		}
		*/
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

/*
	GroundTruthRecord parseGroundTruthRecord(JsonObject solrDoc) {
		GroundTruthRecord record = new GroundTruthRecord();
		
		JsonElement je = solrDoc.get(ISConstants.FIELD_IMAGE_ID);
  	if(je != null && !je.isJsonNull()) {
    	int i = je.getAsInt();
	  	record.set_image_id(i);
  	}
  	je = solrDoc.get(ISConstants.FIELD_GT_INITIAL_PERSON);
  	if(je != null && !je.isJsonNull()) {
    	String v = je.getAsString();
	  	record.set_gt_initial_person(v);
  	}
  	je = solrDoc.get(ISConstants.FIELD_GT_FINAL_PERSON);
  	if(je != null && !je.isJsonNull()) {
    	String v = je.getAsString();
	  	record.set_gt_final_person(v);
  	}
  	je = solrDoc.get(ISConstants.FIELD_INITIAL_REGIONS);
  	if(je != null && !je.isJsonNull()) {
    	String v = je.getAsString();
    	if(v != null) {
  	  	record.set_initial_regions(Utils.parseToRegionModels(v));
    	}
  	}
  	je = solrDoc.get(ISConstants.FIELD_FINAL_REGIONS);
  	if(je != null && !je.isJsonNull()) {
    	String v = je.getAsString();
    	if(v != null) {
  	  	record.set_final_regions(Utils.parseToRegionModels(v));
    	}
  	}
  	je = solrDoc.get(ISConstants.FIELD_INITIAL_UPDATED_TIME);
  	if(je != null && !je.isJsonNull()) {
    	String v = je.getAsString();
    	if(v != null) {
  	  	record.set_initial_update_time(Utils.parseSolrDate(v));
    	}
  	}
  	je = solrDoc.get(ISConstants.FIELD_FINAL_UPDATED_TIME);
  	if(je != null && !je.isJsonNull()) {
    	String v = je.getAsString();
    	if(v != null) {
  	  	record.set_final_update_time(Utils.parseSolrDate(v));
    	}
  	}
  	je = solrDoc.get(ISConstants.FIELD_GROUNDTRUTH_STATUS);
  	if(je != null && !je.isJsonNull()) {
    	int v = je.getAsInt();
	  	record.set_grounfTruthStatus(v);
  	}
  	
  	return record;
	}
*/
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
			boolean flag = true;
			if(previousRegions.length != regions.length)
				flag = false;
			else{
				int i=0;
			for(ImageRegionModel irm: previousRegions){
				if(!previousRegions[i].isEqual(regions[i]))
					flag = false;
				i++;
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
