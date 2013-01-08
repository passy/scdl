package net.rdrei.android.scdl2.guice;

import android.content.Context;
import android.content.pm.PackageManager;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class PackageManagerProvider implements Provider<PackageManager> {

	@Inject
	private Context mContext;

	@Override
	public PackageManager get() {
		return mContext.getPackageManager();
	}

}
