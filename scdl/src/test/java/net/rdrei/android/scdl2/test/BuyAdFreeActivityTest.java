package net.rdrei.android.scdl2.test;

import net.rdrei.android.scdl2.ui.BuyAdFreeActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import android.app.ActionBar;

import com.google.analytics.tracking.android.Tracker;
import com.google.inject.AbstractModule;

@RunWith(TestRunner.class)
public class BuyAdFreeActivityTest {
	@Mock
	Tracker mTracker;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		final ActionBar actionbarStub = new ActionBarStub();
		
		TestRunner.overridenInjector(this, new AbstractModule() {
			
			@Override
			protected void configure() {
				bind(ActionBar.class).toInstance(actionbarStub);
				// Don't actually initiate the tracker here.
				bind(Tracker.class).toInstance(mTracker);
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
