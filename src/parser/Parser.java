package parser;

import global.Command;
import global.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.logging.Logger;

/**
 * To allow parser to parse a new field for task,
 * 1. Add to enum FieldType
 * 2. Define an array of keywords to identify field
 * 3. Define a new get<newfield>Field which adds a KeywordMarker to the list
 * and execute it in getArrayOfKeywordIndexes
 * 4. Define a new extract<newfield> method to handle parsing of arguments
 * and execute it in extractTaskInformation
 * Use getArgumentForField to retrieve the array of argument words
 *
 */
public class Parser {
	
	//status messages
	
	private static final String ERROR_MISSING_START_TIME = "Error: An end time has been entered without start time!";
	private static final String ERROR_MISSING_END_TIME = "Error: A start time has been entered without end time!";
	private static final String ERROR_INVALID_DAY_SPECIFIED = "Error: Invalid day specified!";
	private static final String ERROR_INVALID_NUMBER_OF_ARGUMENTS = "Error: Invalid number of arguments";
	private static final String ERROR_INVALID_COMMAND_SPECIFIED = "Error: Invalid command specified!";
	private static final String ERROR_EMPTY_COMMAND_STRING = "Error: Command string is empty";
	private static final String ERROR_EMPTY_TASK_NAME = "Error: Task name is empty";

	private static Parser parserInstance = null;
	
	Logger logger = Logger.getGlobal();

	private static final String[] COMMAND_ADD = { "add" };
	private static final String[] COMMAND_EDIT = { "edit", "change" };
	private static final String[] COMMAND_DELETE = { "delete", "del" };
	private static final String[] COMMAND_UNDO = { "undo" };
	private static final String[] COMMAND_REDO = { "redo" };
	private static final String[] COMMAND_EXIT = { "exit" };
	private static final String[] COMMAND_DISPLAY = { "display" };
	private static final String[] COMMAND_SAVETO = { "saveto" };

	private static final String[] DATE_SPECIAL = { "this", "next", "today", "tomorrow"
			 };
	private static final String[] MONTHS = { "jan", "feb", "mar", "apr", "may",
			"jun", "jul", "aug", "sep", "oct", "nov", "dec" };
	private static final String[] DAYS = { "sunday" , "monday" , "tuesday" , "wednesday" , "thursday" ,"friday", "saturday" };

	private enum FieldType {
		START_EVENT, END_EVENT, DEADLINE, LOCATION, PERIODIC
	}

	private static final String[] LOCATION = { "loc" , "at" };
	private static final String[] DEADLINE = { "by" };
	private static final String[] START_EVENT = { "start", "from" };
	private static final String[] END_EVENT = { "end" , "to" };
	private static final String[] PERIODIC = { "every" , "repeats" };

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

	
	/**
	 * Parses the command string based on keyword
	 * 
	 * @param command
	 * @return commandObject to be executed, or null if invalid
	 */
	public Command parseCommand(String commandString) throws Exception {
		commandString = commandString.trim();
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
		case SAVETO:
			argumentArray = getSaveToArgument(commandString);
			commandObject.setArguments(argumentArray);
			break;
		default:
			
		}
		return commandObject;
	}
	private String[] getSaveToArgument(String commandString){
		String pathString = commandString.split(" ", 2)[0];
		return new String[]{ pathString };
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
			logger.info("identifyType: Command string is empty!");
			throw new Exception(ERROR_EMPTY_COMMAND_STRING);
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
				logger.info("identifyType: invalid command");
				throw new Exception(ERROR_INVALID_COMMAND_SPECIFIED);
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
		extractLocation(commandString, keywordMarkers, taskObject);
		return true;
	}
	
	private boolean extractLocation(String commandString, ArrayList<KeywordMarker> keywordMarkers, Task taskObject) throws Exception{
		String[] locationArguments = getArgumentsForField(commandString, keywordMarkers, FieldType.LOCATION);
		if(locationArguments!= null){
			if(locationArguments.length == 1){
				logger.finer("extractLocation: argument length is 1.");
				taskObject.setLocation(locationArguments[0]);
				return true;
			} else {
				logger.info("extractLocation: invalid number of arguments");
				throw new Exception(ERROR_INVALID_NUMBER_OF_ARGUMENTS);
			}
		}
		return false;
	}

	private boolean extractDate(String commandString,
			ArrayList<KeywordMarker> keywordMarkers, Task taskObject) throws Exception {
		// check deadline/start_event & end_event
		logger.fine("extractDate: getting date arguments");
		String[] deadlineArguments = getArgumentsForField(commandString,
				keywordMarkers, FieldType.DEADLINE);
		if (deadlineArguments != null) {
			for(int i = 0; i < deadlineArguments.length; i++){
				logger.finer("extractDate: deadlineArguments[" + i + "] contains " +  deadlineArguments[i]);
			}
		}
		String[] startEventArguments = getArgumentsForField(commandString,
				keywordMarkers, FieldType.START_EVENT);
		String[] endEventArguments = getArgumentsForField(commandString,
				keywordMarkers, FieldType.END_EVENT);
		
		if(startEventArguments != null){
			for(int i = 0; i < startEventArguments.length; i++){
				logger.finer("extractDate: startEventArguments[" + i + "] contains " +  startEventArguments[i]);
			}
		}
		logger.fine("extractDate: got date arguments. attempting to parse dates");
		if (deadlineArguments != null) {
			Calendar argumentDate = parseDate(deadlineArguments);
			taskObject.setEndingTime(argumentDate);
			logger.fine("extractDate: deadline set");
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

	private Calendar parseDate(String[] dateArguments) throws Exception {
		logger.fine("parseDate: parsing date");
		int date, month, year;
		if (!hasKeyword(dateArguments, DATE_SPECIAL)) {
			date = Integer.parseInt(dateArguments[0]);
			month = Arrays.asList(MONTHS).indexOf(dateArguments[1]);
			if(dateArguments.length == 3){
				year = Integer.parseInt(dateArguments[2]);
			}else{
				year = Calendar.getInstance().get(Calendar.YEAR);
			}

			Calendar helperDate = new GregorianCalendar();
			helperDate.set(year, month, date);

			return helperDate;
		} else if(dateArguments.length == 2){ // this/next <day>
			logger.finer("parseDate: dateArguments[0] contains " + dateArguments[0]);
			logger.finer("parseDate: dateArguments[1] contains " + dateArguments[1]);
			String firstWord = dateArguments[0];
			String secondWord = dateArguments[1];
			
			if(hasKeyword(secondWord, DAYS)){
				int dayIndex = Arrays.asList(DAYS).indexOf(secondWord) + 1;
				assert(firstWord.equalsIgnoreCase(DATE_SPECIAL[0])
						|| firstWord.equalsIgnoreCase(DATE_SPECIAL[1]));
				if(firstWord.equalsIgnoreCase(DATE_SPECIAL[0])){//this
					date = getNearestDate(dayIndex);
				} else {//next
					date = getNearestDate(dayIndex) + DAYS.length;
				} 
				logger.finer("parseDate: this/next day determined to be " + date);
			} else {
				logger.info("parseDate: invaid day");
				throw new Exception(ERROR_INVALID_DAY_SPECIFIED);
			}
			
			month = Calendar.getInstance().get(Calendar.MONTH);
			year = Calendar.getInstance().get(Calendar.YEAR);
			
			Calendar helperDate = new GregorianCalendar();
			helperDate.set(year, month, date);
			return helperDate;
		} else if (dateArguments.length == 1){ // today/tomorrow
			if(dateArguments[0].equalsIgnoreCase(DATE_SPECIAL[2])){
				return new GregorianCalendar();
			} else {
				Calendar helperDate = new GregorianCalendar();
				helperDate.add(Calendar.DATE, 1);
				return helperDate;
			}
		} else {
			logger.info("parseDate: unknown date arguments");
			throw new Exception("Error: Invalid arguments for date");
		}
	}
	
	private int getNearestDate(int givenDayIndex){
		Calendar dateHelper = Calendar.getInstance();
		int curDayIndex = dateHelper.get(Calendar.DAY_OF_WEEK);
		logger.fine("getNearestDate: given day is " + givenDayIndex);
		logger.fine("getNearestDate: today is " + curDayIndex);
		int todayDate = dateHelper.get(Calendar.DATE);
		
		int difference = (givenDayIndex - curDayIndex) % DAYS.length;
		logger.fine("getNearestDate: difference is " + difference);
		int newDate = todayDate + difference;
		return newDate;
	}
	
	private boolean hasKeyword(String word, String[] keywords){
		for(int i = 0; i < keywords.length; i++){
			if(word.equalsIgnoreCase(keywords[i])){
				return true;
			}
		}
		return false;
	}
	private boolean hasKeyword(String[] words, String[] keywords){
		for(int i = 0; i < words.length; i++){
			if(hasKeyword(words[i], keywords)){
				return true;
			}
		}
		return false;
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
			logger.info("extractName: no task information");
			throw new Exception(ERROR_EMPTY_TASK_NAME);
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
			assert(searchIndex >= 0);
			taskName = commandString.substring(0, searchIndex);
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
			markerForLocation.setFieldType(FieldType.LOCATION);
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
			logger.info("getDateField: start time without end time detected");
			throw new Exception(
					ERROR_MISSING_END_TIME);
		} else if (markerForEndEvent != null) {
			logger.info("getDateField: end time without start time detected");
			throw new Exception(
					ERROR_MISSING_START_TIME);
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