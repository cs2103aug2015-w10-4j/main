package ui.formatter;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import global.Task;

public class FormatterHelper {
	
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

	public static ColumnInfo[] getColumnInfo(List<Task> taskList) {
		int[] columnLengths = new int[COLUMN_COUNT];
		
		for (int i = 0; i < columnLengths.length; i++) {
			//coulumn length should be at least as long as the column name length
			int columnLength = TABLE_COLUMN_NAMES[i].length();
			
			for (int j = 0; j < taskList.size(); j++) {
				Task task = taskList.get(j);
				
				if (i == 0) { //the first column is the number
					String numberAsString = String.valueOf(i + 1);
					columnLength = Math.max(columnLength, numberAsString.length());
				} else {
					Field currentField = null;
					
					try {
						currentField = Task.class.getDeclaredField(FIELD_NAMES[i]);
						currentField.setAccessible(true);
						
						Object objectInField = currentField.get(task);
						String stringRepresentation = getStringRepresentation(objectInField);
						
						if (stringRepresentation != null) {
							columnLength = Math.max(columnLength, stringRepresentation.length());
						}
					} catch (SecurityException | NoSuchFieldException | IllegalAccessException e) {
						System.out.println("Attempting to get " + FIELD_NAMES[i]);
						assert currentField != null;
						System.out.println("Getting " + currentField.getName());
						assert false; //should not happen
					}
				}
			}
			
			columnLengths[i] = columnLength + ADDITIONAL_SPACE;
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

	
}
