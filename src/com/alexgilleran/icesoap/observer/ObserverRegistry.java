package com.alexgilleran.icesoap.observer;

import java.util.ArrayList;
import java.util.List;

public class ObserverRegistry<T> {
	private List<SOAPObserver<T>> listeners = new ArrayList<SOAPObserver<T>>();

	public void addListener(SOAPObserver<T> listener) {
		listeners.add(listener);
	}

	public void removeListener(SOAPObserver<T> listener) {
		listeners.remove(listener);
	}

	public void notifyListeners(T item) {
		for (SOAPObserver<T> listener : listeners) {
			listener.onNewDaoItem(item);
		}
	}
}