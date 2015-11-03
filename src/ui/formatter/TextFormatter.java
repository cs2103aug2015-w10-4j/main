package ui.formatter;

public class TextFormatter {

	private static final String NULL_STRING_SUBSTITUTE = "";
	
	private static final String MESSAGE_DISPLAY_NEWLINE = System.getProperty("line.separator");
	private static final String MESSAGE_DISPLAY_EMPTY = "No items to display.";
	
	private static final char INTERSECTION_CHAR = '+';
	private static final char HORIZONTAL_CHAR = '-';
	private static final char VERTICAL_CHAR = '|';
	
	//@@author A0134155M
	/**
	 * Format a given list of 3D Object array containing task data into a string table.
	 * @param taskLists
	 * @param lineCharLimit
	 * @return
	 */
	public String formatTaskList(Object[][][] taskLists, int lineCharLimit) {
		if (isEmpty(taskLists)) {
			return MESSAGE_DISPLAY_EMPTY;
		}

		StringBuilder result = new StringBuilder();
		for (Object[][] taskList : taskLists) {
			ColumnInfo[] columnInfo = FormatterHelper.getColumnInfo(taskList, lineCharLimit);
			
			result.append(getRowSeparator(columnInfo));
			result.append(getHeader(columnInfo));
			for (int i = 0; i < taskList.length; i++) {
				Object[] currentTaskInfo = taskList[i];
				
				result.append(getRowSeparator(columnInfo));
				result.append(getTaskData(columnInfo, currentTaskInfo, i, lineCharLimit));
			}
			result.append(getRowSeparator(columnInfo));
			result.append(MESSAGE_DISPLAY_NEWLINE);
		}
		
		return result.toString();
	}

	//@@author A0134155M
	private boolean isEmpty(Object[][][] taskLists) {
		int maxLength = 0;
		for (Object[][] taskList : taskLists) {
			if (taskList != null) {
				maxLength = Math.max(maxLength, taskList.length);
			}
		}
		return maxLength == 0;
	}

	//@@author A0134155M
	private String getTaskData(ColumnInfo[] columnInfo, Object[] task, int taskId, int lineCharLimit) {
		StringBuilder result = new StringBuilder();
		
		String[][] columnData = new String[columnInfo.length][];
		
		for (int i = 0; i < columnInfo.length; i++) {
			String stringRepresentation = FormatterHelper.getStringRepresentation(task[i]);
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

	//@@author A0134155M
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

	//@@author A0134155M
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
