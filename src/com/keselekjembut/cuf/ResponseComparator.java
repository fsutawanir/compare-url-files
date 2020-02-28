package com.keselekjembut.cuf;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.core5.concurrent.FutureCallback;

import java.util.LinkedList;

public class ResponseComparator {

	public List<Boolean> compare(final String filePath1, final String filePath2) {
		final List<String> rows1 = Files.getRows(filePath1);
		final List<String> rows2 = Files.getRows(filePath2);
		final List<Boolean> results = new LinkedList<Boolean>();
		final int row1Size = rows1.size();
		final int row2Size = rows2.size();
		final int lengthUse = row1Size > row2Size ? row1Size : row2Size;
		for(int i = 0; i < lengthUse; i++) {
			try {
				if(i > row1Size || i > row2Size) {
					results.add(false);
					continue;
				}
				final String url = rows1.get(i);
				final String url2 = rows2.get(i);
				final boolean isEqual = this.compareResponse(url, url2);
				results.add(isEqual);
			} catch (Exception e) {
				
			}
		}
		
		return results;
	}
	
	public List<Boolean> compareAsync(final String filePath1, final String filePath2) {
		final List<String> rows1 = Files.getRows(filePath1);
		final List<String> rows2 = Files.getRows(filePath2);
		final List<Boolean> results = new LinkedList<Boolean>();
		final int row1Size = rows1.size();
		final int row2Size = rows2.size();
		final int lengthUse = row1Size > row2Size ? row1Size : row2Size;
		final CountDownLatch latch = new CountDownLatch(lengthUse);
		for(int i = 0; i < lengthUse; i++) {
			try {
				if(i > row1Size || i > row2Size) {
					results.add(false);
					continue;
				}
				final int index = i;
				final String url = rows1.get(i);
				final String url2 = rows2.get(i);
				final ComparatorData comparatorData = new ComparatorData(url, url2);
				this.compareResponseAsync(comparatorData, new ICallback() {
					@Override
					public void completed(boolean isEqual) {
						System.out.println("["+index+"]["+isEqual+"]");
						results.add(isEqual);
						latch.countDown();
					}					
				});
			} catch (Exception e) {
				System.out.println(e.getMessage() );
			}
		}
		
		try {
			latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("[DONE]");
		
		return results;
	}
	
	private boolean compareResponse(final String url1, final String url2) throws Exception {
		final String response1 = Https.getInstance().get(url1);
		final String response2 = Https.getInstance().get(url2);
		return response1.equals(response2);
	}
	
	private void compareResponseAsync(
		final ComparatorData comparatorData,
		final ICallback callback
	) {
		final FutureCallback<SimpleHttpResponse> requestCallback = new FutureCallback<SimpleHttpResponse>() {
			@Override
			public void completed(SimpleHttpResponse response) {
				comparatorData.addResponse(response.getBodyText());
				invokeCallback();
			}

			@Override
			public void cancelled() {
				comparatorData.addResponse(null);
				invokeCallback();
			}

			@Override
			public void failed(Exception e) {
				comparatorData.addResponse(null);
				invokeCallback();
			}
			
			private void invokeCallback() {
				if(comparatorData.isCompleted() ) {
					callback.completed(comparatorData.isEqual() );
				}
			}
	    };
		Https.getInstance().getAsync(comparatorData.getUrl1(), requestCallback);
		Https.getInstance().getAsync(comparatorData.getUrl2(), requestCallback);
	}
	
	interface ICallback {
		void completed(boolean isEqual);
	}
}
