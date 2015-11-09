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
	
	//@@author A0134155M
	/**
	 * Initialize TaskTableModel with 2D object array taskListData where the first dimension
	 * is an array of tasks data, and the second dimension describes each field of the task.
	 * Each element of taskListData is still a raw object based on the corresponding field in
	 * the <code>Task</code> class. The constructor will then format the data as string so that
	 * it can be displayed in the table.
	 * @param taskListData 2D object array containing tasks data.
	 */
	public TaskTableModel(Object[][] taskListData) {
		super();

		assert taskListData != null;

		Pair<Object[][], Boolean[]> formattedData = formatter.formatTaskList(taskListData);
		data = formattedData.getFirst();
		isDone = formattedData.getSecond();
	}
	
	//@@author A0134155M
	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}
	
	//@@author A0134155M
	@Override
	public int getRowCount() {
		return data.length;
	}

	//@@author A0134155M
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	//@@author A0134155M
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return data[rowIndex][columnIndex];
	}
	
	//@@author A0134155M
	/**
	 * Returns whether task at particular index has been done or not.
	 * @param taskIndex Task index
	 * @return			Whether the task has been done or not.
	 */
	public boolean isTaskDone(int taskIndex) {
		return isDone[taskIndex] != null ? isDone[taskIndex] : false;
	}

}