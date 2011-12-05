package com.jenkins.nativeDroid;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.jenkins.*;

public class JenkinsNativeDroidActivity extends Activity {
	/** Called when the activity is first created. */
	TextView title;
	LinearLayout rowDefault, rowSettings;
	String[] details;
	ListView dynamicTable;
	JSONObject Config;
	List<HashMap<String, Object>> settingMaps;
	SettingsAdapter settingsAdapter;
	Config conf = new Config();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	    
	    title = (TextView) findViewById(R.id.app_title);
	    rowDefault = (LinearLayout) findViewById(R.id.icon_row_default);
	    rowSettings = (LinearLayout) findViewById(R.id.icon_row_settings);
	    rowSettings.setVisibility(View.GONE);
	    
	    dynamicTable = (ListView)findViewById(R.id.listview);
	    
	    ImageView home = (ImageView) findViewById(R.id.home);
	    ImageView refresh = (ImageView) findViewById(R.id.refresh);
	    ImageView settings = (ImageView) findViewById(R.id.settings);
      
	    ImageView backButton = (ImageView) findViewById(R.id.done);
	    ImageView addButton = (ImageView) findViewById(R.id.add);
	    
	    InputStream is = this.getResources().openRawResource(R.raw.config_latest);
		Config = conf.initialize(is);
	    
	    drawServers();
	    home.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if ( dynamicTable.getTag().toString() != "servers" ) {
					Toast.makeText(getApplicationContext(), "GOING HOME", Toast.LENGTH_SHORT).show();
					dynamicTable.setAdapter(null);
					drawServers();
				}
			}
		});
	    
	    refresh.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if ( dynamicTable.getTag().toString() == "servers" ) {
					Toast.makeText(getApplicationContext(), "Server refresh", Toast.LENGTH_SHORT).show();
					dynamicTable.setAdapter(null);
					drawServers();
				} else {
					Toast.makeText(getApplicationContext(), dynamicTable.getTag().toString(), Toast.LENGTH_SHORT).show();
					drawDetail(dynamicTable.getTag().toString());
				}
			}
		});
	    
	    settings.setOnClickListener(new View.OnClickListener() {
    	   public void onClick(View v) {
     		   rowDefault.setVisibility(View.GONE);
     		   rowSettings.setVisibility(View.VISIBLE);
     		  drawSettings();
    	   }
      	} );
	    
	    backButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				rowDefault.setVisibility(View.VISIBLE);
				rowSettings.setVisibility(View.GONE);
				Toast.makeText(getApplicationContext(), "HOME from SETTING", Toast.LENGTH_SHORT).show();
				dynamicTable.setAdapter(null);
				drawServers();
			}
		});
	    
	    addButton.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View v) {
	    	    final CustomDialog addDialog = new CustomDialog(JenkinsNativeDroidActivity.this, "New");
	    	    Button okbutton = (Button) addDialog.findViewById(R.id.Button01);
	    		okbutton.setOnClickListener(new View.OnClickListener() {
	    			public void onClick(View v) {
	    				EditText name = (EditText) addDialog.findViewById(R.id.edittext01);
	    				EditText url = (EditText) addDialog.findViewById(R.id.edittext02);
	    				Config = conf.add(Config, name.getText().toString(), url.getText().toString());
	    				addDialog.dismiss();
	    				conf.save(Config);
	    				addRow(url.getText().toString(), name.getText().toString(), "add", null, null);
	    			}
	    		});
	    	}
	    });
	    
	    dynamicTable.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view,
        	        int position, long id) {
        		if ( parent.getTag().toString() == "servers" ) {
	        		TextView link = (TextView)view.findViewById(R.id.server_url);
	        		@SuppressWarnings("unchecked")
					HashMap<String, Object>	current_server = (HashMap<String, Object>)dynamicTable.getItemAtPosition(position);
	        		boolean error = (Boolean) current_server.get("error");
	        		if (error == false)
	        			drawDetail(link.getText().toString());
        		} else if (parent.getTag().toString() == "settings") {
        			TextView server_name = (TextView)view.findViewById(R.id.setting_server_name);
        			Iterator settings_servers = Config.keys();
        			while(settings_servers.hasNext()) {
        			    String element = settings_servers.next().toString();
        			    try {
	        			    if (element.equals(server_name.getText().toString())) {
	        			    	if (Config.getJSONObject(element).getString("visible") ==  "true" ) {
	        			    		Config.getJSONObject(element).put("visible", false);
	        			    	} else {
	        			    		Config.getJSONObject(element).put("visible", true);
	        			    	}
	        			    	break;
	        			    }
        			    } catch (Exception e) {
        			    	
        			    }
        			}
        			drawSettings();
        		}
        	}
        });
	    
	    dynamicTable.setOnItemLongClickListener(new OnItemLongClickListener() {
	    	public boolean onItemLongClick(AdapterView<?> parent, View view,
        	        int position, long id) {
	    		if ( parent.getTag().toString() == "settings" ) {
	    			final TextView name = (TextView)view.findViewById(R.id.setting_server_name);
	    			final TextView url = (TextView)view.findViewById(R.id.setting_server_url);
	    			final CustomDialog editDialog = new CustomDialog(JenkinsNativeDroidActivity.this, "Edit");
	    			final EditText nameBox = (EditText) editDialog.findViewById(R.id.edittext01);
    				final EditText urlBox = (EditText) editDialog.findViewById(R.id.edittext02);
	    			nameBox.setText(name.getText().toString(), TextView.BufferType.EDITABLE);
	    			urlBox.setText(url.getText().toString(), TextView.BufferType.EDITABLE);
	    			Button okbutton = (Button) editDialog.findViewById(R.id.Button01);
		    		okbutton.setOnClickListener(new View.OnClickListener() {
		    			public void onClick(View v) {
		    				Config = conf.edit(Config, nameBox.getText().toString(), urlBox.getText().toString(), name.getText().toString(), url.getText().toString());
		    				editDialog.dismiss();
		    				conf.save(Config);
		    				addRow(urlBox.getText().toString(), nameBox.getText().toString(), "edit", url.getText().toString(), name.getText().toString());
		    			}
		    		});
    				conf.save(Config);
	    		}
				return false;
	    	}
	    });
	    
	}
	
	public void drawServers() {
		try {
			Iterator server_list = Config.keys();
		    String[] from = new String[] { "server_image","server_name", "server_url"};
	        int[] to = new int[] { R.id.server_image, R.id.server_name, R.id.server_url };
	        ServerView list = new ServerView();
		    List<HashMap<String, Object>> fillMaps = list.showServers(server_list, Config);
			SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.grid_item_2, from, to);
	        dynamicTable.setAdapter(adapter);
	        dynamicTable.setTag("servers");
		} catch (Exception e) {
			System.out.println("Error in drawServers: " + e);
		}
	}
	
	public void drawDetail(String server_url) {
	    DetailView detail = new DetailView();
	    List<HashMap<String, Object>> fill = detail.showDetails(server_url);
        String[] from = new String[] { "details_image","details_name", "details_datetime", "details_number"};
        int[] to = new int[] { R.id.details_image, R.id.details_name, R.id.details_datetime, R.id.details_number };
        SimpleAdapter adapter = new SimpleAdapter(this, fill, R.layout.grid_item, from, to);
    	dynamicTable.setAdapter(adapter);
    	dynamicTable.setTag(server_url);
	}
	
	public void drawSettings () {
		Iterator server_names = Config.keys();
		String[] from = new String[] { "remove_server","setting_server_name", "setting_server_url"};
        int[] to = new int[] { R.id.remove_server, R.id.setting_server_name, R.id.setting_server_url};
        settingMaps = new ArrayList<HashMap<String, Object>>();
        while(server_names.hasNext()) {
        	try {
        		String element = server_names.next().toString();
        		JSONObject current = Config.getJSONObject(element);
        		String title = element,
        				url = current.getString("url");
        		boolean flag = current.getBoolean("visible");
    			HashMap<String, Object> map = new HashMap<String, Object>();
		    	map.put("setting_server_name", title); //server_name
		    	map.put("setting_server_url", url); //server_url
		    	map.put("remove_server", R.drawable.icon_delete);
		    	map.put("visible", flag);
		    	settingMaps.add(map);
        	} catch (Exception e) {
    			System.out.println("Error in drawSettings: " + e);
        	}
        }
        settingsAdapter = new SettingsAdapter(this, settingMaps, R.layout.grid_item_3, from, to, Config);
        dynamicTable.setAdapter(settingsAdapter);
        dynamicTable.setTag("settings");
        
	}
	
	public void addRow (String url, String name, String type, String old_url, String old_name) {
		if (type == "edit"){
			boolean visibility = false;
			boolean contains = false;
			HashMap<String, Object> old = null;
			for (Iterator<HashMap<String, Object>> i = settingMaps.iterator(); i.hasNext();) {
				HashMap<String, Object> current = (HashMap<String, Object>) i.next();
				if (current.containsValue(name)) {
					contains = true;
					visibility = (Boolean) current.get("visible");
					settingMaps.remove(current);
				} else if (current.containsValue(old_name)) {
					old = current;
				}
			}
			if (contains ==  false) {
				if (old != null) {
					settingMaps.remove(old);
				}
			}
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("visible", visibility);
			map.put("setting_server_name", name); //server_name
			map.put("setting_server_url", url); //server_url
			map.put("remove_server", R.drawable.icon_delete);
			settingMaps.add(map);
			
		}else {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("visible", true);
			map.put("setting_server_name", name); //server_name
			map.put("setting_server_url", url); //server_url
			map.put("remove_server", R.drawable.icon_delete);
			settingMaps.add(map);
		}
		settingsAdapter.notifyDataSetChanged();
	}
	
	public void removeRow (View img) {
		LinearLayout removable = (LinearLayout)img.getParent();
		ListView list = (ListView)removable.getParent();
		String value_remove = ((TextView)removable.getChildAt(0)).getText().toString();
		for (Iterator<HashMap<String, Object>> i = settingMaps.iterator(); i.hasNext();) {
			HashMap<String, Object> current = (HashMap<String, Object>) i.next();
			if (current.containsValue(value_remove)) {
				settingMaps.remove(current);
				break;
			}
		  }
		settingsAdapter.notifyDataSetChanged();
		Config = conf.remove(Config, value_remove);
		conf.save(Config);
	}
}

