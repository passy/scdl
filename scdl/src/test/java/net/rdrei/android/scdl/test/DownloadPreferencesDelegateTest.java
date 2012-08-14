package net.rdrei.android.scdl.test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.HashMap;

import net.rdrei.android.scdl.ApplicationPreferences;
import net.rdrei.android.scdl.PreferenceManagerWrapper;
import net.rdrei.android.scdl.ui.DownloadPreferencesDelegate;
import net.rdrei.android.scdl.ui.DownloadPreferencesDelegateFactory;
import net.rdrei.android.scdl.ui.DownloadPreferencesDelegateImpl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.app.Activity;
import android.os.Environment;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.widget.EditText;

import com.google.inject.Inject;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.internal.Implementation;
import com.xtremelabs.robolectric.internal.Implements;
import com.xtremelabs.robolectric.shadows.ShadowDialogPreference;
import com.xtremelabs.robolectric.shadows.ShadowEnvironment;

@RunWith(TestRunner.class)
public class DownloadPreferencesDelegateTest {
	@Inject
	private DownloadPreferencesDelegateFactory mDelegateFactory;
	
	private FakePreferenceManagerWrapperImpl mPreferenceManager;
	private DownloadPreferencesDelegate mDelegate;
	private Activity mActivity;
	
	@Before
	public void setUp() {
		Robolectric.bindShadowClass(ShadowEditTextPreference.class);
		Robolectric.bindShadowClass(ShadowEnvironment2.class);
		mActivity = new Activity();
		TestRunner.getInjector().injectMembers(this);
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
		mDelegate.onCreate();
		
		Preference preference = mPreferenceManager.preferences.get(ApplicationPreferences.KEY_STORAGE_TYPE);
	}
	
	private static class FakePreferenceManagerWrapperImpl implements PreferenceManagerWrapper {
		
		public HashMap<CharSequence, Preference> preferences;
		
		

		public FakePreferenceManagerWrapperImpl() {
			this.preferences = new HashMap<CharSequence, Preference>(2);
		}

		@Override
		public Preference findPreference(CharSequence key) {
			Preference preference;
			if (!preferences.containsKey(key)) {
				preferences.put(key, new ListPreference(null));
			}
			return preferences.get(key);
		}
	}
	
	@Implements(EditTextPreference.class)
	public static class ShadowEditTextPreference extends ShadowDialogPreference {
		@Implementation
		public EditText getEditText() {
			return new EditText(getContext());
		}
	}
	
	@Implements(Environment.class)
	public static class ShadowEnvironment2 extends ShadowEnvironment {
		@Implementation
		public static File getDataDirectory() {
			return new File("/tmp");
		}
	}
}