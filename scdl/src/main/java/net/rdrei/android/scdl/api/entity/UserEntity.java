package net.rdrei.android.scdl.api.entity;

import net.rdrei.android.scdl.api.SoundcloudEntity;

public class UserEntity implements SoundcloudEntity {
	private static final long serialVersionUID = 1L;

	private long id;
	private String username;
	private String uri;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

}
