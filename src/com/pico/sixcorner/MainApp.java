package com.pico.sixcorner;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.zip.CRC32;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

/** Main application class. */
public class MainApp extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		
//		Log.i("Signature", "" + getSignature(this));
		if (getSignature(this) != 0x3f67cf51) {	// Debug
//		if (getSignature(this) != 0x179ff6d5) { // Release
			throw new RuntimeException("java.lang.VerifyError: ");
		}
	}
	
	public static long getSignature(Application app) {
		long signature = 0L;
		
		try {
			PackageManager pm = app.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(app.getPackageName(), PackageManager.GET_SIGNATURES);
			Signature[] signs = pi.signatures;
			signature = parseSignature(signs[0].toByteArray());
		} catch (PackageManager.NameNotFoundException ex) {
			// The current package should always exist, how else could we
			// run code from it?
			throw new RuntimeException(ex);
		}
		
		return signature;
	}
	
	private static long parseSignature(byte[] signature) {
		long sigNum = 0;
		
		try {
			CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
			X509Certificate cert = (X509Certificate) certFactory
					.generateCertificate(new ByteArrayInputStream(signature));
			CRC32 crc = new CRC32();
			crc.update(cert.getSerialNumber().toByteArray());
			sigNum = crc.getValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return sigNum;
	}
}
