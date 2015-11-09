package ui;

import java.util.List;

import global.Task;

public interface UI {

	public enum DisplayType {
		DEFAULT, FILTERED
	}
	
	static final String DEFAULT_PROMPT = "command ";
	static final String DISPLAY_AREA_FONT_NAME = "Lucida Console";
	
	static final int MAXIMUM_COLUMN_WIDTH = 30;
	
	static final int DEFAULT_DISPLAY_MIN_TABLE = 3;
	static final int DEFAULT_DISPLAY_MIN_ROW = 3;
	static final int FILTERED_DISPLAY_MIN_ROW = 1;
	static final int FILTERED_DISPLAY_MIN_TABLE = 1;
	
	/**
	 * Asks the UI to display content to user
	 * @param stringToShow
	 * @return true if successful
	 */
	public boolean showToUser(String stringToShow);
	
	/**
	 * Asks the UI to display content to user in the status bar
	 * @param stringToShow
	 * @return true if successful
	 */
	public boolean showStatusToUser(String stringToShow);
	
	/**
	 * Asks the UI to display the list of tasks
	 * @param tasks
	 * @return true if successful
	 */
	public boolean showTasks(List<Task> tasks, DisplayType displayType, List<String> titles);
	
	/**
	 * Prompt message and obtain user input
	 * @param prompt message to prompt user
	 * @return userInput
	 */
	public String promptUser(String prompt);
}
