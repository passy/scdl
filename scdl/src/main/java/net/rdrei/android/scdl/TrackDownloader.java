package net.rdrei.android.scdl;

import java.io.IOException;

public interface TrackDownloader {
	
	public static final int MSG_DOWNLOAD_ERROR = -1;
	public static final int MSG_DOWNLOAD_STORAGE_ERROR = -2;
	public static final String EXTRA_ERROR = "ERROR";

	/**
	 * Enqueues the job into the download manager.
	 * @throws IOException 
	 */
	void enqueue() throws IOException;

}