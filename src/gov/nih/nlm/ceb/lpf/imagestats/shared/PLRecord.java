package gov.nih.nlm.ceb.lpf.imagestats.shared;

import gov.nih.nlm.ceb.lpf.imagestats.client.ImageEditContainer;
//import gov.nih.nlm.ceb.lpf.imagestats.server.SearchPL;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import org.vaadin.gwtgraphics.client.DrawingArea;

//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;

public class PLRecord implements Serializable {
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String originalUrl = "";
	private String imageUrl = "";
	private String url = "";
	private String url_thumb = "";
  private String name = "";
  private String eventName = "";
  private String eventShortName = "";
  private String full_name = "";
  private int colorChannels = -1;
  private int imageWidth = 0;
  private int imageHeight = 0;

  private int groundTruthStatus = 0; // 0 => not done 
                                     // 1 => accepted as is from
                                     //      face matcher.
                                     // 2 => initial ground truth 
                                     // 3 => final ground truth

	private int image_id = -1;
	private String gt_initial_person = "";
	private String gt_final_person = "";
	private ImageRegionModel[] initial_regions = new ImageRegionModel[0];
	private ImageRegionModel[] final_regions = new ImageRegionModel[0];
	private String initial_regions_str = "";
	private String final_regions_str = "";
	private Date initial_update_time;
	private Date final_update_time;
  
  
  //private Date date;
  //private String subject;
  private int id = -1;
  private String uuid;
  
  private static int count = 0;
 
  public PLRecord() {
  	id = count++;
  }

  // Copy constructor
  public PLRecord(PLRecord other) {
  	this();
  	
  	uuid = other.uuid;
  	originalUrl = other.originalUrl;
  	imageUrl = other.imageUrl;
  	url = other.url;
  	url_thumb = other.url_thumb;
    name = other.name;
    eventName = other.eventName;
    eventShortName = other.eventShortName;

    colorChannels = other.colorChannels;
    imageWidth = other.imageWidth;
    imageHeight = other.imageHeight;
    groundTruthStatus = other.groundTruthStatus;
  	image_id = other.image_id;
  	gt_initial_person = other.gt_initial_person;
  	gt_final_person = other.gt_final_person;
  	initial_regions = other.initial_regions;
  	final_regions = other.final_regions;
  	initial_regions_str = other.initial_regions_str;
  	final_regions_str = other.final_regions_str;
  	initial_update_time = other.initial_update_time;
  	final_update_time = other.final_update_time;
  	full_name = other.full_name;
  }

  
  public int getId() {
    return id;
  }
 
  public void setId(int anId) {
    this.id = anId;
  }
 
  public String getUuid() {
    return uuid;
  }
 
  public void setUuid(String anId) {
    this.uuid = anId;
  }
 
  public String getName() {
    return name;
  }
 
  public void setName(String aName) {
    this.name = aName;
  }
 
  public String getOriginalUrl() {
    return originalUrl;
  }
 
  public void setOriginalUrl(String anOriginalUrl) {
    this.originalUrl = anOriginalUrl;
  }

  public String getImageUrl() {
    return imageUrl;
  }
 
  public void setImageUrl(String anImageUrl) {
    this.imageUrl = anImageUrl;
  }
  public String getUrl_thumb() {
    return url_thumb;
  }
 
  public void setUrl_thumb(String anImageUrl) {
    this.url_thumb = anImageUrl;
  }
  public String getUrl() {
    return url;
  }
 
  public void setUrl(String anImageUrl) {
    this.url = anImageUrl;
  }
  public String getEventName() {
    return eventName;
  }
 
  public void setEventName(String aName) {
    this.eventName = aName;
  }
  public String getEventShortName() {
    return eventShortName;
  }
 
  public void setEventShortName(String aSName) {
    this.eventShortName = aSName;
  }
  public int getColorChannels() {
    return colorChannels;
  }
 
  public void setColorChannels(int v) {
    this.colorChannels = v;
  }
  public int getImageWidth() {
    return imageWidth;
  }
 
  public void setImageWidth(int v) {
    this.imageWidth = v;
  }
  public int getImageHeight() {
    return imageHeight;
  }
 
  public void setImageHeight(int v) {
    this.imageHeight = v;
  }

  public int getGroundTruthStatus() {
    return groundTruthStatus;
  }
 
  public void setGroundTruthStatus(int v) {
    this.groundTruthStatus = v;
  }
  
  
	// Setters
	public void set_image_id(int id) {
		image_id = id;
	}
	

	public void set_gt_initial_person (String name) {
		gt_initial_person = name;
	}
	
	
	public void set_gt_final_person (String name) {
		gt_final_person = name;
	}
	
	
	public void set_initial_regions (ImageRegionModel[] regions) {
		initial_regions = regions;
		initial_regions_str = ClientUtils.arrayToString(initial_regions);
	}

	public void set_final_regions (ImageRegionModel[] regions) {
		final_regions = regions;
		final_regions_str = ClientUtils.arrayToString(final_regions);
	}
	
	public void set_initial_update_time(Date time) {
		initial_update_time = time;
	}
	
	public void set_final_update_time(Date time) {
		final_update_time = time;
	}
	
  // Getters
	public int get_image_id() {
		return image_id;
	}
	
		public String get_gt_initial_person () {
		return gt_initial_person;
	}
	
	
	public String get_gt_final_person () {
		return gt_final_person;
	}
	
	
	public ImageRegionModel[] get_initial_regions () {
		return initial_regions;
	}

	public ImageRegionModel[] get_final_regions () {
		return final_regions;
	}
	
	public Date get_initial_update_time() {
		return initial_update_time;
	}
	
	public Date get_final_update_time() {
		return final_update_time;
	}
	
	public String get_initial_regions_str() {
		return initial_regions_str;
	}
	
	public String get_final_regions_str() {
		return final_regions_str;
	}
	
	public String get_full_name() {
	  return full_name;	
	}
	
	public void set_full_name(String val) {
	  full_name = val;	
	}
	
	public String[] getGTColumns(boolean final_gt) {
		ArrayList<String> al = new ArrayList<String>(); 
		al.add(String.valueOf(image_id));
		al.add(imageUrl);
		ImageRegionModel[] regions = null;
		if(final_gt) {
			al.add(gt_final_person);
			regions = get_final_regions();
		}
		else {
			al.add(gt_initial_person);
			regions = get_initial_regions();
		}
		al.add(full_name);
		
		if(regions != null) {
		  for(ImageRegionModel r : regions) {
			  al.add(r.toString());
		  }
		}
		
		return al.toArray(new String[0]);
	}
	/*
	public String getDisplayStyle(gov.nih.nlm.ceb.lpf.imagestats.client.PLRecordListView.Style style) {
		String ret = style.thumb();
		switch (groundTruthStatus) {
		case 0:
			break;
		case 2:
		case 3:
			ret = style.thumbEdited();
			break;
		default:
			break;
		}
		return ret;
	}
	*/
}
