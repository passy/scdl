package net.rdrei.android.scdl2;

import android.app.Application;

import org.robolectric.TestLifecycleApplication;

import java.lang.reflect.Method;

public class TestSCDLApplication extends Application implements TestLifecycleApplication {
    @Override
    public void beforeTest(Method method) {

    }

    @Override
    public void prepareTest(Object test) {

    }

    @Override
    public void afterTest(Method method) {

    }
}
