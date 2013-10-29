package net.rdrei.android.scdl2.test;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Provides;

import net.rdrei.android.scdl2.ApplicationPreferences;
import net.rdrei.android.scdl2.R;
import net.rdrei.android.scdl2.api.entity.TrackEntity;
import net.rdrei.android.scdl2.api.entity.UserEntity;
import net.rdrei.android.scdl2.guice.ActivityLayoutInflater;
import net.rdrei.android.scdl2.ui.AdViewManager;
import net.rdrei.android.scdl2.ui.SelectTrackActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowActivity;

import roboguice.inject.ContextSingleton;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.startsWith;
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

	@Test
	public void invalidPurchaseUrlRegression() {
		final TrackEntity track = new TrackEntity();
		final UserEntity user = new UserEntity();
		user.setUsername("awesomesauce");

		track.setDownloadable(false);
		track.setUser(user);
		track.setPurchaseUrl("Http://invalidstart.lol");

		final Bundle bundle = new Bundle();
		bundle.putParcelable(SelectTrackActivity.STATE_TRACK, track);

		final SelectTrackActivity activity = Robolectric.buildActivity(SelectTrackActivity.class)
				.attach()
				.create(bundle)
				.start()
				.resume()
				.get();

		// TODO: Fire off button click for download/purchase thing.
		activity.findViewById(R.id.btn_download).performClick();
		final ShadowActivity shadowActivity = Robolectric.shadowOf(activity);
		final Intent nextIntent = shadowActivity.getNextStartedActivity();
		assertThat(nextIntent.getAction(), equalTo("android.intent.action.VIEW"));
		assertThat(nextIntent.toUri(0), startsWith("http://"));
	}
}