package net.rdrei.android.scdl2.test;

import android.app.ActionBar;

import com.android.vending.billing.IabHelper;
import com.android.vending.billing.IabResult;
import com.android.vending.billing.Inventory;
import com.android.vending.billing.Purchase;
import com.android.vending.billing.SkuDetails;
import com.google.android.gms.analytics.Tracker;
import com.google.inject.AbstractModule;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import net.rdrei.android.scdl2.ui.BuyAdFreeActivity;
import net.rdrei.android.scdl2.ui.BuyAdFreeActivity.PaymentStatus;
import net.rdrei.android.scdl2.ui.BuyAdFreeActivity.PurchaseStateChangeEvent;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class BuyAdFreeActivityTest {
	@Mock
	Tracker mTracker;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		final ActionBar actionbarStub = new ActionBarStub();

		TestHelper.overridenInjector(this, new AbstractModule() {

			@Override
			protected void configure() {
				bind(ActionBar.class).toInstance(actionbarStub);
				// Don't actually initiate the tracker here.
				bind(Tracker.class).toInstance(mTracker);
			}
		});
	}

	@Test
	public void testSmokeOnCreate() {
		// Don't blow up.
		Robolectric.buildActivity(BuyAdFreeActivity.class).create();
	}

	@Test
	public void shouldPropagateInventoryQuery() throws JSONException {
		final BuyAdFreeActivity activity = Robolectric.buildActivity(BuyAdFreeActivity.class)
				.create()
				.get();

		final IabResult result = new IabResult(IabHelper.BILLING_RESPONSE_RESULT_OK, "");

		final HashMap<String, Purchase> purchases = new HashMap<>();
		purchases.put(BuyAdFreeActivity.ADFREE_SKU, new AdfreePurchase());
		Inventory inv = new MyInventory(new HashMap<String, SkuDetails>(), purchases);

		PurchaseChangeSubscriber subscriber = new PurchaseChangeSubscriber();
		Bus bus = TestHelper.getInjector().getInstance(Bus.class);
		bus.register(subscriber);

		activity.onQueryInventoryFinished(result, inv);

		assertThat(subscriber.purchased, is(PaymentStatus.BOUGHT));
	}

	public class MyInventory extends Inventory {
		public MyInventory(Map<String, SkuDetails> details, Map<String, Purchase> purchases) {
			super(details, purchases);
		}
	}

	public class AdfreePurchase extends Purchase {

		public AdfreePurchase(String jsonPurchaseInfo, String signature) throws JSONException {

			super("ITEM_TYPE_INAPP", jsonPurchaseInfo, signature);
		}

		public AdfreePurchase() throws JSONException {
			this("", "");
		}

		@Override
		public String getSku() {
			return BuyAdFreeActivity.ADFREE_SKU;
		}

		@Override
		protected JSONObject unmarshallJSON() throws JSONException {
			return new JSONObject();
		}
	}

	public class PurchaseChangeSubscriber {
		PaymentStatus purchased = PaymentStatus.UNKNOWN;

		@Subscribe
		public void onPurchaseStateChanged(PurchaseStateChangeEvent event) {
			purchased = event.purchased;
		}
	}
}
