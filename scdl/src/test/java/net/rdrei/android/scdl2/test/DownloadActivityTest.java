package net.rdrei.android.scdl2.test;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;

import net.rdrei.android.scdl2.ApplicationPreferences;
import net.rdrei.android.scdl2.R;
import net.rdrei.android.scdl2.api.MediaState;
import net.rdrei.android.scdl2.api.entity.TrackEntity;
import net.rdrei.android.scdl2.api.entity.UserEntity;
import net.rdrei.android.scdl2.guice.ActivityLayoutInflater;
import net.rdrei.android.scdl2.ui.AdViewManager;
import net.rdrei.android.scdl2.ui.DownloadActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowActivity;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class DownloadActivityTest {
	@Before
	public void setUp() {
		final AbstractModule module = new AbstractModule() {
			@Override
			protected void configure() {
				final Injector injector = TestHelper.getInjector();

				final AdViewManagerStub adViewManagerStub = new AdViewManagerStub(
						injector.getProvider(ApplicationPreferences.class),
						new ActivityLayoutInflater(
								Robolectric.buildActivity(Activity.class).create().get()));

				this.bind(AdViewManager.class).toInstance(adViewManagerStub);
			}
		};

		TestHelper.overridenInjector(this, module);
	}

	@Test
	public void smokeTestOnCreate() {
		final DownloadActivity activity = Robolectric.buildActivity(DownloadActivity.class)
				.attach()
				.create()
				.get();
		assertThat(activity, notNullValue());
	}

	@Test
	public void purchaseFlow() {
		final TrackEntity track = new TrackEntity();
		final UserEntity user = new UserEntity();
		final String downloadUrl = "http://3lau.to/downloadstuff";
		user.setUsername("awesomesauce");

		track.setDownloadable(false);
		track.setUser(user);
		track.setPurchaseUrl(downloadUrl);



		final Bundle bundle = new Bundle();
		bundle.putParcelable(DownloadActivity.MEDIA_STATE_TAG, MediaState.fromEntity(track));

		final DownloadActivity activity = Robolectric.buildActivity(DownloadActivity.class)
				.attach()
				.create(bundle)
				.start()
				.resume()
				.get();

		final View button = activity.findViewById(R.id.btn_download);
		assertTrue(button.isEnabled());
		button.performClick();
		final ShadowActivity shadowActivity = Robolectric.shadowOf(activity);
		final Intent nextIntent = shadowActivity.getNextStartedActivity();
		assertThat(nextIntent.getAction(), equalTo("android.intent.action.VIEW"));
		assertThat(nextIntent.toUri(0), startsWith(downloadUrl));
	}

	@Test
	public void invalidPurchaseRegression() {
		final TrackEntity track = new TrackEntity();
		final UserEntity user = new UserEntity();
		// Notice capital H here.
		final String downloadUrl = "Http://not-a-link";
		user.setUsername("awesomesauce");

		track.setDownloadable(false);
		track.setUser(user);
		track.setPurchaseUrl(downloadUrl);

		final Bundle bundle = new Bundle();
		bundle.putParcelable(DownloadActivity.MEDIA_STATE_TAG, MediaState.fromEntity(track));

		final DownloadActivity activity = Robolectric.buildActivity(DownloadActivity.class)
				.attach()
				.create(bundle)
				.start()
				.resume()
				.get();

		final View button = activity.findViewById(R.id.btn_download);
		assertFalse(button.isEnabled());
	}
}