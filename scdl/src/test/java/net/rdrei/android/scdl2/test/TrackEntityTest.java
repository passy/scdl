package net.rdrei.android.scdl2.test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import net.rdrei.android.scdl2.api.entity.TrackEntity;

import org.junit.Test;

public class TrackEntityTest {
	@Test
	public void testFormattedDuration() {
		TrackEntity entity = new TrackEntity();
		entity.setDuration(121000);
		assertThat(entity.getFormattedDuration(), equalTo("2:01"));
	}

	@Test
	public void testFormattedSize() {
		TrackEntity entity = new TrackEntity();
		entity.setOriginalContentSize((long) (5.16 * 1024 * 1024));
		assertThat(entity.getFormattedSize(), equalTo("5.2MB"));
	}

}
