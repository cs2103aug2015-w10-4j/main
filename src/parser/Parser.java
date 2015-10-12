package parser;

import global.Command;
import global.Task;

import java.text.SimpleDateFormat;
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
	private static final String WARNING_INVALID_DAY = "Invalid day supplied!";
	
	private static final String COMMAND_ADD = "add";
	private static final String COMMAND_EDIT = "edit";
	private static final String COMMAND_DELETE = "delete";
	private static final String COMMAND_UNDO = "undo";
	private static final String COMMAND_REDO = "redo";
	private static final String COMMAND_EXIT = "exit";
	private static final String COMMAND_DISPLAY = "display";
	private static final String COMMAND_SAVEPATH = "savepath";
	private static final String[] ARGUMENTS_DATE = {" date ",  " by ", " this ", " next ", "tomorrow"};
	
	private static final String[] MONTHS = {"jan", "feb", "mar", "apr", "may", "jun", "jul", "aug",
		"sep", "oct", "nov", "dec"};
	private static final String[] DAYS = {"sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"};
	
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
				// extracts 'date' segment of the command if present, and returns the remaining 
				// string back to arg
				args[1] = extractDate(args[1], taskObj);
				taskObj.setName(args[1]);
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
				//try to parse the date
				taskObj.setName(extractDate(newArgs[1], taskObj));
				commandObject = new Command(Command.Type.EDIT, indexToDelete, taskObj);
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
		} else if (args[0].equalsIgnoreCase(COMMAND_REDO)) {
			commandObject = new Command(Command.Type.REDO);
		} else if (args[0].equalsIgnoreCase(COMMAND_SAVEPATH)) {
			String[] newArgs = {args[1]};
			commandObject = new Command(Command.Type.SAVEPATH, newArgs);
		} else {
			commandObject = null;
		}
		return commandObject;
	}
	
	/*
	 * Extracts 'date' segment of the command if present and updates date field of taskObj. Extracts 'day'
	 * segment of the command if present and updates date field of taskObj - current supported parameters before
	 * day string are 'this' and 'next' 
	 * Special argument: 'tomorrow' will set date to the next day from current date
	 * pre-condition: String must contain DATE_ARGUMENTS, date parameters are valid dates in format dd MMM yyyy
	 * 					OR
	 * 				  String must contain day arg in lowercase only
	 * post-condition: returns extracted string if date is present, else return original string if date
	 * 				   is not present
	 */
	private String extractDate(String arg, Task taskObj) throws Exception{
		String[] newArgs = {};
		Calendar date = new GregorianCalendar();
		int argument = -1; // 0 = date, 1 = by, 2 = this, 3 = next, 4 = tomorrow
		for (int i = 0; i < ARGUMENTS_DATE.length; i++) {
			if (arg.contains(ARGUMENTS_DATE[i])) {
				newArgs = arg.split(ARGUMENTS_DATE[i]);
				argument = i;
			}
		}
		if (argument == -1) {
			// no date parameters found; return original string
			return arg;
		} else if (argument < 2) {
			// command contains ARGUMENTS_DATE[0||1]
			String[] dateArgs = newArgs[1].split(" ");
			int day = Integer.parseInt(dateArgs[0]);
			int month = Arrays.asList(MONTHS).indexOf(dateArgs[1]);
			// year will be set to current year if not specified by user
			int year;
			try {
				year = Integer.parseInt(dateArgs[2]);
			} catch (ArrayIndexOutOfBoundsException e) {
				year = Calendar.getInstance().get(Calendar.YEAR);
			}
			date.set(year, month, day);
		} else if (argument < 4) {
			// command contains ARGUMENTS_DATE[2||3]
			date = new GregorianCalendar();			
			int setDay = -1, today = 0, offset;
			SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE");
			for (int i = 0; i < DAYS.length; i++) {
				if (newArgs[1].indexOf(DAYS[i]) == 0) {
					setDay = i;
				}
				if (dateFormat.format(date.getTime()).equalsIgnoreCase(DAYS[i])) {
					today = i;
				}
			}
			if (setDay == -1) {
				throw new Exception(WARNING_INVALID_DAY);
			}
			offset = setDay - today;
			if (offset <= 0) {
				offset += 7;
			}
			if (argument == 3) {
				offset += 7;
			}
			date.set(Calendar.DATE, date.get(Calendar.DATE) + offset);
		} else {
			// command contains ARGUMENTS_DATE[4]
			date.set(Calendar.DATE, date.get(Calendar.DATE) + 1);
		}
		taskObj.setEndingTime(date);
		return newArgs[0];
	}
	
	private String extractPeriodic(String arg, Task taskObj){
		return null;
	}
}