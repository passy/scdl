package net.rdrei.android.scdl2.test;

import net.rdrei.android.scdl2.ui.BuyAdFreeActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(TestRunner.class)
public class BuyAdFreeActivityTest {
	
	@Test
	public void testSmokeOnCreate() {
		BuyAdFreeActivity activity = new BuyAdFreeActivity();
		// Don't blow up.
		activity.onCreate(null);
	}
}
