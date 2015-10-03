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
	private static final String ARGUMENTS_DATE = " date ";
	
	private static final String[] months = {"jan", "feb", "mar", "apr", "may", "jun", "jul", "aug",
		"sep", "oct", "nov", "dec"};
	
	/**
	 * Parses the string provided and returns the corresponding object
	 * @param command user input
	 * @return Command object for execution
	 * @throws Exception parsing error message
	 */
	public Command parseCommand(String command) throws Exception {
		String[] args = command.split(" ", 2); // extract CommandType from command
		Command commandObject;
		if (args[0].equalsIgnoreCase(COMMAND_ADD)) {
			try {
				Task taskObj = new Task();
				if (args[1].indexOf(ARGUMENTS_DATE) != -1) {
					extractDate(args[1], taskObj);
				} else {
					taskObj.setName(args[1]);
				}
				commandObject = new Command(Command.Type.ADD, taskObj);
				
			}
			catch (ArrayIndexOutOfBoundsException e) {
				throw new Exception(String.format(WARNING_INSUFFICIENT_ARGUMENT, args[0]));
			}
		} else if (args[0].equalsIgnoreCase(COMMAND_EDIT)) {
			try {
				String[] newArgs = args[1].split(" ", 2);
				String[] indexToDelete = {newArgs[0]};
				Task taskObj = new Task();
				if (newArgs[1].indexOf(ARGUMENTS_DATE) != -1) {
					extractDate(newArgs[1], taskObj);
				} else {
					taskObj.setName(newArgs[1]);
				}
				commandObject = new Command(Command.Type.EDIT, indexToDelete, new Task(newArgs[1]));
			} catch (ArrayIndexOutOfBoundsException e) {
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
			String[] newArgs = {args[1]};
			commandObject = new Command(Command.Type.SAVEPATH, newArgs);
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
			if (fileData.get(i).contains(ARGUMENTS_DATE)) {
				extractDate(fileData.get(i), taskObj);
			} else {
				taskObj.setName(fileData.get(i));
			}
			taskList.add(taskObj);
		}
		return taskList;
	}
	
	/*
	 * Parses arguments after separator
	 * pre-condition: String must contain DATE_ARGUMENTS, all inputs are valid dates in format dd MMM yyyy
	 */
	public void extractDate(String arg, Task taskObj) {
		String[] newArgs = arg.split(ARGUMENTS_DATE);
		taskObj.setName(newArgs[0]);
		String[] dateArgs = newArgs[1].split(" ");
		int day = Integer.parseInt(dateArgs[0]);
		int month = Arrays.asList(months).indexOf(dateArgs[1]);
		// year will be set to current year if not specified by user
		int year;
		try {
			year = Integer.parseInt(dateArgs[2]);
		} catch (ArrayIndexOutOfBoundsException e) {
			year = Calendar.getInstance().get(Calendar.YEAR);
		}
		Calendar date = new GregorianCalendar(year, month, day);
		taskObj.setEndingTime(date);
	}

}
