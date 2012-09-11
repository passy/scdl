package net.rdrei.android.scdl2.ui;

import net.rdrei.android.scdl2.ApplicationPreferences;
import net.rdrei.android.scdl2.R;
import net.rdrei.android.scdl2.guice.ActivityLayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Loads and injects an AdView if required into an existing layout.
 * 
 * @author pascal
 */
public class AdViewManager {
	@Inject
	private Provider<ApplicationPreferences> mPreferencesProvider;

	@Inject
	private ActivityLayoutInflater mInflater;

	public AdViewManager() {
		// Injectable
	}

	private View getAdView() {
		return mInflater.inflate(R.layout.adview, null, false);
	}

	/**
	 * Add the AdView to the given baseView.
	 * 
	 * @param baseView
	 *            The layout element to add the view to.
	 */
	public void addToView(final ViewGroup baseView) {
		final View adView = getAdView();
		baseView.addView(adView);
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
