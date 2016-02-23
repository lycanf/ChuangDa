package com.chuangda.data;

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

}
