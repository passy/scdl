package net.rdrei.android.scdl2.test;

import android.content.pm.PackageInfo;
import android.widget.TextView;

import com.google.inject.AbstractModule;

import net.rdrei.android.scdl2.R;
import net.rdrei.android.scdl2.ui.AboutActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.fest.assertions.api.ANDROID.assertThat;

@RunWith(RobolectricTestRunner.class)
public class AboutActivityTest {
	@Before
	public void setup() {
		final PackageInfo fakeInfo = new PackageInfo();
		fakeInfo.versionName = "1.0.1";

		TestHelper.overridenInjector(this, new AbstractModule() {
			@Override
			protected void configure() {
				bind(PackageInfo.class).toInstance(fakeInfo);
			}
		});
	}

	@Test
	public void testSmokeOnCreate() {
		// Don't blow up.
		Robolectric.buildActivity(AboutActivity.class).create();
	}

	@Test
	public void testVersionString() {
		final AboutActivity activity = Robolectric.buildActivity(AboutActivity.class)
				.attach()
				.create()
				.start()
				.resume()
				.get();

		final TextView view = (TextView) activity.findViewById(R.id.version_number);
		assertThat(view).hasText("1.0.1");
	}
}
