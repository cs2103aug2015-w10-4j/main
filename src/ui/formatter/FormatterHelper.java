package ui.formatter;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import global.Task;

public class FormatterHelper {
	
	private static final String EMPTY_STRING_SUBSTITUTE = " ";
	
	public static final String[] FIELD_NAMES = new String[] { "UNUSED",
			"name",
			"startingTime",
			"endingTime",
			"location",
			"isDone"
			};
	

	//TODO: automate this?
	public static final String[] TABLE_COLUMN_NAMES = new String[] { "No.",
			"Description",
			"Starting Time",
			"Ending Time",
			"Location",
			"Status"
			};
	
	public static final int COLUMN_COUNT = 6;

	private static final String TASK_DONE = "Done.";
	private static final String TASK_NOT_DONE = "Not done yet.";
	
	//additional space at the beginning and the end
	private static final int ADDITIONAL_SPACE = 2;
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yy h:mm a");

	//@@author A0134155M
	/**
	 * Find the minimal width for each column, taking into account the content
	 * of the column and char limit for each line.
	 * @param taskList
	 * @param lineCharLimit
	 * @return an array of ColumnInfo[] (pair of name and width) for each column.
	 */
	public static ColumnInfo[] getColumnInfo(Object[][] taskListData, int lineCharLimit) {
		int[] columnLengths = new int[COLUMN_COUNT];
		
		for (int i = 0; i < columnLengths.length; i++) {
			//coulumn length should be at least as long as the column name length
			int columnLength = TABLE_COLUMN_NAMES[i].length() + ADDITIONAL_SPACE;
			
			for (int j = 0; j < taskListData.length; j++) {
				String entry = getStringRepresentation(taskListData[j][i]);

				int currentEntryLength = entry.length();
				currentEntryLength = Math.min(currentEntryLength, lineCharLimit);
				
				columnLength  = Math.max(columnLength, currentEntryLength);
			}

			columnLengths[i] = columnLength;
		}
		
		ColumnInfo[] columnInfo = new ColumnInfo[COLUMN_COUNT];
		for (int i = 0; i < columnInfo.length; i++) {
			columnInfo[i] = new ColumnInfo(TABLE_COLUMN_NAMES[i], columnLengths[i]);
		}
		
		return columnInfo;
	}

	//@@author A0134155M
	/**
	 * Convert every field in Task class to its string representation
	 * @param objectInField
	 * @return
	 */
	public static String getStringRepresentation(Object objectInField) {
		if (objectInField == null) {
			return EMPTY_STRING_SUBSTITUTE;
		}
		
		if (objectInField instanceof String) {
			return (String) objectInField;
		} else if (objectInField instanceof Calendar) {
			return String.format("%s", DATE_FORMAT.format(((Calendar) objectInField).getTime()));
		} else if (objectInField instanceof Boolean) {
			return String.format("%s", Boolean.TRUE.equals(objectInField) ? TASK_DONE
					: TASK_NOT_DONE);
		}
		
		assert false : "Fields in Task object can only be either String, Calendar, or Boolean";
		return null;
	}

	//@@author A0134155M
	/**
	 * Split string to multiple lines according to character limit for a line as
	 * specified in lineCharLimit
	 * @param string
	 * @param lineCharLimit
	 * @return an array of string representing the content of string after split
	 */
	public static String[] splitString(String string, int lineCharLimit) {
		if (string == null) {
			return new String[] {EMPTY_STRING_SUBSTITUTE};
		}
		
		List<String> resultList = new ArrayList<String>();

		int currentPosition = 0;
		while (currentPosition < string.length()) {
			if (currentPosition + lineCharLimit >= string.length()) { //we can fit all
				resultList.add(string.substring(currentPosition, string.length()));
				currentPosition = string.length();
			} else {
				int nextStartingPosition = currentPosition + lineCharLimit;
				
				String toAdd = string.substring(currentPosition, nextStartingPosition);
				resultList.add(toAdd);
				
				currentPosition = nextStartingPosition;
			}
		}
		
		String[] resultArray = resultList.toArray(new String[] {});
		
		return resultArray;
	}
	
	//@@author A0134155M
	/**
	 * Extract the data in a List<Task> to 3D object array so that it will be easier
	 * to put them into a table. The first dimension of the resulting Object array
	 * denotes the index of the table (since different type of tasks will be displayed
	 * on a different table, the second dimension denotes the task number for each
	 * table, and the last dimension denotes the fields that are in the Task data
	 * structure.
	 * @param tasks list of tasks to be extracted
	 * @param needTaskSplit whether we need to group the tasks according to its type
	 * @param minTable minimum number of table needed (empty if unused)
	 * @param minRowCountPerTable minimum number of row for each table
	 * @return
	 */
	public static Object[][][] getTaskListData(List<Task> tasks, boolean needTaskSplit,
			int minTable, int minRowCountPerTable) {
		List<List<Task>> taskLists = new ArrayList<List<Task>>();
		if (needTaskSplit) {
			taskLists = splitTask(tasks);
		} else {
			taskLists.add(tasks);
		}

		List<Object[][]> result = new ArrayList<Object[][]>();
		//we want the numberings for all taskLists synchronized
		int numberCounter = 0;
		for (int taskListIndex = 0; taskListIndex < taskLists.size(); taskListIndex++) {
			List<Task> currentTaskList = taskLists.get(taskListIndex);

			//including the number column
			List<Object[]> currentTaskListData = new ArrayList<Object[]>();
			for (int i = 0; i < currentTaskList.size(); i++) {
				Task currentTask = currentTaskList.get(i);
				Object[] currentTaskData = new Object[COLUMN_COUNT];
				
				for (int j = 0; j < COLUMN_COUNT; j++) {
					if (j == 0) {
						currentTaskData[j] = String.valueOf(numberCounter + 1);
						numberCounter++;
					} else {
						Field currentField = null;

						try {
							currentField = Task.class.getDeclaredField(FIELD_NAMES[j]);
							currentField.setAccessible(true);
							
							Object objectInField = currentField.get(currentTask);
							
							currentTaskData[j] = objectInField;
						} catch (SecurityException | NoSuchFieldException | IllegalAccessException e) {
							assert currentField != null;
							assert false; //should not happen
						}
					}
				}
				
				currentTaskListData.add(currentTaskData);
			}
			
			while (currentTaskListData.size() < minRowCountPerTable) {
				currentTaskListData.add(createEmptyTaskData());
			}
			
			result.add(currentTaskListData.toArray(new Object[1][1]));
		}
		
		while (result.size() < minTable) {
			Object[][] emptyTaskList = new Object[minRowCountPerTable][];
			
			for (int i = 0; i < minRowCountPerTable; i++) {
				emptyTaskList[i] = createEmptyTaskData();
			}
			
			result.add(emptyTaskList);
		}
		
		return result.toArray(new Object[1][1][1]);
	}

	//@@author A0134155M
	private static Object[] createEmptyTaskData() {
		Object[] emptyTaskData = new Object[COLUMN_COUNT];

		//since the last field that represents whether a task has been done or not
		//must be a boolean...
		//empty task can't be done
		
		emptyTaskData[COLUMN_COUNT - 1] = new Boolean(false);
		
		return emptyTaskData;
	}

	//@@author A0134155M
	private static List<List<Task>> splitTask(List<Task> tasks) {
		List<List<Task>> taskLists = new ArrayList<List<Task>>();

		for (int i = 0; i < tasks.size(); i++) {
			Task currentTask = tasks.get(i);
			Task previousTask = i == 0 ? null : tasks.get(i - 1);
			
			if (!areInSameList(currentTask, previousTask)) {
				if (currentTask.getTime() == null) {//temp workaround to display floating tasks properly (for live demo)
					int curTaskListSize = taskLists.size();
					for (int j = 0; j < 3 - curTaskListSize; j++) {
						taskLists.add(new ArrayList<Task>());
					}
				} else {
					taskLists.add(new ArrayList<Task>());
				}
			}

			int lastTaskListIndex = taskLists.size() - 1;
			taskLists.get(lastTaskListIndex).add(currentTask);
		}

		return taskLists;
	}

	//@@author A0134155M
	private static boolean areInSameList(Task currentTask, Task previousTask) {
		assert currentTask != null;
		if (previousTask == null) {
			return false;
		}
		if (currentTask.hasEndingTime() != previousTask.hasEndingTime()) {
			return false;
		}
		
		if (!currentTask.hasEndingTime()) {
			return true;
		} else {
			Calendar currentTaskDate = currentTask.hasStartingTime() ? currentTask.getStartingTime() :
				currentTask.getEndingTime();
			Calendar previousTaskDate = previousTask.hasStartingTime() ? previousTask.getStartingTime() :
				previousTask.getEndingTime();
			
			return areOnSameDay(currentTaskDate, previousTaskDate);
		}
	}

	//@@author A0134155M
	private static boolean areOnSameDay(Calendar currentTaskDate, Calendar previousTaskDate) {
		return currentTaskDate.get(Calendar.YEAR) == previousTaskDate.get(Calendar.YEAR) &&
				currentTaskDate.get(Calendar.DAY_OF_YEAR) == previousTaskDate.get(Calendar.DAY_OF_YEAR);
	}
	
}
