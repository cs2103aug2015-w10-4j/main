package storage;

import global.Task;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public interface Storage {
	static final String LINE_SEPARATOR = System.getProperty("line.separator");
	static final String HELP_PATH = "help.txt";
	static final String ERROR_HELP = "Unable to retrieve help file!";

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
	
	//@@author A0124093M
	public default String getHelpMessage() {
		BufferedReader br;
		String mainStr = "";
		try {
			String currentLine;
			br = new BufferedReader(new FileReader(HELP_PATH));
			while ((currentLine = br.readLine()) != null) {
				mainStr += currentLine + LINE_SEPARATOR;
			}
			br.close();
		} catch (IOException e) {
			return ERROR_HELP;
		}
		return mainStr;
	}
	
}
