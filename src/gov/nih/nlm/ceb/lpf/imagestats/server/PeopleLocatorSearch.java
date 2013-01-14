package gov.nih.nlm.ceb.lpf.imagestats.server;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public interface PeopleLocatorSearch {

	public static String imagePrefix = "https://pl.nlm.nih.gov/";
	//public int getSearchCount(String query, String event)throws IOException;
	public String search(String sourceUrl, Map<String, Set<String>> searchParams)throws IOException;
}
