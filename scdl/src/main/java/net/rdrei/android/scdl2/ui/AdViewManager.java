package net.rdrei.android.scdl2.ui;

import net.rdrei.android.scdl2.ApplicationPreferences;
import net.rdrei.android.scdl2.R;
import net.rdrei.android.scdl2.guice.ActivityLayoutInflater;

import android.view.InflateException;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;
import com.google.inject.Inject;
import com.google.inject.Provider;

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

	private View getAdView() {
		try {
			return mInflater.inflate(R.layout.adview, null, false);
		} catch (InflateException exc) {
			Crashlytics.logException(exc);
		}

		return null;
	}

	/**
	 * Add the AdView to the given baseView.
	 * 
	 * @param baseView
	 *            The layout element to add the view to.
	 */
	public void addToView(final ViewGroup baseView) {
		final View adView = getAdView();

		if (adView != null) {
			baseView.addView(adView);
		}
	}

	/**
	 * Add to the given baseView, but only if the application is non-adfree.
	 * 
	 * @param baseView
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
