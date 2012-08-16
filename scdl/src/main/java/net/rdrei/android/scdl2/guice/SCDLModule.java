package net.rdrei.android.scdl2.guice;

import net.rdrei.android.scdl2.PreferenceManagerWrapper;
import net.rdrei.android.scdl2.PreferenceManagerWrapperFactory;
import net.rdrei.android.scdl2.PreferenceManagerWrapperImpl;
import net.rdrei.android.scdl2.TrackDownloader;
import net.rdrei.android.scdl2.TrackDownloaderFactory;
import net.rdrei.android.scdl2.TrackDownloaderImpl;
import net.rdrei.android.scdl2.api.SoundcloudApiQuery;
import net.rdrei.android.scdl2.api.SoundcloudApiQueryFactory;
import net.rdrei.android.scdl2.api.SoundcloudApiQueryImpl;
import net.rdrei.android.scdl2.api.URLConnectionFactory;
import net.rdrei.android.scdl2.api.URLConnectionFactoryImpl;
import net.rdrei.android.scdl2.api.URLWrapper;
import net.rdrei.android.scdl2.api.URLWrapperFactory;
import net.rdrei.android.scdl2.api.URLWrapperImpl;
import net.rdrei.android.scdl2.api.entity.ResolveEntity;
import net.rdrei.android.scdl2.api.entity.TrackEntity;
import net.rdrei.android.scdl2.ui.DownloadPreferencesDelegate;
import net.rdrei.android.scdl2.ui.DownloadPreferencesDelegateFactory;
import net.rdrei.android.scdl2.ui.DownloadPreferencesDelegateImpl;

import org.thoughtcrime.ssl.pinning.PinningTrustManager;

import android.app.DownloadManager;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class SCDLModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(URLConnectionFactory.class).to(URLConnectionFactoryImpl.class);
		bind(PinningTrustManager.class).toProvider(
				PinningTrustManagerProvider.class);
		bind(DownloadManager.class).toProvider(DownloadManagerProvider.class);

		install(new FactoryModuleBuilder().implement(URLWrapper.class,
				URLWrapperImpl.class).build(URLWrapperFactory.class));

		install(new FactoryModuleBuilder().implement(TrackDownloader.class,
				TrackDownloaderImpl.class).build(TrackDownloaderFactory.class));

		install(new FactoryModuleBuilder().implement(
				DownloadPreferencesDelegate.class,
				DownloadPreferencesDelegateImpl.class).build(
				DownloadPreferencesDelegateFactory.class));
		
		install(new FactoryModuleBuilder().implement(
				PreferenceManagerWrapper.class,
				PreferenceManagerWrapperImpl.class).build(
				PreferenceManagerWrapperFactory.class));

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

		install(new FactoryModuleBuilder().implement(
				new TypeLiteral<SoundcloudApiQuery<TrackEntity>>() {
				}, new TypeLiteral<SoundcloudApiQueryImpl<TrackEntity>>() {
				}).build(
				new TypeLiteral<SoundcloudApiQueryFactory<TrackEntity>>() {
				}));
	}
}
