package net.rdrei.android.scdl2.test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import net.rdrei.android.scdl2.ShareIntentResolver;
import net.rdrei.android.scdl2.ShareIntentResolver.ShareIntentResolverException;
import net.rdrei.android.scdl2.api.APIException;
import net.rdrei.android.scdl2.api.MediaDownloadType;
import net.rdrei.android.scdl2.api.PendingDownload;
import net.rdrei.android.scdl2.api.entity.ResolveEntity;
import net.rdrei.android.scdl2.api.service.ResolveService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.google.inject.AbstractModule;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowIntent;

@RunWith(RobolectricTestRunner.class)
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
				if (string.contains("/sets/")) {
					entity.setLocation("https://api.soundcloud.com/playlists/13028824.json?client_id=429caab2811564cb27f52a7a4964269b");
				} else if (string.startsWith("https://")) {
					entity.setLocation("https://api.soundcloud.com/tracks/44276907.json?client_id=429caab2811564cb27f52a7a4964269b");
				} else {
					entity.setLocation("http://api.soundcloud.com/tracks/44276907.json?client_id=429caab2811564cb27f52a7a4964269b");
				}
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

		TestHelper.overridenInjector(this, module);
	}

	@Test
	public void testShouldAcceptExtraIntent()
			throws ShareIntentResolverException {

		ShadowIntent intent = Robolectric.shadowOf(mIntent);
		intent.putExtra(Intent.EXTRA_TEXT,
				"http://soundcloud.com/dj-newklear/newklear-contaminated-2");

		final ShareIntentResolver resolver = TestHelper.getInjector()
				.getInstance(ShareIntentResolver.class);

		final String result = resolver.resolve();
		assertThat(result,
				equalTo("http://api.soundcloud.com/tracks/44276907.json?client_id=429caab2811564cb27f52a7a4964269b"));
	}

	@Test
	public void testShouldAcceptDataUri() throws ShareIntentResolverException {

		ShadowIntent intent = Robolectric.shadowOf(mIntent);
		intent.setData(Uri.parse("https://soundcloud.com/dj-newklear/newklear-contaminated-2"));

		final ShareIntentResolver resolver = TestHelper.getInjector()
				.getInstance(ShareIntentResolver.class);

		final String result = resolver.resolve();
		assertThat(
				result,
				equalTo("https://api.soundcloud.com/tracks/44276907.json?client_id=429caab2811564cb27f52a7a4964269b"));
	}

	@Test
	public void testResolveToId() throws ShareIntentResolverException {

		ShadowIntent intent = Robolectric.shadowOf(mIntent);
		intent.setData(Uri
				.parse("https://soundcloud.com/dj-newklear/newklear-contaminated-2"));

		final ShareIntentResolver resolver = TestHelper.getInjector()
				.getInstance(ShareIntentResolver.class);

		final PendingDownload result = resolver.resolvePendingDownload();
		assertThat(result.getId(), equalTo("44276907"));
		assertThat(result.getType(), equalTo(MediaDownloadType.TRACK));
	}

	@Test
	public void testNoSslResolveToId() throws ShareIntentResolverException {

		ShadowIntent intent = Robolectric.shadowOf(mIntent);
		intent.setData(Uri
				.parse("http://soundcloud.com/dj-newklear/newklear-contaminated-2"));

		final ShareIntentResolver resolver = TestHelper.getInjector()
				.getInstance(ShareIntentResolver.class);

		final PendingDownload result = resolver.resolvePendingDownload();
		assertThat(result.getId(), equalTo("44276907"));
		assertThat(result.getType(), equalTo(MediaDownloadType.TRACK));
	}

	@Test(expected = ShareIntentResolverException.class)
	public void testFailWithNullValue() throws ShareIntentResolverException {
		final ShareIntentResolver resolver = TestHelper.getInjector()
				.getInstance(ShareIntentResolver.class);

		resolver.resolve();
	}

	@Test(expected = ShareIntentResolverException.class)
	public void testFailWithInvalidUrl() throws ShareIntentResolverException {
		ShadowIntent intent = Robolectric.shadowOf(mIntent);
		intent.setData(Uri.parse("https://yoosello.de/"));
		final ShareIntentResolver resolver = TestHelper.getInjector()
				.getInstance(ShareIntentResolver.class);

		resolver.resolve();
	}

	@Test(expected = ShareIntentResolverException.class)
	public void testFailWithSuperInvalidUrl() throws ShareIntentResolverException {
		ShadowIntent intent = Robolectric.shadowOf(mIntent);
		intent.setData(Uri.parse("//nope"));
		final ShareIntentResolver resolver = TestHelper.getInjector()
				.getInstance(ShareIntentResolver.class);

		resolver.resolve();
	}

	@Test(expected = ShareIntentResolver.UnsupportedPlaylistUrlException.class)
	public void testFailWithPlaylistUrl() throws ShareIntentResolverException {
		ShadowIntent intent = Robolectric.shadowOf(mIntent);
		intent.setData(Uri.parse("https://soundcloud.com/revealed-recordings/sets/3lau-paris-simo-feat-bright"));
		final ShareIntentResolver resolver = TestHelper.getInjector()
				.getInstance(ShareIntentResolver.class);

		resolver.resolvePendingDownload();
	}
}