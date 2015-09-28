package storage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import global.Task;

public class Storage {
	public static String FILE_PATH = "save.txt";
	public static String FILE_TASKLINE = "%s\r\n";
	
	/**
	 * Saves the list of tasks in the file
	 * @param ArrayList<Task>task    ArrayList that stores the tasks
	 * @return   true if file is saved
	 * @throws IOException 
	 */
	public boolean writeItemList(ArrayList<Task> task) throws IOException {
		String content = "";
		for (int i = 0; i <task.size(); i ++) {
			content += String.format(FILE_TASKLINE,task.get(i).getName());
		}
	
		File file = new File(FILE_PATH);
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(content);
		bw.close();
		return true;
	}

	/**
	 * Saves path to text file
	 *
	 * @param path path is a String contain the path of file to save
	 * @return   true if location changes
	 * @throws IOException
	 */
	public boolean saveFileToPath(String path) throws IOException {
		File file = new File(path);
		if (!file.exists()){
			file.createNewFile();
			FILE_PATH = path;
			return false;
		}
		FILE_PATH = path;
		return true;
	}

	/**
	 * Reads the saved file and returns the ArrayList of tasks
	 * @return ArrayList of tasks read from file
	 * @throws FileNotFoundException if there is no file in the filePath
	 */
	public ArrayList<Task> getItemList() throws FileNotFoundException {
		File file = new File(FILE_PATH);
		Scanner sc = new Scanner(file);
		ArrayList<Task>tasklist = new ArrayList<Task>();
	
		while (sc.hasNext()) {
			String taskName = sc.next();
			Task tempTask = new Task(taskName);
			tasklist.add(tempTask);
		}
		sc.close();
		return tasklist;
	}

}
