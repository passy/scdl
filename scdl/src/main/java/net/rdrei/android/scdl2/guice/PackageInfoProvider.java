package net.rdrei.android.scdl2.guice;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class PackageInfoProvider implements Provider<PackageInfo> {
	@Inject
	private Provider<PackageManager> mPackageManagerProvider;

	@Inject
	private Context mContext;

	@Override
	public PackageInfo get() {
		try {
			return mPackageManagerProvider.get()
					.getPackageInfo(mContext.getPackageName(), PackageManager.GET_META_DATA);
		} catch (PackageManager.NameNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
