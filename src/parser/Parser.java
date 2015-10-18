package parser;

import global.Command;
import global.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.logging.Logger;

/**
 * To allow parser to parse a new field for task,
 * 1. Define a list of keywords to identify field
 * 2. Define a new get<newfield>Field which adds a KeywordMarker to the list
 * and execute it in getArrayOfKeywordIndexes
 * 3. Define a new extract<newfield> method to handle parsing of arguments
 * and execute it in extractTaskInformation
 * Use getArgumentForField to retrieve the array of argument words
 *
 */
public class Parser {
	/**
	 * Parses the command string based on keyword
	 * 
	 * @param command
	 * @return commandObject to be executed, or null if invalid
	 */
	private static Parser parserInstance = null;
	
	Logger logger = Logger.getGlobal();

	// warning messages
	private static final String WARNING_INSUFFICIENT_ARGUMENT = "Warning: '%s': insufficient command arguments";
	private static final String WARNING_INVALID_DATE = "Invalid date specified!";
	private static final String WARNING_INVALID_DAY = "Invalid day specified!";
	private static final String WARNING_INVALID_MONTH = "Invalid month specified!";

	private static final String[] COMMAND_ADD = { "add" };
	private static final String[] COMMAND_EDIT = { "edit", "change" };
	private static final String[] COMMAND_DELETE = { "delete", "del" };
	private static final String[] COMMAND_UNDO = { "undo" };
	private static final String[] COMMAND_REDO = { "redo" };
	private static final String[] COMMAND_EXIT = { "exit" };
	private static final String[] COMMAND_DISPLAY = { "display" };
	private static final String[] COMMAND_SAVETO = { "saveto" };

	private static final String[] DATE_SPECIAL = { "next", "tomorrow", "today",
			"this" };
	private static final String[] MONTHS = { "jan", "feb", "mar", "apr", "may",
			"jun", "jul", "aug", "sep", "oct", "nov", "dec" };

	private enum FieldType {
		START_EVENT, END_EVENT, DEADLINE, LOCATION, THREECHARMONTH, FULLDAY, PERIODIC
	}// SPECIAL,

	private static final String[] LOCATION = { "loc" };
	private static final String[] DEADLINE = { "by" };
	private static final String[] START_EVENT = { "start" };
	private static final String[] END_EVENT = { "end" };

	private class KeywordMarker implements Comparable<KeywordMarker> {
		private int index;
		private FieldType typeOfField;

		private int getIndex() {
			return index;
		}

		private void setIndex(int i) {
			index = i;
		}

		private FieldType getFieldType() {
			return typeOfField;
		}

		private void setFieldType(FieldType fieldType) {
			typeOfField = fieldType;
		}

		@Override
		public int compareTo(KeywordMarker o) {
			if (this.index < o.getIndex()) {
				return -1;
			} else if (this.index > o.getIndex()) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	public Command parseCommand(String commandString) throws Exception {
		Command.Type commandType = identifyType(commandString);
		commandString = clearFirstWord(commandString);
		Command commandObject = new Command(commandType);
		Task taskObject = new Task();
		String[] argumentArray;
		
		switch (commandType) {
		case ADD:
			extractTaskInformation(commandString, taskObject);
			commandObject.addTask(taskObject);
			break;
		case EDIT:
			argumentArray = getEditIndex(commandString);
			commandObject.setArguments(argumentArray);
			commandString = clearFirstWord(commandString);
			
			extractTaskInformation(commandString, taskObject);
			commandObject.addTask(taskObject);
			break;
		case DELETE:
			argumentArray = getDeleteIndexes(commandString);
			commandObject.setArguments(argumentArray);
			break;
		default:
			throw new Exception("parseCommand: no command word identified.");
		}
		return commandObject;
	}
	
	private String[] getEditIndex(String commandString){
		String indexString = commandString.split(" ", 2)[0];
		return new String[]{ indexString };
	}
	
	private String[] getDeleteIndexes(String commandString){
		String[] indexArray = commandString.split(" ");
		return indexArray;
	}

	private String clearFirstWord(String commandString) {
		String[] splitCommand = commandString.split(" ", 2);
		assert (splitCommand.length >= 1);
		if (splitCommand.length == 1) {
			return "";
		} else {
			return splitCommand[1];
		}
	}

	private Command.Type identifyType(String commandString) throws Exception {
		if (commandString.length() == 0) {
			throw new Exception("identifyType: Command string is empty!");
		} else {
			String firstWord = commandString.split(" ", 2)[0];
			if (isCommandKeyword(firstWord, COMMAND_ADD)) {
				return Command.Type.ADD;
			} else if (isCommandKeyword(firstWord, COMMAND_EDIT)) {
				return Command.Type.EDIT;
			} else if (isCommandKeyword(firstWord, COMMAND_DELETE)) {
				return Command.Type.DELETE;
			} else if (isCommandKeyword(firstWord, COMMAND_UNDO)) {
				return Command.Type.UNDO;
			} else if (isCommandKeyword(firstWord, COMMAND_REDO)) {
				return Command.Type.REDO;
			} else if (isCommandKeyword(firstWord, COMMAND_SAVETO)) {
				return Command.Type.SAVETO;
			} else if (isCommandKeyword(firstWord, COMMAND_DISPLAY)) {
				return Command.Type.DISPLAY;
			} else if (isCommandKeyword(firstWord, COMMAND_EXIT)) {
				return Command.Type.EXIT;
			} else {
				return null;// default to add
			}
		}
	}

	private boolean isCommandKeyword(String firstWordInCommandString,
			String[] keywords) {
		for (int i = 0; i < keywords.length; i++) {
			if (firstWordInCommandString.equalsIgnoreCase(keywords[i])) {
				return true;
			}
		}
		return false;
	}

	private boolean extractTaskInformation(String commandString, Task taskObject)
			throws Exception {
		logger.fine("extractTaskInformation: getting keyword markers");
		ArrayList<KeywordMarker> keywordMarkers = getArrayOfKeywordIndexes(commandString);
		Collections.sort(keywordMarkers);
		
		logger.fine("extractedTaskInformation: extracting data from string");
		extractName(commandString, keywordMarkers, taskObject);
		extractDate(commandString, keywordMarkers, taskObject);
		return true;
	}

	private boolean extractDate(String commandString,
			ArrayList<KeywordMarker> keywordMarkers, Task taskObject) {
		// check deadline/start_event & end_event
		logger.fine("extractDate: getting date arguments");
		String[] deadlineArguments = getArgumentsForField(commandString,
				keywordMarkers, FieldType.DEADLINE);
		if(deadlineArguments!=null){
			for(int i = 0; i < deadlineArguments.length; i++){
				logger.finer("extractDate: deadlineArguments[" + i + "] contains " +  deadlineArguments[i]);
			}
		}
		String[] startEventArguments = getArgumentsForField(commandString,
				keywordMarkers, FieldType.START_EVENT);
		String[] endEventArguments = getArgumentsForField(commandString,
				keywordMarkers, FieldType.END_EVENT);
		
		if(startEventArguments!=null){
			for(int i = 0; i < startEventArguments.length; i++){
				logger.finer("extractDate: startEventArguments[" + i + "] contains " +  startEventArguments[i]);
			}
		}
		logger.fine("extractDate: got date arguments. attempting to parse dates");
		if (deadlineArguments != null) {
			Calendar argumentDate = parseDate(deadlineArguments);
			taskObject.setEndingTime(argumentDate);
			return true;
		} else if (startEventArguments != null && startEventArguments != null) {
			Calendar argumentStartDate = parseDate(startEventArguments);
			Calendar argumentEndDate = parseDate(endEventArguments);

			if (argumentStartDate.before(argumentEndDate)) {
				taskObject.setStartingTime(argumentStartDate);
				taskObject.setEndingTime(argumentEndDate);
			} else {
				taskObject.setStartingTime(argumentEndDate);
				taskObject.setEndingTime(argumentStartDate);
			}
			return true;
		} else {
			return false;
		}
	}

	private Calendar parseDate(String[] dateArguments) {
		logger.fine("parseDate: parsing date");
		int date, month, year;
		date = Integer.parseInt(dateArguments[0]);
		month = Arrays.asList(MONTHS).indexOf(dateArguments[1]);
		year = Integer.parseInt(dateArguments[2]);

		Calendar helperDate = new GregorianCalendar();
		helperDate.set(year, month, date);

		return helperDate;
	}

	private String[] getArgumentsForField(String commandString,
			ArrayList<KeywordMarker> keywordMarkers, FieldType typeOfField) {
		for (int i = 0; i < keywordMarkers.size(); i++) {
			KeywordMarker curKeywordMarker = keywordMarkers.get(i);
			if (curKeywordMarker.getFieldType() == typeOfField) {
				int indexSearch;
				if (i < keywordMarkers.size() - 1) {
					// get index of the next field argument
					indexSearch = keywordMarkers.get(i + 1).getIndex() - 1;
					logger.finer("getArgumentsForField: search starting from " + indexSearch);
					while (commandString.charAt(indexSearch) == ' ') {
						indexSearch--;
					}
					while (commandString.charAt(indexSearch) != ' ') {
						indexSearch--;
					}
					while (commandString.charAt(indexSearch) == ' ') {
						indexSearch--;
					}
					indexSearch++;
				} else {
					// until the end of the string
					indexSearch = commandString.length();
				}
				
				int curIndex = curKeywordMarker.getIndex();
				logger.finer("getArgumentsForField: curIndex is " + curIndex);
				logger.finer("getArgumentsForField: indexSearch is " + indexSearch);
				String argumentString = commandString.substring(curIndex,
						indexSearch);
				String[] argumentWords = argumentString.split("[ ]+");
				return argumentWords;
			}
		}
		return null;
	}

	private boolean extractName(String commandString,
			ArrayList<KeywordMarker> keywordMarkers, Task taskObject)
			throws Exception {
		logger.fine("extractName: extracting name");
		String taskName;
		if (commandString.length() == 0) {
			throw new Exception("extractName: Command string is empty!");
		} else if (keywordMarkers.size() > 0) {
			logger.finer("extractName: markersize > 0");
			int searchIndex = keywordMarkers.get(0).getIndex() - 1;
			
			logger.finer("extractName: searchIndex starts from " + searchIndex);
			while (searchIndex >= 0 && commandString.charAt(searchIndex) == ' ') {
				searchIndex--;
			}
			
			logger.finer("extractName: reached next command word at " + searchIndex);
			while (searchIndex >= 0 && commandString.charAt(searchIndex) != ' ') {
				searchIndex--;
			}
			
			logger.finer("extractName: past next command word at " + searchIndex);
			if (searchIndex < 0) {
				throw new Exception("extractName: No task name found.");
			} else {
				taskName = commandString.substring(0, searchIndex);
			}
		} else {
			taskName = commandString;
		}
		taskObject.setName(taskName);
		return true;
	}

	private ArrayList<KeywordMarker> getArrayOfKeywordIndexes(
			String commandString) throws Exception {
		ArrayList<KeywordMarker> keywordMarkerList = new ArrayList<KeywordMarker>();
		getLocationField(keywordMarkerList, commandString);
		getDateField(keywordMarkerList, commandString);

		return keywordMarkerList;
	}

	private boolean getLocationField(ArrayList<KeywordMarker> curMarkerList,
			String commandString) {
		KeywordMarker markerForLocation = getKeywordMarker(commandString,
				LOCATION);
		if (markerForLocation != null) {
			curMarkerList.add(markerForLocation);
			return true;
		}
		return false;
	}

	private boolean getDateField(ArrayList<KeywordMarker> curMarkerList,
			String commandString) throws Exception {
		KeywordMarker markerForDeadline = getKeywordMarker(commandString,
				DEADLINE);
		if (markerForDeadline != null) {
			markerForDeadline.setFieldType(FieldType.DEADLINE);
			curMarkerList.add(markerForDeadline);
			return true;
		}

		KeywordMarker markerForStartEvent = getKeywordMarker(commandString,
				START_EVENT);
		KeywordMarker markerForEndEvent = getKeywordMarker(commandString,
				END_EVENT);
		if (markerForStartEvent != null && markerForEndEvent != null) {
			markerForStartEvent.setFieldType(FieldType.START_EVENT);
			markerForEndEvent.setFieldType(FieldType.END_EVENT);
			curMarkerList.add(markerForStartEvent);
			curMarkerList.add(markerForEndEvent);
			return true;
		} else if (markerForStartEvent != null) {
			throw new Exception(
					"getDateField: A start time has been entered without end time!");
		} else if (markerForEndEvent != null) {
			throw new Exception(
					"getDateField: An end time has been entered without start time!");
		} else {
			return false;
		}
	}

	private KeywordMarker getKeywordMarker(String commandString,
			String[] listOfKeywords) {
		for (int i = 0; i < listOfKeywords.length; i++) {
			String curKeyword = " " + listOfKeywords[i] + " "; // maybe use
																// string format
			int keywordIndex = commandString.indexOf(curKeyword);
			if (commandString.indexOf(curKeyword) != -1) {
				int indexOfArgument = keywordIndex + curKeyword.length();
				int lengthOfCommandString = commandString.length();
				logger.finer("getKeywordMarker: Attempting to check " + curKeyword);
				logger.finer("getKeywordMarker: found at " + keywordIndex + " and argument is at " + indexOfArgument);
				while (lengthOfCommandString > indexOfArgument
						&& commandString.charAt(indexOfArgument) == ' ') {
					indexOfArgument++;
				}
				KeywordMarker newMarker = new KeywordMarker();
				newMarker.setIndex(indexOfArgument);
				return newMarker;
			}
		}
		return null;
	}

	public static Parser getInstance() {
		if (parserInstance == null) {
			parserInstance = new Parser();
		}
		return parserInstance;
	}
}