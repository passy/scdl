package net.rdrei.android.scdl.guice;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class SharedPreferencesProvider implements Provider<SharedPreferences> {
	
	@Inject
	private Context mContext;

	@Override
	public SharedPreferences get() {
		return PreferenceManager.getDefaultSharedPreferences(mContext);
	}
	
	

}
