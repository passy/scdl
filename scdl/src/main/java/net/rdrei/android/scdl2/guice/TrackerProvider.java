package net.rdrei.android.scdl2.guice;

import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.inject.Inject;
import com.google.inject.Provider;

import net.rdrei.android.scdl2.R;

import roboguice.util.Ln;

public class TrackerProvider implements Provider<Tracker> {
	
	@Inject
	private Context mContext;

	@Override
	public Tracker get() {
		Ln.w("I CAN HAZ SINGLETONE?!?!?");

		return GoogleAnalytics.getInstance(mContext).newTracker(R.xml.google_analytics);
	}

}
