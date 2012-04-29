package net.rdrei.android.scdl.guice;

import net.rdrei.android.scdl.api.URLConnectionFactory;
import net.rdrei.android.scdl.api.URLConnectionFactoryImpl;
import net.rdrei.android.scdl.api.URLWrapper;
import net.rdrei.android.scdl.api.URLWrapperFactory;
import net.rdrei.android.scdl.api.URLWrapperImpl;

import org.thoughtcrime.ssl.pinning.PinningTrustManager;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class SCDLModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(URLConnectionFactory.class).to(URLConnectionFactoryImpl.class);
		bind(PinningTrustManager.class).toProvider(
				PinningTrustManagerProvider.class);

		install(new FactoryModuleBuilder().implement(URLWrapper.class,
				URLWrapperImpl.class).build(URLWrapperFactory.class));
	}

}
