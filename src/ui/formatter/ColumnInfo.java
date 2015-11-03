package ui.formatter;

public class ColumnInfo {
	private String columnName;
	private int columnWidth;
	
	//@@author A0134155M
	public ColumnInfo(String columnName, int columnWidth) {
		this.columnName = columnName;
		this.columnWidth = columnWidth;
	}
	
	//@@author A0134155M
	public String getColumnName() {
		return columnName;
	}
	
	//@@author A0134155M
	public int getColumnWidth() {
		return columnWidth;
	}
}
