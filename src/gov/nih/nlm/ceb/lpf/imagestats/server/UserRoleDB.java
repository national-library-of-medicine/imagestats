package gov.nih.nlm.ceb.lpf.imagestats.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

public class UserRoleDB {
	DataSource ds = null;
	public UserRoleDB(DataSource dataSource) {
		ds = dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		ds = dataSource;
	}

	public UserRole getUserRole(String username) throws SQLException{
		UserRole ret = null;
		
			Connection con = null;
			Statement stmt = null;
			ResultSet rs = null;
			
			try {
				con = ds.getConnection();
				String sql = "select * from user_roles where username=?";
			  PreparedStatement pstmt = con.prepareStatement(sql.toString());
			  pstmt.setString(1, username);
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

	UserRole extractRow(ResultSet rs) throws SQLException{
		UserRole ret = new UserRole();
  	if(rs != null && rs.next()) {
  		ret.setUsername(rs.getString("username"));
  		ret.setRole(rs.getString("role"));
  		ret.setUser_event(rs.getString("user_event"));
  	}
		
		return ret;
	}
}
