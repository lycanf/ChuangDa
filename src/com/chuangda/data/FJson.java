package com.chuangda.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FJson {

	public static final String error = "fjson_error";
	
	JSONObject jsonObject = null;
	public boolean canUse = true;
	
	public FJson(String str) {
		try {
			jsonObject = new JSONObject(str);
		} catch (JSONException e) {
			e.printStackTrace();
			canUse = false;
		}
	}
	
	public String getString(String key){
		String ret = "";
		if(canUse){
			if(jsonObject.has(key)){
				try {
					ret = jsonObject.getString(key);
				} catch (JSONException e) {
					e.printStackTrace();
					ret = "";
				}
			}
		}
		return ret;
	}
	
	public String[] getStringArray(String key){
		String[] ret = null;
		if(canUse){
			if(jsonObject.has(key)){
				try {
					JSONArray array = jsonObject.getJSONArray(key);
					if(array.length() > 0){
						ret = new String[array.length()];
						for(int i=0; i<array.length();i++){
							ret[i] = array.getString(i);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
					ret = null;
				}
			}
		}
		return ret;
	}
	
	public int getInt(String key){
		int ret = 0;
		if(canUse){
			if(jsonObject.has(key)){
				try {
					ret = jsonObject.getInt(key);
				} catch (JSONException e) {
					e.printStackTrace();
					ret = 0;
				}
			}
		}
		return ret;
	}
	
	public float getFloat(String key){
		float ret = 0;
		if(canUse){
			if(jsonObject.has(key)){
				try {
					ret = (float) jsonObject.getDouble(key);
				} catch (JSONException e) {
					e.printStackTrace();
					ret = 0;
				}
			}
		}
		return ret;
	}

}
