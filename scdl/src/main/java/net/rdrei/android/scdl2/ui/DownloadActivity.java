package net.rdrei.android.scdl2.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.inject.Inject;

import net.rdrei.android.scdl2.R;
import net.rdrei.android.scdl2.api.APIException;
import net.rdrei.android.scdl2.api.MediaDownloadType;
import net.rdrei.android.scdl2.api.MediaState;
import net.rdrei.android.scdl2.api.PendingDownload;
import net.rdrei.android.scdl2.api.ServiceManager;
import net.rdrei.android.scdl2.api.entity.TrackEntity;
import net.rdrei.android.scdl2.api.service.PlaylistService;
import net.rdrei.android.scdl2.api.service.TrackService;

import roboguice.activity.RoboFragmentActivity;
import roboguice.util.Ln;
import roboguice.util.RoboAsyncTask;

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
		} else {
			CommonMenuFragment.injectMenu(this);
		}

		if (mMediaState == null) {
			Ln.d("No previous state. Starting media resolver.");
			final AbstractPendingDownloadResolver task = new PendingDownloadResolver(this);
			task.execute();
		} else {
			loadMediaFragments();
		}
	}

	protected void loadMediaFragments() {
		Ln.w("loadMediaFragments: Not implemented.");
		Ln.i("But we got a state now: %s", mMediaState);
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
		public PendingDownloadResolver(Context context) {
			super(context);
		}

		@Override
		protected void onErrorCode(TrackErrorActivity.ErrorCode errorCode) {
			startErrorActivity(errorCode);
		}

		@Override
		protected void onSuccess(PendingDownload download) throws Exception {
			final MediaStateLoaderTask task = new MediaStateLoaderTask(getContext(), download);
			task.execute();
		}
	}

	private class MediaStateLoaderTask extends RoboAsyncTask<MediaState> {
		private final PendingDownload mPendingDownload;

		@Inject
		private ServiceManager mServiceManager;

		@Inject
		private roboguice.inject.ContextScope mContextScope;

		protected MediaStateLoaderTask(final Context context, final PendingDownload download) {
			super(context);
			assert download.getType() == MediaDownloadType.TRACK;
			mPendingDownload = download;
		}

		@Override
		protected void onException(final Exception e) throws RuntimeException {
			super.onException(e);
			Ln.e("Error while resolving track: %s", e.toString());

			Toast.makeText(getContext(), "ERROR: " + e.toString(), Toast.LENGTH_LONG).show();
		}

		@Override
		protected void onSuccess(final MediaState state) throws Exception {
			super.onSuccess(state);

			mMediaState = state;
			loadMediaFragments();
		}

		@Override
		public MediaState call() throws Exception {
			mContextScope.enter(context);

			try {
				return resolveDownloadToMedia();
			} finally {
				mContextScope.exit(context);
			}
		}

		private MediaState resolveDownloadToMedia() throws APIException {
			switch (mPendingDownload.getType()) {
				case PLAYLIST:
					final PlaylistService playlistService = mServiceManager.playlistService();
					return MediaState.fromEntity(
							playlistService.getPlaylist(mPendingDownload.getId()));
				case TRACK:
					final TrackService trackService = mServiceManager.trackService();
					return MediaState.fromEntity(trackService.getTrack(mPendingDownload.getId()));
				default:
					throw new IllegalStateException("Unknown PendingDownload type. WTF?");
			}
		}
	}
}
