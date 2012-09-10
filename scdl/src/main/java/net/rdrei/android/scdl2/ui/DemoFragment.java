package net.rdrei.android.scdl2.ui;

import net.rdrei.android.scdl2.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A single slide in the demo pager that is used as a tutorial.
 * 
 * @author pascal
 * 
 */
public class DemoFragment extends
		ContractFragment<DemoFragment.DemoActionListenerContract> {

	private static final String KEY_LAYOUT_ID = "layout_id";

	public static interface DemoActionListenerContract {
		void onNextPage();

		void onStartSoundcloud();
	}

	public static DemoFragment newInstance(final int layoutId) {
		final DemoFragment fragment = new DemoFragment();

		final Bundle bundle = new Bundle();
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
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {

		final View view = inflater.inflate(getLayoutId(), container, false);

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

	/**
	 * Fix for bug described here:
	 * http://stackoverflow.com/questions/8748064/starting
	 * -activity-from-fragment-causes-nullpointerexception
	 */
	@Override
	public void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);
		setUserVisibleHint(true);
	}

	private void bindStartButton(final View startButton) {
		startButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v) {
				getContract().onStartSoundcloud();
			}
		});
	}

	private void bindNextButton(final View nextButton) {
		nextButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v) {
				getContract().onNextPage();
			}
		});
	}
}
