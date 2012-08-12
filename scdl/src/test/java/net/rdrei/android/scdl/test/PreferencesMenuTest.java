package net.rdrei.android.scdl.test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import net.rdrei.android.scdl.R;
import net.rdrei.android.scdl.ui.ApplicationPreferencesActivity;
import net.rdrei.android.scdl.ui.CommonMenuFragment;

import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.shadows.ShadowFragment;
import com.xtremelabs.robolectric.shadows.ShadowFragmentActivity;
import com.xtremelabs.robolectric.shadows.ShadowIntent;
import com.xtremelabs.robolectric.tester.android.view.TestMenuItem;

@RunWith(TestRunner.class)
public class PreferencesMenuTest {

	private void shouldStartPreferencesInActivity(Fragment fragment) {
		final FragmentActivity activity = new FragmentActivity();
		final ShadowFragment shadowFragment = Robolectric.shadowOf(fragment);
		shadowFragment.setActivity(activity);
		
		final MenuItem item = new TestMenuItem(R.id.preferences);
		fragment.onOptionsItemSelected(item);
		
		final ShadowFragmentActivity shadowActivity = Robolectric.shadowOf(activity);
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
