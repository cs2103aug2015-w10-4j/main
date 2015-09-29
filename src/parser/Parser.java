package parser;

import global.Command;
import global.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

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
	private static final String COMMAND_UNDO = "undo";
	private static final String COMMAND_EXIT = "exit";
	private static final String COMMAND_DISPLAY = "display";
	private static final String COMMAND_SAVEPATH = "savepath";
	private static final String ARGUMENTS_DATE = "date";
	private static final String SEPARATOR_ARGUMENTS = ";";
	
	private static final String[] months = {"jan", "feb", "mar", "apr", "may", "jun", "jul", "aug",
		"sep", "oct", "nov", "dec"};
	
	/**
	 * Parses the string provided and returns the corresponding object
	 * @param command user input
	 * @return Command object for execution
	 * @throws Exception parsing error message
	 */
	public Command parseCommand(String command) throws Exception {
		String[] args = command.split(" ", 2);
		Command commandObject;
		if (args[0].equalsIgnoreCase(COMMAND_ADD)) {
			try {
				Task taskObj = new Task();
				if (args[1].contains(SEPARATOR_ARGUMENTS)) {
					parseArguments(args[1], taskObj);
				} else {
					taskObj.setName(args[1]);
				}
				commandObject = new Command(Command.Type.ADD, taskObj);
				
			}
			catch (ArrayIndexOutOfBoundsException e) {
				throw new Exception(String.format(WARNING_INSUFFICIENT_ARGUMENT, args[0]));
			}
		} else if (args[0].equalsIgnoreCase(COMMAND_EDIT)) {
			if (args.length >= 3) { // LW, this will not have enough arguments since args is split into maximum of 2 parts
				String[] indexToDelete = {args[1]};
				commandObject = new Command(Command.Type.EDIT, indexToDelete, new Task(args[2]));
			} else {
				throw new Exception(String.format(WARNING_INSUFFICIENT_ARGUMENT, args[0]));
			}
		} else if (args[0].equalsIgnoreCase(COMMAND_DELETE)) {
			if (args.length >= 2) { // this is to be edited when the parser becomes more complete
				String[] indexToDelete = {args[1]};
				commandObject = new Command(Command.Type.DELETE, indexToDelete);
			} else {
				throw new Exception(String.format(WARNING_INSUFFICIENT_ARGUMENT, args[0]));
			}
		} else if (args[0].equalsIgnoreCase(COMMAND_EXIT)) {
			commandObject = new Command(Command.Type.EXIT);
		} else if (args[0].equalsIgnoreCase(COMMAND_DISPLAY)) {
			commandObject = new Command(Command.Type.DISPLAY);
		} else if (args[0].equalsIgnoreCase(COMMAND_UNDO)) {
			commandObject = new Command(Command.Type.UNDO);
		} else if (args[0].equalsIgnoreCase(COMMAND_SAVEPATH)) {
			commandObject = new Command(Command.Type.SAVEPATH);
		} else {
			commandObject = null;
		}
		return commandObject;
	}

	/*
	 * Parses raw data fed from Storage through Logic into Task objects
	 */
	public ArrayList<Task> parseFileData(ArrayList<String> fileData) {
		ArrayList<Task> taskList = new ArrayList<Task>();
		for (int i = 0; i < fileData.size(); i++) {
			Task taskObj = new Task();
			if (fileData.get(i).contains(SEPARATOR_ARGUMENTS)) {
				parseArguments(fileData.get(i), taskObj);
			} else {
				taskObj.setName(fileData.get(i));
			}
			taskList.add(taskObj);
		}
		return taskList;
	}
	
	/*
	 * Parses arguments after separator
	 * pre-condition: String must contain ARGUMENTS_SEPARATOR
	 */
	public void parseArguments(String arg, Task taskObj) {
		String[] newArgs = arg.split(";");
		taskObj.setName(newArgs[0]);
		String[] dateArgs = newArgs[1].split(" ");
		if (dateArgs[0].equalsIgnoreCase(ARGUMENTS_DATE)) {
			int day = Integer.parseInt(dateArgs[1]);
			int month = Arrays.binarySearch(months, dateArgs[2]);
			int year = Integer.parseInt(dateArgs[3]);
			
			Calendar date = new GregorianCalendar(year, month, day);
			taskObj.setDate(date);
		}
	}
	
}
