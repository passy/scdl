package net.rdrei.android.scdl2.ui;

import android.view.InflateException;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.inject.Inject;
import com.google.inject.Provider;

import net.rdrei.android.option.Option;
import net.rdrei.android.scdl2.ApplicationPreferences;
import net.rdrei.android.scdl2.R;
import net.rdrei.android.scdl2.guice.ActivityLayoutInflater;

import roboguice.util.Ln;

/**
 * Loads and injects an AdView if required into an existing layout.
 *
 * @author pascal
 */
public class AdViewManager {
	private Provider<ApplicationPreferences> mPreferencesProvider;
	private ActivityLayoutInflater mInflater;

	@Inject
	public AdViewManager(Provider<ApplicationPreferences> preferencesProvider,
			ActivityLayoutInflater inflater) {
		this.mPreferencesProvider = preferencesProvider;
		this.mInflater = inflater;
	}

	private Option<View> getAdView(final ViewGroup baseView) {
		try {
			return Option.some(mInflater.inflate(R.layout.adview, baseView, false));
		} catch (InflateException exc) {
			Ln.e(exc);
			Crashlytics.logException(exc);
		}

		return Option.none();
	}

	private Option<View> setupView(final View view) {
		AdView adView;

		try {
			adView = (AdView) view;
		} catch (ClassCastException err) {
			adView = null;
			Crashlytics.logException(err);
		}

		if (adView != null) {
			final AdRequest adRequest = new AdRequest.Builder()
					.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
					.build();
			adView.loadAd(adRequest);
			return Option.some(adView);
		}

		return Option.none();
	}

	/**
	 * Add the AdView to the given baseView.
	 *
	 * @param baseView The layout element to add the view to.
	 */
	public void addToView(final ViewGroup baseView) {
		final Option<View> adView = getAdView(baseView);
		adView
			.flatMap(this::setupView)
			.call(baseView::addView);
	}

	/**
	 * Add to the given baseView, but only if the application is non-adfree.
	 *
	 * @param baseView The view you want to see your ads in.
	 * @return True if added to layout, false if not.
	 */
	public boolean addToViewIfRequired(final ViewGroup baseView) {
		final boolean showAds = !mPreferencesProvider.get().isAdFree();
		if (showAds) {
			addToView(baseView);
		}

		return showAds;
	}
}
