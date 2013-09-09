package gov.nih.nlm.ceb.lpf.imagestats.server;

import gov.nih.nlm.ceb.lpf.imagestats.client.ImageStatsService;
import gov.nih.nlm.ceb.lpf.imagestats.client.ImageStatsServiceAsync;
import gov.nih.nlm.ceb.lpf.imagestats.shared.ISConstants;
import gov.nih.nlm.ceb.lpf.imagestats.shared.PLPagingLoadResultBean;
import gov.nih.nlm.ceb.lpf.imagestats.shared.ClientUtils;
import gov.nih.nlm.ceb.lpf.imagestats.shared.FacetModel;
import gov.nih.nlm.ceb.lpf.imagestats.shared.ImageStatsException;
//import gov.nih.nlm.ceb.lpf.imagestats.shared.IplImageStats;
//import gov.nih.nlm.ceb.lpf.imagestats.shared.IplImageStatsList;
import gov.nih.nlm.ceb.lpf.imagestats.shared.PLRecord;
import gov.nih.nlm.ceb.lpf.imagestats.shared.PLSolrParams;
import gov.nih.nlm.ceb.lpf.imagestats.shared.Utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.DOMSerializer;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.SerializerFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
//import com.googlecode.javacpp.BytePointer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.widget.core.client.box.MessageBox;

//import static com.googlecode.javacv.cpp.opencv_core.IplImage;
//import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;



@SuppressWarnings("serial")
public class ImageStatsImpl extends HttpServlet {
//	SearchPL pl_ws = null;
	//SearchPLUsingSOLR pl = null;

	String defaultUsers = null;
	//DataSource plstageDataSource = null;

	private final ImageStatsServiceAsync imageStatsService = GWT
			.create(ImageStatsService.class);
	
UserRoleDB userroles = null;

	static String [] iplImageElementsInt = {
		"nChannels",
		"depth",
		"width",
		"height",
		"dataOrder",
		"origin",
		"widthStep",
		"imageSize",
		"align"
	};
	
	File tmpDir = null;
	ImageStatsDB imageStatsDB = null;
	UserRoleDB userDB = null;

	public ImageStatsImpl() {
		super();
	}
	
	@Override
	public void init() throws ServletException {
		super.init();
		//pl_ws = new SearchPL();
		//pl_ws.init();
		try {
		  //pl = new SearchPLUsingSOLR();
			/*InitialContext cxt = new InitialContext();
			DataSource plDataSource = (DataSource) cxt.lookup( "java:/comp/env/jdbc/pl" );
			imageStatsDB = new ImageStatsDB(plDataSource);
			DataSource usersDataSource = (DataSource) cxt.lookup( "java:/comp/env/jdbc/users" );
			userDB = new UserRoleDB(usersDataSource);*/
			imageStatsDB = null;
			userDB = null;
			//pl.setUserDB(userDB);

			try {
			  //defaultUsers = (String) cxt.lookup( "java:/comp/env/defaultUsers" );
				defaultUsers = null;
			}
			catch(Exception e) {
				
			}
			
		}
		catch(Exception mue) {
			throw new ServletException(mue);
		}
		
		tmpDir = (File)getServletContext().getAttribute("javax.servlet.context.tempdir");
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
	  invokeServlet(request, response);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
	  invokeServlet(request, response);
	}

	void invokeServlet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		try{
			InitialContext cxt = new InitialContext();

			DataSource usersDataSource = (DataSource) cxt.lookup( "java:/comp/env/jdbc/users" );

			userroles = new UserRoleDB(usersDataSource);
			}catch(Exception e){System.out.println("Database Not Configured Properly");}
		String sourceUrl = request.getParameter("sourceUrl");
		String[] imageUrls = request.getParameterValues("url");
		JsonObject plResults = null;
		if(imageUrls == null) {
			//plResults = pl.searchForImages(sourceUrl, request);
		}
		
		String format = request.getParameter("format");
		if(format == null || format.length() == 0) {
			format = ISConstants.FORMAT_CSV;
		}
		String csv_fs = request.getParameter("csv_fs");
		String csv_rs = request.getParameter("csv_rs");
		String disposition = request.getParameter("file");

		if(csv_fs == null) csv_fs = ",";
		if(csv_rs == null) csv_rs = "\n";
		
		if(disposition != null && disposition.equalsIgnoreCase("true")) {
			if(ISConstants.INITIAL_FORMAT_GROUNDTRUTH_CSV.equalsIgnoreCase(format) ||
					ISConstants.FINAL_FORMAT_GROUNDTRUTH_CSV.equalsIgnoreCase(format)) {
				//TODO - Kludge for google students application. Use filename based on user name.
	      //response.setContentType("text/plain");
	      //response.setCharacterEncoding("UTF-8");
				response.addHeader("Content-Disposition", "attachment; filename=\"imagestats."+Utils.getUser(request)+".tsv\"");
			}
			else {
			  response.addHeader("Content-Type", "text/"+format);
			  response.addHeader("Content-Disposition", "attachment; filename=\"imagestats."+format+"\"");
			}
		}
		if (ISConstants.FORMAT_CSV.equalsIgnoreCase(format)) {
			writeFieldsCSV(response.getWriter(), getDataHeaders(), csv_fs, csv_rs);
			if(imageUrls != null) {
			  for (int i = 0; i < imageUrls.length; i++) {
					String [] columns = getImageStats(imageUrls[i]);
					writeFieldsCSV(response.getWriter(), columns, csv_fs, csv_rs);
			  }
			}/*
			else {
				IplImageStatsList statsList = getImageStatsList(sourceUrl, plResults);
				if(statsList != null) {
					List<IplImageStats> list = statsList.getIplImageSet();
					if(list != null) {
				    Iterator<IplImageStats> iter = list.iterator();
				    while(iter.hasNext()) {
					    String [] columns = getImageStats(iter.next().getImageUrl());
					    writeFieldsCSV(response.getWriter(), columns, csv_fs, csv_rs);
				    }
				  }
				}
			}*/
      response.setContentType("text/plain");
      response.setCharacterEncoding("UTF-8");
		}/*
		else if(ISConstants.FORMAT_JSON.equalsIgnoreCase(format)) {
			IplImageStatsList statsList = getImageStatsList(sourceUrl, plResults);
			Gson gson = new Gson();
			gson.toJson(statsList, response.getWriter());
		}*/
		else if(ISConstants.FORMAT_ZIP.equalsIgnoreCase(format)) {
			if(imageUrls != null && imageUrls.length > 0) {
			  ZipImages(sourceUrl, imageUrls, response.getOutputStream());
			}
			else {
			  ZipImages(sourceUrl, plResults, response.getOutputStream());
			}
		}
		else if(ISConstants.INITIAL_FORMAT_GROUNDTRUTH_CSV.equalsIgnoreCase(format) ||
				ISConstants.FINAL_FORMAT_GROUNDTRUTH_CSV.equalsIgnoreCase(format)) {
			boolean isFinal = ISConstants.FINAL_FORMAT_GROUNDTRUTH_CSV.equalsIgnoreCase(format);
			if(imageUrls != null && imageUrls.length > 0) {
			  exportGroundTruthData(sourceUrl, imageUrls, request, response, isFinal);
			}
			else {
				exportGroundTruthData(sourceUrl, plResults, request, response, isFinal);
			}
		}
		else {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			try {
				DocumentBuilder builder = factory.newDocumentBuilder();
				org.w3c.dom.Document domDoc = builder.newDocument();

				/*org.w3c.dom.Element root = domDoc.createElement("IplImageSet");
				domDoc.appendChild(root);
				for (int i = 0; imageUrls != null && i < imageUrls.length; i++) {
						Element iplImageEl = getImageStatsDOM(domDoc, imageUrls[i]);
						iplImageEl.setAttribute("url", imageUrls[i]);
						root.appendChild(iplImageEl);
				}*/
        response.setContentType("text/xml");
        response.setCharacterEncoding("UTF-8");
				response.getWriter().write(toXMLString(domDoc));
			} catch (ParserConfigurationException pce) {
				// Parser with specified options can't be built
				pce.printStackTrace();
			}
		}
	}
 /*
	public IplImageStatsList getImageStatsList(String sourceServer, JsonObject plResults) throws IOException{
		// Verify that the input is valid.
		IplImageStatsList ret = new IplImageStatsList();
		try {
			JsonArray resultSet = plResults.getAsJsonObject("response").getAsJsonArray("docs");
			ret.setCount(plResults.getAsJsonObject("response").get("numFound").getAsInt());
			Iterator<JsonElement> iter = resultSet.iterator();
			while(iter.hasNext()) {
				JsonObject el = iter.next().getAsJsonObject();
				String str = el.get("url").getAsString();
				if(str != null && str.length() > 0) {
			    URL u = new URL(Utils.prefixServer(sourceServer, str));
			    IplImage iplImage = getIplImage(u);
			    ret.add(new IplImageStats(u.toString(), iplImage));
				}
			}
		} catch (MalformedURLException mfue) {

		}

		return ret;
	}
*/
	/*public Element getImageStatsDOM(Document domDoc, String urlStr) throws IOException{
		// Verify that the input is valid.
		Element ret = null;
		try {
			URL u = new URL(urlStr);
			IplImage iplImage = getIplImage(u);
			ret = toDOMElement(domDoc, iplImage);
		} catch (MalformedURLException mfue) {

		}

		return ret;
	}*/

	public String[] getImageStats(String urlStr)
			throws MalformedURLException, IOException {
		// Verify that the input is valid.
		String[] ret = new String[]{urlStr};
		
		URL u = new URL(urlStr);
		/*IplImage iplImage = getIplImage(u);
		Integer[] dataElements = getIntegerMembers(iplImage);
		if (dataElements != null) {
			ret = new String[dataElements.length + 2]; // Integer elements plus
																									// colorModel and url
			ret[0] = urlStr;
			for (int i = 1; i <= dataElements.length; i++) {
				ret[i] = dataElements[i-1].toString();
			}
			ret[dataElements.length+1] = Utils.getColorModel(iplImage);
		}*/
		return ret;
	}

	String [] getDataHeaders() {
		String [] ret = new String [iplImageElementsInt.length+2];
		ret[0] = "ImageUrl";
		for(int i = 1; i <=  iplImageElementsInt.length; i++) {
			ret[i] = iplImageElementsInt[i-1];
		}
		ret[iplImageElementsInt.length+1] = "colorModel";
		return ret;
	}
	

	/*Element toDOMElement(Document domDoc, IplImage iplImage) {
		
		Element ret = domDoc.createElement("IplImage");
		if(iplImage == null)
			return ret;
		
		BytePointer bp = iplImage.imageData();
		if(bp != null) {
			Element el = domDoc.createElement("imageData");
			try {
		    el.appendChild(domDoc.createTextNode(bp.getString("UTF-8")));
			} catch (UnsupportedEncodingException usee) {
			}
		}
		bp = iplImage.imageDataOrigin();
		if(bp != null) {
			Element el = domDoc.createElement("imageDataOrigin");
			try {
		    el.appendChild(domDoc.createTextNode(bp.getString("UTF-8")));
			} catch (UnsupportedEncodingException usee) {
			}
		}
				Integer [] integerElements = getIntegerMembers(iplImage);
				for(int i = 0; integerElements != null && i < integerElements.length; i++) {
					Element el = domDoc.createElement(iplImageElementsInt[i]);
					el.setAttribute("type", "int");
					el.appendChild(domDoc.createTextNode(integerElements[i].toString()));
					ret.appendChild(el);
				}
				String colorModel = Utils.getColorModel(iplImage);
				if(colorModel.length() > 0) {
					Element el = domDoc.createElement("colorModel");
					el.setAttribute("type", "charArray");
					el.appendChild(domDoc.createTextNode(colorModel));
					ret.appendChild(el);
				}
		return ret;
	}*/
	/*
	IplImage getIplImage(URL imageUrl) throws IOException {
		File file = getTmpFile(imageUrl);
		IplImage ret = getIplImage(file.getAbsolutePath());
		file.delete();
    // read an image  		
		return ret;
	}*/
/*
	Integer[] getIntegerMembers(IplImage iplImage) {
		Integer[] ret = null;
		if (iplImage != null) {
			try {
				ret = new Integer[iplImageElementsInt.length];
				for (int i = 0; i < iplImageElementsInt.length; i++) {
					Method m = iplImage.getClass().getMethod(iplImageElementsInt[i]);
					try {
						Object o = m.invoke(iplImage);
						if (o instanceof Integer) {
							ret[i] = (Integer) o;
						}

					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			} catch (NoSuchMethodException nse) {

			} catch (SecurityException se) {

			}
		}

		return ret;
	}
	*/
	File getTmpFile(URL url) throws IOException {
		File ret = null;
		String filename = url.getPath().replace('/', '_');
		File tmpFile = File.createTempFile(filename, "", tmpDir);
		tmpFile.deleteOnExit();

		URLConnection connection = url.openConnection();
    InputStream input = connection.getInputStream();
    OutputStream output = new FileOutputStream( tmpFile );
    downloadImage(input, output);
    input.close();
    output.close();
		ret = tmpFile;
		
		return ret;
	}
	
	boolean downloadImage(InputStream is, OutputStream os) throws IOException {
		boolean ret = false; 
    ImageInputStream iis = null;
		try {
			iis = ImageIO.createImageInputStream(is);
        Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
        if (iter.hasNext()) {
        // Use the first reader
        ImageReader reader = (ImageReader)iter.next();
        String type = reader.getFormatName();
        if("gif".equalsIgnoreCase(type)) {
        	type = "png";
        }
        BufferedImage bi = ImageIO.read(iis);
    		ImageIO.write( bi, type, os);
    		
    		ret = true;
        // Close stream
        }
			} 
			finally {
        try {
        	if(iis != null) iis.close();
				} catch (IOException e) {
				}
        try {
        	if(is != null) is.close();
				} catch (IOException e) {
				}
			}
		  return ret;
	}
	/*
	IplImage getIplImage(String filename) {
		IplImage ret = cvLoadImage(filename);
    // read an image  		
		return ret;
	}*/
	
	
	String toXMLString(org.w3c.dom.Node node) {
		String ret = null;
		if (node != null) {
			Properties props = OutputPropertiesFactory
			.getDefaultMethodProperties("xml");
			props.setProperty("indent", "yes");
			props.setProperty(OutputPropertiesFactory.S_KEY_INDENT_AMOUNT, "2");
			
			Serializer ser = SerializerFactory.getSerializer(props);

			java.io.StringWriter sw = new java.io.StringWriter();
			ser.setWriter(sw);
			try {
				DOMSerializer dser = ser.asDOMSerializer();
				dser.serialize(node);
				ret = sw.toString();
			} catch (IOException ioe) {
			}
		}
		return ret;
	}
	
	void writeFieldsCSV(Writer wt, String[] fields, String fieldSeperator, String recordSeperator) throws IOException {
		for(int i = 0; fields != null && i < fields.length; i++) {
			if(i != 0) {
				wt.write(fieldSeperator);
			}
			wt.write(fields[i]);
		}
		if(fields != null) {
			wt.write(recordSeperator);
		}
	}

	private void ZipImages(String sourceServer, JsonObject plResults, OutputStream outputStream) throws IOException {
		JsonArray resultSet = plResults.getAsJsonObject("response").getAsJsonArray("docs");
		Iterator<JsonElement> iter = resultSet.iterator();
		ZipOutputStream zout = new ZipOutputStream(outputStream);
		while(iter.hasNext()) {
			JsonObject el = iter.next().getAsJsonObject();
			String str = el.get("url").getAsString();
			if(str != null && str.length() > 0) {
		    URL u = new URL(Utils.prefixServer(sourceServer, str));
		    WriteToZipOutputStream(u, zout);
			}
		}
		zout.close();
	}		
	

	
	void ZipImages(String sourceServer, String [] imageUrls, OutputStream out) throws IOException {
			ZipOutputStream zout = new ZipOutputStream(out);
			for(int i = 0; imageUrls != null && i < imageUrls.length; i++) {
        String str = imageUrls[i];
				if(str != null && str.length() > 0) {
			    URL u = new URL(Utils.prefixServer(sourceServer, str));
			    WriteToZipOutputStream(u, zout);
			  }
			}
	    zout.close();
	}
	
	void WriteToZipOutputStream(URL imageUrl, ZipOutputStream zout) throws IOException {
		String path = imageUrl.getPath();
		
    zout.putNextEntry(new ZipEntry(path.replaceAll("^/", "")));
    URLConnection connection = imageUrl.openConnection();
    InputStream input = connection.getInputStream();
    byte[] buffer = new byte[4096];
    int n = -1;

    while ( (n = input.read(buffer)) != -1)
    {
       zout.write(buffer, 0, n);
    }
    input.close();
	}
	
	void exportGroundTruthData(String sourceServer, JsonObject plResults, HttpServletRequest request, final HttpServletResponse resp, final boolean isFinal) throws IOException {
    final String cvs_fs = "\t";
    final String cvs_rs = "\n";
    String outputfile = defaultUsers;
    if(plResults == null){
		JsonArray resultSet = plResults.getAsJsonObject("response").getAsJsonArray("docs");
		Iterator<JsonElement> iter = resultSet.iterator();
		if(iter.hasNext()) {
			JsonObject el = iter.next().getAsJsonObject();
			PLRecord rec = Utils.parseJsonDoc(sourceServer, el);
			outputfile = rec.getEventShortName() + ".tsv";
			//resp.addHeader("Content-Disposition", "attachment; filename=\"imagestats."+outputfile+"\"");
			
			iter = resultSet.iterator();
		}
		while (iter.hasNext()) {
				JsonObject el = iter.next().getAsJsonObject();
				PLRecord rec = Utils.parseJsonDoc(sourceServer, el);
				writeFieldsCSV(resp.getWriter(), rec.getGTColumns(isFinal), cvs_fs,
						cvs_rs);
		}
    }
    else{
    	PLSolrParams searchparams = null;
    	
    	
    	

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
    	
    	
    	
    	//PLPagingLoadResultBean results = searchSOLRForPaging("", searchparams);
    	imageStatsService
		.searchSOLRForPaging("", searchparams, new AsyncCallback<PLPagingLoadResultBean>() {

			public void onFailure(Throwable t) {
        String details = t.getMessage();
        MessageBox m = new MessageBox(details);
        
        if (t instanceof ImageStatsException) {
          m.setMessage(((ImageStatsException)t).getISMessage());
        }
      m.show();
			}

			public void onSuccess(PLPagingLoadResultBean eventList) {
				if(eventList!=null){
					ArrayList<PLRecord> RecordList = (ArrayList<PLRecord>)eventList.getData();
					Iterator<PLRecord> iter = RecordList.iterator();
				while (iter.hasNext()) {
						PLRecord rec = iter.next();
						try{
							writeFieldsCSV(resp.getWriter(), rec.getGTColumns(isFinal), cvs_fs,
								cvs_rs);
						}catch(Exception e){MessageBox m = new MessageBox(e.getMessage());
											m.setMessage("Error Fetching Data");
											m.show();}
				}
				}
			}
		});
    }
		//resp.addHeader("Content-Disposition", "attachment; filename=\"imagestats."+outputfile+"\"");
	}

	void exportGroundTruthData(String solrServer, String [] imageUrls, HttpServletRequest request,  HttpServletResponse resp, boolean isFinal) throws IOException {
		//exportGroundTruthData(solrServer, pl.searchWithUrls(solrServer, imageUrls),resp, isFinal);
		JsonObject js = null;
		exportGroundTruthData(solrServer, js, request, resp, isFinal);
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
	
	//@Override
}

