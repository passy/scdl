package net.rdrei.android.scdl2.ui;

import java.net.URL;

import net.rdrei.android.scdl2.ApplicationPreferences;
import net.rdrei.android.scdl2.R;
import net.rdrei.android.scdl2.ShareIntentResolver;
import net.rdrei.android.scdl2.ShareIntentResolver.TrackNotFoundException;
import net.rdrei.android.scdl2.ShareIntentResolver.UnsupportedUrlException;
import net.rdrei.android.scdl2.TrackDownloader;
import net.rdrei.android.scdl2.TrackDownloaderFactory;
import net.rdrei.android.scdl2.api.ServiceManager;
import net.rdrei.android.scdl2.api.entity.TrackEntity;
import net.rdrei.android.scdl2.api.service.DownloadService;
import net.rdrei.android.scdl2.api.service.TrackService;
import net.rdrei.android.scdl2.ui.TrackErrorActivity.ErrorCode;
import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.ContextScope;
import roboguice.inject.InjectView;
import roboguice.util.Ln;
import roboguice.util.RoboAsyncTask;
import roboguice.util.SafeAsyncTask;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;

public class SelectTrackActivity extends RoboFragmentActivity {

	private static final String STATE_TRACK = "scdl:TRACK";

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

	@InjectView(R.id.main_layout)
	private ViewGroup mMainLayout;

	@Inject
	private TrackDownloaderFactory mDownloaderFactory;

	@Inject
	private AdViewManager mAdViewManager;
	
	@Inject
	private ApplicationPreferences mPreferences;

	private TrackEntity mTrack;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.select_track);

		if (savedInstanceState != null) {
			Ln.d("Loading previous track record.");
			mTrack = savedInstanceState.getParcelable(STATE_TRACK);
		} else {
			CommonMenuFragment.injectMenu(this);
		}

		if (mTrack == null) {
			Ln.d("mTrack is null. Starting resolving task.");
			final TrackResolverTask task = new TrackResolverTask(this);
			task.execute();
		} else {
			Ln.d("mTrack has been restored. Updating display.");
			updateTrackDisplay();
		}

		bindButtons();
		
		if (mPreferences.isAdFree()) {
			mRemoveAdsButton.setVisibility(View.GONE);
		}
		mAdViewManager.addToViewIfRequired(mMainLayout);
	}

	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);

		if (mTrack != null) {
			Ln.d("Saving instance state for track.");
			outState.putParcelable(STATE_TRACK, mTrack);
		}
	}

	private void bindButtons() {
		mDownloadButton.setOnClickListener(new DownloadButtonClickListener());
		mRemoveAdsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				final Intent intent = new Intent(SelectTrackActivity.this,
						BuyAdFreeActivity.class);
				startActivity(intent);
			}
		});
	}

	/**
	 * Show error activity with the given error code and exit the current
	 * activity.
	 * 
	 * @param errorCode
	 */
	protected void startErrorActivity(
			final TrackErrorActivity.ErrorCode errorCode) {
		final Intent intent = new Intent(this, TrackErrorActivity.class);
		intent.putExtra(TrackErrorActivity.EXTRA_ERROR_CODE, errorCode);
		startActivity(intent);
		finish();
	}

	protected void downloadTrack(final Uri uri) throws Exception {
		// Download using TrackDownloader
		final Handler handler = new Handler(new DownloadHandlerCallback());
		final TrackDownloader downloader = mDownloaderFactory.create(uri,
				mTrack, handler);
		downloader.enqueue();

		Toast.makeText(SelectTrackActivity.this, "Download started.",
				Toast.LENGTH_SHORT).show();
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

		final ArtworkLoaderTask artworkLoaderTask = new ArtworkLoaderTask(
				mTrack.getArtworkUrl());
		artworkLoaderTask.execute();
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
				mTrackUnavailableView
						.setText(R.string.track_error_unavailable_purchase);
			}
		}
	}

	private class DownloadButtonClickListener implements View.OnClickListener {
		private void startDownload() {
			final DownloadTask task = new DownloadTask(
					SelectTrackActivity.this, String.valueOf(mTrack.getId()));
			task.execute();
		}

		private void startPurchase() {
			Uri uri = Uri.parse(mTrack.getPurchaseUrl());

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
				// Keep download button enabled.
			}
		}
	}

	/**
	 * Resolves a track to its id.
	 * 
	 * TODO: Errors in here must be tracked and should end the current activity
	 * (either error activity or just popup dialog).
	 * 
	 * @author pascal
	 * 
	 */
	public class TrackResolverTask extends RoboAsyncTask<String> {

		protected TrackResolverTask(final Context context) {
			super(context);
		}

		@Inject
		private ShareIntentResolver mShareIntentResolver;

		@Inject
		private ContextScope mContextScope;

		@Override
		public String call() throws Exception {
			mContextScope.enter(context);
			try {
				return mShareIntentResolver.resolveId();
			} finally {
				mContextScope.exit(context);
			}
		}

		@Override
		protected void onException(final Exception e) throws RuntimeException {
			super.onException(e);

			if (e instanceof UnsupportedUrlException) {
				startErrorActivity(ErrorCode.UNSUPPORTED_URL);
			} else if (e instanceof TrackNotFoundException) {
				startErrorActivity(ErrorCode.NOT_FOUND);
			} else {
				startErrorActivity(ErrorCode.NETWORK_ERROR);
			}
		}

		@Override
		protected void onSuccess(final String id) throws Exception {
			super.onSuccess(id);

			Ln.d("Resolved track to id %s. Starting further API calls.", id);
			final TrackLoaderTask trackLoaderTask = new TrackLoaderTask(
					context, id);
			trackLoaderTask.execute();
		}
	}

	public class TrackLoaderTask extends RoboAsyncTask<TrackEntity> {
		@Inject
		private ServiceManager mServiceManager;

		@Inject
		private ContextScope mContextScope;

		private final String mId;

		protected TrackLoaderTask(final Context context, final String id) {
			super(context);
			mId = id;
		}

		@Override
		protected void onException(final Exception e) throws RuntimeException {
			super.onException(e);
			Ln.e("Error during resolving track: %s", e.toString());

			Toast.makeText(getContext(), "ERROR: " + e.toString(),
					Toast.LENGTH_LONG).show();
		}

		@Override
		protected void onSuccess(final TrackEntity t) throws Exception {
			super.onSuccess(t);
			mTrack = t;
			updateTrackDisplay();
		}

		@Override
		public TrackEntity call() throws Exception {
			mContextScope.enter(context);
			try {
				final TrackService trackService = mServiceManager
						.trackService();
				return trackService.getTrack(mId);
			} finally {
				mContextScope.exit(context);
			}
		}
	}

	private class ArtworkLoaderTask extends SafeAsyncTask<Drawable> {

		private final String mUrlStr;

		public ArtworkLoaderTask(final String url) {
			super();

			mUrlStr = url;
		}

		@Override
		public Drawable call() throws Exception {
			final URL artworkURL = new URL(mUrlStr);
			return Drawable.createFromStream(artworkURL.openStream(), null);
		}

		@Override
		protected void onSuccess(final Drawable t) throws Exception {
			super.onSuccess(t);

			mArtworkImageView.setImageDrawable(t);
		}
	}

	private class DownloadTask extends RoboAsyncTask<Uri> {
		@Inject
		private ServiceManager mServiceManager;

		private final String mId;

		protected DownloadTask(final Context context, final String id) {
			super(context);
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
			final Intent intent = new Intent(SelectTrackActivity.this,
					TrackErrorActivity.class);
			final ErrorCode errorCode;

			if (msg.what == TrackDownloader.MSG_DOWNLOAD_STORAGE_ERROR) {
				errorCode = ErrorCode.NO_WRITE_PERMISSION;
			} else {
				errorCode = ErrorCode.UNKNOWN_ERROR;
			}

			intent.putExtra(TrackErrorActivity.EXTRA_ERROR_CODE, errorCode);
			startActivity(intent);
			return true;
		}
	}
}
