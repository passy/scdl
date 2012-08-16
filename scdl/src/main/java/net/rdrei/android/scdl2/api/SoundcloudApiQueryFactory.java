package net.rdrei.android.scdl2.api;

import net.rdrei.android.scdl2.api.SoundcloudApiQuery.HttpMethod;

import com.google.gson.reflect.TypeToken;

public interface SoundcloudApiQueryFactory<T extends SoundcloudEntity> {
	SoundcloudApiQuery<T> create(URLWrapper url, HttpMethod method,
			TypeToken<T> typeToken);
}