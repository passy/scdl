package net.rdrei.android.scdl2.test;

import static org.hamcrest.Matchers.endsWith;
import static org.junit.Assert.assertThat;
import net.rdrei.android.scdl2.data.DownloadTable;

import org.junit.Test;
import org.junit.runner.RunWith;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

@RunWith(TestRunner.class)
public class DownloadProviderTest {
	
	@Test
	public void testInsert() {
		final Uri uri = DownloadTable.BASE_URI;
		
		final Context context = new Activity();
		final ContentResolver resolver = context.getContentResolver();
		final ContentValues values = new ContentValues();
		
		values.put(DownloadTable.Columns.DOWNLOAD_ID, 1);
		values.put(DownloadTable.Columns.SOUNDCLOUD_ID, 2L);
		values.put(DownloadTable.Columns.TITLE, "My Title");
		values.put(DownloadTable.Columns.STATUS, 3);
		
		final Uri insert = resolver.insert(uri, values);
		
		assertThat(insert.toString(), endsWith("downloads/0"));
		
		final Uri insert2 = resolver.insert(uri, values);
		
		assertThat(insert2.toString(), endsWith("downloads/1"));
	}
}
