package net.rdrei.android.scdl.api.entity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

import net.rdrei.android.scdl.Config;
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

	@SerializedName("original_format")
	private String originalFormat;

	private String description;
	private String permalink;

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

	/**
	 * Get the download URL as string, including client id parameter.
	 * 
	 * @return
	 */
	public String getDownloadUrl() {
		final StringBuffer buffer = new StringBuffer();
		buffer.append(downloadUrl);
		buffer.append("?client_id=");
		buffer.append(Config.API_CONSUMER_KEY);
		return buffer.toString();
	}

	public Uri getDownloadUri() {
		return Uri.parse(downloadUrl);
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

	public Uri getArtworkUri() {
		return Uri.parse(artworkUrl);
	}

	public void setArtworkUrl(String artworkUrl) {
		this.artworkUrl = artworkUrl;
	}

	/**
	 * @return the originalFormat
	 */
	public String getOriginalFormat() {
		return originalFormat;
	}

	/**
	 * @param originalFormat
	 *            the originalFormat to set
	 */
	public void setOriginalFormat(String originalFormat) {
		this.originalFormat = originalFormat;
	}

	public String getDownloadFilename() {
		return permalink + "." + originalFormat;
	}

	public String getPermalink() {
		return permalink;
	}

	public void setPermalink(String permalink) {
		this.permalink = permalink;
	}

	public String getFormattedDuration() {
		final SimpleDateFormat formatter = new SimpleDateFormat("m:ss");
		final Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(getDuration());
		return formatter.format(calendar.getTime());
	}
}
