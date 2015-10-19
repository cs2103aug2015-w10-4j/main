package ui.formatter;

import java.lang.reflect.Field;
import java.util.List;

import global.Task;

public class TaskListFormatter {

	private static TaskListFormatter instance = null;

	private static final String NULL_STRING_SUBSTITUTE = "";
	
	private static final String MESSAGE_DISPLAY_NEWLINE = System.getProperty("line.separator");
	private static final String MESSAGE_DISPLAY_EMPTY = "No items to display.";
	
	private static final char INTERSECTION_CHAR = '+';
	private static final char HORIZONTAL_CHAR = '-';
	private static final char VERTICAL_CHAR = '|';
	
	public static TaskListFormatter getInstance() {
		if (instance == null) {
			instance = new TaskListFormatter();
		}
		return instance;
	}
	
	public String formatTaskList(List<Task> taskList, int lineCharLimit) {
		if (taskList.size() == 0) {
			return MESSAGE_DISPLAY_EMPTY;
		}
		ColumnInfo[] columnInfo = FormatterHelper.getColumnInfo(taskList, lineCharLimit);
		
		StringBuilder result = new StringBuilder();
		result.append(getRowSeparator(columnInfo));
		result.append(getHeader(columnInfo));
		for (int i = 0; i < taskList.size(); i++) {
			Task task = taskList.get(i);
			
			result.append(getRowSeparator(columnInfo));
			result.append(getTaskData(columnInfo, task, i, lineCharLimit));
		}
		result.append(getRowSeparator(columnInfo));
		
		return result.toString();
	}

	private String getTaskData(ColumnInfo[] columnInfo, Task task, int taskId, int lineCharLimit) {
		StringBuilder result = new StringBuilder();
		
		String[][] columnData = new String[columnInfo.length][];
		
		for (int i = 0; i < columnInfo.length; i++) {
			String stringRepresentation = null;
			try {
				if (i == 0) {
					stringRepresentation = String.valueOf(taskId + 1);
				} else {
					Field currentField = Task.class.getDeclaredField(FormatterHelper.FIELD_NAMES[i]);
					currentField.setAccessible(true);
					
					Object objectInField = currentField.get(task);
					stringRepresentation = FormatterHelper.getStringRepresentation(objectInField);
				}
			} catch (NoSuchFieldException | SecurityException e) {
				assert false;
			} catch (IllegalArgumentException | IllegalAccessException e) {
				assert false;
			}
			columnData[i] = FormatterHelper.splitString(stringRepresentation, lineCharLimit);
		}
		
		int rowCountForCurrentTask = 0;
		for (int i = 0; i < columnData.length; i++) {
			rowCountForCurrentTask = Math.max(rowCountForCurrentTask, columnData[i].length);
		}
		
		for (int row = 1; row <= rowCountForCurrentTask; row++) {
			result.append(VERTICAL_CHAR);
			for (int i = 0; i < columnData.length; i++) {
				if (columnData[i].length >= row) {
					result.append(StringFormatter.formatString(columnData[i][row - 1],
							StringFormatter.Alignment.ALIGN_LEFT,
							columnInfo[i].getColumnWidth()));
					result.append(VERTICAL_CHAR);
				} else {
					result.append(StringFormatter.formatString(NULL_STRING_SUBSTITUTE, 
							StringFormatter.Alignment.ALIGN_LEFT,
							columnInfo[i].getColumnWidth()));
					result.append(VERTICAL_CHAR);
				}
			}
			result.append(MESSAGE_DISPLAY_NEWLINE);
		}
		
		return result.toString();
	}

	private String getHeader(ColumnInfo[] columnInfo) {
		StringBuilder result = new StringBuilder();
		
		result.append(VERTICAL_CHAR);
		for (int i = 0; i < columnInfo.length; i++) {
			result.append(StringFormatter.formatString(columnInfo[i].getColumnName(), 
					StringFormatter.Alignment.ALIGN_CENTER,
					columnInfo[i].getColumnWidth()));
			result.append(VERTICAL_CHAR);
		}
		result.append(MESSAGE_DISPLAY_NEWLINE);
		
		return result.toString();
	}

	private String getRowSeparator(ColumnInfo[] columnInfo) {
		StringBuilder result = new StringBuilder();
		
		result.append(INTERSECTION_CHAR);
		for (int i = 0; i < columnInfo.length; i++) {
			for (int j = 0; j < columnInfo[i].getColumnWidth(); j++) {
				result.append(HORIZONTAL_CHAR);
			}
			result.append(INTERSECTION_CHAR);
		}
		result.append(MESSAGE_DISPLAY_NEWLINE);
		
		return result.toString();
	}
	
}
