package gov.nih.nlm.ceb.lpf.imagestats.shared;

public class ImageStatsException extends Exception implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String localMessage = null;
	public ImageStatsException() {
		super();
		localMessage = getMessage();
	}

	public ImageStatsException(String msg) {
		this();
		localMessage = msg;
	}

	public String getISMessage() {
		return localMessage;
	}
	
	public String getMessage(){
		return localMessage;
	}
}
