package net.rdrei.android.scdl.receiver;

import java.io.File;

import net.rdrei.android.scdl.Config;
import net.rdrei.android.scdl.R;
import net.rdrei.android.scdl.service.MediaScannerService;
import roboguice.receiver.RoboBroadcastReceiver;
import roboguice.util.Ln;
import roboguice.util.RoboAsyncTask;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.google.inject.Inject;

/**
 * Broadcast receiver reacting on download finished events.
 * 
 * @author pascal
 * 
 */
public class DownloadCompleteReceiver extends RoboBroadcastReceiver {

	@Inject
	private DownloadManager mDownloadManager;

	@Inject
	private NotificationManager mNotificationManager;

	/**
	 * Simple POJO for passing around download information.
	 * 
	 * @author pascal
	 * 
	 */
	private class Download {
		private String mTitle;
		private String mPath;
		private int mStatus;

		public String getTitle() {
			return mTitle;
		}

		public void setTitle(String title) {
			mTitle = title;
		}

		/**
		 * This is not used right now, but should be to customize the
		 * notification in case of an error.
		 * 
		 * @return
		 */
		@SuppressWarnings("unused")
		public int getStatus() {
			return mStatus;
		}

		public void setStatus(int status) {
			mStatus = status;
		}

		public String getPath() {
			return mPath;
		}

		public void setPath(String path) {
			mPath = path;
		}
	}

	@Override
	public void handleReceive(Context context, Intent intent) {
		final long downloadId = intent.getLongExtra(
				DownloadManager.EXTRA_DOWNLOAD_ID, 0);
		final ResolveDownloadTask task = new ResolveDownloadTask(context,
				downloadId);
		task.execute();
	}

	/**
	 * @param context
	 * @param title
	 */
	private void showNotification(Context context, final String title) {
		final Intent downloadIntent = new Intent(
				DownloadManager.ACTION_VIEW_DOWNLOADS);

		@SuppressWarnings("deprecation")
		final Notification notification = new Notification.Builder(context)
				.setAutoCancel(true)
				.setContentTitle(
						context.getString(R.string.notification_download_finished))
				.setContentText(title)
				.setTicker(
						String.format(
								context.getString(R.string.notification_download_finished_ticker),
								title))
				.setSmallIcon(android.R.drawable.stat_sys_download_done)
				.setContentIntent(
						PendingIntent
								.getActivity(context, 0, downloadIntent, 0))
				.getNotification();

		mNotificationManager.notify(0, notification);
	}

	private class ResolveDownloadTask extends RoboAsyncTask<Download> {
		private final long mDownloadId;

		public ResolveDownloadTask(Context context, long downloadId) {
			super(context);

			mDownloadId = downloadId;
		}

		@Override
		public Download call() throws Exception {
			final Query query = new DownloadManager.Query();
			query.setFilterById(mDownloadId);
			final Cursor cursor = mDownloadManager.query(query);

			try {
				if (!cursor.moveToFirst()) {
					// Download could not be found.
					Ln.d("Could not find download with id %d.", mDownloadId);
					return null;
				}

				final int descriptionIndex = cursor
						.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION);

				if (!cursor.getString(descriptionIndex).equals(
						context.getString(R.string.download_description))) {
					// Download doesn't belong to us. Weird way to check, but
					// way,
					// way
					// easier than keeping track of the IDs.
					Ln.d("Description did not match SCDL default description.");
					return null;
				}

				final int titleIndex = cursor
						.getColumnIndex(DownloadManager.COLUMN_TITLE);
				final String title = cursor.getString(titleIndex);

				final int statusIndex = cursor
						.getColumnIndex(DownloadManager.COLUMN_STATUS);
				final int status = cursor.getInt(statusIndex);

				final int localUriIndex = cursor
						.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
				final String downloadUri = cursor.getString(localUriIndex);

				final Download download = new Download();
				download.setTitle(title);
				download.setStatus(status);
				download.setPath(downloadUri);

				return download;
			} finally {
				cursor.close();
			}
		}

		@Override
		protected void onSuccess(Download t) throws Exception {
			super.onSuccess(t);
			
			if (shouldMoveFileToLocal(t)) {
				moveFileToLocal(t);
			}

			final Intent scanIntent = new Intent(context,
					MediaScannerService.class);
			scanIntent.putExtra(MediaScannerService.EXTRA_PATH, t.getPath());
			context.startService(scanIntent);

			showNotification(context, t.getTitle());
		}

		protected boolean shouldMoveFileToLocal(Download download) {
			return download.getPath().endsWith(Config.TMP_DOWNLOAD_POSTFIX);
		}

		protected void moveFileToLocal(Download download) {
			final File path = new File(download.getPath());
			final File newDir = context.getDir(path.getName(),
					Context.MODE_WORLD_READABLE);
			path.renameTo(new File(newDir, path.getName()));

		}

	}
}
