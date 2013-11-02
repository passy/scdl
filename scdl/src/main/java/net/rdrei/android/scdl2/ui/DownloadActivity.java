package net.rdrei.android.scdl2.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import net.rdrei.android.scdl2.R;
import net.rdrei.android.scdl2.api.MediaState;
import net.rdrei.android.scdl2.api.PendingDownload;

import roboguice.activity.RoboFragmentActivity;
import roboguice.util.Ln;

/**
 * Dispatches a download request to either the single download or playlist download activity.
 */
public class DownloadActivity extends RoboFragmentActivity {
	public static final String ANALYTICS_TAGS = "DOWNLOAD_ACTIVITY";
	public static final String MEDIA_STATE_TAG = "scdl:MEDIA_STATE_TAG";

	private MediaState mMediaState;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.download);

		if (savedInstanceState != null) {
			Ln.d("Loading previous media state.");
			// TODO: Load MediaState
		} else {
			CommonMenuFragment.injectMenu(this);
		}

		if (mMediaState == null) {
			Ln.d("No previous state. Starting media resolver.");
			final AbstractPendingDownloadResolver task = new PendingDownloadResolver(this);
			// TODO: Resolve MediaState
			task.execute();
		} else {
			loadMediaFragments();
		}
	}

	protected void loadMediaFragments() {
		Ln.w("loadMediaFragments: Not implemented.");
	}

	/**
	 * Show error activity with the given error code and exit the current
	 * activity.
	 *
	 * @param errorCode
	 */
	protected void startErrorActivity(
			final TrackErrorActivity.ErrorCode errorCode) {
		final Intent intent = new Intent(this, TrackErrorActivity.class);
		intent.putExtra(TrackErrorActivity.EXTRA_ERROR_CODE, errorCode);
		startActivity(intent);
		finish();
	}

	private class PendingDownloadResolver extends AbstractPendingDownloadResolver {
		public PendingDownloadResolver(Context context, Handler handler) {
			super(context, handler);
		}

		@Override
		protected void onErrorCode(TrackErrorActivity.ErrorCode errorCode) {
			startErrorActivity(errorCode);
		}

		@Override
		protected void onSuccess(PendingDownload download) throws Exception {
			// TODO: Call Track/Playlist resolver task.
		}
	}
}
