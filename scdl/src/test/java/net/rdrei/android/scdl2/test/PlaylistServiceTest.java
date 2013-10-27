package net.rdrei.android.scdl2.test;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;

import net.rdrei.android.scdl2.api.APIException;
import net.rdrei.android.scdl2.api.ServiceManager;
import net.rdrei.android.scdl2.api.URLConnectionFactory;
import net.rdrei.android.scdl2.api.entity.PlaylistEntity;
import net.rdrei.android.scdl2.api.service.PlaylistService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.thoughtcrime.ssl.pinning.PinningTrustManager;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class PlaylistServiceTest {

	@Inject
	private ServiceManager mServiceManager;

	private FakeURLConnectionFactoryImpl mUrlConnectionFactory;

	@Before
	public void setUp() {
		final AbstractModule module = new AbstractModule() {

			@Override
			protected void configure() {
				mUrlConnectionFactory = new FakeURLConnectionFactoryImpl(
						"/fixtures/playlist.json");
				mUrlConnectionFactory.setResponseCode(200);

				// Disable this to have a real connection test.
				bind(URLConnectionFactory.class).toInstance(
						mUrlConnectionFactory);
				bind(PinningTrustManager.class).toInstance(
						new TrustManagerStub());
			}
		};

		TestHelper.overridenInjector(this, module);
	}

	@Test
	public void testShouldCreateService() {
		final PlaylistService service = mServiceManager.playlistService();
		assertThat(service, notNullValue());
	}

	@Test
	public void shouldResolvePlaylist() throws APIException {
		final PlaylistService service = mServiceManager.playlistService();
		final PlaylistEntity entity = service.getPlaylist("13028824");

		assertThat(entity.getDescription(), startsWith(
				"At the tail end of 2013, few summer anthems"));
		assertThat(entity.getTracks().get(0).getId(), equalTo(116980406l));
	}

}
