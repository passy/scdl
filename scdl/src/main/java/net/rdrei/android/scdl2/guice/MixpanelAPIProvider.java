package net.rdrei.android.scdl2.guice;

import net.rdrei.android.scdl2.Config;

import com.google.inject.Provider;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

public class MixpanelAPIProvider implements Provider<MixpanelAPI> {
	@Override
	public PinningTrustManager get() {
		return MixpanelAPI.getInstance(this, Config.MIXPANEL_API_TOKEN);
	}
}

