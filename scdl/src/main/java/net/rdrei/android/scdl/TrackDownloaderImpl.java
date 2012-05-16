package net.rdrei.android.scdl;

import java.io.File;
import java.io.IOException;

import net.rdrei.android.scdl.api.entity.TrackEntity;
import roboguice.util.Ln;
import roboguice.util.SafeAsyncTask;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.bugsense.trace.BugSenseHandler;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * Class for handling the downloading of tracks.
 * 
 * @author pascal
 * 
 */
public class TrackDownloaderImpl implements TrackDownloader {

	@Inject
	private Context mContext;

	@Inject
	private DownloadManager mDownloadManager;

	private final Uri mUri;
	private final TrackEntity mTrack;
	private final Handler mHandler;

	@Inject
	public TrackDownloaderImpl(@Assisted Uri mUri,
			@Assisted TrackEntity mTrack, @Assisted Handler handler) {
		super();
		this.mUri = mUri;
		this.mTrack = mTrack;
		this.mHandler = handler;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.rdrei.android.scdl.TrackDownloader#enqueue()
	 */
	@Override
	public void enqueue() throws IOException {
		StartDownloadTask startDownloadTask = new StartDownloadTask(mHandler);
		startDownloadTask.execute();
	}

	/**
	 * Check if the given path is writable and attempts to create it.
	 */
	public static boolean checkAndCreateTypePath(File path) {
		final File fullPath = new File(Environment
				.getExternalStorageDirectory().toString(), path.toString());

		if (!fullPath.exists()) {
			return fullPath.mkdirs();
		}

		return fullPath.isDirectory() && fullPath.canWrite();
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

	private class StartDownloadTask extends SafeAsyncTask<Void> {

		public StartDownloadTask(Handler handler) {
			super(handler);
		}

		@Override
		public Void call() throws Exception {
			Ln.d("Starting download of %s.", mUri.toString());
			final Request request;
			try {
				request = createDownloadRequest(mUri);
			} catch (IOException e) {
				throw new IOException(e);
			}

			mDownloadManager.enqueue(request);
			return null;
		}

		/**
		 * Catches exceptions during the creation of the download request and
		 * bubbles them up using the provided handler.
		 */
		@Override
		protected void onException(Exception e) throws RuntimeException {
			super.onException(e);
			final Message msg;

			if (handler == null) {
				BugSenseHandler.log("Download request error without handler.",
						e);
				return;
			}

			if (e instanceof IOException) {
				msg = handler.obtainMessage(MSG_DOWNLOAD_STORAGE_ERROR);
			} else {
				msg = handler.obtainMessage(MSG_DOWNLOAD_ERROR);
				BugSenseHandler.log("Download request error.", e);
			}

			handler.sendMessage(msg);
		}
	}
}
