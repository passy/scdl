package net.rdrei.android.scdl2.ui;

import android.app.AlertDialog;
import android.content.Context;

import net.rdrei.android.scdl2.R;

import javax.inject.Inject;

public class DownloadPreferenceErrorAlertDialogFactory {

	private final Context mContext;

	@Inject
	public DownloadPreferenceErrorAlertDialogFactory(final Context context) {
		mContext = context;
	}

	public AlertDialog newInstance() {
		return new AlertDialog.Builder(mContext)
				.setTitle(mContext.getString(R.string.error_download_fail_title))
				.setMessage(
						mContext.getString(R.string.error_download_fail_msg))
				.create();
	}
}
