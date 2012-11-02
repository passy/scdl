package net.rdrei.android.scdl2.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Metadata and access definitions for the downloads table.
 * 
 * @author pascal
 */
public class DownloadTable {

	/**
	 * Table name used to store the data.
	 */
	public static final String TABLE_NAME = "download";
	/**
	 * Authority used for the content provider.
	 */
	public static final String AUTHORITY = "net.rdrei.android.scdl2.data.download";
	/**
	 * URI to retrieve all downloads saved.
	 */
	public static final Uri BASE_URI = Uri
			.parse("content://net.rdrei.android.scdl2.data.download/downloads");

	public static final String[] DOWNLOADS_PROJECTION = {
		Columns._ID,
		Columns.TITLE,
		Columns.UPDATED,
		Columns.STATUS,
		Columns.SOUNDCLOUD_ID,
		Columns.DOWNLOAD_ID
	};

	public interface Columns extends BaseColumns {
		String TITLE = "title";
		String UPDATED = "updated";
		String STATUS = "status";
		String SOUNDCLOUD_ID = "soundcloud_id";
		String DOWNLOAD_ID = "download_id";
	}
}
