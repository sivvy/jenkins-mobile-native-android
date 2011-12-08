package com.jenkins.nativeDroid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Interpolator.Result;
import android.os.AsyncTask;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ServerView extends AsyncTask<String, Integer, Long> {
	ListView dynamicTable;
	Context context;
	JSONObject config;
	List<HashMap<String, Object>> fillMaps;
	final static String[] from = new String[] { "server_image","server_name", "server_url"};
	final static int[] to = new int[] { R.id.server_image, R.id.server_name, R.id.server_url };
	ProgressDialog loadingBox;
	
	public ServerView(Context c, ListView table, JSONObject conf) {
		super();
		dynamicTable = table;
		context = c;
		config = conf;
		loadingBox = new ProgressDialog(c);
		loadingBox.setMessage("Loading...");
	}
	
	public boolean showServers() {
		Iterator server_list = config.keys();
		RSSReader reader_server = RSSReader.getInstance();
		String string_server;
        
		fillMaps = new ArrayList<HashMap<String, Object>>();
		dynamicTable.setTag("servers");
		while(server_list.hasNext()) {
			try {
				if (isCancelled()) {
					return false;
				}
			    String title = server_list.next().toString(); 
			    JSONObject current = config.getJSONObject(title);
			    String url = current.getString("url");
			    
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
			    	publishProgress(0);
			    }
			} catch(Exception e) {
				System.out.println("Error in showServers: " + e);
			}
		}
		return false;
	}
	
	void updateUI() {
		if (isCancelled()) {
			loadingBox.dismiss();
			return;
		}
		SimpleAdapter adapter = new SimpleAdapter(context, fillMaps, R.layout.grid_item_2, from, to);
    	dynamicTable.setAdapter(adapter);
	}
	
	protected void onCancelled(Result result) {
		dynamicTable.setAdapter(null);
		loadingBox.dismiss();
	}
	
	protected void onProgressUpdate(Integer... valus) {
		this.updateUI();
    }
	
	protected void onPostExecute(Long result) {
		loadingBox.dismiss();
	}
	
	protected void onPreExecute() {
		loadingBox.show();
	}
	
	@Override
	protected Long doInBackground(String... params) {
		this.showServers();
		return null;
	}
}
