package gov.nih.nlm.ceb.lpf.imagestats.server;

import gov.nih.nlm.ceb.lpf.imagestats.shared.ClientUtils;
import gov.nih.nlm.ceb.lpf.imagestats.shared.ImageRegionModel;
import gov.nih.nlm.ceb.lpf.imagestats.shared.GroundTruthRecord;
import gov.nih.nlm.ceb.lpf.imagestats.shared.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

public class ImageStatsDB {

	
	DataSource plstageDataSource = null;
	Pattern imageid_pat = Pattern.compile("^.*/([\\d\\._a-zA-Z]+)$");
	
	public ImageStatsDB(DataSource ds) {
		plstageDataSource = ds;
	}
	
	
	
	public void setDataSource(DataSource ds) {
		plstageDataSource = ds;
	}
	
	public GroundTruthRecord getGroundTruthRecordWithId(String image_id) throws SQLException{
		GroundTruthRecord ret = null;
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			con = plstageDataSource.getConnection();
			String sql = "select * from imagestats where image_id=?";
		  PreparedStatement pstmt = con.prepareStatement(sql.toString());
		  pstmt.setString(1, image_id);
			rs = pstmt.executeQuery();
			ret = extractRow(rs);
		}		finally{
			if(rs != null)
			  rs.close();
			if(stmt != null) {
				stmt.close();
			}
			if(con != null)
			  con.close();
		}
		return ret;
	}

	public GroundTruthRecord getGroundTruthRecord(String image_url) throws SQLException{
		GroundTruthRecord ret = null;
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			con = plstageDataSource.getConnection();
			String sql = "select * from imagestats where image_id=?";
		  PreparedStatement pstmt = con.prepareStatement(sql.toString());
		  pstmt.setString(1, image_url);
			rs = pstmt.executeQuery();
			ret = extractRow(rs);
		}		finally{
			if(rs != null)
			  rs.close();
			if(stmt != null) {
				stmt.close();
			}
			if(con != null)
			  con.close();
		}
		return ret;
		
	}
	
  public static GroundTruthRecord extractRow(ResultSet rs) throws SQLException{
  	GroundTruthRecord ret = null;
  	if(rs != null && rs.next()) {
  		ImageRegionModel[] initial_regions = Utils.parseToRegionModels(rs.getString("initial_regions"));
  		ImageRegionModel[] final_regions = Utils.parseToRegionModels(rs.getString("final_regions"));
  		ret = new GroundTruthRecord(
  				rs.getString("image_id"),
//  				rs.getString("image_url"),
  				rs.getString("gt_initial_person"),
  				rs.getString("gt_final_person"),
  				initial_regions,
  				final_regions,
  				rs.getTimestamp("initial_updated_time"),
  				rs.getTimestamp("final_updated_time"),
  				rs.getInt("groundTruthStatus")
  				);
  	}
  	
  	return ret;
  }
  
	public void saveRegions(String image_id, String authorName, int groundTruthStatus, ImageRegionModel[] regions) throws SQLException {
		Connection con = null;
		try {
			con = plstageDataSource.getConnection();
				if(con != null) {
					//String image_id = getImageId(imageURL);
					GroundTruthRecord row = getGroundTruthRecordWithId(image_id);
					if(row == null) {
						row = new GroundTruthRecord();
						row.set_image_id(image_id);
					}

					//if(row.get_grounfTruthStatus() >= 2) {
						//throw new SQLException("Final ground truth is already done. No changes performed");
					//}
					//row.set_image_url(imageURL);
					row.set_grounfTruthStatus(groundTruthStatus);
					java.sql.Timestamp ts = new java.sql.Timestamp(new java.util.Date().getTime());
					if(groundTruthStatus > 1) {
						row.set_gt_final_person(authorName);
						row.set_final_update_time(ts);
						row.set_final_regions(regions);
					}
					else {
						row.set_gt_initial_person(authorName);
						row.set_initial_update_time(ts);
						row.set_initial_regions(regions);
					}
					StringBuffer sql = new StringBuffer();
						sql.append("replace into imagestats (image_id, gt_initial_person,gt_final_person, initial_regions,  final_regions, initial_updated_time, final_updated_time, groundTruthStatus)");
						sql.append("values (?, ?, ?, ?, ?, ?, ?, ?)");

				  PreparedStatement pstmt = con.prepareStatement(sql.toString());
				  pstmt.setString(1, row.get_image_id());
				  pstmt.setString(2, row.get_gt_initial_person());
				  pstmt.setString(3, row.get_gt_final_person());
				  pstmt.setString(4, ClientUtils.arrayToString(row.get_initial_regions()));
				  pstmt.setString(5, ClientUtils.arrayToString(row.get_final_regions()));
				  pstmt.setTimestamp(6, row.get_initial_update_time());
				  pstmt.setTimestamp(7, row.get_final_update_time());
				  pstmt.setInt(8, row.get_grounfTruthStatus());
				  
				  pstmt.execute();
				}
				
		}
		finally {
			if(con != null) {
				con.close();
			}
		}
	}

	String getImageId(String imageURL) {
		 String ret = "";
			Matcher m = imageid_pat.matcher(imageURL);
			if(m.matches()) {
				ret = m.group(1);
			}
			return ret;
		}
/*
	public GroundTruthRecord getGroundTruthRecord(int image_id) throws SQLException{
	  return getGroundTruthRecordWithId(getImageId(image_url));
	}
	*/	
	
	public static void main(String [] args) {
		try {
			int image_id = 1000;
			String authorName = "test";
			int groundTruthStatus = 2;
			String [] regions = new String[]{"f[0,0;0,0]"};
			java.util.Date initial_date = new java.util.Date();
			java.util.Date final_date = new java.util.Date();
			//GroundTruthRecord row = new GroundTruthRecord();

			Class.forName("com.mysql.jdbc.Driver");
    String url = "jdbc:mysql://lhce-pl-db.nlm.nih.gov:3306/pl?autoReconnect=true";

    Connection con = java.sql.DriverManager.getConnection(url, "imagestats", "1mageStats");
		//ImageStatsDB idb = new ImageStatsDB();
		StringBuffer sql = new StringBuffer();
			sql.append("replace into imagestats (image_id, gt_initial_person,gt_final_person, initial_regions,  final_regions, initial_updated_time, final_updated_time, groundTruthStatus)");
			sql.append("values (?, ?, ?, ?, ?, ?, ?, ?)");

	  PreparedStatement pstmt = con.prepareStatement(sql.toString());
	  pstmt.setInt(1, image_id);
	  pstmt.setString(2, authorName);
	  pstmt.setString(3, authorName);
	  pstmt.setString(4, regions[0]);
	  pstmt.setString(5, regions[0]);
	  java.sql.Timestamp sqlDate = null;
	  java.util.Date utilDate = initial_date;
	  if(utilDate != null) {
	  	sqlDate = new java.sql.Timestamp(utilDate.getTime());
	  }
	  pstmt.setTimestamp(6, sqlDate);
	  sqlDate = null;
	  utilDate = final_date;
	  if(utilDate != null) {
	  	sqlDate = new java.sql.Timestamp(utilDate.getTime());
	  }
	  pstmt.setTimestamp(7, sqlDate);
	  //pstmt.setDate(8, row.get_final_update_time());
	  pstmt.setInt(8, groundTruthStatus);
	  
	  pstmt.execute();
	  
	  pstmt.close();
	  
	  sql.delete(0, sql.length());
	  sql.append("select * from imagestats where image_id = "+image_id);
	  pstmt = con.prepareStatement(sql.toString());
	  ResultSet rs = pstmt.executeQuery(sql.toString());
    java.sql.ResultSetMetaData rsMetaData = rs.getMetaData();
    int numberOfColumns = rsMetaData.getColumnCount();
    for (int i = 1; i < numberOfColumns + 1; i++) {
      String columnName = rsMetaData.getColumnName(i);
      System.out.print(columnName + "\t");
    }
    System.out.println();
    System.out.println("----------------------");

    while(rs.next()) {
	  	for(int i = 1; i <= numberOfColumns; i++) {
	  	  System.out.print(rs.getObject(i).toString()+"\t");
	  	}
  	  System.out.println();
	  }
    
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
