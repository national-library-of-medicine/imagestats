package gov.nih.nlm.ceb.lpf.imagestats.shared;

//import gov.nih.nlm.ceb.lpf.imagestats.server.SearchPL;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

//import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class Utils {
	public static String solrDateFormat = "yyyy-MM-dd'T'hh:mm:ss'Z'";

	public static Pattern region_pat = Pattern.compile("^\\s*([a-zA-Z]+)\\[(\\d+),(\\d+);(\\d+),(\\d+)\\]\\s*$");

	public static String[] extractField(String jsonResultsStr, String fieldName) {
		String[] ret = null;
		if(jsonResultsStr == null || jsonResultsStr.trim().length() == 0) {
			return ret;
		}
		ArrayList<String> al = new ArrayList<String>();
	  JsonParser jsonParser = new JsonParser();
	  JsonArray jsonArray = null;
    jsonArray = jsonParser.parse(jsonResultsStr).getAsJsonArray();
    Iterator<JsonElement> iter = jsonArray.iterator();
    while(iter.hasNext()) {
    	JsonElement f = iter.next().getAsJsonObject().get(fieldName);
    	al.add(f.getAsString());
    }
    
    ret = al.toArray(new String[0]);
    return ret;
	}
	/*
	public static String getColorModel(IplImage iplImage) {
		StringBuffer sb  = new StringBuffer();
		for(int i = 0; iplImage != null && i < 4; i++) {
		  sb.append((char)iplImage.colorModel(i));
		}
		return sb.toString().trim();
	}
*/
	
	static public ImageRegionModel[] parseToRegionModels(String regionsSpec) {
    ImageRegionModel[] ret = new ImageRegionModel[0];
    ArrayList<ImageRegionModel> aList = new ArrayList<ImageRegionModel>();
    if(regionsSpec == null) {
    	return ret;
    }
    String[] specs = regionsSpec.split("\\s+");
    for(String aSpec : specs) {
      Matcher m = region_pat.matcher(aSpec);
      if(m.matches()) {
        String type = m.group(1);
        int x = Integer.parseInt(m.group(2));
        int y = Integer.parseInt(m.group(3));
        int width = Integer.parseInt(m.group(4));
        int height = Integer.parseInt(m.group(5));
        aList.add(new ImageRegionModel(type, x, y, width, height));
      }
    }
    
    ret = aList.toArray(ret);
    return ret;
	}

  static public Date parseSolrDate(String solrDate) {
  	Date ret = null;
  	try {
  	  SimpleDateFormat df = new SimpleDateFormat(solrDateFormat);
  	  ret = df.parse(solrDate);
  	}
  	catch(Exception e) {}
  	return ret;
  }
  
  public static String extractPath(String url) {
  	String ret = url;
  	try {
  		URL u = new URL(url);
  		ret = u.getPath();
  	}
  	catch (MalformedURLException mue) {
  		
  	}
  	return ret;
  }

	public static PLRecord parseJsonDoc(String sourceServer, JsonObject jsonDoc) {
		PLRecord record = new PLRecord();
  	JsonElement je = jsonDoc.get(ISConstants.FIELD_URL);
  	String str = "";
  	if(je != null && !je.isJsonNull()) {
    	str = je.getAsString();
	  	record.setImageUrl(prefixServer(sourceServer, str));
    }
  	je = jsonDoc.get(ISConstants.FIELD_URL_THUMB);
  	if(je != null && !je.isJsonNull()) {
    	str = je.getAsString();
	  	record.setUrl_thumb(prefixServer(sourceServer, str));
  	}
  	je = jsonDoc.get(ISConstants.FIELD_URL);
  	if(je != null && !je.isJsonNull()) {
    	str = je.getAsString();
	  	record.setUrl(prefixServer(sourceServer, str));
  	}
  	je = jsonDoc.get(ISConstants.FIELD_ORIGINAL_URL);
  	if(je != null && !je.isJsonNull()) {
    	str = je.getAsString();
	  	record.setOriginalUrl(str);
  	}
  	je = jsonDoc.get(ISConstants.FIELD_P_UUID);
  	if(je != null && !je.isJsonNull()) {
    	str = je.getAsString();
	  	record.setUuid(str);
  	}
  	je = jsonDoc.get(ISConstants.FIELD_FULL_NAME);
  	if(je != null && !je.isJsonNull()) {
    	str = je.getAsString();
	  	record.set_full_name(str);
  	}
  	je = jsonDoc.get(ISConstants.FIELD_EVENT_NAME);
  	if(je != null && !je.isJsonNull()) {
    	str = je.getAsString();
	  	record.setEventName(str);
  	}
  	je = jsonDoc.get(ISConstants.FIELD_EVENT_SHORT_NAME);
  	if(je != null && !je.isJsonNull()) {
    	str = je.getAsString();
	  	record.setEventShortName(str);
  	}
  	je = jsonDoc.get(ISConstants.FIELD_COLOR_CHANNELS);
  	if(je != null && !je.isJsonNull()) {
    	int v = je.getAsInt();
	  	record.setColorChannels(v);
  	}
  	je = jsonDoc.get(ISConstants.FIELD_IMAGE_WIDTH);
  	if(je != null && !je.isJsonNull()) {
  		int v = je.getAsInt();
	  	record.setImageWidth(v);
  	}
  	je = jsonDoc.get(ISConstants.FIELD_IMAGE_HEIGHT);
  	if(je != null && !je.isJsonNull()) {
    	int v = je.getAsInt();
	  	record.setImageHeight(v);
  	}
  	je = jsonDoc.get(ISConstants.FIELD_GROUNDTRUTH_STATUS);
  	if(je != null && !je.isJsonNull()) {
    	int v = je.getAsInt();
	  	record.setGroundTruthStatus(v);
  	}

  	
		je = jsonDoc.get(ISConstants.FIELD_IMAGE_ID);
  	if(je != null && !je.isJsonNull()) {
    	int image_id = je.getAsInt();
	  	record.set_image_id(Integer.toString(image_id));
  	}
  	je = jsonDoc.get(ISConstants.FIELD_GT_INITIAL_PERSON);
  	if(je != null && !je.isJsonNull()) {
    	String v = je.getAsString();
	  	record.set_gt_initial_person(v);
  	}
  	je = jsonDoc.get(ISConstants.FIELD_GT_FINAL_PERSON);
  	if(je != null && !je.isJsonNull()) {
    	String v = je.getAsString();
	  	record.set_gt_final_person(v);
  	}
  	je = jsonDoc.get(ISConstants.FIELD_INITIAL_REGIONS);
  	if(je != null && !je.isJsonNull()) {
    	String v = je.getAsString();
    	if(v != null) {
  	  	record.set_initial_regions(Utils.parseToRegionModels(v));
    	}
  	}
  	je = jsonDoc.get(ISConstants.FIELD_FINAL_REGIONS);
  	if(je != null && !je.isJsonNull()) {
    	String v = je.getAsString();
    	if(v != null) {
  	  	record.set_final_regions(Utils.parseToRegionModels(v));
    	}
  	}
  	je = jsonDoc.get(ISConstants.FIELD_INITIAL_UPDATED_TIME);
  	if(je != null && !je.isJsonNull()) {
    	String v = je.getAsString();
    	if(v != null) {
  	  	record.set_initial_update_time(Utils.parseSolrDate(v));
    	}
  	}
  	je = jsonDoc.get(ISConstants.FIELD_FINAL_UPDATED_TIME);
  	if(je != null && !je.isJsonNull()) {
    	String v = je.getAsString();
    	if(v != null) {
  	  	record.set_final_update_time(Utils.parseSolrDate(v));
    	}
  	}

		return record;
		
	}

	public static String getUser(HttpServletRequest req) {
		String user = null;
		try {
		  user = req.getUserPrincipal().getName();
		}
		catch (Exception e) {}
		if(user == null) {
			req.getHeader(ISConstants.SM_USER);
		}
		if(user == null) {
			user = "unauthenticated";
		}
		return user;
	}
	
	
    public static String prefixServer(String sourceServer, String plImageName) {
        try {
                String path = plImageName;
                try {
                  URL u = new URL(plImageName);
                  path = u.getPath();
                } 
                catch (MalformedURLException mue) {
                }
          //URL u1 = new URL(sourceServer);
          // Kudge - Always get images from pl host
          URL u1 = new URL("https://pl.nlm.nih.gov"); 
          URL u = new URL("https", u1.getHost(), "/"+path);
                return u.toString();
        }
        catch (MalformedURLException mue) {
                return plImageName;
        }
}

}
