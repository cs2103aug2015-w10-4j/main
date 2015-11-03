package storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import global.Task;

public class JsonFormatStorage implements Storage {
	Logger logger = Logger.getGlobal(); // use logger.<log level>(message) to log a message. default log level is info
	
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private static final String DEFAULT_FILE_PATH = "save.txt";
	
	private String currentFilePath = DEFAULT_FILE_PATH;
	
	private Gson gson;

	/**
	 * Default constructor for JsonFormatStorage. Does not use pretty formatting for JSON.
	 */
	//@@author A0134155M
	public JsonFormatStorage() {
		gson = new GsonBuilder().serializeNulls().create();
	}
	
	/**
	 * Alternative constructor for JsonFormatStorage with option whether to prettify the
	 * JSON or not.
	 * @param usePrettyJson whether to format the JSON with pretty format or not
	 */
	//@@author A0134155M
	public JsonFormatStorage(boolean usePrettyJson) {
		if (usePrettyJson) {
			gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
		} else {
			gson = new GsonBuilder().serializeNulls().create();
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * It saves the data in JSON format.
	 */
	//@@author A0134155M
	@Override
	public boolean writeItemList(ArrayList<Task> tasks) throws IOException {
		String jsonFormat = convertToJsonFormat(tasks);
		
		File outputFile = new File(currentFilePath);
		FileWriter outputFileWriter = new FileWriter(outputFile, false);
		
		outputFileWriter.write(jsonFormat);
		outputFileWriter.write(LINE_SEPARATOR);
		
		outputFileWriter.close();
		logger.info("JsonFormat write tasks of "+ 	storeInfo(tasks) + " into file: "+ currentFilePath);
		return true;
	}

	//@@author A0134155M
	private String convertToJsonFormat(ArrayList<Task> tasks) {
		Task[] tasksArray = new Task[tasks.size()];
		tasksArray = tasks.toArray(tasksArray);
		
		String jsonFormat = gson.toJson(tasksArray);
		logger.info("convert to JsonFormat");
		return jsonFormat;
	}

	/* (non-Javadoc)
	 * @see storage.Storage#saveFileToPath(java.lang.String)
	 */
	//@author A0108355H
	@Override
	public boolean saveFileToPath(String path) throws IOException {
		boolean isFilePathChanged = false;
		
		File newFile = new File(path);
		
		String newFolderPath = "";
		if(path.indexOf("/") != -1) {
			newFolderPath = path.substring(0,path.lastIndexOf("/"));
		}
		if(path.indexOf("\\") != -1) {
			newFolderPath = path.substring(0,path.lastIndexOf("\\"));
		}
		if(!newFolderPath.equals("")) {
			File Folderpath = new File(newFolderPath);
			if(!Folderpath.exists()) {
				Folderpath.mkdirs();
			}
		}	
		
		if (!newFile.exists()) {
			//file is not yet created, try to create one
			newFile.createNewFile();
			copyFile(path, currentFilePath);
			currentFilePath = path;
			isFilePathChanged = true;
			logger.info("JsonFormat save file to new path: "+path);
		} else {
			//exist already, check whether it is the same file
			//with the current one
			if (!currentFilePath.equals(path)) {
				//copyFile(path, currentFilePath);
				// removed because the user might want to open an already existing data file
				currentFilePath = path;
				isFilePathChanged = true;
				logger.info("JsonFormat save file to new path: "+path);
			}
		}
	
		return isFilePathChanged;
	}
	
	//@author A0108355H
	private void copyFile(String newPath, String oldPath) throws IOException {
		FileReader fr = new FileReader(oldPath);
		FileWriter fw = new FileWriter(newPath);
		int c = fr.read();
		while (c != -1) {
			fw.write(c);
			c = fr.read(); // Add this line
		}
		fr.close();
		fw.close();
	}

	/**
	 * {@inheritDoc}
	 * @throws IllegalStateException if the file does not contain a valid JSON.
	 */
	//@@author A0134155M
	@Override	
	public ArrayList<Task> getItemList() throws FileNotFoundException, IllegalStateException {
		File inputFile = new File(currentFilePath);
		Scanner inputFileScanner = new Scanner(inputFile);
		
		StringBuilder rawInputData = new StringBuilder();
		while (inputFileScanner.hasNextLine()) {
			rawInputData.append(inputFileScanner.nextLine());
		}
		
		Task[] processedInputData = gson.fromJson(rawInputData.toString(), Task[].class);
		ArrayList<Task> result;
		if(processedInputData != null) {
			result = new ArrayList<>(Arrays.asList(processedInputData));
		} else {
			result = new ArrayList<>();
		}
		
		inputFileScanner.close();
		logger.info("JsonFormat get items");
		return result;
	}
	
	//@author A0108355H
	private String storeInfo(ArrayList<Task> list) {
		String loggerMsg = "";
		for (int i = 0; i < list.size(); i++) {
			loggerMsg += list.get(i).getAllInfo() + " ";
		}
		return loggerMsg;

	}

}
