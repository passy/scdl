package net.rdrei.android.scdl.ui;

import net.rdrei.android.scdl.R;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

		View view = inflater.inflate(getLayoutId(), container, false);
		
		final View nextButton = view.findViewById(R.id.btn_next);
		if (nextButton != null) {
			bindNextButton(nextButton);
		}

		final View startButton = view.findViewById(R.id.btn_start);
		if (startButton != null) {
			bindStartButton(startButton);
		}
		
		return view;
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

	/**
	 * Fix for bug described here:
	 * http://stackoverflow.com/questions/8748064/starting-activity-from-fragment-causes-nullpointerexception
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		setUserVisibleHint(true);
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
