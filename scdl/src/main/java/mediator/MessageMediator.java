package mediator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import android.os.Message;

public class MessageMediator {

	private final Map<String, MessageMediator.Receiver> mReceiverRegistry;

	public MessageMediator() {
		mReceiverRegistry = new HashMap<String, MessageMediator.Receiver>();
	}

	public void register(final String key,
			final MessageMediator.Receiver receiver) {
		mReceiverRegistry.put(key, receiver);
	}

	public void send(final String key, final Message message) {
		final Receiver receiver = mReceiverRegistry.get(key);
		receiver.receive(message);
	}

	public static class Receiver {
		final private Queue<Message> mQueue;
		private boolean mIsAccepting = false;
		private ReceiveHandler mHandler;

		public Receiver() {
			mQueue = new LinkedList<Message>();
		}

		public void setHandler(final ReceiveHandler handler) {
			mHandler = handler;
		}

		public void accept() {
			mIsAccepting = true;
			sendAll();
		}

		private void receive(final Message message) {
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
		}
	}

	public interface ReceiveHandler {
		public void handleMessage(Message message);
	}
}
