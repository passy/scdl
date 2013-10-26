package net.rdrei.android.scdl2.test;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;

import net.rdrei.android.scdl2.ApplicationPreferences;
import net.rdrei.android.scdl2.guice.ActivityLayoutInflater;
import net.rdrei.android.scdl2.ui.AdViewManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import roboguice.inject.ContextScope;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class AdViewManagerTest {

	private boolean mAdFree = false;

	@Inject
	private AdViewManager mManager;

	private Activity mActivity;

	@Before
	public void setUp() {
		mActivity = Robolectric.buildActivity(Activity.class).create().get();

		final ApplicationPreferences preferences = new ApplicationPreferences() {
			@Override
			public boolean isAdFree() {
				return mAdFree;
			}
		};

		AbstractModule module = new AbstractModule() {
			@Override
			protected void configure() {
				bind(ApplicationPreferences.class).toInstance(preferences);
				bind(ActivityLayoutInflater.class).toInstance(
						new TestLayoutInflater(mActivity));
			}
		};
		
		final ContextScope contextScope = new ContextScope(Robolectric.application);

		contextScope.enter(mActivity);
		try {
			TestHelper.overridenInjector(this, module);
		} finally {
			contextScope.exit(mActivity);
		}
	}

	@Test
	public void doesntInjectWithAdFree() {
		mAdFree = true;
		assertThat(mManager.addToViewIfRequired(null), is(false));
	}

	@Test
	public void doesInjectWithoutAdFree() {
		mAdFree = false;
		final LinearLayout layout = new LinearLayout(mActivity);
		assertThat(mManager.addToViewIfRequired(layout), is(true));
	}

	private class TestLayoutInflater extends ActivityLayoutInflater {
		public TestLayoutInflater(Activity activity) {
			super(activity);
		}

		@Override
		public View inflate(int resource, ViewGroup root, boolean attachToRoot) {
			return new View(mActivity);
		}
	}
}
