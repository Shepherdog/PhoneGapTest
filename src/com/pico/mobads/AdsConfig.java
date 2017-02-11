package com.pico.mobads;

import com.google.gson.Gson;

/**
 * AdsConfig Entity
 * 
 * @author Alex Kon
 *
 */
public final class AdsConfig {
	/** Should be used for all trigger. */
	public static boolean ENABLE_AD = true;
	
	public String app_id = "";
	
	public boolean adBanner_enabled = false;
	public String adBanner_posid = "";
	public String adBanner_display = "";
	
	public boolean adInter_enabled = false;
	public String adInter_posid = "";
	
	public boolean adSplash_enabled = false;
	public String adSplash_posid = "";
	
	private static AdsConfig instance = null;
	
	private AdsConfig() {
		// No public constructor
	}
	
	public static AdsConfig getInstance() {
		if (instance == null) {
			instance = new AdsConfig();
		}
		return instance;
	}
	
	public static AdsConfig parse(String json) {
		instance = new Gson().fromJson(json, AdsConfig.class);
		return instance;
	}
	
	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
