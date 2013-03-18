package net.rdrei.android.scdl2.ui;

import net.rdrei.android.scdl2.ApplicationPreferences;
import net.rdrei.android.scdl2.R;
import roboguice.activity.RoboFragmentActivity;
import roboguice.util.Ln;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import com.android.vending.billing.IabException;
import com.android.vending.billing.IabHelper;
import com.android.vending.billing.IabHelper.OnIabPurchaseFinishedListener;
import com.android.vending.billing.IabHelper.OnIabSetupFinishedListener;
import com.android.vending.billing.IabHelper.QueryInventoryFinishedListener;
import com.android.vending.billing.IabResult;
import com.android.vending.billing.Inventory;
import com.android.vending.billing.Purchase;
import com.bugsense.trace.BugSenseHandler;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Tracker;
import com.google.inject.Inject;
import com.squareup.otto.Bus;
import com.squareup.otto.Produce;
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

	public static enum PaymentStatus {
		BOUGHT, NOT_BOUGHT, UNKNOWN
	}

	public static enum IabStatus {
		ENABLED, DISABLED, UNKNOWN
	}

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

	@Inject
	private FragmentManager mFragmentManager;

	private Fragment mContentFragment;

	private PaymentStatus mBought;

	/**
	 * Keeps track of whether the device supports IAB. Listen to
	 * {@link IabSetupFinishedEvent} to be informed of changes.
	 */
	private IabStatus mIabSupported;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.buy_ad_free);
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mIabHelper.startSetup(this);

		mBought = PaymentStatus.UNKNOWN;
		mIabSupported = IabStatus.UNKNOWN;

		if (savedInstanceState == null) {
			loadFragments();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		EasyTracker.getInstance().activityStart(this);
		mTracker.sendEvent(ANALYTICS_TAG, "start", null, null);

		mBus.register(this);
		Ln.d("mBus @ activity: %s", System.identityHashCode(mBus));
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (mBought == PaymentStatus.BOUGHT) {
			replaceWithThanksFragment();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();

		EasyTracker.getInstance().activityStop(this);
		mBus.unregister(this);
	}

	private void loadFragments() {
		final FragmentTransaction transaction = mFragmentManager
				.beginTransaction();

		if (mPreferences.isAdFree()) {
			mContentFragment = BuyAdFreeThanksFragment.newInstance();
		} else {
			mContentFragment = BuyAdFreeTeaserFragment.newInstance();
		}

		transaction.add(R.id.main_layout, mContentFragment).commit();
	}

	private void replaceWithThanksFragment() {
		if (mContentFragment instanceof BuyAdFreeTeaserFragment) {
			final Fragment newFragment = BuyAdFreeThanksFragment.newInstance();
			mFragmentManager.beginTransaction().remove(mContentFragment)
					.add(R.id.main_layout, newFragment)
					.commitAllowingStateLoss();

			mContentFragment = newFragment;
		}
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
		mIabSupported = result.isSuccess() ? IabStatus.ENABLED : IabStatus.DISABLED;
		mBus.post(new IabSetupFinishedEvent(mIabSupported));

		if (result.isSuccess()) {
			mIabHelper.queryInventoryAsync(this);
		} else {
			Ln.w("Can't connect to IAB: %s", result);
			mTracker.sendEvent(ANALYTICS_TAG, "error", result.toString(), null);
		}
	}

	@Override
	public void onQueryInventoryFinished(IabResult result, Inventory inv) {
		Ln.d("onQueryInventoryFinished: %s", result);

		if (result.isFailure()) {
			mTracker.sendEvent(ANALYTICS_TAG, "error", result.toString(), null);
			Ln.w("Failed to retrieve inventory!");
			return;
		}

		final boolean hasItem = inv.hasPurchase(ADFREE_SKU);
		final PurchaseStateChangeEvent event = new PurchaseStateChangeEvent(
				hasItem ? PaymentStatus.BOUGHT : PaymentStatus.NOT_BOUGHT);
		mBus.post(event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (mIabHelper != null) {
			mIabHelper.dispose();
			mIabHelper = null;
		}
	}

	@Override
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent data) {
		Ln.d("onActivityResult(" + requestCode + "," + resultCode + "," + data);

		// Closure-style callback passing to avoid code duplication.
		final Runnable handleResult = new Runnable() {
			@Override
			public void run() {
				// Pass on the activity result to the helper for handling
				if (!mIabHelper.handleActivityResult(requestCode, resultCode,
						data)) {
					// not handled, so handle it ourselves (here's where you'd
					// perform any handling of activity results not related to
					// in-app
					// billing...
					BuyAdFreeActivity.super.onActivityResult(requestCode,
							resultCode, data);
				}
			}
		};

		if (mIabHelper.isSetupDone()) {
			handleResult.run();
		} else {
			mIabHelper.startSetup(new OnIabSetupFinishedListener() {
				@Override
				public void onIabSetupFinished(IabResult result) {
					if (result.isSuccess()) {
						handleResult.run();
					} else {
						mTracker.sendEvent(ANALYTICS_TAG, "error",
								result.toString(), null);
						BugSenseHandler.sendException(new IabException(result));
					}
				}
			});
		}
	}

	@Subscribe
	public void onPurchaseRequested(PurchaseAdfreeRequestEvent event) {
		mIabHelper.launchPurchaseFlow(this, ADFREE_SKU, ADFREE_REQUEST_CODE,
				this);
	}

	@Subscribe
	public void onPurchaseStateChanged(PurchaseStateChangeEvent event) {
		Ln.i("Saving purchase state as %s.", event.purchased);

		if (event.purchased == PaymentStatus.BOUGHT) {
			mPreferences.setAdFree(true);
			mTracker.sendEvent(ANALYTICS_TAG, "success", null, null);

			mBought = PaymentStatus.BOUGHT;
			replaceWithThanksFragment();
		}
	}

	@Produce
	public IabSetupFinishedEvent produceIabSetupFinishedEvent() {
		return new IabSetupFinishedEvent(mIabSupported);
	}

	@Produce
	public PurchaseStateChangeEvent producePurchaseStateChangeEvent() {
		return new PurchaseStateChangeEvent(mBought);
	}

	@Override
	public void onIabPurchaseFinished(IabResult result, Purchase info) {
		Ln.d("onIabPurchaseFinished: %s", result);
		boolean success = result.isSuccess()
				&& info.getSku().equals(ADFREE_SKU);

		mBus.post(new PurchaseAdfreeFinishedEvent(success));

		if (success) {
			mBus.post(new PurchaseStateChangeEvent(PaymentStatus.BOUGHT));
		} else if (result.getResponse() == IabHelper.BILLING_RESPONSE_RESULT_USER_CANCELED) {
			mTracker.sendEvent(ANALYTICS_TAG, "cancel", result.toString(),
					null);
		} else {
			// Make sure we inform Bugsense about this!
			try {
				BugSenseHandler.sendException(new IabException(result));
			} catch (Exception e) {
				// Fire and forget.
				Ln.w(e);
			}
			mTracker.sendEvent(ANALYTICS_TAG, "error", result.toString(), null);
		}
	}

	public static final class IabSetupFinishedEvent {
		public final IabStatus enabled;

		public IabSetupFinishedEvent(IabStatus enabled) {
			this.enabled = enabled;
		}
	}

	public static final class PurchaseStateChangeEvent {
		public final PaymentStatus purchased;

		public PurchaseStateChangeEvent(PaymentStatus purchased) {
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
