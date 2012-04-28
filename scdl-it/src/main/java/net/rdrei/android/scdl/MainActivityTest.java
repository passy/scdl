package net.rdrei.android.scdl;

import net.rdrei.android.scdl.ui.MainActivity;
import android.test.ActivityInstrumentationTestCase2;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class net.rdrei.android.scdl.MainActivityTest \
 * net.rdrei.android.scdl.tests/android.test.InstrumentationTestRunner
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public MainActivityTest() {
        super("net.rdrei.android.scdl", MainActivity.class);
    }

}
