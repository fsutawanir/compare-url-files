package com.keselekjembut.cuf;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.core5.concurrent.FutureCallback;

import java.util.LinkedList;

import com.keselekjembut.cuf.Files.IFileReaderCallback;

/**
 * Entry point to start comparing response between 2 files
 */
public class ResponseComparator {

	/**
	 * Synchronously compare each line url response between 2 files
	 * 
	 * @param filePath1
	 * @param filePath2
	 * 
	 * @return Contains list of boolean 
	 */
	public List<Boolean> compare(final String filePath1, final String filePath2) {
		// Read the file and collect url in each line
		final List<String> rows1 = Files.getInstance().getRows(filePath1);
		final List<String> rows2 = Files.getInstance().getRows(filePath2);
		
		// Prepare the list to hold each line comparison result 
		final List<Boolean> results = new LinkedList<Boolean>();

		// Decide which file is longer to be use in looping
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
				// do nothing
			}
		}
		
		return results;
	}
	
	public List<Boolean> compareAsync(final String filePath1, final String filePath2) {
		final CountDownLatch readFileLatch = new CountDownLatch(2);
		final List<List<String>> fileRowList = new LinkedList<List<String>>();
		final IFileReaderCallback fileReaderCallback = new IFileReaderCallback() {
			@Override
			public void complete(List<String> rows) {
				readFileLatch.countDown();
				fileRowList.add(rows);
			}
		};
		Files.getInstance().getRowsAsync(filePath1, fileReaderCallback);
		Files.getInstance().getRowsAsync(filePath2, fileReaderCallback);
		
		try {
			readFileLatch.await();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		final List<Boolean> results = new LinkedList<Boolean>();
		final List<String> rows1 = fileRowList.get(0);
		final List<String> rows2 = fileRowList.get(1);
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
	
	/**
	 * Compare 2 url response synchronously.
	 * 
	 * @param url1 Url to be compare
	 * @param url2 Url to be compare
	 * 
	 * @return Return TRUE if response is equal
	 * 
	 * @throws Exception
	 */
	private boolean compareResponse(final String url1, final String url2) throws Exception {
		final String response1 = Https.getInstance().get(url1);
		final String response2 = Https.getInstance().get(url2);
		return response1.equals(response2);
	}
	
	/**
	 * 
	 * @param comparatorData
	 * @param callback
	 */
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
					callback.completed(comparatorData.isResponseEqual() );
				}
			}
	    };
		Https.getInstance().getAsync(comparatorData.getUrl1(), requestCallback);
		Https.getInstance().getAsync(comparatorData.getUrl2(), requestCallback);
	}
	
	/**
	 * 
	 *
	 */
	interface ICallback {
		void completed(boolean isEqual);
	}
}
