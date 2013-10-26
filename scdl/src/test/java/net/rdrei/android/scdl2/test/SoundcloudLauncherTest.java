package net.rdrei.android.scdl2.test;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import net.rdrei.android.scdl2.ui.SoundcloudInstallAsker;
import net.rdrei.android.scdl2.ui.SoundcloudLauncher;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class SoundcloudLauncherTest {
    @Mock
    PackageManager mPackageManager;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSoundcloudInstalled() {
        final PackageManager packageManager = mock(PackageManager.class);
        final Context context = mock(Context.class);
        final Intent launchIntent = new Intent();

        when(packageManager.getLaunchIntentForPackage("com.soundcloud.android"))
                .thenReturn(launchIntent);
        SoundcloudLauncher launcher = new SoundcloudLauncher(context,
                packageManager, new SoundcloudInstallAsker(context));

        assertThat(launcher.isInstalled(), is(true));
    }

    @Test
    public void testSoundcloudNotInstalled() {
        final PackageManager packageManager = mock(PackageManager.class);
        final Context context = mock(Context.class);
        // !
        final Intent launchIntent = null;

        when(packageManager.getLaunchIntentForPackage("com.soundcloud.android"))
                .thenReturn(launchIntent);
        SoundcloudLauncher launcher = new SoundcloudLauncher(context,
                packageManager, new SoundcloudInstallAsker(context));

        assertThat(launcher.isInstalled(), is(false));
    }

    @Test
    public void testSoundcloudLaunchIfInstalled() {
        final PackageManager packageManager = mock(PackageManager.class);
        final Activity context = mock(Activity.class);
        final Intent launchIntent = new Intent();

        when(packageManager.getLaunchIntentForPackage("com.soundcloud.android"))
                .thenReturn(launchIntent);
        SoundcloudLauncher launcher = new SoundcloudLauncher(context,
                packageManager, new SoundcloudInstallAsker(context));
        launcher.launch();

        verify(context).startActivity(launchIntent);
    }

    @Test
    public void testSoundcloudActivityRegression() {
        final PackageManager packageManager = mock(PackageManager.class);
        final Context context = mock(Context.class);
        final Intent launchIntent = new Intent("com.example.doesnotexist");
        final SoundcloudInstallAsker asker = mock(SoundcloudInstallAsker.class);

        // Very much mockery going in here. Idea is:
        // - package manager returns a valid launchIntent
        // - startActivity for some reason throws ActivityNotFoundException
        // - then treat it as not installed (instead of crashing)
        when(packageManager.getLaunchIntentForPackage("com.soundcloud.android"))
                .thenReturn(launchIntent);
        Mockito.doNothing().when(asker).ask();
        Mockito.doThrow(ActivityNotFoundException.class).when(context).startActivity(launchIntent);

        final SoundcloudLauncher launcher = new SoundcloudLauncher(context,
                packageManager, asker);
        launcher.launch();

        verify(asker).ask();
    }
}
