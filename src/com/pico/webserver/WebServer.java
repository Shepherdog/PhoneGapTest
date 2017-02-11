package com.pico.webserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import android.content.Context;
import android.util.Log;

import com.pico.webserver.NanoHTTPD.Response.Status;

/**
 * Tiny server that serving static game assets.
 * 
 * @author Alex Kon
 *
 */
public class WebServer extends NanoHTTPD {
	private static final String TAG = "WebServer";
	
	public static final String MIME_JS = "application/javascript", MIME_JSON = "application/json",
			MIME_CSS = "text/css", MIME_XML = "text/xml", MIME_PNG = "image/png", MIME_JPG = "image/jpeg",
			MIME_MP3 = "audio/mp3", MIME_OGG = "audio/ogg", MIME_DEFAULT_BINARY = "application/octet-stream";
	
	private Context mContext;
	
	public WebServer(Context ctx) throws IOException {
		super(8080);
		
		mContext = ctx;
	}
	
	@Override
	public Response serve(String uri, Method method, Map<String, String> header, Map<String, String> parameters,
			Map<String, String> files) {
//		Log.d(TAG, "SERVE ::  URI " + uri);
		
		final StringBuilder buf = new StringBuilder();
		for (Entry<String, String> kv : header.entrySet())
			buf.append(kv.getKey() + " : " + kv.getValue() + "\n");
		
		String root = "www/";
		InputStream mbuffer = null;
		try {
			if (uri != null) {
				if (uri.contains(".js")) {
					mbuffer = mContext.getAssets().open(root + uri.substring(1));
					return new NanoHTTPD.Response(Status.OK, MIME_JS, mbuffer, -1);
				} else if (uri.contains(".css")) {
					mbuffer = mContext.getAssets().open(root + uri.substring(1));
					return new NanoHTTPD.Response(Status.OK, MIME_CSS, mbuffer, -1);
				} else if (uri.contains(".png")) {
					mbuffer = mContext.getAssets().open(root + uri.substring(1));
					return new NanoHTTPD.Response(Status.OK, MIME_PNG, mbuffer, -1);
				} else if (uri.contains(".json")) {
					mbuffer = mContext.getAssets().open(root + uri.substring(1));
					return new NanoHTTPD.Response(Status.OK, MIME_JSON, mbuffer, -1);
				} else if (uri.contains(".xml")) {
					mbuffer = mContext.getAssets().open(root + uri.substring(1));
					return new NanoHTTPD.Response(Status.OK, MIME_XML, mbuffer, -1);
				} else if (uri.contains(".jpg")) {
					mbuffer = mContext.getAssets().open(root + uri.substring(1));
					return new NanoHTTPD.Response(Status.OK, MIME_JPG, mbuffer, -1);
				} else if (uri.contains(".mp3")) {
					mbuffer = mContext.getAssets().open(root + uri.substring(1));
					return new NanoHTTPD.Response(Status.OK, MIME_MP3, mbuffer, -1);
				} else if (uri.contains(".ogg")) {
					mbuffer = mContext.getAssets().open(root + uri.substring(1));
					return new NanoHTTPD.Response(Status.OK, MIME_OGG, mbuffer, -1);
				} else if (uri.contains(".fnt")) {
					mbuffer = mContext.getAssets().open(root + uri.substring(1));
					return new NanoHTTPD.Response(Status.OK, MIME_PLAINTEXT, mbuffer, -1);
				} else if (uri.contains("/mnt/sdcard")) {
					Log.d(TAG, "request for media on sdCard " + uri);
					File request = new File(uri);
					mbuffer = new FileInputStream(request);
					FileNameMap fileNameMap = URLConnection.getFileNameMap();
					String mimeType = fileNameMap.getContentTypeFor(uri);
					
					Response streamResponse = new Response(Status.OK, mimeType, mbuffer, -1);
					Random rnd = new Random();
					String etag = Integer.toHexString(rnd.nextInt());
					streamResponse.addHeader("ETag", etag);
					streamResponse.addHeader("Connection", "Keep-alive");
					
					return streamResponse;
				} else {
					mbuffer = mContext.getAssets().open(root + "index.html");
					return new NanoHTTPD.Response(Status.OK, MIME_HTML, mbuffer, -1);
				}
			}
		} catch (IOException e) {
			Log.d(TAG, "Error opening file" + uri.substring(1));
			e.printStackTrace();
		}
		
		return null;
	}
}