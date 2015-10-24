package ui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

@SuppressWarnings("serial")
public class TaskTable extends JTable {
	
	private static final Color DEFAULT_COLOR = Color.WHITE;
	private static final Color ALTERNATE_COLOR = Color.LIGHT_GRAY;
	
	public TaskTable(TableModel dm) {
		super(dm);
	}
	
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
		Component c = super.prepareRenderer(renderer, row, column);

		if (row % 2 == 1) {
			c.setBackground(DEFAULT_COLOR);
		} else {
			c.setBackground(ALTERNATE_COLOR);
		}

		return c;
	}
}
