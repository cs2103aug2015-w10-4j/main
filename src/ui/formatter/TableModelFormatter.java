package ui.formatter;

import global.Pair;

public class TableModelFormatter {
	
	private static final String STRIKE_HTML_TAG_BEGIN = "<html><strike>";
	private static final String STRIKE_HTML_TAG_END = "</strike></html>";
	
	private static boolean USE_STRIKE_HTML_TAG = false;
	
	private static final int IS_DONE_FIELD_NUMBER = 5;
	//minus one since we do not want the last field (isDone field)
	private static final int COLUMN_COUNT = FormatterHelper.COLUMN_COUNT - 1;
	
	//@@author A0134155M
	/**
	 * Converts each field of the task in a task list data in a table produced by
	 * FormatterHelper.getTaskListData() to their string representation. Also returns
	 * a Boolean[] array denoting whether a task has been done or not.
	 * @param taskList 2D object array denoting a task list.
	 * @return
	 */
	public Pair<Object[][], Boolean[]> formatTaskList(Object[][] taskList) {
		Object[][] tableModelData = new Object[taskList.length][COLUMN_COUNT];
		Boolean[] isDone = new Boolean[taskList.length];
		
		for (int i = 0; i < taskList.length; i++) {
			assert taskList[i][IS_DONE_FIELD_NUMBER] instanceof Boolean;
			isDone[i] = (Boolean) taskList[i][IS_DONE_FIELD_NUMBER];
			
			for (int j = 0; j < COLUMN_COUNT; j++) {
				tableModelData[i][j] = FormatterHelper.getStringRepresentation(taskList[i][j]);
				
				if (isDone[i] && USE_STRIKE_HTML_TAG) {
					tableModelData[i][j] = STRIKE_HTML_TAG_BEGIN + tableModelData[i][j] +
							STRIKE_HTML_TAG_END;
				}
			}
		}

		Pair<Object[][], Boolean[]> result = new Pair<Object[][], Boolean[]>(tableModelData, isDone);
		return result;
	}
	
}
