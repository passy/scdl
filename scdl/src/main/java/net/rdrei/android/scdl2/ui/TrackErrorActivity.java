package net.rdrei.android.scdl2.ui;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.inject.Inject;

import net.rdrei.android.scdl2.R;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

public class TrackErrorActivity extends RoboActivity {

	private static final String ANALYTICS_TAG = "TRACK_ERROR";

	public static enum ErrorCode {
		UNSUPPORTED_URL, NO_WRITE_PERMISSION, UNKNOWN_ERROR, NO_MARKET, NOT_FOUND, PLAYLIST, NETWORK_ERROR
	}

	public static final String EXTRA_ERROR_CODE = "error_code";

	@InjectView(R.id.error_message)
	private TextView mErrorTextView;

	@InjectView(R.id.main_layout)
	private ViewGroup mMainLayout;

	@InjectExtra(EXTRA_ERROR_CODE)
	private ErrorCode mErrorCode;

	@Inject
	private AdViewManager mAdViewManager;

	@Inject
	private Tracker mTracker;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.track_error);
		setErrorText();

		mTracker.send(new HitBuilders.EventBuilder()
				.setCategory(ANALYTICS_TAG)
				.setAction(mErrorCode.toString())
				.build()
		);

		if (savedInstanceState == null) {
			mAdViewManager.addToViewIfRequired(mMainLayout);
		}
	}

	private void setErrorText() {
		final int errorMessage;

		switch (mErrorCode) {
			case UNSUPPORTED_URL:
				errorMessage = R.string.track_error_unsupported_url;
				break;
			case NO_WRITE_PERMISSION:
				errorMessage = R.string.track_error_no_write_permission;
				break;
			case NO_MARKET:
				errorMessage = R.string.track_error_no_market;
				break;
			case NOT_FOUND:
				errorMessage = R.string.track_error_not_found;
				break;
			case NETWORK_ERROR:
				errorMessage = R.string.track_error_network;
				break;
			case PLAYLIST:
				errorMessage = R.string.track_error_unsupported_playlist;
				break;
			default:
				errorMessage = R.string.track_error_unknown;
		}

		mErrorTextView.setText(errorMessage);
	}
}
