package com.alexgilleran.icesoap.request.impl;

import java.util.List;

import android.os.AsyncTask;

import com.alexgilleran.icesoap.envelope.SOAPEnvelope;
import com.alexgilleran.icesoap.observer.ListObserverRegistry;
import com.alexgilleran.icesoap.observer.SOAPListObserver;
import com.alexgilleran.icesoap.parser.IceSoapListParser;
import com.alexgilleran.icesoap.parser.ItemObserver;
import com.alexgilleran.icesoap.request.ListRequest;

/**
 * Implementation of {@link ListRequest}
 * 
 * @author Alex Gilleran
 * 
 * @param <ResultType>
 *            The type of the contents of the list to retrieve.
 */
public class ListRequestImpl<ResultType> extends RequestImpl<List<ResultType>>
		implements ListRequest<ResultType> {
	/** The parser to use to parse the result */
	private IceSoapListParser<ResultType> parser;
	/** The registry to use to dispatch item-related events */
	private ListObserverRegistry<ResultType> itemRegistry = new ListObserverRegistry<ResultType>(
			this);

	/**
	 * 
	 * Creates a new list request.
	 * 
	 * @param url
	 *            The URL to post the request to
	 * @param parser
	 *            The {@link IceSoapListParser} to use to parse the response.
	 * @param soapEnv
	 *            The SOAP envelope to send, as a {@link SOAPEnvelope}
	 */
	public ListRequestImpl(String url, IceSoapListParser<ResultType> parser,
			SOAPEnvelope soapEnv) {
		super(url, parser, soapEnv);

		this.parser = parser;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void registerObserver(SOAPListObserver<ResultType> observer) {
		super.registerObserver(observer);

		itemRegistry.addObserver(observer);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deregisterObserver(SOAPListObserver<ResultType> observer) {
		super.deregisterObserver(observer);

		itemRegistry.removeObserver(observer);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AsyncTask<Void, ResultType, List<ResultType>> createTask() {
		return new ListRequestTask();
	}

	/**
	 * Subclass of {@link RequestImpl} RequestTask that caters for mid-request
	 * events on the UI thread through the use of progress updates.
	 */
	private class ListRequestTask extends RequestTask<ResultType> {
		/**
		 * {@inheritDoc}
		 * 
		 * Adds an item observer to the list parser so we can take the new item
		 * events from the parser, then re-broadcast them to the request's
		 * observers on the UI thread.
		 */
		@Override
		protected void onPreExecute() {
			parser.registerItemObserver(itemObserver);
		}

		/**
		 * Sends notifications about new items on the UI thread.
		 */
		@Override
		protected void onProgressUpdate(ResultType... item) {
			itemRegistry.notifyNewItem(ListRequestImpl.this, item[0]);
		}

		/**
		 * Parser observer used to catch new items from the parser, then use the
		 * {@link AsyncTask#publishProgress(Object...))} to re-broadcast on the
		 * UI thread.
		 */
		private ItemObserver<ResultType> itemObserver = new ItemObserver<ResultType>() {
			@SuppressWarnings("unchecked")
			@Override
			public void onNewItem(ResultType item) {
				publishProgress(item);
			}
		};
	}
}