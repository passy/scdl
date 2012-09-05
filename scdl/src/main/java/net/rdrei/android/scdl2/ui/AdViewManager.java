package net.rdrei.android.scdl2.ui;

import net.rdrei.android.scdl2.ApplicationPreferences;
import net.rdrei.android.scdl2.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.inject.Inject;

/**
 * Loads and injects an AdView if required into an existing layout.
 * 
 * @author pascal
 */
public class AdViewManager {
	@Inject
	private Context mContext;
	
	@Inject
	private ApplicationPreferences mPreferences;
	
	@Inject
	private LayoutInflater mInflater;
	
	public AdViewManager() {
		// Injectable
	}
	
	private View getAdView() {
		return mInflater.inflate(R.layout.adview, null, false);
	}
	
	/**
	 * Add the AdView to the given baseView.
	 * 
	 * @param baseView The layout element to add the view to.
	 */
	public void addToView(ViewGroup baseView) {
		View adView = getAdView();
		baseView.addView(adView);
	}
	
	/**
	 * Add to the given baseView, but only if the application is non-adfree.
	 * 
	 * @param baseView
	 * @return True if added to layout, false if not.
	 */
	public boolean addToViewIfRequired(ViewGroup baseView) {
		boolean showAds = !mPreferences.isAdFree();
		if (showAds) {
			addToView(baseView);
		}
		
		return showAds;
	}
}
