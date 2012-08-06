package net.rdrei.android.scdl.test;

import net.rdrei.android.scdl.ui.SelectTrackActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(TestRunner.class)
public class SelectActivityTest {
	
	@Test
	public void smokeTestOnCreate() {
		SelectTrackActivity activity = new SelectTrackActivity();
		activity.onCreate(null);
	}
}