package net.rdrei.android.scdl2.api.entity;

import net.rdrei.android.scdl2.api.SoundcloudEntity;

public class ResolveEntity implements SoundcloudEntity {
	private static final long serialVersionUID = 1L;

	private String status;
	private String location;

	public String getStatus() {
		return status;
	}

	public String getLocation() {
		return location;
	}

	public void setStatus(final String status) {
		this.status = status;
	}

	public void setLocation(final String location) {
		this.location = location;
	}
}