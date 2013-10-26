package net.rdrei.android.scdl2.test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import net.rdrei.android.scdl2.api.APIException;
import net.rdrei.android.scdl2.api.ServiceManager;
import net.rdrei.android.scdl2.api.URLConnectionFactory;
import net.rdrei.android.scdl2.api.entity.TrackEntity;
import net.rdrei.android.scdl2.api.service.TrackService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.thoughtcrime.ssl.pinning.PinningTrustManager;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;

/**
 * This testcase checks if purchase information are correctly extracted from the
 * JSON response.
 * 
 * @author pascal
 * 
 */
@RunWith(RobolectricTestRunner.class)
public class PurchaseEntityTest {
	@Inject
	private ServiceManager mServiceManager;

	private FakeURLConnectionFactoryImpl mUrlConnectionFactory;

	@Before
	public void setUp() {
		final AbstractModule module = new AbstractModule() {

			@Override
			protected void configure() {
				mUrlConnectionFactory = new FakeURLConnectionFactoryImpl(
						"/fixtures/track_purchase.json");
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
	public void testResolvePurchase() throws APIException {
		final TrackService service = mServiceManager.trackService();
		TrackEntity entity = service.getTrack("60200642");

		assertThat(entity.isPurchasable(), is(true));

		assertThat(entity.isDownloadable(), is(false));

		assertThat(entity.getPurchaseUrl(),
				equalTo("http://bit.ly/3LAU_HAUS"));

		assertThat(entity.getPurchaseTitle(), equalTo("Download on Facebook"));
	}

}