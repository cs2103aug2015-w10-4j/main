package parser;

import global.Command;
import global.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
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
	
	static final String ERROR_MISSING_INTERVAL = "Error: Missing repeat interval.";
	static final String ERROR_MISSING_REPEATS = "Error: Missing number of repeats.";
	static final String ERROR_INVALID_DATE_ARGUMENTS = "Error: Invalid arguments for date.";
	static final String ERROR_INVALID_PERIODIC_INSTANCES = "Error: Invalid periodic instances.";
	static final String ERROR_INVALID_PERIODIC_INTERVAL_VALUE = "Error: Invalid periodic interval value.";
	static final String ERROR_INVALID_MONTH_SPECIFIED = "Error: Invalid month specified!";
	static final String ERROR_INVALID_DATE_SPECIFIED = "Error: Invalid date specified!";
	static final String ERROR_INVALID_TIME = "Error: Invalid time!";
	static final String ERROR_INVALID_TIME_FORMAT = "Error: Invalid time format specified!";
	static final String ERROR_INVALID_PERIODIC_INTERVAL = "Error: Invalid periodic interval specified.";
	static final String ERROR_MISSING_START_TIME = "Error: An end time has been entered without start time!";
	static final String ERROR_MISSING_END_TIME = "Error: A start time has been entered without end time!";
	static final String ERROR_MISSING_START_OR_END_TIME = "Error: Start time or end time missing.";
	static final String ERROR_INVALID_DAY_SPECIFIED = "Error: Invalid day specified!";
	static final String ERROR_INVALID_NUMBER_OF_ARGUMENTS = "Error: Invalid number of arguments.";
	static final String ERROR_INVALID_COMMAND_SPECIFIED = "Error: Invalid command specified!";
	static final String ERROR_EMPTY_COMMAND_STRING = "Error: Command string is empty.";
	static final String ERROR_EMPTY_TASK_NAME = "Error: Task name is empty.";

	
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
	static final String[] COMMAND_HELP = { "help" };

	static final String[] DATE_SPECIAL = { "this", "next", "today", "tomorrow" };
	static final String[] MONTHS = { "jan", "feb", "mar", "apr", "may",
			"jun", "jul", "aug", "sep", "oct", "nov", "dec" };
	static final String[] DAYS = { "sunday" , "monday" , "tuesday" , "wednesday" , "thursday" ,"friday", "saturday" };
	

	static final String[] PERIODIC = { "days", "weeks" , "months" };

	enum FieldType {
		START_EVENT, END_EVENT, DEADLINE, LOCATION, INTERVAL_PERIODIC, INSTANCES_PERIODIC
	}

	static final String[] LOCATION = { "loc", "at" };
	static final String[] DEADLINE = { "by" , "before" , "on" };
	static final String[] TIME = { "H", "AM", "PM" , "am" , "pm" };
	static final String TIME_SEPARATOR = ".";
	static final String[] START_EVENT = { "start" , "from" , "starts" , "starting" };
	static final String[] END_EVENT = { "end" , "to" , "ends" , "until" , "ending" };
	static final String[] INTERVAL_PERIODIC = { "every" , "repeats" };
	static final String[] INSTANCES_PERIODIC = { "for" };
	
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
		commandString = removeFirstWord(commandString);
		Command commandObject = new Command(commandType);
		Task taskObject = new Task();
		String[] argumentArray;
		
		switch (commandType) {
			case ADD :
				extractTaskInformation(commandString, taskObject);
				commandObject.addTask(taskObject);
				break;
			case EDIT :
				if (commandString.split(" ").length == 1) {// if insufficient arguments .eg "edit"
					throw new Exception(ERROR_INVALID_NUMBER_OF_ARGUMENTS);
				} else {
					argumentArray = getParameterOneAsArray(commandString);
					commandObject.setArguments(argumentArray);
					commandString = removeFirstWord(commandString);

					extractFieldInformation(commandString, taskObject);
					commandObject.addTask(taskObject);
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
				extractFieldInformation(commandString, taskObject);
				commandObject.addTask(taskObject);
				break;
			default:
			
		}
		return commandObject;
	}
	
	String[] getSaveToArgument(String commandString) {
		return new String[] { commandString };
	}
	
	String[] getParameterOneAsArray(String commandString) {
		String indexString = commandString.split(WHITE_SPACE_REGEX, 2)[0];
		return new String[] { indexString };
	}
	
	String getParameterTwo(String commandString) {
		return commandString.split(WHITE_SPACE_REGEX)[1];
	}

	String[] getMultipleIndexes(String commandString) {
		String[] indexArray = commandString.split(WHITE_SPACE_REGEX);
		return indexArray;
	}

	String removeFirstWord(String commandString) {
		String[] splitCommand = commandString.split(WHITE_SPACE_REGEX, 2);
		assert (splitCommand.length >= 1);
		if (splitCommand.length == 1) {
			return "";
		} else {
			return splitCommand[1];
		}
	}
	
	/**
	 * Attempts to extract field information for edit and search commands, where
	 * task name is not compulsory
	 * @param commandString
	 * @param taskObject
	 * @return
	 * @throws Exception
	 */
	boolean extractFieldInformation(String commandString, Task taskObject)
			throws Exception {
		logger.fine("extractFieldInformation: getting keyword markers");
		ArrayList<KeywordMarker> keywordMarkers = getArrayOfKeywordMarkers(commandString);
	
		Collections.sort(keywordMarkers);
		
		logger.fine("extractedFieldInformation: extracting data from string");
		extractName(commandString, keywordMarkers, taskObject, false);
		extractDate(commandString, keywordMarkers, taskObject, false);
		extractLocation(commandString, keywordMarkers, taskObject);
		return true;
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
			} else if (isCommandKeyword(firstWord, COMMAND_HELP)) {
				return Command.Type.HELP;
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
	
	/**
	 * Extracts data from the command string and puts them into the relevant field in the 
	 * task object
	 * @param commandString
	 * @param taskObject
	 * @return
	 * @throws Exception
	 */
	boolean extractTaskInformation(String commandString, Task taskObject)
			throws Exception {
		logger.fine("extractTaskInformation: getting keyword markers");
		ArrayList<KeywordMarker> keywordMarkers = getArrayOfKeywordMarkers(commandString);
	
		Collections.sort(keywordMarkers);
		
		logger.fine("extractedTaskInformation: extracting data from string");
		extractName(commandString, keywordMarkers, taskObject, true);
		boolean hasDate = extractDate(commandString, keywordMarkers, taskObject, true);
		extractLocation(commandString, keywordMarkers, taskObject);
		extractPeriodic(commandString, keywordMarkers, taskObject, hasDate); // valid only if date is specified
		return true;
	}
	
	boolean extractPeriodic(String commandString, ArrayList<KeywordMarker> keywordMarkers, Task taskObject, boolean hasDate) throws Exception{
		String[] periodicIntervalArguments = getArgumentsForField(commandString, keywordMarkers, FieldType.INTERVAL_PERIODIC);
		String[] periodicInstancesArguments = getArgumentsForField(commandString, keywordMarkers, FieldType.INSTANCES_PERIODIC);
		if (hasDate && periodicIntervalArguments != null
				&& periodicInstancesArguments != null) {
			if (periodicIntervalArguments.length == 2) {
				logger.finer("extractPeriodic: interval argument length is 2.");
				int periodicIntervalValue;
				try {
					periodicIntervalValue = Integer
							.parseInt(periodicIntervalArguments[0]);
				} catch (NumberFormatException e) {
					throw new Exception(ERROR_INVALID_PERIODIC_INTERVAL_VALUE);
				}

				String periodicIntervalUnit = periodicIntervalArguments[1];
				if (hasKeyword(periodicIntervalUnit, PERIODIC)) {
					taskObject.setPeriodicInterval(periodicIntervalValue + " "
							+ periodicIntervalUnit);
				} else {
					logger.info("extractPeriodic: invalid period interval");
					throw new Exception(ERROR_INVALID_PERIODIC_INTERVAL);
				}
			} else {
				logger.info("extractPeriodic: invalid number of interval arguments - "
						+ periodicIntervalArguments.length);
				throw new Exception(ERROR_INVALID_NUMBER_OF_ARGUMENTS);
			}

			if (periodicInstancesArguments.length == 1) {
				logger.finer("extractPeriodic: periodic argument length is 1.");
				int periodicInstancesValue;
				try {
					periodicInstancesValue = Integer
							.parseInt(periodicInstancesArguments[0]);
				} catch (NumberFormatException e) {
					throw new Exception(ERROR_INVALID_PERIODIC_INSTANCES);
				}
				taskObject.setPeriodicRepeats(periodicInstancesArguments[0]);
			} else {
				logger.info("extractPeriodic: invalid number of instance arguments - "
						+ periodicInstancesArguments.length);
				throw new Exception(ERROR_INVALID_NUMBER_OF_ARGUMENTS);
			}

			return true;
		}
		return false;
	}
	
	/**
	 * Extracts the location arguments as an array, joins
	 * them up into a location string, and set it as the location
	 * @param commandString
	 * @param keywordMarkers
	 * @param taskObject
	 * @return
	 * @throws Exception
	 */
	boolean extractLocation(String commandString, ArrayList<KeywordMarker> keywordMarkers, Task taskObject) throws Exception{
		String[] locationArguments = getArgumentsForField(commandString, keywordMarkers, FieldType.LOCATION);
		String location = "";
		if (locationArguments != null) {
			for (int i = 0; i < locationArguments.length; i++) {
				location += locationArguments[i] + " ";
			}
			location = location.trim();
			taskObject.setLocation(location);
			logger.finer("extractLocation: location added");
			return true;
		}
		return false;
	}

	boolean extractDate(String commandString,
			ArrayList<KeywordMarker> keywordMarkers, Task taskObject, boolean isNewTask) throws Exception {
		// check deadline
		logger.fine("extractDate: getting date arguments");
		String[] deadlineArguments = getArgumentsForField(commandString,
				keywordMarkers, FieldType.DEADLINE);

		if (deadlineArguments != null) {
			for (int i = 0; i < deadlineArguments.length; i++) {
				logger.finer("extractDate: deadlineArguments[" + i
						+ "] contains " + deadlineArguments[i]);
			}
		}
		
		// check start/end event
		String[] startEventArguments = getArgumentsForField(commandString,
				keywordMarkers, FieldType.START_EVENT);
		String[] endEventArguments = getArgumentsForField(commandString,
				keywordMarkers, FieldType.END_EVENT);
		
		if (startEventArguments != null) {
			for (int i = 0; i < startEventArguments.length; i++) {
				logger.finer("extractDate: startEventArguments[" + i
						+ "] contains " + startEventArguments[i]);
			}
		}
		
		logger.fine("extractDate: got date arguments. attempting to parse dates");
		if (deadlineArguments != null) {
			Calendar argumentDate = parseDate(deadlineArguments);
			taskObject.setEndingTime(argumentDate);
			logger.fine("extractDate: deadline set");
			return true;
		} else if (startEventArguments != null && endEventArguments != null) {
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
		} else if ((startEventArguments != null || endEventArguments != null)
				&& isNewTask) {
			throw new Exception(ERROR_MISSING_START_OR_END_TIME);
		} else if (startEventArguments != null) {
			Calendar argumentStartDate = parseDate(startEventArguments);
			taskObject.setStartingTime(argumentStartDate);
			return true;
		} else if (endEventArguments != null) {
			Calendar argumentEndDate = parseDate(endEventArguments);
			taskObject.setEndingTime(argumentEndDate);
			return true;
		} else {
			return false;
		}
	}

	// if time not specified, it will be parsed to 09:00 AM
	// TIME keyword in commandString must be capitalized
	Calendar parseDate(String[] dateArgumentsTemp) throws Exception {
		logger.fine("parseDate: parsing date");
		int date, month, year, hour = 9, minute = 0, isAMorPM = 0;
		Integer hourOfDay = null;
		Calendar helperDate;
		
		// start of parsing time
		// time argument in dateArguments is removed from array
		// new array is created since array length cannot be modified
		String[] dateArguments;
		if (hasTimeKeyword(dateArgumentsTemp, TIME)) {
			dateArguments = new String[dateArgumentsTemp.length - 1];
			for (int i = 0; i < dateArgumentsTemp.length; i++) {
				boolean keywordFound = false;
				for (int n = 0; n < TIME.length; n++) {
					// low-level check if TIME keywords is present at the end of the argument e.g. 6(pm)
					if (dateArgumentsTemp[i].endsWith(TIME[n])) {
						keywordFound = true;
						try {
							String tempTime = dateArgumentsTemp[i];
							tempTime = tempTime.replace(TIME[n], "");
							if (n == 0) {	// h: 24 hour time format
								hourOfDay = Integer.parseInt(tempTime.substring(0, 2));
								minute = Integer.parseInt(tempTime.substring(2));
							} else {	// am/pm: 12 hour time format
								isAMorPM = (n == 1 || n == 3) ? 0 : 1;
								if (tempTime.contains(TIME_SEPARATOR)) { // check if minutes is specified
									String[] tempTimeSplit = tempTime.split("\\" + TIME_SEPARATOR);
									minute = Integer.parseInt(tempTimeSplit[1]);
									hour = Integer.parseInt(tempTimeSplit[0]);
								} else {
									hour = Integer.parseInt(tempTime);
								}
								// for 12 hour time format, 12am/pm means hour = 0
								hour = hour == 12 ? 0 : hour;
							}
							// although Calendar can parse beyond this range, it will be
							// misleading for the user. so throw exception
							if (hour > 12 || hour < 0 || minute > 59) {
								throw new Exception(ERROR_INVALID_TIME);
							}
						} catch (ArrayIndexOutOfBoundsException|NumberFormatException e) {
							throw new Exception(ERROR_INVALID_TIME_FORMAT);
						} catch (Exception e) {
							throw new Exception(ERROR_INVALID_TIME_FORMAT);
						}
					}
				}
				if (!keywordFound) {
					dateArguments[i] = dateArgumentsTemp[i];
				}			
			}
		} else {
			dateArguments = dateArgumentsTemp;
		}
		
		if (dateArguments.length == 0) {
			throw new Exception(ERROR_INVALID_NUMBER_OF_ARGUMENTS);
		} else if(!hasKeyword(dateArguments, DATE_SPECIAL)
				&& dateArguments.length != 1) {
			try {
				date = Integer.parseInt(dateArguments[0]);
			} catch (NumberFormatException e) {
				throw new Exception(ERROR_INVALID_DATE_SPECIFIED);
			}
			month = getIndexOfList(dateArguments[1], Arrays.asList(MONTHS));
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
				int dayIndex = getIndexOfList(secondWord, Arrays.asList(DAYS)) + 1;
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
		} else if (hasKeyword(dateArguments, DATE_SPECIAL)
				&& dateArguments.length == 1) { // today/tomorrow
			if (dateArguments[0].equalsIgnoreCase(DATE_SPECIAL[2])) {
				helperDate = new GregorianCalendar();
			} else {
				helperDate = new GregorianCalendar();
				helperDate.add(Calendar.DATE, 1);
			}
		} else if (hasKeyword(dateArguments, DAYS) && dateArguments.length == 1) {
			int dayIndex = getIndexOfList(dateArguments[0], Arrays.asList(DAYS)) + 1;
			date = getNearestDate(dayIndex);
			
			month = Calendar.getInstance().get(Calendar.MONTH);
			year = Calendar.getInstance().get(Calendar.YEAR);

			helperDate = new GregorianCalendar();
			helperDate.clear();
			helperDate.set(year, month, date);
		} else {
			logger.info("parseDate: unknown date arguments");
			throw new Exception(ERROR_INVALID_DATE_ARGUMENTS);
		}

		if (hourOfDay == null) {
			helperDate.set(Calendar.HOUR, hour);
			helperDate.set(Calendar.AM_PM, isAMorPM);
		} else {
			helperDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
		}
		helperDate.set(Calendar.MINUTE, minute);
		return helperDate;
	}
	
	/**
	 * Functions similarly to <List>.indexOf(<String>), but is not case-sensitive
	 * 
	 * @param word
	 * @param listOfWords
	 * @return index of word in list if found, else -1
	 */
	private int getIndexOfList(String word, List<String> listOfWords) {
		for (int i = 0; i < listOfWords.size(); i++) {
			if (listOfWords.get(i).equalsIgnoreCase(word)) {
				return i;
			}
		}
		return -1;
	}
	

	/**
	 * @param givenDayIndex day of the week sunday to saturday -> 1 to 7 
	 * @return date of the nearest day
	 */
	int getNearestDate(int givenDayIndex) {
		Calendar dateHelper = Calendar.getInstance();
		int curDayIndex = dateHelper.get(Calendar.DAY_OF_WEEK);
		logger.fine("getNearestDate: given day is " + givenDayIndex);
		logger.fine("getNearestDate: today is " + curDayIndex);
		int todayDate = dateHelper.get(Calendar.DATE);

		int difference = ((givenDayIndex - curDayIndex) % DAYS.length + DAYS.length)
				% DAYS.length;
		logger.fine("getNearestDate: difference is " + difference);
		int newDate = todayDate + difference;
		return newDate;
	}
	
	boolean hasKeyword(String word, String[] keywords) {
		for (int i = 0; i < keywords.length; i++) {
			if (word.equalsIgnoreCase(keywords[i])) {
				return true;
			}
		}
		return false;
	}
	
	boolean hasKeyword(String[] words, String[] keywords) {
		for (int i = 0; i < words.length; i++) {
			if (hasKeyword(words[i], keywords)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Method created to search for TIME keywords. Can't use hasKeyword since 
	 * the keyword is concatenated with the time itself, e.g. '6pm' instead of '6 pm'
	*/
	boolean hasTimeKeyword(String[] words, String[] keywords) {
		for (int i = 0; i < words.length; i++) {
			for (int n = 0; n < keywords.length; n++) {
				if (words[i].contains(keywords[n])) {
					return true;
				}
			}
		}
		return false;
	}
	

	/**
	 * Method to obtain arguments after a keyword and before the
	 * next keyword
	 * @param commandString
	 * @param keywordMarkers
	 * @param typeOfField
	 * @return String array of argument words
	 */
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
						indexSearch).replaceAll("\\\\", "");
				String[] argumentWords = argumentString.split(WHITE_SPACE_REGEX);
				return argumentWords;
			}
		}
		return null;
	}

	/**
	 * Extracts task name from a string
	 * It is assumed that the start of the string is the task name
	 * @param commandString
	 * @param keywordMarkers
	 * @param taskObject
	 * @return
	 * @throws Exception
	 */
	boolean extractName(String commandString,
			ArrayList<KeywordMarker> keywordMarkers, Task taskObject, boolean isNewTask)
			throws Exception {
		logger.fine("extractName: extracting name");
		String taskName = null;
		if (commandString.length() == 0 && isNewTask) {
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
			if (searchIndex >= 0) {
				taskName = commandString.substring(0, searchIndex);
			} else {
				if (isNewTask) {
					throw new Exception(ERROR_EMPTY_TASK_NAME);
				}
			}
		} else {
			taskName = commandString;
		}
		taskName = taskName.replaceAll("\\\\", "");
		taskObject.setName(taskName);
		return true;
	}

	/**
	 * Attempts to mark the relevant fields of a task, and adds a marker
	 * to mark the starting of each field's arguments
	 * 
	 * @param commandString
	 * @return
	 * @throws Exception
	 */
	ArrayList<KeywordMarker> getArrayOfKeywordMarkers(
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
			throw new Exception(ERROR_MISSING_REPEATS);
		} else if (markerForInstancesPeriodic != null) {
			throw new Exception(ERROR_MISSING_INTERVAL);
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
		if (markerForStartEvent != null) {
			markerForStartEvent.setFieldType(FieldType.START_EVENT);
			curMarkerList.add(markerForStartEvent);
		}
		if (markerForEndEvent != null) {
			markerForEndEvent.setFieldType(FieldType.END_EVENT);
			curMarkerList.add(markerForEndEvent);
		}
		return true;
	}

	/**
	 * Marks the index 2 positions after the found word. 
	 * This index is supposed to indicate the start of the field's arguments
	 * @param commandString
	 * @param listOfKeywords
	 * @return
	 */
	KeywordMarker getKeywordMarker(String commandString,
			String[] listOfKeywords) {
		for (int i = 0; i < listOfKeywords.length; i++) {
			String curKeyword = String.format(" %s ", listOfKeywords[i]);
			String curKeywordStart = String.format("%s ", listOfKeywords[i]);
			int keywordIndex = commandString.indexOf(curKeyword);
			if (keywordIndex != -1 || commandString.startsWith(curKeywordStart)) {
				int indexOfArgument;
				if(keywordIndex != -1) {
					indexOfArgument = keywordIndex + curKeyword.length();
				} else {
					indexOfArgument = -1 + curKeyword.length();
				}
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