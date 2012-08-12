package net.rdrei.android.scdl.test;

import net.rdrei.android.scdl.ui.SelectTrackActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(TestRunner.class)
public class SelectActivityTest {

	@Test
	public void smokeTestOnCreate() {
		new SelectTrackActivity();
		// Can't inflate the layout, at the moment, because it can't extract the
		// admob_unit_id.
		// activity.onCreate(null);
	}
}