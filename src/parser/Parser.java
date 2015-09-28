package parser;

import global.Command;
import global.Task;

public class Parser {
	
	/**
	 * Parses the command string based on keyword
	 * @param command
	 * @return commandObject to be executed, or null if invalid
	 */
	
	// warning messages
	private static final String WARNING_INSUFFICIENT_ARGUMENT = "Warning: '%s': insufficient command arguments";
	
	public Command parseCommand(String command) throws Exception {
		String[] args = command.split(" ");
		Command.Type currentCommand;
		Command commandObject;
		if (args[0].equalsIgnoreCase("add")) {
			currentCommand = Command.Type.ADD;
			try {
				commandObject = new Command(currentCommand, new Task(args[1]));
			}
			catch (ArrayIndexOutOfBoundsException e) {
				throw new Exception(String.format(WARNING_INSUFFICIENT_ARGUMENT, args[0]));
			}
		} else if (args[0].equalsIgnoreCase("edit")) {
			currentCommand = Command.Type.EDIT;
			if (args.length >= 3) {
				String[] indexToDelete = {args[1]};
				commandObject = new Command(currentCommand, indexToDelete, new Task(args[2]));
			} else {
				throw new Exception(String.format(WARNING_INSUFFICIENT_ARGUMENT, args[0]));
			}
		} else if (args[0].equalsIgnoreCase("delete")) {
			currentCommand = Command.Type.DELETE;
			if (args.length >= 2) { // this is to be edited when the parser becomes more complete
				String[] indexToDelete = {args[1]};
				commandObject = new Command(currentCommand, indexToDelete);
			} else {
				throw new Exception(String.format(WARNING_INSUFFICIENT_ARGUMENT, args[0]));
			}
		} else if (args[0].equalsIgnoreCase("exit")) {
			currentCommand = Command.Type.EXIT;
			commandObject = new Command(currentCommand);
		} else if (args[0].equalsIgnoreCase("display")) {
			currentCommand = Command.Type.DISPLAY;
			commandObject = new Command(currentCommand);
		} else {
			commandObject = null;
		}
		return commandObject;
	}
	
}
