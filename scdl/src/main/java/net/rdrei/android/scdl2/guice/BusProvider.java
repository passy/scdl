package net.rdrei.android.scdl2.guice;

import roboguice.util.Ln;

import com.google.inject.Provider;
import com.squareup.otto.Bus;

public class BusProvider implements Provider<Bus> {

	@Override
	public Bus get() {
		Ln.d("Creating new Bus instance.");
		return new Bus();
	}

}
