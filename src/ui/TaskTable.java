package ui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import ui.tasktable.TaskTableModel;

@SuppressWarnings("serial")
public class TaskTable extends JTable {
	
	private static final Color DEFAULT_COLOR = Color.WHITE;
	private static final Color ALTERNATE_COLOR = Color.LIGHT_GRAY;
	private static final Color DONE_COLOR = Color.GREEN;
	
	private TaskTableModel model = null;
	
	public TaskTable(TaskTableModel dm) {
		super(dm);
		
		this.model = dm;
	}
	
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
		Component c = super.prepareRenderer(renderer, row, column);

		if (row % 2 == 1) {
			c.setBackground(DEFAULT_COLOR);
		} else {
			c.setBackground(ALTERNATE_COLOR);
		}
		
		//If the task is done, make it green
		if (model.isTaskDone(row).equals(Boolean.TRUE)) {
			c.setBackground(DONE_COLOR);
		}

		return c;
	}
}
