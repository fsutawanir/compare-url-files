package com.keselekjembut.cuf;

import java.io.IOException;
import java.util.List;
import java.util.LinkedList;

public class ResponseComparator {

	public synchronized static List<Boolean> compare(final String filePath1, final String filePath2) {
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
				results.add(ResponseComparator.compareResponse(url, url2));
			} catch (Exception e) {
				
			}
		}
		
		return results;
	}
	
	private synchronized static Boolean compareResponse(final String url1, final String url2) throws Exception {
		final String response1 = Https.get(url1);
		final String response2 = Https.get(url2);
		return response1.equals(response2);
	}
	
	
}
