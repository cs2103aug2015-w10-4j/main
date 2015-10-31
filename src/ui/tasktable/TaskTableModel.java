package ui.tasktable;

import javax.swing.table.AbstractTableModel;

import global.Pair;
import ui.formatter.TableModelFormatter;

@SuppressWarnings("serial")
public class TaskTableModel extends AbstractTableModel {
	
	private final String[] columnNames = {"No.",
			"Description",
			"Starting Time",
			"Ending Time",
			"Location",
	};

	private final TableModelFormatter formatter = new TableModelFormatter();
	
	private Object[][] data = null;
	private Boolean[] isDone;
	
	public TaskTableModel(Object[][] taskListData) {
		super();

		assert taskListData != null;

		Pair<Object[][], Boolean[]> formattedData = formatter.formatTaskList(taskListData);
		data = formattedData.getFirst();
		isDone = formattedData.getSecond();
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
	
	public Boolean isTaskDone(int taskIndex) {
		assert isDone[taskIndex] != null;
		return isDone[taskIndex];
	}

}