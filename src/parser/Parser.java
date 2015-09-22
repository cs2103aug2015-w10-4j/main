package parser;

import java.io.IOException;

public class Parser {

	public Command parseCommand (String command) throws IOException {
		String[] args = command.split(" ");
		Command.Type currentCommand;
		if (args[0].equalsIgnoreCase("add")) {
			currentCommand = Command.Type.add;
		} else if (args[0].equalsIgnoreCase("edit")) {
			currentCommand = Command.Type.edit;
		} else if (args[0].equalsIgnoreCase("delete")) {
			currentCommand = Command.Type.delete;
		} else if (args[0].equalsIgnoreCase("exit")) {
			currentCommand = Command.Type.exit;
		} else {
			throw new IOException("Invalid command given");
		}
		return new Command(currentCommand, args[1]);		
			
	}
}
