package net.rdrei.android.scdl2.test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import net.rdrei.android.scdl2.api.APIException;
import net.rdrei.android.scdl2.api.ServiceManager;
import net.rdrei.android.scdl2.api.URLConnectionFactory;
import net.rdrei.android.scdl2.api.service.DownloadService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.thoughtcrime.ssl.pinning.PinningTrustManager;

import android.net.Uri;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;

@RunWith(TestRunner.class)
public class DownloadServiceTest {

	@Inject
	private ServiceManager mServiceManager;

	private FakeURLConnectionFactoryImpl mUrlConnectionFactory;
	
	private static final String DOWNLOAD_URL = "http://ak-media.soundcloud.com/9OQ63FqBTMAA?AWSAccessKeyId=AKIAJBHW5FB4ERKUQUOQ&Expires=1335895042&Signature=XJ8NOIJXKf09iEOC4sc5SU7OmBM%3D&__gda__=1335895042_b26bb4a83dd728ef4d883e107d69be1ca";

	@Before
	public void setUp() {
		final AbstractModule module = new AbstractModule() {

			@Override
			protected void configure() {
				mUrlConnectionFactory = new FakeURLConnectionFactoryImpl();
				mUrlConnectionFactory.setResponseCode(302);
				mUrlConnectionFactory.setHeaderField("Location", DOWNLOAD_URL);
				bind(URLConnectionFactory.class).toInstance(
						mUrlConnectionFactory);
				bind(PinningTrustManager.class).toInstance(
						new TrustManagerStub());
			}
		};

		TestRunner.overridenInjector(this, module);
	}

	@Test
	public void testShouldCreateService() {
		final DownloadService service = mServiceManager.downloadService();
		assertThat(service, notNullValue());
	}

	@Test
	public void testDownloadResolver() throws APIException {
		final DownloadService service = mServiceManager.downloadService();
		Uri uri = service.resolveUri("44276907");
		assertThat(uri.toString(), equalTo(DOWNLOAD_URL));
	}
}