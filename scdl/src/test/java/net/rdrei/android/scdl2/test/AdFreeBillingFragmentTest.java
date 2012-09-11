package net.rdrei.android.scdl2.test;

import net.rdrei.android.mediator.DelayedMessageQueue;
import net.rdrei.android.scdl2.ui.AdFreeBillingFragment;
import net.rdrei.android.scdl2.ui.BuyAdFreeActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.shadows.ShadowFragment;

@RunWith(TestRunner.class)
public class AdFreeBillingFragmentTest {
	
	@Test
	public void testSetupSmoke() {
		BuyAdFreeActivity activity = new BuyAdFreeActivity();
		activity.setMessageQueue(new DelayedMessageQueue());
		
		AdFreeBillingFragment fragment = AdFreeBillingFragment.newInstance("TEST");
		ShadowFragment shadowFragment = Robolectric.shadowOf(fragment);
		shadowFragment.setActivity(activity);
		
		fragment.onAttach(activity);
		fragment.onActivityCreated(null);
	}
}
