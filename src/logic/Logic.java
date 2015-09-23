package logic;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import ui.UI;
import parser.Parser;
import storage.Storage;
import global.Command;
import global.Task;

public class Logic {
	
	/*
	 * Declaration of object variables
	 */
	UI UIObject;
	Parser parserObject;
	Storage storageObject;
	ArrayList<Task> listOfTasks = new ArrayList<Task>();
	
	/*
	 * Static strings - errors, warnings and messages
	 */
	public static String MESSAGE_WELCOME = "Welcome to Tasky! This is an open source project";
	public static String MESSAGE_PROMPT_COMMAND = "command :";
	public static String MESSAGE_SUCCESS_ADD = "Item successfully added.";
	public static String MESSAGE_SUCCESS_DELETE = "Item successfully deleted.";
	public static String MESSAGE_SUCCESS_EDIT = "Item successfully edited.";
	public static String MESSAGE_SUCCESS_EXIT = "Exiting program...";
	public static String MESSAGE_DISPLAY_TASKLINE = "%d. %s\r\n";
	public static String ERROR_WRITING_FILE = "Error writing file.";
	public static String ERROR_FILE_NOT_FOUND = "Error file not found";
	public static String WARNING_INVALID_ARGUMENT = "Warning: Invalid argument for command";
	public static String WARNING_INVALID_COMMAND = "Warning: Invalid command.";
	public static String WARNING_NO_COMMAND_HANDLER = "Warning: Handler for this command type has not been defined.";
	public static String WARNING_INVALID_INDEX = "Warning: There is no item at this index.";
	public static String WARNING_UI_INTERRUPTED = "Warning: UI prompt has been interrupted";
	
	
	/*
	 * Main program
	 */
	public static void main(String[] args){
		Logic logicObject = new Logic();
		logicObject.start();
	}
	/*
	 * Constructor to initialize object variables
	 */
	public Logic(){
		UIObject = new UI();
		parserObject = new Parser();
		storageObject = new Storage();
		try {
			listOfTasks = storageObject.getItemList();
		} catch (FileNotFoundException e) {
			UIObject.showToUser(ERROR_FILE_NOT_FOUND);
		}
	}
	public void start(){
		showWelcomeMessage();
		readUserInput();
	}
	public void showWelcomeMessage(){
		UIObject.showToUser(MESSAGE_WELCOME);
	}
	
	/**
	 * Repeatedly
	 *  Reads the user input, parses the command, executes the command object,
	 *  shows the result in UI, writes latest task list to file
	 * until the program exits
	 */
	public void readUserInput(){
		try {
			while (true) {
				String userInput = UIObject.promptUser(MESSAGE_PROMPT_COMMAND);
				Command commandObject = parserObject.parseCommand(userInput);
				String executionResult = executeCommand(commandObject);
				UIObject.showToUser(executionResult);
				storageObject.writeItemList(listOfTasks);
			}
		} catch (InterruptedException e) {
			// something interrupted the UI's wait for user input
			UIObject.showToUser(WARNING_UI_INTERRUPTED);
		} catch (IOException e) {
			// error writing
			UIObject.showToUser(ERROR_WRITING_FILE);
		}
		
	}
	
	/**
	 * Executes a command based on commandObject
	 * @return a string to be shown to user
	 */
	public String executeCommand(Command commandObject){
		if(commandObject == null){
			return WARNING_INVALID_COMMAND;
		}
		Command.Type commandType = commandObject.getCommandType();
		Task userTask = commandObject.getTask();
		ArrayList<String> argumentList = commandObject.getArguments();
		if(commandType == Command.Type.add){
			return addItem(userTask);
		}else if(commandType == Command.Type.delete){
			return deleteItem(argumentList);
		}else if(commandType == Command.Type.edit){
			return editItem(userTask, argumentList);
		}else if(commandType == Command.Type.display){
			return displayItems();
		}else if(commandType == Command.Type.exit){
			return exitProgram();
		}
		return WARNING_NO_COMMAND_HANDLER;
	}
	
	public String addItem(Task userTask){
		listOfTasks.add(userTask);
		return MESSAGE_SUCCESS_ADD;
	}
	
	/**
	 * Deletes an item from the list of tasks in memory
	 * @param argumentList the index string is read from position 0
	 * @return status string
	 */
	public String deleteItem(ArrayList<String> argumentList){
		if(argumentList == null || argumentList.isEmpty()){
			return WARNING_INVALID_ARGUMENT;
		}
		try {
			int index = Integer.parseInt(argumentList.get(0)) - 1;
			if(isValidIndex(index)){
				listOfTasks.remove(index);
			}else{
				return WARNING_INVALID_INDEX;
			}
			return MESSAGE_SUCCESS_DELETE;
		} catch (NumberFormatException e){
			return WARNING_INVALID_ARGUMENT;
		}
		
	}
	
	/**
	 * Replaces an item from the list of tasks in memory with the new userTask
	 * @param userTask the new task to be replaced with
	 * @param argumentList the index string is read from position 0
	 * @return status string
	 */
	public String editItem(Task userTask, ArrayList<String> argumentList){
		if(argumentList == null || argumentList.isEmpty()){
			return WARNING_INVALID_ARGUMENT;
		}
		try {
			int index = Integer.parseInt(argumentList.get(0)) - 1;
			if(isValidIndex(index)){
				listOfTasks.remove(index);
			}else{
				return WARNING_INVALID_INDEX;
			}
			listOfTasks.add(index, userTask);
		} catch (NumberFormatException e) {
			return WARNING_INVALID_ARGUMENT;
		}
		return MESSAGE_SUCCESS_EDIT;
	}
	public boolean isValidIndex(int index){
		return !(index > listOfTasks.size() - 1 || index < 0);
	}
	
	/**
	 * @return string to be displayed
	 */
	public String displayItems(){
		String stringToDisplay = "";
		for(int i = 0; i < listOfTasks.size(); i++){
			stringToDisplay += String.format(MESSAGE_DISPLAY_TASKLINE, i+1, listOfTasks.get(i).getName());
		}
		return stringToDisplay;
	}
	public String exitProgram(){
		System.exit(1);
		return MESSAGE_SUCCESS_EXIT;
	}
}
