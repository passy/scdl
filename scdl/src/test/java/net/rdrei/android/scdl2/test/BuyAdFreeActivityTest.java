package net.rdrei.android.scdl2.test;

import net.rdrei.android.scdl2.ui.BuyAdFreeActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.app.ActionBar;

import com.google.analytics.tracking.android.Tracker;
import com.google.inject.AbstractModule;

@RunWith(TestRunner.class)
public class BuyAdFreeActivityTest {
	@Before
	public void setup() {
		final ActionBar actionbarStub = new ActionBarStub();
		final Tracker trackerStub = new TrackerStub();
		
		TestRunner.overridenInjector(this, new AbstractModule() {
			
			@Override
			protected void configure() {
				bind(ActionBar.class).toInstance(actionbarStub);
				bind(Tracker.class).toInstance(trackerStub);
			}
		});
	}
	
	@Test
	public void testSmokeOnCreate() {
		BuyAdFreeActivity activity = new BuyAdFreeActivity();
		// Don't blow up.
		activity.onCreate(null);
	}
}
