package net.rdrei.android.scdl;

import net.rdrei.android.scdl.api.entity.TrackEntity;
import android.net.Uri;

public interface TrackDownloaderFactory {
	TrackDownloader create(Uri mUri, TrackEntity mTrack);
}
