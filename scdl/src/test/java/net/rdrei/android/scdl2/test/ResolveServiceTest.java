package net.rdrei.android.scdl2.test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import net.rdrei.android.scdl2.api.APIException;
import net.rdrei.android.scdl2.api.ServiceManager;
import net.rdrei.android.scdl2.api.URLConnectionFactory;
import net.rdrei.android.scdl2.api.entity.ResolveEntity;
import net.rdrei.android.scdl2.api.service.ResolveService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.thoughtcrime.ssl.pinning.PinningTrustManager;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;

@RunWith(TestRunner.class)
public class ResolveServiceTest {

	@Inject
	private ServiceManager mServiceManager;

	private FakeURLConnectionFactoryImpl mUrlConnectionFactory;

	@Before
	public void setUp() {
		final AbstractModule module = new AbstractModule() {

			@Override
			protected void configure() {
				mUrlConnectionFactory = new FakeURLConnectionFactoryImpl(
						"/fixtures/resolve.json");
				mUrlConnectionFactory.setResponseCode(302);
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
		final ResolveService service = mServiceManager.resolveService();
		assertThat(service, notNullValue());
	}

	@Test
	public void testResolveTrack() throws APIException {
		final ResolveService service = mServiceManager.resolveService();
		ResolveEntity entity = service
				.resolve("http://soundcloud.com/dj-newklear/newklear-contaminated-2");
		assertThat(entity.getLocation(), equalTo("http://api.soundcloud.com/tracks/44276907.json?client_id=429caab2811564cb27f52a7a4964269b"));
	}
}