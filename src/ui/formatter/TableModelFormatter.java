package ui.formatter;

public class TableModelFormatter {
	
	private static final String STRIKE_HTML_TAG_BEGIN = "<html><strike>";
	private static final String STRIKE_HTML_TAG_END = "</strike></html>";
	
	private static final int IS_DONE_FIELD_NO = 7;
	private static final int COLUMN_COUNT = FormatterHelper.COLUMN_COUNT - 1;
	
	public Object[][] formatTaskList(Object[][] taskList) {
		Object[][] tableModelData = new Object[taskList.length][COLUMN_COUNT];
		
		for (int i = 0; i < taskList.length; i++) {
			assert taskList[i][IS_DONE_FIELD_NO] instanceof Boolean;
			Boolean isDone = (Boolean) taskList[i][IS_DONE_FIELD_NO];
			
			for (int j = 0; j < COLUMN_COUNT; j++) {
				tableModelData[i][j] = FormatterHelper.getStringRepresentation(taskList[i][j]);
				
				if (isDone) {
					tableModelData[i][j] = STRIKE_HTML_TAG_BEGIN + tableModelData[i][j] +
							STRIKE_HTML_TAG_END;
				}
			}
		}
		
		return tableModelData;
	}
	
}
