package com.pico.mobads;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.text.TextUtils;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.BannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.umeng.analytics.MobclickAgent;

public class MobAds {
	
	@SuppressWarnings("deprecation")
	public static void showAdView(final Activity act) {
		// Load Banner Ad
		String configStr = MobclickAgent.getConfigParams(act, "AdsConfig");
		if (!TextUtils.isEmpty(configStr)) {
			AdsConfig.parse(configStr);
		}
		AdsConfig adsConfig = AdsConfig.getInstance();
		if (AdsConfig.ENABLE_AD && adsConfig.adBanner_enabled) {
			BannerView adView = new BannerView(act, ADSize.BANNER, adsConfig.app_id, adsConfig.adBanner_posid);
			adView.setADListener(new BannerADListener() {
				@Override
				public void onADClicked() {
					MobclickAgent.onEvent(act, "click_bannerAd");
				}
				
				@Override
				public void onADCloseOverlay() {
				}
				
				@Override
				public void onADClosed() {
				}
				
				@Override
				public void onADExposure() {
				}
				
				@Override
				public void onADLeftApplication() {
				}
				
				@Override
				public void onADOpenOverlay() {
				}
				
				@Override
				public void onADReceiv() {
				}
				
				@Override
				public void onNoAD(int arg0) {
				}
			});
			
			RelativeLayout parentLayout = new RelativeLayout(act);
			RelativeLayout.LayoutParams parentLayputParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT);
			act.addContentView(parentLayout, parentLayputParams);
			
			int w = (act.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) ? 640
					: LayoutParams.FILL_PARENT;
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(w, LayoutParams.WRAP_CONTENT);
			if (adsConfig.adBanner_display.equals("top")) {
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
			} else if (adsConfig.adBanner_display.equals("bottom")) {
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
			}
			layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
			parentLayout.addView(adView, layoutParams);
			adView.loadAD();
		}
	}
}
