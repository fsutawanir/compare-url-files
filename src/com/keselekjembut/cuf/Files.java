package com.keselekjembut.cuf;

import java.io.*;
import java.util.List;
import java.util.LinkedList;

/**
 * File helper
 */
class Files {
	
	/** Singleton instance of Files */
	private static Files INSTANCE;
	
	/** Private constructor */
	private Files() {
		
	}
	
	/**
	 * Get singleton instance of Files
	 * 
	 * @return Files instance
	 */
	public synchronized static final Files getInstance() {
		if(Files.INSTANCE == null) {
			Files.INSTANCE = new Files();
		}
		return Files.INSTANCE;
	}
	
	/**
	 * Read file, collect string per row, then returns it as a list via a callback.
	 * If there's an exception, null will be sent.
	 * All process running asynchronously.
	 * 
	 * @param filePath
	 * @param callback
	 */
	public void getRowsAsync(String filePath, final IFileReaderCallback callback) {
		final Runnable run = new Runnable() {
			@Override
			public void run() {
				final List<String> rows = Files.getInstance().getRows(filePath);
				callback.complete(rows);
			}
		};
		final Thread thread = new Thread(run);
		thread.start();
	}
	
	/**
	 * Read file, collect string per row, then returns it as a list.
	 * If there's an exception, return null.
	 * 
	 * @param filePath
	 * @param callback
	 * 
	 * @return List of string in each row in file
	 */
	public List<String> getRows(String filePath) {
		final List<String> rows = new LinkedList<String>();
		
        String line = null;
        BufferedReader bufferedReader = null;

        try {
            FileReader fileReader = new FileReader(filePath);
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
        }
        finally {
        	// Do not forget to close the file
            try {
                if(bufferedReader != null) {
                    bufferedReader.close();
                }
            }
            catch(IOException e) {
                // do nothing
            }
        }

        return null;
	}
	
	/**
	 * File reader callback
	 */
	interface IFileReaderCallback {
		
		/**
		 * Will be invoked if read file and collect string in each line is complete
		 * 
		 * @param rows List of each line in file
		 */
		void complete(List<String> rows);
	}
}
