package global;

public class Command {

	private Type commandType;
	private Task task;
	
	public enum Type {
		add, edit, delete, display, exit;
	}
	
	public Command(Type commandType, Task task){ 
		this.commandType = commandType;
		this.task = task;
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
