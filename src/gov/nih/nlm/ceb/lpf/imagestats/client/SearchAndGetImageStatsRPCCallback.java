package gov.nih.nlm.ceb.lpf.imagestats.client;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SearchAndGetImageStatsRPCCallback implements AsyncCallback<String> {
	private Throwable t = null;
	String resp = null;
	boolean executed = false;
	Timer tm = new Timer() {
		public void run() {
		}
	};
	
	public void onFailure(Throwable th) {
			t = th;
			resp = null;
			executed = true;
	}
	
	public void onSuccess(String result) {
			t = null;
			resp = result;
			executed = true;
	}
	public String getImageStats() {
		while (!executed) {
			tm.schedule(20);
		}
		return resp;
}

	public Throwable getThrowable() {
		while (!executed) {
			tm.schedule(20);
		}
		return t;
	}
	
	public void reset() {
		  executed = false;
	}

}
