package gov.nih.nlm.ceb.lpf.imagestats.shared;

import java.io.Serializable;

public interface ISConstants extends Serializable {
	//public final static String FIELD_SHORTNAME = "name"; 
	public final static String FIELD_EVENT_SHORT_NAME = "shortname"; 
	public final static String FIELD_NAME_LABEL = "Image Collections"; 
	public final static String FIELD_COLOR_CHANNELS = "color_channels"; 
	public final static String FIELD_COLOR_CHANNELS_LABEL = "Color Channels"; 
	public final static String FIELD_IMAGE_WIDTH = "image_width"; 
	public final static String FIELD_IMAGE_WIDTH_LABEL = "Image Width"; 
	public final static String FIELD_IMAGE_HEIGHT = "image_height"; 
	public final static String FIELD_IMAGE_HEIGHT_LABEL = "Image Height"; 
	public final static String IMAGE_TYPE_LABEL = "Image Type"; 
	public final static String IMAGE_TYPE = "type"; 
	public final static String JPEG_TYPE_QUERY_STR = "url_thumb:(jpg OR jpeg)"; 
	public final static String JPEG_TYPE_QUERY_LABEL = "jpg OR jpeg"; 
	public final static String GIF_TYPE_QUERY_STR = "url_thumb:gif"; 
	public final static String GIF_TYPE_QUERY_LABEL = "gif"; 
	public final static String PNG_TYPE_QUERY_STR = "url_thumb:png"; 
	public final static String PNG_TYPE_QUERY_LABEL = "png"; 
	
	
	public final static String EVENT_TEST_LABEL = "Test Exercise";
	public final static String EVENT_TEST = "test";
	public final static String EVENT_CMAX2009_LABEL = "CMAX 2009 Drill";
	public final static String EVENT_CMAX2009 = "cmax2009";
	public final static String EVENT_CMAX2010_LABEL = "CMAX 2010 Drill";
	public final static String EVENT_CMAX2010 = "cmax2010";
	public final static String EVENT_TESTPF_LABEL = "Test with Person Finder";
	public final static String EVENT_TESTPF = "testpf";
	public final static String EVENT_HEPL_LABEL = "Haiti Earthquake";
	public final static String EVENT_HEPL = "hepl";
	public final static String EVENT_COLUMBIA2011_LABEL = "Camp Roberts";
	public final static String EVENT_COLUMBIA2011 = "colombia2011";
	public final static String EVENT_CHRISTCHURCH_LABEL = "ChristChurch Earthquake";
	public final static String EVENT_CHRISTCHURCH = "christchurch";
	public final static String EVENT_SENDAI2011_LABEL = "Japan Earthquake and Tsunami";
	public final static String EVENT_SENDAI2011 = "sendai2011";
	public final static String EVENT_LISBON_LABEL = "Lisbon Earthquake Simulation";
	public final static String EVENT_LISBON = "lisbon";
	public final static String EVENT_JOPLIN_LABEL = "Joplin Tornado";
	public final static String EVENT_JOPLIN = "joplin";
	public final static String EVENT_TESTRU_LABEL = "Test with ReUnite";
	public final static String EVENT_TESTRU = "testru";
	public final static String EVENT_TESTTP_LABEL = "Test with TriagePic";
	public final static String EVENT_TESTTP = "testtp";
	public final static String EVENT_TURKEY2011_LABEL = "Turkey 2011 Earthquake";
	public final static String EVENT_TURKEY2011 = "turkey2011";
	public final static String EVENT_SENDONG2011_LABEL = "Typhoon Sendong";
	public final static String EVENT_SENDONG2011 = "sendong2011";
	public final static String EVENT_TUTORIAL4REUNITE_LABEL = "Tutorial for ReUnite";
	public final static String EVENT_TUTORIAL4REUNITE = "tutorial4reunite";
	public final static String EVENT_CYCTEST_LABEL = "Cyclone X - ARC";
	public final static String EVENT_CYCTEST = "cyctest";
	public final static String EVENT_TSUTEST_LABEL = "NSW Tsunami - ARC";
	public final static String EVENT_TSUTEST = "tsutest";
	
	public final static String FIELD_URL = "url";
	public final static String FIELD_ORIGINAL_URL = "originalUrl";
	public final static String FIELD_P_UUID = "p_uuid";
	public final static String FIELD_EVENT_NAME = "name";
	public final static String FIELD_FULL_NAME = "full_name";
	public final static String FIELD_URL_THUMB = "url_thumb";
	public final static String FIELD_GROUNDTRUTH_STATUS = "groundTruthStatus";
	public final static String FIELD_GROUNDTRUTH_STATUS_LABEL = "Ground Truth Status";
	public final static String FIELD_IMAGE_ID = "image_id";
	
	public final static String FIELD_GT_INITIAL_PERSON = "gt_initial_person";
	public final static String FIELD_GT_FINAL_PERSON = "gt_final_person";
	public final static String FIELD_INITIAL_REGIONS = "initial_regions";
	public final static String FIELD_FINAL_REGIONS = "final_regions";
	public final static String FIELD_INITIAL_UPDATED_TIME = "initial_updated_time";
	public final static String FIELD_FINAL_UPDATED_TIME = "final_updated_time";

	public final static String GROUNDTRUTH_STATUS_ZERO = "0";
	public final static String GROUNDTRUTH_STATUS_ONE = "1";
	public final static String GROUNDTRUTH_STATUS_TWO = "2";
	public final static String GROUNDTRUTH_STATUS_THREE = "3";
	public final static String GROUNDTRUTH_STATUS_NOT_DONE = "Not Done";
	public final static String GROUNDTRUTH_STATUS_FM_SAVED = "Saved from FaceMatcher(Yellow)";
	public final static String GROUNDTRUTH_STATUS_INITIAL_DONE = "Initiated(Yellow)";
	public final static String GROUNDTRUTH_STATUS_FINAL_DONE = "Completed(Green)";
	
	public final static String FORMAT_ZIP = "zip";
	public final static String FORMAT_CSV = "csv";
	public final static String FORMAT_XML = "xml";
	public final static String FORMAT_JSON = "json";
	public final static String INITIAL_FORMAT_GROUNDTRUTH_CSV = "initial_groundtruth.lst";
	public final static String FINAL_FORMAT_GROUNDTRUTH_CSV = "final_groundtruth.lst";
	public final static String SM_USER = "SM_USER";
	public final static int MIN_W = 16;
	public final static int MIN_H = 16;

}
