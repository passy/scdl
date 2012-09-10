package net.rdrei.android.mediator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import android.os.Message;

/**
 * A simple message queue net.rdrei.android.mediator, that collects messages until the receiver
 * is ready to process them.
 * 
 * Messages are standard Android message, so we can leverage the efficient
 * recycling process. However, we don't reuse any of the Handler 
 * infrastructure, so be sure not to attach it to one by accident.
 * 
 * @author pascal
 *
 */
public class MessageMediator {

	private final Map<String, MessageMediator.Receiver> mReceiverRegistry;

	public MessageMediator() {
		mReceiverRegistry = new HashMap<String, MessageMediator.Receiver>();
	}

	/**
	 * Register a new handler for a given, unique key.
	 * 
	 * @param key
	 * @param receiver
	 */
	public void register(final String key,
			final MessageMediator.Receiver receiver) {
		mReceiverRegistry.put(key, receiver);
	}

	public void send(final String key, final Message message) {
		final Receiver receiver = mReceiverRegistry.get(key);
		receiver.dispatch(message);
	}

	public static class Receiver {
		private final Queue<Message> mQueue;
		private boolean mIsAccepting = false;
		private Handler mHandler;

		public Receiver() {
			mQueue = new LinkedList<Message>();
		}

		public void setHandler(final Handler handler) {
			mHandler = handler;
		}

		public void accept() {
			mIsAccepting = true;
			sendAll();
		}

		public void dispatch(final Message message) {
			if (mIsAccepting) {
				send(message);
			} else {
				mQueue.add(message);
			}
		}

		private void sendAll() {
			for (final Message message : mQueue) {
				send(message);
			}
		}

		private void send(final Message message) {
			if (mHandler == null) {
				throw new NullPointerException(
						"There was no Message Receive Handler provided!");
			}

			mHandler.handleMessage(message);
			message.recycle();
		}
	}

	public interface Handler {
		public void handleMessage(Message message);
	}
}
