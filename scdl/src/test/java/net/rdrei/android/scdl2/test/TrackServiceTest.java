package net.rdrei.android.scdl2.test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import net.rdrei.android.scdl2.api.APIException;
import net.rdrei.android.scdl2.api.ServiceManager;
import net.rdrei.android.scdl2.api.URLConnectionFactory;
import net.rdrei.android.scdl2.api.entity.TrackEntity;
import net.rdrei.android.scdl2.api.service.TrackService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.thoughtcrime.ssl.pinning.PinningTrustManager;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;

@RunWith(TestRunner.class)
public class TrackServiceTest {

	@Inject
	private ServiceManager mServiceManager;

	private FakeURLConnectionFactoryImpl mUrlConnectionFactory;

	@Before
	public void setUp() {
		final AbstractModule module = new AbstractModule() {

			@Override
			protected void configure() {
				mUrlConnectionFactory = new FakeURLConnectionFactoryImpl(
						"/fixtures/track.json");
				mUrlConnectionFactory.setResponseCode(200);
				// Disable this to have a real connection test.
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
		final TrackService service = mServiceManager.trackService();
		assertThat(service, notNullValue());
	}

	@Test
	public void testResolveTrack() throws APIException {
		final TrackService service = mServiceManager.trackService();
		TrackEntity entity = service.getTrack("44276907");

		assertThat(
				entity.getTitle(),
				equalTo("Newklear - Contaminated Selection *SPECIAL GUEST SHOW BOUNFM RADIO APRIL. 24TH*"));
	}

}
