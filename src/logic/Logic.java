package logic;

import global.Command;
import global.Task;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LogManager;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

import parser.Parser;
import storage.Storage;
import storage.JsonFormatStorage;
import ui.UI;
import ui.UI.DisplayType;

/**
 * This file contains the main program of the command-line calendar, Tasky.
 * Please read our user guide at README.md if there are any questions.
 * 
 * @author cs2103aug2015-w10-4j
 *
 */
public class Logic {
	private static final String ERROR_START_TIME_WITHOUT_END_TIME = "Error: Cannot add start time without end time!";
	private static final String ERROR_START_TIME_BEFORE_END_TIME = "Error: Starting time cannot be after ending time.";
	private static final String FILTER_TITLE_LOCATION = "Location: ";
	private static final String FILTER_TITLE_TASK_NAME = "Task: ";
	private static final String FILTER_TITLE_TIME = "Time: ";
	private static final String FILTER_SEARCH_SEPARATOR = ", ";
	private static final String TITLE_TOP_DISPLAY = "Top %d Items for ";
	private static final String TITLE_TOMORROW = "Tomorrow";
	private static final String TITLE_TODAY = "Today";
	private static final String LOG_FILE_NAME = "tasky.log";
	private static final String CONFIG_FILE_NAME = "config.txt";
	private static final String DEFAULT_LOGGING_LEVEL_STRING = "INFO";
	private static final String DEFAULT_SAVE_FILE_PATH = "save.txt";
	private static final int DEFAULT_DISPLAY_SIZE = 3;
	private static final String PROPERTY_KEY_LOGGING_LEVEL = "loggingLevel";
	private static final String PROPERTY_KEY_SAVE_FILE = "saveFile";
	private static final String PROPERTY_KEY_DISPLAY_SIZE = "defaultDisplaySize";
	/*
	 * Declaration of object variables
	 */
	Logger logger = Logger.getGlobal();
	UI UIObject;
	Parser parserObject;
	Storage storageObject;
	History historyObject;
	Properties propObject;
	ArrayList<Task> listOfTasks = new ArrayList<Task>();
	ArrayList<Task> listOfShownTasks = new ArrayList<Task>();
	ArrayList<Task> listFilter = new ArrayList<Task>();
	boolean shouldShowDone = true;
	boolean shouldShowUndone = true;
	int displaySize = DEFAULT_DISPLAY_SIZE;
	private static final int ID_RANGE = 1000;
	private static final int RECURRING_MAX = 100;
	private static final Level DEFAULT_LEVEL = Level.INFO;
	
	// date format converter
	static SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM");

	/*
	 * Static strings - errors and messages
	 */
	private static final String MESSAGE_WELCOME = "Welcome to Tasky! This is an open source project";
	private static final String MESSAGE_PROMPT_COMMAND = "command :";
	private static final String MESSAGE_UNDO = "Undo : ";
	private static final String MESSAGE_REDO = "Redo : ";
	private static final String MESSAGE_SUCCESS_HISTORY_ADD = "Deleted item(s) restored.";
	private static final String MESSAGE_SUCCESS_HISTORY_DELETE = "Added item(s) removed.";
	private static final String MESSAGE_SUCCESS_HISTORY_EDIT = "Reverted edits.";
	private static final String MESSAGE_SUCCESS_ADD = "Item(s) successfully added.";
	private static final String MESSAGE_SUCCESS_DELETE = "Item(s) successfully deleted.";
	private static final String MESSAGE_SUCCESS_MARK = "Item(s) successfully marked as done.";
	private static final String MESSAGE_SUCCESS_UNMARK = "Item(s) successfully marked as undone.";
	private static final String MESSAGE_SUCCESS_SEARCH = "Search results.";
	private static final String MESSAGE_SUCCESS_EDIT = "Item(s) successfully edited.";
	private static final String MESSAGE_SUCCESS_EXIT = "Exiting program...";
	private static final String MESSAGE_SUCCESS_DISPLAY = "Displaying items.";
	private static final String MESSAGE_SUCCESS_CHANGE_FILE_PATH = "File path successfully changed.";
	private static final String MESSAGE_SUCCESS_NO_CHANGE_FILE_PATH = "File path not changed. Entered file path is the same as current one used.";
	private static final String MESSAGE_DISPLAY_EMPTY = "No items to display.";
	private static final String MESSAGE_SUCCESS_HELP = "Showing help message";
	private static final String IDENTIFIER_ALL = "all";
	private static final String ERROR_WRITING_FILE = "Error: Unable to write file.";
	private static final String ERROR_CREATING_FILE = "Error: Unable to create file.";
	private static final String ERROR_FILE_NOT_FOUND = "Error: Data file not found.";
	private static final String ERROR_LOG_FILE_INITIALIZE = "Error: Cannot initialize log file.";
	private static final String ERROR_INVALID_ARGUMENT = "Error: Invalid argument for command.";
	private static final String ERROR_INVALID_COMMAND = "Error: Invalid command.";
	private static final String ERROR_NO_COMMAND_HANDLER = "Error: Handler for this command type has not been defined.";
	private static final String ERROR_HISTORY_NO_COMMAND_HANDLER = "Error: History called by unidentified command.";
	private static final String ERROR_INVALID_INDEX = "Error: There is no item at this index.";
	private static final String ERROR_UI_INTERRUPTED = "Error: UI prompt has been interrupted.";
	private static final String ERROR_NO_HISTORY = "Error: No history found.";
	private static final String ERROR_CANNOT_WRITE_TO_HISTORY = "Error: Unable to store command in history.";
	private static final String ERROR_CANNOT_PARSE_PERIODIC_VALUES = "Error: Unable to parse values for periodic";
	private static final String ERROR_NO_FILTER = "Error: No filter detected for search";
	private static final String ERROR_EDIT_CANNOT_RECURRING = "Error: Cannot convert a normal task to recurring";
	

	private static final String WARNING_TIMING_CLASH = "WARNING: There are clashing timings between tasks.";
	
	private static final String WHITE_SPACE_REGEX = "\\s+";

	/*
	 * Main program
	 */
	public static void main(String[] args) {
		Logic logicObject = new Logic();
		logicObject.start();
	}

	/*
	 * Constructor to initialize object variables
	 */
	public Logic() {
		UIObject = new UI();
		parserObject = new Parser();
		storageObject = new JsonFormatStorage(true);
		historyObject = new History();
		propObject = new Properties();
		try {
			FileHandler logHandler = new FileHandler(LOG_FILE_NAME);
			LogManager.getLogManager().reset(); // removes printout to console
												// aka root handler
			logHandler.setFormatter(new SimpleFormatter()); // set output to a
															// human-readable
															// log format
			logger.addHandler(logHandler);
			
			File configFile = new File(CONFIG_FILE_NAME);
			if (configFile.exists()) { // assumes that the config file has not been incorrectly modified
				BufferedReader bufReader = new BufferedReader(new FileReader(
						new File(CONFIG_FILE_NAME)));
				propObject.load(bufReader);
				bufReader.close();
			} else {
				configFile.createNewFile();
				propObject.setProperty(PROPERTY_KEY_SAVE_FILE, DEFAULT_SAVE_FILE_PATH);
				propObject.setProperty(PROPERTY_KEY_LOGGING_LEVEL, DEFAULT_LOGGING_LEVEL_STRING);
				propObject.setProperty(PROPERTY_KEY_DISPLAY_SIZE, Integer.toString(DEFAULT_DISPLAY_SIZE));
				writeProperties();
			}
			storageObject.saveFileToPath(propObject.getProperty(PROPERTY_KEY_SAVE_FILE));
			String logLevelString = propObject.getProperty(PROPERTY_KEY_LOGGING_LEVEL);
			displaySize = Integer.parseInt(propObject.getProperty(PROPERTY_KEY_DISPLAY_SIZE));
			switch (logLevelString) {
				case "WARNING":
					logger.setLevel(Level.WARNING);
					break;
				case "INFO":
					logger.setLevel(Level.INFO);
					break;
				case "FINE":
					logger.setLevel(Level.FINE);
					break;
				case "FINER":
					logger.setLevel(Level.FINER);
					break;
				case "FINEST":
					logger.setLevel(Level.FINEST);
					break;
				default:
					logger.setLevel(DEFAULT_LEVEL);
			}
			updateListOfTasks();
			
		} catch (FileNotFoundException e) {
			UIObject.showToUser(ERROR_FILE_NOT_FOUND);
		} catch (SecurityException | IOException | NumberFormatException e) {
			UIObject.showToUser(ERROR_LOG_FILE_INITIALIZE);
		} catch (Exception e) {
			UIObject.showToUser(e.getMessage());
		}
	}

	void start() {
		initializeDisplay();
		readAndExecuteUserInput();
	}

	void initializeDisplay() {
		showUpdatedItems();
		UIObject.showStatusToUser(MESSAGE_WELCOME);
	}

	/**
	 * Repeatedly Reads the user input, parses the command, executes the command
	 * object, shows the result in UI, writes latest task list to file until the
	 * program exits
	 */
	void readAndExecuteUserInput() {
		while (true) {
			try {
				String userInput = UIObject.promptUser(MESSAGE_PROMPT_COMMAND);
				Command commandObject = parserObject.parseCommand(userInput);
				
				String executionResult = executeCommand(commandObject, true,
						true);
				UIObject.showStatusToUser(executionResult);
				if (commandObject.getCommandType() == Command.Type.HELP) {
					showHelpMsg();
				} else {
					showUpdatedItems();
				}
				
				storageObject.writeItemList(listOfTasks);
			} catch (InterruptedException e) {
				// something interrupted the UI's wait for user input
				UIObject.showStatusToUser(ERROR_UI_INTERRUPTED);
			} catch (IOException e) {
				// error writing
				UIObject.showStatusToUser(ERROR_WRITING_FILE);
			} catch (Exception e) {
				// warning from parsing user command
				//e.printStackTrace();
				UIObject.showStatusToUser(e.getMessage());
			}
		}
	}

	/**
	 * Executes a command based on commandObject
	 * 
	 * 
	 * @param commandObject
	 * @param isUserInput
	 *            false if command is called from redo
	 * @param isUndoHistory
	 *            true if command is called from undo false if command is called
	 *            by user directly
	 * 
	 * @return status string to be shown to user
	 */
	String executeCommand(Command commandObject, boolean isUserInput,
			boolean isUndoHistory) {
		if (commandObject == null) {
			return ERROR_INVALID_COMMAND;
		}
		Command.Type commandType = commandObject.getCommandType();
		ArrayList<Task> userTasks = commandObject.getTasks();
		ArrayList<String> argumentList = commandObject.getArguments();
		ArrayList<Integer> indexList = new ArrayList<Integer>();
		if (commandType == null) {
			logger.warning("Command type is null!");
			return ERROR_NO_COMMAND_HANDLER;
		} else {
			switch (commandType) {
				case ADD :
					argumentList = removeDuplicates(argumentList);
					indexList = parseIntList(argumentList);
					logger.info("ADD command detected");
					return addItem(userTasks, indexList, isUserInput,
							isUndoHistory);
				case DELETE :
					argumentList = processIndexArguments(argumentList);
					argumentList = removeDuplicates(argumentList);
					if (isUserInput) {
						try {
							indexList = remapArguments(argumentList);
						} catch (Exception e) {
							return e.getMessage();
						}
					} else {
						indexList = parseIntList(argumentList);
					}
					logger.info("DELETE command detected");
					return deleteItem(indexList, isUserInput, isUndoHistory);
				case EDIT :
					argumentList = processIndexArguments(argumentList);
					argumentList = removeDuplicates(argumentList);
					if (isUserInput) {
						try {
							indexList = remapArguments(argumentList);
						} catch (Exception e) {
							return e.getMessage();
						}
					} else {
						indexList = parseIntList(argumentList);
					}
					logger.info("EDIT command detected");
					return editItem(userTasks, indexList, isUserInput,
							isUndoHistory);
				case DISPLAY :
					logger.info("DISPLAY command detected");
					return displayItems(argumentList);
				case UNDO :
					logger.info("UNDO command detected");
					return undoCommand();
				case REDO :
					logger.info("REDO command detected");
					return redoCommand();
				case SAVETO :
					logger.info("SAVETO command detected");
					return saveFilePath(argumentList);
				case EXIT :
					logger.info("EXIT command detected");
					return exitProgram();
				case MARK:
					logger.info("MARK command detected");
					argumentList = processIndexArguments(argumentList);
					argumentList = removeDuplicates(argumentList);
					if (isUserInput) {
						try {
							indexList = remapArguments(argumentList);
						} catch (Exception e) {
							return e.getMessage();
						}
					} else {
						indexList = parseIntList(argumentList);
					}
					return markDoneStatus(indexList, isUserInput, isUndoHistory, true);
				case UNMARK:
					logger.info("UNMARK command detected");
					argumentList = processIndexArguments(argumentList);
					argumentList = removeDuplicates(argumentList);
					if (isUserInput) {
						try {
							indexList = remapArguments(argumentList);
						} catch (Exception e) {
							return e.getMessage();
						}
					} else {
						indexList = parseIntList(argumentList);
					}
					return markDoneStatus(indexList, isUserInput, isUndoHistory, false);
				case SEARCH:
					logger.info("SEARCH command detected");
					return addSearchFilter(userTasks);
				case HELP:
					return MESSAGE_SUCCESS_HELP;
				default :
					logger.warning("Command type cannot be identified!");
					return ERROR_NO_COMMAND_HANDLER;
			}
		}
	}
	
	/**
	 * Converts a list of integer strings into list of integers
	 * 
	 * @param argumentList
	 * @return
	 */
	ArrayList<Integer> parseIntList(ArrayList<String> argumentList) {
		ArrayList<Integer> intList = new ArrayList<Integer>();
		for (int i = 0; i < argumentList.size(); i++) {
			intList.add(Integer.parseInt(argumentList.get(i)));
		}
		return intList;
	}
	
	/**
	 * Pushes a reversed command to history, and returns the
	 * respective status message
	 * @param commandType
	 * @param commandToPush
	 * @param isUserInput
	 * @param isUndoHistory
	 * @return
	 */
	String pushToHistory(Command.Type commandType, Command commandToPush, boolean isUserInput, boolean isUndoHistory) {
		String normalStatus;
		String undoStatus;
		String redoStatus;
		switch (commandType) {
			case ADD:
				normalStatus = MESSAGE_SUCCESS_ADD;
				undoStatus = MESSAGE_UNDO + MESSAGE_SUCCESS_HISTORY_ADD;
				redoStatus = MESSAGE_REDO + MESSAGE_SUCCESS_HISTORY_ADD;
				break;
			case DELETE:
				normalStatus = MESSAGE_SUCCESS_DELETE;
				undoStatus = MESSAGE_UNDO + MESSAGE_SUCCESS_HISTORY_DELETE;
				redoStatus = MESSAGE_REDO + MESSAGE_SUCCESS_HISTORY_DELETE;
				break;
			case EDIT:
				normalStatus = MESSAGE_SUCCESS_EDIT;
				undoStatus = MESSAGE_UNDO + MESSAGE_SUCCESS_HISTORY_EDIT;
				redoStatus = MESSAGE_REDO + MESSAGE_SUCCESS_HISTORY_EDIT;
				break;
			case MARK:
				normalStatus = MESSAGE_SUCCESS_MARK;
				undoStatus = MESSAGE_UNDO + MESSAGE_SUCCESS_MARK;
				redoStatus = MESSAGE_REDO + MESSAGE_SUCCESS_MARK;
				break;
			case UNMARK:
				normalStatus = MESSAGE_SUCCESS_UNMARK;
				undoStatus = MESSAGE_UNDO + MESSAGE_SUCCESS_UNMARK;
				redoStatus = MESSAGE_REDO + MESSAGE_SUCCESS_UNMARK;
				break;
			default:
				return ERROR_HISTORY_NO_COMMAND_HANDLER;
		}
		
		logger.fine("Checking if history should be cleared.");
		if (isUserInput) {
			historyObject.clearUndoHistoryList();
		}
		logger.finer("Checking if command is called by undo.");
		if (isUndoHistory) {
			logger.finer("Command is NOT called by undo.");

			logger.finer("Attempting to reverse command and push it to history.");
			if (!historyObject.pushCommand(commandToPush, true)) {
				return ERROR_CANNOT_WRITE_TO_HISTORY;
			}
			if (isUserInput) {
				return normalStatus;
			} else {
				return redoStatus;
			}
			
		} else {
			logger.finer("Command is called by undo.");

			logger.finer("Attempting to reverse command and push it to undoHistory.");
			if (!historyObject.pushCommand(commandToPush, false)) {
				return ERROR_CANNOT_WRITE_TO_HISTORY;
			}
			return undoStatus;
		}
	}

	/**
	 * Adds an item to the list of tasks in memory
	 * 
	 * @param userTasks
	 *            an arraylist of tasks to be added
	 * @param indexList
	 *            if empty, tasks will be added to the back of the list in the
	 *            order given in userTasks else should contain the same number
	 *            of elements as userTasks, to determine the positions the tasks
	 *            are to be inserted at
	 * @param isUserInput
	 * @param isUndoHistory
	 * 
	 * @return status string
	 */
	String addItem(ArrayList<Task> userTasks, ArrayList<Integer> indexList,
			boolean isUserInput, boolean isUndoHistory) {
		try {
			ArrayList<Integer> parsedIntList = new ArrayList<Integer>();
			
			boolean hasClashes = false;
			logger.fine("Checking for clashes.");
			if (haveClashes(userTasks)) {
				logger.finer("Clash in timing detected.");
				hasClashes = true;
			}

			if (isEmptyIndexList(indexList)) {
				for (int i = 0; i < userTasks.size(); i++) {
					int index = i + listOfTasks.size();
					addHelper(userTasks, parsedIntList, i, index);
				}
				logger.finer("No specified index. Defaulting all items to the end of list.");
			} else if (indexList.size() != userTasks.size()) {
				return ERROR_INVALID_ARGUMENT;
			} else {
				logger.fine("Adding tasks to list.");
				for (int i = 0; i < userTasks.size(); i++) {
					int index = indexList.get(i);
					addHelper(userTasks, parsedIntList, i, index);
					logger.finer("Index " + (index + 1) + " specified.");
				}
			}
			

			String[] argumentListForReverse = new String[parsedIntList.size()];
			Integer[] integerArr = new Integer[parsedIntList.size()];
			parsedIntList.toArray(integerArr);
			for (int i = 0; i < parsedIntList.size(); i++) {
				argumentListForReverse[i] = String.valueOf(integerArr[i]);
			}

			String historyStatus = pushToHistory(Command.Type.ADD, new Command(Command.Type.DELETE, argumentListForReverse), isUserInput, isUndoHistory);
			if (hasClashes) {
				return WARNING_TIMING_CLASH;
			}
			return historyStatus;
		} catch (NumberFormatException e) {
			return ERROR_INVALID_ARGUMENT;
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	/**
	 * Helper for the addItem method
	 * 
	 * Mainly used to extract a given user task from the parser,
	 * then attempts to split the task if it is recurring. At the
	 * same time, it helps to keep track of the index the items are
	 * added at so that the reversed command can be created
	 * @param userTasks
	 * @param parsedIntList
	 * @param i
	 * @param index
	 * @throws Exception
	 */
	private void addHelper(ArrayList<Task> userTasks,
			ArrayList<Integer> parsedIntList, int i, int index)
			throws Exception {
		Task curTask = userTasks.get(i);
		int newIdPrefix = getNewIdPrefix();
		if (curTask.hasPeriodicInterval()) {
			ArrayList<Task> splitTasks = splitPeriodic(curTask);
			for (int j = 0; j < splitTasks.size(); j++) {
				splitTasks.get(j).setId(newIdPrefix * RECURRING_MAX + j);
			}
			listOfTasks.addAll(index, splitTasks);
			for (int j = 0; j < splitTasks.size(); j++) {
				parsedIntList.add(index + j);
			}
		} else {
			curTask.setId(newIdPrefix * RECURRING_MAX);
			listOfTasks.add(index, curTask);
			parsedIntList.add(index);
		}
	}

	/**
	 * Deletes item from the list of tasks in memory
	 * This method will also push a reversed version of the command to history
	 * 
	 * @param argumentList
	 *            all elements in this array should be integer strings elements
	 *            the array will first have its duplicates removed, then sorted
	 *            in an increasing order
	 * @param isUserInput
	 * @param isUndoHistory
	 * 
	 * @return status string
	 */
	String deleteItem(ArrayList<Integer> indexList,
			boolean isUserInput, boolean isUndoHistory) {
		String[] argumentListForReverse;
		if (isEmptyIndexList(indexList)) {
			return ERROR_INVALID_ARGUMENT;
		}
		Collections.sort(indexList);

		argumentListForReverse = new String[indexList.size()];

		ArrayList<Task> tasksRemoved = new ArrayList<Task>();
		for (int i = indexList.size() - 1; i >= 0; i--) {
			int index = indexList.get(i);
			if (isValidIndex(index)) {
				argumentListForReverse[i] = Integer.toString(indexList.get(i)); // for undo

				// add to start of list to maintain order
				tasksRemoved.add(0, listOfTasks.remove(index));
				logger.fine("Task removed from list.");
			} else {
				int offset = 1;
				while (tasksRemoved.size() != 0) {
					listOfTasks.add(indexList.get(i + offset),
							tasksRemoved.remove(0));
					offset++;
				}

				return ERROR_INVALID_INDEX;
			}

		}

		Command commandToPush = new Command(Command.Type.ADD, argumentListForReverse, tasksRemoved);
		String historyStatus = pushToHistory(Command.Type.DELETE, commandToPush, isUserInput, isUndoHistory);
		return historyStatus;
	}

	/**
	 * Remaps index based on UI list to the index based on the list in memory,
	 * and at the same time, converting it to integer type list
	 * @param argumentList
	 * @return
	 * @throws Exception
	 */
	private ArrayList<Integer> remapArguments(
			ArrayList<String> argumentList) throws Exception {
		ArrayList<Integer> remappedArgumentList = new ArrayList<Integer>();
		for (String oldIndexString : argumentList) {
			int oldIndex = Integer.parseInt(oldIndexString) - 1;
			if (oldIndex < listOfShownTasks.size() && oldIndex >= 0) {
				Task task = listOfShownTasks.get(oldIndex);
				int newIndex = listOfTasks.indexOf(task);
				remappedArgumentList.add(newIndex);
			} else {
				throw new Exception(ERROR_INVALID_INDEX);
			}
		}
		return remappedArgumentList;
	}
	
	String addSearchFilter(ArrayList<Task> userTasks) {
		if (userTasks.size() == 0) {
			return ERROR_NO_FILTER;
		} else {
			Task taskObject = userTasks.get(0);
			listFilter.add(taskObject);
			return MESSAGE_SUCCESS_SEARCH;
		}
	}
	
	/**
	 * Marks or unmarks a task as done based on the isDone parameter
	 * @param indexList
	 * @param isUserInput
	 * @param isUndoHistory
	 * @param isDone
	 * @return
	 */
	String markDoneStatus(ArrayList<Integer> indexList,
			boolean isUserInput, boolean isUndoHistory, boolean isDone) {
		String[] argumentListForReverse;
		if (isEmptyIndexList(indexList)) {
			return ERROR_INVALID_ARGUMENT;
		}
		
		Collections.sort(indexList);
		argumentListForReverse = new String[indexList.size()];

		for (int i = indexList.size() - 1; i >= 0; i--) {
			int index = indexList.get(i);
			if (!isValidIndex(index)) {
				return ERROR_INVALID_INDEX;
			}
		}
		
		ArrayList<Task> tasksRemoved = new ArrayList<Task>();
		for (int i = indexList.size() - 1; i >= 0; i--) {
			int index = indexList.get(i);
			argumentListForReverse[i] = String.valueOf(index); // for undo

			// add to start of list to maintain order
			Task taskRemoved = listOfTasks.remove(index);
			tasksRemoved.add(0, taskRemoved);
			Task cloneOfTask = taskRemoved.clone();
			cloneOfTask.setDone(isDone);
			listOfTasks.add(index, cloneOfTask);
			logger.fine("Task marked/unmarked.");
		}
		
		Command.Type commandType;
		Command.Type reversedCommandType;
		if (isDone) {
			commandType = Command.Type.MARK;
			reversedCommandType = Command.Type.UNMARK;
		} else {
			commandType = Command.Type.UNMARK;
			reversedCommandType = Command.Type.MARK;
		}
		Command commandToPush = new Command(reversedCommandType, argumentListForReverse);
		String historyStatus = pushToHistory(commandType, commandToPush, isUserInput, isUndoHistory);
		return historyStatus;
	}

	/**
	 * Identifies special keywords in the argumentlist, and returns a
	 * usable arraylist of index strings
	 * @param argumentList
	 * @return
	 * @throws NumberFormatException
	 * @throws IndexOutOfBoundsException
	 */
	ArrayList<String> processIndexArguments(ArrayList<String> argumentList)
			throws NumberFormatException, IndexOutOfBoundsException {
		ArrayList<String> finalArgumentList = new ArrayList<>();
		if (argumentList == null) {
			System.out.println("Null...");
			return null;
		}
		if (argumentList.size() == 1 && argumentList.get(0).equalsIgnoreCase(IDENTIFIER_ALL)) {
			argumentList.clear();
			for (int i = 0; i < listOfShownTasks.size(); i++) {
				finalArgumentList.add(String.valueOf(i + 1));
			}
		} else {
			for (String argument : argumentList) {
				if (argument.indexOf('-', 1) != -1) {
					int dashPosition = argument.indexOf('-', 1);
					String leftPart = argument.substring(0, dashPosition);
					String rightPart = argument.substring(dashPosition + 1, argument.length());

					int fromInclusive = Integer.parseInt(leftPart);
					int toInclusive = Integer.parseInt(rightPart);
					for (int index = fromInclusive; index <= toInclusive; index++) {
						finalArgumentList.add(String.valueOf(index));
					}
				} else {
					//unchecked
					finalArgumentList.add(argument);
				}
			}
		}
		return finalArgumentList;
	}
	
	String editFields(Task newTask, Task clonedTask) {
		if (newTask.hasName()) {
			clonedTask.setName(newTask.getName());
		}
		if (newTask.hasLocation()) {
			clonedTask.setLocation(newTask.getLocation());
		}
		if (newTask.hasStartingTime() && newTask.hasEndingTime()) {
			clonedTask.setStartingTime(newTask.getStartingTime());
			clonedTask.setEndingTime(newTask.getEndingTime());
		}
		if (newTask.hasStartingTime() && !newTask.hasEndingTime()) {
			Calendar newStartingTime = newTask.getStartingTime();
			if (!clonedTask.hasEndingTime()) {
				return ERROR_START_TIME_WITHOUT_END_TIME;
			}
			Calendar endingTime = clonedTask.getEndingTime();
			if (!newStartingTime.after(endingTime)) {
				clonedTask.setStartingTime(newTask.getStartingTime());
			} else {
				return ERROR_START_TIME_BEFORE_END_TIME;
			}
		}
		if (newTask.hasEndingTime() && !newTask.hasStartingTime()) {
			Calendar newEndingTime = newTask.getEndingTime();
			Calendar startingTime = clonedTask.getStartingTime();
			if (!newEndingTime.before(startingTime)) {
				clonedTask.setEndingTime(newTask.getEndingTime());
			} else {
				return ERROR_START_TIME_BEFORE_END_TIME;
			}
		}
		if (newTask.hasPeriodicInterval() || newTask.hasPeriodicRepeats()) {
			return ERROR_EDIT_CANNOT_RECURRING;
		}
		return null;
	}
	
	/**
	 * Updates the non-null fields in the given task object to the task in memory list
	 * at the given index
	 * 
	 * @param userTasks
	 *            this should be of size 1 which contains the new task information
	 *            of the relevant fields
	 * @param indexList
	 *            a number string, which contains the index position of the task
	 *            to edit
	 * @param isUserInput
	 * @param isUndoHistory
	 * 
	 * @return status string
	 */
	String editItem(ArrayList<Task> userTasks, ArrayList<Integer> indexList,
			boolean isUserInput, boolean isUndoHistory) {
		if (isEmptyIndexList(indexList)) {
			return ERROR_INVALID_ARGUMENT;
		}
		Task userTask = userTasks.get(0); // should only have 1 item
		try {
			logger.fine("Attempting to determine index.");
			int index = indexList.get(0);
			
			boolean hasClashes = false;
			if (isValidIndex(index)) {
				// for history
				Task taskEdited = listOfTasks.get(index);
				

				if (isUserInput) {
					Task newTask = taskEdited.clone();
					String statusOfSpecialEdit = editFields(userTask,
							newTask);
					if (statusOfSpecialEdit != null) {
						return statusOfSpecialEdit;
					}
					listOfTasks.remove(index);
					logger.fine("Old task removed from list.");
					
					if (hasClashes(newTask)) {
						hasClashes = true;
					}
					listOfTasks.add(index, newTask);
					logger.fine("New task added to list.");
				} else {
					listOfTasks.remove(index);
					logger.fine("Old task removed from list.");

					if (hasClashes(userTask)) {
						hasClashes = true;
					}
					listOfTasks.add(index, userTask);
					logger.fine("New task added to list.");
				}
				
				String[] indexString = { Integer.toString(index) };
				String historyStatus = pushToHistory(Command.Type.EDIT, new Command(Command.Type.EDIT,
						indexString, taskEdited), isUserInput, isUndoHistory);
				if (hasClashes) {
					return WARNING_TIMING_CLASH;
				}
				return historyStatus;
			} else {
				return ERROR_INVALID_INDEX;
			}
		} catch (NumberFormatException e) {
			return ERROR_INVALID_ARGUMENT;
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	String displayItems(ArrayList<String> argumentList) {
		if (listOfTasks.isEmpty()) {
			return MESSAGE_DISPLAY_EMPTY;
		}
		if (argumentList.size() == 1 && argumentList.get(0).equals("all")) {
			shouldShowUndone = true;
			shouldShowDone = true;
			listFilter.clear();
			listFilter.add(new Task());
		} else if (argumentList.size() == 1
				&& argumentList.get(0).equals("done")) {
			shouldShowUndone = false;
			shouldShowDone = true;
			listFilter.clear();
			listFilter.add(new Task());
		} else if (argumentList.size() == 1 && argumentList.get(0).equals("undone")) {
			shouldShowDone = false;
			shouldShowUndone = true;
			listFilter.clear();
			listFilter.add(new Task());
		} else {
			shouldShowUndone = true;
			shouldShowDone = true;
			listFilter.clear();
		}
		return MESSAGE_SUCCESS_DISPLAY;
	}

	/**
	 * Sorts a list of tasks by time
	 * The starting time for a task is used if it exists
	 * Else the ending time for a task is used instead
	 * @return
	 */
	boolean sortListOfTasks() {
		Collections.sort(listOfTasks);
		return true;
	}
	
	boolean isTimingInDay(Calendar time, Calendar date) {
		return time.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
				time.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR);
	}
	
	ArrayList<Task> getTasksInDay(ArrayList<Task> listOfEventsDeadlines,
			Calendar date) {
		ArrayList<Task> listOfTasksInDay = new ArrayList<Task>();
		for (int i = 0; i < listOfEventsDeadlines.size(); i++) {
			Task curTask = listOfEventsDeadlines.get(i);
			Calendar itemTime = curTask.getTime();
			if (isTimingInDay(itemTime, date)) {
				listOfTasksInDay.add(curTask);
			}
		}

		return listOfTasksInDay;
	}
	
	
	
	/*
	 * show help message to UI
	 * 
	 */
	boolean showHelpMsg() {
		UIObject.showToUser(storageObject.getHelp());
		return true;
	}
	
	/**
	 * This method filters the list of tasks to be shown to the user,
	 * based on the current list of filter keywords
	 * 
	 * It will attempt to show the 3 most urgent tasks in each category of
	 * floating/deadline/event by default if there are no filter keywords
	 * 
	 * @return calls the UI to display updated list of items
	 */
	boolean showUpdatedItems() {
		listOfShownTasks.clear();
		if (listFilter.size() == 0) {
			ArrayList<Task> listOfFloating = new ArrayList<Task>();
			ArrayList<Task> listOfEventsDeadlines = new ArrayList<Task>();
			for (int i = 0; i < listOfTasks.size(); i++) {
				Task curTask = listOfTasks.get(i);
				if (!curTask.isDone()) {
					if (curTask.hasEndingTime()) {
						listOfEventsDeadlines.add(curTask);
					} else {
						listOfFloating.add(curTask);
					}
				}
			}
			Collections.sort(listOfEventsDeadlines);
			
			ArrayList<Task> listOfFirstDate = new ArrayList<Task>();
			ArrayList<Task> listOfSecondDate = new ArrayList<Task>();
			if (listOfEventsDeadlines.size() != 0) {
				Task firstTask = listOfEventsDeadlines.get(0);
				Calendar firstDate, secondDate = null;
				firstDate = firstTask.getTime();
				listOfFirstDate = getTasksInDay(listOfEventsDeadlines,
						firstDate);

				int i = 1;
				while (i < listOfEventsDeadlines.size() && secondDate == null) {
					Task curTask = listOfEventsDeadlines.get(i);
					Calendar curDate = curTask.getTime();
					if (!isTimingInDay(curDate, firstDate)) {
							secondDate = curDate;
					}
					i++;
				}
				if (secondDate != null) {
					listOfSecondDate = getTasksInDay(listOfEventsDeadlines,
							secondDate);
				}
			}
			addTasksToList(listOfFirstDate);
			addTasksToList(listOfSecondDate);
			addTasksToList(listOfFloating);
			
			// default view
			List<String> listOfTitles = new ArrayList<String>();
			addTitleForDate(listOfFirstDate, listOfTitles);
			addTitleForDate(listOfSecondDate, listOfTitles);
			
			if (listOfFloating.size() != 0) {
				listOfTitles.add("Other tasks");
			} else {
				listOfTitles.add("No other tasks.");
			}
			
			return UIObject.showTasks(listOfShownTasks, DisplayType.DEFAULT, listOfTitles);
		} else {
			listOfShownTasks = new ArrayList<Task>();
			List<String> searchStrings = new ArrayList<String>();
			
			for (int i = 0; i < listOfTasks.size(); i++) {
				Task curTask = listOfTasks.get(i);
				if (curTask.isDone() && shouldShowDone) {
					listOfShownTasks.add(curTask);
				}
				if (!curTask.isDone() && shouldShowUndone) {
					listOfShownTasks.add(curTask);
				}
			}
			
			searchStrings.add(FILTER_TITLE_TASK_NAME);
			searchStrings.add(FILTER_TITLE_TIME);
			searchStrings.add(FILTER_TITLE_LOCATION);
			
			boolean isEmptyName = true;
			boolean isEmptyTime = true;
			boolean isEmptyLocation = true;
			
			// Filter by name
			for (int j = 0; j < listFilter.size(); j++) {
				Task curFilter = listFilter.get(j);
				String searchTaskName = curFilter.getName();
				int i = 0;
				if (searchTaskName != null) {
					if (!isEmptyName) {
						searchStrings.set(0,
								searchStrings.get(0).concat(FILTER_SEARCH_SEPARATOR));
					}
					isEmptyName = false;
					searchStrings.set(0, searchStrings.get(0).concat(searchTaskName));
					while (i < listOfShownTasks.size()) {
						Task curTask = listOfShownTasks.get(i);
						if (!curTask.getName().toLowerCase().contains(searchTaskName)) {
							listOfShownTasks.remove(i);
						} else {
							i++;
						}
					}
				}
			}
			
			// Filter by time
			for (int j = 0; j < listFilter.size(); j++) {
				Task curFilter = listFilter.get(j);
				Calendar filterTime = curFilter.getTime();
				if (filterTime != null) {
					Calendar filterTimeStart = (Calendar) filterTime.clone();
					filterTimeStart.set(Calendar.HOUR_OF_DAY, 0);
					filterTimeStart.set(Calendar.MINUTE, 0);
					Calendar filterTimeEnd = (Calendar) filterTime.clone();
					filterTimeEnd.set(Calendar.HOUR_OF_DAY, 23);
					filterTimeEnd.set(Calendar.MINUTE, 59);
					if (!isEmptyTime) {
						searchStrings.set(
								1,
								searchStrings.get(1).concat(
										FILTER_SEARCH_SEPARATOR));
					}
					isEmptyTime = false;
					searchStrings.set(
							1,
							searchStrings.get(1).concat(
									dateFormat.format(filterTime.getTime())));
					int i = 0;
					while (i < listOfShownTasks.size()) {
						Task curTask = listOfShownTasks.get(i);
						if (curTask.getTime() == null
								|| curTask.getTime().before(filterTimeStart)
								|| curTask.getTime().after(filterTimeEnd)) {
							listOfShownTasks.remove(i);
						} else {
							i++;
						}
					}
				}
				
			}
			
			// Filter by location
			for (int j = 0; j < listFilter.size(); j++) {
				Task curFilter = listFilter.get(j);
				String searchLocation = curFilter.getLocation();
				int i = 0;
				if (searchLocation != null) {
					if (!isEmptyLocation) {
						searchStrings.set(2,
								searchStrings.get(2).concat(FILTER_SEARCH_SEPARATOR));
					}
					isEmptyLocation = false;
					searchStrings.set(2, searchStrings.get(2).concat(searchLocation));
					while (i < listOfShownTasks.size()) {
						Task curTask = listOfShownTasks.get(i);
						if (curTask.getLocation() == null || !curTask.getLocation().toLowerCase().contains(searchLocation)) {
							listOfShownTasks.remove(i);
						} else {
							i++;
						}
					}
				}
			}
			
			return UIObject.showTasks(listOfShownTasks, DisplayType.FILTERED, searchStrings);
		}
	}

	private void addTitleForDate(ArrayList<Task> listOfItemsInDate,
			List<String> listOfTitles) {
		if (listOfItemsInDate.size() != 0) {
			Calendar curTime = Calendar.getInstance();
			int curDate = curTime.get(Calendar.DATE);
			int curMonth = curTime.get(Calendar.MONTH) + 1;
			Task curItem = listOfItemsInDate.get(0);
			
			Calendar curItemTime = curItem.getTime();
			int curItemDate = curItemTime.get(Calendar.DATE);
			int curItemMonth = curItemTime.get(Calendar.MONTH) + 1;
			addTitleForDateHelper(listOfTitles, curDate, curMonth,
					curItemDate, curItemMonth);
		} else {
			listOfTitles.add("No upcoming tasks.");
		}
	}

	private void addTitleForDateHelper(List<String> listOfTitles, int curDate,
			int curMonth, int curItemDate, int curItemMonth) {
		Calendar itemDate = new GregorianCalendar();
		itemDate.set(Calendar.DATE, curItemDate);
		itemDate.set(Calendar.MONTH, curItemMonth - 1);
		String itemDateString = dateFormat.format(itemDate.getTime());
		String titleTop = String.format(TITLE_TOP_DISPLAY, displaySize);
		if (curMonth == curItemMonth) {
			if (curDate == curItemDate) {
				listOfTitles.add(titleTop + TITLE_TODAY);
			} else if (curDate == curItemDate - 1) {
				listOfTitles.add(titleTop + TITLE_TOMORROW);
			} else {
				listOfTitles.add(titleTop + itemDateString);
			}
		} else {
			listOfTitles.add(titleTop + itemDateString);
		}
	}

	private void addTasksToList(ArrayList<Task> listOfFirstDate) {
		if (listOfFirstDate.size() >= displaySize) {
			listOfShownTasks.addAll(listOfFirstDate.subList(0, displaySize));
		} else {
			listOfShownTasks.addAll(listOfFirstDate);
		}
	}

	/**
	 * Retrieves the last command from history and attempts to execute it.
	 * this will push the reversed version of the command to the redo history 
	 * 
	 * @return status message
	 */
	String undoCommand() {
		Command previousCommand = historyObject.getPreviousCommand(true);
		if (previousCommand == null) {
			return ERROR_NO_HISTORY;
		}
		return executeCommand(previousCommand, false, false);
	}

	/**
	 * Retrieves the last command from undo history and attempts to execute it.
	 * this will no longer push it to history, therefore, you can't undo a redo
	 * 
	 * @return status message
	 */
	String redoCommand() {
		Command previousCommand = historyObject.getPreviousCommand(false);
		if (previousCommand == null) {
			return ERROR_NO_HISTORY;
		}
		return executeCommand(previousCommand, false, true);
	}

	/**
	 * Sets the data file path
	 * 
	 * @param argumentList
	 *            the file path string is read from position 0
	 * @return status string
	 * @throws Exception 
	 */
	String saveFilePath(ArrayList<String> argumentList) {
		if (isEmptyArgumentList(argumentList)) {
			return ERROR_INVALID_ARGUMENT;
		}
		String filePath = argumentList.get(0);
		try {
			boolean locationChanged = storageObject.saveFileToPath(filePath);
			if (locationChanged) {
				updateProperties(PROPERTY_KEY_SAVE_FILE, filePath);
			}
			updateListOfTasks();
			if (locationChanged) {
				return MESSAGE_SUCCESS_CHANGE_FILE_PATH;
			} else {
				return MESSAGE_SUCCESS_NO_CHANGE_FILE_PATH;
			}
		} catch (IOException e) {
			return ERROR_CREATING_FILE;
		} catch (Exception e) {
			return e.getMessage();
		}
	}
	
	boolean isValidIndex(int index) {
		return index >= 0 && index < listOfTasks.size();
	}

	boolean isEmptyArgumentList(ArrayList<String> argumentList) {
		if (argumentList == null || argumentList.isEmpty()) {
			return true;
		}
		return false;
	}
	
	boolean isEmptyIndexList(ArrayList<Integer> indexList) {
		if (indexList == null || indexList.isEmpty()) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if a list of tasks has timing clashes with other
	 * tasks in memory
	 * 
	 * @param task
	 * @return whether there is a clash or not
	 */
	boolean haveClashes(ArrayList<Task> tasks) {
		for (int i = 0; i < tasks.size(); i++) {
			if (hasClashes(tasks.get(i))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if a given task has timing clashes with other
	 * tasks in memory
	 * 
	 * @param task
	 * @return whether there is a clash or not
	 */
	boolean hasClashes(Task task) {
		if (task.getStartingTime() != null && task.getEndingTime() != null) {
			for (int i = 0; i < listOfTasks.size(); i++) {
				Task curTaskToCheck = listOfTasks.get(i);
				if (curTaskToCheck.getStartingTime() != null
						&& curTaskToCheck.getEndingTime() != null
						&& isClashing(task, curTaskToCheck)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks if 2 tasks are clashing
	 * Note that this method expects that both tasks have
	 * starting & ending times, if not the program will
	 * stop execution.
	 * 
	 * @param taskOne
	 * @param taskTwo
	 * @return whether there is a clash or not
	 */
	boolean isClashing(Task taskOne, Task taskTwo) {
		Calendar taskOneStart = taskOne.getStartingTime();
		Calendar taskOneEnd = taskOne.getEndingTime();
		Calendar taskTwoStart = taskTwo.getStartingTime();
		Calendar taskTwoEnd = taskTwo.getEndingTime();
		assert (!(taskOneStart == null));
		assert (!(taskOneEnd == null));
		assert (!(taskTwoStart == null));
		assert (!(taskTwoEnd == null));

		if ((taskOneStart.before(taskTwoStart) && taskOneEnd
				.before(taskTwoStart))
				|| (taskTwoStart.before(taskOneStart) && taskTwoEnd
						.before(taskOneStart))) {
			return false;
		} else {
			return true;
		}
	}

	// Create an array with all unique elements
	ArrayList<String> removeDuplicates(ArrayList<String> parsedIntArgumentList) {
		if (parsedIntArgumentList == null) {
			return new ArrayList<String>();
		}
		HashSet<String> hs = new HashSet<>();
		hs.addAll(parsedIntArgumentList);
		parsedIntArgumentList.clear();
		parsedIntArgumentList.addAll(hs);
		return parsedIntArgumentList;
	}
	
	/**
	 * Reads the task list from the data file
	 * @return
	 * @throws Exception
	 */
	boolean updateListOfTasks() throws Exception {
		try {
			listOfTasks = storageObject.getItemList();
		} catch (FileNotFoundException e) {
			throw new Exception(ERROR_FILE_NOT_FOUND);
		}
		return true;
	}
	
	/**
	 * Creates a list of tasks with timings offset from the given
	 * recurring task, then returns it
	 * 
	 * @param recurringTask
	 * @return
	 * @throws Exception
	 */
	ArrayList<Task> splitPeriodic(Task recurringTask) throws Exception {
		if (!recurringTask.hasPeriodicInterval() || !recurringTask.hasPeriodicRepeats()) {
			return null; // no periodic to split
		} else {
			String noOfRepeatsString = recurringTask.getPeriodicRepeats();
			int noOfRepeats = Integer.parseInt(noOfRepeatsString);
			String periodicIntervalString = recurringTask.getPeriodicInterval();
			recurringTask.setPeriodicRepeats(null);
			recurringTask.setPeriodicInterval(null);

			ArrayList<Task> listOfRecurringTasks = new ArrayList<Task>();
			for (int i = 0; i < noOfRepeats; i++) {
				Task newTask = recurringTask.clone();
				listOfRecurringTasks.add(newTask);
				addInterval(recurringTask, periodicIntervalString);
			}
			return listOfRecurringTasks;
		}
	}
	
	/**
	 * Pushes back the starting & ending time of a given task by a specified interval
	 * @param curTask a task to be modified
	 * @param periodicIntervalString a time interval in the form <integer> <days/weeks/months>
	 * 
	 */
	boolean addInterval(Task curTask, String periodicIntervalString)
			throws Exception {
		System.out.println(periodicIntervalString);
		String[] periodicIntervalWords = periodicIntervalString.split(
				WHITE_SPACE_REGEX, 2);
		String periodicIntervalUnit = periodicIntervalWords[1];
		int periodicInterval;
		try {
			periodicInterval = Integer
					.parseInt(periodicIntervalWords[0]);
		} catch (NumberFormatException e) {
			throw new Exception(ERROR_CANNOT_PARSE_PERIODIC_VALUES);
		}
		
		int calendarUnit;
		System.out.println(periodicIntervalUnit);
		if (periodicIntervalUnit.equalsIgnoreCase("days") || periodicIntervalUnit.equalsIgnoreCase("day")) {
			calendarUnit = Calendar.DATE;
		} else if (periodicIntervalUnit.equalsIgnoreCase("weeks") || periodicIntervalUnit.equalsIgnoreCase("week")) {
			calendarUnit = Calendar.WEEK_OF_YEAR;
		} else if (periodicIntervalUnit.equalsIgnoreCase("months") || periodicIntervalUnit.equalsIgnoreCase("month")){
			calendarUnit = Calendar.MONTH;
		} else {
			calendarUnit = Calendar.YEAR;
		}
		
		if (curTask.hasStartingTime()) {
			curTask.getStartingTime().add(calendarUnit, periodicInterval);
		}
		if (curTask.hasEndingTime()) {
			curTask.getEndingTime().add(calendarUnit, periodicInterval);
		}
		
		return true;
	}
	
	int getNewIdPrefix() {
		int[] idList = new int[listOfTasks.size()];

		for (int i = 0; i < listOfTasks.size(); i++) {
			Task curTask = listOfTasks.get(i);
			idList[i] = curTask.getId();
		}
		for (int i = 0; i < ID_RANGE; i++) {
			boolean isUsed = false;
			// check if id is used
			for (int j = 0; j < listOfTasks.size(); j++) {
				Task curTask = listOfTasks.get(j);
				int curId = curTask.getId();
				if (curId / RECURRING_MAX == i) {
					isUsed = true;
				}
			}
			if (!isUsed) {
				return i;
			}
		}
		return -1; // all ids are used up!
	}
	
	boolean updateProperties(String key, String value) throws IOException{
		propObject.setProperty(key, value);
		writeProperties();
		return true;
	}
	
	boolean writeProperties() throws IOException{
		BufferedWriter bufWriter = new BufferedWriter(new FileWriter(new File(CONFIG_FILE_NAME)));
		propObject.store(bufWriter, null);
		bufWriter.close();
		return true;
	}

	String exitProgram() {
		System.exit(0);
		return MESSAGE_SUCCESS_EXIT;
	}
}