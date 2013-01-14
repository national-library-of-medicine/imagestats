package gov.nih.nlm.ceb.lpf.imagestats.shared;

import java.io.Serializable;

public class GroundTruthRecord implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	int image_id = -1;
//	String image_url = "";
	String gt_initial_person = "";
	String gt_final_person = "";
	ImageRegionModel[] initial_regions = new ImageRegionModel[0];
	ImageRegionModel[] final_regions = new ImageRegionModel[0];
	java.sql.Timestamp initial_update_time = null;
	java.sql.Timestamp final_update_time = null;
	int groundTruthStatus = 0;
	
	public GroundTruthRecord() {
	}

	public GroundTruthRecord(int imageId, 
			String gtInitialPerson, 
			String gtFinalPerson,
			ImageRegionModel[] initialRegions,
			ImageRegionModel[] finalRegions,
			java.sql.Timestamp initialUpdateTime,
			java.sql.Timestamp finalUpdateTime,
			int ground_truth_status) {
		image_id = imageId;
		gt_initial_person = gtInitialPerson;
		gt_final_person = gtFinalPerson;
		initial_regions = initialRegions;
		final_regions = finalRegions;
		initial_update_time = initialUpdateTime;
		final_update_time = finalUpdateTime;
		groundTruthStatus = ground_truth_status;
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
	}

	public void set_final_regions (ImageRegionModel[] regions) {
		final_regions = regions;
	}
	
	public void set_initial_update_time(java.sql.Timestamp time) {
		initial_update_time = time;
	}
	
	public void set_final_update_time(java.sql.Timestamp time) {
		final_update_time = time;
	}
	
	public void set_grounfTruthStatus(int status) {
		groundTruthStatus = status;
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
	
	public java.sql.Timestamp get_initial_update_time() {
		return initial_update_time;
	}
	
	public java.sql.Timestamp get_final_update_time() {
		return final_update_time;
	}
	
	public int get_grounfTruthStatus() {
		return groundTruthStatus;
	}
}
