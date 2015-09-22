package logic;
import java.io.IOException;
import java.util.ArrayList;

import ui.UI;
import parser.Parser;
import storage.Storage;
import global.Command;
import global.Task;

public class Logic {
	UI UIObject;
	Parser parserObject;
	Storage storageObject;
	ArrayList<Task> listOfTasks = new ArrayList<Task>();
	
	public static String MESSAGE_WELCOME = "Welcome to Tasky! This is an open source project";
	public static String MESSAGE_PROMPT_COMMAND = "command :";
	public static String MESSAGE_SUCCESS_ADD = "Item successfull added.";
	public static String MESSAGE_SUCCESS_DELETE = "Item successfull deleted.";
	public static String MESSAGE_SUCCESS_EDIT = "Item successfull edited.";
	public static String MESSAGE_SUCCESS_EXIT = "Exiting program...";
	public static String ERROR_WRITING_FILE = "Error writing file.";
	public static String WARNING_INVALID_ARGUMENT = "Warning: Invalid argument for command";
	public static String WARNING_INVALID_COMMAND = "Warning: Invalid command.";
	public static String WARNING_NO_COMMAND_HANDLER = "Warning: Handler for this command type has not been defined.";
	public static String WARNING_INVALID_INDEX = "Warning: There is no item at this index.";
	
	public static void main(String[] args){
		Logic logicObject = new Logic();
		logicObject.start();
	}
	public Logic(){
		UIObject = new UI();
		parserObject = new Parser();
		storageObject = new Storage();
	}
	public void start(){
		showWelcomeMessage();
		readUserInput();
	}
	public void showWelcomeMessage(){
		UIObject.showToUser(MESSAGE_WELCOME);
	}
	public void readUserInput(){
		
		try {
			while (true) {
				String userInput = UIObject.promptUser(MESSAGE_PROMPT_COMMAND);
				Command commandObject = parserObject.parseCommand(userInput);
				String executionResult = executeCommand(commandObject);
				UIObject.showToUser(executionResult);
				storageObject.writeitem(listOfTasks);
			}
		} catch (InterruptedException e) {
			// 
			e.printStackTrace();
		} catch (IOException e) {
			// error writing
			UIObject.showToUser(ERROR_WRITING_FILE);
		}
		
	}
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
	public String deleteItem(ArrayList<String> argumentList){
		if(argumentList == null || argumentList.isEmpty()){
			return WARNING_INVALID_ARGUMENT;
		}
		int index = Integer.parseInt(argumentList.get(0));
		if(argumentList.size() < index || index < 1){
			return WARNING_INVALID_INDEX;
		}else{
			listOfTasks.remove(index - 1);
		}
		return MESSAGE_SUCCESS_DELETE;
	}
	public String editItem(Task userTask, ArrayList<String> argumentList){
		if(argumentList == null || argumentList.isEmpty()){
			return WARNING_INVALID_ARGUMENT;
		}
		int index = Integer.parseInt(argumentList.get(0));
		listOfTasks.remove(index);
		listOfTasks.add(index, userTask);
		return MESSAGE_SUCCESS_EDIT;
	}
	public String displayItems(){
		String stringToDisplay = "";
		for(int i = 0; i < listOfTasks.size(); i++){
			stringToDisplay += (i+1) + ". " + listOfTasks.get(i).getName() + "\n";
		}
		return stringToDisplay;
	}
	public String exitProgram(){
		System.exit(1);
		return MESSAGE_SUCCESS_EXIT;
	}
}
