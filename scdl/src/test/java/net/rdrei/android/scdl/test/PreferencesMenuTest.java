package net.rdrei.android.scdl.test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import net.rdrei.android.scdl.R;
import net.rdrei.android.scdl.ui.ApplicationPreferencesActivity;
import net.rdrei.android.scdl.ui.MainActivity;
import net.rdrei.android.scdl.ui.SelectTrackActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.shadows.ShadowActivity;
import com.xtremelabs.robolectric.shadows.ShadowIntent;
import com.xtremelabs.robolectric.tester.android.view.TestMenuItem;

@RunWith(TestRunner.class)
public class PreferencesMenuTest {

	private void shouldStartPreferencesInActivity(Activity activity) {
		MenuItem item = new TestMenuItem(R.id.preferences);
		activity.onOptionsItemSelected(item);

		ShadowActivity shadowActivity = Robolectric.shadowOf(activity);
		Intent startedActivity = shadowActivity.getNextStartedActivity();

		// Intent was actually started
		assertThat(startedActivity, notNullValue());
		ShadowIntent intent = Robolectric.shadowOf(startedActivity);

		assertThat(intent.getComponent().getClassName(),
				equalTo(ApplicationPreferencesActivity.class.getName()));
	}

	@Test
	public void testShouldStartPreferencesInMainActivity() {
		FragmentActivity activity = new MainActivity();
		shouldStartPreferencesInActivity(activity);
	}

	@Test
	public void testShouldStartPreferencesInSelectTrackActivity() {
		Activity activity = new SelectTrackActivity();
		shouldStartPreferencesInActivity(activity);
	}
}
