package parser;

import global.Command;
import global.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.logging.Logger;

import logic.Logic;
import storage.Storage;

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
	
	static final String ERROR_INVALID_PERIODIC_INSTANCES = "Error: Invalid periodic instances";
	static final String ERROR_INVALID_PERIODIC_INTERVAL_VALUE = "Error: Invalid periodic interval value";
	static final String ERROR_INVALID_MONTH_SPECIFIED = "Error: Invalid date specified!";
	static final String ERROR_INVALID_DATE_SPECIFIED = "Error: Invalid date specified!";
	static final String ERROR_INVALID_PERIODIC_INTERVAL = "Error: Invalid periodic interval specified";
	static final String ERROR_MISSING_START_TIME = "Error: An end time has been entered without start time!";
	static final String ERROR_MISSING_END_TIME = "Error: A start time has been entered without end time!";
	static final String ERROR_INVALID_DAY_SPECIFIED = "Error: Invalid day specified!";
	static final String ERROR_INVALID_NUMBER_OF_ARGUMENTS = "Error: Invalid number of arguments";
	static final String ERROR_INVALID_COMMAND_SPECIFIED = "Error: Invalid command specified!";
	static final String ERROR_EMPTY_COMMAND_STRING = "Error: Command string is empty";
	static final String ERROR_EMPTY_TASK_NAME = "Error: Task name is empty";
	
	static final String WHITE_SPACE_REGEX = "\\s+";
	
	Logger logger = Logger.getGlobal();

	static final String[] COMMAND_ADD = { "add" };
	static final String[] COMMAND_EDIT = { "edit", "change" };
	static final String[] COMMAND_DELETE = { "delete", "del" };
	static final String[] COMMAND_UNDO = { "undo" };
	static final String[] COMMAND_REDO = { "redo" };
	static final String[] COMMAND_MARK = { "mark" };
	static final String[] COMMAND_UNMARK = { "unmark" };
	static final String[] COMMAND_EXIT = { "exit" };
	static final String[] COMMAND_DISPLAY = { "display" };
	static final String[] COMMAND_SEARCH = { "search" };
	static final String[] COMMAND_SAVETO = { "saveto" };

	static final String[] DATE_SPECIAL = { "this", "next", "today", "tomorrow"
			 };
	static final String[] MONTHS = { "jan", "feb", "mar", "apr", "may",
			"jun", "jul", "aug", "sep", "oct", "nov", "dec" };
	static final String[] DAYS = { "sunday" , "monday" , "tuesday" , "wednesday" , "thursday" ,"friday", "saturday" };
	

	static final String[] PERIODIC = { "days", "weeks" , "months" };

	enum FieldType {
		START_EVENT, END_EVENT, DEADLINE, LOCATION, INTERVAL_PERIODIC, INSTANCES_PERIODIC
	}

	static final String[] LOCATION = { "loc" , "at" };
	static final String[] DEADLINE = { "by" };
	static final String[] START_EVENT = { "start", "from" };
	static final String[] END_EVENT = { "end" , "to" };
	static final String[] INTERVAL_PERIODIC = { "every" , "repeats" };
	static final String[] INSTANCES_PERIODIC = { "for" };
	static final String S_LOCATION = "LOCATION";
	static final String S_DEADLINE = "DEADLINE";
	static final String S_START_EVENT = "START_EVENT";
	static final String S_END_EVENT = "END_EVENT";
	static final String S_INTERVAL_PERIODIC = "INTERVAL_PERIODIC";
	static final String S_INSTANCES_PERIODIC = "INSTANCES_PERIODIC";

	
	public FieldType editPartIs(String keyword){
		if (hasKeyword(keyword, LOCATION)) {
			return FieldType.LOCATION;
		}
		if (hasKeyword(keyword, DEADLINE)) {
			return FieldType.DEADLINE;
		}
		if (hasKeyword(keyword, START_EVENT)) {
			return FieldType.START_EVENT;
		}
		if (hasKeyword(keyword, END_EVENT)) {
			return FieldType.END_EVENT;
		}
		if (hasKeyword(keyword, INTERVAL_PERIODIC)) {
			return FieldType.INTERVAL_PERIODIC;
		}
		if (hasKeyword(keyword, INSTANCES_PERIODIC)) {
			return FieldType.INSTANCES_PERIODIC;
		}
		return null;
	}
	
	class KeywordMarker implements Comparable<KeywordMarker> {
		int index;
		FieldType typeOfField;

		int getIndex() {
			return index;
		}

		void setIndex(int i) {
			index = i;
		}

		FieldType getFieldType() {
			return typeOfField;
		}

		void setFieldType(FieldType fieldType) {
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
			case ADD :
				extractTaskInformation(commandString, taskObject);
				commandObject.addTask(taskObject);
				break;
			case EDIT :
				// need to fix edit
				if(commandString.split(" ").length ==1) {// if insufficient arguments .eg "edit"
					throw new Exception(ERROR_INVALID_NUMBER_OF_ARGUMENTS);
				} else {//if special editing.eg edit 1 loc nus
				argumentArray = getOneIndex(commandString);
				FieldType editType = editPartIs(getTwoIndex(commandString));
				if (editType != null) {
					executeSpecialEdit(editType, commandString, commandObject,
							argumentArray);
				} else {// normal editing
					commandObject.setArguments(argumentArray);
					commandString = clearFirstWord(commandString);

					extractTaskInformation(commandString, taskObject);
					commandObject.addTask(taskObject);
				}
				}
				break;
			case DELETE :
				argumentArray = getMultipleIndexes(commandString);
				commandObject.setArguments(argumentArray);
				break;
			case SAVETO :
				argumentArray = getSaveToArgument(commandString);
				commandObject.setArguments(argumentArray);
				break;
			case MARK :
				argumentArray = getMultipleIndexes(commandString);
				commandObject.setArguments(argumentArray);
				break;
			case UNMARK :
				argumentArray = getMultipleIndexes(commandString);
				commandObject.setArguments(argumentArray);
				break;
			case SEARCH :
				argumentArray = getMultipleIndexes(commandString);
				extractTaskInformation(commandString, taskObject);
				commandObject.addTask(taskObject);
				break;
			default:
			
		}
		return commandObject;
	}
	String[] getSaveToArgument(String commandString){
		String pathString = commandString.split(WHITE_SPACE_REGEX, 2)[0];
		return new String[]{ pathString };
	}
	String[] getOneIndex(String commandString){
		String indexString = commandString.split(WHITE_SPACE_REGEX, 2)[0];
		return new String[]{ indexString };
	}
	
	String getTwoIndex(String commandString){
		return commandString.split(WHITE_SPACE_REGEX)[1];
	}
	
	String[] getMultipleIndexes(String commandString){
		String[] indexArray = commandString.split(WHITE_SPACE_REGEX);
		return indexArray;
	}

	String clearFirstWord(String commandString) {
		String[] splitCommand = commandString.split(WHITE_SPACE_REGEX, 2);
		assert (splitCommand.length >= 1);
		if (splitCommand.length == 1) {
			return "";
		} else {
			return splitCommand[1];
		}
	}
	
	void executeSpecialEdit(FieldType editType, String commandString,
			 Command commandObject,
			String[] argumentArray) throws Exception {
		Task editTask = new Task();
		String newLocation = "";
		if (editType.equals(FieldType.LOCATION)) {
			String[] argumentArr = getMultipleIndexes(commandString);
			for (int i = 2; i < argumentArr.length; i++) {
				newLocation += argumentArr[i] + " ";
			}
			
			editTask.setLocation(newLocation);

			commandObject.setArguments(argumentArray);
			commandObject.addTask(editTask);
		} else if (editType.equals(FieldType.DEADLINE)) {
			ArrayList<KeywordMarker> keywordMarkers = getArrayOfKeywordIndexes(commandString);
			extractDate(commandString, keywordMarkers, editTask);
			commandObject.setArguments(argumentArray);
			commandObject.addTask(editTask);
		} else if (editType.equals(FieldType.START_EVENT)
				|| editType.equals(FieldType.END_EVENT)) {
			ArrayList<KeywordMarker> keywordMarkers = getArrayOfKeywordIndexes(commandString);
			extractDate(commandString, keywordMarkers, editTask);
			commandObject.setArguments(argumentArray);
			commandObject.addTask(editTask);
		} else if (editType.equals(FieldType.INTERVAL_PERIODIC)
				|| editType.equals(FieldType.INSTANCES_PERIODIC)) {
			ArrayList<KeywordMarker> keywordMarkers = getArrayOfKeywordIndexes(commandString);
			extractPeriodic(commandString, keywordMarkers, editTask, true);
			commandObject.setArguments(argumentArray);
			commandObject.addTask(editTask);
		} else {
			// assertion error
		}	
	}

	Command.Type identifyType(String commandString) throws Exception {
		if (commandString.length() == 0) {
			logger.info("identifyType: Command string is empty!");
			throw new Exception(ERROR_EMPTY_COMMAND_STRING);
		} else {
			String firstWord = commandString.split(WHITE_SPACE_REGEX, 2)[0];
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
			} else if (isCommandKeyword(firstWord, COMMAND_MARK)) {
				return Command.Type.MARK;
			} else if (isCommandKeyword(firstWord, COMMAND_UNMARK)) {
				return Command.Type.UNMARK;
			} else if (isCommandKeyword(firstWord, COMMAND_SEARCH)) {
				return Command.Type.SEARCH;
			} else {
				logger.info("identifyType: invalid command");
				throw new Exception(ERROR_INVALID_COMMAND_SPECIFIED);
			}
		}
	}

	boolean isCommandKeyword(String firstWordInCommandString,
			String[] keywords) {
		for (int i = 0; i < keywords.length; i++) {
			if (firstWordInCommandString.equalsIgnoreCase(keywords[i])) {
				return true;
			}
		}
		return false;
	}
	
	

	boolean extractTaskInformation(String commandString, Task taskObject)
			throws Exception {
		logger.fine("extractTaskInformation: getting keyword markers");
		ArrayList<KeywordMarker> keywordMarkers = getArrayOfKeywordIndexes(commandString);
	
		Collections.sort(keywordMarkers);
		
		
		
		logger.fine("extractedTaskInformation: extracting data from string");
		extractName(commandString, keywordMarkers, taskObject);
		boolean hasDate = extractDate(commandString, keywordMarkers, taskObject);
		extractLocation(commandString, keywordMarkers, taskObject);
		extractPeriodic(commandString, keywordMarkers, taskObject, hasDate); // valid only if date is specified
		return true;
	}
	
	boolean extractPeriodic(String commandString, ArrayList<KeywordMarker> keywordMarkers, Task taskObject, boolean hasDate) throws Exception{
		String[] periodicIntervalArguments = getArgumentsForField(commandString, keywordMarkers, FieldType.INTERVAL_PERIODIC);
		String[] periodicInstancesArguments = getArgumentsForField(commandString, keywordMarkers, FieldType.INSTANCES_PERIODIC);
		if(hasDate && periodicIntervalArguments!= null && periodicInstancesArguments != null){
			if(periodicIntervalArguments.length == 2){
				logger.finer("extractPeriodic: interval argument length is 2.");
				int periodicIntervalValue;
				try{
					periodicIntervalValue = Integer.parseInt(periodicIntervalArguments[0]);
				}catch(NumberFormatException e){
					throw new Exception(ERROR_INVALID_PERIODIC_INTERVAL_VALUE);
				}
				
				String periodicIntervalUnit = periodicIntervalArguments[1];
				if(hasKeyword(periodicIntervalUnit, PERIODIC)){
					taskObject.setPeriodicInterval(periodicIntervalValue + " " + periodicIntervalUnit);
				} else {
					logger.info("extractPeriodic: invalid period interval");
					throw new Exception(ERROR_INVALID_PERIODIC_INTERVAL);
				}
				//return true;
			} else {
				logger.info("extractPeriodic: invalid number of interval arguments - " + periodicIntervalArguments.length);
				throw new Exception(ERROR_INVALID_NUMBER_OF_ARGUMENTS);
			}
			
			if(periodicInstancesArguments.length == 1){
				logger.finer("extractPeriodic: periodic argument length is 1.");
				int periodicInstancesValue;
				try{
					periodicInstancesValue = Integer.parseInt(periodicInstancesArguments[0]);
				}catch(NumberFormatException e){
					throw new Exception(ERROR_INVALID_PERIODIC_INSTANCES);
				}
				taskObject.setPeriodicRepeats(periodicInstancesArguments[0]);
			} else {
				logger.info("extractPeriodic: invalid number of instance arguments - " + periodicInstancesArguments.length);
				throw new Exception(ERROR_INVALID_NUMBER_OF_ARGUMENTS);
			}

			return true;
		}
		return false;
	}
	
	boolean extractLocation(String commandString, ArrayList<KeywordMarker> keywordMarkers, Task taskObject) throws Exception{
		String[] locationArguments = getArgumentsForField(commandString, keywordMarkers, FieldType.LOCATION);
		String location = "";
		if(locationArguments!= null){
			for(int i =0; i < locationArguments.length; i++) {
				location += locationArguments[i] + " ";
			}
			taskObject.setLocation(location);
			logger.finer("extractLocation: location added");
			return true;
/*	
			if(locationArguments.length == 1){
				logger.finer("extractLocation: argument length is 1.");
				taskObject.setLocation(locationArguments[0]);
				return true;
			} else {
				logger.info("extractLocation: invalid number of arguments");
				throw new Exception(ERROR_INVALID_NUMBER_OF_ARGUMENTS);
			}*/
		}
		return false;
	}

	boolean extractDate(String commandString,
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

	Calendar parseDate(String[] dateArguments) throws Exception {
		logger.fine("parseDate: parsing date");
		int date, month, year;
		Calendar helperDate;
		if (!hasKeyword(dateArguments, DATE_SPECIAL)
				&& dateArguments.length != 1) {
			try {
				date = Integer.parseInt(dateArguments[0]);
			} catch (NumberFormatException e) {
				throw new Exception(ERROR_INVALID_DATE_SPECIFIED);
			}
			month = Arrays.asList(MONTHS).indexOf(dateArguments[1]);
			if (month == -1) {
				throw new Exception(ERROR_INVALID_MONTH_SPECIFIED);
			}
			if (dateArguments.length == 3) {
				year = Integer.parseInt(dateArguments[2]);
			} else {
				year = Calendar.getInstance().get(Calendar.YEAR);
			}

			helperDate = new GregorianCalendar();
			helperDate.clear();
			helperDate.set(year, month, date);
		} else if (dateArguments.length == 2) { // this/next <day>
			logger.finer("parseDate: dateArguments[0] contains "
					+ dateArguments[0]);
			logger.finer("parseDate: dateArguments[1] contains "
					+ dateArguments[1]);
			String firstWord = dateArguments[0];
			String secondWord = dateArguments[1];

			if (hasKeyword(secondWord, DAYS)) {
				int dayIndex = Arrays.asList(DAYS).indexOf(secondWord) + 1;
				assert (firstWord.equalsIgnoreCase(DATE_SPECIAL[0]) || firstWord
						.equalsIgnoreCase(DATE_SPECIAL[1]));
				if (firstWord.equalsIgnoreCase(DATE_SPECIAL[0])) {// this
					date = getNearestDate(dayIndex);
				} else {// next
					date = getNearestDate(dayIndex) + DAYS.length;
				}
				logger.finer("parseDate: this/next day determined to be "
						+ date);
			} else {
				logger.info("parseDate: invalid day");
				throw new Exception(ERROR_INVALID_DAY_SPECIFIED);
			}

			month = Calendar.getInstance().get(Calendar.MONTH);
			year = Calendar.getInstance().get(Calendar.YEAR);

			helperDate = new GregorianCalendar();
			helperDate.clear();
			helperDate.set(year, month, date);
			return helperDate;
		} else if (hasKeyword(dateArguments, DATE_SPECIAL)
				&& dateArguments.length == 1) { // today/tomorrow
			if (dateArguments[0].equalsIgnoreCase(DATE_SPECIAL[2])) {
				return new GregorianCalendar();
			} else {
				helperDate = new GregorianCalendar();
				helperDate.add(Calendar.DATE, 1);
				return helperDate;
			}
		} else {
			logger.info("parseDate: unknown date arguments");
			throw new Exception("Error: Invalid arguments for date");
		}
		return helperDate;
	}
	
	/**
	 * 
	 * @param givenDayIndex day of the week sunday to saturday -> 1 to 7 
	 * @return date of the nearest day
	 */
	int getNearestDate(int givenDayIndex){
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
	
	boolean hasKeyword(String word, String[] keywords){
		for(int i = 0; i < keywords.length; i++){
			if(word.equalsIgnoreCase(keywords[i])){
				return true;
			}
		}
		return false;
	}
	
	boolean hasKeyword(String[] words, String[] keywords){
		for(int i = 0; i < words.length; i++){
			if (hasKeyword(words[i], keywords)) {
				return true;
			}
		}
		return false;
	}

	String[] getArgumentsForField(String commandString,
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
				String[] argumentWords = argumentString.split(WHITE_SPACE_REGEX);
				return argumentWords;
			}
		}
		return null;
	}

	boolean extractName(String commandString,
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

	ArrayList<KeywordMarker> getArrayOfKeywordIndexes(
			String commandString) throws Exception {
		ArrayList<KeywordMarker> keywordMarkerList = new ArrayList<KeywordMarker>();
		
		getLocationField(keywordMarkerList, commandString);
		getDateField(keywordMarkerList, commandString);
		getPeriodicField(keywordMarkerList, commandString);
		return keywordMarkerList;
	}
	
	boolean getPeriodicField(ArrayList<KeywordMarker> curMarkerList,
			String commandString) throws Exception {
		KeywordMarker markerForIntervalPeriodic = getKeywordMarker(commandString,
				INTERVAL_PERIODIC);
		KeywordMarker markerForInstancesPeriodic = getKeywordMarker(commandString,
				INSTANCES_PERIODIC);
		if (markerForIntervalPeriodic != null && markerForInstancesPeriodic != null) {
			markerForIntervalPeriodic.setFieldType(FieldType.INTERVAL_PERIODIC);
			curMarkerList.add(markerForIntervalPeriodic);
			markerForInstancesPeriodic.setFieldType(FieldType.INSTANCES_PERIODIC);
			curMarkerList.add(markerForInstancesPeriodic);
			return true;
		} else if (markerForIntervalPeriodic != null) {
			throw new Exception("Error: Missing number of repeats");
		} else if (markerForInstancesPeriodic != null) {
			throw new Exception("Error: Missing repeat interval");
		} else {
			return false;
		}
	}

	boolean getLocationField(ArrayList<KeywordMarker> curMarkerList,
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

	boolean getDateField(ArrayList<KeywordMarker> curMarkerList,
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

	KeywordMarker getKeywordMarker(String commandString,
			String[] listOfKeywords) {
		for (int i = 0; i < listOfKeywords.length; i++) {
			String curKeyword = String.format(" %s ", listOfKeywords[i]);
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
}