package net.rdrei.android.scdl2.api;

public class PendingDownload {
	public enum PendingDownloadType { PLAYLIST, TRACK };

	private String mId;
	private PendingDownloadType mType;

	public PendingDownload(String mId, PendingDownloadType mType) {
		this.mId = mId;
		this.mType = mType;
	}

	public String getId() {
		return mId;
	}

	public void setId(String id) {
		mId = id;
	}

	public PendingDownloadType getType() {
		return mType;
	}

	public void setType(PendingDownloadType type) {
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
