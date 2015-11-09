package ui.formatter;

import java.util.List;

//@@author A0134155M
/**
 * This class is used to convert/format an object array containing all data of a 
 * <code>Task</code> object into its string value and put them into a table made from 
 * <i>ASCII</i> characters.
 */
public class TextFormatter {

    private static final String NULL_STRING_SUBSTITUTE = "";
    
    private static final String MESSAGE_DISPLAY_NEWLINE = System.getProperty("line.separator");
    private static final String MESSAGE_DISPLAY_EMPTY = "No items to display.";
    
    private static final char INTERSECTION_CHAR = '+';
    private static final char HORIZONTAL_CHAR = '-';
    private static final char VERTICAL_CHAR = '|';
    
    /**
     * Format a given list of 3D Object array containing task data into a string table.
     * @param taskLists
     * @param lineCharLimit
     * @return string table containing all tasks data
     */
    public String formatTaskList(Object[][][] taskLists, List<String> tableTitles, int lineCharLimit) {
        if (isEmpty(taskLists)) {
            return MESSAGE_DISPLAY_EMPTY;
        }

        StringBuilder result = new StringBuilder();
        for (int taskListIndex = 0; taskListIndex < taskLists.length; taskListIndex++) {
            Object[][] taskList = taskLists[taskListIndex];
            ColumnInfo[] columnInfo = FormatterHelper.getColumnInfo(taskList, lineCharLimit);
            
            if (isValidIndex(tableTitles, taskListIndex)) {
                String currentTableTitle = tableTitles.get(taskListIndex);
                result.append(currentTableTitle);
                result.append(MESSAGE_DISPLAY_NEWLINE);
            }
            
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

    private boolean isValidIndex(List<String> tableTitles, int taskListIndex) {
        if (tableTitles == null) {
            return false;
        } else {
            return 0 <= taskListIndex && taskListIndex < tableTitles.size();
        }
    }

    private boolean isEmpty(Object[][][] taskLists) {
        int maxLength = 0;
        for (Object[][] taskList : taskLists) {
            if (taskList != null) {
                maxLength = Math.max(maxLength, taskList.length);
            }
        }
        return maxLength == 0;
    }

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
