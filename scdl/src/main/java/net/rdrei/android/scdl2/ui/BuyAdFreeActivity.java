package net.rdrei.android.scdl2.ui;

import net.rdrei.android.mediator.DelayedMessageQueue;
import net.rdrei.android.scdl2.ApplicationPreferences;
import net.rdrei.android.scdl2.R;
import net.rdrei.android.scdl2.ui.BuyAdFreeTeaserFragment.BuyAdFreeFragmentContract;
import net.robotmedia.billing.BillingRequest.ResponseCode;
import roboguice.activity.RoboFragmentActivity;
import roboguice.util.Ln;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Tracker;
import com.google.inject.Inject;

/**
 * This class acts as net.rdrei.android.mediator between the three different
 * fragments loaded into it that are responsible for a) the billing logic b) the
 * teaser view leading to a purchase, giving explanations c) the thanks view
 * after a successful purchase
 * 
 * @author pascal
 */
public class BuyAdFreeActivity extends RoboFragmentActivity implements
		BuyAdFreeFragmentContract {

	private static final String ANALYTICS_TAG = "ANALYTICS_TAG";
	private static final String BILLING_FRAGMENT_TAG = "BILLING";
	private static final String KEY_TEASER_HANDLER = "TEASER_HANDLER";
	private static final String KEY_BILLING_HANDLER = "BILLING_HANDLER";

	@Inject
	private ApplicationPreferences mPreferences;

	@Inject
	private ActionBar mActionBar;

	@Inject
	private DelayedMessageQueue mMessageQueue;
	
	@Inject
	private Tracker mTracker;

	private Fragment mContentFragment;
	private AdFreeBillingFragment mBillingFragment;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.buy_ad_free);
		mActionBar.setDisplayHomeAsUpEnabled(true);

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

	private void loadFragments() {
		final FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();

		if (mPreferences.isAdFree()) {
			mContentFragment = BuyAdFreeThanksFragment.newInstance();
		} else {
			mBillingFragment = AdFreeBillingFragment
					.newInstance(KEY_BILLING_HANDLER);
			mContentFragment = BuyAdFreeTeaserFragment
					.newInstance(KEY_TEASER_HANDLER);

			transaction.add(mBillingFragment, BILLING_FRAGMENT_TAG);
		}

		transaction.add(R.id.main_layout, mContentFragment).commit();
	}
	
	private void replaceWithTeaserFragment() {
		getSupportFragmentManager().beginTransaction().remove(mContentFragment)
				.add(R.id.main_layout, BuyAdFreeTeaserFragment.newInstance(KEY_TEASER_HANDLER))
				.commit();
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
	public void onBillingChecked(final boolean supported) {
		final Message message = Message.obtain();

		if (supported) {
			message.what = BuyAdFreeTeaserFragment.MSG_BILLING_SUPPORTED;
		} else {
			message.what = BuyAdFreeTeaserFragment.MSG_BILLING_UNSUPPORTED;
		}

		mMessageQueue.send(KEY_TEASER_HANDLER, message);
	}

	@Override
	public void onBuyError(final ResponseCode response) {
		Ln.e("IAB error: %s", response.toString());

		mMessageQueue.send(KEY_TEASER_HANDLER, Message.obtain(null,
				BuyAdFreeTeaserFragment.MSG_PURCHASE_ERROR));
		mTracker.trackEvent(ANALYTICS_TAG, "error", response.toString(), null);
	}

	@Override
	public void onBuySuccess() {
		mPreferences.setAdFree(true);

		mMessageQueue.removeHandler(KEY_TEASER_HANDLER);
		getSupportFragmentManager().beginTransaction().remove(mContentFragment)
				.add(R.id.main_layout, BuyAdFreeThanksFragment.newInstance())
				.commit();
		mTracker.trackEvent(ANALYTICS_TAG, "success", null, null);
	}

	@Override
	public void onBuyRevert() {
		mPreferences.setAdFree(false);
		
		if (!(mContentFragment instanceof BuyAdFreeTeaserFragment)) {
			replaceWithTeaserFragment();
		}

		mMessageQueue.send(KEY_TEASER_HANDLER,
				BuyAdFreeTeaserFragment.MSG_PURCHASE_REVERTED);
	}

	@Override
	public void onBuyCancel() {
		mMessageQueue.send(KEY_TEASER_HANDLER,
				BuyAdFreeTeaserFragment.MSG_PURCHASE_CANCELLED);
		mTracker.trackEvent(ANALYTICS_TAG, "cancel", null, null);
	}

	@Override
	public void onPurchaseRequested() {
		mMessageQueue.send(KEY_TEASER_HANDLER,
				BuyAdFreeTeaserFragment.MSG_PURCHASE_REQUESTED);
		mTracker.trackEvent(ANALYTICS_TAG, "request", null, null);
	}

	@Override
	public void onBuyClicked() {
		mMessageQueue.send(KEY_TEASER_HANDLER,
				BuyAdFreeTeaserFragment.MSG_BILLING_REQUESTED);
		mMessageQueue.send(KEY_BILLING_HANDLER,
				AdFreeBillingFragment.MSG_REQUEST_PURCHASE);
	}

	@Override
	/**
	 * Register and accept the message handler of a subordinate fragment.
	 */
	public void registerMessageHandler(final String key,
			final DelayedMessageQueue.Handler handler) {
		mMessageQueue.setHandler(key, handler);
	}

	/**
	 * Urgs, for testing only. I feel bad about this. Should use reflection at
	 * some point for this hackery.
	 * 
	 * @param queue
	 */
	public void setMessageQueue(final DelayedMessageQueue queue) {
		mMessageQueue = queue;
	}
}
