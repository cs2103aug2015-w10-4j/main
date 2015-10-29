package storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import global.Task;

public interface Storage {
	static final String HELP_PATH = "src/storage/Help";
	static final String LINE_SEPARATOR = System.getProperty("line.separator");
	/**
	 * Saves the list of tasks in the file
	 * @param ArrayList<Task> ArrayList that stores the RAW tasks as Strings in text file
	 * @return true if file is saved
	 * @throws IOException 
	 */
	public boolean writeItemList(ArrayList<Task> tasks) throws IOException;

	/**
	 * Saves path to text file
	 *
	 * @param path path is a String contain the path of file to save
	 * @return true if location changes
	 * @throws IOException
	 */
	public boolean saveFileToPath(String path) throws IOException;

	/**
	 * Reads the saved file and returns the ArrayList of tasks
	 * @return ArrayList of tasks read from file
	 * @throws FileNotFoundException if there is no file in the filePath
	 */
	public ArrayList<Task> getItemList() throws FileNotFoundException;
	
	public default String getHelp()  {
		String helpContent = "";
		File helpfile = new File(HELP_PATH);
		
		Scanner sc;
		try {
			sc = new Scanner(helpfile);
		
		while (sc.hasNextLine()) {
			helpContent += sc.nextLine()+ LINE_SEPARATOR;
		}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return helpContent;
	}
	
}
