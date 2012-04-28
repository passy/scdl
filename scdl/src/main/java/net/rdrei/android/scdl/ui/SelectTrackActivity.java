package net.rdrei.android.scdl.ui;

import net.rdrei.android.scdl.R;
import android.os.Bundle;
import roboguice.activity.RoboActivity;

public class SelectTrackActivity extends RoboActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
	}

}
