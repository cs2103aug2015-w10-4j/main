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
	public static String filePath="save.txt";
	/**
	 * Saves the list of tasks in the file
	 * @param ArrayList<Task>task    ArrayList that stores the tasks
	 * @return   true if file is saved
	 * @throws IOException 
	 */
	public boolean writeitem(ArrayList<Task> task) throws IOException{
		String content = "";
		for(int i = 0; i <task.size(); i ++){
			content += task.get(i).getName() + "\r\n";
		}
	
		File file = new File(filePath);
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(content);
	//bw.newLine();
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
	public boolean saveFileToPath(String path) throws IOException{
		File file = new File(path);
		if (!file.exists()){
			file.createNewFile();
			filePath = path;
			return false;
		}
		filePath = path;
		return true;
	}

	/**
	 * Reads the saved file and returns the ArrayList of tasks
	 * @return     ArrayList that contain tasks that has created
	 * @throws FileNotFoundException if there is no file in the filePath
	 */
	public ArrayList<Task> getItemList() throws FileNotFoundException{
		File file = new File(filePath);
		Scanner sc = new Scanner(file);
		ArrayList<Task>tasklist = new ArrayList<Task>();
	
		while (sc.hasNext()){
			String taskName=sc.next();
			Task tempTask = new Task(taskName);
			tasklist.add(tempTask);
		}
		sc.close();
		return tasklist;
	}

}
