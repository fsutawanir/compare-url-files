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
	 * @param filePath1 First file path
	 * @param filePath2 Second file path
	 * 
	 * @return List of boolean containing each line comparison result  
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
	
	/**
	 * Asynchronously compare each line url response between 2 files
	 * 
	 * @param filePath1 First file path
	 * @param filePath2 Second file path
	 * 
	 * @return List of boolean containing each line comparison result  
	 */
	public List<Boolean> compareAsync(final String filePath1, final String filePath2) {
		// Useful to hold process until file reading is complete.
		// 2 as parameter because we want thread to wait 2 process to be complete. 
		final CountDownLatch readFileLatch = new CountDownLatch(2);
		// Contain each line of both file
		final List<List<String>> fileRowList = new LinkedList<List<String>>();
		// File reader callback
		final IFileReaderCallback fileReaderCallback = new IFileReaderCallback() {
			@Override
			public void complete(List<String> rows) {
				// Notify latch to mark 1 process is complete 
				readFileLatch.countDown();
				// Collect file row result 
				fileRowList.add(rows);
			}
		};
		// Read first file
		Files.getInstance().getRowsAsync(filePath1, fileReaderCallback);
		// Read second file
		Files.getInstance().getRowsAsync(filePath2, fileReaderCallback);
		
		try {
			// Thread will be wait until file reading is complete
			readFileLatch.await();
		} catch (InterruptedException e1) {
			// do nothing
		}
		
		// Preapre the list to hold each line response comparison result
		final List<Boolean> results = new LinkedList<Boolean>();
		// Get first file url collection
		final List<String> rows1 = fileRowList.get(0);
		// Get second file url collection
		final List<String> rows2 = fileRowList.get(1);
		
		// Decide which file is longer to be use in looping
		final int row1Size = rows1.size();
		final int row2Size = rows2.size();
		final int lengthUse = row1Size > row2Size ? row1Size : row2Size;
		
		// Useful to hold process until url comparison process is complete
		// lengthUse as parameter because we want thread to wait n process to be complete.
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
			// do nothing
		}
		
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
	 * Compare 2 url response asynchronously.
	 * 
	 * @param comparatorData
	 * @param callback
	 */
	private void compareResponseAsync(
		final ComparatorData comparatorData,
		final ICallback callback
	) {
		
		// Preparing the callback to retrieved asynchronous http request response
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
				// Check if all response has been collected
				if(comparatorData.isCompleted() ) {
					// If yes, then start the comparison
					// and invoke callback so it will be collected
					callback.completed(comparatorData.isResponseEqual() );
				}
			}
	    };
	    
	    // Start the process to get response from url 1
		Https.getInstance().getAsync(comparatorData.getUrl1(), requestCallback);
	    // Start the process to get response from url 2
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
