package net.rdrei.android.scdl2.receiver;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import net.rdrei.android.scdl2.ApplicationPreferences;
import net.rdrei.android.scdl2.Config;
import net.rdrei.android.scdl2.IOUtil;
import net.rdrei.android.scdl2.R;
import net.rdrei.android.scdl2.guice.TrackerProvider;
import net.rdrei.android.scdl2.service.MediaScannerService;
import net.rdrei.android.scdl2.ui.DownloadPreferencesActivity;
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
import android.support.v4.app.NotificationCompat;

import com.bugsense.trace.BugSenseHandler;
import com.google.inject.Inject;

/**
 * Broadcast receiver reacting on download finished events.
 *
 * @author pascal
 *
 */
public class DownloadCompleteReceiver extends RoboBroadcastReceiver {

	private static final int HTTP_ERROR_FORBIDDEN = 403;

	private static final String ANALYTICS_TAG = "DOWNLOAD_COMPLETED_RECEIVER";

	@Inject
	private DownloadManager mDownloadManager;

	@Inject
	private NotificationManager mNotificationManager;

	@Inject
	private TrackerProvider mTrackerProvider;

	/**
	 * Simple POJO for passing around download information.
	 *
	 * @author pascal
	 *
	 */
	public static class Download {
		private String mTitle;
		private String mPath;
		private int mStatus;
		private int mReason;

		public String getTitle() {
			return mTitle;
		}

		public void setTitle(final String title) {
			mTitle = title;
		}

		/**
		 * This is not used right now, but should be to customize the
		 * notification in case of an error.
		 *
		 * @return
		 */
		public int getStatus() {
			return mStatus;
		}

		public void setStatus(final int status) {
			mStatus = status;
		}

		public String getPath() {
			return mPath;
		}

		public void setPath(final String path) {
			mPath = path;
		}

		public int getReason() {
			return mReason;
		}

		public void setReason(final int reason) {
			mReason = reason;
		}

		/**
		 * Returns the normalized path to the downloaded file, i.e. without
		 * leading protocol specifier.
		 *
		 * @return String
		 */
		public String getNormalizedPath() {
			if (mPath == null) {
				throw new NullPointerException(
						"Path wasn't set when requesting normalizedPath.");
			}

			try {
				return new File(new URI(mPath)).getAbsolutePath();
			} catch (final URISyntaxException e) {
				throw new RuntimeException(
						"Parsing path URI from DownloadManager failed horribly.",
						e);
			}
		}
	}

	@Override
	public void handleReceive(final Context context, final Intent intent) {
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
	private void showSuccessNotification(final Context context,
			final String title) {
		final Intent downloadIntent = new Intent(
				DownloadManager.ACTION_VIEW_DOWNLOADS);

		@SuppressWarnings("deprecation")
		final Notification notification = new NotificationCompat.Builder(
				context)
				.setAutoCancel(true)
				.setContentTitle(
						context.getString(R.string.notification_download_finished))
				.setContentText(title)
				.setTicker(
						context.getString(
								R.string.notification_download_finished_ticker,
								title))
				.setSmallIcon(android.R.drawable.stat_sys_download_done)
				.setContentIntent(
						PendingIntent
								.getActivity(context, 0, downloadIntent, 0))
				.getNotification();

		mNotificationManager.notify(0, notification);
	}

	/**
	 * Create a notification indicating a download error.
	 *
	 * @param context
	 * @param reason
	 *            The error code provided by {@link DownloadManager}
	 */
	private void showErrorNotification(final Context context, final int reason,
			final String title) {
		final String errorMessage = getDownloadErrorMessage(context, reason);
		final Intent preferencesIntent = new Intent(context,
				DownloadPreferencesActivity.class);
		preferencesIntent.putExtra(
				DownloadPreferencesActivity.EXTRA_DOWNLOAD_ERROR, reason);

		@SuppressWarnings("deprecation")
		final Notification notification = new NotificationCompat.Builder(context)
				.setAutoCancel(true)
				.setContentTitle(
						context.getString(
								R.string.notification_download_failed, title))
				.setContentText(errorMessage)
				.setTicker(
						context.getString(
								R.string.notification_download_failed_ticker,
								title))
				.setSmallIcon(android.R.drawable.stat_notify_error)
				.setContentIntent(
						PendingIntent.getActivity(context, 0,
								preferencesIntent, 0)).getNotification();

		mNotificationManager.notify(0, notification);
		mTrackerProvider.get().sendEvent(ANALYTICS_TAG, "error",
				String.format("code:%d", reason), null);
	}

	private String getDownloadErrorMessage(final Context context,
			final int reason) {
		final int messageId;

		switch (reason) {
		case DownloadManager.ERROR_DEVICE_NOT_FOUND:
			messageId = R.string.error_cant_write;
			break;
		case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
			messageId = R.string.error_already_exists;
			break;
		case DownloadManager.ERROR_INSUFFICIENT_SPACE:
			messageId = R.string.error_insufficient_space;
			break;
		case HTTP_ERROR_FORBIDDEN:
			messageId = R.string.error_download_expired;
			break;
		default:
			messageId = R.string.error_unknown;
		}

		return context.getString(messageId, reason);
	}

	private class ResolveDownloadTask extends RoboAsyncTask<Download> {
		private final long mDownloadId;

		public ResolveDownloadTask(final Context context, final long downloadId) {
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
					// way, way easier than keeping track of the IDs.
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

				final int reasonIndex = cursor
						.getColumnIndex(DownloadManager.COLUMN_REASON);
				final int reason = cursor.getInt(reasonIndex);

				final Download download = new Download();
				download.setTitle(title);
				download.setStatus(status);
				download.setPath(downloadUri);
				download.setReason(reason);

				return download;
			} finally {
				cursor.close();
			}
		}

		@Override
		protected void onSuccess(final Download t) throws Exception {
			super.onSuccess(t);

			if (t == null) {
				BugSenseHandler.sendException(new NullPointerException(
						"Received null-pointer in "
								+ "DownloadCompleteReceiver.onSuccess()"));
				return;
			}

			if (t.getStatus() == DownloadManager.STATUS_FAILED) {
				Ln.e("Download of '%s' failed with reason %d", t.getTitle(),
						t.getReason());
				showErrorNotification(context, t.getReason(), t.getTitle());
				return;
			}

			if (shouldMoveFileToLocal(t)) {
				Ln.d("Moving temporary file to local location.");
				moveFileToLocal(t);
			}

			final Intent scanIntent = new Intent(context,
					MediaScannerService.class);
			scanIntent.putExtra(MediaScannerService.EXTRA_PATH,
					t.getNormalizedPath());
			context.startService(scanIntent);

			showSuccessNotification(context, t.getTitle());
		}

		protected boolean shouldMoveFileToLocal(final Download download) {
			return download.getPath().endsWith(Config.TMP_DOWNLOAD_POSTFIX);
		}

		/**
		 * Moves a download to a local location and removes the temporary path
		 * suffix.
		 *
		 * @param download
		 */
		protected void moveFileToLocal(final Download download) {
			final File path = new File(download.getNormalizedPath());
			final String filename = path.getName();
			@SuppressWarnings("deprecation")
			final File newDir = context.getDir(
					ApplicationPreferences.DEFAULT_STORAGE_DIRECTORY,
					Context.MODE_WORLD_READABLE);

			final String newFileName = path.getName().substring(0,
					filename.length() - Config.TMP_DOWNLOAD_POSTFIX.length());

			final File newPath = new File(newDir, newFileName);
			try {
				IOUtil.copyFile(path, newPath);
			} catch (final IOException err) {
				Ln.w(err, "Failed to rename download.");
				BugSenseHandler.sendException(err);
				return;
			}

			Ln.d("Download moved to %s", newPath.toString());
			path.delete();
			download.setPath(newPath.getAbsolutePath());
		}
	}
}
