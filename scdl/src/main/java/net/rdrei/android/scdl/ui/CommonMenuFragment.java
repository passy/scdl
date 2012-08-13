package net.rdrei.android.scdl.ui;

import net.rdrei.android.scdl.R;
import sheetrock.panda.changelog.ChangeLog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class CommonMenuFragment extends Fragment {
	public static CommonMenuFragment newInstance() {
		CommonMenuFragment fragment = new CommonMenuFragment();

		return fragment;
	}

	public static void injectMenu(FragmentActivity mActivity) {
		CommonMenuFragment menuFragment = CommonMenuFragment.newInstance();

		mActivity.getSupportFragmentManager().beginTransaction()
				.add(menuFragment, "MENU").commit();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		inflater.inflate(R.menu.main, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.preferences) {
			final Intent intent = new Intent(this.getActivity(),
					ApplicationPreferencesActivity.class);
			startActivity(intent);
			return true;
		} else if (item.getItemId() == R.id.changelog) {
			ChangeLog changeLog = new ChangeLog(this.getActivity());
			changeLog.getFullLogDialog().show();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
}