package global;

public class Command {

	private Type commandType;
	private String description;
	
	public enum Type {
		add, edit, delete, display, exit;
	}
	
	public Command(Type commandType, String description) {
		this.commandType = commandType;
		this.description = description;
	}
	
	public Type getCommandType() {
		return commandType;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setCommandType(Type commandType) {
		this.commandType = commandType;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
}
