package net.rdrei.android.scdl2.test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import net.rdrei.android.scdl2.ui.ApplicationPreferencesActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowPreferenceActivity;

@RunWith(RobolectricTestRunner.class)
public class ApplicationPreferencesActivityTest {

	@Test
	public void testLaunch() {
		ApplicationPreferencesActivity activity = new ApplicationPreferencesActivity();
		ShadowPreferenceActivity shadowActivity = Robolectric
				.shadowOf(activity);

		// So far, the activity itself doesn't load a preference resource
		// itself, but only a header.
		assertThat(shadowActivity.getPreferencesResId(), is(-1));
	}
}
