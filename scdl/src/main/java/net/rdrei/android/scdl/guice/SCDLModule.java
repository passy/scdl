package net.rdrei.android.scdl.guice;

import net.rdrei.android.scdl.api.SoundcloudApiQuery;
import net.rdrei.android.scdl.api.SoundcloudApiQueryFactory;
import net.rdrei.android.scdl.api.SoundcloudApiQueryImpl;
import net.rdrei.android.scdl.api.URLConnectionFactory;
import net.rdrei.android.scdl.api.URLConnectionFactoryImpl;
import net.rdrei.android.scdl.api.URLWrapper;
import net.rdrei.android.scdl.api.URLWrapperFactory;
import net.rdrei.android.scdl.api.URLWrapperImpl;
import net.rdrei.android.scdl.api.entity.ResolveEntity;

import org.thoughtcrime.ssl.pinning.PinningTrustManager;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class SCDLModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(URLConnectionFactory.class).to(URLConnectionFactoryImpl.class);
		bind(PinningTrustManager.class).toProvider(
				PinningTrustManagerProvider.class);

		install(new FactoryModuleBuilder().implement(URLWrapper.class,
				URLWrapperImpl.class).build(URLWrapperFactory.class));

		// Unfortunately, I haven't figured yet how to use generics for the type
		// specifications here. This is why there needs to be one binding per
		// type for now.
		// This below should work for my understanding, but it doesnt. Thus, the
		// explicit versions further down.
		install(new FactoryModuleBuilder().implement(
				new TypeLiteral<SoundcloudApiQuery<?>>() {
				}, new TypeLiteral<SoundcloudApiQueryImpl<?>>() {
				}).build(new TypeLiteral<SoundcloudApiQueryFactory<?>>() {
		}));

		install(new FactoryModuleBuilder().implement(
				new TypeLiteral<SoundcloudApiQuery<ResolveEntity>>() {
				}, new TypeLiteral<SoundcloudApiQueryImpl<ResolveEntity>>() {
				}).build(
				new TypeLiteral<SoundcloudApiQueryFactory<ResolveEntity>>() {
				}));
	}
}
