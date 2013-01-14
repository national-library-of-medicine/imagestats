package gov.nih.nlm.ceb.lpf.imagestats.server;

import java.io.Serializable;


public class UserRole implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String username = null;
	String user_role = null;
	String user_event = null;
	public UserRole() {
		super();
	}
	
	public UserRole(UserRole other) {
		username = other.getUsername();
		user_role = other.getUser_role();
		user_event = other.getUser_event();
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getUser_role() {
		return user_role;
	}
	
	public String getUser_event() {
		return user_event;
	}
	
	public void setUsername(String val) {
		username = val;
	}
	
	public void setRole(String val) {
		user_role = val;
	}
	
	public void setUser_event(String val) {
		user_event = val;
	}

}
