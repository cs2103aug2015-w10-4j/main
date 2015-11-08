package storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import global.Task;

public interface Storage {
	static final String LINE_SEPARATOR = System.getProperty("line.separator");
	static final String HELP_MESSAGE = "" +
			"Commands  Example usage\n" +
			"add       add task123 date 11 sep 2015\n" +
			"display   display\n" +
			"edit      edit 1 task456 date 12 sep 2015\n" +
			"delete    delete 1\n" +
			"search    search task123\n" +
			"undo      undo\n" +
			"redo      redo\n" +
			"saveto    saveto new_file.txt\n" +
			"exit      exit\n";
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
	
	public default String getHelpMessage()  {
		return HELP_MESSAGE;
	}
	
}
