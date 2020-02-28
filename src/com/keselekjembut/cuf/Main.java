package com.keselekjembut.cuf;

import java.util.List;

public class Main {
	
	public static void main(String [] args) {
//		Main.compare();
		Main.compareAsync();
    }
	
	private static void compare() {
		long startTime = System.currentTimeMillis();
		final ResponseComparator responseComparator = new ResponseComparator();
		final List<Boolean> results = 
				responseComparator.compare("files/test3.txt", "files/test4.txt");
		for(final Boolean b : results) {
			System.out.println(b);
		}
		long endTime = System.currentTimeMillis();
		long duration = (endTime - startTime);
		System.out.println("Invocation duration : " + duration + " milliseconds");
	}
	
	private static void compareAsync() {
		long startTime = System.currentTimeMillis();
		final ResponseComparator responseComparator = new ResponseComparator();
		final List<Boolean> results = 
				responseComparator.compareAsync("files/test1.txt", "files/test2.txt");
		for(final Boolean b : results) {
			System.out.println(b);
		}
		
		long endTime = System.currentTimeMillis();
		long duration = (endTime - startTime);
		System.out.println("Invocation duration : " + duration + " milliseconds");
	}
}
