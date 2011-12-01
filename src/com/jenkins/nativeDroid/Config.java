package com.jenkins.nativeDroid;

import java.io.InputStream;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

public class Config {
	public JSONObject initialize (InputStream is) {
		JSONObject obj = null;
		try
		{
			byte [] buffer = new byte[is.available()];
			while (is.read(buffer) != -1);
			String json = new String(buffer);
			obj = new JSONObject(json);
		}
		catch (Exception je)
		{
			System.out.println("Error w/file: " + je.getMessage());
		}
		return obj;
	}
	
	public JSONObject remove (JSONObject obj, String str) {
		Iterator server_list = obj.keys();
		while(server_list.hasNext()) {
		    String element = server_list.next().toString(); 
		    if (element.equals(str)) {
		    	try {
					if (obj.has(str)) {
						obj.remove(str);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	break;
		    }
		}
		return obj;
	}
	
	public JSONObject add (JSONObject obj, String name, String url) {
		String newServer = "{\"title\": \""+ name +"\",\"url\": \""+ url +"\",\"visible\":true}";
		try {
			obj.put(name, new JSONObject(newServer));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	public JSONObject edit (JSONObject obj, String input_server, String input_url, String view_server, String view_url) {
		if (input_server.equals(view_server)) {
			if(!input_url.equals(view_url)) {
				try {
					String visible = obj.getJSONObject(view_server).getString("visible");
					obj.remove(view_server);
					obj.put(input_server, new JSONObject("{\"title\": \""+ input_server +"\",\"url\": \""+ input_url +"\",\"visible\": "+ visible +"}"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			obj.remove(view_server);
			add(obj, input_server, input_url);
		}
		return obj;
	}
	
	public void save (JSONObject obj) {
		//do nothing
	}
}
