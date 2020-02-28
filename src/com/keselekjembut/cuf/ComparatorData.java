package com.keselekjembut.cuf;

import java.util.LinkedList;
import java.util.List;

public class ComparatorData {

	private final String url1;
	private final String url2;
	private final List<String> responses;
	
	public ComparatorData(final String url1, final String url2) {
		this.url1 = url1;
		this.url2 = url2;
		this.responses = new LinkedList<String>();
	}
	
	public String getUrl1() {
		return this.url1;
	}
	
	public String getUrl2() {
		return this.url2;
	}
	
	public void addResponse(final String response) {
		this.responses.add(response);
	}
	
	public boolean isCompleted() {
		return this.responses.size() == 2;
	}
	
	public boolean isEqual() {
		if(this.responses.size() < 2) {
			System.out.println("["+url1+"]["+url2+"] Lho kok kurang dari 2 ?");
			return false;
		}
		final boolean isEqual = responses.get(0).equals(responses.get(1));
		// System.out.println("["+url1+"]["+url2+"] RESULT :: " + isEqual);
		return isEqual;
	}
}
