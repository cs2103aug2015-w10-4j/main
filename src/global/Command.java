package global;

import java.util.ArrayList;

public class Command {

	private Type commandType;
	private ArrayList<String> argumentList;
	private Task task;
	
	public enum Type {
		add, edit, delete, display, exit;
	}
	
	public Command(Type commandType){ 
		this.commandType = commandType;
	}
	public Command(Type commandType, Task task){ 
		this.commandType = commandType;
		this.task = task;
	}
	
	public Command(Type commandType, String[] args){ 
		this.commandType = commandType;
		for(int i = 0; i < args.length; i++){
			argumentList.add(args[i]);
		}
	}
	
	public Command(Type commandType, String[] args, Task task){ 
		this.commandType = commandType;
		this.task = task;
		for(int i = 0; i < args.length; i++){
			argumentList.add(args[i]);
		}
	}
	
	// --------------- getter methods --------------------
	public Type getCommandType() {
		return commandType;
	}
	
	public Task getTask() {
		return task;
	}
	
	// --------------- setter methods ------------------- 
	public void setCommandType(Type commandType) {
		this.commandType = commandType;
	}
	
	public void setTask(Task task) {
		this.task = task;
	}
}
