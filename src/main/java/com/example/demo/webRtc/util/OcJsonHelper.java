package com.example.demo.webRtc.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class OcJsonHelper {

	private static Gson gson = null;
	static {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
		gson = gsonBuilder.create();
	}
	
	public static <T> String toJson(T obj) {
		return gson.toJson(obj);
	}
	
	public static <T> T toBean(String json, Class<T> responseType) {
		return gson.fromJson(json, responseType);
	}
	
	public static <T> JsonObject toJsonObject(T request) {
		return (JsonObject)gson.toJsonTree(request);
	}
	
	public static Map<String, String> toMap(JsonObject json) {
		Map<String, String> map = new HashMap<>();
		for (Entry<String, JsonElement> entry : json.entrySet()) {
			JsonElement ele = entry.getValue();
			String val = "";
			if (ele.isJsonPrimitive()) {
				val = ele.getAsString();
			} else {
				val = ele.toString();
			}
			map.put(entry.getKey(), val);
		}
		return map;
	}
}
