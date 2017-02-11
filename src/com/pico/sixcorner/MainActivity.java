/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

package com.pico.sixcorner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.cordova.CordovaChromeClient;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaPreferences;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewClient;
import org.apache.cordova.PluginEntry;
import org.apache.cordova.Whitelist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;

import com.pico.mobads.MobAds;
import com.pico.webserver.WebServer;
import com.umeng.analytics.MobclickAgent;

public class MainActivity extends Activity implements CordovaInterface {
//	private Handler mUiThreadHandler = new Handler(Looper.getMainLooper());

	private final ExecutorService threadPool = Executors.newCachedThreadPool();

	private CordovaWebView mWebView;

    // Plugin to call when activity result is received
    protected int activityResultRequestCode;
    protected CordovaPlugin activityResultCallback;

    protected CordovaPreferences prefs = new CordovaPreferences();
    protected Whitelist internalWhitelist = new Whitelist();
    protected Whitelist externalWhitelist = new Whitelist();
    protected ArrayList<PluginEntry> pluginEntries;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		WebServer server = null;
		try {
			server = new WebServer(getApplicationContext());
			try {
				server.start();
				System.out.println("\nRunning! Point your browers to http://localhost:8080/ \n");
			} catch (IOException ioe) {
				System.err.println("Couldn't start server:\n" + ioe);
				// System.exit(-1);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

        internalWhitelist.addWhiteListEntry("*", false);
        externalWhitelist.addWhiteListEntry("tel:*", false);
        externalWhitelist.addWhiteListEntry("sms:*", false);
        prefs.set("loglevel", "DEBUG");
        
		mWebView = (CordovaWebView) findViewById(R.id.webView);
		
        mWebView.init(this, makeWebViewClient(mWebView), makeChromeClient(mWebView),
        		pluginEntries, internalWhitelist, externalWhitelist, prefs);
        
		mWebView.loadUrl("file:///android_asset/www/index.html");
//		mWebView.loadUrl("http://localhost:8080/index.html");
		
		MobclickAgent.updateOnlineConfig(this);// 友盟数据更新
		MobclickAgent.onError(this);// 友盟错误报告
//		MobclickAgent.setOnlineConfigureListener(new UmengOnlineConfigureListener() {
//			@Override
//			public void onDataReceived(JSONObject arg0) {
//				mUiThreadHandler.post(new Runnable() {
//					@Override
//					public void run() {
//						showAdView();
//					}
//				});
//			}
//		});
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				MobAds.showAdView(MainActivity.this);
			}
		}, 3500);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Dispatch "Back" key
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			showExitConfirmDialog();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}
	
	protected void showExitConfirmDialog() {
		AlertDialog.Builder builder = new Builder(this);
		
		builder.setTitle("提示");
		builder.setMessage("确定要离开吗？");
		
		builder.setPositiveButton("是的", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				
				finish();
			}
		});
		
		builder.setNegativeButton("不要", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		
		builder.create().show();
	}

    protected CordovaWebViewClient makeWebViewClient(CordovaWebView webView) {
        return webView.makeWebViewClient(this);
    }
    
    protected CordovaChromeClient makeChromeClient(CordovaWebView webView) {
        return webView.makeWebChromeClient(this);
    }
    
	@Override
	public Activity getActivity() {
		return this;
	}

	@Override
	public ExecutorService getThreadPool() {
		return threadPool;
	}

	@Override
	public Object onMessage(String id, Object data) {
		if ("exit".equals(id)) {
			super.finish();
        }
		return null;
	}

	@Override
	public void setActivityResultCallback(CordovaPlugin plugin) {
        if (activityResultCallback != null) {
            activityResultCallback.onActivityResult(activityResultRequestCode, Activity.RESULT_CANCELED, null);
        }
        this.activityResultCallback = plugin;
	}

	@Override
	public void startActivityForResult(CordovaPlugin command, Intent intent, int requestCode) {
		setActivityResultCallback(command);
		try {
			startActivityForResult(intent, requestCode);
		} catch (RuntimeException e) {
			activityResultCallback = null;
			throw e;
		}
	}
}
