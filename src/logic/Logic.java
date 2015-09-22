package logic;
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
	ArrayList<com.sun.jmx.snmp.tasks.Task> listOfTasks = new ArrayList<Task>();
	
	public static String MESSAGE_WELCOME = "Welcome to Tasky! This is an open source project";
	public static String MESSAGE_PROMPT_COMMAND = "command :";
	
	public static void main(String[] args){
		Logic logicObject = new Logic();
		logicObject.start();
	}
	public Logic(){
		UIObject = new UI();
		parserObject = new parserObject();
		storageObject = new storageObject();
	}
	public void start(){
		showWelcomeMessage();
		readUserInput();
	}
	public void showWelcomeMessage(){
		UIObject.showToUser(MESSAGE_WELCOME);
	}
	public void readUserInput(){
		String userInput = UIObject.promptUser(MESSAGE_PROMPT_COMMAND);
		Command commandObject = parserObject.parseCommand(userInput);
		boolean executionResult = executeCommand(commandObject);
	}
	public boolean executeCommand(Command commandObject){
		Command.Type commandType = commandObject.getCommandType();
		Task userTask = commandObject.getTask();
		if(commandType == Command.Type.add){
			addItem(userTask);
		}else if(commandType == Command.Type.delete){
			deleteItem(userTask);
		}else if(commandType == Command.Type.edit){
			editItem(userTask);
		}else if(commandType == Command.Type.display){
			displayItems();
		}else if(commandType == Command.Type.exit){
			exitProgram();
		}
	}
	
	public boolean addItem(Task userTask){
		listOfTasks.add(userTask);
	}
	public boolean deleteItem(Task userTask){
		int index = userTask.getIndex();
		listOfTasks.remove(index);
	}
	public boolean editItem(Task userTask){
		int index = userTask.getIndex();
		listOfTasks.remove(index);
		listOfTasks.add(index, userTask);
	}
	public boolean displayItems(){
		String stringToDisplay = "";
		for(int i = 0; i < listOfTasks.size(); i++){
			stringToDisplay += (i+1) + ". " + listOfTasks.get(i) + "\n";
		}
		UIObject.showToUser(stringToDisplay);
	}
	public boolean exitProgram(){
		System.exit(1);
	}
}
