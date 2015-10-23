package ui;

import javax.swing.table.AbstractTableModel;

import ui.formatter.TableModelFormatter;

@SuppressWarnings("serial")
public class TaskyTableModel extends AbstractTableModel {
	
	private final String[] columnNames = {"No.",
			"Description",
			"Starting Time",
			"Ending Time",
			"Location",
			"Every",
			"Repeat"
	};
	private final TableModelFormatter formatter = new TableModelFormatter();
	
	private Object[][] data = null;
	
	public TaskyTableModel(Object[][] taskListData) {
		super();

		assert taskListData != null;
		data = formatter.formatTaskList(taskListData);
	}
	
	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	@Override
	public int getRowCount() {
		return data.length;
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return data[rowIndex][columnIndex];
	}

}
