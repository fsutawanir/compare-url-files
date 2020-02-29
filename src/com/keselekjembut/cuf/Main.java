package com.keselekjembut.cuf;

import java.util.List;

public class Main {
	
	private static final String TEST_2DATA_1_PATH = "files/test-2data-1.txt";
	private static final String TEST_2DATA_2_PATH = "files/test-2data-2.txt";
	
	private static final String TEST_100DATA_1_PATH = "files/test-100data-1.txt";
	private static final String TEST_100DATA_2_PATH = "files/test-100data-2.txt";
	
	private static final String TEST_500DATA_1_PATH = "files/test-500data-1.txt";
	private static final String TEST_500DATA_2_PATH = "files/test-500data-2.txt";
	
	public static void main(String [] args) {
//		Main.compare(Main.TEST_2DATA_1_PATH, Main.TEST_2DATA_2_PATH);
//		Main.compareAsync(Main.TEST_2DATA_1_PATH, Main.TEST_2DATA_2_PATH);
		
		Main.compare(Main.TEST_100DATA_1_PATH, Main.TEST_100DATA_2_PATH);
		Main.compareAsync(Main.TEST_100DATA_1_PATH, Main.TEST_100DATA_2_PATH);
    }
	
	private static void compare(final String filePath1, final String filePath2) {
		long startTime = System.currentTimeMillis();
		final ResponseComparator responseComparator = new ResponseComparator();
		final List<Boolean> results = responseComparator.compare(filePath1, filePath2);
		for(final Boolean b : results) {
			System.out.println(b);
		}
		long endTime = System.currentTimeMillis();
		long duration = (endTime - startTime);
		System.out.println("Invocation duration : " + duration + " milliseconds");
	}
	
	private static void compareAsync(final String filePath1, final String filePath2) {
		long startTime = System.currentTimeMillis();
		final ResponseComparator responseComparator = new ResponseComparator();
		final List<Boolean> results = responseComparator.compareAsync(filePath1, filePath2);
		for(final Boolean b : results) {
			System.out.println(b);
		}
		
		long endTime = System.currentTimeMillis();
		long duration = (endTime - startTime);
		System.out.println("Invocation duration : " + duration + " milliseconds");
	}
}
