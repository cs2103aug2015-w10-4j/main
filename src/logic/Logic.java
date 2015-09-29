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
import history.History;

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
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
	
	/*
	 * Static strings - errors, warnings and messages
	 */
	public static final String MESSAGE_WELCOME = "Welcome to Tasky! This is an open source project";
	public static final String MESSAGE_PROMPT_COMMAND = "command :";
	public static final String MESSAGE_SUCCESS_ADD = "Item successfully added.";
	public static final String MESSAGE_SUCCESS_DELETE = "Item successfully deleted.";
	public static final String MESSAGE_SUCCESS_EDIT = "Item successfully edited.";
	public static final String MESSAGE_SUCCESS_EXIT = "Exiting program...";
	public static final String MESSAGE_DISPLAY_TASKLINE_INDEX = "%d. ";
	public static final String MESSAGE_DISPLAY_NEWLINE = "\r\n"; // isolated this string for ease of concatenation
	public static final String MESSAGE_DISPLAY_EMPTY = "No items to display.";
	public static final String SEPARATOR_ARGUMENTS = ";";
	public static final String SEPARATOR_DISPLAY_FIELDS = "|";
	public static final String ERROR_WRITING_FILE = "Error writing file.";
	public static final String ERROR_FILE_NOT_FOUND = "Error file not found";
	public static final String WARNING_INVALID_ARGUMENT = "Warning: Invalid argument for command";
	public static final String WARNING_INVALID_COMMAND = "Warning: Invalid command.";
	public static final String WARNING_NO_COMMAND_HANDLER = "Warning: Handler for this command type has not been defined.";
	public static final String WARNING_INVALID_INDEX = "Warning: There is no item at this index.";
	public static final String WARNING_UI_INTERRUPTED = "Warning: UI prompt has been interrupted";
	public static final String WARNING_NO_HISTORY = "Warning: No history found";
	public static final String WARNING_CANNOT_WRITE_TO_HISTORY = "Warning: Unable to store command in history";
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
			ArrayList<String> fileData = storageObject.getItemList();
			listOfTasks = parserObject.parseFileData(fileData);
		} catch (FileNotFoundException e) {
			UIObject.showToUser(ERROR_FILE_NOT_FOUND);
		}
	}
	
	public void start() {
		showWelcomeMessage();
		readUserInput();
	}
	
	public void showWelcomeMessage() {
		UIObject.showToUser(MESSAGE_WELCOME);
	}
	
	/**
	 * Repeatedly
	 *  Reads the user input, parses the command, executes the command object,
	 *  shows the result in UI, writes latest task list to file
	 * until the program exits
	 */
	public void readUserInput() {
		while (true) {
			try {
				String userInput = UIObject.promptUser(MESSAGE_PROMPT_COMMAND);
				Command commandObject = parserObject.parseCommand(userInput);
				String executionResult = executeCommand(commandObject);
				UIObject.showToUser(executionResult);
				storageObject.writeItemList(listOfTasks);
			} catch (InterruptedException e) {
				// something interrupted the UI's wait for user input
				UIObject.showToUser(WARNING_UI_INTERRUPTED);
			} catch (IOException e) {
				// error writing
				UIObject.showToUser(ERROR_WRITING_FILE);
			} catch (Exception e) {
				// error in user command
				UIObject.showToUser(e.getMessage());
			}
		}
	}
	
	/**
	 * Executes a command based on commandObject
	 * @return a string to be shown to user
	 */
	public String executeCommand(Command commandObject) {
		if (commandObject == null) {
			return WARNING_INVALID_COMMAND;
		}
		Command.Type commandType = commandObject.getCommandType();
		Task userTask = commandObject.getTask();
		ArrayList<String> argumentList = commandObject.getArguments();
		switch (commandType) {
			case ADD:
				return addItem(userTask, argumentList);
			case DELETE:
				historyObject.pushCommand(commandObject);
				return deleteItem(argumentList);
			case EDIT:
				historyObject.pushCommand(commandObject);
				return editItem(userTask, argumentList);
			case DISPLAY:
				return displayItems();
			case UNDO:
				return undoCommand();
			case EXIT:
				return exitProgram();
			default:
		}
		return WARNING_NO_COMMAND_HANDLER;
	}
	
	public String addItem(Task userTask, ArrayList<String> argumentList) {
		try {
			int index;
			if (argumentList == null || argumentList.isEmpty()) {
				index = listOfTasks.size();
			}else{
				index = Integer.parseInt(argumentList.get(0)) - 1;
			}
			listOfTasks.add(index, userTask);
			//handle history
			String[] indexString = {Integer.toString(index + 1)};
			if (!pushToHistory(new Command(Command.Type.DELETE, indexString))) {
				return WARNING_CANNOT_WRITE_TO_HISTORY;
			};
			
			return MESSAGE_SUCCESS_ADD;	
		} catch (NumberFormatException e) {
			return WARNING_INVALID_ARGUMENT;
		}
	}
	
	/**
	 * Deletes an item from the list of tasks in memory
	 * @param argumentList the index string is read from position 0
	 * @return status string
	 */
	public String deleteItem(ArrayList<String> argumentList) {
		if (argumentList == null || argumentList.isEmpty()) {
			return WARNING_INVALID_ARGUMENT;
		}
		try {
			int index = Integer.parseInt(argumentList.get(0)) - 1;
			if (isValidIndex(index)) {
				//handle history
				Task taskRemoved = listOfTasks.get(index);
				String[] indexString = {Integer.toString(index + 1)};
				if(!pushToHistory(new Command(Command.Type.ADD, indexString, taskRemoved))){
					return WARNING_CANNOT_WRITE_TO_HISTORY;
				};
				
				listOfTasks.remove(index);
			} else {
				return WARNING_INVALID_INDEX;
			}
			
			return MESSAGE_SUCCESS_DELETE;
		} catch (NumberFormatException e) {
			return WARNING_INVALID_ARGUMENT;
		}
		
	}
	
	/**
	 * Replaces an item from the list of tasks in memory with the new userTask
	 * @param userTask the new task to be replaced with
	 * @param argumentList the index string is read from position 0
	 * @return status string
	 */
	public String editItem(Task userTask, ArrayList<String> argumentList) {
		if (argumentList == null || argumentList.isEmpty()) {
			return WARNING_INVALID_ARGUMENT;
		}
		try {
			int index = Integer.parseInt(argumentList.get(0)) - 1;
			if (isValidIndex(index)) {
				//handle history
				Task taskEdited = listOfTasks.get(index);
				String[] indexString = {Integer.toString(index + 1)};
				if(!pushToHistory(new Command(Command.Type.ADD, indexString, taskEdited))){
					return WARNING_CANNOT_WRITE_TO_HISTORY;
				};
				
				listOfTasks.remove(index);
			} else {
				return WARNING_INVALID_INDEX;
			}
			listOfTasks.add(index, userTask);
		} catch (NumberFormatException e) {
			return WARNING_INVALID_ARGUMENT;
		}
		return MESSAGE_SUCCESS_EDIT;
	}
	
	public boolean isValidIndex(int index) {
		return index >= 0 && index < listOfTasks.size();
	}
	
	/**
	 * @return string to be displayed, in the form of "[taskname] ;[date]"
	 */
	public String displayItems() {
		if (listOfTasks.isEmpty()) {
			return MESSAGE_DISPLAY_EMPTY;
		}
		String stringToDisplay = "";
		for (int i = 0; i < listOfTasks.size(); i++) {
			Task curTask = listOfTasks.get(i);
			stringToDisplay += String.format(MESSAGE_DISPLAY_TASKLINE_INDEX, i + 1);
			if(curTask != null){
				stringToDisplay += listOfTasks.get(i).getName();
				if (curTask.getDate() != null) {
					stringToDisplay += SEPARATOR_DISPLAY_FIELDS + dateFormat.format(listOfTasks.get(i).getDate().getTime());
				}
			}
				stringToDisplay += MESSAGE_DISPLAY_NEWLINE;
		}
		return stringToDisplay;
	}
	
	public String undoCommand(){
		Command previousCommand = historyObject.getPreviousCommand();
		if(previousCommand == null){
			return WARNING_NO_HISTORY;
		}
		return executeCommand(previousCommand);
	}
		
	public boolean pushToHistory(Command commandObject){
		return historyObject.pushCommand(commandObject);
	}
	
	public String exitProgram() {
		System.exit(1);
		return MESSAGE_SUCCESS_EXIT;
	}
}
