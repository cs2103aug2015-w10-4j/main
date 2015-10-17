package storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Logger;

import parser.Parser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import global.Task;

public class JsonFormatStorage implements Storage {
	Logger logger = Logger.getGlobal(); // use logger.<log level>(message) to log a message. default log level is info
	private static JsonFormatStorage storageInstance = null;
	
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private static final String DEFAULT_FILE_PATH = "save.txt";
	
	private String currentFilePath = DEFAULT_FILE_PATH;
	
	private Gson gson;

	/**
	 * Default constructor for JsonFormatStorage. Does not use pretty formatting for JSON.
	 */
	public JsonFormatStorage() {
		gson = new GsonBuilder().serializeNulls().create();
	}
	
	/**
	 * Alternative constructor for JsonFormatStorage with option whether to prettify the
	 * JSON or not.
	 * @param usePrettyJson whether to format the JSON with pretty format or not
	 */
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
	@Override
	public boolean writeItemList(ArrayList<Task> tasks) throws IOException {
		String jsonFormat = convertToJsonFormat(tasks);
		
		File outputFile = new File(currentFilePath);
		FileWriter outputFileWriter = new FileWriter(outputFile, false);
		
		outputFileWriter.write(jsonFormat);
		outputFileWriter.write(LINE_SEPARATOR);
		
		outputFileWriter.close();
		return true;
	}

	private String convertToJsonFormat(ArrayList<Task> tasks) {
		Task[] tasksArray = new Task[tasks.size()];
		tasksArray = tasks.toArray(tasksArray);
		
		String jsonFormat = gson.toJson(tasksArray);
		return jsonFormat;
	}

	/* (non-Javadoc)
	 * @see storage.Storage#saveFileToPath(java.lang.String)
	 */
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
			currentFilePath = path;
			isFilePathChanged = true;
		} else {
			//exist already, check whether it is the same file
			//with the current one
			if (!currentFilePath.equals(path)) {
				currentFilePath = path;
				isFilePathChanged = true;
			}
		}
		
		return isFilePathChanged;
	}

	/**
	 * {@inheritDoc}
	 * @throws IllegalStateException if the file does not contain a valid JSON.
	 */
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
		return result;
	}
	
	public static Storage getInstance(){
		if(storageInstance == null){
			storageInstance = new JsonFormatStorage(true);
		}
		return storageInstance;
	}

}
