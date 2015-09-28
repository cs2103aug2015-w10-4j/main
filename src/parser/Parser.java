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
	
	private static final String COMMAND_ADD = "add";
	private static final String COMMAND_EDIT = "edit";
	private static final String COMMAND_DELETE = "delete";
	private static final String COMMAND_EXIT = "exit";
	private static final String COMMAND_DISPLAY = "display";
	private static final String COMMAND_SAVEPATH = "savepath";
			
	public Command parseCommand(String command) throws Exception {
		String[] args = command.split(" ");
		Command.Type currentCommand;
		Command commandObject;
		if (args[0].equalsIgnoreCase(COMMAND_ADD)) {
			currentCommand = Command.Type.add;
			try {
				commandObject = new Command(currentCommand, new Task(args[1]));
			}
			catch (ArrayIndexOutOfBoundsException e) {
				throw new Exception(String.format(WARNING_INSUFFICIENT_ARGUMENT, args[0]));
			}
		} else if (args[0].equalsIgnoreCase(COMMAND_EDIT)) {
			currentCommand = Command.Type.edit;
			if (args.length >= 3) {
				String[] indexToDelete = {args[1]};
				commandObject = new Command(currentCommand, indexToDelete, new Task(args[2]));
			} else {
				throw new Exception(String.format(WARNING_INSUFFICIENT_ARGUMENT, args[0]));
			}
		} else if (args[0].equalsIgnoreCase(COMMAND_DELETE)) {
			currentCommand = Command.Type.delete;
			if (args.length >= 2) { // this is to be edited when the parser becomes more complete
				String[] indexToDelete = {args[1]};
				commandObject = new Command(currentCommand, indexToDelete);
			} else {
				throw new Exception(String.format(WARNING_INSUFFICIENT_ARGUMENT, args[0]));
			}
		} else if (args[0].equalsIgnoreCase(COMMAND_EXIT)) {
			currentCommand = Command.Type.exit;
			commandObject = new Command(currentCommand);
		} else if (args[0].equalsIgnoreCase(COMMAND_DISPLAY)) {
			currentCommand = Command.Type.display;
			commandObject = new Command(currentCommand);
		} else if (args[0].equalsIgnoreCase(COMMAND_SAVEPATH)) {
			currentCommand = Command.Type.savepath;
			commandObject = new Command(currentCommand)
		} else {
			commandObject = null;
		}
		return commandObject;
	}
}
