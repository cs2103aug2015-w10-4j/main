package global;

import java.util.ArrayList;

public class Command {

	private Type commandType;
	private ArrayList<String> argumentList;
	private Task task;
	
	public enum Type {
		ADD, EDIT, DELETE, DISPLAY, EXIT, SAVEPATH, UNDO;
	}
	
	public Command(Type commandType) { 
		setCommandType(commandType);
		this.task = null;
		this.argumentList = null;
	}
	
	public Command(Type commandType, Task task) { 
		setCommandType(commandType);
		setTask(task);
		this.argumentList = null;
	}
	
	public Command(Type commandType, String[] args) { 
		setCommandType(commandType);
		setArguments(args);
		this.task = null;
	}
	
	public Command(Type commandType, String[] args, Task task) { 
		setCommandType(commandType);
		setTask(task);
		setArguments(args);
	}
	
	// --------------- getter methods --------------------
	public Type getCommandType() {
		return commandType;
	}
	
	public Task getTask() {
		return task;
	}
	
	public ArrayList<String> getArguments() {
		return argumentList;
	}
	
	// --------------- setter methods ------------------- 
	public void setCommandType(Type commandType) {
		this.commandType = commandType;
	}
	
	public void setTask(Task task) {
		this.task = task;
	}
	
	public void setArguments(String[] args) {
		argumentList = new ArrayList<String>();
		for(int i = 0; i < args.length; i++){
			argumentList.add(args[i]);
		}
	}
}
