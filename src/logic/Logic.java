package logic;

import global.Command;
import global.Task;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LogManager;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

import parser.Parser;
import storage.Storage;
import storage.JsonFormatStorage;
import ui.UI;

/**
 * This file contains the main program of the command-line calendar, Tasky.
 * Please read our user guide at README.md if there are any questions.
 * 
 * @author cs2103aug2015-w10-4j
 *
 */
public class Logic {
	/*
	 * Declaration of object variables
	 */
	Logger logger = Logger.getGlobal();
	UI UIObject;
	Parser parserObject;
	Storage storageObject;
	History historyObject;
	ArrayList<Task> listOfTasks = new ArrayList<Task>();
	ArrayList<Task> listOfShownTasks = new ArrayList<Task>();
	ArrayList<Task> listFilter = new ArrayList<Task>();

	// date format converter
	static SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");

	/*
	 * Static strings - errors and messages
	 */
	private static final String MESSAGE_WELCOME = "Welcome to Tasky! This is an open source project";
	private static final String MESSAGE_PROMPT_COMMAND = "command :";
	private static final String MESSAGE_UNDO = "Undo : ";
	private static final String MESSAGE_REDO = "Redo : ";
	private static final String MESSAGE_SUCCESS_HISTORY_ADD = "Deleted item(s) restored.";
	private static final String MESSAGE_SUCCESS_HISTORY_DELETE = "Added item(s) removed.";
	private static final String MESSAGE_SUCCESS_HISTORY_MARK = "Item(s) successfully marked.";
	private static final String MESSAGE_SUCCESS_HISTORY_UNMARK = "Item(s) successfully unmarked.";
	private static final String MESSAGE_SUCCESS_HISTORY_EDIT = "Reverted edits.";
	private static final String MESSAGE_SUCCESS_ADD = "Item(s) %s successfully added.";
	private static final String MESSAGE_SUCCESS_DELETE = "Item(s) %s successfully deleted.";
	private static final String MESSAGE_SUCCESS_MARK = "Item(s) %s successfully marked.";
	private static final String MESSAGE_SUCCESS_UNMARK = "Item(s) %s successfully unmarked.";
	private static final String MESSAGE_SUCCESS_SEARCH = "Search results.";// for '%s'";
	private static final String MESSAGE_SUCCESS_EDIT = "Item(s) %s successfully edited.";
	private static final String MESSAGE_SUCCESS_EXIT = "Exiting program...";
	private static final String MESSAGE_SUCCESS_DISPLAY = "Displaying items.";
	private static final String MESSAGE_SUCCESS_CHANGE_FILE_PATH = "File path successfully changed.";
	private static final String MESSAGE_SUCCESS_NO_CHANGE_FILE_PATH = "File path not changed. Entered file path is the same as current one used.";
	private static final String MESSAGE_DISPLAY_EMPTY = "No items to display.";
	private static final String SEPARATOR_ITEM_LIST = ", ";
	private static final String IDENTIFIER_DELETE_ALL = "all";
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
	

	private static final String WARNING_TIMING_CLASH = "Warning: There are clashing timings between tasks.";
	
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
		try {
			FileHandler logHandler = new FileHandler("tasky.log");
			LogManager.getLogManager().reset(); // removes printout to console
												// aka root handler
			logHandler.setFormatter(new SimpleFormatter()); // set output to a
															// human-readable
															// log format
			logger.addHandler(logHandler);
			logger.setLevel(Level.FINER); // setting of log level
			
			updateListOfTasks();
		} catch (FileNotFoundException e) {
			UIObject.showToUser(ERROR_FILE_NOT_FOUND);
		} catch (SecurityException | IOException e) {
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
				showUpdatedItems();
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
	 * @param shouldClearHistory
	 *            false if command is called from redo
	 * @param isUndoHistory
	 *            true if command is called from undo false if command is called
	 *            by user directly
	 * 
	 * @return status string to be shown to user
	 */
	String executeCommand(Command commandObject, boolean shouldClearHistory,
			boolean isUndoHistory) {
		if (commandObject == null) {
			return ERROR_INVALID_COMMAND;
		}

		Command.Type commandType = commandObject.getCommandType();
		ArrayList<Task> userTasks = commandObject.getTasks();
		ArrayList<String> argumentList = commandObject.getArguments();
		if(commandType == null){
			logger.warning("Command type is null!");
			return ERROR_NO_COMMAND_HANDLER;
		} else {
			switch (commandType) {
				case ADD :
					logger.info("ADD command detected");
					return addItem(userTasks, argumentList, shouldClearHistory,
							isUndoHistory);
				case DELETE :
					logger.info("DELETE command detected");
					return deleteItem(argumentList, shouldClearHistory, isUndoHistory);
				case EDIT :
					logger.info("EDIT command detected");
					return editItem(userTasks, argumentList, shouldClearHistory,
							isUndoHistory);
				case DISPLAY :
					logger.info("DISPLAY command detected");
					return displayItems();
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
					return markDoneStatus(argumentList, shouldClearHistory, isUndoHistory, true);
				case UNMARK:
					logger.info("UNMARK command detected");
					return markDoneStatus(argumentList, shouldClearHistory, isUndoHistory, false);
				case SEARCH:
					logger.info("SEARCH command detected");
					return searchFilter(userTasks);
				default :
					logger.warning("Command type cannot be identified!");
					return ERROR_NO_COMMAND_HANDLER;
			}
		}
	}
	
	/**
	 * Pushes a reversed command to history, and returns the
	 * respective status message
	 * @param commandType
	 * @param commandToPush
	 * @param shouldClearHistory
	 * @param isUndoHistory
	 * @return
	 */
	String pushToHistory(Command.Type commandType, Command commandToPush, boolean shouldClearHistory, boolean isUndoHistory){
		String normalStatus;
		String undoStatus;
		switch(commandType){
			case ADD:
				normalStatus = MESSAGE_SUCCESS_ADD;
				undoStatus = MESSAGE_UNDO + MESSAGE_SUCCESS_HISTORY_ADD;
				break;
			case DELETE:
				normalStatus = MESSAGE_SUCCESS_DELETE;
				undoStatus = MESSAGE_UNDO + MESSAGE_SUCCESS_HISTORY_DELETE;
				break;
			case EDIT:
				normalStatus = MESSAGE_SUCCESS_EDIT;
				undoStatus = MESSAGE_UNDO + MESSAGE_SUCCESS_HISTORY_EDIT;
				break;
			case MARK:
				normalStatus = MESSAGE_SUCCESS_MARK;
				undoStatus = MESSAGE_UNDO + MESSAGE_SUCCESS_HISTORY_MARK;
				break;
			case UNMARK:
				normalStatus = MESSAGE_SUCCESS_UNMARK;
				undoStatus = MESSAGE_UNDO + MESSAGE_SUCCESS_HISTORY_UNMARK;
				break;
			default:
				return ERROR_HISTORY_NO_COMMAND_HANDLER;
		}
		
		logger.fine("Checking if history should be cleared.");
		if (shouldClearHistory) {
			historyObject.clearUndoHistoryList();
		}
		logger.finer("Checking if command is called by undo.");
		if (isUndoHistory) {
			logger.finer("Command is NOT called by undo.");

			logger.finer("Attempting to reverse command and push it to history.");
			if (!historyObject.pushCommand(commandToPush, true)) {
				return ERROR_CANNOT_WRITE_TO_HISTORY;
			}
			return normalStatus;
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
	 * @param argumentList
	 *            if empty, tasks will be added to the back of the list in the
	 *            order given in userTasks else should contain the same number
	 *            of elements as userTasks, to determine the positions the tasks
	 *            are to be inserted at
	 * @param shouldClearHistory
	 * @param isUndoHistory
	 * 
	 * @return status string
	 */
	String addItem(ArrayList<Task> userTasks, ArrayList<String> argumentList,
			boolean shouldClearHistory, boolean isUndoHistory) {
		try {
			logger.fine("Attempting to determine index.");
			ArrayList<Integer> parsedIntList = new ArrayList<Integer>(); // for
																			// status
			boolean hasClashes = false;

			logger.fine("Checking for clashes.");
			if (haveClashes(userTasks)) {
				logger.finer("Clash in timing detected.");
				hasClashes = true;
			}

			if (isEmptyArgumentList(argumentList)) {
				for (int i = 0; i < userTasks.size(); i++) {
					int index = i + listOfTasks.size();
					addHelper(userTasks, parsedIntList, i, index);
				}
				logger.finer("No specified index. Defaulting all items to the end of list.");
			} else if (argumentList.size() != userTasks.size()) {
				return ERROR_INVALID_ARGUMENT;
			} else {
				logger.fine("Adding tasks to list.");
				for (int i = 0; i < userTasks.size(); i++) {
					int index = Integer.parseInt(argumentList.get(i)) - 1;
					addHelper(userTasks, parsedIntList, i, index);
					logger.finer("Index " + (index + 1) + " specified.");
				}
			}
			

			String[] argumentListForReverse = new String[parsedIntList.size()];
			Integer[] integerArr = new Integer[parsedIntList.size()];
			parsedIntList.toArray(integerArr);
			for(int i = 0; i < parsedIntList.size(); i++){
				argumentListForReverse[i] = String.valueOf(integerArr[i]+1);
			}

			String historyStatus = pushToHistory(Command.Type.ADD, new Command(Command.Type.DELETE, argumentListForReverse), shouldClearHistory, isUndoHistory);
			historyStatus = statusItemFormatting(historyStatus, parsedIntList);
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
		if (curTask.hasPeriodicInterval()) {
			ArrayList<Task> splitTasks = splitPeriodic(curTask);
			listOfTasks.addAll(index, splitTasks);
			for(int j = 0; j < splitTasks.size(); j++){
				parsedIntList.add(index+j);
			}
		} else {
			listOfTasks.add(index, curTask);
		}

		parsedIntList.add(index);
	}

	/**
	 * Deletes item from the list of tasks in memory
	 * This method will also push a reversed version of the command to history
	 * 
	 * @param argumentList
	 *            all elements in this array should be integer strings elements
	 *            the array will first have its duplicates removed, then sorted
	 *            in an increasing order
	 * @param shouldClearHistory
	 * @param isUndoHistory
	 * 
	 * @return status string
	 */
	String deleteItem(ArrayList<String> argumentList,
			boolean shouldClearHistory, boolean isUndoHistory) {
		ArrayList<Integer> parsedIntArgumentList = new ArrayList<>();
		String[] argumentListForReverse;
		if (isEmptyArgumentList(argumentList)) {
			return ERROR_INVALID_ARGUMENT;
		}
		
		try {
			logger.fine("Cleaning up arguments.");
			
			argumentList = preprocessDeleteArgument(argumentList);
			argumentList = removeDuplicates(argumentList);
			
			for (String argument : argumentList) {
				parsedIntArgumentList.add(Integer.parseInt(argument) - 1);
			}
			
			Collections.sort(parsedIntArgumentList);
		} catch (NumberFormatException | IndexOutOfBoundsException e) {
			return ERROR_INVALID_ARGUMENT;
		}

		argumentListForReverse = new String[argumentList.size()];

		ArrayList<Task> tasksRemoved = new ArrayList<Task>();
		for (int i = parsedIntArgumentList.size() - 1; i >= 0; i--) {
			int index = parsedIntArgumentList.get(i);
			if (isValidIndex(index)) {
				argumentListForReverse[i] = argumentList.get(i); // for undo

				// add to start of list to maintain order
				tasksRemoved.add(0, listOfTasks.remove(index));
				logger.fine("Task removed from list.");
			} else {
				int offset = 1;
				while (tasksRemoved.size() != 0) {
					listOfTasks.add(parsedIntArgumentList.get(i + offset),
							tasksRemoved.remove(0));
					offset++;
				}

				return ERROR_INVALID_INDEX;
			}

		}

		Command commandToPush = new Command(Command.Type.ADD, argumentListForReverse, tasksRemoved);
		String historyStatus = pushToHistory(Command.Type.DELETE, commandToPush, shouldClearHistory, isUndoHistory);
		historyStatus = statusItemFormatting(historyStatus, parsedIntArgumentList);
		return historyStatus;

	}
	
	String searchFilter(ArrayList<Task> userTasks) {
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
	 * @param argumentList
	 * @param shouldClearHistory
	 * @param isUndoHistory
	 * @param isDone
	 * @return
	 */
	String markDoneStatus(ArrayList<String> argumentList,
			boolean shouldClearHistory, boolean isUndoHistory, boolean isDone) {
		ArrayList<Integer> parsedIntArgumentList = new ArrayList<>();
		String[] argumentListForReverse;
		if (isEmptyArgumentList(argumentList)) {
			return ERROR_INVALID_ARGUMENT;
		}
		
		try {
			logger.fine("Cleaning up arguments.");
			
			argumentList = preprocessDeleteArgument(argumentList);
			argumentList = removeDuplicates(argumentList);
			
			for (String argument : argumentList) {
				parsedIntArgumentList.add(Integer.parseInt(argument) - 1);
			}
			
			Collections.sort(parsedIntArgumentList);
		} catch (NumberFormatException | IndexOutOfBoundsException e) {
			return ERROR_INVALID_ARGUMENT;
		}

		argumentListForReverse = new String[argumentList.size()];

		for (int i = parsedIntArgumentList.size() - 1; i >= 0; i--) {
			int index = parsedIntArgumentList.get(i);
			if (!isValidIndex(index)) {
				return ERROR_INVALID_INDEX;
			}
		}
		
		ArrayList<Task> tasksRemoved = new ArrayList<Task>();
		for (int i = parsedIntArgumentList.size() - 1; i >= 0; i--) {
			int index = parsedIntArgumentList.get(i);
			argumentListForReverse[i] = argumentList.get(i); // for undo

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
		String historyStatus = pushToHistory(commandType, commandToPush, shouldClearHistory, isUndoHistory);
		historyStatus = statusItemFormatting(historyStatus, parsedIntArgumentList);
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
	ArrayList<String> preprocessDeleteArgument(ArrayList<String> argumentList)
			throws NumberFormatException, IndexOutOfBoundsException {
		ArrayList<String> finalArgumentList = new ArrayList<>();
		if (argumentList.size() == 1 && argumentList.get(0).equalsIgnoreCase(IDENTIFIER_DELETE_ALL)) {
			argumentList.clear();
			for (int i = 0; i < listOfTasks.size(); i++) {
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
	
	String editSpecialField(Task newTask, Task clonedTask) {
		if (newTask.hasName()) {
			clonedTask.setName(newTask.getName());
		}
		if (newTask.hasLocation()) {
			clonedTask.setLocation(newTask.getLocation());
		}
		if (newTask.hasStartingTime()) {
			clonedTask.setStartingTime(newTask.getStartingTime());
			clonedTask.setEndingTime(newTask.getEndingTime());
		}
		if (newTask.hasEndingTime() && !newTask.hasStartingTime()) {
			clonedTask.setEndingTime(newTask.getEndingTime());
		}
		if (newTask.hasPeriodicInterval() || newTask.hasPeriodicRepeats()) {
			return ERROR_EDIT_CANNOT_RECURRING;
		}
		return null;
	}
	
	/**
	 * Replaces an item from the list of tasks in memory with the new userTask
	 * 
	 * @param userTasks
	 *            this should be of size 1 which contains the new task to
	 *            replaced with. all other tasks will be ignored
	 * @param argumentList
	 *            a number string, which contains the index position of the task
	 *            to edit
	 * @param shouldClearHistory
	 * @param isUndoHistory
	 * 
	 * @return status string
	 */
	String editItem(ArrayList<Task> userTasks, ArrayList<String> argumentList,
			boolean shouldClearHistory, boolean isUndoHistory) {
		if (isEmptyArgumentList(argumentList)) {
			return ERROR_INVALID_ARGUMENT;
		}
		Task userTask = userTasks.get(0); // should only have 1 item
		try {
			logger.fine("Attempting to determine index.");
			int index = Integer.parseInt(argumentList.get(0)) - 1;
			boolean hasClashes = false;
			if (isValidIndex(index)) {
				// for history
				Task taskEdited = listOfTasks.get(index);
				if (hasClashes(userTask)) {
					hasClashes = true;
				}

				Task newTask = taskEdited.clone();
				String statusOfSpecialEdit = editSpecialField(userTask, newTask);
				if (statusOfSpecialEdit != null) {
					return statusOfSpecialEdit;
				}
				listOfTasks.remove(index);
				logger.fine("Old task removed from list.");
				
				listOfTasks.add(index, newTask);
				logger.fine("New task added to list.");
				
				String[] indexString = { Integer.toString(index + 1) };
				ArrayList<Integer> parsedIntArgumentList = new ArrayList<Integer>();
				parsedIntArgumentList.add(index);
				String historyStatus = pushToHistory(Command.Type.EDIT, new Command(Command.Type.EDIT,
						indexString, taskEdited), shouldClearHistory, isUndoHistory);
				historyStatus = statusItemFormatting(historyStatus, parsedIntArgumentList);
				if(hasClashes){
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

	String displayItems() {
		if (listOfTasks.isEmpty()) {
			return MESSAGE_DISPLAY_EMPTY;
		} else {
			return MESSAGE_SUCCESS_DISPLAY;
		}
	}

	/**
	 * Sorts a list of tasks by time
	 * The starting time for a task is used if it exists
	 * Else the ending time for a task is used instead
	 * @return
	 */
	boolean sortListOfTasks(){ // by time
		Collections.sort(listOfTasks);
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
			ArrayList<Task> listOfDeadlines = new ArrayList<Task>();
			ArrayList<Task> listOfEvents = new ArrayList<Task>();
			for (int i = 0; i < listOfTasks.size(); i++) {
				Task curTask = listOfTasks.get(i);
				if (curTask.hasEndingTime()) {
					if (curTask.hasStartingTime()) {
						listOfEvents.add(curTask);
					} else {
						listOfDeadlines.add(curTask);
					}
				} else {
					listOfFloating.add(curTask);
				}
			}
			
			if (listOfFloating.size() >= 3) {
				listOfShownTasks.addAll(listOfFloating.subList(0, 3));
			} else {
				listOfShownTasks.addAll(listOfFloating);
			}
			if (listOfDeadlines.size() >= 3) {
				listOfShownTasks.addAll(listOfDeadlines.subList(0, 3));
			} else {
				listOfShownTasks.addAll(listOfDeadlines);
			}
			if (listOfEvents.size() >= 3) {
				listOfShownTasks.addAll(listOfEvents.subList(0, 3));
			} else {
				listOfShownTasks.addAll(listOfEvents);
			}
			// default view
			return UIObject.showTasks(listOfShownTasks);
		} else {
			listOfShownTasks = new ArrayList<Task>();
			for (int i = 0; i < listOfTasks.size(); i++) {
				listOfShownTasks.add(listOfTasks.get(i));
			}

			for (int j = 0; j < listFilter.size(); j++) {
				Task curFilter = listFilter.get(j);
				String searchTaskName = curFilter.getName();
				int i = 0;
				if (searchTaskName != null) {
					while (i < listOfShownTasks.size()) {
						Task curTask = listOfShownTasks.get(i);
						if (!curTask.getName().contains(searchTaskName)) {
							listOfShownTasks.remove(i);
						} else {
							i++;
						}
					}
				}
			}
			
			for (int j = 0; j < listFilter.size(); j++) {
				Task curFilter = listFilter.get(j);
				String searchLocation = curFilter.getLocation();
				int i = 0;
				if (searchLocation != null) {
					//System.out.println("searchLocation is '" + searchLocation + "'");
					while (i < listOfShownTasks.size()) {
						Task curTask = listOfShownTasks.get(i);
						if (!curTask.getLocation().contains(searchLocation)) {
							listOfShownTasks.remove(i);
						} else {
							i++;
						}
					}
				}
			}
			listFilter.clear();
			return UIObject.showTasks(listOfShownTasks);
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
		return MESSAGE_REDO + executeCommand(previousCommand, false, true);
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
		HashSet<String> hs = new HashSet<>();
		hs.addAll(parsedIntArgumentList);
		parsedIntArgumentList.clear();
		parsedIntArgumentList.addAll(hs);
		return parsedIntArgumentList;
	}

	/**
	 * Given a string & a list of integers,
	 * concatenate the integers nicely into the form "1,2,3,..." and
	 * use it to format the given string. At the end of the method,
	 * the resulting string is returned
	 * 
	 * This is mainly used for status formatting
	 * 
	 * @param string
	 * @param parsedIntList
	 * @return resultingString
	 */
	String statusItemFormatting(String string,
			ArrayList<Integer> parsedIntList) {
		Collections.sort(parsedIntList);
		String combinedNumberStrings = "";
		for (int i = 0; i < parsedIntList.size(); i++) {
			combinedNumberStrings += (parsedIntList.get(i) + 1);
			if (i != parsedIntList.size() - 1) {
				combinedNumberStrings += SEPARATOR_ITEM_LIST;
			}
		}
		return String.format(string, combinedNumberStrings);
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
	ArrayList<Task> splitPeriodic(Task recurringTask) throws Exception{
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
	boolean addInterval(Task curTask, String periodicIntervalString) throws Exception{
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
		if(periodicIntervalUnit.equalsIgnoreCase("days")){
			calendarUnit = Calendar.DATE;
		} else if(periodicIntervalUnit.equalsIgnoreCase("weeks")) {
			calendarUnit = Calendar.WEEK_OF_YEAR;
		} else {
			calendarUnit = Calendar.YEAR;
		}
		
		if(curTask.hasStartingTime()) {
			curTask.getStartingTime().add(calendarUnit, periodicInterval);
		}
		curTask.getEndingTime().add(calendarUnit, periodicInterval);
		return true;
	}

	String exitProgram() {
		System.exit(0);
		return MESSAGE_SUCCESS_EXIT;
	}
}