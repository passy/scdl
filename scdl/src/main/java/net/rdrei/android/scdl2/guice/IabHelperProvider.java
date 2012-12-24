package net.rdrei.android.scdl2.guice;

import net.rdrei.android.scdl2.R;
import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.android.vending.billing.IabHelper;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class IabHelperProvider implements Provider<IabHelper> {

	@Inject
	private Context mContext;

	@Override
	public IabHelper get() {
		final IabHelper helper = new IabHelper(mContext,
				mContext.getString(R.string.iab_pubkey));
		
		if ((mContext.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
			helper.enableDebugLogging(true);
		}

		return helper;
	}

}
