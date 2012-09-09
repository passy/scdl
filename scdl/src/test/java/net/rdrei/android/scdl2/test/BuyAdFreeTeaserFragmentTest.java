package net.rdrei.android.scdl2.test;

import static org.junit.Assert.assertFalse;
import net.rdrei.android.scdl2.R;
import net.rdrei.android.scdl2.ui.BuyAdFreeTeaserFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import roboguice.activity.RoboFragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.TextView;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.shadows.ShadowFragment;

@RunWith(TestRunner.class)
public class BuyAdFreeTeaserFragmentTest {
	private BuyAdFreeTeaserFragment mFragment;

	@Before
	public void setup() {
		RoboFragmentActivity activity = new RoboFragmentActivity();

		FragmentManager fragmentManager = activity.getSupportFragmentManager();
		mFragment = BuyAdFreeTeaserFragment.newInstance();
		fragmentManager.beginTransaction().add(mFragment, "TEST").commit();
	}

	@Test
	public void testSpannedTextLoaded() {
		ShadowFragment fragment = Robolectric.shadowOf(mFragment);
		TextView text = (TextView) fragment.getView().findViewById(
				R.id.buy_ad_free_teaser_text);

		assertFalse(text.getText().toString().isEmpty());
	}
}
