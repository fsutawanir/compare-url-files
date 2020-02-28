package com.keselekjembut.cuf;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;

public class Main {
	
	public static void main(String [] args) {
		final List<Boolean> results = 
			ResponseComparator.compare("files/test1.txt", "files/test2.txt");
		for(final Boolean b : results) {
			System.out.println(b);
		}
    }
	
}
