package net.rdrei.android.scdl2.service;

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
public class MediaScannerService extends Service implements
		MediaScannerConnectionClient {

	public static final String EXTRA_PATH = "path";

	private MediaScannerConnection mScannerConnection;
	private String mPath;

	@Override
	public void onCreate() {
		mScannerConnection = new MediaScannerConnection(this, this);
	}

	@Override
	public IBinder onBind(final Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(final Intent intent, final int flags, final int startId) {
		if (intent == null) {
			stopSelf();
		} else {
			mPath = intent.getStringExtra(EXTRA_PATH);
			mScannerConnection.connect();
		}

		return START_STICKY;
	}

	@Override
	public void onMediaScannerConnected() {
		Ln.d("Starting media scan for new file %s", mPath);
		mScannerConnection.scanFile(mPath, null);
	}

	@Override
	public void onScanCompleted(final String path, final Uri uri) {
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
