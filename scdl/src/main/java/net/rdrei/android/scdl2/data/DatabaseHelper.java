package net.rdrei.android.scdl2.data;

import roboguice.util.Ln;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	/**
	 * Current version of the database. Used to trigger upgrades.
	 */
	private static final int DATABASE_VERSION = 1;
	/**
	 * Local file name of the database.
	 */
	public static final String DATABASE_NAME = "scdl";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Ln.d("Creating new database.");
		
		db.execSQL("CREATE TABLE " + DownloadsTable.TABLE_NAME + " ("
				+ DownloadsTable.Columns._ID + " INTEGER PRIMARY KEY, "
				+ DownloadsTable.Columns.SOUNDCLOUD_ID + " INTEGER NOT NULL, "
				+ DownloadsTable.Columns.DOWNLOAD_ID + " INTEGER NOT NULL, "
				+ DownloadsTable.Columns.STATUS + " INTEGER NOT NULL, "
				+ DownloadsTable.Columns.TITLE + " TEXT, "
				+ DownloadsTable.Columns.UPDATED + " INTEGER NOT NULL" +
				")");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		throw new UnsupportedOperationException(
				"There's no need to upgrade, yet.");
	}
}
