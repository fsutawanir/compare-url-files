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
	 * Synchronously compare each line URL response between 2 files
	 * 
	 * @param filePath1 First file path
	 * @param filePath2 Second file path
	 * 
	 * @return List of boolean containing each line comparison result  
	 */
	public List<Boolean> compare(final String filePath1, final String filePath2) {
		// Read the file and collect URL in each line
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
	 * Asynchronously compare each line URL response between 2 files
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
		
		// Prepare the list to hold each line response comparison result
		final List<Boolean> results = new LinkedList<Boolean>();
		// Get first file URL collection
		final List<String> rows1 = fileRowList.get(0);
		// Get second file URL collection
		final List<String> rows2 = fileRowList.get(1);
		
		// Decide which file is longer to be use in looping
		final int row1Size = rows1.size();
		final int row2Size = rows2.size();
		final int lengthUse = row1Size > row2Size ? row1Size : row2Size;
		
		// Useful to hold process until URL comparison process is complete
		// lengthUse as parameter because we want thread to wait n process to be complete.
		final CountDownLatch latch = new CountDownLatch(lengthUse);
		
		for(int i = 0; i < lengthUse; i++) {
			try {
				
				if(i > row1Size || i > row2Size) {
					// If there is difference in row size
					// just add comparison result as false
					results.add(false);
					continue;
				}
				
				final int index = i;
				final String url = rows1.get(i);
				final String url2 = rows2.get(i);
				
				// Prepare comparison data wrapper
				final ComparatorData comparatorData = new ComparatorData(url, url2);
				
				// Start comparison process
				this.compareResponseAsync(comparatorData, new IResponseComparisonCallback() {
					@Override
					public void completed(boolean isEqual) {
						System.out.println("["+index+"]["+isEqual+"]");
						// Collect response comparison result
						results.add(isEqual);
						// Notify latch to count down to mark that one process is complete 
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
	 * Compare 2 URL response synchronously.
	 * 
	 * @param url1 URL to be compare
	 * @param url2 URL to be compare
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
	 * Compare 2 URL response asynchronously.
	 * 
	 * @param comparatorData
	 * @param callback
	 */
	private void compareResponseAsync(
		final ComparatorData comparatorData,
		final IResponseComparisonCallback callback
	) {
		// Preparing the callback to retrieved asynchronous HTTP request response
		final FutureCallback<SimpleHttpResponse> requestCallback = new FutureCallback<SimpleHttpResponse>() {
			@Override
			public void completed(SimpleHttpResponse response) {
				// Collect response
				comparatorData.addResponse(response.getBodyText());
				invokeCallback();
			}

			@Override
			public void cancelled() {
				// If HTTP request is cancel, this method will be called
				// lets just set response to null
				comparatorData.addResponse(null);
				invokeCallback();
			}

			@Override
			public void failed(Exception e) {
				// If HTTP request is failed, this method will be called
				// lets just set response to null
				comparatorData.addResponse(null);
				invokeCallback();
			}
			
			/**
			 * Invoke callback if all URL response is collected to return comparison result
			 */
			private void invokeCallback() {
				// Check if all response has been collected
				if(comparatorData.isCompleted() ) {
					// If yes, then start the comparison
					// and invoke callback so it will be collected
					callback.completed(comparatorData.isResponseEqual() );
				}
			}
	    };
	    
	    // Start the process to get response from URL 1
		Https.getInstance().getAsync(comparatorData.getUrl1(), requestCallback);
	    // Start the process to get response from URL 2
		Https.getInstance().getAsync(comparatorData.getUrl2(), requestCallback);
	}
	
	/**
	 * URL comparison process callback
	 */
	interface IResponseComparisonCallback {
		
		/**
		 * Will be called if comparison process between both URL is complete
		 * then return the result.
		 * 
		 * @param isEqual Response comparison result
		 */
		void completed(boolean isEqual);
	}
}
