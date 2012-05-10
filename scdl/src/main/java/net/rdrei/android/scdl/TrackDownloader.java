package net.rdrei.android.scdl;

public interface TrackDownloader {

	/**
	 * Enqueues the job into the download manager.
	 */
	public abstract void enqueue();

}