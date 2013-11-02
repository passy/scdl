package net.rdrei.android.scdl2.ui;

import android.content.Context;

import com.google.inject.Inject;

import net.rdrei.android.scdl2.api.APIException;
import net.rdrei.android.scdl2.api.MediaState;
import net.rdrei.android.scdl2.api.PendingDownload;
import net.rdrei.android.scdl2.api.ServiceManager;
import net.rdrei.android.scdl2.api.service.PlaylistService;
import net.rdrei.android.scdl2.api.service.TrackService;

import roboguice.util.RoboAsyncTask;

public abstract class AbstractMediaStateLoaderTask extends RoboAsyncTask<MediaState> {
	protected final PendingDownload mPendingDownload;

	@Inject
	private ServiceManager mServiceManager;

	@Inject
	private roboguice.inject.ContextScope mContextScope;

	public AbstractMediaStateLoaderTask(Context context, final PendingDownload download) {
		super(context);
		mPendingDownload = download;
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
