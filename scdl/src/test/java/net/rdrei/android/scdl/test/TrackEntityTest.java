package net.rdrei.android.scdl.test;


import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import net.rdrei.android.scdl.api.entity.TrackEntity;

import org.junit.Test;

public class TrackEntityTest {
	@Test
	public void testFormattedDuration() {
		TrackEntity entity = new TrackEntity();
		entity.setDuration(121000);
		assertThat(entity.getFormattedDuration(), equalTo("2:01"));
	}

}
