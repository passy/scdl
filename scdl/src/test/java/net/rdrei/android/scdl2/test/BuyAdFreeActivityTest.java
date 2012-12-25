package net.rdrei.android.scdl2.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import net.rdrei.android.scdl2.ui.BuyAdFreeActivity;
import net.rdrei.android.scdl2.ui.BuyAdFreeActivity.PurchaseStateChangeEvent;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import android.app.ActionBar;

import com.android.vending.billing.IabHelper;
import com.android.vending.billing.IabResult;
import com.android.vending.billing.Inventory;
import com.android.vending.billing.Purchase;
import com.android.vending.billing.SkuDetails;
import com.google.analytics.tracking.android.Tracker;
import com.google.inject.AbstractModule;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

@RunWith(TestRunner.class)
public class BuyAdFreeActivityTest {
	@Mock
	Tracker mTracker;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		final ActionBar actionbarStub = new ActionBarStub();

		TestRunner.overridenInjector(this, new AbstractModule() {

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
		final BuyAdFreeActivity activity = new BuyAdFreeActivity();
		// Don't blow up.
		activity.onCreate(null);
	}

	@Test
	public void shouldPropagateInventoryQuery() throws JSONException {
		final BuyAdFreeActivity activity = new BuyAdFreeActivity();
		activity.onCreate(null);

		final IabResult result = new IabResult(
				IabHelper.BILLING_RESPONSE_RESULT_OK, "");

		final HashMap<String, Purchase> purchases = new HashMap<>();
		purchases.put(BuyAdFreeActivity.ADFREE_SKU, new AdfreePurchase());
		Inventory inv = new MyInventory(new HashMap<String, SkuDetails>(),
				purchases);

		PurchaseChangeSubscriber subscriber = new PurchaseChangeSubscriber();
		Bus bus = TestRunner.getInjector().getInstance(Bus.class);
		bus.register(subscriber);

		activity.onQueryInventoryFinished(result, inv);

		assertThat(subscriber.purchased, is(true));
	}

	public class MyInventory extends Inventory {
		public MyInventory(Map<String, SkuDetails> details,
				Map<String, Purchase> purchases) {
			super(details, purchases);
		}
	}

	public class AdfreePurchase extends Purchase {

		public AdfreePurchase(String jsonPurchaseInfo, String signature)
				throws JSONException {
			super(jsonPurchaseInfo, signature);
		}

		public AdfreePurchase() throws JSONException {
			this("", "");
		}

		@Override
		protected void unmarshallJSON() {
			// NO-Op
		}

		@Override
		public String getSku() {
			return BuyAdFreeActivity.ADFREE_SKU;
		}
	}

	public class PurchaseChangeSubscriber {
		boolean purchased = false;

		@Subscribe
		public void onPurchaseStateChanged(PurchaseStateChangeEvent event) {
			purchased = event.purchased;
		}
	}
}
