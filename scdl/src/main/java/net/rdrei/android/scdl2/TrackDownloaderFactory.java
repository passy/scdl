package net.rdrei.android.scdl2;

import net.rdrei.android.scdl2.api.entity.TrackEntity;
import android.net.Uri;
import android.os.Handler;

public interface TrackDownloaderFactory {
	/**
	 * Creates a new instance of TrackDownloader.
	 * 
	 * @param mUri
	 *            The URI to download.
	 * @param mTrack
	 *            The track object associated with the download.
	 * @param handler
	 *            Optional. May send error messages, see {@link TrackDownloader}
	 *            .
	 */
	TrackDownloader create(Uri mUri, TrackEntity mTrack, Handler handler);
}
