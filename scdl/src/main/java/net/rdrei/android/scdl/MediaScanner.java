package net.rdrei.android.scdl;

import roboguice.util.Ln;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;

import com.google.inject.Inject;

public class MediaScanner implements MediaScannerConnectionClient {
	@Inject
	private Context mContext;
	
	private MediaScannerConnection mScannerConnection;
	private String mPath;
	
	public MediaScanner() {
		
	}
	
	public void scanFile(String path) {
		mPath = path;
		
		mScannerConnection = new MediaScannerConnection(mContext, this);
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
		if (path.equals(mPath)) {
			mScannerConnection.disconnect();
		}
	}
}
