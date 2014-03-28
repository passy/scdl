package net.rdrei.android.scdl2.test;

import android.app.AlertDialog;

import com.google.inject.Inject;

import net.rdrei.android.scdl2.ui.DownloadPreferenceErrorAlertDialogFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class DownloadPreferenceErrorAlertDialogFactoryTest {
	@Inject
	private DownloadPreferenceErrorAlertDialogFactory mFactory;

	@Before
	public void setUp() throws Exception {

	}

	@Test
	public void testNewInstance() throws Exception {
		final AlertDialog alertDialog = mFactory.newInstance();
		assert(alertDialog != null);
	}
}
