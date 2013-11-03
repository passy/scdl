package net.rdrei.android.scdl2.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.inject.Inject;

import net.rdrei.android.scdl2.ApplicationPreferences;
import net.rdrei.android.scdl2.R;
import net.rdrei.android.scdl2.api.MediaState;
import net.rdrei.android.scdl2.api.entity.TrackEntity;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

/**
 * Fragment for downloading a single track, used by DownloadActivity.
 */
public class DownloadTrackFragment extends RoboFragment {

	@InjectView(R.id.track_title)
	private TextView mTitleView;

	@InjectView(R.id.track_description)
	private TextView mDescriptionView;

	@InjectView(R.id.detail_container)
	private View mDetailContainerView;

	@InjectView(R.id.track_unavailable)
	private TextView mTrackUnavailableView;

	@InjectView(R.id.progress_bar)
	private View mProgressBarView;

	@InjectView(R.id.btn_download)
	private Button mDownloadButton;

	@InjectView(R.id.btn_remove_ads)
	private Button mRemoveAdsButton;

	@InjectView(R.id.img_artwork)
	private ImageView mArtworkImageView;

	@InjectView(R.id.track_length)
	private TextView mLengthView;

	@InjectView(R.id.track_artist)
	private TextView mArtistView;

	@InjectView(R.id.track_size)
	private TextView mSizeView;

	@Inject
	private ApplicationPreferences mPreferences;

	private TrackEntity mTrack;

	private static String TRACK_TAG = "TRACK_TAG";

	public static DownloadTrackFragment newInstance(MediaState state) {
		final DownloadTrackFragment fragment = new DownloadTrackFragment();
		final Bundle args = new Bundle();
		// This will raise a loud exception if we have a null, which is fine.
		args.putParcelable(TRACK_TAG, state.getTrackOption().get());
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mTrack = getArguments().getParcelable(TRACK_TAG);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.select_track, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (mPreferences.isAdFree()) {
			mRemoveAdsButton.setVisibility(View.GONE);
		}

		updateTrackDisplay();
	}

	private void updateTrackDisplay() {
		if (mTrack == null) {
			return;
		}

		mTitleView.setText(mTrack.getTitle());
		mDescriptionView.setText(mTrack.getDescription());
		mLengthView.setText(mTrack.getFormattedDuration());
		mLengthView.setVisibility(View.VISIBLE);
		mSizeView.setText(mTrack.getFormattedSize());
		mArtistView.setText(mTrack.getUser().getUsername());
		mProgressBarView.setVisibility(View.GONE);
		mDetailContainerView.setVisibility(View.VISIBLE);

		updateTrackAvailability();
		updateButtons();

//		final ArtworkLoaderTask artworkLoaderTask = new ArtworkLoaderTask(
//				mTrack.getArtworkUrl());
//		artworkLoaderTask.execute();
	}

	/**
	 * Update the button display based on the availability of mTrack.
	 */
	private void updateButtons() {
		if (mTrack.isDownloadable()) {
			mDownloadButton.setEnabled(true);
		} else if (mTrack.isPurchasable()) {
			mDownloadButton.setEnabled(true);
			final String title = mTrack.getPurchaseTitle();
			if (title == null || title.isEmpty()) {
				mDownloadButton.setText(R.string.lbl_purchase);
			} else {
				mDownloadButton.setText(title);
			}
		}
	}

	/**
	 * Updates UI properties based on the availability of the song.
	 */
	private void updateTrackAvailability() {
		if (!mTrack.isDownloadable()) {
			mTrackUnavailableView.setVisibility(View.VISIBLE);

			if (mTrack.isPurchasable()) {
				Crouton.showText(this.getActivity(), getString(R.string.track_crouton_unavilable_purchase),
						Style.INFO);
				mTrackUnavailableView.setText(R.string.track_error_unavailable_purchase);
			} else {
				Crouton.showText(this.getActivity(), getString(R.string.track_crouton_unavilable), Style.ALERT);
			}
		}
	}
}
