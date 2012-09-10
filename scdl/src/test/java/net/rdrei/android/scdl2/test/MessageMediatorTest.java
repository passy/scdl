package net.rdrei.android.scdl2.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import net.rdrei.android.mediator.MessageMediator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.os.Message;

import com.google.inject.Inject;

@RunWith(TestRunner.class)
public class MessageMediatorTest {
	
	@Inject
	private MessageMediator mMediator;
	
	@Inject
	private MessageMediator.Receiver mReceiver0;
	
	@Inject
	private MessageMediator.Receiver mReceiver1;

	private static final String KEY_ZERO = "ZERO";
	private static final String KEY_ONE = "ONE";
	
	@Before
	public void inject() {
		TestRunner.getInjector().injectMembers(this);
	}
	
	@Test
	public void testReceivesQueuedMessages() {
		final Counter counter = new Counter();
		final Message message0 = Message.obtain();
		message0.arg1 = 23;
		
		final Message message1 = Message.obtain();
		message1.arg1 = 42;
		
		mMediator.register(KEY_ZERO, mReceiver0);
		mMediator.send(KEY_ZERO, message0);
		mMediator.send(KEY_ZERO, message1);
		
		mReceiver0.setHandler(new MessageMediator.Handler() {
			public void handleMessage(final Message msg) {
				counter.inc();
				
				if (counter.getCount() == 1) {
					assertThat(msg.arg1, is(23));
				} else if (counter.getCount() == 2) {
					assertThat(msg.arg1, is(42));
				}
			}
		});
		mReceiver0.accept();
		
		assertThat(counter.getCount(), is(2));
	}
	
	@Test
	public void testReceivesLiveMessages() {
		final Counter counter = new Counter();
		final Message message0 = Message.obtain();
		message0.arg1 = 23;
		
		final Message message1 = Message.obtain();
		message1.arg1 = 42;
		
		mMediator.register(KEY_ZERO, mReceiver0);
		
		mReceiver0.setHandler(new MessageMediator.Handler() {
			public void handleMessage(final Message msg) {
				counter.inc();
				
				if (counter.getCount() == 1) {
					assertThat(msg.arg1, is(23));
				} else if (counter.getCount() == 2) {
					assertThat(msg.arg1, is(42));
				}
				
				msg.recycle();
			}
		});
		mMediator.send(KEY_ZERO, message0);
		mReceiver0.accept();
		assertThat(counter.getCount(), is(1));
		mMediator.send(KEY_ZERO, message1);
		
		assertThat(counter.getCount(), is(2));
	}
	
	@Test
	public void testReceivesMessageDispatching() {
		final Counter counter = new Counter();
		
		final Message message0 = Message.obtain();
		message0.arg1 = 23;
		
		final Message message1 = Message.obtain();
		message1.arg1 = 42;
		
		mMediator.register(KEY_ZERO, mReceiver0);
		mMediator.register(KEY_ONE, mReceiver1);
		
		mReceiver0.setHandler(new MessageMediator.Handler() {
			public void handleMessage(final Message msg) {
				assertThat(msg.arg1, is(23));
				counter.inc();
			}
		});
		
		mReceiver1.setHandler(new MessageMediator.Handler() {
			public void handleMessage(final Message msg) {
				assertThat(msg.arg1, is(42));
				counter.inc();
			}
		});
		
		mMediator.send(KEY_ZERO, message0);
		mReceiver0.accept();
		
		mMediator.send(KEY_ONE, message1);
		mReceiver1.accept();
		
		assertThat(counter.getCount(), is(2));
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
