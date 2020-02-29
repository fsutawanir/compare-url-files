package com.keselekjembut.cuf;

import java.util.LinkedList;
import java.util.List;

/**
 * Class to hold url, each url response, and comparion result 
 */
class ComparatorData {

	private final String url1;
	private final String url2;
	private final List<String> responses;
	
	/**
	 * Constructor
	 */
	public ComparatorData(final String url1, final String url2) {
		this.url1 = url1;
		this.url2 = url2;
		this.responses = new LinkedList<String>();
	}
	
	/**
	 * Get first url
	 * 
	 * @return First url
	 */
	public String getUrl1() {
		return this.url1;
	}
	
	/**
	 * Get second url
	 * 
	 * @return Second url
	 */
	public String getUrl2() {
		return this.url2;
	}
	
	/**
	 * Add response of url
	 * 
	 * @param response
	 */
	public void addResponse(final String response) {
		this.responses.add(response);
	}
	
	/**
	 * If response size is 2, it means all url already has the response
	 * and ready to compare.
	 * 
	 * @return Return TRUE if url already has the response.
	 */
	public boolean isCompleted() {
		return this.responses.size() == 2;
	}
	
	/**
	 * Compare response between each url.
	 * 
	 * @return TRUE if response is equal, otherwise FALSE.
	 */
	public boolean isResponseEqual() {
		if(this.responses.size() < 2) {
			return false;
		}
		return responses.get(0).equals(responses.get(1));
	}
}
