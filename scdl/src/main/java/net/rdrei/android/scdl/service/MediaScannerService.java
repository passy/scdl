package net.rdrei.android.scdl.service;

import roboguice.util.Ln;
import android.app.Service;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.IBinder;

/**
 * Calls the MediaScanner with the given path.
 * 
 * This is necessary in order to get called from the BroadcastReceive which may
 * not be able to bind to a system service.
 * 
 * @author pascal
 * 
 */
public class MediaScannerService extends Service implements MediaScannerConnectionClient {
	
	public static final String EXTRA_PATH = "path";
	
	private MediaScannerConnection mScannerConnection;
	private String mPath;
	
	@Override
	public void onCreate() {
		mScannerConnection = new MediaScannerConnection(this, this);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		mPath = intent.getStringExtra(EXTRA_PATH);
		mScannerConnection.connect();
	}

	@Override
	public void onMediaScannerConnected() {
		Ln.d("Starting media scan for new file %s", mPath);
		mScannerConnection.scanFile(mPath, null);
	}

	@Override
	public void onScanCompleted(String path, Uri uri) {
		Ln.d("Media scan completed.");
		stopSelf();
	}

	@Override
	public void onDestroy() {
		if (mScannerConnection != null) {
			mScannerConnection.disconnect();
			mScannerConnection = null;
		}
		super.onDestroy();
	}
}
