package com.keselekjembut.cuf;

import java.util.List;

/**
 * Main class
 */
public class Main {
	
	/**
	 * File path containings 2 url sample for testing 
	 */
	private static final String TEST_2DATA_1_PATH = "files/test-2data-1.txt";
	private static final String TEST_2DATA_2_PATH = "files/test-2data-2.txt";
	
	/**
	 * File path containings 100 url sample for testing
	 */
	private static final String TEST_100DATA_1_PATH = "files/test-100data-1.txt";
	private static final String TEST_100DATA_2_PATH = "files/test-100data-2.txt";
	
	/**
	 * File path containings 500 url sample for testing
	 */
	private static final String TEST_500DATA_1_PATH = "files/test-500data-1.txt";
	private static final String TEST_500DATA_2_PATH = "files/test-500data-2.txt";
	
	/**
	 * Main function
	 * 
	 * @param args
	 */
	public static void main(String [] args) {	
		Main.compare(Main.TEST_100DATA_1_PATH, Main.TEST_100DATA_2_PATH);
		Main.compareAsync(Main.TEST_100DATA_1_PATH, Main.TEST_100DATA_2_PATH);
    }
	
	/**
	 * Testing synchronous compare in ResponseComparator
	 * 
	 * @param filePath1 First file path
	 * @param filePath2 Second file path
	 */
	private static void compare(final String filePath1, final String filePath2) {
		// Method execution start time to help measuring execution duration
		long startTime = System.currentTimeMillis();
		
		final ResponseComparator responseComparator = new ResponseComparator();
		final List<Boolean> results = responseComparator.compare(filePath1, filePath2);
		for(final Boolean b : results) {
			System.out.println(b);
		}
		
		// Method execution end time to help measuring execution duration
		long endTime = System.currentTimeMillis();
		// Calculate method execution duration time
		long duration = (endTime - startTime);
		
		System.out.println("Execution Duration : " + duration + " milliseconds");
	}
	
	/**
	 * Testing asynchronous compare in ResponseComparator
	 * 
	 * @param filePath1 First file path
	 * @param filePath2 Second file path
	 */
	private static void compareAsync(final String filePath1, final String filePath2) {
		// Method execution start time to help measuring execution duration
		long startTime = System.currentTimeMillis();
		
		final ResponseComparator responseComparator = new ResponseComparator();
		final List<Boolean> results = responseComparator.compareAsync(filePath1, filePath2);
		for(final Boolean b : results) {
			System.out.println(b);
		}
		
		// Method execution end time to help measuring execution duration
		long endTime = System.currentTimeMillis();
		// Calculate method execution duration time
		long duration = (endTime - startTime);
		
		System.out.println("Execution Duration : " + duration + " milliseconds");
	}
}
