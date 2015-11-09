package ui.formatter;

//@@author A0134155M
/**
 * This is a data structure that contains column name and needed width for 
 * a column.
 */
public class ColumnInfo {
    private String columnName;
    private int columnWidth;
    
    public ColumnInfo(String columnName, int columnWidth) {
        this.columnName = columnName;
        this.columnWidth = columnWidth;
    }
    
    public String getColumnName() {
        return columnName;
    }
    
    public int getColumnWidth() {
        return columnWidth;
    }
}
