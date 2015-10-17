package ui.formatter;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import global.Task;

public class FormatterHelper {
	
	private static final String EMPTY_STRING_SUBSTITUTE = "-";
	
	public static final String[] FIELD_NAMES = new String[] { "UNUSED",
															  "name",
															  "startingTime",
															  "endingTime",
															  "location",
															  "periodic" };
	

	private static final String[] TABLE_COLUMN_NAMES = new String[] { "No.",
																	  "Description",
																	  "Starting Time",
																	  "Ending Time",
																	  "Location",
																	  "Period" };
	
	private static final int COLUMN_COUNT = 6;
	
	//additional space at the beginning and the end
	private static final int ADDITIONAL_SPACE = 2;
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy");

	public static ColumnInfo[] getColumnInfo(List<Task> taskList, int lineCharLimit) {
		int[] columnLengths = new int[COLUMN_COUNT];
		
		for (int i = 0; i < columnLengths.length; i++) {
			//coulumn length should be at least as long as the column name length
			int columnLength = TABLE_COLUMN_NAMES[i].length() + ADDITIONAL_SPACE;
			
			for (int j = 0; j < taskList.size(); j++) {
				Task task = taskList.get(j);
				
				if (i == 0) { //the first column is the number
					String numberAsString = String.valueOf(i + 1);
					int currentRowColumnLength = numberAsString.length();
					if (currentRowColumnLength > lineCharLimit) {
						currentRowColumnLength = lineCharLimit;
					}
					columnLength = Math.max(columnLength, currentRowColumnLength);
				} else {
					Field currentField = null;
					
					try {
						currentField = Task.class.getDeclaredField(FIELD_NAMES[i]);
						currentField.setAccessible(true);
						
						Object objectInField = currentField.get(task);
						String stringRepresentation = getStringRepresentation(objectInField);
						
						if (stringRepresentation != null) {
							int currentRowColumnLength = stringRepresentation.length();
							if (currentRowColumnLength > lineCharLimit) {
								currentRowColumnLength = lineCharLimit;
							}
							columnLength = Math.max(columnLength, currentRowColumnLength);
						}
					} catch (SecurityException | NoSuchFieldException | IllegalAccessException e) {
						assert currentField != null;
						assert false; //should not happen
					}
				}
			}
			
			columnLengths[i] = columnLength;
		}
		
		ColumnInfo[] columnInfo = new ColumnInfo[COLUMN_COUNT];
		for (int i = 0; i < columnInfo.length; i++) {
			columnInfo[i] = new ColumnInfo(TABLE_COLUMN_NAMES[i], columnLengths[i]);
		}
		
		return columnInfo;
	}

	public static String getStringRepresentation(Object objectInField) {
		if (objectInField == null) {
			return null;
		}
		
		if (objectInField instanceof String) {
			return (String) objectInField;
		} else if (objectInField instanceof Calendar) {
			return String.format("%s", DATE_FORMAT.format(((Calendar) objectInField).getTime()));
		}
		
		assert false : "Fields in Task object can only be either String or Calendar";
		return null;
	}

	public static String[] splitString(String string, int lineCharLimit) {
		assert lineCharLimit > 1 : "Does not work if charLimit = 1";
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
	
}
