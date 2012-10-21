package net.rdrei.android.scdl2.guice;

import android.content.Context;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Tracker;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class TrackerProvider implements Provider<Tracker> {
	
	@Inject
	private Context mContext;

	@Override
	public Tracker get() {
		EasyTracker.getInstance().setContext(mContext);
		return EasyTracker.getTracker();
	}

}
