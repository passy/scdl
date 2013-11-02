package net.rdrei.android.scdl2.ui;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.rdrei.android.scdl2.R;
import net.rdrei.android.scdl2.api.MediaState;

import roboguice.util.Ln;

/**
 * Fragment for downloading a single track, used by DownloadActivity.
 */
public class DownloadTrackFragment extends Fragment {

	private static String MEDIA_STATE_ARG = "MEDIA_STATE_ARG";

	public static DownloadTrackFragment newInstance(MediaState state) {
		final DownloadTrackFragment fragment = new DownloadTrackFragment();
		final Bundle args = new Bundle();
		args.putParcelable(MEDIA_STATE_ARG, state);
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		Ln.d("onAttach for DownloadTrackFragment: %s", getArguments().getParcelable(MEDIA_STATE_ARG));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.select_track, container, false);
	}
}
