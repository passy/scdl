package net.rdrei.android.scdl.ui;

import java.net.URL;

import net.rdrei.android.scdl.R;
import net.rdrei.android.scdl.ShareIntentResolver;
import net.rdrei.android.scdl.api.ServiceManager;
import net.rdrei.android.scdl.api.entity.TrackEntity;
import net.rdrei.android.scdl.api.service.TrackService;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import roboguice.util.Ln;
import roboguice.util.RoboAsyncTask;
import roboguice.util.SafeAsyncTask;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;

public class SelectTrackActivity extends RoboActivity {

	@InjectView(R.id.title)
	private TextView mTitleView;

	@InjectView(R.id.progress_bar)
	private View mProgressBarView;

	@InjectView(R.id.btn_download)
	private Button mDownloadButton;

	@InjectView(R.id.btn_cancel)
	private Button mCancelButton;

	@InjectView(R.id.img_artwork)
	private ImageView mArtworkImageView;

	private TrackEntity mTrack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.select_track);

		final TrackLoaderTask task = new TrackLoaderTask(this);
		task.execute();

		bindButtons();
	}

	private void bindButtons() {
		mDownloadButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					downloadTrack();
				} catch (Exception e) {
					Ln.e(e);
					throw new IllegalStateException(e);
				}
			}

		});

		mCancelButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Ln.d("Canceling download. Bye, bye!");
				finish();
			}
		});
	}

	protected void downloadTrack() throws Exception {
		if (mTrack == null) {
			throw new NullPointerException("Tried downloading track that "
					+ "wasn't resolved, yet!");
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
				Ln.d("Starting download of %s.", mTrack.getDownloadUrl());
				Request request = new Request(mTrack.getDownloadUri());
				request.setTitle(mTrack.getTitle());

				downloadManager.enqueue(request);
			}
		}).start();
		Toast.makeText(SelectTrackActivity.this, "Download started.",
				Toast.LENGTH_SHORT).show();
	}

	protected void updateTrackDisplay() {
		mTitleView.setText(mTrack.getTitle());
		mProgressBarView.setVisibility(View.GONE);
		mDownloadButton.setEnabled(true);

		ArtworkLoaderTask artworkLoaderTask = new ArtworkLoaderTask(
				mTrack.getArtworkUrl());
		artworkLoaderTask.execute();
	}

	public class TrackLoaderTask extends RoboAsyncTask<TrackEntity> {

		@Inject
		private ShareIntentResolver mShareIntentResolver;

		@Inject
		private ServiceManager mServiceManager;

		protected TrackLoaderTask(Context context) {
			super(context);
		}

		@Override
		protected void onException(Exception e) throws RuntimeException {
			super.onException(e);
			Ln.e(e);
			Ln.e("Error during resolving track: %s", e.toString());

			Toast.makeText(getContext(), "ERROR: " + e.toString(),
					Toast.LENGTH_LONG).show();
		}

		@Override
		protected void onSuccess(TrackEntity t) throws Exception {
			super.onSuccess(t);
			mTrack = t;
			updateTrackDisplay();
		}

		@Override
		public TrackEntity call() throws Exception {
			String id = mShareIntentResolver.resolveId();
			TrackService trackService = mServiceManager.trackService();
			return trackService.getTrack(id);
		}
	}

	private class ArtworkLoaderTask extends SafeAsyncTask<Drawable> {

		private String mUrlStr;

		public ArtworkLoaderTask(String url) {
			super();

			mUrlStr = url;
		}

		@Override
		public Drawable call() throws Exception {
			URL artworkURL = new URL(mUrlStr);
			return Drawable.createFromStream(artworkURL.openStream(), null);
		}

		@Override
		protected void onSuccess(Drawable t) throws Exception {
			super.onSuccess(t);

			mArtworkImageView.setImageDrawable(t);
		}
	}
}
