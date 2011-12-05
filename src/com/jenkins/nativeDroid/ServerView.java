package com.jenkins.nativeDroid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

public class ServerView {

	public List<HashMap<String, Object>> showServers(Iterator server_list, JSONObject object) {
		RSSReader reader_server = RSSReader.getInstance();
		String string_server;
		List<HashMap<String, Object>> fillMaps = new ArrayList<HashMap<String, Object>>();
		while(server_list.hasNext()) {
			try {
			    String element = server_list.next().toString(); 
			    JSONObject current = object.getJSONObject(element);
//			    String title = current.getString("title"),
			    String title = element,
			    		url = current.getString("url");
			    boolean flag = current.getBoolean("visible"),
			    		error = true;
		    
			    if ( flag ==  true ) {
			    	string_server = reader_server.writeNews(url);
			    	int icon_server = R.drawable.icon_server_error; //error
					if ( string_server != "" ) {
						error = false;
						icon_server = R.drawable.icon_server_stable; //stable server
				    	String[] details_server = string_server.split("#l#");
				    	for(int i = 0; i < details_server.length; i++){
				    		String[] current_server = details_server[i].split("#t#");
				    		String title_server = current_server[0];
				    		String[] titleContents = title_server.split("\\(");
				    		String status = titleContents[1];
				    		if ( status.indexOf( "stable" ) < 0 && status.indexOf( "normal" ) < 0 && status.indexOf( "?" ) < 0 ) {
				    			icon_server = R.drawable.icon_server_fail; //fail server
				    		}
				    	}
			    	}
			    	HashMap<String, Object> map = new HashMap<String, Object>();
			    	map.put("error", error);
			    	map.put("server_name", title); //server_name
			    	map.put("server_url", url); //server_url
			    	map.put("server_image", icon_server);
			    	fillMaps.add(map);
			    }
			} catch(Exception e) {
				
			}
		}
		return fillMaps;
	}
}
