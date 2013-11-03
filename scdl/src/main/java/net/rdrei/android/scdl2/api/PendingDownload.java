package net.rdrei.android.scdl2.api;

public class PendingDownload {
	private String mId;
	private MediaDownloadType mType;

	public PendingDownload(String mId, MediaDownloadType mType) {
		this.mId = mId;
		this.mType = mType;
	}

	public String getId() {
		return mId;
	}

	public void setId(String id) {
		mId = id;
	}

	public MediaDownloadType getType() {
		return mType;
	}

	public void setType(MediaDownloadType type) {
		mType = type;
	}

	@Override
	public String toString() {
		return "PendingDownload{" +
				"mId='" + mId + '\'' +
				", mType=" + mType +
				'}';
	}
}
