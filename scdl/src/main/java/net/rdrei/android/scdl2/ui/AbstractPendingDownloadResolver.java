package net.rdrei.android.scdl2.ui;

import android.content.Context;
import android.os.Handler;

import com.google.inject.Inject;

import net.rdrei.android.scdl2.ShareIntentResolver;
import net.rdrei.android.scdl2.api.PendingDownload;

import roboguice.inject.ContextScope;
import roboguice.util.RoboAsyncTask;

/**
 * Resolves an Intent URL to a pending download.
 */
public abstract class AbstractPendingDownloadResolver extends RoboAsyncTask<PendingDownload> {
	@Inject
	private ShareIntentResolver mShareIntentResolver;

	@Inject
	private ContextScope mContextScope;

	public AbstractPendingDownloadResolver(final Context context) {
		super(context);
	}

	@Override
	public PendingDownload call() throws Exception {
		mContextScope.enter(getContext());
		try {
			return mShareIntentResolver.resolvePendingDownload();
		} finally {
			mContextScope.exit(getContext());
		}
	}

	@Override
	protected void onException(final Exception e) throws RuntimeException {
		super.onException(e);
		final TrackErrorActivity.ErrorCode errorCode;

		if (e instanceof ShareIntentResolver.UnsupportedUrlException) {
			errorCode = TrackErrorActivity.ErrorCode.UNSUPPORTED_URL;
		} else if (e instanceof ShareIntentResolver.TrackNotFoundException) {
			errorCode = TrackErrorActivity.ErrorCode.NOT_FOUND;
		} else if (e instanceof ShareIntentResolver.UnsupportedPlaylistUrlException) {
			errorCode = TrackErrorActivity.ErrorCode.PLAYLIST;
		} else {
			errorCode = TrackErrorActivity.ErrorCode.NETWORK_ERROR;
		}

		onErrorCode(errorCode);
	}

	protected abstract void onErrorCode(final TrackErrorActivity.ErrorCode errorCode);

	@Override
	protected abstract void onSuccess(final PendingDownload download) throws Exception;
}
