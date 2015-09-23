package parser;

import global.Command;
import global.Task;

public class Parser {

	/**
	 * Parses the command string based on keyword
	 * @param command
	 * @return commandObject to be executed, or null if invalid
	 */
	public Command parseCommand(String command) {
		String[] args = command.split(" ");
		Command.Type currentCommand;
		Command commandObject;
		if (args[0].equalsIgnoreCase("add")) {
			currentCommand = Command.Type.add;
			commandObject = new Command(currentCommand, new Task(args[1]));
			return commandObject;
		} else if (args[0].equalsIgnoreCase("edit")) {
			currentCommand = Command.Type.edit;
			if (args.length >= 3) {
				String[] indexToDelete = {args[1]};
				commandObject = new Command(currentCommand, indexToDelete, new Task(args[2]));
				return commandObject;
			} else {
				return null;
			}
		} else if (args[0].equalsIgnoreCase("delete")) {
			currentCommand = Command.Type.delete;
			if (args.length >= 2) { // this is to be edited when the parser becomes more complete
				String[] indexToDelete = {args[1]};
				commandObject = new Command(currentCommand, indexToDelete);
				return commandObject;
			} else {
				return null;
			}
		} else if (args[0].equalsIgnoreCase("exit")) {
			currentCommand = Command.Type.exit;
			commandObject = new Command(currentCommand);
			return commandObject;
		} else if (args[0].equalsIgnoreCase("display")) {
			currentCommand = Command.Type.display;
			commandObject = new Command(currentCommand);
			return commandObject;
		} else {
			return null;
		}
	}
}
