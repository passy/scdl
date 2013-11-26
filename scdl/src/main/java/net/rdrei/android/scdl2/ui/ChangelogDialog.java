package net.rdrei.android.scdl2.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;

import net.rdrei.android.scdl2.R;

import it.gmariotti.changelibs.library.view.ChangeLogListView;
import roboguice.util.Ln;

public class ChangelogDialog extends DialogFragment {

	public static final String FRAGMENT_TAG = "changelog_about";

	public ChangelogDialog() {
		super();
	}

	public static void showDialogForActivity(FragmentActivity activity) {
		FragmentManager fm = activity.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment prev = fm.findFragmentByTag(FRAGMENT_TAG);
		if (prev != null) {
			ft.remove(prev);
		}
		ft.addToBackStack(null);

		new ChangelogDialog().show(ft, FRAGMENT_TAG);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Activity activity = getActivity();
		if (activity == null) {
			Ln.w("Cannot create dialog, activity is null.");
			return super.onCreateDialog(savedInstanceState);
		}

		final LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);

		final ChangeLogListView changelogView = (ChangeLogListView) layoutInflater.inflate(
				R.layout.changelog_view, null);

		return new AlertDialog.Builder(activity).setTitle(R.string.changelog_full_title)
				.setView(changelogView)
				.setPositiveButton(R.string.changelog_ok_button,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						})
				.create();
	}
}
