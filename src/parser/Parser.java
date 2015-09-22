package parser;

import global.Command;
import global.Task;

public class Parser {

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
			commandObject = new Command(currentCommand, args, new Task(args[1]));
			return commandObject;
		} else if (args[0].equalsIgnoreCase("delete")) {
			currentCommand = Command.Type.delete;
			commandObject = new Command(currentCommand, args);
			return commandObject;
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
