package net.rdrei.android.scdl2.test;


import android.app.Activity;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Provides;

import net.rdrei.android.scdl2.ApplicationPreferences;
import net.rdrei.android.scdl2.guice.ActivityLayoutInflater;
import net.rdrei.android.scdl2.ui.AdViewManager;
import net.rdrei.android.scdl2.ui.SelectTrackActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import roboguice.inject.ContextSingleton;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class SelectTrackActivityTest {
	@Before
	public void setUp() {
		final AbstractModule module = new AbstractModule() {
			@Override
			protected void configure() {
				final Injector injector = TestHelper.getInjector();

				final AdViewManagerStub adViewManagerStub = new AdViewManagerStub(
						injector.getProvider(ApplicationPreferences.class),
						new ActivityLayoutInflater(Robolectric.buildActivity(Activity.class).create().get()));

				this.bind(AdViewManager.class).toInstance(adViewManagerStub);
			}
		};

		TestHelper.overridenInjector(this, module);
	}

	@Test
	public void smokeTestOnCreate() {
		final SelectTrackActivity activity = Robolectric.buildActivity(SelectTrackActivity.class)
				.attach()
				.create()
				.get();
		assertThat(activity, notNullValue());
	}
}