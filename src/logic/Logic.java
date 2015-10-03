package logic;
import global.Command;
import global.Task;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import parser.Parser;
import storage.Storage;
import ui.UI;

public class Logic {
	
	/*
	 * Declaration of object variables
	 */
	UI UIObject;
	Parser parserObject;
	Storage storageObject;
	History historyObject;
	ArrayList<Task> listOfTasks = new ArrayList<Task>();
	
	// date format converter
	static SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
	
	/*
	 * Static strings - errors and messages
	 */
	public static final String MESSAGE_WELCOME = "Welcome to Tasky! This is an open source project";
	public static final String MESSAGE_PROMPT_COMMAND = "command :";
	public static final String MESSAGE_SUCCESS_ADD = "Item successfully added.";
	public static final String MESSAGE_SUCCESS_DELETE = "Item successfully deleted.";
	public static final String MESSAGE_SUCCESS_EDIT = "Item successfully edited.";
	public static final String MESSAGE_SUCCESS_EXIT = "Exiting program...";
	public static final String MESSAGE_SUCCESS_CHANGE_FILE_PATH = "File path successfully changed.";
	public static final String MESSAGE_SUCCESS_SAME_FILE_PATH = "File path not changed.";
	public static final String MESSAGE_DISPLAY_TASKLINE_INDEX = "%d. ";
	public static final String MESSAGE_DISPLAY_NEWLINE = "\r\n"; // isolated this string for ease of concatenation
	public static final String MESSAGE_DISPLAY_EMPTY = "No items to display.";
	public static final String SEPARATOR_DISPLAY_FIELDS = " | ";
	public static final String ERROR_WRITING_FILE = "Error: Unable to write file.";
	public static final String ERROR_CREATING_FILE = "Error: Unable to create file.";
	public static final String ERROR_FILE_NOT_FOUND = "Error: File not found";
	public static final String ERROR_INVALID_ARGUMENT = "Error: Invalid argument for command";
	public static final String ERROR_INVALID_COMMAND = "Error: Invalid command.";
	public static final String ERROR_NO_COMMAND_HANDLER = "Error: Handler for this command type has not been defined.";
	public static final String ERROR_INVALID_INDEX = "Error: There is no item at this index.";
	public static final String ERROR_UI_INTERRUPTED = "Error: UI prompt has been interrupted";
	public static final String ERROR_NO_HISTORY = "Error: No history found";
	public static final String ERROR_CANNOT_WRITE_TO_HISTORY = "Error: Unable to store command in history";
	/*
	 * Main program
	 */
	public static void main(String[] args) {
		Logic logicObject = new Logic();
		logicObject.start();
	}
	
	/*
	 * Constructor to initialize object variables
	 */
	public Logic() {
		UIObject = new UI();
		parserObject = new Parser();
		storageObject = new Storage();
		historyObject = new History();
		try {
			listOfTasks = parserObject.parseFileData(storageObject.getItemList());
		} catch (FileNotFoundException e) {
			UIObject.showToUser(ERROR_FILE_NOT_FOUND);
		}
	}
	
	void start() {
		showWelcomeMessage();
		readAndExecuteUserInput();
	}
	
	void showWelcomeMessage() {
		UIObject.showToUser(MESSAGE_WELCOME);
	}
	
	/**
	 * Repeatedly
	 *  Reads the user input, parses the command, executes the command object,
	 *  shows the result in UI, writes latest task list to file
	 * until the program exits
	 */
	void readAndExecuteUserInput() {
		while (true) {
			try {
				String userInput = UIObject.promptUser(MESSAGE_PROMPT_COMMAND);
				Command commandObject = parserObject.parseCommand(userInput);
				String executionResult = executeCommand(commandObject, true);
				UIObject.showToUser(executionResult);
				storageObject.writeItemList(listOfTasks);
			} catch (InterruptedException e) {
				// something interrupted the UI's wait for user input
				UIObject.showToUser(ERROR_UI_INTERRUPTED);
			} catch (IOException e) {
				// error writing
				UIObject.showToUser(ERROR_WRITING_FILE);
			} catch (Exception e) {
				// warning from parsing user command
				UIObject.showToUser(e.getMessage());
			}
		}
	}
	
	/**
	 * Executes a command based on commandObject
	 * @return status string to be shown to user
	 */
	String executeCommand(Command commandObject, boolean shouldPushToHistory) {
		if (commandObject == null) {
			return ERROR_INVALID_COMMAND;
		}
		Command.Type commandType = commandObject.getCommandType();
		Task userTask = commandObject.getTask();
		ArrayList<String> argumentList = commandObject.getArguments();
		switch (commandType) {
			case ADD :
				return addItem(userTask, argumentList, shouldPushToHistory);
			case DELETE :
				historyObject.pushCommand(commandObject);
				return deleteItem(argumentList, shouldPushToHistory);
			case EDIT :
				historyObject.pushCommand(commandObject);
				return editItem(userTask, argumentList, shouldPushToHistory);
			case DISPLAY :
				return displayItems();
			case UNDO :
				return undoCommand();
			case SAVEPATH :
				return saveFilePath(argumentList);
			case EXIT :
				return exitProgram();
			default:
		}
		return ERROR_NO_COMMAND_HANDLER;
	}
	
	String addItem(Task userTask, ArrayList<String> argumentList, boolean shouldPushToHistory) {
		try {
			int index;
			if (isEmptyArgumentList(argumentList)) {
				index = listOfTasks.size();
			} else {
				index = Integer.parseInt(argumentList.get(0)) - 1;
			}
			listOfTasks.add(index, userTask);
			
			if(shouldPushToHistory){
				//	handle history
				String[] indexString = {Integer.toString(index + 1)};
				if (!pushToHistory(new Command(Command.Type.DELETE, indexString))) {
					return ERROR_CANNOT_WRITE_TO_HISTORY;
				}
			}
			
			return MESSAGE_SUCCESS_ADD;	
		} catch (NumberFormatException e) {
			return ERROR_INVALID_ARGUMENT;
		}
	}
	
	/**
	 * Deletes an item from the list of tasks in memory
	 * @param argumentList the index string is read from position 0
	 * @return status string
	 */
	String deleteItem(ArrayList<String> argumentList, boolean shouldPushToHistory) {
		if (isEmptyArgumentList(argumentList)) {
			return ERROR_INVALID_ARGUMENT;
		}
		try {
			int index = Integer.parseInt(argumentList.get(0)) - 1;
			if (isValidIndex(index)) {
				
				//handle history
				if(shouldPushToHistory){
					Task taskRemoved = listOfTasks.get(index);
					String[] indexString = {Integer.toString(index + 1)};
					if (!pushToHistory(new Command(Command.Type.ADD, indexString, taskRemoved))) {
						return ERROR_CANNOT_WRITE_TO_HISTORY;
					}
				}
				
				listOfTasks.remove(index);
			} else {
				return ERROR_INVALID_INDEX;
			}
			
			return MESSAGE_SUCCESS_DELETE;
		} catch (NumberFormatException e) {
			return ERROR_INVALID_ARGUMENT;
		}
		
	}
	
	/**
	 * Replaces an item from the list of tasks in memory with the new userTask
	 * @param userTask the new task to be replaced with
	 * @param argumentList the index string is read from position 0
	 * @return status string
	 */
	String editItem(Task userTask, ArrayList<String> argumentList, boolean shouldPushToHistory) {
		if (isEmptyArgumentList(argumentList)) {
			return ERROR_INVALID_ARGUMENT;
		}
		try {
			int index = Integer.parseInt(argumentList.get(0)) - 1;
			if (isValidIndex(index)) {
				
				if(shouldPushToHistory){
					//	handle history
					Task taskEdited = listOfTasks.get(index);
					String[] indexString = {Integer.toString(index + 1)};
					if (!pushToHistory(new Command(Command.Type.ADD, indexString, taskEdited))) {
						return ERROR_CANNOT_WRITE_TO_HISTORY;
					}
				}
				
				listOfTasks.remove(index);
			} else {
				return ERROR_INVALID_INDEX;
			}
			listOfTasks.add(index, userTask);
		} catch (NumberFormatException e) {
			return ERROR_INVALID_ARGUMENT;
		}
		return MESSAGE_SUCCESS_EDIT;
	}
	
	boolean isValidIndex(int index) {
		return index >= 0 && index < listOfTasks.size();
	}
	
	/**
	 * @return string to be displayed, in the form of "[taskname] ;[date]"
	 */
	String displayItems() {
		if (listOfTasks.isEmpty()) {
			return MESSAGE_DISPLAY_EMPTY;
		}
		String stringToDisplay = "";
		for (int i = 0; i < listOfTasks.size(); i++) {
			Task curTask = listOfTasks.get(i);
			stringToDisplay += String.format(MESSAGE_DISPLAY_TASKLINE_INDEX, i + 1);
			if(curTask != null){
				stringToDisplay += listOfTasks.get(i).getName();
				if (curTask. getEndingTime() != null) {
					stringToDisplay += SEPARATOR_DISPLAY_FIELDS + dateFormat.format(listOfTasks.get(i). getEndingTime().getTime());
				}
			}
			stringToDisplay += MESSAGE_DISPLAY_NEWLINE;
		}
		return stringToDisplay;
	}
	
	String undoCommand(){
		Command previousCommand = historyObject.getPreviousCommand();
		if (previousCommand == null) {
			return ERROR_NO_HISTORY;
		}
		return executeCommand(previousCommand, false);
	}
		
	boolean pushToHistory(Command commandObject){
		return historyObject.pushCommand(commandObject);
	}
	
	String saveFilePath(ArrayList<String> argumentList){
		if (isEmptyArgumentList(argumentList)) {
			return ERROR_INVALID_ARGUMENT;
		}
		String filePath = argumentList.get(0);
		try {
			boolean locationChanged = storageObject.saveFileToPath(filePath);
			if (locationChanged) {
				return MESSAGE_SUCCESS_CHANGE_FILE_PATH;
			} else {
				return MESSAGE_SUCCESS_SAME_FILE_PATH;
			}
		} catch (IOException e) {
			return ERROR_CREATING_FILE;
		}
	}
	
	boolean isEmptyArgumentList(ArrayList<String> argumentList) {
		if (argumentList == null || argumentList.isEmpty()) {
			return true;
		}
		return false;
	}
	
	String exitProgram() {
		System.exit(1);
		return MESSAGE_SUCCESS_EXIT;
	}
}
