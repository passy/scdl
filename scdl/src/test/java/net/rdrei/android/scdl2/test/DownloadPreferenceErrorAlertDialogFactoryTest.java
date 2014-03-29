package net.rdrei.android.scdl2.test;

import android.app.Activity;
import android.app.AlertDialog;

import com.google.inject.Inject;

import net.rdrei.android.scdl2.ui.DownloadPreferenceErrorAlertDialogFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowAlertDialog;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
public class DownloadPreferenceErrorAlertDialogFactoryTest {
	@Inject
	private DownloadPreferenceErrorAlertDialogFactory mFactory;

	@Before
	public void setUp() throws Exception {
		TestHelper.getInjector().injectMembers(this);
	}

	@Test
	public void testNewInstanceCreatesAResult() throws Exception {
		final AlertDialog alertDialog = mFactory.newInstance();
		assertThat(alertDialog).isNotNull();
	}

	@Test
	public void testDialogCanBeShown() throws Exception {
		final AlertDialog alertDialog = mFactory.newInstance();

		alertDialog.show();
		final AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
		final ShadowAlertDialog shadowAlertDialog = Robolectric.shadowOf(dialog);

		// Maybe too precise, but I want to have a clear indicator that the dialog was
		// actually shown.
		assertThat(shadowAlertDialog.getTitle()).isEqualTo("Your download failed :(");
	}
}
