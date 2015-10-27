package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import ui.tasktable.TaskTableModel;

@SuppressWarnings("serial")
public class TaskTable extends JTable {
	
	private static final Color HEADER_COLOR = new Color(0x443266);
	private static final Color DEFAULT_ROW_COLOR = Color.WHITE;
	private static final Color ALTERNATE_ROW_COLOR = new Color(0xC3C3E5);
	private static final Color DONE_COLOR = Color.GREEN;
	
	private static final String HEADER_FONT_NAME = "SansSerif";
	private static final int HEADER_FONT_STYLE = Font.BOLD;
	private static final int HEADER_FONT_SIZE = 12;
	
	private TaskTableModel model = null;
	
	public TaskTable(TaskTableModel dm) {
		super(dm);
		
		this.model = dm;
		
		prepareTable();
	}
	
	private void prepareTable() {
		JTableHeader tableHeader = getTableHeader();
		tableHeader.setFont(new Font(HEADER_FONT_NAME, HEADER_FONT_STYLE, HEADER_FONT_SIZE));
		tableHeader.setBackground(HEADER_COLOR);
		tableHeader.setForeground(Color.WHITE);
		
		TableCellRenderer headerRenderer = tableHeader.getDefaultRenderer();
		JLabel headerLabel = (JLabel) headerRenderer;
		headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		setShowGrid(true);
		setGridColor(Color.LIGHT_GRAY);
	}
	
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
		Component c = super.prepareRenderer(renderer, row, column);

		if (row % 2 == 0) {
			c.setBackground(DEFAULT_ROW_COLOR);
		} else {
			c.setBackground(ALTERNATE_ROW_COLOR);
		}
		
		//If the task is done, make it green
		if (model.isTaskDone(row).equals(Boolean.TRUE)) {
			c.setBackground(DONE_COLOR);
		}

		return c;
	}
}
