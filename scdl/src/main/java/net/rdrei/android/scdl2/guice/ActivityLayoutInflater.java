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
	public ActivityLayoutInflater(Activity activity) {
		mInflater = LayoutInflater.from(activity);
	}

	public View inflate(int resource, ViewGroup root) {
		return mInflater.inflate(resource, root);
	}

	public View inflate(XmlPullParser parser, ViewGroup root) {
		return mInflater.inflate(parser, root);
	}

	public View inflate(int resource, ViewGroup root, boolean attachToRoot) {
		return mInflater.inflate(resource, root, attachToRoot);
	}

	public View inflate(XmlPullParser parser, ViewGroup root,
			boolean attachToRoot) {
		return mInflater.inflate(parser, root, attachToRoot);
	}
}
