package net.rdrei.android.scdl2.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Field;

import net.rdrei.android.scdl2.R;
import net.rdrei.android.scdl2.ui.BuyAdFreeActivity;
import net.rdrei.android.scdl2.ui.BuyAdFreeTeaserFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.v4.app.FragmentManager;
import android.widget.Button;
import android.widget.TextView;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.shadows.ShadowFragment;

@RunWith(TestRunner.class)
public class BuyAdFreeTeaserFragmentTest {
	private BuyAdFreeTeaserFragment mFragment;

	@Before
	public void setup() {
		TestRunner.getInjector().injectMembers(this);

		final BuyAdFreeActivity activity = new BuyAdFreeActivity();

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

	@Test
	public void testEnableButton() throws NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException {
		mFragment.onActivityCreated(null);

		// Evil, but I really don't have any idea how to get that button other
		// than this.
		Field buttonField = mFragment.getClass().getDeclaredField(
				"mButton");
		buttonField.setAccessible(true);
		Button button;
		button = (Button) buttonField.get(mFragment);
				
		assertThat(button.isEnabled(), is(false));
	}
}