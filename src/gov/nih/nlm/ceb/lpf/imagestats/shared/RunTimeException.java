package gov.nih.nlm.ceb.lpf.imagestats.shared;

public class RunTimeException extends Exception implements java.io.Serializable  {

	private static final long serialVersionUID = 1L;
	String localMessage = null;
	public RunTimeException() {
		super();
		localMessage = getMessage();
	}

	public RunTimeException(String msg) {
		localMessage = msg;
	}

	public String getISMessage() {
		return localMessage;
	}
}
