package net.rdrei.android.scdl2.ui;

import android.content.Intent;
import android.os.Bundle;

import net.rdrei.android.scdl2.R;

import roboguice.activity.RoboActivity;
import roboguice.activity.RoboFragmentActivity;

/**
 * Dispatches a download request to either the single download or playlist download activity.
 */
public class DownloadActivity extends RoboFragmentActivity {
	public static final String ANALYTICS_TAGS = "DOWNLOAD_ACTIVITY";
	public static final String MEDIA_STATE_TAG = "scdl:MEDIA_STATE_TAG";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.download);

		if (savedInstanceState != null) {
			// TODO: Load MediaState
		} else {
			CommonMenuFragment.injectMenu(this);
			// TODO: Resolve MediaState
		}
	}
}
