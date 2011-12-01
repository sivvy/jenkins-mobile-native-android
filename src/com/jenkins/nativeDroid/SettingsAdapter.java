package com.jenkins.nativeDroid;

import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class SettingsAdapter extends SimpleAdapter {
	JSONObject Config;
	public SettingsAdapter(Context context,
			List<HashMap<String, Object>> settingMaps, int resource, String[] from,
			int[] to, JSONObject config) {
		super(context, settingMaps, resource, from, to);
		Config = config;
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		@SuppressWarnings("unchecked")
		HashMap<String, Object>	current_server = (HashMap<String, Object>)this.getItem(position);
		boolean visible = (Boolean) current_server.get("visible");
		TextView name = (TextView) view.findViewById(R.id.setting_server_name);
		String server_name = name.getText().toString();
		boolean flag = true;
		try {
			flag = Config.getJSONObject(server_name).getBoolean("visible");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (visible == false && flag == false) {
			view.setBackgroundColor(Color.GRAY);
			name.setTextColor(Color.WHITE);
		} else {
			view.setBackgroundColor(Color.WHITE);
			name.setTextColor(Color.BLACK);
		}
		
		return view;
	}

}
