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

import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.FragmentTestUtil;

@RunWith(RobolectricTestRunner.class)
public class BuyAdFreeTeaserFragmentTest {
	private BuyAdFreeTeaserFragment mFragment;

	@Before
	public void setup() {
		TestHelper.getInjector().injectMembers(this);

		mFragment = BuyAdFreeTeaserFragment.newInstance();
		FragmentTestUtil.startFragment(mFragment);
	}

	@Test
	public void testSpannedTextLoaded() {
		TextView text = (TextView) mFragment.getView().findViewById(
				R.id.buy_ad_free_teaser_text);

		assertFalse(text.getText().toString().isEmpty());
	}

	@Test
	public void testEnableButton() throws NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException {

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