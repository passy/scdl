package net.rdrei.android.scdl2.ui;

import net.rdrei.android.scdl2.ApplicationPreferences;
import net.rdrei.android.scdl2.R;
import roboguice.activity.RoboFragmentActivity;
import roboguice.util.Ln;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import com.android.vending.billing.IabHelper;
import com.android.vending.billing.IabHelper.OnIabPurchaseFinishedListener;
import com.android.vending.billing.IabHelper.OnIabSetupFinishedListener;
import com.android.vending.billing.IabHelper.QueryInventoryFinishedListener;
import com.android.vending.billing.IabResult;
import com.android.vending.billing.Inventory;
import com.android.vending.billing.Purchase;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Tracker;
import com.google.inject.Inject;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * This activity loads the appropriate fragments based on whether the user has
 * done a purchase yet or not. It will also handle the payment processing via
 * messages from the corresponding fragments.
 * 
 * @author pascal
 */
public class BuyAdFreeActivity extends RoboFragmentActivity implements
		OnIabSetupFinishedListener, QueryInventoryFinishedListener,
		OnIabPurchaseFinishedListener {

	private static final String ANALYTICS_TAG = "BUY_ADFREE";

	/**
	 * Request code to differentiate between different items.
	 */
	private static final int ADFREE_REQUEST_CODE = 0;

	/**
	 * SKU used in the developer console to identify the item.
	 */
	public static final String ADFREE_SKU = "adfree";

	@Inject
	private ApplicationPreferences mPreferences;

	@Inject
	private ActionBar mActionBar;

	@Inject
	private Tracker mTracker;

	@Inject
	private IabHelper mIabHelper;

	@Inject
	private Bus mBus;

	private Fragment mContentFragment;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.buy_ad_free);
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mIabHelper.startSetup(this);

		if (savedInstanceState == null) {
			loadFragments();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		EasyTracker.getInstance().activityStart(this);
		mTracker.trackEvent(ANALYTICS_TAG, "start", null, null);
	}

	@Override
	protected void onStop() {
		super.onStop();

		EasyTracker.getInstance().activityStop(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		mBus.register(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		mBus.unregister(this);
	}

	private void loadFragments() {
		final FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();

		if (mPreferences.isAdFree()) {
			mContentFragment = BuyAdFreeThanksFragment.newInstance();
		} else {
			mContentFragment = BuyAdFreeTeaserFragment.newInstance();
		}

		transaction.add(R.id.main_layout, mContentFragment).commit();
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			final Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			startActivity(intent);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onIabSetupFinished(IabResult result) {
		Ln.d("onIabSetupFinished: %s", result);
		mBus.post(new IabSetupFinishedEvent(result.isSuccess()));

		if (result.isSuccess()) {
			mIabHelper.queryInventoryAsync(this);
		}
	}

	@Override
	public void onQueryInventoryFinished(IabResult result, Inventory inv) {
		Ln.d("onQueryInventoryFinished: %s", result);
		if (result.isFailure()) {
			Ln.w("Failed to retrieve inventory!");
			return;
		}

		mBus.post(new PurchaseStateChangeEvent(inv.hasPurchase(ADFREE_SKU)));
	}

	@Subscribe
	public void onPurchaseRequested(PurchaseAdfreeRequestEvent event) {
		mIabHelper.launchPurchaseFlow(this, ADFREE_SKU, ADFREE_REQUEST_CODE,
				this);
	}
	
	@Subscribe
	public void onPurchaseStateChanged(PurchaseStateChangeEvent event) {
		mPreferences.setAdFree(event.purchased);
		
		if (event.purchased) {
			// TODO: Replace fragment.
		}
	}

	@Override
	public void onIabPurchaseFinished(IabResult result, Purchase info) {
		Ln.d("onIabPurchaseFinished: ", result);
		boolean success = result.isSuccess() && info.getSku() == ADFREE_SKU;
		
		mBus.post(new PurchaseAdfreeFinishedEvent(success));
		
		if (success) {
			mBus.post(new PurchaseStateChangeEvent(true));
		} else {
			new AlertDialog.Builder(this)
				.setTitle(R.string.buy_ad_free_error_title)
				.setMessage(R.string.buy_ad_free_error_message)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.create().show();
		}
	}

	public static final class IabSetupFinishedEvent {
		public final boolean enabled;

		public IabSetupFinishedEvent(boolean enabled) {
			this.enabled = enabled;
		}
	}

	public static final class PurchaseStateChangeEvent {
		public final boolean purchased;

		public PurchaseStateChangeEvent(boolean purchased) {
			super();
			this.purchased = purchased;
		}
	}

	public static final class PurchaseAdfreeRequestEvent {
	}
	
	public static final class PurchaseAdfreeFinishedEvent {
		final public boolean success;

		public PurchaseAdfreeFinishedEvent(boolean success) {
			super();
			this.success = success;
		}
	}
}
