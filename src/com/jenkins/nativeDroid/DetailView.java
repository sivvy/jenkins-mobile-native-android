package com.jenkins.nativeDroid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DetailView {
	String[] details;
	public List<HashMap<String, Object>> showDetails(String server_url) {
		RSSReader reader = RSSReader.getInstance();
		String string = reader.writeNews(server_url);
		details = string.split("#l#");
		// prepare the list of all records
		List<HashMap<String, Object>> fillMaps = new ArrayList<HashMap<String, Object>>();
		int ctr = 0;
		for(int i = 0; i < details.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			String[] current = details[i].split("#t#");
			String title = current[0],
					date_time = current[1];
			String[] titleContents = title.split("\\("),
					line = titleContents[0].split(" ");
			String status = titleContents[1];
			int icon_name;
			
			if (status.indexOf("stable") >= 0 || status.indexOf("normal") >= 0) {
		        icon_name = R.drawable.icon_stable;
		    } else if (status.indexOf("?") >= 0) {
		        icon_name = R.drawable.icon_building;
		    } else {
		        icon_name = R.drawable.icon_fail;
		    };
		    map.put("id", ctr++);
			map.put("details_image", icon_name);
			map.put("details_name", line[0]);
			map.put("details_datetime", date_time.replaceAll("[TZ]", " "));
			map.put("details_number", line[1]);
			fillMaps.add(map);
		}
		return fillMaps;
	}
}
