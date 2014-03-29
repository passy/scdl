package net.rdrei.android.scdl2.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.squareup.picasso.Picasso;

import net.rdrei.android.scdl2.ApplicationPreferences;
import net.rdrei.android.scdl2.R;
import net.rdrei.android.scdl2.RoboContractFragment;
import net.rdrei.android.scdl2.TrackDownloader;
import net.rdrei.android.scdl2.TrackDownloaderFactory;
import net.rdrei.android.scdl2.api.MediaState;
import net.rdrei.android.scdl2.api.ServiceManager;
import net.rdrei.android.scdl2.api.entity.TrackEntity;
import net.rdrei.android.scdl2.api.service.DownloadService;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import roboguice.inject.InjectView;
import roboguice.util.Ln;
import roboguice.util.RoboAsyncTask;

/**
 * Fragment for downloading a single track, used by DownloadActivity.
 */
public class DownloadTrackFragment extends RoboContractFragment<DownloadMediaContract> {

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

	@Inject
	private Provider<Tracker> mTrackerProvider;

	@Inject
	private TrackDownloaderFactory mDownloaderFactory;

	private TrackEntity mTrack;

	private static String TRACK_TAG = "TRACK_TAG";

	private static String ANALYTICS_TAG = "DOWNLOAD_TRACK_FRAGMENT";

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

		bindButtons();
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
		updateArtwork();
	}

	private void updateArtwork() {
		// It's from a JSON resource we don't control, so let's be safe.
		if (mTrack.getArtworkUrl() != null) {
			Picasso.with(getActivity()).load(mTrack.getArtworkUri()).into(mArtworkImageView);
		}
	}

	private void bindButtons() {
		mDownloadButton.setOnClickListener(new DownloadButtonClickListener());
		mRemoveAdsButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				final Intent intent = new Intent(getActivity(), BuyAdFreeActivity.class);
				startActivity(intent);
			}
		});
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
				Crouton.showText(this.getActivity(),
						getString(R.string.track_crouton_unavilable_purchase), Style.INFO);
				mTrackUnavailableView.setText(R.string.track_error_unavailable_purchase);
			} else {
				Crouton.showText(this.getActivity(), getString(R.string.track_crouton_unavilable),
						Style.ALERT);
			}
		}
	}

	private void downloadTrack(final Uri uri) throws Exception {
		// Download using TrackDownloader
		final Handler handler = new Handler(new DownloadHandlerCallback());
		final TrackDownloader downloader = mDownloaderFactory.create(uri, mTrack, handler);
		downloader.enqueue();

		Toast.makeText(getActivity(), getString(R.string.toast_download_started),
				Toast.LENGTH_SHORT).show();
	}

	private class DownloadTask extends RoboAsyncTask<Uri> {
		@Inject
		private ServiceManager mServiceManager;

		private final String mId;

		protected DownloadTask(final String id) {
			super(getActivity());
			mId = id;
		}

		@Override
		public Uri call() throws Exception {
			final DownloadService service = mServiceManager.downloadService();
			return service.resolveUri(mId);
		}

		@Override
		protected void onSuccess(final Uri t) throws Exception {
			super.onSuccess(t);

			Ln.d("Resolved download URL: %s", t);
			downloadTrack(t);
		}
	}

	private class DownloadHandlerCallback implements Handler.Callback {

		@Override
		public boolean handleMessage(final Message msg) {
			final TrackErrorActivity.ErrorCode errorCode;

			if (msg.what == TrackDownloader.MSG_DOWNLOAD_STORAGE_ERROR) {
				errorCode = TrackErrorActivity.ErrorCode.NO_WRITE_PERMISSION;
			} else {
				errorCode = TrackErrorActivity.ErrorCode.UNKNOWN_ERROR;
			}

			mTrackerProvider.get()
					.send(new HitBuilders.ExceptionBuilder()
									.setDescription("Track Download Error")
									.setFatal(false)
									.set("WHAT", String.valueOf(msg.what))
									.build()
					);

			getContract().handleFatalError(errorCode);
			return true;
		}
	}

	private class DownloadButtonClickListener implements View.OnClickListener {
		private void startDownload() {
			final DownloadTask task = new DownloadTask(String.valueOf(mTrack.getId()));
			task.execute();

			mTrackerProvider.get()
					.send(new HitBuilders.EventBuilder()
							.setCategory(ANALYTICS_TAG)
							.setAction("DOWNLOAD")
							.setLabel(mTrack.getTitle())
							.set("ID", String.valueOf(mTrack.getId()))
							.build()
					);
		}

		private void startPurchase() {
			Uri uri = Uri.parse(mTrack.getPurchaseUrl());

			mTrackerProvider.get()
					.send(new HitBuilders.EventBuilder()
									.setCategory(ANALYTICS_TAG)
									.setAction("PURCHASE")
									.setLabel(mTrack.getTitle())
									.set("ID", String.valueOf(mTrack.getId()))
									.set("URI", uri.toString())
									.build()
					);

			final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		}

		@Override
		public void onClick(final View v) {
			if (mTrack.isDownloadable()) {
				startDownload();
				mDownloadButton.setEnabled(false);
			} else if (mTrack.isPurchasable()) {
				startPurchase();
				// Keep purchase button enabled.
			}
		}
	}
}
