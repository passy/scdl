/**
 * Copyright (C) 2011, Karsten Priegnitz
 *
 * Permission to use, copy, modify, and distribute this piece of software
 * for any purpose with or without fee is hereby granted, provided that
 * the above copyright notice and this permission notice appear in the
 * source code of all copies.
 *
 * It would be appreciated if you mention the author in your change log,
 * contributors list or the like.
 *
 * @author: Karsten Priegnitz
 * @see: http://code.google.com/p/android-change-log/
 */
package sheetrock.panda.changelog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.rdrei.android.scdl.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.WebView;

public class ChangeLog {

	private final Context context;
	private String lastVersion, thisVersion;

	// this is the key for storing the version name in SharedPreferences
	private static final String VERSION_KEY = "PREFS_VERSION_KEY";

	/**
	 * Constructor
	 * 
	 * Retrieves the version names and stores the new version name in
	 * SharedPreferences
	 * 
	 * @param context
	 */
	public ChangeLog(Context context) {
		this(context, PreferenceManager.getDefaultSharedPreferences(context));
	}

	/**
	 * Constructor
	 * 
	 * Retrieves the version names and stores the new version name in
	 * SharedPreferences
	 * 
	 * @param context
	 * @param sp
	 *            the shared preferences to store the last version name into
	 */
	public ChangeLog(Context context, SharedPreferences sp) {
		this.context = context;

		// get version numbers
		this.lastVersion = sp.getString(VERSION_KEY, "");
		Log.d(TAG, "lastVersion: " + lastVersion);
		try {
			this.thisVersion = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			this.thisVersion = "?";
			Log.e(TAG, "could not get version name from manifest!");
			e.printStackTrace();
		}
		Log.d(TAG, "appVersion: " + this.thisVersion);

		// save new version number to preferences
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(VERSION_KEY, this.thisVersion);
		editor.commit();
	}

	/**
	 * @return The version name of the last installation of this app (as
	 *         described in the former manifest). This will be the same as
	 *         returned by <code>getThisVersion()</code> the second time this
	 *         version of the app is launched (more precisely: the second time
	 *         ChangeLog is instantiated).
	 * @see AndroidManifest.xml#android:versionName
	 */
	public String getLastVersion() {
		return this.lastVersion;
	}

	/**
	 * @return The version name of this app as described in the manifest.
	 * @see AndroidManifest.xml#android:versionName
	 */
	public String getThisVersion() {
		return this.thisVersion;
	}

	/**
	 * @return <code>true</code> if this version of your app is started the
	 *         first time
	 */
	public boolean firstRun() {
		return !this.lastVersion.equals(this.thisVersion);
	}

	/**
	 * @return <code>true</code> if your app is started the first time ever.
	 *         Also <code>true</code> if your app was deinstalled and installed
	 *         again.
	 */
	public boolean firstRunEver() {
		return "".equals(this.lastVersion);
	}

	/**
	 * @return an AlertDialog displaying the changes since the previous
	 *         installed version of your app (what's new).
	 */
	public AlertDialog getLogDialog() {
		return this.getDialog(false);
	}

	/**
	 * @return an AlertDialog with a full change log displayed
	 */
	public AlertDialog getFullLogDialog() {
		return this.getDialog(true);
	}

	private AlertDialog getDialog(boolean full) {
		WebView wv = new WebView(this.context);
		wv.setBackgroundColor(0); // transparent
		// wv.getSettings().setDefaultTextEncodingName("utf-8");
		wv.loadDataWithBaseURL(null, this.getLog(full), "text/html", "UTF-8",
				null);

		AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
		builder.setTitle(
				context.getResources().getString(
						full ? R.string.changelog_full_title
								: R.string.changelog_title))
				.setView(wv)
				.setCancelable(false)
				.setPositiveButton(
						context.getResources().getString(
								R.string.changelog_ok_button),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		return builder.create();
	}

	/**
	 * @return HTML displaying the changes since the previous installed version
	 *         of your app (what's new)
	 */
	public String getLog() {
		return this.getLog(false);
	}

	/**
	 * @return HTML which displays full change log
	 */
	public String getFullLog() {
		return this.getLog(true);
	}

	/** modes for HTML-Lists (bullet, numbered) */
	private enum Listmode {
		NONE, ORDERED, UNORDERED,
	};

	private Listmode listMode = Listmode.NONE;
	private StringBuffer sb = null;
	private static final String EOCL = "END_OF_CHANGE_LOG";

	private String getLog(boolean full) {
		// read changelog.txt file
		sb = new StringBuffer();
		try {
			InputStream ins = context.getResources().openRawResource(
					R.raw.changelog);
			BufferedReader br = new BufferedReader(new InputStreamReader(ins));

			String line = null;
			boolean advanceToEOVS = false; // if true: ignore further version
											// sections
			while ((line = br.readLine()) != null) {
				line = line.trim();
				char marker = line.length() > 0 ? line.charAt(0) : 0;
				if (marker == '$') {
					// begin of a version section
					this.closeList();
					String version = line.substring(1).trim();
					// stop output?
					if (!full) {
						if (this.lastVersion.equals(version)) {
							advanceToEOVS = true;
						} else if (version.equals(EOCL)) {
							advanceToEOVS = false;
						}
					}
				} else if (!advanceToEOVS) {
					switch (marker) {
					case '%':
						// line contains version title
						this.closeList();
						sb.append("<div class='title'>"
								+ line.substring(1).trim() + "</div>\n");
						break;
					case '_':
						// line contains version title
						this.closeList();
						sb.append("<div class='subtitle'>"
								+ line.substring(1).trim() + "</div>\n");
						break;
					case '!':
						// line contains free text
						this.closeList();
						sb.append("<div class='freetext'>"
								+ line.substring(1).trim() + "</div>\n");
						break;
					case '#':
						// line contains numbered list item
						this.openList(Listmode.ORDERED);
						sb.append("<li>" + line.substring(1).trim() + "</li>\n");
						break;
					case '*':
						// line contains bullet list item
						this.openList(Listmode.UNORDERED);
						sb.append("<li>" + line.substring(1).trim() + "</li>\n");
						break;
					default:
						// no special character: just use line as is
						this.closeList();
						sb.append(line + "\n");
					}
				}
			}
			this.closeList();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return sb.toString();
	}

	private void openList(Listmode listMode) {
		if (this.listMode != listMode) {
			closeList();
			if (listMode == Listmode.ORDERED) {
				sb.append("<div class='list'><ol>\n");
			} else if (listMode == Listmode.UNORDERED) {
				sb.append("<div class='list'><ul>\n");
			}
			this.listMode = listMode;
		}
	}

	private void closeList() {
		if (this.listMode == Listmode.ORDERED) {
			sb.append("</ol></div>\n");
		} else if (this.listMode == Listmode.UNORDERED) {
			sb.append("</ul></div>\n");
		}
		this.listMode = Listmode.NONE;
	}

	private static final String TAG = "ChangeLog";

	/**
	 * manually set the last version name - for testing purposes only
	 * 
	 * @param lastVersion
	 */
	void setLastVersion(String lastVersion) {
		this.lastVersion = lastVersion;
	}
}
