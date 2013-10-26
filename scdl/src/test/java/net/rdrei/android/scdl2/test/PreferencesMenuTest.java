package net.rdrei.android.scdl2.test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import net.rdrei.android.scdl2.R;
import net.rdrei.android.scdl2.ui.ApplicationPreferencesActivity;
import net.rdrei.android.scdl2.ui.CommonMenuFragment;

import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;

import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.tester.android.view.TestMenuItem;
import org.robolectric.util.ActivityController;

@RunWith(RobolectricTestRunner.class)
public class PreferencesMenuTest {

	private void shouldStartPreferencesInActivity(Fragment fragment) {
        final ActivityController<FragmentActivity> controller = Robolectric.buildActivity(FragmentActivity.class);
        controller.create().start().resume();
        final FragmentActivity activity = controller.get();
        activity.getSupportFragmentManager().beginTransaction()
            .add(fragment, null)
            .commit();

		final MenuItem item = new TestMenuItem(R.id.preferences);
		fragment.onOptionsItemSelected(item);
		
		final ShadowActivity shadowActivity = Robolectric.shadowOf(activity);
		final Intent startedActivity = shadowActivity.getNextStartedActivity();

		// Intent was actually started
		assertThat(startedActivity, notNullValue());
		final ShadowIntent intent = Robolectric.shadowOf(startedActivity);

		assertThat(intent.getComponent().getClassName(),
				equalTo(ApplicationPreferencesActivity.class.getName()));
	}

	@Test
	public void testShouldStartPreferencesInCommonMenuFragment() {
		final Fragment fragment = new CommonMenuFragment();
		shouldStartPreferencesInActivity(fragment);
	}
}
