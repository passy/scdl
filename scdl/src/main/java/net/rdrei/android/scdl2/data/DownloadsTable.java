package net.rdrei.android.scdl2.data;

import android.provider.BaseColumns;

/**
 * Metadata and access definitions for the downloads table.
 * 
 * @author pascal
 */
public class DownloadsTable {
	
	public static final String TABLE_NAME = "downloads";
	
	public interface Columns extends BaseColumns {
		String TITLE = "title";
		String UPDATED = "updated";
		String STATUS = "status";
		String SOUNDCLOUD_ID = "soundcloud_id";
		String DOWNLOAD_ID = "download_id";
	}
}
