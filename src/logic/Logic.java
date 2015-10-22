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
	private static final String ERROR_UNSPECIFIED_TIMING = "Error: Cannot convert into periodic task due to unspecified timing.";
	/*
	 * Declaration of object variables
	 */
	Logger logger = Logger.getGlobal();
	UI UIObject;
	Parser parserObject;
	Storage storageObject;
	History historyObject;
	ArrayList<Task> listOfTasks = new ArrayList<Task>();
	ArrayList<String> listFilter = new ArrayList<String>();

	// date format converter
	static SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");

	/*
	 * Static strings - errors and messages
	 */
	private static final String MESSAGE_WELCOME = "Welcome to Tasky! This is an open source project";
	private static final String MESSAGE_PROMPT_COMMAND = "command :";
	private static final String MESSAGE_SUCCESS_UNDO_ADD = "Undo : Deleted item(s) restored.";
	private static final String MESSAGE_SUCCESS_REDO_ADD = "Redo : Deleted item(s) restored.";
	private static final String MESSAGE_SUCCESS_UNDO_DELETE = "Undo : Added item(s) removed.";
	private static final String MESSAGE_SUCCESS_REDO_DELETE = "Redo : Added item(s) removed.";
	private static final String MESSAGE_SUCCESS_UNDO_MARK = "Undo : Item(s) successfully marked.";
	private static final String MESSAGE_SUCCESS_REDO_MARK = "Redo : Item(s) successfully marked.";
	private static final String MESSAGE_SUCCESS_UNDO_UNMARK = "Undo : Item(s) successfully unmarked.";
	private static final String MESSAGE_SUCCESS_REDO_UNMARK = "Redo : Item(s) successfully unmarked.";
	private static final String MESSAGE_SUCCESS_UNDO_EDIT = "Undo : Reverted edits.";
	private static final String MESSAGE_SUCCESS_REDO_EDIT = "Redo : Reverted edits.";
	private static final String MESSAGE_SUCCESS_ADD = "Item(s) %s successfully added.";
	private static final String MESSAGE_SUCCESS_DELETE = "Item(s) %s successfully deleted.";
	private static final String MESSAGE_SUCCESS_MARK = "Item(s) %s successfully marked.";
	private static final String MESSAGE_SUCCESS_UNMARK = "Item(s) %s successfully unmarked.";
	private static final String MESSAGE_SUCCESS_SEARCH = "Search results for '%s'";
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
		showWelcomeMessage();
		readAndExecuteUserInput();
	}

	void showWelcomeMessage() {
		UIObject.showToUser(MESSAGE_WELCOME);
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
	 * @param commandObject
	 * @param shouldPushToHistory
	 *            false if command is called from redo
	 * @param isUndoHistory
	 *            true if command is called from undo false if command is called
	 *            by user directly
	 * 
	 * @return status string to be shown to user
	 */
	String executeCommand(Command commandObject, boolean shouldPushToHistory,
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
					return addItem(userTasks, argumentList, shouldPushToHistory,
							isUndoHistory);
				case DELETE :
					logger.info("DELETE command detected");
					return deleteItem(argumentList, shouldPushToHistory, isUndoHistory);
				case EDIT :
					logger.info("EDIT command detected");
					return editItem(userTasks, argumentList, shouldPushToHistory,
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
					return markDoneStatus(argumentList, shouldPushToHistory, isUndoHistory, true);
				case UNMARK:
					logger.info("UNMARK command detected");
					return markDoneStatus(argumentList, shouldPushToHistory, isUndoHistory, false);
				case SEARCH:
					logger.info("SEARCH command detected");
					return searchForTaskName(argumentList);
				default :
					logger.warning("Command type cannot be identified!");
					return ERROR_NO_COMMAND_HANDLER;
			}
		}
	}
	
	String pushToHistory(Command.Type commandType, Command commandToPush, boolean shouldPushToHistory, boolean isUndoHistory){
		String normalStatus;
		String undoStatus;
		String redoStatus;
		switch(commandType){
			case ADD:
				normalStatus = MESSAGE_SUCCESS_ADD;
				undoStatus = MESSAGE_SUCCESS_UNDO_ADD;
				redoStatus = MESSAGE_SUCCESS_REDO_ADD;
				break;
			case DELETE:
				normalStatus = MESSAGE_SUCCESS_DELETE;
				undoStatus = MESSAGE_SUCCESS_UNDO_DELETE;
				redoStatus = MESSAGE_SUCCESS_REDO_DELETE;
				break;
			case EDIT:
				normalStatus = MESSAGE_SUCCESS_EDIT;
				undoStatus = MESSAGE_SUCCESS_UNDO_EDIT;
				redoStatus = MESSAGE_SUCCESS_REDO_EDIT;
				break;
			case MARK:
				normalStatus = MESSAGE_SUCCESS_MARK;
				undoStatus = MESSAGE_SUCCESS_UNDO_MARK;
				redoStatus = MESSAGE_SUCCESS_REDO_MARK;
				break;
			case UNMARK:
				normalStatus = MESSAGE_SUCCESS_UNMARK;
				undoStatus = MESSAGE_SUCCESS_UNDO_UNMARK;
				redoStatus = MESSAGE_SUCCESS_REDO_UNMARK;
				break;
			default:
				return ERROR_HISTORY_NO_COMMAND_HANDLER;
		}
		
		logger.fine("Checking if command should be pushed to history.");
		if (shouldPushToHistory) {
			logger.finer("Pushing command to history.");

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
		} else {
			return redoStatus;
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
	 * @param shouldPushToHistory
	 * @param isUndoHistory
	 * 
	 * @return status string
	 */
	String addItem(ArrayList<Task> userTasks, ArrayList<String> argumentList,
			boolean shouldPushToHistory, boolean isUndoHistory) {
		try {
			logger.fine("Attempting to determine index.");
			ArrayList<Integer> parsedIntList = new ArrayList<Integer>(); // for
																			// status
			String[] argumentListForReverse = new String[userTasks.size()]; // for
																			// undo
			boolean hasClashes = false;

			logger.fine("Checking for clashes.");
			if (haveClashes(userTasks)) {
				logger.finer("Clash in timing detected.");
				hasClashes = true;
			}

			if (isEmptyArgumentList(argumentList)) {
				for (int i = 0; i < userTasks.size(); i++) {
					int index = i + listOfTasks.size();
					listOfTasks.add(index, userTasks.get(i));

					parsedIntList.add(index);
					argumentListForReverse[i] = Integer.toString(index + 1);
				}
				logger.finer("No specified index. Defaulting all items to the end of list.");
			} else if (argumentList.size() != userTasks.size()) {
				return ERROR_INVALID_ARGUMENT;
			} else {
				logger.fine("Adding tasks to list.");
				for (int i = 0; i < userTasks.size(); i++) {
					int index = Integer.parseInt(argumentList.get(i)) - 1;
					listOfTasks.add(index, userTasks.get(i));

					parsedIntList.add(index);
					argumentListForReverse[i] = argumentList.get(i);
					logger.finer("Index " + (index + 1) + " specified.");
				}
			}
			
			resolvePeriodic();

			String historyStatus = pushToHistory(Command.Type.ADD, new Command(Command.Type.DELETE, argumentListForReverse), shouldPushToHistory, isUndoHistory);
			historyStatus = multipleItemFormatting(historyStatus, parsedIntList);
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
	 * Deletes an item from the list of tasks in memory
	 * 
	 * @param argumentList
	 *            all elements in this array should be integer strings elements
	 *            the array will first have its duplicates removed, then sorted
	 *            in an increasing order
	 * @param shouldPushToHistory
	 * @param isUndoHistory
	 * 
	 * @return status string
	 */
	String deleteItem(ArrayList<String> argumentList,
			boolean shouldPushToHistory, boolean isUndoHistory) {
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
		String historyStatus = pushToHistory(Command.Type.DELETE, commandToPush, shouldPushToHistory, isUndoHistory);
		historyStatus = multipleItemFormatting(historyStatus, parsedIntArgumentList);
		return historyStatus;

	}
	
	String searchForTaskName(ArrayList<String> argumentList) {
		String searchTerm = "";
		if (isEmptyArgumentList(argumentList)) {
			return ERROR_INVALID_ARGUMENT;
		}
		for (int i = 0; i < argumentList.size(); i++) {
			searchTerm += argumentList.get(i);
			if (i != argumentList.size() - 1) {
				searchTerm += " ";
			}
		}
		listFilter.add(searchTerm);

		return String.format(MESSAGE_SUCCESS_SEARCH, searchTerm);
	}
	
	String markDoneStatus(ArrayList<String> argumentList,
			boolean shouldPushToHistory, boolean isUndoHistory, boolean isDone) {
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
		if(isDone){
			commandType = Command.Type.MARK;
			reversedCommandType = Command.Type.UNMARK;
		}else{
			commandType = Command.Type.UNMARK;
			reversedCommandType = Command.Type.MARK;
		}
		Command commandToPush = new Command(reversedCommandType, argumentListForReverse);
		String historyStatus = pushToHistory(commandType, commandToPush, shouldPushToHistory, isUndoHistory);
		historyStatus = multipleItemFormatting(historyStatus, parsedIntArgumentList);
		return historyStatus;
	}

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
			if(!clonedTask.hasEndingTime()){
				return ERROR_UNSPECIFIED_TIMING;
			} else {
				clonedTask.setPeriodicInterval(newTask.getPeriodicInterval());
				clonedTask.setPeriodicRepeats(newTask.getPeriodicRepeats());
			}
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
	 * @param shouldPushToHistory
	 * @param isUndoHistory
	 * 
	 * @return status string
	 */
	String editItem(ArrayList<Task> userTasks, ArrayList<String> argumentList,
			boolean shouldPushToHistory, boolean isUndoHistory) {
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

				resolvePeriodic();
				
				String[] indexString = { Integer.toString(index + 1) };
				ArrayList<Integer> parsedIntArgumentList = new ArrayList<Integer>();
				parsedIntArgumentList.add(index);
				String historyStatus = pushToHistory(Command.Type.EDIT, new Command(Command.Type.EDIT,
						indexString, taskEdited), shouldPushToHistory, isUndoHistory);
				historyStatus = multipleItemFormatting(historyStatus, parsedIntArgumentList);
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
			// showUpdatedItems();
			return MESSAGE_SUCCESS_DISPLAY;
		}
	}

	/**
	 * @return calls the UI to display updated list of items
	 */
	boolean showUpdatedItems() {
		if(listFilter.size() == 0){
			// default view
			return UIObject.showTasks(listOfTasks);
		}else {
			ArrayList<Task> filteredList = new ArrayList<Task>();
			for (int i = 0; i < listOfTasks.size(); i++) {
				filteredList.add(listOfTasks.get(i));
			}

			for (int j = 0; j < listFilter.size(); j++) {
				String curSearchTerm = listFilter.get(j);
				int i = 0;
				while (i < filteredList.size()) {
					Task curTask = filteredList.get(i);
					if (!curTask.getName().contains(curSearchTerm)) {
						filteredList.remove(i);
					} else {
						i++;
					}
				}
			}
			listFilter.clear();
			return UIObject.showTasks(filteredList);
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
		return executeCommand(previousCommand, true, false);
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
		return executeCommand(previousCommand, false, false);
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

	boolean haveClashes(ArrayList<Task> tasks) {
		for (int i = 0; i < tasks.size(); i++) {
			if (hasClashes(tasks.get(i))) {
				return true;
			}
		}
		return false;
	}

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

	String multipleItemFormatting(String string,
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
	
	boolean updateListOfTasks() throws Exception {
		try {
			listOfTasks = storageObject.getItemList();
		} catch (FileNotFoundException e) {
			throw new Exception(ERROR_FILE_NOT_FOUND);
		}
		resolvePeriodic();
		return true;
	}
	
	boolean resolvePeriodic() throws Exception{
		for(int i = 0; i < listOfTasks.size(); i++){
			Task curTask = listOfTasks.get(i);
			String periodicRepeats = curTask.getPeriodicRepeats();
			if(periodicRepeats != null){
				//check if date is updated
				int periodicRepeatsInt, periodicIntervalValue;
				String periodicInterval = curTask.getPeriodicInterval();
				Calendar startingTime = curTask.getStartingTime();
				Calendar endingTime = curTask.getEndingTime();
				assert (periodicInterval != null);
				assert (endingTime != null);
				String[] periodicIntervalWords = periodicInterval.split(
						WHITE_SPACE_REGEX, 2);
				String periodicIntervalUnit = periodicIntervalWords[1];
				try {
					periodicRepeatsInt = Integer.parseInt(periodicRepeats);
					periodicIntervalValue = Integer
							.parseInt(periodicIntervalWords[0]);
				} catch (NumberFormatException e) {
					throw new Exception(ERROR_CANNOT_PARSE_PERIODIC_VALUES);
				}
				
				Calendar curTime = Calendar.getInstance();
				
				
				while(periodicRepeatsInt > 0 && endingTime.before(curTime)){
					if(periodicIntervalUnit.equalsIgnoreCase("days")){
						if(startingTime!= null) {
							startingTime.add(Calendar.DATE, periodicIntervalValue);
						}
						endingTime.add(Calendar.DATE, periodicIntervalValue);
					} else if (periodicIntervalUnit.equalsIgnoreCase("weeks")){
						if(startingTime!= null) {
							startingTime.add(Calendar.WEEK_OF_YEAR, periodicIntervalValue);
						}
						endingTime.add(Calendar.WEEK_OF_YEAR, periodicIntervalValue);
					} else if (periodicIntervalUnit.equalsIgnoreCase("months")){
						if(startingTime!= null) {
							startingTime.add(Calendar.MONTH, periodicIntervalValue);
						}
						endingTime.add(Calendar.MONTH, periodicIntervalValue);
					} else {
						throw new Exception("Error: Periodic interval unit unrecognised.");
					}
					periodicRepeatsInt--;
				}
				if(periodicRepeatsInt > 0){
					curTask.setPeriodicRepeats(Integer.toString(periodicRepeatsInt));
				} else {
					curTask.setPeriodicRepeats(null);
				}
			}
		}
		return true;
	}

	String exitProgram() {
		System.exit(0);
		return MESSAGE_SUCCESS_EXIT;
	}
}