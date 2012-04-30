package net.rdrei.android.scdl.test;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import net.rdrei.android.scdl.ShareIntentResolver;
import net.rdrei.android.scdl.ShareIntentResolver.ShareIntentResolverException;
import net.rdrei.android.scdl.api.APIException;
import net.rdrei.android.scdl.api.entity.ResolveEntity;
import net.rdrei.android.scdl.api.service.ResolveService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.google.inject.AbstractModule;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.shadows.ShadowIntent;

@RunWith(TestRunner.class)
public class ShareIntentResolverTest {

	private Activity mActivity;
	private Intent mIntent;
	private ResolveService mResolveService;

	@Before
	public void setUp() {
		mIntent = new Intent();

		mActivity = new Activity() {
			@Override
			public Intent getIntent() {
				return mIntent;
			}
		};
		
		mResolveService = new ResolveService() {
			@Override
			public ResolveEntity resolve(String string) throws APIException {
				ResolveEntity entity = new ResolveEntity();
				entity.setStatus("302 - Found");
				entity.setLocation("https://api.soundcloud.com/tracks/44276907.json?client_id=429caab2811564cb27f52a7a4964269b");
				return entity;
			}
		};

		AbstractModule module = new AbstractModule() {
			@Override
			protected void configure() {
				bind(Activity.class).toInstance(mActivity);
				bind(ResolveService.class).toInstance(mResolveService);
			}
		};

		TestRunner.overridenInjector(this, module);
	}

	@Test
	public void testShouldAcceptExtraIntent()
			throws ShareIntentResolverException {

		ShadowIntent intent = Robolectric.shadowOf(mIntent);
		intent.putExtra(Intent.EXTRA_TEXT,
				"http://soundcloud.com/dj-newklear/newklear-contaminated-2");

		final ShareIntentResolver resolver = TestRunner.getInjector()
				.getInstance(ShareIntentResolver.class);

		final String result = resolver.resolve();
		assertThat(
				result,
				equalTo("http://api.soundcloud.com/tracks/44276907.json?client_id=429caab2811564cb27f52a7a4964269b"));
	}

	@Test
	public void testShouldAcceptDataUri() throws ShareIntentResolverException {

		ShadowIntent intent = Robolectric.shadowOf(mIntent);
		intent.setData(Uri
				.parse("http://soundcloud.com/dj-newklear/newklear-contaminated-2"));

		final ShareIntentResolver resolver = TestRunner.getInjector()
				.getInstance(ShareIntentResolver.class);

		final String result = resolver.resolve();
		assertThat(
				result,
				equalTo("http://api.soundcloud.com/tracks/44276907.json?client_id=429caab2811564cb27f52a7a4964269b"));
	}

	@Test
	public void testResolveToId() throws ShareIntentResolverException {

		ShadowIntent intent = Robolectric.shadowOf(mIntent);
		intent.setData(Uri
				.parse("http://soundcloud.com/dj-newklear/newklear-contaminated-2"));

		final ShareIntentResolver resolver = TestRunner.getInjector()
				.getInstance(ShareIntentResolver.class);

		final String result = resolver.resolveId();
		assertThat(result, equalTo("44276907"));
	}

	@Test(expected = ShareIntentResolverException.class)
	public void testFailWithNullValue() throws ShareIntentResolverException {
		final ShareIntentResolver resolver = TestRunner.getInjector()
				.getInstance(ShareIntentResolver.class);

		resolver.resolve();
	}

	@Test(expected = ShareIntentResolverException.class)
	public void testFailWithInvalidUrl() throws ShareIntentResolverException {
		ShadowIntent intent = Robolectric.shadowOf(mIntent);
		intent.setData(Uri.parse("https://yoosello.de/"));
		final ShareIntentResolver resolver = TestRunner.getInjector()
				.getInstance(ShareIntentResolver.class);

		resolver.resolve();
	}
}