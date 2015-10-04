package storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import global.Task;

public class JsonFormatStorage implements Storage {
	
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	private static String filePath = "save.txt";
	
	private Gson gson;

	/**
	 * Default constructor for JsonFormatStorage. Does not use pretty formatting for JSON.
	 */
	public JsonFormatStorage() {
		gson = new GsonBuilder().create();
	}
	
	/**
	 * Alternative constructor for JsonFormatStorage with option whether to prettify the
	 * JSON or not.
	 * @param prettyJson whether to format the json with pretty format or not
	 */
	public JsonFormatStorage(boolean prettyJson) {
		gson = new GsonBuilder().setPrettyPrinting().create();
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * It saves the data in JSON format.
	 */
	@Override
	public boolean writeItemList(ArrayList<Task> tasks) throws IOException {
		String jsonFormat = convertToJsonFormat(tasks);
		
		File outputFile = new File(filePath);
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
		boolean filePathChanged = false;
		
		File newFile = new File(path);
		if (!newFile.exists()) {
			//file is not yet created, try to create one
			newFile.createNewFile();
			filePath = path;
			filePathChanged = true;
		} else {
			//exist already, check whether it is the same file
			//with the current one
			if (!filePath.equals(path)) {
				filePath = path;
				filePathChanged = true;
			}
		}
		
		return filePathChanged;
	}

	/**
	 * {@inheritDoc}
	 * @throws IllegalStateException if the file does not contain a valid JSON.
	 */
	@Override	
	public ArrayList<Task> getItemList() throws FileNotFoundException, IllegalStateException {
		File inputFile = new File(filePath);
		Scanner inputFileScanner = new Scanner(inputFile);
		
		String rawInputData = inputFileScanner.next();
		
		Task[] processedInputData = gson.fromJson(rawInputData, Task[].class);
		ArrayList<Task> result = new ArrayList<>(Arrays.asList(processedInputData));
		
		inputFileScanner.close();
		return result;
	}

}
