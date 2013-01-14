package gov.nih.nlm.ceb.lpf.imagestats.shared;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

public class ClientUtils {

	public static String special_escape_chars = "/ &-$%#@!*_\"'~";
	public static String special_escape_chars_with = "\\";

	public final static Map<String, String> field_label = new LinkedHashMap<String, String>();
	static {
		field_label.put(ISConstants.FIELD_EVENT_NAME, ISConstants.FIELD_NAME_LABEL);
		field_label.put(ISConstants.FIELD_GROUNDTRUTH_STATUS, ISConstants.FIELD_GROUNDTRUTH_STATUS_LABEL);
		field_label.put(ISConstants.FIELD_IMAGE_WIDTH, ISConstants.FIELD_IMAGE_WIDTH_LABEL);
		field_label.put(ISConstants.FIELD_IMAGE_HEIGHT, ISConstants.FIELD_IMAGE_HEIGHT_LABEL);
		field_label.put(ISConstants.IMAGE_TYPE, ISConstants.IMAGE_TYPE_LABEL);
		field_label.put(ISConstants.JPEG_TYPE_QUERY_STR, ISConstants.JPEG_TYPE_QUERY_LABEL);
		field_label.put(ISConstants.GIF_TYPE_QUERY_STR, ISConstants.GIF_TYPE_QUERY_LABEL);
		field_label.put(ISConstants.PNG_TYPE_QUERY_STR, ISConstants.PNG_TYPE_QUERY_LABEL);
		field_label.put(ISConstants.FIELD_COLOR_CHANNELS, ISConstants.FIELD_COLOR_CHANNELS_LABEL);

		field_label.put(ISConstants.GROUNDTRUTH_STATUS_ZERO, ISConstants.GROUNDTRUTH_STATUS_NOT_DONE);
		field_label.put(ISConstants.GROUNDTRUTH_STATUS_ONE, ISConstants.GROUNDTRUTH_STATUS_FM_SAVED);
		field_label.put(ISConstants.GROUNDTRUTH_STATUS_TWO, ISConstants.GROUNDTRUTH_STATUS_INITIAL_DONE);
		field_label.put(ISConstants.GROUNDTRUTH_STATUS_THREE, ISConstants.GROUNDTRUTH_STATUS_FINAL_DONE);

		field_label.put(ISConstants.EVENT_TEST, ISConstants.EVENT_TEST_LABEL);
		field_label.put(ISConstants.EVENT_CMAX2009, ISConstants.EVENT_CMAX2009_LABEL);
		field_label.put(ISConstants.EVENT_CMAX2010, ISConstants.EVENT_CMAX2010_LABEL);
		field_label.put(ISConstants.EVENT_TESTPF, ISConstants.EVENT_TESTPF_LABEL);
		field_label.put(ISConstants.EVENT_HEPL, ISConstants.EVENT_HEPL_LABEL);
		field_label.put(ISConstants.EVENT_COLUMBIA2011, ISConstants.EVENT_COLUMBIA2011_LABEL);
		field_label.put(ISConstants.EVENT_CHRISTCHURCH, ISConstants.EVENT_CHRISTCHURCH_LABEL);
		field_label.put(ISConstants.EVENT_SENDAI2011, ISConstants.EVENT_SENDAI2011_LABEL);
		field_label.put(ISConstants.EVENT_LISBON, ISConstants.EVENT_LISBON_LABEL);
		field_label.put(ISConstants.EVENT_JOPLIN, ISConstants.EVENT_JOPLIN_LABEL);
		field_label.put(ISConstants.EVENT_TESTRU, ISConstants.EVENT_TESTRU_LABEL);
    field_label.put(ISConstants.EVENT_TESTTP, ISConstants.EVENT_TESTTP_LABEL);
		field_label.put(ISConstants.EVENT_TURKEY2011, ISConstants.EVENT_TURKEY2011_LABEL);
		field_label.put(ISConstants.EVENT_SENDONG2011, ISConstants.EVENT_SENDONG2011_LABEL);
		field_label.put(ISConstants.EVENT_TUTORIAL4REUNITE, ISConstants.EVENT_TUTORIAL4REUNITE_LABEL);
		field_label.put(ISConstants.EVENT_CYCTEST, ISConstants.EVENT_CYCTEST_LABEL);
		field_label.put(ISConstants.EVENT_TSUTEST, ISConstants.EVENT_TSUTEST_LABEL);
	};
	
	public static String[] extractElementArray(String jsonArrayStr, String elementName) {
  	JSONArray ja = getJSONValue(jsonArrayStr).isArray();
  	return getArrayFromJSONArray(ja, elementName);
	}
	
	@SuppressWarnings("deprecation")
	public static JSONValue getJSONValue(String jsonStr) {
		return JSONParser.parse(jsonStr);
	}
	
	public static String[] getArrayFromJSONArray(JSONArray jsonArray, String elementName) {
    ArrayList<String> al = new ArrayList<String>();
  	for(int i = 0; jsonArray != null && i < jsonArray.size(); i++) {
  		if(jsonArray.get(i).isObject().get(elementName) == null) {
  			al.add("");
  		}
  		else {
  		  al.add(jsonArray.get(i).isObject().get(elementName).isString().stringValue());
  		}
  	}
  	
  	return al.toArray(new String[0]);
  }
  
	public static int getIntFromJSON(JSONObject jsonObj, String elementName) {
	 	return (int)jsonObj.get(elementName).isNumber().doubleValue();
	}
  
	public static String getStrFromJSON(JSONObject jsonObj, String elementName) {
	 	return jsonObj.get(elementName).isString().stringValue();
	}

	public static PLSolrParams buildDefaultSOLRParams() {
		PLSolrParams ret = new PLSolrParams();

    //ret.add("q", "*:*");
    ret.add("fq", "url_thumb:[* TO *]");
    ret.add("qt", "edismax");
		ret.add("facet", "on");
		//ret.add("facet.mincount", "1");
		ret.add("facet.sort", "index");
		ret.add("facet.field", ISConstants.FIELD_EVENT_NAME);
		ret.add("facet.field", ISConstants.FIELD_GROUNDTRUTH_STATUS);
		ret.add("facet.field", ISConstants.FIELD_COLOR_CHANNELS);
		ret.add("facet.query", ISConstants.JPEG_TYPE_QUERY_STR);
		ret.add("facet.query", ISConstants.GIF_TYPE_QUERY_STR);
		ret.add("facet.query", ISConstants.PNG_TYPE_QUERY_STR);
		ret.add("facet.range", ISConstants.FIELD_IMAGE_WIDTH);
		ret.add("f."+ISConstants.FIELD_IMAGE_WIDTH+".facet.range.start", "0");
		ret.add("f."+ISConstants.FIELD_IMAGE_WIDTH+".facet.range.end", "2000");
		ret.add("f."+ISConstants.FIELD_IMAGE_WIDTH+".facet.range.gap", "400");
		ret.add("f."+ISConstants.FIELD_IMAGE_WIDTH+".facet.range.other", "after");
		ret.add("facet.range", ISConstants.FIELD_IMAGE_HEIGHT);
		ret.add("f."+ISConstants.FIELD_IMAGE_HEIGHT+".facet.range.start", "0");
		ret.add("f."+ISConstants.FIELD_IMAGE_HEIGHT+".facet.range.end", "2000");
		ret.add("f."+ISConstants.FIELD_IMAGE_HEIGHT+".facet.range.gap", "400");
		ret.add("f."+ISConstants.FIELD_IMAGE_HEIGHT+".facet.range.other", "after");
		//ret.add("rows", "0");
		ret.add("wt", "json");
		ret.add("sort", "created desc");
		
    return ret;	
	}
	
	public static String getFieldLabel(String name) {
		String ret = field_label.get(name);
		if(ret == null) {
			ret = name;
		}
		
		return ret;
	}
	
	static public String arrayToString(ImageRegionModel[] regions) {
		StringBuffer sw = new StringBuffer();
		for (int i = 0; regions != null && i < regions.length; i++) {
			if(i != 0) {
				sw.append(" ");
			}
		  regions[i].writeTo(sw);
		}
		return sw.toString();
	}

  public static String escapeSpecialChars(String inputString) {
  	StringBuffer buf = new StringBuffer();
  	String prev = "";
  	String cur = "";
  	//char[] charBuf = inputString.toCharArray();
  	for(int i = 0; i < inputString.length(); i++) {
  		cur = inputString.substring(i,i+1);
  		if(special_escape_chars.contains(cur) && !special_escape_chars_with.equals(prev)) {
  			buf.append(special_escape_chars_with);
  		}
  		buf.append(cur);
  		prev = cur;
  	}
  	
  	return buf.toString();
  }


}
