package net.rdrei.android.scdl2.guice;

import org.xmlpull.v1.XmlPullParser;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.inject.Inject;

/**
 * Provides a strict subset of {@link LayoutInflater} that is always injected
 * with a Activity context.
 * 
 * @author pascal
 * 
 */
public class ActivityLayoutInflater {
	private final LayoutInflater mInflater;

	@Inject
	public ActivityLayoutInflater(final Activity activity) {
		mInflater = LayoutInflater.from(activity);
	}

	public View inflate(final int resource, final ViewGroup root) {
		return mInflater.inflate(resource, root);
	}

	public View inflate(final XmlPullParser parser, final ViewGroup root) {
		return mInflater.inflate(parser, root);
	}

	public View inflate(final int resource, final ViewGroup root,
			final boolean attachToRoot) {
		return mInflater.inflate(resource, root, attachToRoot);
	}

	public View inflate(final XmlPullParser parser, final ViewGroup root,
			final boolean attachToRoot) {
		return mInflater.inflate(parser, root, attachToRoot);
	}
}
