package ui;

import java.util.List;
import java.util.Scanner;

import global.Task;
import ui.formatter.FormatterHelper;
import ui.formatter.TextFormatter;

public class TextUI implements UI {
	
	/*
	 * Assume standard console height + a few more
	 */
	private static final int MAX_CONSOLE_LINE = 35;
	
	private TextFormatter taskListFormatter = new TextFormatter();
	private Scanner userInputScanner = new Scanner(System.in);
	
	private String currentTaskData = "";
	private String currentStatus = "";
	
	//@@author A0134155M
	@Override
	public boolean showToUser(String stringToShow) {
		System.out.println(stringToShow);
		return true;
	}
	
	//@@author A0134155M
	private boolean refreshConsole() {
		clearConsole();
		outputNewData();
		return true;
	}

	//@@author A0134155M
	private void outputNewData() {
		System.out.println(currentTaskData);
		System.out.println(currentStatus);
	}

	//@@author A0134155M
	@Override
	public boolean showStatusToUser(String stringToShow) {
		setNewStatus(stringToShow);
		return refreshConsole();
	}

	//@@author A0134155M
	private void setNewStatus(String stringToShow) {
		currentStatus = stringToShow;
	}

	//@@author A0134155M
	@Override
	public boolean showTasks(List<Task> tasks, DisplayType displayType, List<String> titles) {
		int minTable = -1;
		int minRowCountPerTable = -1;
		if (displayType == DisplayType.DEFAULT) {
			minTable = DEFAULT_DISPLAY_MIN_TABLE;
			minRowCountPerTable = DEFAULT_DISPLAY_MIN_ROW;
		} else if (displayType == DisplayType.FILTERED) {
			minTable = FILTERED_DISPLAY_MIN_TABLE;
			minRowCountPerTable = FILTERED_DISPLAY_MIN_ROW;
		} else {
			assert false : "DisplayType = ?";
		}

		Object[][][] taskListsData = FormatterHelper.getTaskListData(tasks,
				displayType == DisplayType.DEFAULT, minTable, minRowCountPerTable);
		assert taskListsData != null;

		String formattedTaskList = taskListFormatter.formatTaskList(taskListsData,
				titles, MAXIMUM_COLUMN_WIDTH);
		
		setNewTaskData(formattedTaskList);
		return refreshConsole();
	}

	//@@author A0134155M
	private void setNewTaskData(String formattedTaskList) {
		currentTaskData = formattedTaskList;
	}

	//@@author A0134155M
	private void clearConsole() {
		for (int i = 0; i < MAX_CONSOLE_LINE; i++) {
			System.out.println();
		}
	}

	//@@author A0134155M
	@Override
	public String promptUser(String prompt) {
		System.out.print(prompt);
		String userInput = readUserInput();
		return userInput;
	}

	//@@author A0134155M
	private String readUserInput() {
		String userInput = userInputScanner.nextLine();
		return userInput;
	}

}
