package net.rdrei.android.scdl2.guice;

import android.app.DownloadManager;
import android.content.Context;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class DownloadManagerProvider implements Provider<DownloadManager> {

	@Inject
	Context mContext;

	@Override
	public DownloadManager get() {
		return (DownloadManager) mContext
				.getSystemService(Context.DOWNLOAD_SERVICE);
	}

}
