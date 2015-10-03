package storage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Scanner;

import global.Task;

public class Storage {
	
	private static final String ARGUMENTS_SEPERATOR = ";";
	private static final String ARGUMENTS_DATE = "date ";
	
	public static String FILE_PATH = "save.txt";
	public static String FILE_NEWLINE = "\r\n";
	
	// date format converter
	private static SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
	
	/**
	 * Saves the list of tasks in the file
	 * @param ArrayList<Task> ArrayList that stores the RAW tasks as Strings in text file
	 * Task data is saved in the following format: "[taskname];[date]" on each line. To be improved
	 * @return true if file is saved
	 * @throws IOException 
	 */
	public boolean writeItemList(ArrayList<Task> task) throws IOException {
		String content = "";
		for (int i = 0; i <task.size(); i ++) {
			Task curTask = task.get(i);
			if(curTask != null){
				content += curTask.getName();
				if (curTask.getEndingTime() != null) {
					content += ARGUMENTS_SEPERATOR + ARGUMENTS_DATE + sdf.format(task.get(i).getEndingTime().getTime());
				}
			}
			content += FILE_NEWLINE;
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
	 * @return true if location changes
	 * @throws IOException
	 */
	public boolean saveFileToPath(String path) throws IOException {
		File file = new File(path);
		if (!file.exists()) {
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
	public ArrayList<String> getItemList() throws FileNotFoundException {
		File file = new File(FILE_PATH);
		Scanner sc = new Scanner(file);
		ArrayList<String> taskList = new ArrayList<String>();
	
		while (sc.hasNextLine()) {
			taskList.add(sc.nextLine());
		}
		sc.close();
		return taskList;
	}
	
	/*
	 * read saved file
	 */
	public ArrayList<Task> readFileData(ArrayList<String> fileData) {
		ArrayList<Task> taskList = new ArrayList<Task>();
		for (int i = 0; i < fileData.size(); i++) {
			Task taskObj = new Task();
			if (fileData.get(i).contains(ARGUMENTS_DATE)) {
				parser.Parser.extractDate(fileData.get(i), taskObj);
			} else {
				taskObj.setName(fileData.get(i));
			}
			taskList.add(taskObj);
		}
		return taskList;
	}
	


}
