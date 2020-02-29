package com.keselekjembut.cuf;

import java.io.*;
import java.util.List;
import java.util.LinkedList;

public class Files {
	
	interface IFileReaderCallback {
		void complete(List<String> rows);
	}
	
	public static void getRowsAsync(String filePath, final IFileReaderCallback callback) {
		final Runnable run = new Runnable() {
			@Override
			public void run() {
				final List<String> rows = Files.getRows(filePath);
				callback.complete(rows);
			}
		};
		final Thread thread = new Thread(run);
		thread.start();
	}
	
	public static List<String> getRows(String filePath) {
		final List<String> rows = new LinkedList<String>();
        String line = null;
        BufferedReader bufferedReader = null;

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(filePath);

            // Always wrap FileReader in BufferedReader.
            bufferedReader = new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                rows.add(line);
            }

            return rows;
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + filePath + "'");
        }
        catch(IOException ex) {
            System.out.println("Error reading file '" + filePath + "'");                  
            // Or we could just do this: 
            // ex.printStackTrace();
        }
        finally {
            try {
                if(bufferedReader != null) {
                    // Always close files.
                    bufferedReader.close();
                }
            }
            catch(IOException e) {
                // do nothing
            }
        }

        return null;
	}
}
