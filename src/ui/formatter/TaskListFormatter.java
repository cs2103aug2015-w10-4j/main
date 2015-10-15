package ui.formatter;

import java.lang.reflect.Field;
import java.util.List;

import global.Task;

public class TaskListFormatter {

	private static TaskListFormatter instance = null;
	private static final String MESSAGE_DISPLAY_NEWLINE = System.getProperty("line.separator");
	private static final String MESSAGE_DISPLAY_EMPTY = "No items to display.";
	
	private char INTERSECTION_CHAR = '+';
	private char HORIZONTAL_CHAR = '-';
	private char VERTICAL_CHAR = '|';
	
	public static TaskListFormatter getInstance() {
		if (instance == null) {
			instance = new TaskListFormatter();
		}
		return instance;
	}
	
	public String formatTaskList(List<Task> taskList) {
		if (taskList.size() == 0) {
			return MESSAGE_DISPLAY_EMPTY;
		}
		ColumnInfo[] columnInfo = FormatterHelper.getColumnInfo(taskList);
		
		String result = "";
		result += getRowSeparator(columnInfo);
		result += getHeader(columnInfo);
		for (int i = 0; i < taskList.size(); i++) {
			Task task = taskList.get(i);
			
			result += getRowSeparator(columnInfo);
			result += getRowData(columnInfo, task, i);
		}
		result += getFooter(columnInfo);
		
		return result;
	}

	private String getFooter(ColumnInfo[] columnInfo) {
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

	private String getRowData(ColumnInfo[] columnInfo, Task task, int taskId) {
		StringBuilder result = new StringBuilder();
		
		result.append(VERTICAL_CHAR);
		for (int i = 0; i < columnInfo.length; i++) {
			try {
				String stringRepresentation = null;
				
				if (i == 0) {
					stringRepresentation = String.valueOf(taskId + 1);
				} else {
					Field currentField = Task.class.getDeclaredField(FormatterHelper.FIELD_NAMES[i]);
					currentField.setAccessible(true);
					
					Object objectInField = currentField.get(task);
					stringRepresentation = FormatterHelper.getStringRepresentation(objectInField);
				}
				
				result.append(StringFormatter.formatString(stringRepresentation,
														   StringFormatter.Alignment.ALIGN_LEFT,
														   columnInfo[i].getColumnWidth()));
				result.append(VERTICAL_CHAR);
			
			} catch (NoSuchFieldException | SecurityException e) {
				assert false;
			} catch (IllegalArgumentException | IllegalAccessException e) {
				assert false;
			}
		}
		result.append(MESSAGE_DISPLAY_NEWLINE);
		
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
