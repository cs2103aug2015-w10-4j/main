package global;

import java.util.ArrayList;

public class Command {

	private Type commandType;
	private ArrayList<String> argumentList;
	private ArrayList<Task> tasks;
	
	public enum Type {
		ADD, EDIT, DELETE, DISPLAY, EXIT, SAVETO, UNDO, REDO;
	}
	
	public Command(Type commandType) { 
		setCommandType(commandType);
		this.tasks = null;
		this.argumentList = null;
	}
	
	public Command(Type commandType, Task task) { 
		setCommandType(commandType);
		addTask(task);
		this.argumentList = null;
	}
	
	public Command(Type commandType, String[] args) { 
		setCommandType(commandType);
		setArguments(args);
		this.tasks = null;
	}
	
	public Command(Type commandType, String[] args, Task task) { 
		setCommandType(commandType);
		addTask(task);
		setArguments(args);
	}
	
	public Command(Type commandType, String[] args, ArrayList<Task> tasks) { 
		setCommandType(commandType);
		addTasks(tasks);
		setArguments(args);
	}
	
	// --------------- getter methods --------------------
	public Type getCommandType() {
		return commandType;
	}
	
	public ArrayList<Task> getTasks() {
		return tasks;
	}
	
	public Task getTask(int index) {
		if(tasks != null && tasks.size() > index) {
			return tasks.get(index);
		} else {
			return null;
		}
		
	}
	
	public ArrayList<String> getArguments() {
		return argumentList;
	}
	
	// --------------- setter methods ------------------- 
	public void setCommandType(Type commandType) {
		this.commandType = commandType;
	}
	
	public void addTask(Task task) {
		if (this.tasks == null) {
			this.tasks = new ArrayList<Task>();
		}
		this.tasks.add(task);
	}
	
	public void addTasks(ArrayList<Task> tasks) {
		for(int i = 0; i < tasks.size(); i++){
			addTask(tasks.get(i));
		}
	}
	
	public void setArguments(String[] args) {
		argumentList = new ArrayList<String>();
		for(int i = 0; i < args.length; i++){
			argumentList.add(args[i]);
		}
	}
}
