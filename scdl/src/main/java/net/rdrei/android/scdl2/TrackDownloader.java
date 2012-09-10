package net.rdrei.android.scdl2;

import java.io.IOException;

public interface TrackDownloader {

	int MSG_DOWNLOAD_ERROR = -1;
	int MSG_DOWNLOAD_STORAGE_ERROR = -2;
	String EXTRA_ERROR = "ERROR";

	/**
	 * Enqueues the job into the download manager.
	 * 
	 * @throws IOException
	 */
	void enqueue() throws IOException;

}