package net.rdrei.android.scdl2.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import net.rdrei.android.scdl2.ApplicationPreferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import com.google.inject.Inject;
import com.google.inject.Injector;

@RunWith(RobolectricTestRunner.class)
public class ApplicationPreferenceTest {
	
	@Inject
	ApplicationPreferences mPreferences;
	
	@Before
	public void inject() {
		final Injector injector = TestHelper.getInjector();
		injector.injectMembers(this);
	}
	
	@Test
	public void testAdFreeDefaultsToFalse() {
		assertThat(mPreferences.isAdFree(), is(false));
	}
	
	@Test
	public void testAdFreeSetTrue() {
		mPreferences.setAdFree(true);
		assertThat(mPreferences.isAdFree(), is(true));
	}
}
