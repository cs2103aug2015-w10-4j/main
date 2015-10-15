package parser;

import global.Command;
import global.Task;

import java.text.SimpleDateFormat;
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
	private static final String WARNING_INVALID_DAY = "Invalid day specified!";
	private static final String WARNING_INVALID_MONTH = "Invalid month specified!";
	
	private static final String COMMAND_ADD = "add";
	private static final String COMMAND_EDIT = "edit";
	private static final String COMMAND_DELETE = "delete";
	private static final String COMMAND_UNDO = "undo";
	private static final String COMMAND_REDO = "redo";
	private static final String COMMAND_EXIT = "exit";
	private static final String COMMAND_DISPLAY = "display";
	private static final String COMMAND_SAVEPATH = "savepath";
	private static final String ARGUMENT_FROM = "start";
	private static final String ARGUMENT_TO = "end";
	
	private static final String[] ARGUMENT_EVENT = {"start", "end"};
	private static final String[] ARGUMENTS_END_DATE = {" date ", " by "};
	private static final String[] ARGUMENTS_SPECIAL_END_DATE = {" this ", " next ", " tomorrow", " today"};
	private static final String ARGUMENTS_PERIODIC = " every ";
	private static final String ARGUMENT_LOC = "loc";
	private static final String DEFAULT_DAY = "friday";
	
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
		String[] args = command.split(" ",2); // extract CommandType from command
		Command commandObject;
		if (args[0].equalsIgnoreCase(COMMAND_ADD)) {
			try {
				Task taskObj = new Task();
				// Using old method of extracting task name temporarily for v0.1
//				taskObj.setName(extractTaskName(args[1]));
//				taskObj.setEndingTime(extractDate(args[1]));
				taskObj.setName(extractDate(args[1], taskObj));
			//	taskObj.setPeriodic(extractPeriodic(args[1], taskObj));
				args[1] = extractLocation(args[1], taskObj);
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
				// Using old method of extracting task name temporarily for v0.1
//				taskObj.setName(extractTaskName(newArgs[1]));
//				taskObj.setEndingTime(extractDate(args[1]));
				taskObj.setName(extractDate(newArgs[1], taskObj));
				commandObject = new Command(Command.Type.EDIT, indexToDelete, taskObj);
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new Exception(String.format(WARNING_INSUFFICIENT_ARGUMENT, args[0]));
			}
		} else if (args[0].equalsIgnoreCase(COMMAND_DELETE)) {
			if (args.length >= 2) { // this is to be edited when the parser becomes more complete
				String[] indexToDelete = args[1].split(" ");
				
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
			commandObject = new Command(Command.Type.SAVETO, newArgs);
		} else {
			commandObject = null;
		}
		return commandObject;
	}
	
	/* 
	 * Extracts and returns 'name' segment of the command. 
	 */
	private String extractTaskName(String arg) throws Exception {
		return arg.split("'")[1];
	}
	
	
	/*
	 * Extracts 'date' segment of the command if present returns Calendar object. Extracts 'day'
	 * segment of the command if present and returns Calendar object - current supported parameters before
	 * day string are 'this' and 'next' 
	 * Special argument: 'tomorrow' will set date to the next day from current date
	 * pre-condition: String must contain DATE_ARGUMENTS, date parameters are valid dates in format dd MMM yyyy
	 * 					OR
	 * 				  String must contain day arg in lowercase only
	 * post-condition: returns parsed Calendar object if date is present, else return null. Exception if
	 * 				   day is not spelt in full.
	 * 
	 */
	private String extractDate(String arg, Task taskObj) throws Exception {
		String[] newArgs = {};
		Calendar date = new GregorianCalendar();
		Calendar date1 = new GregorianCalendar();
		if (hasKeyword(arg, ARGUMENTS_END_DATE)) {
			String keywordToSplitAt = getKeyword(arg, ARGUMENTS_END_DATE);
			newArgs = arg.split(keywordToSplitAt);
			
			String[] dateArgs = newArgs[1].split(" ");
			
			int day = Integer.parseInt(dateArgs[0]);
			
			int month = Arrays.asList(MONTHS).indexOf(dateArgs[1]);
			if (month == -1) {
				throw new Exception(WARNING_INVALID_MONTH);
			}
			
			// year will be set to current year if not specified by user
			int year;
			try {
				year = Integer.parseInt(dateArgs[2]);
			} catch (ArrayIndexOutOfBoundsException|NumberFormatException e) {
				year = Calendar.getInstance().get(Calendar.YEAR);
			}
			
			date.set(year, month, day);
			taskObj.setEndingTime(date);
			return newArgs[0];
		} else if (hasKeyword(arg, ARGUMENT_EVENT)) {
			newArgs = arg.split(ARGUMENT_TO);			
			if(newArgs[0].indexOf(ARGUMENT_FROM) != -1) {// if there is a "from" in the input

			String[] tempArgs = newArgs[0].split(ARGUMENT_FROM);
			arg = tempArgs[0];
            String [] fromArgs =tempArgs[1].split(" ");

            int day = Integer.parseInt(fromArgs[1]);		
			int month = Arrays.asList(MONTHS).indexOf(fromArgs[2]);
			if (month == -1) {
				throw new Exception(WARNING_INVALID_MONTH);
			}
			
			// year will be set to current year if not specified by user
			int year;
			try {
				year = Integer.parseInt(fromArgs[3]);
			} catch (ArrayIndexOutOfBoundsException|NumberFormatException e) {
				year = Calendar.getInstance().get(Calendar.YEAR);
			}
			

            date1.set(year, month, day);
            taskObj.setStartingTime(date1);
            

            String[] dateArgs = newArgs[1].split(" ");
			day = Integer.parseInt(dateArgs[1]);
			
			month = Arrays.asList(MONTHS).indexOf(dateArgs[2]);
			if (month == -1) {
				throw new Exception(WARNING_INVALID_MONTH);
			}
			
			// year will be set to current year if not specified by user

			try {
				year = Integer.parseInt(dateArgs[3]);
			} catch (ArrayIndexOutOfBoundsException|NumberFormatException e) {
				year = Calendar.getInstance().get(Calendar.YEAR);
			}
			

            date.set(year, month, day);
            taskObj.setEndingTime(date);
            if(date.before(date1)){
		    	taskObj.setEndingTime(date1);
				taskObj.setStartingTime(date);
		    }else {
		    	taskObj.setEndingTime(date);
				taskObj.setStartingTime(date1);
		    }

		//	SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
		//	System.out.println(dateFormat.format(taskObj.getStartingTime().getTime())+" "+dateFormat.format(taskObj.getEndingTime().getTime()));

            return arg;
			
		} else {
			throw new Exception(WARNING_INVALID_DAY);
		}
			
		} else if (hasKeyword(arg, ARGUMENTS_SPECIAL_END_DATE)) {
			String keywordToSplitAt = getKeyword(arg, ARGUMENTS_SPECIAL_END_DATE);
			newArgs = arg.split(keywordToSplitAt);
			
			date = new GregorianCalendar();			
			int setDate, today, todayDate, offset;
			
			
			today = date.get(Calendar.DAY_OF_WEEK);
			todayDate = date.get(Calendar.DATE);
			
			switch(keywordToSplitAt) {
				case "tomorrow":
					setDate = todayDate + 1;
					break;
				case "today":
					setDate = todayDate;
					break;
				case "this":
					if(!hasKeyword(newArgs[1], DAYS)) {
						throw new Exception(WARNING_INVALID_DAY);
					}
					offset = dayOfTheWeek(getKeyword(newArgs[1], DAYS)) - today;
					if (offset < 0) {
						offset += DAYS.length;
					}
					setDate = todayDate + offset;
					break;
				case "next":
					if(!hasKeyword(newArgs[1], DAYS)) {
						throw new Exception(WARNING_INVALID_DAY);
					}
					offset = dayOfTheWeek(getKeyword(newArgs[1], DAYS)) - today;
					if (offset < 0) {
						offset += DAYS.length;
					}
					setDate = todayDate + offset + DAYS.length;
					break;
				default:
					offset = dayOfTheWeek(DEFAULT_DAY) - today;
					if (offset < 0) {
						offset += DAYS.length;
					}
					setDate = todayDate + offset;
			}
			
			date.set(Calendar.DATE, setDate);
			taskObj.setEndingTime(date);
			return newArgs[0];
		} else {
			return arg;
		}
		
		
	}
	/*
	 * Extracts 'loc' segment
	 * pre-condition: String must contain LOCATION_ARGUMENTS
	 * post-condition: returns extracted string if LOCATION_ARGUMENTS is present, else return original string if date
	 * 				   is not present
	 */
	private String extractLocation(String arg, Task taskObj) throws Exception{
		String[] newArgs = {};
		String returnArg = "";
		boolean hasLoc = false;
			if (arg.contains(ARGUMENT_LOC)) {
				newArgs = arg.split(ARGUMENT_LOC);
				hasLoc = true;
			}
			
			if (hasLoc){
				taskObj.setLocation(newArgs[1]);
				returnArg = newArgs[0];
			} else {
				returnArg = arg;
			}

		return returnArg;
	}
	
	private String extractPeriodic(String arg, Task taskObj){
		if (arg.contains(ARGUMENTS_PERIODIC)){
			String argPeriodic = arg.split(ARGUMENTS_PERIODIC)[1];
			for (int i = 0; i < DAYS.length; i++) {
				if (argPeriodic.indexOf(DAYS[i]) == 0) {
					return DAYS[i];
				}
			}
		}
		return null;
	}
	
	private boolean hasKeyword(String str, String[] keywords){
		for(int i = 0; i < keywords.length; i++){
			if(str.contains(keywords[i])) {
				return true;
			}
		}
		return false;
	}
	
	private String getKeyword(String str, String[] keywords){
		for(int i = 0; i < keywords.length; i++){
			if(str.contains(keywords[i])) {
				return keywords[i];
			}
		}
		return null; // should never happen
	}
	
	private int dayOfTheWeek(String dayString){
		return Arrays.asList(DAYS).indexOf(dayString) + 1;
	}
}