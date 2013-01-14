package gov.nih.nlm.ceb.lpf.imagestats.client;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SearchCount implements AsyncCallback<Integer> {
	private int count = -1;
	private Throwable t = null;
	boolean executed = false;
	Timer tm = new Timer() {
		public void run() {
		}
	};
	

	public void onFailure(Throwable th) {
			t = th;
			count = -1;
			executed = true;
	}

	public void onSuccess(Integer searchCount) {
			t = null;
			count = searchCount;
			executed = true;
	}

	public int getSearchCount() {
		while (!executed) {
			tm.schedule(20);
		}
			return count;
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
