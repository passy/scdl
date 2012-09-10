package net.rdrei.android.mediator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import android.os.Message;

public class DelayedMessageQueue {

	private final Map<String, LinkedList<Message>> mQueueMap;
	private final Map<String, Handler> mHandlerRegistry;

	public DelayedMessageQueue() {
		mQueueMap = new HashMap<String, LinkedList<Message>>();
		mHandlerRegistry = new HashMap<String, DelayedMessageQueue.Handler>();
	}

	public void send(final String key, final Message message) {
		final Handler handler = mHandlerRegistry.get(key);

		if (handler == null) {
			storeMessage(key, message);
		} else {
			dispatch(handler, message);
		}
	}

	private void storeMessage(String key, Message message) {
		LinkedList<Message> queue = mQueueMap.get(key);

		if (queue == null) {
			queue = new LinkedList<Message>();
			mQueueMap.put(key, queue);
		}

		queue.add(message);
	}

	public void setHandler(final String key, final Handler handler) {
		synchronized (this) {
			mHandlerRegistry.put(key, handler);

			LinkedList<Message> delayedMessages = mQueueMap.get(key);

			if (delayedMessages == null) {
				return;
			}

			for (final Message message : delayedMessages) {
				dispatch(handler, message);
			}

			// Instead of dequeuing, we can also remove the queue as a whole
			// because it's consumed and most likely not reused.
			mQueueMap.remove(key);
		}
	}

	private void dispatch(Handler handler, Message message) {
		handler.handleMessage(message);
	}

	public static interface Handler {
		public void handleMessage(final Message message);
	}
}
