package gov.nih.nlm.ceb.lpf.imagestats.server;

import gov.nih.nlm.ceb.lpf.imagestats.shared.ClientUtils;
import gov.nih.nlm.ceb.lpf.imagestats.shared.FacetModel;
import gov.nih.nlm.ceb.lpf.imagestats.shared.ISConstants;
import gov.nih.nlm.ceb.lpf.imagestats.shared.ImageStatsException;
import gov.nih.nlm.ceb.lpf.imagestats.shared.PLPagingLoadResultBean;
import gov.nih.nlm.ceb.lpf.imagestats.shared.PLRecord;
import gov.nih.nlm.ceb.lpf.imagestats.shared.PLSolrParams;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import com.google.gson.JsonObject;

public class SearchFileSystem {
	
	ImageStatsDB imageStatsDB = null;
	
	public SearchFileSystem(ImageStatsDB idb) {
		imageStatsDB = idb;
	}

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
					if(!searchBoxCriteria(SearchName, ImageName, Event, subType))
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
					if(!searchBoxCriteria(SearchName, ImageName, Event, subType))
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
	
	private boolean searchBoxCriteria(String searchName, String ImageName, String Directory, String type) throws ImageStatsException{
		String[] tempSearchName = searchName.split(":");
		
		ImageName = ImageName.toLowerCase();
		Directory = Directory.toLowerCase();
		String nameWithoutExt = "";
		try{
		if(ImageName.contains("."))
			nameWithoutExt = ImageName.substring(0,(ImageName.lastIndexOf(".", ImageName.length()-1)));
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
		
		String[] a = s1.split("\\*");
		if(s1.charAt(0)=='*'){
			if(a.length>0)
				if(s2.contains(a[0]))
					s2 = s2.substring(s2.indexOf(a[0]));
				else
					return false;
			else
				s2 = "";
		}
		for(String s:a){
			if(s2.startsWith(s))
				s2 = s2.replaceFirst(s, "");
			else{
				while((!s2.equals("")) && (!s2.startsWith(s)))
					s2 = s2.substring(1,s2.length());
				if(s2.equals(""))
					return false;
				else
					s2 = s2.replaceFirst(s, "");
			}
		}
		if(s1.charAt(s1.length()-1) == '*')
			return true;
		if(!s2.equals(""))
			return false;
		return true;
	}
	
	JsonObject searchForImages(String sourceUrl, HttpServletRequest request) {
		return null;
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
	            //log(e.getMessage());
	        } finally {
	            reader.dispose();
	        }
	    } else {
	        //log("No reader found for given format: " + suffix);
	    }
	    return ret;
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
	
	JsonObject searchWithUrls(String solrServer, String [] imageUrls) {
	  return null;	
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

	

}
