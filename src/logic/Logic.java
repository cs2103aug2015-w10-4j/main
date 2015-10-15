package logic;

import global.Command;
import global.Task;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LogManager;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

import parser.Parser;
import storage.Storage;
import storage.JsonFormatStorage;
import ui.UI;


/**
 * This file contains the main program of the command-line calendar, Tasky.
 * Please read our user guide at README.md if there are any questions.
 * 
 * @author cs2103aug2015-w10-4j
 *
 */
public class Logic {
	
	/*
	 * Declaration of object variables
	 */
	Logger logger = Logger.getGlobal();
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
	private static final String MESSAGE_WELCOME = "Welcome to Tasky! This is an open source project";
	private static final String MESSAGE_PROMPT_COMMAND = "command :";

	private static final String MESSAGE_SUCCESS_UNDO_ADD = "Undo : Deleted item restored.";
	private static final String MESSAGE_SUCCESS_REDO_ADD = "Redo : Deleted item restored.";
	private static final String MESSAGE_SUCCESS_UNDO_DELETE = "Undo : Added item removed.";
	private static final String MESSAGE_SUCCESS_REDO_DELETE = "Redo : Added item removed.";
	private static final String MESSAGE_SUCCESS_UNDO_EDIT = "Undo : Reverted edits.";
	private static final String MESSAGE_SUCCESS_REDO_EDIT = "Redo : Reverted edits.";
	private static final String MESSAGE_SUCCESS_ADD = "Item successfully added.";
	private static final String MESSAGE_SUCCESS_DELETE = "Item successfully deleted.";
	private static final String MESSAGE_SUCCESS_EDIT = "Item successfully edited.";
	private static final String MESSAGE_SUCCESS_EXIT = "Exiting program...";
	private static final String MESSAGE_SUCCESS_DISPLAY = "Displaying items.";
	private static final String MESSAGE_SUCCESS_CHANGE_FILE_PATH = "File path successfully changed.";
	private static final String MESSAGE_SUCCESS_NO_CHANGE_FILE_PATH = "File path not changed. Entered file path is the same as current one used.";
	private static final String MESSAGE_DISPLAY_TASKLINE_INDEX = "%3d. ";
	private static final String MESSAGE_DISPLAY_NEWLINE = "\r\n";
	private static final String MESSAGE_DISPLAY_EMPTY = "No items to display.";
	private static final String SEPARATOR_DISPLAY_FIELDS = " | ";
	private static final String ERROR_WRITING_FILE = "Error: Unable to write file.";
	private static final String ERROR_CREATING_FILE = "Error: Unable to create file.";
	private static final String ERROR_FILE_NOT_FOUND = "Error: Data file not found.";
	private static final String ERROR_LOG_FILE_INITIALIZE = "Error: Cannot initialize log file.";
	private static final String ERROR_INVALID_ARGUMENT = "Error: Invalid argument for command.";
	private static final String ERROR_INVALID_COMMAND = "Error: Invalid command.";
	private static final String ERROR_NO_COMMAND_HANDLER = "Error: Handler for this command type has not been defined.";
	private static final String ERROR_INVALID_INDEX = "Error: There is no item at this index.";
	private static final String ERROR_UI_INTERRUPTED = "Error: UI prompt has been interrupted.";
	private static final String ERROR_NO_HISTORY = "Error: No history found.";
	private static final String ERROR_CANNOT_WRITE_TO_HISTORY = "Error: Unable to store command in history.";
	private static final String ERROR_TIMING_CLASH = "Error: There are clashing timings between tasks.";
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
		storageObject = new JsonFormatStorage(true);
		historyObject = new History();
		try {
			FileHandler logHandler = new FileHandler("tasky.log");
			LogManager.getLogManager().reset(); // removes printout to console aka root handler
			logHandler.setFormatter(new SimpleFormatter()); // set output to a human-readable log format
			logger.addHandler(logHandler);
			logger.setLevel(Level.INFO); // setting of log level
			
			listOfTasks = storageObject.getItemList();
		} catch (FileNotFoundException e) {
			UIObject.showToUser(ERROR_FILE_NOT_FOUND);
		} catch (SecurityException|IOException e) {
			UIObject.showToUser(ERROR_LOG_FILE_INITIALIZE);
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
				String executionResult = executeCommand(commandObject, true, true);
				UIObject.showStatusToUser(executionResult);
				showUpdatedItems();
				storageObject.writeItemList(listOfTasks);
			} catch (InterruptedException e) {
				// something interrupted the UI's wait for user input
				UIObject.showStatusToUser(ERROR_UI_INTERRUPTED);
			} catch (IOException e) {
				// error writing
				UIObject.showStatusToUser(ERROR_WRITING_FILE);
			} catch (Exception e) {
				// warning from parsing user command
				UIObject.showStatusToUser(e.getMessage());
			}
		}
	}
	
	/**
	 * Executes a command based on commandObject
	 * @return status string to be shown to user
	 */
	String executeCommand(Command commandObject, boolean shouldPushToHistory, boolean isUndoHistory) {
		if (commandObject == null) {
			return ERROR_INVALID_COMMAND;
		}
		
		Command.Type commandType = commandObject.getCommandType();
		Task userTask = commandObject.getTask();
		ArrayList<String> argumentList = commandObject.getArguments();
		
		switch (commandType) {
			case ADD :
				logger.info("ADD command detected");
				return addItem(userTask, argumentList, shouldPushToHistory, isUndoHistory);
			case DELETE :
				logger.info("DELETE command detected");
				return deleteItem(argumentList, shouldPushToHistory, isUndoHistory);

			case EDIT :
				logger.info("EDIT command detected");
				return editItem(userTask, argumentList, shouldPushToHistory, isUndoHistory);
			case DISPLAY :
				logger.info("DISPLAY command detected");
				return displayItems();
			case UNDO :
				logger.info("UNDO command detected");
				return undoCommand();
			case REDO :
				logger.info("REDO command detected");
				return redoCommand();
			case SAVETO :
				logger.info("SAVETO command detected");
				return saveFilePath(argumentList);
			case EXIT :
				logger.info("EXIT command detected");
				return exitProgram();
			default :
		}
		logger.warning("Command type cannot be identified!");
		return ERROR_NO_COMMAND_HANDLER;
	}
	
	/**
	 * Adds an item to the list of tasks in memory
	 * @param argumentList if empty, last element is used. if not, the index string is read from position 0
	 * @param shouldPushToHistory
	 * @return status string
	 */
	String addItem(Task userTask, ArrayList<String> argumentList, boolean shouldPushToHistory, boolean isUndoHistory) {
		try {
			logger.fine("Attempting to determine index.");
			int index;
			if (isEmptyArgumentList(argumentList)) {
				index = listOfTasks.size();
				logger.finer("No specified index. Defaulting to the end of list.");
			} else {
				index = Integer.parseInt(argumentList.get(0)) - 1;
				logger.finer("Index " + index + " specified.");
			}
			
			logger.fine("Checking for clashes.");
			if(hasClashes(userTask)){
				logger.finer("Clash in timing detected, exiting method.");
				return ERROR_TIMING_CLASH;
			}
			
			logger.fine("Adding task to list.");
			listOfTasks.add(index, userTask);
			
			
			logger.fine("Checking if command should be pushed to history.");
			if (shouldPushToHistory) {
				logger.finer("Pushing command to history.");
				
				logger.finer("Checking if command is called by undo.");
				if (isUndoHistory) {
					logger.finer("Command is NOT called by undo.");
					
					logger.finer("Attempting to reverse command and push it to history.");
					String[] indexString = {Integer.toString(index + 1)};
					if (!pushToHistory(new Command(Command.Type.DELETE, indexString))) {
						return ERROR_CANNOT_WRITE_TO_HISTORY;
					}
					return MESSAGE_SUCCESS_ADD;
				} else {
					logger.finer("Command is called by undo.");
					
					logger.finer("Attempting to reverse command and push it to undoHistory.");
					String[] indexString = {Integer.toString(index + 1)};
					if (!pushToUndoHistory(new Command(Command.Type.DELETE, indexString))) {
						return ERROR_CANNOT_WRITE_TO_HISTORY;
					}
					return MESSAGE_SUCCESS_UNDO_ADD;
				}
			} else {
				return MESSAGE_SUCCESS_REDO_ADD;
			}
		} catch (NumberFormatException e) {
			return ERROR_INVALID_ARGUMENT;
		}
	}
	
	/**
	 * Deletes an item from the list of tasks in memory
	 * @param argumentList the index string is read from position 0
	 * @param shouldPushToHistory
	 * @return status string
	 */
	String deleteItem(ArrayList<String> argumentList, boolean shouldPushToHistory, boolean isUndoHistory) {
		String result = "";
		if (isEmptyArgumentList(argumentList)) {
			return ERROR_INVALID_ARGUMENT;
		}
	
		try {
			logger.fine("Attempting to determine index.");
			
			argumentList = removeDuplicates(argumentList);
			Collections.sort(argumentList);
			
			
			for(int i = argumentList.size() - 1; i >= 0; i--) {
				int index = Integer.parseInt(argumentList.get(i)) - 1;
				if (isValidIndex(index)) {
					// for history
					Task taskRemoved = listOfTasks.remove(index);
					logger.fine("Task removed from list.");
				
					if(shouldPushToHistory){
						logger.fine("Pushing command to history.");
						if(isUndoHistory){
							logger.finer("Command is NOT called by undo.");
						
							logger.finer("Attempting to reverse command and push it to history.");
							//	handle history
							String[] indexString = {Integer.toString(index + 1)};
							if (!pushToHistory(new Command(Command.Type.ADD, indexString, taskRemoved))) {
								result = ERROR_CANNOT_WRITE_TO_HISTORY;
							}
							result +=  (index +1) + "th " +MESSAGE_SUCCESS_DELETE +" ";
						}else{
							logger.finer("Command is called by undo.");
						
							logger.finer("Attempting to reverse command and push it to undoHistory.");
							String[] indexString = {Integer.toString(index + 1)};
							if (!pushToUndoHistory(new Command(Command.Type.ADD, indexString, taskRemoved))) {
								//return ERROR_CANNOT_WRITE_TO_HISTORY;
								result = ERROR_CANNOT_WRITE_TO_HISTORY;
							}
							result = ERROR_CANNOT_WRITE_TO_HISTORY;
						}
					} else {
						//return MESSAGE_SUCCESS_REDO_DELETE;
						result += (index + 1) + "th " + MESSAGE_SUCCESS_REDO_DELETE +" ";
					}
				
				} else {
					//return ERROR_INVALID_INDEX;
					result += ERROR_INVALID_INDEX;
				}
			
			}
		} catch (NumberFormatException e) {
			return ERROR_INVALID_ARGUMENT;
		}
		return result;
		
	}
	
	/**
	 * Replaces an item from the list of tasks in memory with the new userTask
	 * @param userTask the new task to be replaced with
	 * @param argumentList the index string is read from position 0
	 * @return status string
	 */
	String editItem(Task userTask, ArrayList<String> argumentList, boolean shouldPushToHistory, boolean isUndoHistory) {
		if (isEmptyArgumentList(argumentList)) {
			return ERROR_INVALID_ARGUMENT;
		}
		try {
			logger.fine("Attempting to determine index.");
			int index = Integer.parseInt(argumentList.get(0)) - 1;
			if (isValidIndex(index)) {
				// for history
				Task taskEdited = listOfTasks.get(index);
				if(hasClashes(userTask)){
					return ERROR_TIMING_CLASH;
				}
				listOfTasks.remove(index);
				logger.fine("Old task removed from list.");
				listOfTasks.add(index, userTask);
				logger.fine("New task added to list.");
				
				
				if(shouldPushToHistory){
					logger.fine("Pushing command to history.");
					if(isUndoHistory){
						logger.finer("Command is NOT called by undo.");
						
						logger.finer("Attempting to reverse command and push it to history.");
						String[] indexString = {Integer.toString(index + 1)};
						if (!pushToHistory(new Command(Command.Type.EDIT, indexString, taskEdited))) {
							return ERROR_CANNOT_WRITE_TO_HISTORY;
						}
						return MESSAGE_SUCCESS_EDIT;
					}else{
						logger.finer("Command is called by undo.");
						
						logger.finer("Attempting to reverse command and push it to undoHistory.");
						String[] indexString = {Integer.toString(index + 1)};
						if (!pushToUndoHistory(new Command(Command.Type.EDIT, indexString, taskEdited))) {
							return ERROR_CANNOT_WRITE_TO_HISTORY;
						}
						return MESSAGE_SUCCESS_UNDO_EDIT;
					}
				}else{
					return MESSAGE_SUCCESS_REDO_EDIT;
				}
			} else {
				return ERROR_INVALID_INDEX;
			}
		} catch (NumberFormatException e) {
			return ERROR_INVALID_ARGUMENT;
		}
	}
	
	boolean isValidIndex(int index) {
		return index >= 0 && index < listOfTasks.size();
	}
	
	String displayItems(){
		if (listOfTasks.isEmpty()) {
			return MESSAGE_DISPLAY_EMPTY;
		}else{
		   // showUpdatedItems();
			return MESSAGE_SUCCESS_DISPLAY;
		}
	}
	
	/**
	 * @return calls the UI to display updated list of items
	 */
	boolean showUpdatedItems() {
		if (listOfTasks.isEmpty()) {
			UIObject.showToUser(MESSAGE_DISPLAY_EMPTY);
		}else{
			String stringToDisplay = "";
			for (int i = 0; i < listOfTasks.size(); i++) {
				Task curTask = listOfTasks.get(i);
				assert(curTask != null);
				
				// Index
				stringToDisplay += String.format(MESSAGE_DISPLAY_TASKLINE_INDEX, i + 1);
				
				// Name
				stringToDisplay += String.format("%-30.30s", curTask.getName());
				stringToDisplay += SEPARATOR_DISPLAY_FIELDS;
				
				// Date
				stringToDisplay += String.format("%-11s", (curTask.getEndingTime() != null ?
						dateFormat.format(curTask.getEndingTime().getTime()) : ""));
				stringToDisplay += SEPARATOR_DISPLAY_FIELDS;
				
				// Location
				stringToDisplay += String.format("%-15s", (curTask.getLocation() != null ?
						curTask.getLocation() : ""));
				stringToDisplay += SEPARATOR_DISPLAY_FIELDS;
				
				// Periodic
				stringToDisplay += String.format("%-15s", (curTask.getPeriodic() != null ?
						curTask.getPeriodic() : ""));
				
				stringToDisplay += MESSAGE_DISPLAY_NEWLINE;
			}
			UIObject.showToUser(stringToDisplay);
		}
		return true;
	}
	
	String undoCommand(){
		Command previousCommand = historyObject.getPreviousCommand(true);
		if (previousCommand == null) {
			return ERROR_NO_HISTORY;
		}
		return executeCommand(previousCommand, true, false);
	}
	
	String redoCommand(){
		Command previousCommand = historyObject.getPreviousCommand(false);
		if (previousCommand == null) {
			return ERROR_NO_HISTORY;
		}
		return executeCommand(previousCommand, false, false);
	}
		
	boolean pushToHistory(Command commandObject){
		return historyObject.pushCommand(commandObject, true);
	}
	
	boolean pushToUndoHistory(Command commandObject){
		return historyObject.pushCommand(commandObject, false);
	}
	
	/**
	 * Sets the data file path
	 * @param argumentList the file path string is read from position 0
	 * @return status string
	 */
	String saveFilePath(ArrayList<String> argumentList){
		if (isEmptyArgumentList(argumentList)) {
			return ERROR_INVALID_ARGUMENT;
		}
		String filePath = argumentList.get(0);
		try {
			boolean locationChanged = storageObject.saveFileToPath(filePath);
			listOfTasks = storageObject.getItemList();
			if (locationChanged) {
				return MESSAGE_SUCCESS_CHANGE_FILE_PATH;
			} else {
				return MESSAGE_SUCCESS_NO_CHANGE_FILE_PATH;
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
	
	boolean hasClashes(Task task){
		if(task.getStartingTime() != null && task.getEndingTime() != null){
			for(int i = 0; i < listOfTasks.size(); i++){
				Task curTaskToCheck = listOfTasks.get(i);
				
				if (task.getStartingTime() != null
						&& task.getEndingTime() != null 
						&& isClashing(task, curTaskToCheck)){
					return true;
				}
			}
		}
		return false;
	}
	
	boolean isClashing(Task taskOne, Task taskTwo){
		Calendar taskOneStart = taskOne.getStartingTime();
		Calendar taskOneEnd = taskOne.getEndingTime();
		Calendar taskTwoStart = taskOne.getStartingTime();
		Calendar taskTwoEnd = taskOne.getEndingTime();
		assert(!(taskOneStart == null));
		assert(!(taskOneEnd == null));
		assert(!(taskTwoStart == null));
		assert(!(taskTwoEnd == null));
		
		if((taskOneStart.before(taskTwoStart) && taskOneEnd.before(taskTwoStart))
				|| (taskTwoStart.before(taskOneStart) && taskTwoEnd.before(taskOneStart))) {
			return true;
		} else {
			return false;
		}
	}
	
	// Create an array with all unique elements
	public ArrayList<String> removeDuplicates(ArrayList<String> A) {

		// add elements to al, including duplicates
		HashSet<String> hs = new HashSet<>();
		hs.addAll(A);
		A.clear();
		A.addAll(hs);
		
	 return A;
	}
	
	String exitProgram() {
		System.exit(0);
		return MESSAGE_SUCCESS_EXIT;
	}
}