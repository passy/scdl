package net.rdrei.android.scdl;

import java.io.File;
import java.io.IOException;

import net.rdrei.android.scdl.api.entity.TrackEntity;
import roboguice.util.Ln;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import com.google.inject.Inject;

/**
 * Class for handling the downloading of tracks.
 * 
 * @author pascal
 * 
 */
public class TrackDownloader {

	@Inject
	private Context mContext;

	private Uri mUri;
	private TrackEntity mTrack;

	public TrackDownloader(Uri mUri, TrackEntity mTrack) {
		super();
		this.mUri = mUri;
		this.mTrack = mTrack;
	}

	/**
	 * Enqueues the job into the download manager.
	 */
	public void enqueue() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				DownloadManager downloadManager = (DownloadManager) mContext
						.getSystemService(Context.DOWNLOAD_SERVICE);
				Ln.d("Starting download of %s.", mUri.toString());
				final Request request;
				try {
					request = createDownloadRequest(mUri);
				} catch (IOException e) {
					// We can't correctly bubble this up. Please let's not make
					// this happen.
					throw new IllegalStateException(e);
				}

				downloadManager.enqueue(request);
			}
		}).start();
	}

	/**
	 * Check if the given path is writable and attempts to create it.
	 */
	public static boolean checkAndCreateTypePath(File path) {
		if (!path.exists()) {
			return path.mkdirs();
		}

		return (path.isDirectory() && path.canWrite());
	}

	/**
	 * Creates a new download manager request based on the given uri.
	 * 
	 * @param uri
	 * @return
	 * @throws IOException
	 *             If directory can't be used for saving the file.
	 */
	private DownloadManager.Request createDownloadRequest(final Uri uri)
			throws IOException {
		final Request request = new Request(uri);
		// Path based on the public Music directory and a - currently -
		// hard-coded value.
		final File typePath = new File(Environment.DIRECTORY_MUSIC,
				"Soundcloud");

		if (!checkAndCreateTypePath(typePath)) {
			throw new IOException(String.format(
					"Can't open directory %s to write.", typePath.toString()));
		}

		request.setTitle(mTrack.getTitle());
		request.setDescription(mContext
				.getString(R.string.download_description));
		request.setDestinationInExternalPublicDir(typePath.toString(),
				mTrack.getDownloadFilename());

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// We have an audio file, please scan it!
			request.allowScanningByMediaScanner();
		}

		return request;
	}

}
