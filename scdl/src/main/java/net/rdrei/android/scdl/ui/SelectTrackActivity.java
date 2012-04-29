package net.rdrei.android.scdl.ui;

import net.rdrei.android.scdl.R;
import roboguice.activity.RoboActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SelectTrackActivity extends RoboActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		String url = extras.getString(Intent.EXTRA_TEXT, null);
		
		// TODO: Not like that, obviously.
		assert url != null;
		
		View view = findViewById(R.id.textView1);
		((TextView)view).setText("URL: " + url);
		// Resolve first with http://api.soundcloud.com/resolve.json?url=http://soundcloud.com/johann_schwarz/johann-schwarz-spring-into&client_id=429caab2811564cb27f52a7a4964269b
	}

}
