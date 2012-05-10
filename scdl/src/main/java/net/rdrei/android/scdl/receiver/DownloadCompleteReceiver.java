package net.rdrei.android.scdl.receiver;

import net.rdrei.android.scdl.R;
import roboguice.receiver.RoboBroadcastReceiver;
import roboguice.util.Ln;
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

	@Override
	public void handleReceive(Context context, Intent intent) {
		long downloadId = intent.getLongExtra(
				DownloadManager.EXTRA_DOWNLOAD_ID, 0);

		final Query query = new DownloadManager.Query();
		query.setFilterById(downloadId);
		final Cursor cursor = mDownloadManager.query(query);

		if (!cursor.moveToFirst()) {
			// Download could not be found.
			Ln.d("Could not find download with id %d.", downloadId);
			return;
		}

		final int descriptionIndex = cursor
				.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION);

		if (!cursor.getString(descriptionIndex).equals(
				context.getString(R.string.download_description))) {
			// Download doesn't belong to us. Weird way to check, but way, way
			// easier than keeping track of the IDs.
			Ln.d("Description did not match SCDL default description.");
			return;
		}

		final int titleIndex = cursor
				.getColumnIndex(DownloadManager.COLUMN_TITLE);
		final String title = cursor.getString(titleIndex);

		showNotification(context, title);
	}

	/**
	 * @param context
	 * @param title
	 */
	private void showNotification(Context context, final String title) {
		final Intent downloadIntent = new Intent(
				DownloadManager.ACTION_VIEW_DOWNLOADS);

		Notification notification = new Notification.Builder(context)
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
}
