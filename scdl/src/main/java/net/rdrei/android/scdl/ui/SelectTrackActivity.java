package net.rdrei.android.scdl.ui;

import net.rdrei.android.scdl.R;
import net.rdrei.android.scdl.ShareIntentResolver;
import net.rdrei.android.scdl.api.ServiceManager;
import net.rdrei.android.scdl.api.entity.TrackEntity;
import net.rdrei.android.scdl.api.service.TrackService;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import roboguice.util.Ln;
import roboguice.util.RoboAsyncTask;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;

public class SelectTrackActivity extends RoboActivity {

	@InjectView(R.id.title)
	private TextView mTitleView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.select_track);
		
		final TrackLoaderTask task = new TrackLoaderTask(this);
		task.execute();
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
			mTitleView.setText(t.getTitle());
		}

		@Override
		public TrackEntity call() throws Exception {
			String id = mShareIntentResolver.resolveId();
			TrackService trackService = mServiceManager.trackService();
			return trackService.getTrack(id);
		}
	}
}
