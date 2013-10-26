package net.rdrei.android.scdl2.test;

import android.app.Activity;
import android.os.Environment;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.widget.EditText;

import com.google.analytics.tracking.android.Tracker;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;

import net.rdrei.android.scdl2.ApplicationPreferences;
import net.rdrei.android.scdl2.PreferenceManagerWrapper;
import net.rdrei.android.scdl2.ui.DownloadPreferencesDelegate;
import net.rdrei.android.scdl2.ui.DownloadPreferencesDelegateFactory;
import net.rdrei.android.scdl2.ui.DownloadPreferencesDelegateImpl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowDialogPreference;
import org.robolectric.shadows.ShadowEnvironment;

import java.io.File;
import java.util.HashMap;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@Config(shadows = {DownloadPreferencesDelegateTest.ShadowEnvironment2.class,
		DownloadPreferencesDelegateTest.ShadowEditTextPreference.class})
@RunWith(RobolectricTestRunner.class)
public class DownloadPreferencesDelegateTest {
	@Inject
	private DownloadPreferencesDelegateFactory mDelegateFactory;

	@Mock
	Tracker mTracker;

	private FakePreferenceManagerWrapperImpl mPreferenceManager;
	private DownloadPreferencesDelegate mDelegate;
	private Activity mActivity;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		mActivity = Robolectric.buildActivity(Activity.class).create().get();
		TestHelper.overridenInjector(this, new AbstractModule() {
			@Override
			protected void configure() {
				bind(Tracker.class).toInstance(mTracker);
			}
		});
		mPreferenceManager = new FakePreferenceManagerWrapperImpl();
		mPreferenceManager.preferences.put(ApplicationPreferences.KEY_STORAGE_CUSTOM_PATH, new EditTextPreference(mActivity));
		mPreferenceManager.preferences.put(ApplicationPreferences.KEY_STORAGE_TYPE, new ListPreference(mActivity));
		mDelegate = mDelegateFactory.create(mPreferenceManager);
	}

	@Test
	public void testSmokeFreeExternalStorage() {
		long storage = DownloadPreferencesDelegateImpl.getFreeExternalStorage();
		assertThat(storage, equalTo(0l));
	}

	@Test
	public void testPreferenceEditSetup() {
		mDelegate.onCreate(null);

		mPreferenceManager.preferences.get(ApplicationPreferences.KEY_STORAGE_TYPE);
	}

	private static class FakePreferenceManagerWrapperImpl implements PreferenceManagerWrapper {

		public HashMap<CharSequence, Preference> preferences;


		public FakePreferenceManagerWrapperImpl() {
			this.preferences = new HashMap<CharSequence, Preference>(2);
		}

		@Override
		public Preference findPreference(CharSequence key) {
			if (!preferences.containsKey(key)) {
				preferences.put(key, new ListPreference(null));
			}
			return preferences.get(key);
		}
	}

	@Implements(Environment.class)
	public static class ShadowEnvironment2 extends ShadowEnvironment {
		@Implementation
		public static File getDataDirectory() {
			return new File("/tmp");
		}
	}

	@Implements(EditTextPreference.class)
	public static class ShadowEditTextPreference extends ShadowDialogPreference {
		@Implementation
		public EditText getEditText() {
			return new EditText(getContext());
		}
	}
}