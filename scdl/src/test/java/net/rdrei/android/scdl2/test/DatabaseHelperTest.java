package net.rdrei.android.scdl2.test;

import static org.junit.Assert.assertNotNull;
import net.rdrei.android.scdl2.data.DatabaseHelper;

import org.junit.Test;
import org.junit.runner.RunWith;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

@RunWith(TestRunner.class)
public class DatabaseHelperTest {

	@Test
	public void testCreation() {
		final Context context = new Activity();
		DatabaseHelper dbh = new DatabaseHelper(context);
		
		SQLiteDatabase db = dbh.getWritableDatabase();
		assertNotNull(db);
	}
	
}
