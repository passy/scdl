package net.rdrei.android.scdl.ui;

import net.rdrei.android.scdl.R;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A single slide in the demo pager that is used as a tutorial.
 * 
 * @author pascal
 * 
 */
public class DemoFragment extends Fragment {

	private static String KEY_LAYOUT_ID = "layout_id";
	private OnDemoActionListener mDemoActionListener;

	public static DemoFragment newInstance(int layoutId) {
		DemoFragment fragment = new DemoFragment();

		Bundle bundle = new Bundle();
		bundle.putInt(KEY_LAYOUT_ID, layoutId);
		fragment.setArguments(bundle);

		return fragment;
	}

	private int getLayoutId() {
		return getArguments().getInt(KEY_LAYOUT_ID);
	}

	/**
	 * Load the given demo layout.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return inflater.inflate(getLayoutId(), container, false);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			mDemoActionListener = (OnDemoActionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnDemoActionListener");
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		final FragmentActivity activity = getActivity();
		final View nextButton = activity.findViewById(R.id.btn_next);
		if (nextButton != null) {
			bindNextButton(nextButton);
		}

		final View startButton = activity.findViewById(R.id.btn_start);
		if (startButton != null) {
			bindStartButton(startButton);
		}
	}

	private void bindStartButton(View startButton) {
		startButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mDemoActionListener.onStartSoundcloud();
			}
		});
	}

	private void bindNextButton(View nextButton) {
		nextButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mDemoActionListener.onNextPage();
			}
		});
	}
}
