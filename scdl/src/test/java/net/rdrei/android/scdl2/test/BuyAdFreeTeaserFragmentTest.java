package net.rdrei.android.scdl2.test;

import static org.junit.Assert.assertFalse;
import net.rdrei.android.mediator.DelayedMessageQueue;
import net.rdrei.android.mediator.MessageMediator.Receiver;
import net.rdrei.android.scdl2.R;
import net.rdrei.android.scdl2.ui.BuyAdFreeActivity;
import net.rdrei.android.scdl2.ui.BuyAdFreeTeaserFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import roboguice.activity.RoboFragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.widget.TextView;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.shadows.ShadowFragment;

@RunWith(TestRunner.class)
public class BuyAdFreeTeaserFragmentTest {
	private BuyAdFreeTeaserFragment mFragment;
	private DelayedMessageQueue mQueue = new DelayedMessageQueue();

	@Before
	public void setup() {
		final BuyAdFreeActivity activity = new BuyAdFreeActivity();
		activity.setMessageQueue(mQueue);
		
		FragmentManager fragmentManager = activity.getSupportFragmentManager();
		mFragment = BuyAdFreeTeaserFragment.newInstance("TEST");
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