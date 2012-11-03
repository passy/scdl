package net.rdrei.android.scdl2.data;

import roboguice.content.RoboContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class DownloadProvider extends RoboContentProvider {

	private static final String TYPE_DOWNLOAD_ID = "vnd.android.cursor.item/vnd.net.rdrei.scdl.data.download";
	private static final String TYPE_DOWNLOADS = "vnd.android.cursor.dir/vnd.net.rdrei.scdl.data.download";
	private static final int DOWNLOADS = 0;
	private static final int DOWNLOAD_ID = 1;
	private static final UriMatcher URI_MATCHER;

	@Inject
	private DatabaseHelper mDatabase;

	@Inject
	private Provider<ContentResolver> mContentResolverProvider;

	static {
		URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
		URI_MATCHER.addURI(DownloadTable.AUTHORITY, "downloads", DOWNLOADS);
		URI_MATCHER.addURI(DownloadTable.AUTHORITY, "downloads/#", DOWNLOAD_ID);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		switch (URI_MATCHER.match(uri)) {
		case DOWNLOADS:
			return TYPE_DOWNLOADS;
		case DOWNLOAD_ID:
			return TYPE_DOWNLOAD_ID;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		if (URI_MATCHER.match(uri) != DOWNLOADS) {
			throw new IllegalArgumentException("Invalid insert URI " + uri);
		}

		if (values == null) {
			throw new NullPointerException("Values must not be null!");
		}

		final SQLiteDatabase db = mDatabase.getWritableDatabase();
		final long rowId = db.insert(DownloadTable.TABLE_NAME, null, values);

		if (rowId > 0) {
			final Uri newUri = ContentUris.withAppendedId(
					DownloadTable.BASE_URI, rowId);
			mContentResolverProvider.get().notifyChange(newUri, null);
			return newUri;
		}

		throw new IllegalStateException("Failed to insert row for " + uri);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		final SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(DownloadTable.TABLE_NAME);
		String[] whereArgs = null;

		switch (URI_MATCHER.match(uri)) {
		case DOWNLOADS:
			// All downloads.
			break;
		case DOWNLOAD_ID:
			queryBuilder.appendWhere(DownloadTable.Columns._ID + "=?");
			whereArgs = new String[] { uri.getLastPathSegment() };
			break;
		default:
			throw new IllegalArgumentException("Unhandled URI " + uri);
		}

		String orderBy;

		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = DownloadTable.Columns.DEFAULT_SORT_ORDER;
		} else {
			orderBy = sortOrder;
		}

		final SQLiteDatabase db = mDatabase.getReadableDatabase();
		final Cursor cursor = queryBuilder.query(db, projection, selection,
				whereArgs, null, null, orderBy);

		cursor.setNotificationUri(mContentResolverProvider.get(), uri);

		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
