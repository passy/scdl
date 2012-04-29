package net.rdrei.android.scdl.api.entity;

import com.google.gson.annotations.SerializedName;

import net.rdrei.android.scdl.api.SoundcloudEntity;

/**
 * Entity tracking some of the Track information.
 * 
 * TODO: Add and display user information.
 * 
 * @author pascal
 * 
 */
public class TrackEntity implements SoundcloudEntity {
	private static final long serialVersionUID = 1L;

	private long id;
	private long duration;
	private String title;
	private boolean downloadable;

	@SerializedName("download_url")
	private String downloadUrl;
	@SerializedName("artwork_url")
	private String artworkUrl;
	private String description;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isDownloadable() {
		return downloadable;
	}

	public void setDownloadable(boolean downloadable) {
		this.downloadable = downloadable;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getArtworkUrl() {
		return artworkUrl;
	}

	public void setArtworkUrl(String artworkUrl) {
		this.artworkUrl = artworkUrl;
	}
}
