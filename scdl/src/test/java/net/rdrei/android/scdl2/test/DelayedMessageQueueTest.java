package net.rdrei.android.scdl2.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import net.rdrei.android.mediator.DelayedMessageQueue;
import net.rdrei.android.mediator.DelayedMessageQueue.Handler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import android.os.Message;

import com.google.inject.Inject;

@RunWith(RobolectricTestRunner.class)
public class DelayedMessageQueueTest {
	
	@Inject
	private DelayedMessageQueue mQueue;
	
	private static final String KEY_ZERO = "ZERO";
	private static final String KEY_ONE = "ONE";
	
	@Before
	public void inject() {
		TestHelper.getInjector().injectMembers(this);
	}
	
	@Test
	public void testAsyncReceival() {
		final Counter counter = new Counter();
		mQueue.send(KEY_ZERO, Message.obtain(null, 1));
		mQueue.send(KEY_ZERO, Message.obtain(null, 2));
		
		mQueue.setHandler(KEY_ZERO, new Handler() {
			public void handleMessage(Message msg) {
				counter.inc();
				
				assertThat(msg.what, is(counter.getCount()));
			}
		});
		
		assertThat(counter.getCount(), is(2));
	}
	
	@Test
	public void testSyncReceival() {
		final Counter counter = new Counter();
		
		mQueue.setHandler(KEY_ZERO, new Handler() {
			public void handleMessage(Message msg) {
				counter.inc();
				
				assertThat(msg.what, is(counter.getCount()));
			}
		});
		
		mQueue.send(KEY_ZERO, Message.obtain(null, 1));
		mQueue.send(KEY_ZERO, Message.obtain(null, 2));
		
		assertThat(counter.getCount(), is(2));
	}
	
	@Test
	public void testMixedReceival() {
		final Counter counter = new Counter();
		
		mQueue.send(KEY_ZERO, Message.obtain(null, 1));
		
		mQueue.setHandler(KEY_ZERO, new Handler() {
			public void handleMessage(Message msg) {
				counter.inc();
				
				assertThat(msg.what, is(counter.getCount()));
			}
		});
		
		mQueue.send(KEY_ZERO, Message.obtain(null, 2));
		assertThat(counter.getCount(), is(2));
	}
	
	public void testDispatch() {
		final Counter counter = new Counter();
		
		mQueue.send(KEY_ONE, Message.obtain(null, 2));
		mQueue.send(KEY_ZERO, Message.obtain(null, 1));
		mQueue.send(KEY_ZERO, Message.obtain(null, 3));
		
		mQueue.setHandler(KEY_ZERO, new Handler() {
			public void handleMessage(Message msg) {
				counter.inc();
			}
		});
		
		mQueue.setHandler(KEY_ONE, new Handler() {
			public void handleMessage(Message msg) {
				counter.inc();
				
				assertThat(msg.what, is(2));
			}
		});
		
		assertThat(counter.getCount(), is(3));
	}
	
	private final static class Counter {
		private int mCount = 0;
		
		public int getCount() {
			return mCount;
		}
		
		public void inc() {
			mCount += 1;
		}
	}
}
