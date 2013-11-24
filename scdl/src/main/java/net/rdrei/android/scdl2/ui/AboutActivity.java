package net.rdrei.android.scdl2.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.inject.Inject;

import net.rdrei.android.scdl2.R;

import roboguice.activity.RoboActivity;
import roboguice.activity.RoboFragmentActivity;
import roboguice.fragment.RoboFragment;

public class AboutActivity extends RoboFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new AboutContentFragment())
					.commit();
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class AboutContentFragment extends RoboFragment {

		@Inject
		private PackageInfo mPackageInfo;

		public AboutContentFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_about, container, false);

			TextView versionTextView = (TextView) rootView.findViewById(R.id.version_number);
			versionTextView.setText(mPackageInfo.versionName);
			return rootView;
		}
	}

}
