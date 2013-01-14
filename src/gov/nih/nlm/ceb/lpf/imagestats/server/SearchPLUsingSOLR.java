package gov.nih.nlm.ceb.lpf.imagestats.server;

import gov.nih.nlm.ceb.lpf.imagestats.shared.ClientUtils;
import gov.nih.nlm.ceb.lpf.imagestats.shared.PLSolrParams;
import gov.nih.nlm.ceb.lpf.imagestats.shared.Utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.http.NameValuePair;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SearchPLUsingSOLR {

	
	UserRoleDB userroles = null;
	
	public void setUserDB(UserRoleDB aDB) {
		userroles = aDB;
	}
	
	public String search(String sourceUrl, PLSolrParams searchParams, String user) throws IOException {
		String ret = null;
		try {
			addUserEventFilter(searchParams, user);
			URL solrUrl = new URL(sourceUrl);
			ret = executePost(solrUrl, searchParams);
		}
		catch(MalformedURLException mue) {
			
		}
    return ret;
	}
	
	
	public JsonObject searchForImages(String sourceUrl, HttpServletRequest request) throws IOException {
		//int rows = -1;
		PLSolrParams urlparams = new PLSolrParams();
		urlparams.add("qt", "edismax");
		urlparams.add("wt", "json");
	  urlparams.add("sort", "created desc");
		urlparams.add("fq", "url_thumb:[* TO *]");
	  addUserEventFilter(urlparams, Utils.getUser(request));
		
//	  urlparams.add("sort", "expiry_date desc");
	  Enumeration<String> en = (Enumeration<String>) request.getParameterNames();
	  while(en.hasMoreElements()) {
	  	String n = en.nextElement();
	  	String [] vals = request.getParameterValues(n);
	  	urlparams.add(n, vals);
	  }
	  	
		JsonParser jsonParser = new JsonParser();
		return jsonParser.parse(executePost(new URL(sourceUrl), urlparams)).getAsJsonObject();
	}
	
	public JsonObject searchWithUrls(String solrUrl, String [] urls) throws IOException {
		JsonObject ret = null;
		PLSolrParams urlparams = new PLSolrParams();
		urlparams.add("wt", "json");
		urlparams.add("qt", "edismax");
	  urlparams.add("sort", "created desc");
		StringBuffer sb = new StringBuffer();
		for(String u : urls) {
			if(sb.length() > 0) {
				sb.append(" ");
			}
			String full_path = Utils.extractPath(u);
			if(full_path.startsWith("/")) {
				full_path = full_path.substring(1);
			}
			sb.append(ClientUtils.escapeSpecialChars(full_path));
		}
		sb.insert(0, "url:(").append(")");
		urlparams.add("q", sb.toString());
		
		try {
			JsonParser jsonParser = new JsonParser();
			return jsonParser.parse(executePost(new URL(solrUrl), urlparams)).getAsJsonObject();
		}
		catch(MalformedURLException mue) {
			
		}
    return ret;
	}
	
	public static String executePost(URL url, Map<String, Set<String>> urlParameters) throws IOException {
      //Create connection
		  String ret = null;
      HttpClient httpclient = new DefaultHttpClient();
      HttpPost httppost = new HttpPost(url.toString());
    	List<NameValuePair> data = new ArrayList<NameValuePair>();
      Iterator<String> iter = urlParameters.keySet().iterator();
      while(iter.hasNext()) {
      	String name = iter.next();
      	Set<String> vals = urlParameters.get(name);
      	Iterator<String> vals_iter = vals.iterator();
      	while(vals_iter.hasNext()) {
          data.add(new BasicNameValuePair(name, vals_iter.next()));
      	}
      }
      
      httppost.setEntity(new UrlEncodedFormEntity(data,  "UTF-8"));
      HttpResponse response = httpclient.execute(httppost);
      HttpEntity entity = response.getEntity();

      ret = EntityUtils.toString(entity);
      return ret;
	}
	
	void addUserEventFilter(PLSolrParams urlParameters, String user) {
		if(userroles != null) {
			try {
			  UserRole role = userroles.getUserRole(user);
			  String event = role.getUser_event();
			  if(event != null && event.trim().length() > 0) {
			    urlParameters.add("fq", "shortname:"+event);
			  }
			}
			catch (SQLException sqle) {
				
			}
		}
	}
}
