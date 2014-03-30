package net.rdrei.android.scdl2.guice;

import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.inject.Inject;
import com.google.inject.Provider;

import net.rdrei.android.scdl2.R;

public class TrackerProvider implements Provider<Tracker> {

	@Inject
	private Context mContext;

	@Override
	public Tracker get() {
		final GoogleAnalytics googleAnalytics = GoogleAnalytics.getInstance(mContext);
		return googleAnalytics.newTracker(R.xml.google_analytics);
	}

}
